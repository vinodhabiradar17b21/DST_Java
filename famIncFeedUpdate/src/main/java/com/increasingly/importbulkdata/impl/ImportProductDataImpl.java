package com.increasingly.importbulkdata.impl;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.increasingly.importbulkdata.impl.db.BatchUpdateAssociateProductsDataImpl;
import com.increasingly.importbulkdata.impl.db.BatchUpdateCategoryDataImpl;
import com.increasingly.importbulkdata.impl.db.BatchUpdateProductDataImpl;
import com.increasingly.importbulkdata.impl.db.BatchUpdateProductOptionsImpl;
import com.increasingly.importbulkdata.impl.db.BatchUpdateProductOtherImageDataImpl;
import com.increasingly.importbulkdata.impl.db.DeleteTemporaryStorageData;
import com.increasingly.importbulkdata.impl.db.GetClientProductFeedDetailsList;
import com.increasingly.importbulkdata.impl.db.GetProductFeedFieldMappingDetails;
import com.increasingly.importbulkdata.impl.db.InsertAssociatedProductsDataImpl;
import com.increasingly.importbulkdata.impl.db.InsertProductCategoryDataImpl;
import com.increasingly.importbulkdata.impl.db.InsertProductDataImpl;
import com.increasingly.importbulkdata.impl.db.InsertProductOptionDetailsImpl;
import com.increasingly.importbulkdata.impl.db.InsertProductsOtherImagesDataImpl;
import com.increasingly.importbulkdata.impl.db.InsertFeedUpdateFileDetailsImpl;
import com.increasingly.importbulkdata.impl.db.GetUpdatedFeedFileDetails;
import com.increasingly.importbulkdata.impl.db.UpdateActiveProductPriceInfo;
import com.increasingly.importbulkdata.impl.db.UpdateCustomizations;
import com.increasingly.importbulkdata.util.FileUtil;
import com.increasingly.importbulkdata.util.FormatLoggerMessage;
import com.increasingly.importbulkdata.util.ParseCSVFile;
import com.increasingly.importbulkdata.util.GetProperties;

import static com.increasingly.importbulkdata.util.Constants.*;

public class ImportProductDataImpl
{
	private static final Logger logger = LoggerFactory.getLogger(ImportProductDataImpl.class.getClass());
	private static Properties increasinglyProperties = null;
	private String feedUrl = "";
	private String increasinglyFTPURL = "";
	private Integer feedId = 26;
	public HttpURLConnection connection;
	public InputStream importDataStream;
	public GZIPInputStream gZipInputStream;
	public ZipInputStream zipInputStream;
		
	private static String tmpFeedDwnldPath = System.getProperty("user.home")+"/DownloadedFeeds";
	private String temporaryFeedSavePath = "";
	private String fileFormat = "";
	private String delimiter = ",";
	private String categoryDelimiter = "/";
	private String characterSetEncoder = "UTF-8";
	private String ftpUserName = null;
	private String ftpPassword = null;
	private Integer ftpPort = null;	
	ArrayList<Product> productList; 
	ArrayList<ProductCategoryMapping> categoryList; 
	ArrayList<AssociatedProducts> associatedProductList;
	ArrayList<ProductImage> productImageList;
	ArrayList<LinkedHashMap<String,Object>> productInventoryDataArrayList;
	List<ProductOption> productOptionsArrayList;
	
	ObjectMapper mapper = new ObjectMapper();
	Boolean isMagentoProductExport = false;
	Boolean isFirstSetRecord = true;
	Boolean isLastSetRecord = true;
	

	private ProcessTime processTime;
	DateTime startDatetime;
	Boolean isDifferentialFeed = true;
	Boolean feedUpdateFailed = false;
	
	FTPClient ftpClient = new FTPClient();

	public void runService() 
	{			
		try
		{
			processTime = new ProcessTime();
			increasinglyProperties = GetProperties.readProperties("webapp/WEB-INF/increasingly.properties");	
			
			this.increasinglyFTPURL = increasinglyProperties.getProperty("increasinglyFTPURL");
			this.ftpUserName = increasinglyProperties.getProperty("ftpUserName");
			this.ftpPassword = increasinglyProperties.getProperty("ftpPassword");
			
			Map<String, Object> input = new HashMap<String, Object>();
			
			Map<Integer, Map<String, Object>> clientFeedDetailsList = new HashMap<Integer, Map<String,Object>>();
			GetClientProductFeedDetailsList getClientProductFeedDetailsList = GetClientProductFeedDetailsList.getInstance();
			clientFeedDetailsList = getClientProductFeedDetailsList.runService(input);
			  
		    
			for(Map.Entry<Integer, Map<String, Object>> feedIdItem : clientFeedDetailsList.entrySet()){
				feedId = feedIdItem.getKey();
				input.put(FEED_ID, feedIdItem.getKey());
				input.put(STORE_CODE, feedIdItem.getValue().get(STORE_CODE));
				
				List<String> fileList = getFileList(increasinglyFTPURL,input);
				
				if (fileList.size() > 0)
				{
					List<String> listOfFilesProcessed = new ArrayList<String>();
					startDatetime = DateTime.now();						
					
					// Get the file list which are updated since 5 days
					List<String> updatedFeedFilesFromDb = getUpdatedFeedFileDetails(input);
					
					for (int i = 0; i < fileList.size(); i++)
					{
						String fileLink = fileList.get(i);
						
						if(!updatedFeedFilesFromDb.contains(fileLink.split("/")[3].trim())){
													
							feedUrl = fileLink;
					
						    downloadAndReadFeed();
							
							listOfFilesProcessed.add(fileLink);
						}
					}
					
					processTime.setTotalProcessTime(processTime.getTimeTaken(startDatetime));
				}	
			}
			
		}
		catch(Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "ImportProductDataImpl" , "runService", "Error occured " ,"");
			logger.error(errorMessage,ex);
		}		
	}
	
	private void downloadAndReadFeed()
	{		
		if(downloadFeed())
		{
		  readProductContent(temporaryFeedSavePath);		 
		  processProductFeedData(feedUrl);
		}
				
	}
	
	private Boolean downloadFeed()
	{
		Boolean isFileUploadSuccessful = false;
		try
		{
			String[] tmpArr = feedUrl.trim().split("\\.");
			String fileExt = tmpArr[tmpArr.length - 1];			
			Integer fileCompressionType = FILE_COMPRESSION_TYPE_NONE;
			
			URL url = new URL(feedUrl);

			if (!fileExt.toLowerCase().contains(FILE_EXTENSION_EXE))
			{
				importDataStream = fetchContent();
				
				if (importDataStream != null)
				{
					String contentType = "";
					if (url.getProtocol().equalsIgnoreCase(PROTOCOL_HTTP) || url.getProtocol().equalsIgnoreCase(PROTOCOL_HTTPS))
					{
						contentType = connection.getContentType();
						
						if(contentType.contains("application/json"))
						{
							fileExt = "txt";
							fileFormat = "json";
						}
						else if(contentType.contains("text/plain"))
						{
							fileExt = "txt";
							fileFormat = "txt";
						}
						else if(contentType.contains("application/csv"))
						{
							fileExt = "csv";
							fileFormat = "csv";
						}
						else if(contentType.contains("text/csv"))
						{
							fileExt = "csv";
							fileFormat = "csv";
						}
						else if(contentType.contains("application/xml"))
						{
							fileExt = "xml";
							fileFormat = "xml";
						}
						else if(contentType.contains("text/xml"))
						{
							fileExt = "xml";
							fileFormat = "xml";
						}
						else
						{
							fileFormat = fileExt;
						}
					}
					else
					{
						fileFormat = fileExt;
					}
					
					if(isMagentoProductExport)
					{
						fileExt = "txt";
						fileFormat = "json";
					}
				
					temporaryFeedSavePath = tmpFeedDwnldPath + "_" + feedId.toString() + "." + fileExt;

					if ((!Strings.isNullOrEmpty(contentType) && (contentType.contains(FILE_EXTENSION_GZ) || contentType.contains(FILE_EXTENSION_DEFLATE)))
							|| fileExt.toLowerCase().contains(FILE_EXTENSION_GZ) || fileExt.toLowerCase().contains(FILE_EXTENSION_DEFLATE))
					{
						fileCompressionType = FILE_COMPRESSION_TYPE_GZIP;
						gZipInputStream = new GZIPInputStream(importDataStream);
					}
					else if ((!Strings.isNullOrEmpty(contentType) && contentType.contains(FILE_EXTENSION_ZIP)) || fileExt.toLowerCase().contains(FILE_EXTENSION_ZIP))
					{
						fileCompressionType = FILE_COMPRESSION_TYPE_ZIP;
						zipInputStream = new ZipInputStream(importDataStream);
					}

					/** Delete file if it already exist */
					FileUtil.deleteFile(temporaryFeedSavePath);

					if (fileCompressionType.equals(FILE_COMPRESSION_TYPE_NONE))
					{
						isFileUploadSuccessful = FileUtil.uploadFile(importDataStream, temporaryFeedSavePath);
					}
					else if (fileCompressionType.equals(FILE_COMPRESSION_TYPE_GZIP))
					{
						// .gz files will have the original filename (Eg: test.xml.gz)
						fileFormat = tmpArr[tmpArr.length - 2];
						isFileUploadSuccessful = FileUtil.uploadGZipedFileAndDecompress(gZipInputStream, temporaryFeedSavePath);
					}
					else if (fileCompressionType.equals(FILE_COMPRESSION_TYPE_ZIP))
					{
						// zipInputStream.getNextEntry().getName() will give the original file name
						ZipEntry ze = zipInputStream.getNextEntry();
						String[] tArr = ze.getName().split("\\.");
						fileFormat = tArr[tArr.length - 1];
						isFileUploadSuccessful = FileUtil.uploadZipedFileAndDecompress(zipInputStream, ze, temporaryFeedSavePath);
					}
					
					if (ftpClient.isConnected() && ftpClient.completePendingCommand())
					{
						ftpClient.logout();
						ftpClient.disconnect();
					}
					
					logger.info("Feed file " + feedUrl + " downloaded successfully...");
				}
			}
			else
			{				
				logger.info("Feed is not in proper format...");
			}
		}
		catch (Exception e)
		{			
			logger.error(e.getMessage(), e);
		}

		return isFileUploadSuccessful;
	}

	private InputStream fetchContent() 
	{
		logger.info(LOG_APPLICATION_FLOW + "Downloading product data from : " + feedUrl + "...");
		InputStream inputStream = null;
		try
		{
			URL url = new URL(URLDecoder.decode(feedUrl));

			if (url.getProtocol().equalsIgnoreCase(PROTOCOL_FTP))
			{
				if (feedUrl.toLowerCase().contains(increasinglyFTPURL))
				{
					ftpClient.connect(url.getHost());
					ftpClient.login(ftpUserName, ftpPassword);
					ftpClient.enterLocalPassiveMode();
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
					ftpClient.setConnectTimeout(90000);					
					inputStream = ftpClient.retrieveFileStream(url.getPath());
				}
				else
				{
				  URLConnection uc = (URLConnection) url.openConnection();
				  uc.setConnectTimeout(10000); // CONNECTION_TIMEOUT
				  inputStream = uc.getInputStream();	
				}
			}
			else if (url.getProtocol().equalsIgnoreCase(PROTOCOL_HTTP) || url.getProtocol().equalsIgnoreCase(PROTOCOL_HTTPS))
			{
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(90000);
				connection.setRequestProperty("User-Agent", "Mozilla/5.0");
				
				if (url.getUserInfo() != null && feedId != 27) {
				    String basicAuth = "Basic " + new String(Base64.encodeBase64(url.getUserInfo().getBytes()));
				    connection.setRequestProperty("Authorization", basicAuth);
				}
				else if(feedId == 27)
				{
					String basicAuth = "Basic " + new String("aW5jcmVhc2luZ2x5Z3NyQGNpdHJ1c2xpbWUuY29tOmR1b3NkaGZpdXNkZmdoc2RmdTEyM2Y1NDM=");
				    connection.setRequestProperty("Authorization", basicAuth);
				}
				
				inputStream = connection.getInputStream();
			}
			else
			{			
				logger.info(LOG_INFO + "Invalid protocol. Only ftp, http or https allowed.");
			}

			logger.info(LOG_APPLICATION_FLOW + "Downloading of product data completed.");
			return inputStream;
		}
		catch (Exception e)
		{		
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "ImportProductDataImpl" , "fetchContent", "Error occured while downloading content from url" + feedUrl,"");
			logger.error(errorMessage,e);
			return null;
		}
	}
		
	/** This method is used to read complete feed data */
	public void readProductContent(String localFilePath) 
	{	
		productList = new ArrayList<Product>();
		categoryList = new ArrayList<ProductCategoryMapping>();
		associatedProductList = new ArrayList<AssociatedProducts>();
		productImageList = new ArrayList<ProductImage>();
		productInventoryDataArrayList = new ArrayList<LinkedHashMap<String,Object>>();
		productOptionsArrayList = new ArrayList<ProductOption>();
		ArrayList<LinkedHashMap<String,Object>> productData = new ArrayList<LinkedHashMap<String,Object>>();
		
		try
		{	
			logger.info(LOG_APPLICATION_FLOW + "Started reading product content from downloaded feed.");			
			
			ParseCSVFile parseCsvFile = new ParseCSVFile();
			productData = parseCsvFile.getCSVParsedData(delimiter, localFilePath, characterSetEncoder);				
					
			Map<String,Object> input = new HashMap<String,Object>();
			input.put(FEED_ID, feedId);
			ArrayList<Map<String,String>> fieldMappingDetailsFromDb = getFieldMappingDetails(input);
			
			LinkedHashMap<String, String> fieldMappingDetails = new LinkedHashMap<String, String>();

			for (Map<String, String> s : fieldMappingDetailsFromDb)
			{
				fieldMappingDetails.put(s.get("field_name"), s.get("client_field_name"));
			}
			
			Customizations customization = new Customizations();
			productData = customization.customize7FAMFeed(productData,fieldMappingDetails);
			
		    for(Map<String,Object> item : productData)
	        {	
	        	Boolean hasErrorOccured = false;
	        	String productId = (String)item.get(fieldMappingDetails.get(PRODUCT_ID));
	        	
	        	Product product = new Product();
	        	product.setProductId(productId);
	        	
	        	if(item.get(fieldMappingDetails.get(PRODUCT_NAME)) != null)
	        	{
	        	  product.setProductName((String)item.get(fieldMappingDetails.get(PRODUCT_NAME)));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(PRODUCT_SKU)) != null)
	        	{
	        		product.setProductSku((String)item.get(fieldMappingDetails.get(PRODUCT_SKU)));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(PRODUCT_PRICE)) != null)
	        	{
	        	  product.setProductPrice(String.valueOf(item.get(fieldMappingDetails.get(PRODUCT_PRICE))));	
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(SPECIAL_PRICE)) != null)
	        	{
	        	  product.setSpecialPrice(String.valueOf(item.get(fieldMappingDetails.get(SPECIAL_PRICE))));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(IMAGE_URL)) != null)
	        	{
	        	  product.setImageUrl((String)item.get(fieldMappingDetails.get(IMAGE_URL)));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(PRODUCT_URL)) != null)
	        	{
	        	  product.setProductUrl((String)item.get(fieldMappingDetails.get(PRODUCT_URL)));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(DESCRIPTION)) != null)
	        	{
	        	  product.setDescription((String)item.get(fieldMappingDetails.get(DESCRIPTION)));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(SHORT_DESCRIPTION)) != null)
	        	{
	        	  product.setShortDescription((String)item.get(fieldMappingDetails.get(SHORT_DESCRIPTION)));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(CLIENT_PRODUCT_STATUS)) != null)
	        	{
	        	  product.setProductStatus(Integer.parseInt(item.get(fieldMappingDetails.get(CLIENT_PRODUCT_STATUS)).toString()));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(PRODUCT_TYPE)) != null)
	        	{
	        	  product.setProductType((String)item.get(fieldMappingDetails.get(PRODUCT_TYPE)));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(MANUFACTURER)) != null)
	        	{
	        	  product.setManufacturer((String)item.get(fieldMappingDetails.get(MANUFACTURER)));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(COLOR)) != null)
	        	{
	        	  product.setColor((String)item.get(fieldMappingDetails.get(COLOR)));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(SIZE)) != null)
	        	{
	        	  product.setSize((String)item.get(fieldMappingDetails.get(SIZE)));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(WEIGHT)) != null)
	        	{
	        	  product.setWeight(String.valueOf(item.get(fieldMappingDetails.get(WEIGHT))));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(VISIBILITY)) != null)
	        	{
	        	  product.setVisibility((String)item.get(fieldMappingDetails.get(VISIBILITY)));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(QUANTITY)) != null)
	        	{
	        	  product.setQunatity(Integer.parseInt(item.get(fieldMappingDetails.get(QUANTITY)).toString()));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(CREATED_DATE)) != null)
	        	{
	        	  product.setCreatedDate(String.valueOf(item.get(fieldMappingDetails.get(CREATED_DATE))));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(UPDATED_DATE)) != null)
	        	{
	        	  product.setUpdatedDate(String.valueOf(item.get(fieldMappingDetails.get(UPDATED_DATE))));
	        	}	
	        	if(item.get(fieldMappingDetails.get(FIELD1)) != null)
	        	{
	        	  product.setField1(String.valueOf(item.get(fieldMappingDetails.get(FIELD1))));
	        	}
	        	if(item.get(fieldMappingDetails.get(FIELD2)) != null)
	        	{
	        	  product.setField2(String.valueOf(item.get(fieldMappingDetails.get(FIELD2))));
	        	}
	        	if(item.get(fieldMappingDetails.get(FIELD3)) != null)
	        	{
	        	  product.setField3(String.valueOf(item.get(fieldMappingDetails.get(FIELD3))));
	        	}
	        	if(item.get(fieldMappingDetails.get(FIELD4)) != null)
	        	{
	        	  product.setField4(String.valueOf(item.get(fieldMappingDetails.get(FIELD4))));
	        	}
	        	if(item.get(fieldMappingDetails.get(FIELD5)) != null)
	        	{
	        	  product.setField5(String.valueOf(item.get(fieldMappingDetails.get(FIELD5))));
	        	}
	        	if(item.get(fieldMappingDetails.get(FIELD6)) != null)
	        	{
	        	  product.setField6(String.valueOf(item.get(fieldMappingDetails.get(FIELD6))));
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(PRODUCT_OPTIONS)) != null && !item.get(fieldMappingDetails.get(PRODUCT_OPTIONS)).toString().trim().isEmpty())
	        	{	
        			ArrayList<LinkedHashMap<String,Object>> productOptions = (ArrayList<LinkedHashMap<String,Object>>)item.get(fieldMappingDetails.get(PRODUCT_OPTIONS));
        			
        			if(productOptions != null)
        			{
        				try
        				{
	        				List<ProductOption> tempProductOptions = new ArrayList<ProductOption>();
	        				
	        				for(LinkedHashMap<String,Object> option : productOptions)
					        {	        					
	        					ProductOption productOption = new ProductOption();		
	        					
	        					productOption.setParentProductId(productId);
	        					
	        					if(option.get(fieldMappingDetails.get(CHILD_PRODUCT_ID)) != null)
	        					productOption.setChildProductId((String)option.get(fieldMappingDetails.get(CHILD_PRODUCT_ID)));	
	        					
	        					if(option.get(fieldMappingDetails.get(CHILD_PRODUCT_SKU)) != null)
		        					productOption.setChildProductSku((String)option.get(fieldMappingDetails.get(CHILD_PRODUCT_SKU)));
	        					
	        					if(option.get(fieldMappingDetails.get(STORE_ID)) != null)
		        					productOption.setStoreId((String)option.get(fieldMappingDetails.get(STORE_ID)));
	        					
	        					if(option.get(fieldMappingDetails.get(ATTRIBUTE_CODE)) != null)
		        					productOption.setAttributeCode(option.get(fieldMappingDetails.get(ATTRIBUTE_CODE)).toString().trim());
	        					
	        					if(option.get(fieldMappingDetails.get(ATTRIBUTE_ID)) != null)
		        					productOption.setAttributeId(option.get(fieldMappingDetails.get(ATTRIBUTE_ID)).toString().trim());
	        					
	        					if(option.get(fieldMappingDetails.get(ATTRIBUTE_LABEL)) != null)
		        					productOption.setAttributeLabel(option.get(fieldMappingDetails.get(ATTRIBUTE_LABEL)).toString().trim());
	        					
	        					if(option.get(fieldMappingDetails.get(OPTION_ID)) != null)
		        					productOption.setOptionId(String.valueOf(option.get(fieldMappingDetails.get(OPTION_ID))).trim());
	        					
	        					if(option.get(fieldMappingDetails.get(OPTION_TEXT)) != null)
		        					productOption.setOptionText(option.get(fieldMappingDetails.get(OPTION_TEXT)).toString().trim());
	        					
	        					if(option.get(fieldMappingDetails.get(OPTION_IMAGE_URL)) != null)
		        					productOption.setOptionImageUrl((String)option.get(fieldMappingDetails.get(OPTION_IMAGE_URL)));
	        					
	        					if(option.get(fieldMappingDetails.get(IS_PERCENT)) != null)
		        					productOption.setIsPercent(Integer.parseInt((String)option.get(fieldMappingDetails.get(IS_PERCENT))));
	        					
	        					if(option.get(fieldMappingDetails.get(PRICING_VALUE)) != null)
		        					productOption.setPricingValue(String.valueOf(option.get(fieldMappingDetails.get(PRICING_VALUE))));
	        					
	        					if(option.get(fieldMappingDetails.get(FIELD_TYPE)) != null)
		        					productOption.setFieldType((String)option.get(fieldMappingDetails.get(FIELD_TYPE)));
	        					
	        					if(option.get(fieldMappingDetails.get(COLOR_CODE)) != null)
		        					productOption.setColorCode(option.get(fieldMappingDetails.get(COLOR_CODE)).toString().trim());
	        					
	        					if(option.get(fieldMappingDetails.get(ATTRIBUTE_QUANTITY)) != null)                                  
	                                productOption.setQuantity(Integer.parseInt(option.get(fieldMappingDetails.get(ATTRIBUTE_QUANTITY)).toString()));
	                               
	                            if(option.get(fieldMappingDetails.get(ATTRIBUTE_PRICE)) != null)
	                                productOption.setPrice(String.valueOf(option.get(fieldMappingDetails.get(ATTRIBUTE_PRICE))));   
	                               
	                            if(option.get(fieldMappingDetails.get(ATTRIBUTE_SPECIAL_PRICE)) != null)
	                                productOption.setSpecialPrice(String.valueOf(option.get(fieldMappingDetails.get(ATTRIBUTE_SPECIAL_PRICE))));
	        					
	        					tempProductOptions.add(productOption);
	        					
					        }
	        				
	        				productOptionsArrayList.addAll(tempProductOptions);
        				}
        				catch(Exception ex)
        				{
        					hasErrorOccured = true;
        					String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "ImportProductDataImpl" , "readProductContent", "Error occured while reading product options, feed id - " + feedId + " product id - "+ productId,"");
        					logger.error(errorMessage,ex);
        				}
        				
        			}
	        		
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(CATEGORIES)) != null)
	        	{	
	        		int count;
	        		if(fileFormat.equals(CONTENT_TYPE_JSON) && isMagentoProductExport)
	        		{
	        			ArrayList<LinkedHashMap<String,Object>> categoryArrayList = (ArrayList<LinkedHashMap<String,Object>>)item.get(fieldMappingDetails.get(CATEGORIES));
		        	
	        			count=0;
		        		for(LinkedHashMap<String,Object> category : categoryArrayList)
				        {
		        			count = count +1;
		        			
			        		ProductCategoryMapping productCategoryMapping = new ProductCategoryMapping();				        		
			        		productCategoryMapping.setCategoryId((String)category.get(ID));
			        		
			        		if(isMagentoProductExport)
			        		{
			        		 productCategoryMapping.setCategoryName((String)category.get(NAME));
			        		}
			        		else
			        		{
			        			productCategoryMapping.setCategoryName("");
			        		}
			        		
			        		productCategoryMapping.setProductId(productId);
			        		productCategoryMapping.setCategoryLevel(count);
			        		
			        		categoryList.add(productCategoryMapping);				        	
				        }
	        		}
	        		else
	        		{
	        			String categories = (String)item.get(fieldMappingDetails.get(CATEGORIES));
	        			
	        			List<String> tempCategorylist = new ArrayList<String>();
						if (!Strings.isNullOrEmpty(categoryDelimiter))
						{
							tempCategorylist = Arrays.asList(categories.split(Pattern.quote(categoryDelimiter)));
						}
						else
						{
							tempCategorylist.add(categories);
						}
						
						count=0;
						
						for(String category : tempCategorylist)
				        {
							count = count+1;
							
			        		ProductCategoryMapping productCategoryMapping = new ProductCategoryMapping();				        		
			        		productCategoryMapping.setCategoryId(category);
			        		productCategoryMapping.setCategoryName("");
			        		productCategoryMapping.setProductId(productId);
			        		productCategoryMapping.setCategoryLevel(count);
			        		
			        		categoryList.add(productCategoryMapping);				        	
				        }
	        			
	        		}
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(ASSOCIATED_PRODUCTS)) != null && item.get(fieldMappingDetails.get(PRODUCT_TYPE)) != null)
	        	{
	        		List<String> tempAssociatedProductList = new ArrayList<String>();
	        		if(fileFormat.equals(CONTENT_TYPE_JSON))
	        		{
	        	      tempAssociatedProductList = (ArrayList<String>)item.get(fieldMappingDetails.get(ASSOCIATED_PRODUCTS));
	        		}
	        		else
	        		{
	        		  String strAssociatedProductList = (String)item.get(fieldMappingDetails.get(ASSOCIATED_PRODUCTS));	        	  
	        		  tempAssociatedProductList = Arrays.asList(strAssociatedProductList.split(","));
	        		}
	        	  
	        	  processAssociatedProducts(tempAssociatedProductList,productId,(String)item.get(fieldMappingDetails.get(PRODUCT_TYPE)));
	        	  product.setHasAssociatedProducts(true);
	        	}
	        			        	
	        	if(item.get(fieldMappingDetails.get(RELATED_PRODUCTS)) != null)
	        	{
	        		List<String> tempRelatedProductList = new ArrayList<String>();
	        		if(fileFormat.equals(CONTENT_TYPE_JSON))
	        		{
	        	      tempRelatedProductList = (ArrayList<String>)item.get(fieldMappingDetails.get(RELATED_PRODUCTS));
	        		}
	        		else
	        		{
	        	      String relatedProductList = (String)item.get(fieldMappingDetails.get(RELATED_PRODUCTS)); 	        	 
	        	      tempRelatedProductList = Arrays.asList(relatedProductList.split(","));
	        		}
	        	  
	        	  processAssociatedProducts(tempRelatedProductList,productId,RELATED_PRODUCTS);	
	        	  product.setHasRelatedProducts(true);
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(UP_SELL_PRODUCTS)) != null)
	        	{
	        		List<String> tempUpSellProductList = new ArrayList<String>();
	        		if(fileFormat.equals(CONTENT_TYPE_JSON))
	        		{
	        	      tempUpSellProductList = (ArrayList<String>)item.get(fieldMappingDetails.get(UP_SELL_PRODUCTS));
	        		}
	        		else
	        		{
	        	      String upSellProductList = (String)item.get(fieldMappingDetails.get(UP_SELL_PRODUCTS));	        	  
	        	      tempUpSellProductList = Arrays.asList(upSellProductList.split(","));
	        		}
	        	  
	        	  processAssociatedProducts(tempUpSellProductList,productId,UP_SELL_PRODUCTS);	
	        	  product.setHasUpSellProducts(true);
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(CROSS_SELL_PRODUCTS)) != null)
	        	{
	        		List<String> tempCrossSellProductList = new ArrayList<String>();
	        		if(fileFormat.equals(CONTENT_TYPE_JSON))
	        		{
	        	      tempCrossSellProductList = (ArrayList<String>)item.get(fieldMappingDetails.get(CROSS_SELL_PRODUCTS));
	        		}
	        		else
	        		{
	        		  String crossSellProductList = (String)item.get(fieldMappingDetails.get(CROSS_SELL_PRODUCTS)); 		          
	        		  tempCrossSellProductList = Arrays.asList(crossSellProductList.split(","));
	        		}
		        	  
	        	  processAssociatedProducts(tempCrossSellProductList,productId,CROSS_SELL_PRODUCTS);
	        	  product.setHasCrossSellProducts(true);
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(OTHER_IMAGE_LIST)) != null)
	        	{
	        		List<String> tempProductImageList = new ArrayList<String>();
	        		if(fileFormat.equals(CONTENT_TYPE_JSON))
	        		{
	        		  tempProductImageList = (ArrayList<String>)item.get(fieldMappingDetails.get(OTHER_IMAGE_LIST));
	        		}
	        		else
	        		{
	        			String strProductImageList = (String)item.get(fieldMappingDetails.get(OTHER_IMAGE_LIST)); 
				        tempProductImageList = new ArrayList<String>();
				        tempProductImageList = Arrays.asList(strProductImageList.split(","));
	        		}	        		
			          
	        		for(String tempImageUrl : tempProductImageList)
	        	    {
	         	    	ProductImage productImage = new ProductImage();
	         	    	productImage.setProductId(productId);
	         	    	productImage.setImageUrl(tempImageUrl);			         	    	
	         	    	productImageList.add(productImage);			        	    	
	        	    }
	        		product.setHasOtherImages(true);
	        	}
	        	
	        	if(item.get(fieldMappingDetails.get(PRODUCT_INVENTORY_DATA)) != null)
	        	{	
	        		if(fileFormat.equals(CONTENT_TYPE_JSON) && isMagentoProductExport)
	        		{
	        			LinkedHashMap<String,Object> productInventoryData = (LinkedHashMap<String,Object>)item.get(fieldMappingDetails.get(PRODUCT_INVENTORY_DATA));
	        			
	        			if(productInventoryData != null)
	        			{
	        			  productInventoryDataArrayList.add(productInventoryData);
	        			}
	        		}
	        	}
	        	
	        	
	        	if(!hasErrorOccured)
	        	{
	        		productList.add(product);
	        	}
	        }  
			
			
			logger.info(LOG_APPLICATION_FLOW + "Completed reading product content from downloaded feed.");
			
		}
		catch (Exception e)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "ImportProductDataImpl" , "readProductContent", "Error occured while parsing feed data, feed id - " + feedId,"");
			logger.error(errorMessage,e);
		}
		
		
	}
	
	public ArrayList<Map<String,String>> getFieldMappingDetails(Map<String,Object> input)
	{		
		GetProductFeedFieldMappingDetails getProductFeedFieldMappingDetails = GetProductFeedFieldMappingDetails.getInstance();
		return getProductFeedFieldMappingDetails.runService(input);
	}
	
	
	public Integer processProductFeedData(String feedUrl)
	{	
		Map<String,Object> input = new HashMap<String,Object>();
		input.put(FEED_ID, feedId);
		Integer result = 0;
		
		try
		{			
			if(productList.size() > 0)
			{
				logger.info(LOG_APPLICATION_FLOW + "Started bulk insertion of product data.");
				Map<String,Object> productData = new HashMap<String,Object>();
				productData.put(FEED_ID, feedId);
				productData.put(PRODUCT_LIST, productList);
				BatchUpdateProductDataImpl batchUpdateProductDataImpl = BatchUpdateProductDataImpl.getInstance(); 
				batchUpdateProductDataImpl.runService(productData);
				productData = null;
				logger.info(LOG_APPLICATION_FLOW + "Completed bulk insertion of product data.");
								
				input.put(IS_FIRST_SET_RECORD, isFirstSetRecord);
				input.put(IS_LAST_SET_RECORD, isLastSetRecord);
				input.put(IS_DIFFERENTIAL_FEED, isDifferentialFeed);
				InsertProductDataImpl insertProductDataImpl = InsertProductDataImpl.getInstance();
				result = insertProductDataImpl.runService(input);				
				logger.info(LOG_APPLICATION_FLOW + "Completed insertion of product data to main tables.");
			}
			
			if(result ==1)
			{	
				if(productOptionsArrayList.size() > 0)
				{
					logger.info(LOG_APPLICATION_FLOW + "Started bulk insertion of product option list.");
					
					Map<String,Object> productOptionData = new HashMap<String,Object>();
					productOptionData.put(FEED_ID, feedId);
					productOptionData.put(PRODUCT_OPTIONS_LIST, productOptionsArrayList);
					
					BatchUpdateProductOptionsImpl batchUpdateProductOptionsDataImpl = BatchUpdateProductOptionsImpl.getInstance();
					Boolean productOptionsBatchUpdateStatus = batchUpdateProductOptionsDataImpl.runService(productOptionData);
					productOptionData = null;
					logger.info(LOG_APPLICATION_FLOW + "Completed bulk insertion of product options list.");
					
					if(productOptionsBatchUpdateStatus)
					{
						InsertProductOptionDetailsImpl insertProductOptionsDataImpl = InsertProductOptionDetailsImpl.getInstance();
						Integer hasProductOptionsSaved = insertProductOptionsDataImpl.runService(input);
						
						if(hasProductOptionsSaved == 1 && !feedUpdateFailed)
						{
							logger.info(LOG_APPLICATION_FLOW + "Completed insertion of product option list to main table.");							
						}
						else
						{
							logger.info(LOG_INFO + "Failed  to insert product option list.");
							DeleteTemporaryStorageData deleteTemporaryStorageData = new DeleteTemporaryStorageData();
							deleteTemporaryStorageData.deleteTemporaryProductOptionDetails(input);
						}
					}
				}
				
				try
				{
					UpdateActiveProductPriceInfo updateActiveProductPriceInfo = UpdateActiveProductPriceInfo.getInstance();
					result = updateActiveProductPriceInfo.runService(input);
					
					if(result == 0)
					{
						logger.error(LOG_ERROR + "Error occured while updating active product price data" + feedId);
					}
				}
				catch(Exception ex)
				{
					String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "ImportProductDataImpl" , "processProductFeedData", "Error occured while updating active product price data" + feedId,"");
					logger.error(errorMessage,ex);
				}
				
				if(associatedProductList.size() > 0)
				{
					logger.info(LOG_APPLICATION_FLOW + "Started bulk insertion of associated product list.");
					Map<String,Object> associatedProductData = new HashMap<String,Object>();
					associatedProductData.put(FEED_ID, feedId);
					associatedProductData.put(ASSOCIATED_PRODUCT_LIST, associatedProductList);
					BatchUpdateAssociateProductsDataImpl batchUpdateAssociateProductsDataImpl = BatchUpdateAssociateProductsDataImpl.getInstance();
					Boolean associatedProductBatchUpdateStatus = batchUpdateAssociateProductsDataImpl.runService(associatedProductData);
					associatedProductData = null;
					logger.info(LOG_APPLICATION_FLOW + "Completed bulk insertion of associated product list.");
					
					if(associatedProductBatchUpdateStatus)
					{
						InsertAssociatedProductsDataImpl insertAssociatedProductsDataImpl = InsertAssociatedProductsDataImpl.getInstance();
						Integer hasAssociatedProductsInserted = insertAssociatedProductsDataImpl.runService(input);
						
						if(hasAssociatedProductsInserted == 1)
						{
							logger.info(LOG_APPLICATION_FLOW + "Completed insertion of associated product list to main table.");
						}
						else
						{
							logger.info(LOG_INFO + "Failed  to insert associated product list.");
							DeleteTemporaryStorageData deleteTemporaryStorageData = new DeleteTemporaryStorageData();
							deleteTemporaryStorageData.deleteTemporaryAssociatedProductDetails(input);
						}
					}
				}
				
				if(categoryList.size() > 0)
				{
					logger.info(LOG_APPLICATION_FLOW + "Started bulk insertion of category data.");
					Map<String,Object> categoryData = new HashMap<String,Object>();
					categoryData.put(FEED_ID, feedId);
					categoryData.put(CATEGORY_LIST, categoryList);
					BatchUpdateCategoryDataImpl batchUpdateCategoryDataImpl = BatchUpdateCategoryDataImpl.getInstance();
					Boolean categoryBatchUpdateStatus = batchUpdateCategoryDataImpl.runService(categoryData);
					categoryData = null;
					logger.info(LOG_APPLICATION_FLOW + "Completed bulk insertion of category data.");
				
					if(categoryBatchUpdateStatus)
					{
						InsertProductCategoryDataImpl insertProductCategoryDataImpl = InsertProductCategoryDataImpl.getInstance();
						Integer hasCategoryDataInserted = insertProductCategoryDataImpl.runService(input);
						
						if(hasCategoryDataInserted == 1)
						{
							logger.info(LOG_APPLICATION_FLOW + "Completed insertion of category data into main table.");
						}
						else
						{
							logger.info(LOG_INFO + "Failed to insert product category data.");
							//DeleteTemporaryStorageData deleteTemporaryStorageData = new DeleteTemporaryStorageData();
							//deleteTemporaryStorageData.deleteTemporaryProductCategoryDetails(input);
						}
					}
				}
				
				if(productImageList != null && productImageList.size() > 0)
				{
					logger.info(LOG_APPLICATION_FLOW + "Started bulk insertion of products other image data.");
					Map<String,Object> productImageData = new HashMap<String,Object>();
					productImageData.put(FEED_ID, feedId);
					productImageData.put(OTHER_IMAGE_LIST, productImageList);
					BatchUpdateProductOtherImageDataImpl batchUpdateProductOtherImageDataImpl = BatchUpdateProductOtherImageDataImpl.getInstance();
					Boolean otherImageBatchUpdateStatus = batchUpdateProductOtherImageDataImpl.runService(productImageData);
					productImageData = null;
					logger.info(LOG_APPLICATION_FLOW + "Completed bulk insertion of products other image data.");
					
					if(otherImageBatchUpdateStatus)
					{
						InsertProductsOtherImagesDataImpl insertProductsOtherImagesDataImpl = InsertProductsOtherImagesDataImpl.getInstance();
						Integer hasCategoryDataInserted = insertProductsOtherImagesDataImpl.runService(input);
						
						if(hasCategoryDataInserted == 1)
						{
							logger.info(LOG_APPLICATION_FLOW + "Completed insertion of products other images data into main table.");
						}
						else
						{
							logger.info(LOG_INFO + "Failed to insert products other images data.");
							DeleteTemporaryStorageData deleteTemporaryStorageData = new DeleteTemporaryStorageData();
							deleteTemporaryStorageData.deleteTemporaryProductOtherImageDetails(input);
						}
					}
				}
				
				if(productInventoryDataArrayList != null && productInventoryDataArrayList.size() > 0 && feedId != 5)
				{
					Map<String,Object> inputInventoryData = new HashMap<String,Object>();
		            inputInventoryData.put(FEED_ID, feedId);
		            inputInventoryData.put(PRODUCT_INVENTORY_LIST, productInventoryDataArrayList);
					UpdateProductInventoryDetails updateProductInventoryDetails = new UpdateProductInventoryDetails();
					updateProductInventoryDetails.updateProductInventory(inputInventoryData);
				}	
				
				// Insertion of updated file details to the table
				String[] tmpArr = feedUrl.trim().split("/");
				input.put(UPDATED_FEED_FILE_NAME, tmpArr[3]);
				InsertFeedUpdateFileDetailsImpl insertFeedUpdateFileDetailsImpl = InsertFeedUpdateFileDetailsImpl.getInstance();
				Integer hasFeedUpdateFileDetailsSaved = insertFeedUpdateFileDetailsImpl.runService(input);
				
				if(hasFeedUpdateFileDetailsSaved == 1)
				{								
					logger.info(LOG_APPLICATION_FLOW + "Completed insertion of feed update file details to main table.");
				}
				else
				{
					logger.info(LOG_INFO + "Failed  to insert feed update file details.");
					
					feedUpdateFailed = true;
					
					DeleteTemporaryStorageData deleteTemporaryProductOptionStorageData = new DeleteTemporaryStorageData();
					deleteTemporaryProductOptionStorageData.deleteTemporaryProductOptionDetails(input);
					
					DeleteTemporaryStorageData deleteTemporaryAssociatedProductStorageData = new DeleteTemporaryStorageData();
					deleteTemporaryAssociatedProductStorageData.deleteTemporaryAssociatedProductDetails(input);
					
					DeleteTemporaryStorageData deleteTemporaryProductCategoryStorageData = new DeleteTemporaryStorageData();
					deleteTemporaryProductCategoryStorageData.deleteTemporaryProductCategoryDetails(input);
					
					DeleteTemporaryStorageData deleteTemporaryProductOtherImageStorageData = new DeleteTemporaryStorageData();
					deleteTemporaryProductOtherImageStorageData.deleteTemporaryProductOtherImageDetails(input);
					
					DeleteTemporaryStorageData deleteTemporaryProductStorageData = new DeleteTemporaryStorageData();
					deleteTemporaryProductStorageData.deleteTemporaryProductDetails(input);
				}
				
				Integer hasCustomizationsUpdated = 0;
				Map<String,Object> customizationInput = new HashMap<String,Object>();
				customizationInput.put(FEED_ID, feedId);
				
				UpdateCustomizations updateCustomizations = UpdateCustomizations.getInstance();
				hasCustomizationsUpdated = updateCustomizations.runService(customizationInput);
				
				if(hasCustomizationsUpdated == 1)
				{
				   logger.info(LOG_APPLICATION_FLOW + "completed updating customizations for feed id -" + feedId);
				}
				else
				{
				   logger.info(LOG_APPLICATION_FLOW + "failed to update customizations for feed id -" + feedId);
				}
			}
			else
			{
				feedUpdateFailed = true;
				DeleteTemporaryStorageData deleteTemporaryStorageData = new DeleteTemporaryStorageData();
				deleteTemporaryStorageData.deleteTemporaryProductDetails(input);
			}
			
		}
		catch(Exception ex)
		{
			feedUpdateFailed = true;
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "ImportProductDataImpl" , "processProductFeedData", "Error occured while processing feed data" + feedId,"");
			logger.error(errorMessage,ex);
			
			DeleteTemporaryStorageData deleteTemporaryStorageData = new DeleteTemporaryStorageData();
			deleteTemporaryStorageData.deleteTemporaryProductDetailsAllData(input);
			
		}
		
		return 1;
	}
	
	public void processAssociatedProducts(List<String> productList,String productId,String associationType)
	{
		for(String associatedProductId : productList)
	    {
 	    	AssociatedProducts associatedProducts = new AssociatedProducts();
 	    	associatedProducts.setProductId(productId);
 	    	associatedProducts.setAssociatedProductId(associatedProductId);
 	    	associatedProducts.setAssociationType(associationType);
 	    	associatedProductList.add(associatedProducts);			        	    	
	    }
	}	
	
	public List<String> getFileList(String feedUrl, Map<String, Object> input)
	{
		List<String> existingFileList = new ArrayList<String>();
		
		try
		{			
			FTPClient ftpClient1 = new FTPClient();			
			URL url = new URL(URLDecoder.decode(feedUrl));	
			ftpClient1.connect(url.getHost());
			ftpClient1.login(ftpUserName, ftpPassword);
			ftpClient1.enterLocalPassiveMode();
			
			ftpClient1.setConnectTimeout(10000);		
			ftpClient1.setFileType(FTP.BINARY_FILE_TYPE);
			String store_code = (String) input.get(STORE_CODE);			
			
			FTPFile[] files = ftpClient1.listFiles(url.getPath());			
			
			for(FTPFile file : files)
			{
				if(file.getName().matches(".*?"+store_code+".*?"))
				{
					existingFileList.add(feedUrl + "/" + file.getName());
				}
			}
			
			ftpClient1.logout();
			ftpClient1.disconnect();
		}
		catch(Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "ImportProductDataImpl" , "getFileList", "Error occured while fetching feed file from ftp - " + feedId,"");
			logger.error(errorMessage,ex);
		}
				
		return existingFileList;
	}
	
	public List<String> getUpdatedFeedFileDetails(Map<String,Object> input)
	{		
		GetUpdatedFeedFileDetails getUpdatedFeedFileDetails = GetUpdatedFeedFileDetails.getInstance();
		return getUpdatedFeedFileDetails.runService(input);
	}
}
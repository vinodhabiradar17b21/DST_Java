package com.increasingly.recommender.impl;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jersey.repackaged.com.google.common.base.Joiner;
import static com.increasingly.recommender.constants.Constants.*;

import org.apache.logging.log4j.ThreadContext;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.constants.RequestType;
import com.increasingly.recommender.impl.collections.ClientApiKeyDetailsCache;
import com.increasingly.recommender.impl.collections.ProductIdCache;
import com.increasingly.recommender.impl.db.TrackBundleSearchRequest;
import com.increasingly.recommender.utils.ExportDataToTextFile;
import com.increasingly.recommender.utils.FormatLoggerMessage;
import com.increasingly.recommender.utils.GeneralUtils;

public class BundleRecommender
{
	private static final Logger logger = LoggerFactory.getLogger(BundleRecommender.class.getClass());
	private BundleRequest bundleRequest;
	private BundleResponse bundleResponse;
	private BundleResult bundleResult;
	private List<Integer> bundleIdList = new ArrayList<Integer>();	
	private BundleConfiguration bundleConfiguration;
	private GeoDetails geoDetails;
		
	/**
	 * Process Times
	 */
	private ProcessTimes processTimes = new ProcessTimes();
	DateTime startDatetime = DateTime.now();
	DateTime processStartDateTime = DateTime.now();
	
	public BundleRecommender()
	{
		bundleResult = new BundleResult();
		bundleResponse = new BundleResponse();
	}
	
	/* Response Values */
	public Charset getResponseEncoding()
	{
		return bundleResponse.getEncoding();
	}

	public String getResponseContent()
	{
		return bundleResponse.getContent();
	}

	public BundleJsonResponseContent getBundleJsonResponseContent()
	{
		return bundleResponse.getBundleJsonResponseContent();
	}
	
	public BundleAvailabilityCheckJsonResponse getBundleAvailabilityJsonResponse()
	{
		return bundleResponse.getBundleAvailabilityJsonResponse();
	}
	
	public ProductsListWithBundleAvailablityDetails getProductsListWithBundleAvailablityJsonResponse()
	{
		return bundleResponse.getProductsListWithBundleAvailablityJsonResponse();
	}
	
	public ProcessTimes getResponseProcessTimes()
	{
		return bundleResponse.getResponseProcessTimes();
	}
	
	public void getBundles(BundleRequest bundleRequest)
	{
		
		try
		{			
			this.bundleRequest = bundleRequest;
			readAndValidateBundleRequest();
			
			logger.info(LOG_APPLICATION_FLOW + "Completed the validating request.");

		    decryptBundleRequest();
			
			logger.info(LOG_APPLICATION_FLOW + "Completed the decrypting the request parameters.");
			switch (bundleRequest.getRequestType())
			{
			 case RequestType.RECOMMEND_BUNDLES:
			 	  getRecommendedProductBundles();
				  break;	
			 case RequestType.BUNDLES_AVAILABILITY_CHECK:
				  getRecommendedProductBundles();
				  break;
			 case RequestType.BUNDLES_AVAILABILITY_CHECK_MULTIPLE:
				  getRecommendedProductBundles();
				  break;			
			}
			
			bundleResponse.getResponseProcessTimes().setTotalProcessTime(processTimes.getTimeTaken(processStartDateTime));
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "getBundles" , "getBundles method error" , bundleRequest.getQueryString());
			logger.error(errorMessage,ex);
		}
	}
	
	private void getRecommendedProductBundles()
	{
					
		try
		{
			if (!bundleResponse.getCanContinue())
			{
				return;
			}

			/* Read Querystring Parameters */
			try
			{
				// Read Client ID
				bundleRequest.setClientId(Integer.parseInt(bundleRequest.getQueryStringParameter(CLIENT_ID,"0")));
				ThreadContext.put("client_id",bundleRequest.getClientId().toString());
								
				bundleRequest.setApiKey(bundleRequest.getQueryStringParameter(API_KEY));
				ThreadContext.put("api_key",bundleRequest.getApiKey());
				
				bundleRequest.setProductIds(bundleRequest.getQueryStringParameter(PRODUCT_IDS));
				ThreadContext.put("product_ids",bundleRequest.getProductIds());
												
				bundleRequest.setCategoryIds(bundleRequest.getQueryStringParameter(CATEGORY_ID));
				ThreadContext.put("category_ids",bundleRequest.getCategoryIds());
				
				bundleRequest.setPageType(bundleRequest.getQueryStringParameter(PAGE_TYPE));
				ThreadContext.put("page_type",bundleRequest.getPageType());
				
				bundleRequest.setClientVisitorId(bundleRequest.getQueryStringParameter(CLIENT_VISITOR_ID));
				ThreadContext.put("client_visitor_id",bundleRequest.getClientVisitorId());
				
				bundleRequest.setIsPsku(bundleRequest.getQueryStringParameter(IS_PSKU));
				ThreadContext.put("is_psku",bundleRequest.getIsPsku());				
				
				if(!bundleRequest.getQueryStringParameter(BACK_FILL_BUNDLES).isEmpty())
				{
					if(Integer.parseInt(bundleRequest.getQueryStringParameter(BACK_FILL_BUNDLES))==1)
					{
						bundleRequest.setBackFillBundles(true);
					}
					else
					{
						bundleRequest.setBackFillBundles(false);
					}
					ThreadContext.put("bfb",bundleRequest.getBackFillBundles().toString());
				}
				
				bundleRequest.setPageTypeId();
				bundleRequest.setNoOfProductsInBundle(Integer.parseInt(bundleRequest.getQueryStringParameter(NUMBER_OF_PRODUCTS_IN_BUNDLE,"0")));
				
				if(!bundleRequest.getQueryStringParameter(BT).isEmpty())
				{
					int bundleTypeId  = Integer.parseInt(bundleRequest.getQueryStringParameter(BT));
					bundleRequest.setBundleTypeId(bundleTypeId);
					ThreadContext.put("bt",String.valueOf(bundleTypeId));
				}

				if(!bundleRequest.getQueryStringParameter(NO_OF_BUNDLES).isEmpty())
				{
					int noOfBundles  = Integer.parseInt(bundleRequest.getQueryStringParameter(NO_OF_BUNDLES));
					bundleRequest.setNoOfBundles(noOfBundles);
					ThreadContext.put("no_of_bundles",String.valueOf(noOfBundles));
				}
				
				List<String> customerProductIdList = new ArrayList<String>();
				
				if(bundleRequest.getProductIds().trim().length() > 0)
				{
					customerProductIdList = Arrays.asList(bundleRequest.getProductIds().split(","));
					
					if(customerProductIdList.size() > 0)
					{
					  bundleRequest.setCustomerProductIdList(customerProductIdList);
					}
				}
				
				if(bundleRequest.getCategoryIds().trim().length() > 0)
				{
					List<String> customerCategoryIdList = Arrays.asList(bundleRequest.getCategoryIds().trim().split(","));
					
					if(customerCategoryIdList.size() > 0)
					{
					  bundleRequest.setCustomerCategoryIdList(customerCategoryIdList);
					}
				}
				
				if(bundleRequest.getPageType().isEmpty())
				{
					if(customerProductIdList.size() == 1)
					{
						bundleRequest.setPageType(PRODUCT_PAGE);
					}
					else if(customerProductIdList.size() > 1)
					{
						bundleRequest.setPageType(CART_PAGE);	
					}
					else if(bundleRequest.getCategoryIds().length() > 0)
					{
						bundleRequest.setPageType(CATEGORY_PAGE);	
					}
				}
											
				ThreadContext.put("HostName", Configuration.getMachineName());			
								
			}
			catch (Exception ex)
			{
				String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "getProductBundles" , "Error reading query string parameters" , "");
				logger.error(errorMessage,ex);
			}

			if (bundleRequest.getApiKey().length() > 0 || bundleRequest.getClientId() > 0)
			{
				if(bundleRequest.getApiKey().length() > 0 && (bundleRequest.getClientId() == null || bundleRequest.getClientId() <= 0))
				{
					Integer clientId = ClientApiKeyDetailsCache.getCache().get(bundleRequest.getApiKey());
					
					if(clientId != null && clientId > 0)
					{
					  bundleRequest.setClientId(clientId);
					}
					else
					{
						return;
					}
				}
				
				String userGuId = "";
				
				if (bundleRequest.getCookieData().get(INCREASINGLY_BUNDLE_COOKIE) != null && bundleRequest.getCookieData().get(INCREASINGLY_BUNDLE_COOKIE).getValue().length() > 0)
				{
					userGuId = URLDecoder.decode(bundleRequest.getCookieData().get(INCREASINGLY_BUNDLE_COOKIE).getValue());
					bundleRequest.setVisitorId(userGuId);
					ThreadContext.put("VisitorId",bundleRequest.getVisitorId());
				}
				
				if(userGuId.isEmpty() && !bundleRequest.getClientVisitorId().isEmpty())
				{
				  userGuId = bundleRequest.getClientVisitorId();	
				}
				
				// Read Bundle Information
				BundleConfigurationService bundleConfigurationService = new BundleConfigurationService();
				
				this.bundleConfiguration = bundleConfigurationService.getBundleConfiguration(bundleResponse, bundleRequest);
				bundleResult.setBundleConfiguration(this.bundleConfiguration);
				logger.info(LOG_APPLICATION_FLOW + "Completed the reading bundle configuration information for the client id -" + bundleRequest.getClientId());
				
				if(bundleConfiguration != null)
				{	
					if(bundleConfiguration.getUseGeoCountryTargetting())
					{	
						if(bundleConfiguration.getAllowedGeoCountryIdList() != null && bundleConfiguration.getAllowedGeoCountryIdList().size() > 0)
						{
							geoDetails = new GeoDetails();
							geoDetails.setUserGeoDetails(bundleRequest.getUserIpAddress());
							
							if(!bundleConfiguration.getAllowedGeoCountryIdList().contains(geoDetails.getCountryId()))
							{
								bundleResponse.setCanContinue(false);
								String infoMessage = FormatLoggerMessage.formatInfo(LOG_INFO , "getRecommendedProductBundles" , "Visitor country - '" + geoDetails.getCountryName() + "' is exluded from bundle serving." , "Visitor Ip - " + bundleRequest.getUserIpAddress() + " Visitor Id -" + bundleRequest.getClientVisitorId());
								logger.info(infoMessage);
							}
						}
					}
					
					if(bundleResponse.getCanContinue())
					{
						
						String controlGroupVisitorIdCharSet = bundleConfiguration.getControlGroupVisitorIdCharSet();
						
						if (controlGroupVisitorIdCharSet == null || controlGroupVisitorIdCharSet.isEmpty() || userGuId.isEmpty() || (!controlGroupVisitorIdCharSet.isEmpty() && !userGuId.isEmpty() 
								&& controlGroupVisitorIdCharSet.contains(userGuId.substring((userGuId.length() - 1), userGuId.length()))))
						{
							bundleResult.setFeedId(bundleConfiguration.getFeedId());
							
							lookupInternalProductAndCategoryIdInfo();
											
							if(bundleConfiguration.getIsCategoryExclusionEnabled() && (bundleRequest.getPageType().equals(PRODUCT_PAGE) || bundleRequest.getPageType().equals(CATEGORY_PAGE)))
							{
								if(bundleResult.getInternalCategoryList() != null && bundleResult.getInternalCategoryList().size() > 0)
								{
									if(bundleConfiguration.getCategoryExclusionList() != null && bundleConfiguration.getCategoryExclusionList().size() > 0)
									{
										if(bundleRequest.getCustomerCategoryIdList() != null && bundleRequest.getCustomerCategoryIdList().size() > 0)
										{
											for(int categoryId : bundleResult.getInternalCategoryList())
											{
												if(!bundleResult.getBundleConfiguration().getCategoryExclusionList().contains(categoryId))
												{
													bundleResponse.setCanContinue(true);
													break; 
												}
											}
										}
										else if(bundleRequest.getCustomerCategoryIdList() == null || bundleRequest.getCustomerCategoryIdList().isEmpty() || bundleRequest.getCustomerCategoryIdList().size() == 0)
										{
											for(int categoryId : bundleResult.getInternalCategoryList())
											{
												if(!bundleResult.getBundleConfiguration().getCategoryExclusionList().contains(categoryId))
												{
													bundleResponse.setCanContinue(true);
													break; 
												}
												else
												{
													bundleResponse.setCanContinue(false);
												}
											}
										}
									}
								}
							}
							
							if(bundleResponse.getCanContinue())
							{
								if(bundleRequest.getRequestType() == RequestType.BUNDLES_AVAILABILITY_CHECK_MULTIPLE)
								{
									ProductsListWithBundleAvailablityDetails productsListWithBundleAvailablityDetails = new ProductsListWithBundleAvailablityDetails();
									productsListWithBundleAvailablityDetails.setProductListWithNoOfBundleAvailabilityDetails(getRelevantProductBundlesCountForMultipleItems());
									bundleResponse.setProductsListWithBundleAvailablityJsonResponse(productsListWithBundleAvailablityDetails);
								}								
								else
								{
									// Bundle Selection
									getRelevantProductBundles();
								}
								
								if(bundleIdList.size() > 0)
								{	
									if(bundleRequest.getRequestType() == RequestType.BUNDLES_AVAILABILITY_CHECK)
									{	
										if(bundleRequest.getCustomerProductIdList() != null && bundleRequest.getCustomerProductIdList().size() > 0)
										{
										   BundleAvailabilityCheckJsonResponse bundleAvailabilityCheckJsonResponse = new BundleAvailabilityCheckJsonResponse();
										   bundleAvailabilityCheckJsonResponse.setProductListWithBundles(bundleRequest.getCustomerProductIdList());
										   bundleResponse.setBundleAvailabilityJsonResponse(bundleAvailabilityCheckJsonResponse);
										}
										
									}
									else
									{
										startDatetime = DateTime.now();
										BundleDetailsService bundleDetailsService = new BundleDetailsService();
										List<BundleDetailsResponseContent> bundleDetailsList = bundleDetailsService.getBundleDetails(bundleIdList,bundleRequest,bundleResult,bundleResponse);
										bundleResponse.getResponseProcessTimes().setTimeTakenToGetBundleDetails(processTimes.getTimeTaken(startDatetime));
										
										if(bundleDetailsList != null && bundleDetailsList.size() > 0)
										{
											BundleJsonResponseContent bundleJsonResponseContent = new BundleJsonResponseContent();
											bundleJsonResponseContent.setBundleCount(bundleDetailsList.size());
											bundleJsonResponseContent.setBundles(bundleDetailsList);
											
											if(bundleConfiguration.getIsFreeShippingActive())
											{
											 bundleJsonResponseContent.setIsFreeShippingActive(bundleConfiguration.getIsFreeShippingActive());
											 bundleJsonResponseContent.setFreeShippingSubTotal(bundleConfiguration.getFreeShippingSubTotal());
											 bundleJsonResponseContent.setFreeShippingTitle(bundleConfiguration.getFreeShippingTitle());											 
											}
											
											if(bundleConfiguration.getHasClientRecommendations() && bundleRequest.getPageType().equalsIgnoreCase(PRODUCT_PAGE))
											{
												List<Long> clientRecommendationsInternalProductIdList = bundleConfiguration.getClientProductRecommendationList();
												
												if(clientRecommendationsInternalProductIdList != null && clientRecommendationsInternalProductIdList.containsAll(bundleResult.getInternalProductIdList()))
												{
													bundleJsonResponseContent.setHasClientBundleRecommendations(true);
												}
											}
											
											bundleResponse.setBundleJsonResponseContent(bundleJsonResponseContent);									
											
											for(BundleDetailsResponseContent bundleDetailsResponseContent : bundleDetailsList)
											{
												bundleResponse.setFinalBundleList(bundleDetailsResponseContent.getBundleId());
											}
										}										
									}
								}
								else
								{
									String infoMessage = FormatLoggerMessage.formatInfo(LOG_INFO , "getRecommendedProductBundles" , "There are no relevent bundles to serve." , "");
									logger.info(infoMessage);
								}
							}
							
						}
					}
					
					
				}
				else
				{
					String infoMessage = FormatLoggerMessage.formatInfo(LOG_INFO , "getRecommendedProductBundles" , "Failed to get bundle configuration details." , "");
					logger.info(infoMessage);
				}
				
				
			}
			else
			{
				bundleResponse.setCanContinue(false);
				String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "getRecommendedProductBundles" , "Both API Key and Client Id are missing" , "");
				logger.error(errorMessage);
			}
				
		}
		catch (Exception ex)
		{
			bundleResponse.setCanContinue(false);
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR ,"BundleRecommender" , "getProductBundles" ,"");
			logger.error(errorMessage,ex);
		}
		finally
		{ 
			if(bundleRequest.getRequestType() == RequestType.RECOMMEND_BUNDLES)
			{  
				startDatetime = DateTime.now();
				trackBundleRequest();
				bundleResponse.getResponseProcessTimes().setTimeTakenToInsertBundleRequestDetails(processTimes.getTimeTaken(startDatetime));
			}
			
		}
	}
	
	/**
	 * Validates the Request
	 */
	private void readAndValidateBundleRequest()
	{
		try
		{
			if (!bundleRequest.readAndValidateBundleRequest())
			{				
				bundleResponse.setCanContinue(false);
			}
		}
		catch (Exception ex)
		{
			bundleResponse.setCanContinue(false);
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "ValidateBundleRequest" , "Error validating request" , "");
			logger.error(errorMessage,ex);
		}
	}

	/*
	 * Decrypts the Querystring parameter of request
	 */
	private void decryptBundleRequest()
	{
		try
		{
			if (!bundleResponse.getCanContinue())
			{
				return;
			}

			if (!bundleRequest.Decrypt())
			{				
				bundleResponse.setCanContinue(false);
			}
		}
		catch (Exception ex)
		{
			bundleResponse.setCanContinue(false);
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "Decryption error" , "Error decrypting request" , "");
			logger.error(errorMessage,ex);
		}
	}
	
	private void lookupInternalProductAndCategoryIdInfo()
	{
		try
		{
			if (!bundleResponse.getCanContinue())
			{
				return;
			}

			// Get Internal Product Id
			if (bundleRequest.getCustomerProductIdList() != null && bundleRequest.getCustomerProductIdList().size() > 0)
			{
				startDatetime = DateTime.now();
				ProductIdService productIdService = new ProductIdService();
				productIdService.getCachedInternalProductIdList(bundleRequest.getCustomerProductIdList(),bundleRequest, bundleResult);
				bundleResponse.getResponseProcessTimes().setTimeTakenToGetInternalProductIds(processTimes.getTimeTaken(startDatetime));
				
				if(bundleResult.getInternalProductIdList() != null && bundleResult.getInternalProductIdList().size() > 0)
				{
					startDatetime = DateTime.now();
					ProductCategoryService productCategoryService = new ProductCategoryService();
					productCategoryService.getProductCategoryList(bundleResult.getInternalProductIdList(),bundleRequest,bundleResult);
					bundleResponse.getResponseProcessTimes().setTimeTakenToGetCategoryProductIdListOfRequestedProducts(processTimes.getTimeTaken(startDatetime));
				}
			}
		
			// Get Internal Category Id list
			if (bundleRequest.getCustomerCategoryIdList() != null && bundleRequest.getCustomerCategoryIdList().size() > 0)
			{
				startDatetime = DateTime.now();
				CategoryIdService categoryIdService = new CategoryIdService();
				categoryIdService.getCachedInternalCategoryId(bundleRequest.getCustomerCategoryIdList(), bundleRequest, bundleResult);
				bundleResponse.getResponseProcessTimes().setTimeTakenToGetInternalCategoryIds(processTimes.getTimeTaken(startDatetime));
			}
			
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "lookupInternalProductAndCategoryIdInfo" , "Error looking up Internal Product Ids and Category id" ,"");
			logger.error(errorMessage,ex);
		}
	}
	
		
	public void getRelevantProductBundles()
	{		
		List<Integer> bundleIdList = null;
		
		Integer bundleTypeId = bundleRequest.getBundleTypeId();
		
		
		String userGuId = "";
		String controlGroupVisitorIdCharSet = "01234abc";		
		
		if(userGuId.isEmpty() && !bundleRequest.getClientVisitorId().isEmpty())
		{
		  userGuId = bundleRequest.getClientVisitorId();	
		}		
		
		//|| bundleResult.getInternalCategoryList().contains(9657) || bundleResult.getInternalCategoryList().contains(9642) || bundleResult.getInternalCategoryList().contains(9639)
		//|| bundleResult.getInternalCategoryList().contains(9645) || bundleResult.getInternalCategoryList().contains(9660) || bundleResult.getInternalCategoryList().contains(9839) || bundleResult.getInternalCategoryList().contains(9678)
							
		if (bundleRequest.getClientId() == 15  && (!controlGroupVisitorIdCharSet.isEmpty() && !userGuId.isEmpty() && controlGroupVisitorIdCharSet.contains(userGuId.substring((userGuId.length() - 1), userGuId.length())))
				&& (bundleResult.getInternalCategoryList() != null && (bundleResult.getInternalCategoryList().contains(9740) || bundleResult.getInternalCategoryList().contains(9678) || bundleResult.getInternalCategoryList().contains(9660))))
		{
			bundleTypeId = 13;
		}
		
		
		if(bundleRequest.getPageType().equals(PRODUCT_PAGE) || bundleRequest.getPageType().equals(CART_PAGE) || bundleRequest.getPageType().equals(CHECKOUT_PAGE))
		{
			if(bundleResult.getInternalProductIdList() != null && bundleResult.getInternalProductIdList().size() > 0)
			{
				startDatetime = DateTime.now();
				ProductIdBundleListService productIdBundleListService = new ProductIdBundleListService();
				bundleIdList = productIdBundleListService.getCachedBundleIdList(bundleResult.getInternalProductIdList(),bundleRequest,bundleResult,bundleResponse,bundleTypeId);
				bundleResponse.getResponseProcessTimes().setTimeTakenToGetProductPageBundleIdList(processTimes.getTimeTaken(startDatetime));
			}
		}
		else if(bundleRequest.getPageType().equals(CATEGORY_PAGE))
		{
			if(bundleResult.getInternalCategoryList() != null && bundleResult.getInternalCategoryList().size() > 0)
			{
				startDatetime = DateTime.now();
				CategoryIdBundleListService categoryIdBundleListService = new CategoryIdBundleListService();
				bundleIdList = categoryIdBundleListService.getCachedBundleIdList(bundleResult.getInternalCategoryList(),bundleRequest,bundleResult);
				bundleResponse.getResponseProcessTimes().setTimeTakenToGetCategoryPageBundleIdList(processTimes.getTimeTaken(startDatetime));
			}
		}	
		if(bundleIdList != null && bundleIdList.size() > 0)
		{
			this.bundleIdList.addAll(bundleIdList);
		}
	}	
	
	
	public List<ProductWithBundleAvailability> getRelevantProductBundlesCountForMultipleItems()
	{		
		DateTime startDatetime = DateTime.now();
		List<ProductWithBundleAvailability> productsWithBundleAvailablityDetailsList = new ArrayList<ProductWithBundleAvailability>();
		
		if(bundleResult.getInternalProductIdList() != null && bundleResult.getInternalProductIdList().size() > 0)
		{
			MultipleProductIdBundleListService multipleProductIdBundleListService = new MultipleProductIdBundleListService();
			Map<Long,List<BundleIdWithPurchaseCount>> multipleProductBundleIdListWithPurchaseCount = multipleProductIdBundleListService.getCachedBundleIdList(bundleResult.getInternalProductIdList(),bundleRequest,bundleResult,bundleResponse);
		    
			Set<Integer> tempAllBundleIdList = new HashSet<Integer>();
			Map<Long,List<Integer>> productBundleIdList = new HashMap<Long,List<Integer>>();
			
			for(Long internalProductId: bundleResult.getInternalProductIdList())
			{		
				List<Integer> tempBundleIdList = new ArrayList<Integer>();
				List<BundleIdWithPurchaseCount> bundleIdListWithPurchaseCount = multipleProductBundleIdListWithPurchaseCount.get(internalProductId);
				
				if(bundleIdListWithPurchaseCount != null && bundleIdListWithPurchaseCount.size() > 0)
				{
				  Collections.sort(bundleIdListWithPurchaseCount, new SortBundlesWithPurchaseRate());
				  
				  for(BundleIdWithPurchaseCount bundleItem :bundleIdListWithPurchaseCount)
				  {
					  tempBundleIdList.add(bundleItem.getBundleId());
				  }
				}	
				
				if(tempBundleIdList.size() > 0)
				{
					tempAllBundleIdList.addAll(tempBundleIdList);
					productBundleIdList.put(internalProductId, tempBundleIdList);
				}
			}
			
						
			BundleLinkProductInfoService bundleLinkProductInfoService = new BundleLinkProductInfoService();
			Map<Integer,List<BundleLinkProductInfo>> bundleLinkProductInfoList = bundleLinkProductInfoService.getBundleLinkProductInfoList(new ArrayList(tempAllBundleIdList));
			
			for(String customerProductId : bundleRequest.getCustomerProductIdList())
			{
				String key = GeneralUtils.calculateProductKey(bundleResult.getFeedId(), customerProductId);
				Long internalProductId = ProductIdCache.getCache().get(key);
				
				List<Integer> tempBundleIdList = productBundleIdList.get(internalProductId);
				
				String field1 = "";
				
				if(tempBundleIdList != null && tempBundleIdList.size() > 0)
				{
					if(bundleLinkProductInfoList != null && bundleLinkProductInfoList.size() > 0)
					{
						for(Integer bundleId:bundleLinkProductInfoList.keySet())
						{
							if(tempBundleIdList.contains(bundleId))
							{
								for(BundleLinkProductInfo bundleLinkProductInfo:bundleLinkProductInfoList.get(bundleId))
								{
									if(!bundleLinkProductInfo.getField1().isEmpty())
									{
										field1 = bundleLinkProductInfo.getField1();
										break;
									}									
								}
								
							}
							
							if(!field1.isEmpty())
							{
								break;
							}
							
						}						
					}
					
					ProductWithBundleAvailability productWithBundleAvailability = new ProductWithBundleAvailability();
					productWithBundleAvailability.setCustomerProductId(customerProductId);
					productWithBundleAvailability.setNumberOfBundles(tempBundleIdList.size());
					productWithBundleAvailability.setField1(field1);
					productsWithBundleAvailablityDetailsList.add(productWithBundleAvailability);
				}
			}
			
		}
		
		System.out.println(processTimes.getTimeTaken(startDatetime));
		return productsWithBundleAvailablityDetailsList;		
			
	}	
	
	public void trackBundleRequest()
	{
		try
		{
			Map<String,Object> bundleSearchRequestMap = new HashMap<String,Object>();
			
			bundleSearchRequestMap.put(CLIENT_ID, bundleRequest.getClientId());
			bundleSearchRequestMap.put(PRODUCT_IDS, bundleRequest.getProductIds());
			bundleSearchRequestMap.put(CATEGORY_ID, bundleRequest.getCategoryIds());
			
			if(!bundleRequest.getPageType().isEmpty())
			{
				int pageTypeId = 0;
				if(bundleRequest.getPageType().equalsIgnoreCase("catalog_product_view"))
				{
					pageTypeId = 100;
				}
				if(bundleRequest.getPageType().equalsIgnoreCase("catalog_category_view"))
				{
					pageTypeId = 101;
				}
				if(bundleRequest.getPageType().equalsIgnoreCase("checkout_cart_index"))
				{
					pageTypeId = 103;
				}
				if(bundleRequest.getPageType().equalsIgnoreCase("checkout_index"))
				{
					pageTypeId = 108;
				}
				bundleSearchRequestMap.put(PAGE_TYPE_ID, pageTypeId);
			}
			
			bundleSearchRequestMap.put(CLIENT_VISITOR_ID, bundleRequest.getClientVisitorId());
			bundleSearchRequestMap.put(USER_AGENT, bundleRequest.getUserAgent());
			bundleSearchRequestMap.put(SEARCH_DATE, DateTime.now().toString("y-M-d H:m:s"));
			
			if(bundleResponse.getFinalBundleList().size() > 0)
			{
			  bundleSearchRequestMap.put(BUNDLE_ID_LIST, Joiner.on(",").join(bundleResponse.getFinalBundleList()).toString());
			}
			else
			{
				 bundleSearchRequestMap.put(BUNDLE_ID_LIST,"");
			}
			
			if(geoDetails != null)
			{
				if(geoDetails.getCountryId() > 0)
				{
					bundleSearchRequestMap.put(COUNTRY_ID, geoDetails.getCountryId());
				}
			}
			
			String isDbInsertion=Configuration.getIsDBInsertion();
			
			if(isDbInsertion.trim().equalsIgnoreCase("true")) {
			 TrackBundleSearchRequest trackBundleSearchRequest = TrackBundleSearchRequest.getInstance();
			 trackBundleSearchRequest.runService(bundleSearchRequestMap);
			}			
			else {
			   ExportDataToTextFile exportdatatotextfile=new ExportDataToTextFile();
			   exportdatatotextfile.writeInTextFile(bundleSearchRequestMap);
			}
						
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		
	}
	
	
}
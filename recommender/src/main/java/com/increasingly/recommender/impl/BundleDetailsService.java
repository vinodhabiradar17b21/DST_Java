package com.increasingly.recommender.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.impl.collections.BundleDetailsCache;
import com.increasingly.recommender.impl.collections.BundleIdProductListCache;
import com.increasingly.recommender.impl.db.BundleDetailsList;
import com.increasingly.recommender.impl.db.BundleProductItemList;
import com.increasingly.recommender.utils.FormatLoggerMessage;

import static com.increasingly.recommender.constants.Constants.*;
import jersey.repackaged.com.google.common.base.Joiner;

public class BundleDetailsService
{
	private ProcessTimes processTimes = new ProcessTimes();	
	private static DecimalFormat decimalFormatter = new DecimalFormat("#0.00");
	private static final Logger logger = LoggerFactory.getLogger(BundleDetailsService.class.getClass());
			
	public List<BundleDetailsResponseContent> getBundleDetails(List<Integer> bundleIdList,BundleRequest bundleRequest,BundleResult bundleResult,BundleResponse bundleResponse)
	{
		//List<BundleDetails> bundleDetailsList = new ArrayList<BundleDetails>();
		List<BundleDetailsResponseContent> bundleDetailsList = new ArrayList<BundleDetailsResponseContent>();
		List<Integer> nonCachedBundleIdList = new ArrayList<Integer>();

		try
		{
			for (Integer bundleId : bundleIdList)
			{				
				BundleDetails bundleDetails = BundleDetailsCache.getCache().get(bundleId);

				if (bundleDetails != null)
				{
					//bundleDetailsList.add(bundleDetails);					
				}	
				else
				{
				  nonCachedBundleIdList.add(bundleId);
				}
			}
				
			if (nonCachedBundleIdList.size() > 0)
			{
				// Get's product item list for each of bundles in set and puts in cache.
				BundleIdProductListService bundleIdProductListService = new BundleIdProductListService();
				bundleIdProductListService.getBundleIdProductList(nonCachedBundleIdList);
				
				ArrayList<Map<String, Object>> multipleBundleDetailsMap = new ArrayList<Map<String, Object>>();
				Map<String, Object> input = new HashMap<String, Object>();			
				input.put(BUNDLE_ID_LIST, Joiner.on(",").join(nonCachedBundleIdList).toString());
				BundleDetailsList bundleDetailsListFromDB = BundleDetailsList.getInstance();
				multipleBundleDetailsMap =  bundleDetailsListFromDB.runService(input);		
				
				DateTime startDatetime = DateTime.now();
				
				if(multipleBundleDetailsMap != null)
				{				
					List<Long> allBundleProductItemList = new ArrayList<Long>();
					for (Map<String, Object> item : multipleBundleDetailsMap)
					{
						Integer bundleId = Integer.parseInt(item.get("BundleID").toString());
						
						List<Long> tempBundleProductItemList = BundleIdProductListCache.getCache().get(bundleId);
						
						if(tempBundleProductItemList != null)
						{
							for(Long internalProductId: tempBundleProductItemList)
							{
								if(!allBundleProductItemList.contains(internalProductId))
								{
									allBundleProductItemList.add(internalProductId);
								}
							}
						}						
					}
					
					if(allBundleProductItemList != null && allBundleProductItemList.size() > 0 )
				    {
				      ProductDetailsService productDetailsService = new ProductDetailsService();
				      productDetailsService.getProductDetails(allBundleProductItemList,bundleResult);				    
				    }
					
					for (Map<String, Object> item : multipleBundleDetailsMap)
					{		
						BundleDetails bundleDetails = new BundleDetails();
						Integer bundleId = Integer.parseInt(item.get("BundleID").toString());	
					    bundleDetails.setBundleId(bundleId);
					    
					    /* if discounting is handling at database side
					    double totalPrice = Double.parseDouble(item.get("TotalPrice").toString());
					    bundleDetails.setTotalPrice(decimalFormatter.format(totalPrice));
					    
					    double bundlePrice = 0;
					    if(item.get("BundlePrice") != null)
					    {
					       bundlePrice = Double.parseDouble(item.get("BundlePrice").toString());
					      					       
					       if(totalPrice > bundlePrice)
					       {
					    	   bundleDetails.setBundlePrice(decimalFormatter.format(bundlePrice));
					    	   Double discountPrice = totalPrice-bundlePrice;
					    	   bundleDetails.setDiscountPrice(decimalFormatter.format(discountPrice));
					       }					      
					    }
					    */
					    
					    bundleDetails.setProductCount(Integer.parseInt(item.get("ProuductCount").toString()));
					    
					    if(item.get("BundleDiscountPercentage")!= null && !item.get("BundleDiscountPercentage").toString().isEmpty())
					    {
					      bundleDetails.setBundleDiscountPercentage(Double.parseDouble(item.get("BundleDiscountPercentage").toString()));
					    }
					    
					    List<Long> bundleProductItemList = new ArrayList<Long>();
					    bundleProductItemList = BundleIdProductListCache.getCache().get(bundleId);
					    
					    if(bundleProductItemList != null && bundleProductItemList.size() > 0 && 
					    		bundleProductItemList.size() == (int)bundleDetails.getProductCount())
					    {
					      ProductDetailsService productDetailsService = new ProductDetailsService();
					      List<ProductDetails> productDetailsList = productDetailsService.getProductDetails(bundleProductItemList,bundleResult);
					    
					      if(productDetailsList != null && productDetailsList.size() > 0 &&
					    		  productDetailsList.size() == (int)bundleDetails.getProductCount())
					      {
					        bundleDetails.setBundleProductItemDetails(productDetailsList);	
					        double totalPrice = 0;
					        double totalSpecialPrice = 0;
					        double bundlePrice = 0;
					        
					        for(ProductDetails productDetails : productDetailsList)
					        {
					        	double price = 0;
					        	double specialPrice = 0;
					        	
					        	if(productDetails.getPrice() != null)
					        	{
					        		price = Double.parseDouble(productDetails.getPrice());
					        	}
					        	
					        	if(price > 0)
					        	{
					        	  totalPrice = totalPrice + price;
					        	}
					        	
					        	if(productDetails.getSpecialPrice() != null)
					        	{
					        		specialPrice = Double.parseDouble(productDetails.getSpecialPrice());
					        	}
					        	
					        	if(specialPrice > 0 && specialPrice <= price)
					        	{
					        		totalSpecialPrice = totalSpecialPrice + specialPrice;
					        	}
					        	else if(price > 0)
					        	{
					        		totalSpecialPrice = totalSpecialPrice + price;
					        	}
					        }
					        
					        if(totalPrice > 0)
					        {
					        	bundleDetails.setTotalPrice(decimalFormatter.format(totalPrice));
					        	bundleDetails.setTotalSpecialPrice(decimalFormatter.format(totalSpecialPrice));
					        	double bundleDiscountPercentage = 1.0;
					        	
					        	if(bundleDetails.getBundleDiscountPercentage() != null && bundleDetails.getBundleDiscountPercentage() > 0)
					        	{
					        		bundleDiscountPercentage = (100 - bundleDetails.getBundleDiscountPercentage())/100;
					        		
					        		if(bundleDiscountPercentage <= 0)
					        		{
					        			bundleDiscountPercentage = 1.0;
					        		}
					        	}
					        	
					        	if(totalSpecialPrice > 0)
					        	{
					        		bundlePrice = totalSpecialPrice * bundleDiscountPercentage;
					        	}
					        	else
					        	{
					        		bundlePrice = totalPrice * bundleDiscountPercentage;
					        	}
					        						        	
							    bundleDetails.setBundlePrice(decimalFormatter.format(bundlePrice));
							    Double discountPrice = totalPrice-bundlePrice;
							    
							    if(discountPrice > 0)
							    {
							      bundleDetails.setDiscountPrice(decimalFormatter.format(discountPrice));
							    }
							    
							   // bundleDetailsList.add(bundleDetails);
							    BundleDetailsCache.getCache().put(bundleId, bundleDetails);  
							    
					        }					     
					       
					      }
					      
					    }						     
					    
					}
				}
				
				bundleResponse.getResponseProcessTimes().setTimeTakenToGetProductDetails(processTimes.getTimeTaken(startDatetime));
			}
			
			for (Integer bundleId : bundleIdList)
			{				
				BundleDetails bundleDetails = BundleDetailsCache.getCache().get(bundleId);				
				
								
				if (bundleDetails != null)
				{
					BundleDetailsResponseContent bundleDetailsResponseContent = new BundleDetailsResponseContent(bundleDetails);				
					
					List<ProductDetailsResponseContent> mainPageProductDetails = new ArrayList<ProductDetailsResponseContent>();
					List<ProductDetailsResponseContent> otherProductDetails = new ArrayList<ProductDetailsResponseContent>();
					
					List<ProductDetailsResponseContent> displayOrderProductDetails = new ArrayList<ProductDetailsResponseContent>();
					String currentPageCustomerProductId = "";				
					
					if(bundleResult.getAbandonedCartBundleIdList() != null && bundleResult.getAbandonedCartBundleIdList().contains(bundleId)){
						bundleDetailsResponseContent.setIsAbandonedCartBundle(true);
				    }
					
					if(bundleRequest.getPageType().equals(PRODUCT_PAGE))
				    {
						if(bundleRequest.getCustomerProductIdList() != null && !bundleRequest.getCustomerProductIdList().isEmpty())
						{
						   currentPageCustomerProductId = bundleRequest.getCustomerProductIdList().get(0);
						}				      
				     
			    	   for(ProductDetails productdetails:bundleDetails.getBundleProductItemDetails())
					   {
			    		   ProductDetailsResponseContent productResponseContent = new ProductDetailsResponseContent(productdetails);
			    		   
							if(currentPageCustomerProductId != null && currentPageCustomerProductId.length() > 0 && 
									productdetails.getProductId().equals(currentPageCustomerProductId))
							{
								//productResponseContent.setDescription(""); commented for RC templates
								mainPageProductDetails.add(productResponseContent);	
							}
							else
							{
								if(bundleDetailsList.size() <= 5)
								otherProductDetails.add(productResponseContent);
								else if(bundleDetailsList.size() > 5)
								{
									productResponseContent.setDescription("");
									otherProductDetails.add(productResponseContent);
								}
							}							
							
							if(bundleRequest.getIsSecure())
							{
								if(productResponseContent.getProductUrl() != null && !productResponseContent.getProductUrl().isEmpty())
								{
									productResponseContent.setProductUrl(productResponseContent.getProductUrl().replace("http://", "https://"));
								}								

								if(productResponseContent.getImageUrl() != null && !productResponseContent.getImageUrl().isEmpty())
								{
									productResponseContent.setImageUrl(productResponseContent.getImageUrl().replace("http://", "https://"));
								}
								
								if(productResponseContent.getOtherImageList() != null && productResponseContent.getOtherImageList().size() > 0)
								{
									List<String> securedOtherImageList = new ArrayList<String>();
									
									for(String otherImage:productResponseContent.getOtherImageList())
									{
										if(otherImage != null && !otherImage.isEmpty())
										{
											securedOtherImageList.add(otherImage.replace("http://", "https://"));
										}
									}
									
									productResponseContent.setOtherImageList(securedOtherImageList);
								}
							}
					    }
			    	   
			    	    if(mainPageProductDetails.size() > 0)
			    	    {
			    	    	displayOrderProductDetails.addAll(mainPageProductDetails);
			    	    }
			    	     
			    	    if(otherProductDetails.size() > 0)
			    	    {
			    	       displayOrderProductDetails.addAll(otherProductDetails);
			    	    }
			    	   
			    	    if(displayOrderProductDetails.size() > 0)
			    	    {
			    	    	//bundleDetails.setBundleProductItemDetails(displayOrderProductDetails);
			    	    	bundleDetailsResponseContent.setBundleProductItemDetails(displayOrderProductDetails);
			    	    }
			    	   
			    	    //bundleDetailsList.add(bundleDetails);	    
			    	    bundleDetailsList.add(bundleDetailsResponseContent);
			    	    
			    	    /*
			    	    String specificBundleList = "393,396";
			    	    if(specificBundleList.contains(currentPageCustomerProductId))
			    	    {
				    	    if(bundleDetailsList.size() >= 1)
				    	    {
				    	    	break;
				    	    }
			    	    }*/
			    	  
				     	
				    }
					else
					{	
						if(bundleRequest.getIsSecure())
						{
							for(ProductDetails productdetails:bundleDetails.getBundleProductItemDetails())
							{
					    	   ProductDetailsResponseContent productResponseContent = new ProductDetailsResponseContent(productdetails);
					    	   
					    	    
									if(productResponseContent.getProductUrl() != null && !productResponseContent.getProductUrl().isEmpty())
									{
										productResponseContent.setProductUrl(productResponseContent.getProductUrl().replace("http://", "https://"));
									}								
	
									if(productResponseContent.getImageUrl() != null && !productResponseContent.getImageUrl().isEmpty())
									{
										productResponseContent.setImageUrl(productResponseContent.getImageUrl().replace("http://", "https://"));
									}
									
									if(productResponseContent.getOtherImageList() != null && productResponseContent.getOtherImageList().size() > 0)
									{
										List<String> securedOtherImageList = new ArrayList<String>();
										
										for(String otherImage:productResponseContent.getOtherImageList())
										{
											if(otherImage != null && !otherImage.isEmpty())
											{
												securedOtherImageList.add(otherImage.replace("http://", "https://"));
											}
										}
										
										productResponseContent.setOtherImageList(securedOtherImageList);
									}								
					    	    
					    	    displayOrderProductDetails.add(productResponseContent);
					    	}
						}
						
						if(displayOrderProductDetails.size() > 0)
				    	{	
				    	   bundleDetailsResponseContent.setBundleProductItemDetails(displayOrderProductDetails);
				    	}
						
						bundleDetailsList.add(bundleDetailsResponseContent);
					}
										
				}
			}
			
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR, "getBundleDetails", "Error getting bundle details", Joiner.on(",").join(nonCachedBundleIdList).toString());
			logger.error(errorMessage, ex);
		}

		return bundleDetailsList;
	}

}
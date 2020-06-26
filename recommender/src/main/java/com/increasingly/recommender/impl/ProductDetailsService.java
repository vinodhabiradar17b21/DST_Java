package com.increasingly.recommender.impl;

import static com.increasingly.recommender.constants.Constants.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jersey.repackaged.com.google.common.base.Joiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.constants.ProductTypes;
import com.increasingly.recommender.impl.collections.ProductDetailsCache;
import com.increasingly.recommender.impl.db.MultipleProductDetails;
import com.increasingly.recommender.utils.FormatLoggerMessage;

public class ProductDetailsService
{
	private static final Logger logger = LoggerFactory.getLogger(ProductDetailsService.class.getClass());
	private DecimalFormat decimalFormatter = new DecimalFormat("#0.00");
	
	
	public List<ProductDetails> getProductDetails(List<Long> productIdList,BundleResult bundleResult)
	{
		List<ProductDetails> productDetailsList = new ArrayList<ProductDetails>();
		List<Long> nonCachedProductIdList = new ArrayList<Long>();

		try
		{
			for (long internalProductId : productIdList)
			{				
				ProductDetails productDetails = ProductDetailsCache.getCache().get(internalProductId);

				if (productDetails == null && !nonCachedProductIdList.contains(internalProductId))
				{
					nonCachedProductIdList.add(internalProductId);									
				}				
			}
				
			ProductAttributeListService productAttributeListService = new ProductAttributeListService();
			Map<Long,Map<String,ProductAttributeDetails>> multiProductAttributeList = null;
			
			if (nonCachedProductIdList.size() > 0)
			{
				ArrayList<Map<String, Object>> multipleProductDetailsMap = new ArrayList<Map<String, Object>>();
				Map<String, Object> input = new HashMap<String, Object>();			
				input.put(PRODUCT_ID_LIST, Joiner.on(",").join(nonCachedProductIdList).toString());
				
				MultipleProductDetails multipleProductDetailsListFromDB = MultipleProductDetails.getInstance();
				multipleProductDetailsMap =  multipleProductDetailsListFromDB.runService(input);
								
				
				if(multipleProductDetailsMap != null)
				{			
					if(bundleResult.getBundleConfiguration().getDecimalPrecision() == 4)
					{
						decimalFormatter = new DecimalFormat("#0.0000");
					}
					else if(bundleResult.getBundleConfiguration().getDecimalPrecision() == 3)
					{
						decimalFormatter = new DecimalFormat("#0.000");
					}
					
					OtherImageListService otherImageListService = new OtherImageListService();
					Map<Long,List<String>> productOtherImageList = otherImageListService.getProductOtherImages(input);
					
					//ProductAttributeListService productAttributeListService = new ProductAttributeListService();
					//Map<Long,Map<String,ProductAttributeDetails>> multiProductAttributeList = productAttributeListService.getProductAttributeList(input,bundleResult);
					
					for (Map<String, Object> item : multipleProductDetailsMap)
					{		
						ProductDetails productDetails = new ProductDetails();
						Long internalProductId = Long.parseLong(item.get("InternalProductId").toString());	
					 						
						productDetails.setProductId(item.get("ProductId").toString());
						productDetails.setProductName(item.get("ProductName").toString().trim());
						
						if(item.get("ImageUrl") != null)
						{
						  productDetails.setImageUrl(item.get("ImageUrl").toString());
						}
						
						productDetails.setProductUrl(item.get("ProductUrl").toString());
						
						String price = decimalFormatter.format(item.get("Price")).toString().trim();						
						productDetails.setPrice(price);
						
						if(item.get("SpecialPrice") != null)
						{
						  String specialPrice = decimalFormatter.format(item.get("SpecialPrice")).toString().trim();
						  
						  if(Double.parseDouble(specialPrice) < Double.parseDouble(price))
						  {
						    productDetails.setSpecialPrice(specialPrice);
						  }
						  else
						  {
							  productDetails.setSpecialPrice(price);
						  }
						}
						
						if(item.get("Description") != null)
						{
						 productDetails.setDescription(item.get("Description").toString().trim());
						}
						
						if(item.get("ShortDescription") != null)
						{
						 productDetails.setShortDescription(item.get("ShortDescription").toString().trim());
						}
						
						productDetails.setQuantity(Integer.parseInt(item.get("Quantity").toString()));
						
						if(item.get("Manufacturer") != null)
						{
						 productDetails.setManufacturer(item.get("Manufacturer").toString().trim());	
						}
						
						if(item.get("CategoryName") != null)
						{
						  productDetails.setCategory(item.get("CategoryName").toString());
						}
						
						if(item.get("CategoryId") != null)
						{
						  productDetails.setCategoryId(item.get("CategoryId").toString());
						}
						
						if(item.get("ProductSku") != null)
						{
						 productDetails.setProductSku(item.get("ProductSku").toString().trim());	
						}
						
						if(item.get("ProductType") != null)
						{
						 productDetails.setProductType(item.get("ProductType").toString().trim());
						}
						
						if(item.get("Color") != null)
						{
						 productDetails.setColor(item.get("Color").toString().trim());
						}
						
						if(item.get("Size") != null)
						{
						 productDetails.setSize(item.get("Size").toString().trim());
						}
						
						if(item.get("Weight") != null)
						{
						 productDetails.setWeight(decimalFormatter.format(item.get("Weight")).toString().trim());
						}
						
												
						if(item.get("HasOtherImages") != null && productOtherImageList.containsKey(internalProductId))
						{
							if(productOtherImageList.get(internalProductId).size() > 0)
							{
								productDetails.setOtherImageList(productOtherImageList.get(internalProductId));
							}
						}
						
						if(item.get("Field1") != null)
						{
							productDetails.setField1(item.get("Field1").toString().trim());
						}
						if(item.get("Field2") != null)
						{
							productDetails.setField2(item.get("Field2").toString().trim());
						}
						if(item.get("Field3") != null)
						{
							productDetails.setField3(item.get("Field3").toString().trim());
						}
						if(item.get("Field4") != null)
						{
							productDetails.setField4(item.get("Field4").toString().trim());
						}
						if(item.get("Field5") != null)
						{
							productDetails.setField5(item.get("Field5").toString().trim());
						}
						if(item.get("Field6") != null)
						{
							productDetails.setField6(item.get("Field6").toString().trim());
						}
						
						ProductDetailsCache.getCache().put(internalProductId, productDetails);
						
						/*
						if(multiProductAttributeList != null && multiProductAttributeList.containsKey(internalProductId))
						{
							if(multiProductAttributeList.get(internalProductId).size() > 0)
							{
								Map<String,ProductAttributeDetails> attributeDetails = multiProductAttributeList.get(internalProductId);
								
								List<ProductAttributeDetails> tempProductAttributeList = new ArrayList<ProductAttributeDetails>();
								
								for (Map.Entry<String, ProductAttributeDetails> tempProductAttribute : attributeDetails.entrySet())
								{
									tempProductAttributeList.add(tempProductAttribute.getValue());
								}								
								productDetails.setProductAttributeDeatilsList(tempProductAttributeList);
								
								if(tempProductAttributeList != null && tempProductAttributeList.size() > 0)
								{
									for(ProductAttributeDetails itemAttributeDetails:tempProductAttributeList)
									{
										if(itemAttributeDetails != null)
										{
											List<ProductAttributeValues> productAttributeValuesList = itemAttributeDetails.getAttributeValues();
											
											if(productAttributeValuesList != null)
											{
												for(ProductAttributeValues attributeValues : productAttributeValuesList)
												{
													if(attributeValues != null)
													{
														if(attributeValues.getPricingIsPercent() != null && attributeValues.getPricingIsPercent() == 1 && attributeValues.getPricingValue() != null
																&& !attributeValues.getPricingValue().isEmpty())
														{
															double pricingValue = Double.parseDouble(attributeValues.getPricingValue());
															double specialPricingValue = Double.parseDouble(attributeValues.getSpecialPricingValue());
															
															if(pricingValue > 0)
															{	
																if(productDetails.getPrice() != null && !productDetails.getPrice().isEmpty()
																		&& Double.parseDouble(productDetails.getPrice()) > 0)
																{													 
																	pricingValue = ((Double.parseDouble(productDetails.getPrice())/100) * pricingValue);		
																	attributeValues.setPricingValue(decimalFormatter.format(pricingValue));														
																}
															}
															if(specialPricingValue > 0)
															{
																
																if(productDetails.getSpecialPrice() != null && !productDetails.getSpecialPrice().isEmpty()
																		&& Double.parseDouble(productDetails.getSpecialPrice()) > 0)
																{													  
																   specialPricingValue = ((Double.parseDouble(productDetails.getSpecialPrice())/100) * specialPricingValue);	
																   attributeValues.setSpecialPricingValue(decimalFormatter.format(specialPricingValue));													
																}
																
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}*/
						
						
						/*
						if(productDetails.getProductType().equals(ProductTypes.PRODUCT_TYPE_CONFIGURABLE) && productDetails.getProductAttributeDeatilsList() != null &&
								productDetails.getProductAttributeDeatilsList().size() > 0)
						{
							ProductDetailsCache.getCache().put(internalProductId, productDetails); 
						}
						else if(productDetails.getProductType().equals(ProductTypes.PRODUCT_TYPE_SIMPLE))
						{
							ProductDetailsCache.getCache().put(internalProductId, productDetails); 
							
						}*/
					                  
					}
					
				}
			}	
			
			Map<String, Object> inputConfigurableProductList = new HashMap<String, Object>();			
			List<Long> configurableProductIdList = new ArrayList<Long>();
			
			for (long internalProductId : productIdList)
			{				
				ProductDetails productDetails = ProductDetailsCache.getCache().get(internalProductId);

				if (productDetails != null)
				{							
					if(productDetails.getProductType().equalsIgnoreCase(ProductTypes.PRODUCT_TYPE_CONFIGURABLE) && !configurableProductIdList.contains(internalProductId))
					{
						configurableProductIdList.add(internalProductId);
					}														
				}				
			}						
			
			if(configurableProductIdList.size() > 0)
			{			  
			  multiProductAttributeList = productAttributeListService.getProductAttributeList(configurableProductIdList,bundleResult);
			}
			
			for (long internalProductId : productIdList)
			{				
				ProductDetails productDetails = ProductDetailsCache.getCache().get(internalProductId);

				if (productDetails != null)
				{
					if(multiProductAttributeList != null && multiProductAttributeList.containsKey(internalProductId))
					{
						if(multiProductAttributeList.get(internalProductId).size() > 0)
						{
							Map<String,ProductAttributeDetails> attributeDetails = multiProductAttributeList.get(internalProductId);
							
							List<ProductAttributeDetails> tempProductAttributeList = new ArrayList<ProductAttributeDetails>();
							
							if(attributeDetails != null && attributeDetails.size() > 0)
							{
								for (Map.Entry<String, ProductAttributeDetails> tempProductAttribute : attributeDetails.entrySet())
								{
									tempProductAttributeList.add(tempProductAttribute.getValue());
								}								
								productDetails.setProductAttributeDeatilsList(tempProductAttributeList);
							}
							
							if(tempProductAttributeList != null && tempProductAttributeList.size() > 0)
							{
								double parentProductPrice = 0;								
								double configProductPrice = 0;
								double configSpecialProductPrice = 0;
								//double lowestProductPrice = 0;
								
								if(productDetails.getPrice() != null && !productDetails.getPrice().isEmpty())
								{
									parentProductPrice = Double.parseDouble(productDetails.getPrice());
								}
								
								for(ProductAttributeDetails itemAttributeDetails:tempProductAttributeList)
								{
									if(itemAttributeDetails != null)
									{
										List<ProductAttributeValues> productAttributeValuesList = itemAttributeDetails.getAttributeValues();
										
										if(productAttributeValuesList != null)
										{
											for(ProductAttributeValues attributeValues : productAttributeValuesList)
											{
												if(attributeValues != null)
												{
													if(attributeValues.getPricingIsPercent() != null && attributeValues.getPricingIsPercent() == 1 && attributeValues.getPricingValue() != null
															&& !attributeValues.getPricingValue().isEmpty())
													{
														double pricingValue = Double.parseDouble(attributeValues.getPricingValue());
														double specialPricingValue = Double.parseDouble(attributeValues.getSpecialPricingValue());
														
														if(pricingValue > 0)
														{	
															if(productDetails.getPrice() != null && !productDetails.getPrice().isEmpty()
																	&& Double.parseDouble(productDetails.getPrice()) > 0)
															{													 
																pricingValue = ((Double.parseDouble(productDetails.getPrice())/100) * pricingValue);		
																attributeValues.setPricingValue(decimalFormatter.format(pricingValue));														
															}
														}
														if(specialPricingValue > 0)
														{
															
															if(productDetails.getSpecialPrice() != null && !productDetails.getSpecialPrice().isEmpty()
																	&& Double.parseDouble(productDetails.getSpecialPrice()) > 0)
															{													  
															   specialPricingValue = ((Double.parseDouble(productDetails.getSpecialPrice())/100) * specialPricingValue);	
															   attributeValues.setSpecialPricingValue(decimalFormatter.format(specialPricingValue));													
															}
															
														}
													}
													
													if(parentProductPrice <= 0)
													{
														double childProductPrice = 0;
														double childProductSpecialPrice = 0;
														
														if(attributeValues.getChildProductPrice() != null && !attributeValues.getChildProductPrice().isEmpty())
														{
															childProductPrice = Double.parseDouble(attributeValues.getChildProductPrice());
														}
														
														if(attributeValues.getChildProductSpecialPrice() != null && !attributeValues.getChildProductSpecialPrice().isEmpty())
														{
															childProductSpecialPrice = Double.parseDouble(attributeValues.getChildProductSpecialPrice());
														}													
													
														
														if((configProductPrice == 0 && configSpecialProductPrice == 0) || 
														   (childProductSpecialPrice > 0 && ((childProductSpecialPrice < configSpecialProductPrice) || (configSpecialProductPrice == 0 && childProductSpecialPrice < configProductPrice)))
															|| (childProductSpecialPrice == 0 && ((childProductPrice < configSpecialProductPrice) || (configSpecialProductPrice == 0 && childProductPrice < configProductPrice))))															
														{
															configProductPrice = childProductPrice;
															configSpecialProductPrice = childProductSpecialPrice;															
														}
														
														/*if(lowestProductPrice == 0 || (childProductPrice < lowestProductPrice))
														{
															lowestProductPrice = childProductPrice;
														}*/
													}
												}
											}
										}
									}
								}
								
								/*
								if(configSpecialProductPrice > 0 && lowestProductPrice > 0 && lowestProductPrice > configSpecialProductPrice && lowestProductPrice < configProductPrice)
								{
									configProductPrice = lowestProductPrice;
								}*/
								
								if(configProductPrice > 0)
								{									
								  productDetails.setPrice(decimalFormatter.format(configProductPrice).toString());
								  productDetails.setSpecialPrice(decimalFormatter.format(configSpecialProductPrice).toString());
								}
							}
						}
					}
					
					if(productDetails.getProductType().equalsIgnoreCase(ProductTypes.PRODUCT_TYPE_CONFIGURABLE))
					{
						if(productDetails.getProductAttributeDeatilsList() != null && productDetails.getProductAttributeDeatilsList().size() > 0)
						{							
							productDetailsList.add(productDetails);	
						}						
					}
					else if(productDetails.getProductType().equalsIgnoreCase(ProductTypes.PRODUCT_TYPE_SIMPLE))
					{
						productDetailsList.add(productDetails); 						
					}
					
												
				}				
			}			
			
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR, "getProductDetails", "Error getting product details", Joiner.on(",").join(nonCachedProductIdList).toString());
			logger.error(errorMessage, ex);
		}

		return productDetailsList;
	}
}
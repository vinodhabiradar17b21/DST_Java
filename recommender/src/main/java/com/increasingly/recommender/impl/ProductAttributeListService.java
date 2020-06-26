package com.increasingly.recommender.impl;

import static com.increasingly.recommender.constants.Constants.LOG_ERROR;
import static com.increasingly.recommender.constants.Constants.PRODUCT_ID_LIST;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jersey.repackaged.com.google.common.base.Joiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.impl.collections.ProductAttributesListCache;
import com.increasingly.recommender.impl.collections.ProductDetailsCache;
import com.increasingly.recommender.impl.db.ProductAttributeList;
import com.increasingly.recommender.utils.FormatLoggerMessage;

public class ProductAttributeListService
{
	private static final Logger logger = LoggerFactory.getLogger(ProductAttributeListService.class.getClass());
	private DecimalFormat decimalFormatter = new DecimalFormat("#0.00");
	
	public Map<Long,Map<String,ProductAttributeDetails>> getProductAttributeList(List<Long> configurableProductIdList,BundleResult bundleResult)
	{
		Map<Long,Map<String,ProductAttributeDetails>> productAttributeDetailsList = new HashMap<Long,Map<String,ProductAttributeDetails>>();
		Map<Long,Map<String,ProductAttributeDetails>> tempProductAttributeDetailsList = new HashMap<Long,Map<String,ProductAttributeDetails>>();
		List<Long> nonCachedProductIdList = new ArrayList<Long>();
		
		try
		{			
			for (long internalProductId : configurableProductIdList)
			{				
				Map<String,ProductAttributeDetails> productAttributeInfoDetails = ProductAttributesListCache.getCache().get(internalProductId);

				if (productAttributeInfoDetails == null && !nonCachedProductIdList.contains(internalProductId))
				{
					nonCachedProductIdList.add(internalProductId);									
				}				
			}
			
			if(nonCachedProductIdList.size() > 0)
			{
				if(bundleResult.getBundleConfiguration().getDecimalPrecision() == 4)
				{
					decimalFormatter = new DecimalFormat("#0.0000");
				}
				else if(bundleResult.getBundleConfiguration().getDecimalPrecision() == 3)
				{
					decimalFormatter = new DecimalFormat("#0.000");
				}
				
				Map<String, Object> input = new HashMap<String, Object>();	
				input.put(PRODUCT_ID_LIST, Joiner.on(",").join(nonCachedProductIdList).toString());
				
				ProductAttributeList productAttributeList = ProductAttributeList.getInstance();
				ArrayList<Map<String, Object>> productAttributeListFromDb = productAttributeList.runService(input);
				
				for(Map<String,Object> item:productAttributeListFromDb)
				{
					long productId = (Long)item.get("internal_parent_product_id");
					String attributeCode = (String)item.get("attribute_code");
					String attributeId = (String)item.get("attribute_id");
					String attributeLabel = (String)item.get("attribute_label");
					
					String childProductId = (String)item.get("child_product_id");
					String childProductSku = (String)item.get("child_product_sku");
					String childProductName = (String)item.get("child_product_name");
					String childProductImageUrl = (String)item.get("child_product_image_url");
					String childProductUrl = (String)item.get("child_product_url");
					String childProductDescription = (String)item.get("child_product_description");
					
					String childProductPrice = null;
					if(item.get("child_product_price") != null)
					{
						childProductPrice = decimalFormatter.format(item.get("child_product_price")).toString().trim();
					}
					
					String childProductSpecialPrice = null;
					if(item.get("child_product_special_price") != null)
					{
						childProductSpecialPrice = decimalFormatter.format(item.get("child_product_special_price")).toString().trim();
					}
					
					String optionId = (String)item.get("option_id");
					String optionText = (String)item.get("option_text");
					String optionImageUrl = (String)item.get("option_image_url");
					
					String pricingValue = null;
					if(item.get("pricing_value") != null)
					{
					  pricingValue = decimalFormatter.format(item.get("pricing_value")).toString().trim();
					}
					
					Integer pricingIsPercent = (Integer)item.get("pricing_is_percent");				
					String colorCode = (String)item.get("color_code");
					
					ProductAttributeValues productAttributeValues = new ProductAttributeValues();
					productAttributeValues.setChildProductId(childProductId);
					productAttributeValues.setChildProductSku(childProductSku);
					productAttributeValues.setChildProductName(childProductName);
					productAttributeValues.setChildProductImageUrl(childProductImageUrl);
					productAttributeValues.setChildProductUrl(childProductUrl);
					productAttributeValues.setChildProductDescription(childProductDescription);
					productAttributeValues.setChildProductPrice(childProductPrice);
					productAttributeValues.setChildProductSpecialPrice(childProductSpecialPrice);
					
					productAttributeValues.setOptionId(optionId);
					productAttributeValues.setOptionText(optionText);
					productAttributeValues.setOptionImageUrl(optionImageUrl);
					productAttributeValues.setPricingValue(pricingValue);
					productAttributeValues.setSpecialPricingValue(pricingValue);
					productAttributeValues.setPricingIsPercent(pricingIsPercent);
					productAttributeValues.setColorCode(colorCode);
									
					if(tempProductAttributeDetailsList.containsKey(productId))
					{
						Map<String,ProductAttributeDetails> productAttributeInfo = tempProductAttributeDetailsList.get(productId);
						
						if(productAttributeInfo.containsKey(attributeId))
						{
							ProductAttributeDetails productAttributeDetails = productAttributeInfo.get(attributeId);
							productAttributeDetails.setAttributeValues(productAttributeValues);		
							tempProductAttributeDetailsList.put(productId, productAttributeInfo);
						}
						else
						{						
							ProductAttributeDetails productAttributeDetails = new ProductAttributeDetails();
							productAttributeDetails.setAttributeId(attributeId);
							productAttributeDetails.setAttributeCode(attributeCode);
							productAttributeDetails.setFrontEndLabel(attributeLabel);						
							productAttributeDetails.setAttributeValues(productAttributeValues);
							productAttributeInfo.put(attributeId, productAttributeDetails);	
							tempProductAttributeDetailsList.put(productId, productAttributeInfo);
						}
						
					}
					else
					{
						Map<String,ProductAttributeDetails> productAttributeInfo = new HashMap<String,ProductAttributeDetails>();
						
						if(productAttributeInfo.containsKey(attributeId))
						{
							ProductAttributeDetails productAttributeDetails = productAttributeInfo.get(attributeId);
							productAttributeDetails.setAttributeValues(productAttributeValues);		
							tempProductAttributeDetailsList.put(productId, productAttributeInfo);	
						}
						else
						{						
							ProductAttributeDetails productAttributeDetails = new ProductAttributeDetails();
							productAttributeDetails.setAttributeId(attributeId);
							productAttributeDetails.setAttributeCode(attributeCode);
							productAttributeDetails.setFrontEndLabel(attributeLabel);						
							productAttributeDetails.setAttributeValues(productAttributeValues);
							productAttributeInfo.put(attributeId, productAttributeDetails);	
							tempProductAttributeDetailsList.put(productId, productAttributeInfo);	
						}									
						
					}
				}
				
				for (long internalProductId : nonCachedProductIdList)
				{
					if (tempProductAttributeDetailsList.containsKey(internalProductId))
					{
						ProductAttributesListCache.getCache().put(internalProductId, tempProductAttributeDetailsList.get(internalProductId));
					}
				}
				
			}
			
			for (long internalProductId : configurableProductIdList)
			{				
				Map<String,ProductAttributeDetails> productAttributeInfoDetails = ProductAttributesListCache.getCache().get(internalProductId);

				if (productAttributeInfoDetails != null && !productAttributeDetailsList.containsKey(internalProductId))
				{
					productAttributeDetailsList.put(internalProductId, productAttributeInfoDetails);													
				}				
			}
			
			
		}
		catch(Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR, "getProductAttributeList", "Error getting product attribute list", "");
			logger.error(errorMessage, ex);
		}
		
		return productAttributeDetailsList;
		
	}
}
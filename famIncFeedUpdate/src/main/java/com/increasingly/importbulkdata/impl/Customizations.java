package com.increasingly.importbulkdata.impl;

import static com.increasingly.importbulkdata.util.Constants.ATTRIBUTE_CODE;
import static com.increasingly.importbulkdata.util.Constants.ATTRIBUTE_ID;
import static com.increasingly.importbulkdata.util.Constants.ATTRIBUTE_LABEL;
import static com.increasingly.importbulkdata.util.Constants.CHILD_PRODUCT_ID;
import static com.increasingly.importbulkdata.util.Constants.CLIENT_PRODUCT_STATUS;
import static com.increasingly.importbulkdata.util.Constants.IS_PERCENT;
import static com.increasingly.importbulkdata.util.Constants.LOG_ERROR;
import static com.increasingly.importbulkdata.util.Constants.OPTION_ID;
import static com.increasingly.importbulkdata.util.Constants.OPTION_TEXT;
import static com.increasingly.importbulkdata.util.Constants.PRICING_VALUE;
import static com.increasingly.importbulkdata.util.Constants.PRODUCT_OPTIONS;
import static com.increasingly.importbulkdata.util.Constants.SPECIAL_PRICE;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.importbulkdata.util.FormatLoggerMessage;

public class Customizations
{
	private static final Logger logger = LoggerFactory.getLogger(Customizations.class.getClass());
	
	public ArrayList<LinkedHashMap<String, Object>> customize7FAMFeed(ArrayList<LinkedHashMap<String, Object>> productData,LinkedHashMap<String, String> fieldMappingDetails) 
	{	
		try
		{
			
			for(Map<String,Object> item : productData)
	        {	
				ArrayList<LinkedHashMap<String,Object>> tempProductOptions = new ArrayList<LinkedHashMap<String,Object>>();
				
				if(item.get(fieldMappingDetails.get(CLIENT_PRODUCT_STATUS)) != null && !item.get(fieldMappingDetails.get(CLIENT_PRODUCT_STATUS)).toString().isEmpty())
	        	{
					String productStatus = item.get(fieldMappingDetails.get(CLIENT_PRODUCT_STATUS)).toString().trim();
								
					if(productStatus.equals("Enabled"))
						item.put(fieldMappingDetails.get(CLIENT_PRODUCT_STATUS), 1);
					else
						item.put(fieldMappingDetails.get(CLIENT_PRODUCT_STATUS), 0);
					
	        	}
				
				if(item.get(fieldMappingDetails.get(PRODUCT_OPTIONS)) != null && !item.get(fieldMappingDetails.get(PRODUCT_OPTIONS)).toString().trim().isEmpty())
	        	{	
					String[] productAttributeOptionsValue = item.get(fieldMappingDetails.get(PRODUCT_OPTIONS)).toString().trim().split("\\|");
					
					for(String productAttributeOption:productAttributeOptionsValue)
					{
						LinkedHashMap<String,Object> productOption = new LinkedHashMap<String,Object>();
						
						try
						{
							String[] attributeOptionDetails = productAttributeOption.split(",");
							
							if(attributeOptionDetails[0] != null && !attributeOptionDetails[0].trim().isEmpty())
							{
								productOption.put(CHILD_PRODUCT_ID, attributeOptionDetails[0].trim());
							}
							
							if(attributeOptionDetails[1] != null && !attributeOptionDetails[1].trim().isEmpty())
							{
								productOption.put(ATTRIBUTE_CODE, attributeOptionDetails[1]);
							}
							
							if(attributeOptionDetails[2] != null && !attributeOptionDetails[2].trim().isEmpty())
							{
								productOption.put(ATTRIBUTE_ID, attributeOptionDetails[2]);
							}
							
							if(attributeOptionDetails[3] != null && !attributeOptionDetails[3].trim().isEmpty())
							{
								productOption.put(ATTRIBUTE_LABEL, attributeOptionDetails[3]);
							}
							
							if(attributeOptionDetails[4] != null && !attributeOptionDetails[4].trim().isEmpty())
							{
								productOption.put(OPTION_ID, attributeOptionDetails[4]);
							}
							
							if(attributeOptionDetails[5] != null && !attributeOptionDetails[5].trim().isEmpty())
							{
								productOption.put(OPTION_TEXT, attributeOptionDetails[5]);
							}
							
							if(attributeOptionDetails[6] != null && !attributeOptionDetails[6].trim().isEmpty())
							{
								productOption.put(IS_PERCENT, attributeOptionDetails[6]);
							}
							
							if(attributeOptionDetails.length > 7 && attributeOptionDetails[7] != null)
							{
								if(!attributeOptionDetails[7].trim().isEmpty())
								{
									productOption.put(PRICING_VALUE, attributeOptionDetails[7]);
								}
							}
						}
						catch(Exception ex)
						{
							String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "Customizations" , "customize7FAMFeed", "Error occured while parsing attribute options","");
							logger.error(errorMessage,ex);
						}
						
						tempProductOptions.add(productOption);			
					}
					
									
	        	}
				item.put(fieldMappingDetails.get(PRODUCT_OPTIONS), tempProductOptions);	
				
				if(item.get(" Discounted Price") != null && !item.get(" Discounted Price").toString().isEmpty())
                {
                    double discountedPrice = Double.valueOf(item.get(" Discounted Price").toString());
                    
                    if(discountedPrice > 0)
                    {
                        item.put(fieldMappingDetails.get(SPECIAL_PRICE), item.get(" Discounted Price"));
                    }
                }
	        }
		
		}
		catch(Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "Customizations" , "customize7FAMFeed", "Error occured while customizing feed","");
			logger.error(errorMessage,ex);
		}
		
		return productData;
	}
}
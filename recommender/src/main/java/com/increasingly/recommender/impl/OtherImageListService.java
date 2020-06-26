package com.increasingly.recommender.impl;

import static com.increasingly.recommender.constants.Constants.LOG_ERROR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.impl.db.ProductOtherImageList;
import com.increasingly.recommender.utils.FormatLoggerMessage;

public class OtherImageListService
{
	private static final Logger logger = LoggerFactory.getLogger(OtherImageListService.class.getClass());
	
	public Map<Long,List<String>> getProductOtherImages(Map<String, Object> input)
	{
		Map<Long,List<String>> productImageList = new HashMap<Long,List<String>>();
	
		try
		{
			ProductOtherImageList productOtherImageList = ProductOtherImageList.getInstance();
			ArrayList<Map<String, Object>> productOtherImagesFromDb = productOtherImageList.runService(input);
			
			for(Map<String,Object> item:productOtherImagesFromDb)
			{
				long productId = (Long)item.get("internal_product_id");
				String imageUrl = (String)item.get("image_url");
				
				if(productImageList.containsKey(productId))
				{
					List<String> otherImages = productImageList.get(productId);
					otherImages.add(imageUrl);
					productImageList.put(productId, otherImages);				
				}
				else
				{
					List<String> otherImages = new ArrayList<String>();
					otherImages.add(imageUrl);
					productImageList.put(productId, otherImages);
				}
			}
		}
		catch(Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR, "getProductOtherImages", "Error getting product more images", "");
			logger.error(errorMessage, ex);
		}
		
		return productImageList;
		
	}
}
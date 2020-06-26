package com.increasingly.recommender.impl;

import static com.increasingly.recommender.constants.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jersey.repackaged.com.google.common.base.Joiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.impl.collections.CategoryIdCache;
import com.increasingly.recommender.impl.collections.ProductIdCache;
import com.increasingly.recommender.impl.collections.RequestCountForCategoryCache;
import com.increasingly.recommender.impl.collections.RequestCountForProductCache;
import com.increasingly.recommender.impl.db.InternalCategoryIdList;
import com.increasingly.recommender.utils.FormatLoggerMessage;
import com.increasingly.recommender.utils.GeneralUtils;

public class CategoryIdService 
{
	private static final Logger logger = LoggerFactory.getLogger(CategoryIdService.class.getClass());

	/**
	 * @param customerCategoryId
	 * @param bundleRequest
	 * @param bundleResult
	 * @return
	 */
	public void getCachedInternalCategoryId(List<String> customerCategoryIdList,BundleRequest bundleRequest,BundleResult bundleResult)
	{
		List<String> nonCachedCategoryIdList = new ArrayList<String>();
		List<Integer> internalCategoryIdList = new ArrayList<Integer>();
						
		try
		{		
			for(String customerCategoryId :customerCategoryIdList)
			{
				String key = GeneralUtils.calculateCategoryKey(bundleResult.getFeedId(), customerCategoryId);
				Integer internalCategoryId = CategoryIdCache.getCache().get(key);

				if(internalCategoryId != null && internalCategoryId > 0)
				{
					internalCategoryIdList.add(internalCategoryId);					
				}
				else
				{
					nonCachedCategoryIdList.add(customerCategoryId);
				}
			}
			
			if (nonCachedCategoryIdList.size() > 0)			 
			{					
				Map<String, Object> input = new HashMap<String, Object>();
				input.put(CATEGORY_ID_LIST, Joiner.on(",").join(nonCachedCategoryIdList).toString());
				input.put(FEED_ID, bundleResult.getFeedId());
				
				ArrayList<LinkedHashMap<String, Object>> tempInternalCategoryIdList = new ArrayList<LinkedHashMap<String, Object>>();
				InternalCategoryIdList getInternalCategoryIdListFromDB = InternalCategoryIdList.getInstance();
				tempInternalCategoryIdList =  getInternalCategoryIdListFromDB.runService(input);
				
				if(tempInternalCategoryIdList != null)
				{
					for (LinkedHashMap<String, Object> field : tempInternalCategoryIdList)
					{
						String customerCategoryId = field.get("CustomerCategoryID").toString();
						Integer internalCategoryId = Integer.parseInt(field.get("InternalCategoryID").toString());
						int impressionCount = Integer.parseInt(field.get("CategoryImpressionCount").toString());

						String key = GeneralUtils.calculateCategoryKey(bundleResult.getFeedId(), customerCategoryId);
						CategoryIdCache.getCache().put(key, internalCategoryId);
						internalCategoryIdList.add(internalCategoryId);
						
						Integer currentCategoryImpressionCount = 0;
						
						if(RequestCountForCategoryCache.getCache().get(internalCategoryId) != null)
						{
							List<Integer> categoryImpressionDetails = RequestCountForCategoryCache.getCache().get(internalCategoryId);
							
							if(categoryImpressionDetails.size() > 1)
							{
								currentCategoryImpressionCount = categoryImpressionDetails.get(1);
							}
							
						}
						
						List<Integer> requestImpressionCountCache = new ArrayList<Integer>();
						requestImpressionCountCache.add(impressionCount);
						requestImpressionCountCache.add(currentCategoryImpressionCount);
						RequestCountForCategoryCache.getCache().put(internalCategoryId,requestImpressionCountCache);

					}				
				}
			}
			
			if(internalCategoryIdList.size() > 0)
			{
				bundleResult.setInternalCategoryList(internalCategoryIdList);
			}
				
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR ,"getCachedInternalCategoryId" , "Error getting internal category id","");
			logger.error(errorMessage, ex);
		}		
		
	}
}
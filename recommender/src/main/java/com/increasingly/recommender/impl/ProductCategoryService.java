package com.increasingly.recommender.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.increasingly.recommender.impl.collections.ProductCategoryListCache;
import com.increasingly.recommender.utils.FormatLoggerMessage;
import static com.increasingly.recommender.constants.Constants.*;


public class ProductCategoryService
{
	private static final Logger logger = LoggerFactory.getLogger(ProductCategoryService.class.getClass());
	
	public void getProductCategoryList(List<Long> internalProductIdList,BundleRequest bundleRequest, BundleResult bundleResult)
	{		
		List<Integer> internalCategoryList = new ArrayList<Integer>();		
				
		try
		{
			internalCategoryList = ProductCategoryListCache.getCache().getProductCategoryList(internalProductIdList, bundleResult.getFeedId());
			
			if(internalCategoryList.size() > 0)
			{
			  bundleResult.setInternalCategoryList(internalCategoryList);
			}
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "getProductCategoryList" , "Error getting product category info","");
			logger.error(errorMessage, ex);
		}		
		
	}
	
}
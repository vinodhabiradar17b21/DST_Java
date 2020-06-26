package com.increasingly.recommender.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jersey.repackaged.com.google.common.base.Joiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.impl.collections.ProductIdCache;
import com.increasingly.recommender.impl.collections.RequestCountForCartBundleCache;
import com.increasingly.recommender.impl.collections.RequestCountForCartProductCache;
import com.increasingly.recommender.impl.collections.RequestCountForProductCache;
import com.increasingly.recommender.impl.db.InternalProductIdList;
import com.increasingly.recommender.impl.db.InternalProductIdListBySKU;
import com.increasingly.recommender.utils.FormatLoggerMessage;
import com.increasingly.recommender.utils.GeneralUtils;

import static com.increasingly.recommender.constants.Constants.*;

public class ProductIdService
{
	private static final Logger logger = LoggerFactory.getLogger(ProductIdService.class.getClass());

		
	
	public void getCachedInternalProductIdList(List<String> customerProductIdList, BundleRequest bundleRequest,BundleResult bundleResult)
	{
		List<Long> returnInternalProductIdList = new ArrayList<Long>();
		List<String> nonCachedproductIdList = new ArrayList<String>();	
						
		try
		{			
			for (String customerProductId : customerProductIdList)
			{				
				String key = GeneralUtils.calculateProductKey(bundleResult.getFeedId(), customerProductId);

				Long productId = ProductIdCache.getCache().get(key);

				if (productId != null && productId > 0)
				{
					returnInternalProductIdList.add(productId);					
				}
				else
				{
					nonCachedproductIdList.add(customerProductId);				
				}
			}
		
			if (nonCachedproductIdList.size() > 0)
			{
				List<LinkedHashMap<String, Object>> productIdAndKeyList = new ArrayList<LinkedHashMap<String, Object>>();
								
				Map<String, Object> input = new HashMap<String, Object>();
				input.put(FEED_ID, bundleResult.getFeedId());
				input.put(PRODUCT_ID_LIST, Joiner.on(",").join(nonCachedproductIdList).toString());
							
				if(!bundleRequest.getIsPsku().isEmpty() && bundleRequest.getIsPsku().equalsIgnoreCase("1"))
				{
					InternalProductIdListBySKU getInternalProductIdListBySKUFromDB = InternalProductIdListBySKU.getInstance();
					productIdAndKeyList =  getInternalProductIdListBySKUFromDB.runService(input);
				}
				else{
					InternalProductIdList getInternalProductIdListFromDB = InternalProductIdList.getInstance();
					productIdAndKeyList =  getInternalProductIdListFromDB.runService(input);
				}
				if(productIdAndKeyList != null)
				{
					for (LinkedHashMap<String, Object> field : productIdAndKeyList)
					{
						String customerProductId = field.get("CustomerProductID").toString();
						long internalProductId = Long.parseLong(field.get("InternalProductID").toString());
						int productImpressionCount = Integer.parseInt(field.get("ProductImpressionCount").toString());
						String key = GeneralUtils.calculateProductKey(bundleResult.getFeedId(), customerProductId);
						ProductIdCache.getCache().put(key, internalProductId);
						returnInternalProductIdList.add(internalProductId);	
						
						Integer currentProductImpressionCount = 0;
						Integer currentCartProductImpressionCount = 0;
						
						if(RequestCountForProductCache.getCache().get(internalProductId) != null)
						{
							List<Integer> productImpressionDetails = RequestCountForProductCache.getCache().get(internalProductId);
							
							if(productImpressionDetails.size() > 1)
							{
								currentProductImpressionCount = productImpressionDetails.get(1);
							}
							
						}
						
						if(RequestCountForCartProductCache.getCache().get(internalProductId) != null)
						{
							List<Integer> cartProductImpressionDetails = RequestCountForCartProductCache.getCache().get(internalProductId);
							
							if(cartProductImpressionDetails.size() > 1)
							{
								currentCartProductImpressionCount = cartProductImpressionDetails.get(1);
							}
							
						}
						
						// Cache Product Impressions
						List<Integer> requestProductImpressionCountCache = new ArrayList<Integer>();
						requestProductImpressionCountCache.add(productImpressionCount);
						requestProductImpressionCountCache.add(currentProductImpressionCount);
						RequestCountForProductCache.getCache().put(internalProductId,requestProductImpressionCountCache);
						
						// Cache Cart Impressions		
						List<Integer> requestCartProductImpressionCountCache = new ArrayList<Integer>();
						int cartImpressionCount = Integer.parseInt(field.get("CartImpressionCount").toString());
						requestCartProductImpressionCountCache.add(currentCartProductImpressionCount);
						requestCartProductImpressionCountCache.add(cartImpressionCount);
						RequestCountForCartProductCache.getCache().put(internalProductId,requestCartProductImpressionCountCache);
					}				
				}
				
			}
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR ,"getCachedInternalProductIdList" , "Error getting internal product id list" ,"");
			logger.error(errorMessage, ex);
		}
		
		bundleResult.setInternalProductIdList(returnInternalProductIdList);
	}
}
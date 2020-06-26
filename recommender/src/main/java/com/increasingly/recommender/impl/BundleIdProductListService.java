package com.increasingly.recommender.impl;

import static com.increasingly.recommender.constants.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.increasingly.recommender.impl.collections.BundleIdProductListCache;
import com.increasingly.recommender.impl.db.BundleProductItemList;

import jersey.repackaged.com.google.common.base.Joiner;


public class BundleIdProductListService
{
	public void getBundleIdProductList(List<Integer> bundleIdList) throws Exception
	{			
		List<Integer> nonCachedBundleIdList = new ArrayList<Integer>();
		
		for(Integer bundleId : bundleIdList)
		{
			List<Long> productIdList = BundleIdProductListCache.getCache().get(bundleId);
			if(productIdList == null || productIdList.size() == 0)
			{
				nonCachedBundleIdList.add(bundleId);
			}
		}
		
		if (nonCachedBundleIdList != null && nonCachedBundleIdList.size() > 0)
		{
			ArrayList<Map<String, Object>> bundleProductListFromDb = new ArrayList<Map<String, Object>>();
			Map<String, Object> input = new HashMap<String, Object>();
			input.put(BUNDLE_ID_LIST, Joiner.on(",").join(nonCachedBundleIdList).toString());
			
			BundleProductItemList bundleProductItemListFromDB = BundleProductItemList.getInstance();
			bundleProductListFromDb = bundleProductItemListFromDB.runService(input);					
			
			Map<Integer, List<Long>> tmpBundleProductList = new HashMap<Integer, List<Long>>();

			if (bundleProductListFromDb != null)
			{
				for (Map<String, Object> item : bundleProductListFromDb)
				{
					Integer bundleId = Integer.parseInt(item.get("BundleID").toString());
					Long internalProductId = Long.parseLong(item.get("ProductID").toString());
					
					if (tmpBundleProductList.containsKey(bundleId))
					{
						List<Long> tmpProductIdList = tmpBundleProductList.get(bundleId);
						tmpProductIdList.add(internalProductId);
						tmpBundleProductList.put(bundleId, tmpProductIdList);
					}
					else
					{
						List<Long> tmpProductIdList = new ArrayList<Long>();
						tmpProductIdList.add(internalProductId);
						tmpBundleProductList.put(bundleId, tmpProductIdList);
					}
				}								
				
				if (tmpBundleProductList.size() > 0)
				{
					for (Integer bundleId : nonCachedBundleIdList)
					{
						if (tmpBundleProductList.containsKey(bundleId))
						{	
							BundleIdProductListCache.getCache().put(bundleId, tmpBundleProductList.get(bundleId));
						}
					}
				}				
				
			}			
		}	
				
	}
}
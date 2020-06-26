package com.increasingly.recommender.impl;

import static com.increasingly.recommender.constants.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jersey.repackaged.com.google.common.base.Joiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.impl.collections.BundleLinkProductInfoCache;
import com.increasingly.recommender.impl.db.BundleLinkProductInfoList;
import com.increasingly.recommender.utils.FormatLoggerMessage;

public class BundleLinkProductInfoService
{
	private static final Logger logger = LoggerFactory.getLogger(ProductDetailsService.class.getClass());
	
	public Map<Integer,List<BundleLinkProductInfo>> getBundleLinkProductInfoList(List<Integer> bundleIdList)
	{		
		Map<Integer,List<BundleLinkProductInfo>> multipleBundleLinkProductInfoList = new HashMap<Integer,List<BundleLinkProductInfo>>();
		
		List<Integer> nonCachedBundleIdList = new ArrayList<Integer>();
		
		try
		{
			for (Integer bundleId : bundleIdList)
			{				
				List<BundleLinkProductInfo> bundleLinkProductInfoList = BundleLinkProductInfoCache.getCache().get(bundleId);
	
				if (bundleLinkProductInfoList == null || bundleLinkProductInfoList.size() == 0)
				{
					nonCachedBundleIdList.add(bundleId);			
				}				
			}
			
			Map<String, Object> input = new HashMap<String, Object>();				
			input.put(BUNDLE_ID_LIST, Joiner.on(",").join(nonCachedBundleIdList).toString());
			
			ArrayList<Map<String, Object>> bundleLinkProductInfoListFromDb = new ArrayList<Map<String, Object>>();
			
			BundleLinkProductInfoList bundleLinkProductInfoListDBObj = BundleLinkProductInfoList.getInstance();
			bundleLinkProductInfoListFromDb = bundleLinkProductInfoListDBObj.runService(input);
			
			Map<Integer, List<BundleLinkProductInfo>> tmpBundleProductInfoList = new HashMap<Integer, List<BundleLinkProductInfo>>();
			
			if (bundleLinkProductInfoListFromDb != null)
			{
				for (Map<String, Object> item : bundleLinkProductInfoListFromDb)
				{
					
					Integer bundleId = Integer.parseInt(item.get("BundleID").toString());
					String customerProductId = item.get("ProductID").toString();
					String field1 = "";
					
					if(item.get("Field1") != null)
					{
					  field1 = item.get("Field1").toString();
					}
					
					BundleLinkProductInfo bundleLinkProductInfo = new BundleLinkProductInfo();
					bundleLinkProductInfo.setProductId(customerProductId);
					bundleLinkProductInfo.setField1(field1);
	
					if (tmpBundleProductInfoList.containsKey(bundleId))
					{
						List<BundleLinkProductInfo> tmpList = tmpBundleProductInfoList.get(bundleId);							
						tmpList.add(bundleLinkProductInfo);
						tmpBundleProductInfoList.put(bundleId, tmpList);
					}
					else
					{
						List<BundleLinkProductInfo> tmpList = new ArrayList<BundleLinkProductInfo>();
						tmpList.add(bundleLinkProductInfo);
						tmpBundleProductInfoList.put(bundleId, tmpList);
					}
				}	
				
				if (tmpBundleProductInfoList.size() > 0)
				{
					for (Integer bundleId : nonCachedBundleIdList)
					{
						if (tmpBundleProductInfoList.containsKey(bundleId))
						{									
							BundleLinkProductInfoCache.getCache().put(bundleId, tmpBundleProductInfoList.get(bundleId));
						}
					}
				}
				
				bundleLinkProductInfoListFromDb = null;
			}
			
			for (Integer bundleId : bundleIdList)
			{
							
				List<BundleLinkProductInfo> tmpBundleLinkProductInfoList = BundleLinkProductInfoCache.getCache().get(bundleId);
				
				if(tmpBundleLinkProductInfoList != null)
				{
					if(tmpBundleLinkProductInfoList.size() > 0)
					{	
						if(!multipleBundleLinkProductInfoList.containsKey(bundleId))
						{
							multipleBundleLinkProductInfoList.put(bundleId, tmpBundleLinkProductInfoList);
						}
					}
				}
			}
			
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR, "getBundleLinkProductInfoList", "Error getting bundle link product info", Joiner.on(",").join(nonCachedBundleIdList).toString());
			logger.error(errorMessage, ex);
		}

		return multipleBundleLinkProductInfoList;
	}
}
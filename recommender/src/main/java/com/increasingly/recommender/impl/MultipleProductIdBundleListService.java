package com.increasingly.recommender.impl;

import static com.increasingly.recommender.constants.Constants.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jersey.repackaged.com.google.common.base.Joiner;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.impl.collections.ProductIdBundleListCache;
import com.increasingly.recommender.impl.db.MultipleProductIdBundleList;
import com.increasingly.recommender.impl.db.ProductIdBundleList;
import com.increasingly.recommender.utils.FormatLoggerMessage;


public class MultipleProductIdBundleListService
{
	private static final Logger logger = LoggerFactory.getLogger(MultipleProductIdBundleListService.class.getClass());
	private ProcessTimes processTimes = new ProcessTimes();
	
	/**
	 * @param internalProductIdList
	 * @return BundleIdList
	 */
	public Map<Long,List<BundleIdWithPurchaseCount>> getCachedBundleIdList(List<Long> internalProductIdList,BundleRequest bundleRequest,BundleResult bundleResult,BundleResponse bundleResponse)
	{
		Map<Long,List<BundleIdWithPurchaseCount>> multipleProductBundleIdListWithPurchaseCount = new HashMap<Long,List<BundleIdWithPurchaseCount>>();
				
		List<Long> nonCachedProductIdList = new ArrayList<Long>();	
					
		try
		{			
			for (long internalProductId : internalProductIdList)
			{				
				List<BundleIdWithPurchaseCount> bundleIdList = ProductIdBundleListCache.getCache().get(internalProductId);

				if (bundleIdList == null || bundleIdList.size() == 0)
				{
					nonCachedProductIdList.add(internalProductId);			
				}				
			}

			if (nonCachedProductIdList.size() > 0)
			{
				ArrayList<Map<String, Object>> productIdBundleListFromDb = new ArrayList<Map<String, Object>>();
				
				Map<String, Object> input = new HashMap<String, Object>();				
				input.put(PRODUCT_ID_LIST, Joiner.on(",").join(nonCachedProductIdList).toString());
				input.put(CONFIG_ID,bundleResult.getBundleConfiguration().getConfigId());

				MultipleProductIdBundleList multipleProductIdBundleList = MultipleProductIdBundleList.getInstance();
				productIdBundleListFromDb =  multipleProductIdBundleList.runService(input);					
				
				Map<Long, List<BundleIdWithPurchaseCount>> tmpProductIdBundleList = new HashMap<Long, List<BundleIdWithPurchaseCount>>();

				if (productIdBundleListFromDb != null)
				{
					for (Map<String, Object> item : productIdBundleListFromDb)
					{
						Long internalProductId = Long.parseLong(item.get("ProductID").toString());
						Integer bundleId = Integer.parseInt(item.get("BundleID").toString());
						Integer purchaseCount = Integer.parseInt(item.get("PurchaseCount").toString());
						
						BundleIdWithPurchaseCount bundleIdWithPurchaseCount = new BundleIdWithPurchaseCount();
						bundleIdWithPurchaseCount.setBundleId(bundleId);
						bundleIdWithPurchaseCount.setPurchaseCount(purchaseCount);

						if (tmpProductIdBundleList.containsKey(internalProductId))
						{
							List<BundleIdWithPurchaseCount> tmpBundleIdList = tmpProductIdBundleList.get(internalProductId);							
							tmpBundleIdList.add(bundleIdWithPurchaseCount);
							tmpProductIdBundleList.put(internalProductId, tmpBundleIdList);
						}
						else
						{
							List<BundleIdWithPurchaseCount> tmpBundleIdList = new ArrayList<BundleIdWithPurchaseCount>();
							tmpBundleIdList.add(bundleIdWithPurchaseCount);
							tmpProductIdBundleList.put(internalProductId, tmpBundleIdList);
						}
					}								
					
					if (tmpProductIdBundleList.size() > 0)
					{
						for (long internalProductId : nonCachedProductIdList)
						{
							if (tmpProductIdBundleList.containsKey(internalProductId))
							{									
								ProductIdBundleListCache.getCache().put(internalProductId, tmpProductIdBundleList.get(internalProductId));
							}
						}
					}				
					
				}	
				
				productIdBundleListFromDb = null;
			}
			
					
			BundleConfiguration bundleConfiguration = bundleResult.getBundleConfiguration();
			DateTime startDatetime = DateTime.now();
			
			for (long internalProductId : internalProductIdList)
			{
				List<BundleIdWithPurchaseCount> finalBundleIdListWithPurchaseCount = new ArrayList<BundleIdWithPurchaseCount>();
				List<BundleIdWithPurchaseCount> tempBundleIdListWithPurchaseCount = new ArrayList<BundleIdWithPurchaseCount>();
				List<Integer> tempBundleIdList = new ArrayList<Integer>();		
				List<Integer> finalBundleIdList = new ArrayList<Integer>();
				ChooseRelevantBundles chooseRelevantBundles = new ChooseRelevantBundles();	
				
				List<BundleIdWithPurchaseCount> bundleIdWithPurchaseCountList = ProductIdBundleListCache.getCache().get(internalProductId);
				
				if(bundleIdWithPurchaseCountList != null)
				{
					tempBundleIdListWithPurchaseCount.addAll(bundleIdWithPurchaseCountList);
					Collections.sort(bundleIdWithPurchaseCountList, new SortBundlesWithPurchaseRate());
					
					for(BundleIdWithPurchaseCount bundleIdWithPurchaseCountItem :bundleIdWithPurchaseCountList)
					{
						tempBundleIdList.add(bundleIdWithPurchaseCountItem.getBundleId());						
					}
					
					if(tempBundleIdList.size() > 0)
					{	
						finalBundleIdList.addAll(chooseRelevantBundles.getProductPageBundleIdList(tempBundleIdList,bundleConfiguration,bundleRequest,null,bundleResult,null));
					}
					
					if(tempBundleIdListWithPurchaseCount.size() > 0)
					{
						for(BundleIdWithPurchaseCount bundleIdWithPurchaseCountItem :tempBundleIdListWithPurchaseCount)
						{
							if(finalBundleIdList.contains(bundleIdWithPurchaseCountItem.getBundleId()))
							{
								finalBundleIdListWithPurchaseCount.add(bundleIdWithPurchaseCountItem);
							}
						}
					}
					
					multipleProductBundleIdListWithPurchaseCount.put(internalProductId, finalBundleIdListWithPurchaseCount);
				}
			}
			
			bundleResponse.getResponseProcessTimes().setTimeTakenToGetBundleProductIdList(processTimes.getTimeTaken(startDatetime));
			
				
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR ,"getCachedBundleIdList" , "Error getting bundle id list for internal product id","");
			logger.error(errorMessage, ex);
		}	
		
		return multipleProductBundleIdListWithPurchaseCount;
	}
		
}
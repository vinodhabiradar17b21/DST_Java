package com.increasingly.recommender.impl;

import static com.increasingly.recommender.constants.Constants.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jersey.repackaged.com.google.common.base.Joiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.impl.collections.AbandonedCartBundleListCache;
import com.increasingly.recommender.impl.collections.CategoryIdAbandonedCartBundleListCache;
import com.increasingly.recommender.impl.collections.CategoryIdBundleListCache;
import com.increasingly.recommender.impl.collections.CategoryIdBundleListWithBundleTypeCache;
import com.increasingly.recommender.impl.db.CategoryIdBundleList;
import com.increasingly.recommender.utils.FormatLoggerMessage;

public class CategoryIdBundleListService
{
	private static final Logger logger = LoggerFactory.getLogger(CategoryIdBundleListService.class.getClass());
	
	/**
	 * @param internalCategoryIdList
	 * @return BundleIdList
	 */
	public List<Integer> getCachedBundleIdList(List<Integer> internalCategoryIdList,BundleRequest bundleRequest,BundleResult bundleResult)
	{
		List<Integer> categoryBundleIdList = new ArrayList<Integer>();
		List<BundleIdWithPurchaseCount> tempBundleIdListWithPurchaseCount = new ArrayList<BundleIdWithPurchaseCount>();
		List<BundleIdWithPurchaseCount> tempAbandonedCartBundleIdListWithPurchaseCount = new ArrayList<BundleIdWithPurchaseCount>();
		List<Integer> nonCachedCategoryIdList = new ArrayList<Integer>();		
			
		try
		{		
			int bundleTypeId = bundleRequest.getBundleTypeId();
			for (Integer internalCategoryId : internalCategoryIdList)
			{		
				List<BundleIdWithPurchaseCount> bundleIdList = null;				

				if(bundleTypeId > 0)
				{
					bundleIdList = CategoryIdBundleListWithBundleTypeCache.getCache().get(internalCategoryId+"_"+bundleTypeId);
				}
				else
				{
					bundleIdList = CategoryIdBundleListCache.getCache().get(internalCategoryId);
				}
				
				if (bundleIdList != null && bundleIdList.size() > 0)
				{
					tempBundleIdListWithPurchaseCount.addAll(bundleIdList);				
				}
				else
				{
					nonCachedCategoryIdList.add(internalCategoryId);						
				}
			}

			if (nonCachedCategoryIdList.size() > 0)
			{
				Map<String,ArrayList<Map<String, Object>>> categoryIdBundleListFromDb = new HashMap<String, ArrayList<Map<String,Object>>>();
				
				Map<String, Object> input = new HashMap<String, Object>();				
				input.put(CATEGORY_ID_LIST, nonCachedCategoryIdList.get(0));
				input.put(CLIENT_ID, bundleRequest.getClientId());
				input.put(BUNDLE_TYPE_ID, bundleRequest.getBundleTypeId());

				if(bundleResult.getBundleConfiguration().getClientVisitorIds().contains(bundleRequest.getClientVisitorId()))
				{
					input.put(NO_OF_BUNDLES, 100);
				}
				else
				{
					input.put(NO_OF_BUNDLES, 0);
				}
				
				CategoryIdBundleList categoryIdBundleList = CategoryIdBundleList.getInstance();
				categoryIdBundleListFromDb =  categoryIdBundleList.runService(input);					
				
				Map<Integer, List<BundleIdWithPurchaseCount>> tmpCategoryIdBundleList = new HashMap<Integer, List<BundleIdWithPurchaseCount>>();
				Map<Integer, List<BundleIdWithPurchaseCount>> tmpAbandonedCartBundleIdList = new HashMap<Integer, List<BundleIdWithPurchaseCount>>();

				if (categoryIdBundleListFromDb != null)
				{
					if(categoryIdBundleListFromDb.get(BUNDLE_ID_LIST)!=null && categoryIdBundleListFromDb.get(BUNDLE_ID_LIST).size() > 0)
					{
						for (Map<String, Object> item : categoryIdBundleListFromDb.get(BUNDLE_ID_LIST))
						{
							Integer internalCategoryId = Integer.parseInt(item.get("CategoryID").toString());
							Integer bundleId = Integer.parseInt(item.get("BundleID").toString());
							Integer purchaseCount = Integer.parseInt(item.get("PurchaseCount").toString());
							Double marginPercent = Double.parseDouble(item.get("MarginPercent").toString());
							
							BundleIdWithPurchaseCount bundleIdWithPurchaseCount = new BundleIdWithPurchaseCount();
							bundleIdWithPurchaseCount.setBundleId(bundleId);
							bundleIdWithPurchaseCount.setPurchaseCount(purchaseCount);
							bundleIdWithPurchaseCount.setMarginPercent(marginPercent);
							
							if (tmpCategoryIdBundleList.containsKey(internalCategoryId))
							{
								List<BundleIdWithPurchaseCount> tmpBundleIdList = tmpCategoryIdBundleList.get(internalCategoryId);
								tmpBundleIdList.add(bundleIdWithPurchaseCount);
								tmpCategoryIdBundleList.put(internalCategoryId, tmpBundleIdList);
							}
							else
							{
								List<BundleIdWithPurchaseCount> tmpBundleIdList = new ArrayList<BundleIdWithPurchaseCount>();
								tmpBundleIdList.add(bundleIdWithPurchaseCount);
								tmpCategoryIdBundleList.put(internalCategoryId, tmpBundleIdList);
							}
						}								
						
						if (tmpCategoryIdBundleList.size() > 0)
						{
							for (Integer internalCategoryId : nonCachedCategoryIdList)
							{
								if (tmpCategoryIdBundleList.containsKey(internalCategoryId))
								{							
									tempBundleIdListWithPurchaseCount.addAll(tmpCategoryIdBundleList.get(internalCategoryId));
									if(bundleTypeId > 0)
									{
										CategoryIdBundleListWithBundleTypeCache.getCache().put(internalCategoryId+"_"+bundleTypeId, tmpCategoryIdBundleList.get(internalCategoryId));
									}
									else
									{
										CategoryIdBundleListCache.getCache().put(internalCategoryId, tmpCategoryIdBundleList.get(internalCategoryId));
									}
								}
							}
						}	
					}
					
					// Abandoned Cart Bundles 
					if(categoryIdBundleListFromDb.get(ABANDONED_CART_BUNDLE_ID_LIST)!=null && categoryIdBundleListFromDb.get(ABANDONED_CART_BUNDLE_ID_LIST).size() > 0)
					{
						for (Map<String, Object> item : categoryIdBundleListFromDb.get(ABANDONED_CART_BUNDLE_ID_LIST))
						{
							Integer internalCategoryId = Integer.parseInt(item.get("CategoryID").toString());
							Integer bundleId = Integer.parseInt(item.get("BundleID").toString());
							Integer purchaseCount = Integer.parseInt(item.get("PurchaseCount").toString());
							Double marginPercent = Double.parseDouble(item.get("MarginPercent").toString());

							BundleIdWithPurchaseCount bundleIdWithPurchaseCount = new BundleIdWithPurchaseCount();
							bundleIdWithPurchaseCount.setBundleId(bundleId);
							bundleIdWithPurchaseCount.setPurchaseCount(purchaseCount);
							bundleIdWithPurchaseCount.setMarginPercent(marginPercent);
							
							if (tmpAbandonedCartBundleIdList.containsKey(internalCategoryId))
							{
								List<BundleIdWithPurchaseCount> tmpBundleIdList = tmpAbandonedCartBundleIdList.get(internalCategoryId);
								tmpBundleIdList.add(bundleIdWithPurchaseCount);
								tmpAbandonedCartBundleIdList.put(internalCategoryId, tmpBundleIdList);
							}
							else
							{
								List<BundleIdWithPurchaseCount> tmpBundleIdList = new ArrayList<BundleIdWithPurchaseCount>();
								tmpBundleIdList.add(bundleIdWithPurchaseCount);
								tmpAbandonedCartBundleIdList.put(internalCategoryId, tmpBundleIdList);
							}
						}								
						
						if (tmpAbandonedCartBundleIdList.size() > 0)
						{
							for(Map.Entry<Integer, List<BundleIdWithPurchaseCount>> entry: tmpAbandonedCartBundleIdList.entrySet())
							{
								CategoryIdAbandonedCartBundleListCache.getCache().put(entry.getKey(),entry.getValue());
							}
						}
					}
				}			
			}
			
			List<Integer> tempBundleIdList = new ArrayList<Integer>();
			List<Integer> finalBundleIdList = new ArrayList<Integer>();
			List<Integer> tempAbandonedCartBundleIdList = new ArrayList<Integer>();
			List<BundleIdWithPurchaseCount> abandonedCartBundleIdWithPurchaseCountList = new ArrayList<BundleIdWithPurchaseCount>();
			BundleConfiguration bundleConfiguration = bundleResult.getBundleConfiguration();

			abandonedCartBundleIdWithPurchaseCountList = CategoryIdAbandonedCartBundleListCache.getCache().get(internalCategoryIdList.get(0));
			
			int maxNoOfBundles = bundleRequest.getNoOfBundles();
			
			if(maxNoOfBundles == 0)
			{
				maxNoOfBundles = bundleConfiguration.getMaxNoOfBundlesForCategoryPage();
			}
			if(tempBundleIdListWithPurchaseCount.size() > 0  || (abandonedCartBundleIdWithPurchaseCountList != null && abandonedCartBundleIdWithPurchaseCountList.size() > 0))
			{
				if(abandonedCartBundleIdWithPurchaseCountList != null && abandonedCartBundleIdWithPurchaseCountList.size() > 0)
				{
					tempAbandonedCartBundleIdListWithPurchaseCount.addAll(abandonedCartBundleIdWithPurchaseCountList);
				}
				
				if(tempAbandonedCartBundleIdListWithPurchaseCount.size() > 0)
				{
					if(bundleConfiguration.getIsMarginBundlingEnabled())
					{
						Collections.sort(tempAbandonedCartBundleIdListWithPurchaseCount, new SortBundlesOnMarginPercent());
						
						for(BundleIdWithPurchaseCount abandonedCartBundleIdWithPurchaseCountItem :tempAbandonedCartBundleIdListWithPurchaseCount)
						{
							tempAbandonedCartBundleIdList.add(abandonedCartBundleIdWithPurchaseCountItem.getBundleId());
							tempAbandonedCartBundleIdListWithPurchaseCount.remove(abandonedCartBundleIdWithPurchaseCountItem.getBundleId());
							
							if(tempAbandonedCartBundleIdList.size() >= 5)
							{
								break;
							}
						}
					}
					
					Collections.sort(tempAbandonedCartBundleIdListWithPurchaseCount, new SortBundlesWithPurchaseRate());
					for(BundleIdWithPurchaseCount abandonedCartBundleIdWithPurchaseCountItem :tempAbandonedCartBundleIdListWithPurchaseCount)
					{
						if(!tempAbandonedCartBundleIdList.contains(abandonedCartBundleIdWithPurchaseCountItem.getBundleId()))
						{
							tempAbandonedCartBundleIdList.add(abandonedCartBundleIdWithPurchaseCountItem.getBundleId());
						}
						
						if(tempAbandonedCartBundleIdList.size() >= 30)
						{
							break;
						}
					}
				}
				
				if(bundleConfiguration.getIsMarginBundlingEnabled())
				{
					Collections.sort(tempBundleIdListWithPurchaseCount, new SortBundlesOnMarginPercent());
					
					for(BundleIdWithPurchaseCount bundleIdWithPurchaseCount :tempBundleIdListWithPurchaseCount)
					{
						tempBundleIdList.add(bundleIdWithPurchaseCount.getBundleId());
						tempBundleIdListWithPurchaseCount.remove(bundleIdWithPurchaseCount.getBundleId());
						
						if(tempBundleIdList.size() >= 5)
						{
							break;
						}
						
					}
				}
				
				Collections.sort(tempBundleIdListWithPurchaseCount, new SortBundlesWithPurchaseRate());
				for(BundleIdWithPurchaseCount bundleIdWithPurchaseCount :tempBundleIdListWithPurchaseCount)
				{
					if(!tempBundleIdList.contains(bundleIdWithPurchaseCount.getBundleId()))
					{
						tempBundleIdList.add(bundleIdWithPurchaseCount.getBundleId());
					}
					
					if(tempBundleIdList.size() >= 30)
					{
						break;
					}
				}
				
				ChooseRelevantBundles chooseRelevantBundles = new ChooseRelevantBundles();
				
				if(tempBundleIdList.size() > 0 || tempAbandonedCartBundleIdList.size() > 0)
				{			
				  finalBundleIdList.addAll(chooseRelevantBundles.getCategoryPageBundleIdList(tempBundleIdList,bundleConfiguration,bundleResult,tempAbandonedCartBundleIdList,bundleRequest,maxNoOfBundles));
				}
				
				if(finalBundleIdList.size() > 0)
				{										
					for(Integer bundleId :finalBundleIdList)
					{
						for(BundleIdWithPurchaseCount bundleIdWithPurchaseCountItem :tempBundleIdListWithPurchaseCount)
						{
							if(bundleIdWithPurchaseCountItem.getBundleId().equals(bundleId))
							{
								categoryBundleIdList.add(bundleIdWithPurchaseCountItem.getBundleId());
							}
						}
					}
					
					for(Integer bundleId :finalBundleIdList)
					{
						for(BundleIdWithPurchaseCount bundleIdWithPurchaseCountItem :tempAbandonedCartBundleIdListWithPurchaseCount)
						{
							if(bundleIdWithPurchaseCountItem.getBundleId().equals(bundleId))
							{
								categoryBundleIdList.add(bundleIdWithPurchaseCountItem.getBundleId());
							}								
						}							
					}
					
				}	
				else 
				{
					if(bundleRequest.getBackFillBundles()!=null)
					{
						if(bundleRequest.getBackFillBundles())
						{
							categoryBundleIdList.addAll(getBackFillBundles(bundleConfiguration,maxNoOfBundles));
						}
					}
					else if(bundleConfiguration.isBackFillBundlesEnabled())
					{
						categoryBundleIdList.addAll(getBackFillBundles(bundleConfiguration,maxNoOfBundles));
					}
				}
			}
			else
			{
				if(bundleRequest.getBackFillBundles()!=null)
				{
					if(bundleRequest.getBackFillBundles())
					{					
						categoryBundleIdList.addAll(getBackFillBundles(bundleConfiguration,maxNoOfBundles));
					}
				}
				else if(bundleConfiguration.isBackFillBundlesEnabled())
				{
					categoryBundleIdList.addAll(getBackFillBundles(bundleConfiguration,maxNoOfBundles));
				}
			}
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR ,"getCachedBundleIdList" , "Error getting bundle id list for internal category id list","");
			logger.error(errorMessage, ex);
		}	
		
		return categoryBundleIdList;
	}
	
	public List<Integer> getBackFillBundles(BundleConfiguration bundleConfiguration, int maxNoOfBundles)
	{
		List<Integer> finalBundleIdList = new ArrayList<Integer>();
		for(int item : bundleConfiguration.getBackFillBundlesList())
		{
			finalBundleIdList.add(item);
			if(finalBundleIdList.size() == maxNoOfBundles)
			{
				break;
			}	
		}
		return finalBundleIdList;
	}
	
}
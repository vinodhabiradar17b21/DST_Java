package com.increasingly.recommender.impl;

import static com.increasingly.recommender.constants.Constants.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import jersey.repackaged.com.google.common.base.Joiner;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.impl.collections.AbandonedCartBundleListCache;
import com.increasingly.recommender.impl.collections.CartProductIdBundleListCache;
import com.increasingly.recommender.impl.collections.CartProductIdBundleListWithBundleTypeCache;
import com.increasingly.recommender.impl.collections.CartProductIdMLBundleListCache;
import com.increasingly.recommender.impl.collections.CheckoutProductIdBundleListCache;
import com.increasingly.recommender.impl.collections.CheckoutProductIdBundleListWithBundleTypeCache;
import com.increasingly.recommender.impl.collections.CheckoutProductIdMLBundleListCache;
import com.increasingly.recommender.impl.collections.ProductIdBundleListWithBundleTypeCache;
import com.increasingly.recommender.impl.collections.ProductIdBundleListCache;
import com.increasingly.recommender.impl.collections.ProductIdMLBundleListCache;
import com.increasingly.recommender.impl.db.CartPageProductIdBundleList;
import com.increasingly.recommender.impl.db.CheckoutPageProductIdBundleList;
import com.increasingly.recommender.impl.db.ProductIdBundleList;
import com.increasingly.recommender.utils.FormatLoggerMessage;


public class ProductIdBundleListService
{
	private static final Logger logger = LoggerFactory.getLogger(ProductIdBundleListService.class.getClass());
	private ProcessTimes processTimes = new ProcessTimes();
	private static Properties increasinglyProperties = null;

	/**
	 * @param internalProductIdList
	 * @return BundleIdList
	 */
	public List<Integer> getCachedBundleIdList(List<Long> internalProductIdList,BundleRequest bundleRequest,BundleResult bundleResult,BundleResponse bundleResponse,Integer reqBundleTypeId)
	{
		List<Integer> productBundleIdList = new ArrayList<Integer>();
		List<BundleIdWithPurchaseCount> tempBundleIdListWithPurchaseCount = new ArrayList<BundleIdWithPurchaseCount>();
		List<BundleIdWithPurchaseCount> tempAbandonedCartBundleIdListWithPurchaseCount = new ArrayList<BundleIdWithPurchaseCount>();
		List<BundleIdWithPurchaseCount> abandonedCartBundleIdWithPurchaseCountList = null;
		List<Long> nonCachedProductIdList = new ArrayList<Long>();	
		Map<Long, List<BundleIdWithPurchaseCount>> tempBundleIdListWithPurchaseCountForCartPage = new HashMap<Long, List<BundleIdWithPurchaseCount>>();
		Map<Long, List<BundleIdWithPurchaseCount>> tempAbandonedCartBundleIdListWithPurchaseCountForCartPage = new HashMap<Long, List<BundleIdWithPurchaseCount>>();
		try
		{			
			for (long internalProductId : internalProductIdList)
			{				
				
				//List<BundleIdWithPurchaseCount> bundleIdList = ProductIdBundleListCache.getCache().get(internalProductId);
				
				List<BundleIdWithPurchaseCount> bundleIdList = null;				

				/* Allow simple/configurable product bundles in cart page */
				
				
				if(bundleRequest.getPageType().equals(CART_PAGE))
				{
					if(reqBundleTypeId == 13)
					{
						bundleIdList = CartProductIdMLBundleListCache.getCache().get(internalProductId);
					}
					else
					{
						if(reqBundleTypeId > 0)
						{
							bundleIdList = CartProductIdBundleListWithBundleTypeCache.getCache().get(internalProductId+"_"+reqBundleTypeId);
						}
						else
						{
							bundleIdList = CartProductIdBundleListCache.getCache().get(internalProductId);
						}
					}
				}
				else if(bundleRequest.getPageType().equals(CHECKOUT_PAGE))
				{
					if(reqBundleTypeId == 13)
					{
						bundleIdList = CheckoutProductIdMLBundleListCache.getCache().get(internalProductId);
					}
					else
					{
						if(reqBundleTypeId > 0)
						{
							bundleIdList = CheckoutProductIdBundleListWithBundleTypeCache.getCache().get(internalProductId+"_"+reqBundleTypeId);
						}
						else
						{
							bundleIdList = CheckoutProductIdBundleListCache.getCache().get(internalProductId);
						}
					}
				}
				else
				{
					if(reqBundleTypeId == 13)
					{
						bundleIdList = ProductIdMLBundleListCache.getCache().get(internalProductId);
					}
					else
					{
						if(reqBundleTypeId > 0)
						{
							bundleIdList = ProductIdBundleListWithBundleTypeCache.getCache().get(internalProductId+"_"+reqBundleTypeId);
						}
						else
						{
							bundleIdList = ProductIdBundleListCache.getCache().get(internalProductId);
						}
					}
					
				}
				

				if (bundleIdList == null || bundleIdList.size() == 0)
				{
					nonCachedProductIdList.add(internalProductId);			
				}
			}

			if (nonCachedProductIdList.size() > 0)
			{
			
				Map<String,ArrayList<Map<String, Object>>> productIdBundleListFromDb = new HashMap<String, ArrayList<Map<String,Object>>>();
				
				Map<String, Object> input = new HashMap<String, Object>();				
				input.put(PRODUCT_ID_LIST, Joiner.on(",").join(nonCachedProductIdList).toString());
				input.put(CONFIG_ID,bundleResult.getBundleConfiguration().getConfigId());			
				input.put(BUNDLE_TYPE_ID, reqBundleTypeId);

				if(bundleResult.getBundleConfiguration().getClientVisitorIds().contains(bundleRequest.getClientVisitorId()))
				{
					input.put(NO_OF_BUNDLES, 100);
				}
				else
				{
					input.put(NO_OF_BUNDLES, 0);
				}
				if(bundleRequest.getPageType().equals(CART_PAGE))
				{
					CartPageProductIdBundleList cartPageProductIdBundleList = CartPageProductIdBundleList.getInstance();
					productIdBundleListFromDb = cartPageProductIdBundleList.runService(input);	
				}
				else if(bundleRequest.getPageType().equals(CHECKOUT_PAGE))
				{
					CheckoutPageProductIdBundleList checkoutPageProductIdBundleList = CheckoutPageProductIdBundleList.getInstance();
					productIdBundleListFromDb = checkoutPageProductIdBundleList.runService(input);	
				}
				else{
					ProductIdBundleList productIdBundleList = ProductIdBundleList.getInstance();
					productIdBundleListFromDb = productIdBundleList.runService(input);	
				}
							
				
				Map<Long, List<BundleIdWithPurchaseCount>> tmpProductIdBundleList = new HashMap<Long, List<BundleIdWithPurchaseCount>>();
				Map<Long, List<BundleIdWithPurchaseCount>> tmpAbandonedCartBundleIdList = new HashMap<Long, List<BundleIdWithPurchaseCount>>();

				if (productIdBundleListFromDb != null)
				{
					if(productIdBundleListFromDb.get(BUNDLE_ID_LIST)!=null && productIdBundleListFromDb.get(BUNDLE_ID_LIST).size() > 0)
					{
						for (Map<String, Object> item : productIdBundleListFromDb.get(BUNDLE_ID_LIST))
						{
							Long internalProductId = Long.parseLong(item.get("ProductID").toString());
							Integer bundleId = Integer.parseInt(item.get("BundleID").toString());
							Integer purchaseCount = Integer.parseInt(item.get("PurchaseCount").toString());
							Double marginPercent = Double.parseDouble(item.get("MarginPercent").toString());
							
							BundleIdWithPurchaseCount bundleIdWithPurchaseCount = new BundleIdWithPurchaseCount();
							bundleIdWithPurchaseCount.setBundleId(bundleId);
							bundleIdWithPurchaseCount.setPurchaseCount(purchaseCount);
							bundleIdWithPurchaseCount.setMarginPercent(marginPercent);
							
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
									//ProductIdBundleListCache.getCache().put(internalProductId, tmpProductIdBundleList.get(internalProductId));
																	
									if(bundleRequest.getPageType().equals(CART_PAGE))
									{
										if(reqBundleTypeId == 13)
										{
											CartProductIdMLBundleListCache.getCache().put(internalProductId, tmpProductIdBundleList.get(internalProductId));
										}
										else
										{
											if(reqBundleTypeId > 0)
											{
												CartProductIdBundleListWithBundleTypeCache.getCache().put(internalProductId+"_"+reqBundleTypeId, tmpProductIdBundleList.get(internalProductId));
											}
											else
											{
												CartProductIdBundleListCache.getCache().put(internalProductId, tmpProductIdBundleList.get(internalProductId));
											}
										}
									}
									else if(bundleRequest.getPageType().equals(CHECKOUT_PAGE))
									{
										if(reqBundleTypeId == 13)
										{
											CheckoutProductIdMLBundleListCache.getCache().put(internalProductId, tmpProductIdBundleList.get(internalProductId));
										}
										else
										{
											if(reqBundleTypeId > 0)
											{
												CheckoutProductIdBundleListWithBundleTypeCache.getCache().put(internalProductId+"_"+reqBundleTypeId, tmpProductIdBundleList.get(internalProductId));
											}
											else
											{
												CheckoutProductIdBundleListCache.getCache().put(internalProductId, tmpProductIdBundleList.get(internalProductId));
											}
										}
									}
									else
									{
										if(reqBundleTypeId == 13)
										{
											ProductIdMLBundleListCache.getCache().put(internalProductId, tmpProductIdBundleList.get(internalProductId));
										}
										else
										{
											if(reqBundleTypeId > 0)
											{
												ProductIdBundleListWithBundleTypeCache.getCache().put(internalProductId+"_"+reqBundleTypeId, tmpProductIdBundleList.get(internalProductId));
											}
											else
											{
												ProductIdBundleListCache.getCache().put(internalProductId, tmpProductIdBundleList.get(internalProductId));
											}
										}
									}
									
								}
							}
						}
					}
						
					
					// Caching of Abandoned Cart Bundles with visitorId as Key		
					if(productIdBundleListFromDb.get(ABANDONED_CART_BUNDLE_ID_LIST)!=null && productIdBundleListFromDb.get(ABANDONED_CART_BUNDLE_ID_LIST).size() > 0)
					{
						for (Map<String, Object> item : productIdBundleListFromDb.get(ABANDONED_CART_BUNDLE_ID_LIST))
						{
							
							Long internalProductId = Long.parseLong(item.get("ProductID").toString());
							Integer bundleId = Integer.parseInt(item.get("BundleID").toString());
							Integer purchaseCount = Integer.parseInt(item.get("PurchaseCount").toString());
							Double marginPercent = Double.parseDouble(item.get("MarginPercent").toString());
							
							BundleIdWithPurchaseCount bundleIdWithPurchaseCount = new BundleIdWithPurchaseCount();
							bundleIdWithPurchaseCount.setBundleId(bundleId);
							bundleIdWithPurchaseCount.setPurchaseCount(purchaseCount);
							bundleIdWithPurchaseCount.setMarginPercent(marginPercent);
							
							if (tmpAbandonedCartBundleIdList.containsKey(internalProductId))
							{
								List<BundleIdWithPurchaseCount> tmpBundleIdList = tmpAbandonedCartBundleIdList.get(internalProductId);							
								tmpBundleIdList.add(bundleIdWithPurchaseCount);
								tmpAbandonedCartBundleIdList.put(internalProductId, tmpBundleIdList);
							}
							else
							{
								List<BundleIdWithPurchaseCount> tmpBundleIdList = new ArrayList<BundleIdWithPurchaseCount>();
								tmpBundleIdList.add(bundleIdWithPurchaseCount);
								tmpAbandonedCartBundleIdList.put(internalProductId, tmpBundleIdList);
							}
						}
						if (tmpAbandonedCartBundleIdList.size() > 0)
						{
							for(Map.Entry<Long, List<BundleIdWithPurchaseCount>> entry: tmpAbandonedCartBundleIdList.entrySet())
							{
								AbandonedCartBundleListCache.getCache().put(entry.getKey(),entry.getValue());
							}
						}
					}	
				}	
				
				productIdBundleListFromDb = null;
			}
			
			List<Integer> tempBundleIdList = new ArrayList<Integer>();
			List<Integer> tempAbandonedCartBundleIdList = new ArrayList<Integer>();
			List<Integer> tempCartBundleIdList = new ArrayList<Integer>();
			List<Integer> finalBundleIdList = new ArrayList<Integer>();
			
			ChooseRelevantBundles chooseRelevantBundles = new ChooseRelevantBundles();			
			BundleConfiguration bundleConfiguration = bundleResult.getBundleConfiguration();
			DateTime startDatetime = DateTime.now();
			int maxNoOfBundles = 0;
			if(bundleRequest.getPageType().equals(PRODUCT_PAGE))
			{
				maxNoOfBundles = bundleRequest.getNoOfBundles();
				
				if(maxNoOfBundles == 0)
				{
					maxNoOfBundles = bundleConfiguration.getMaxNoOfBundlesForProductPage();
				}
				
				for (long internalProductId : internalProductIdList)
				{
					// List<BundleIdWithPurchaseCount> bundleIdWithPurchaseCountList = ProductIdBundleListCache.getCache().get(internalProductId);
					
					List<BundleIdWithPurchaseCount> bundleIdWithPurchaseCountList = null;

					if(reqBundleTypeId == 13)
					{
						bundleIdWithPurchaseCountList = ProductIdMLBundleListCache.getCache().get(internalProductId);
					}
					else
					{
						if(reqBundleTypeId > 0)
						{
							bundleIdWithPurchaseCountList = ProductIdBundleListWithBundleTypeCache.getCache().get(internalProductId+"_"+reqBundleTypeId);
						}
						else
						{
							bundleIdWithPurchaseCountList = ProductIdBundleListCache.getCache().get(internalProductId);
						}
					}
					
					abandonedCartBundleIdWithPurchaseCountList = AbandonedCartBundleListCache.getCache().get(internalProductId);
					
					if(abandonedCartBundleIdWithPurchaseCountList != null)
					{
						tempAbandonedCartBundleIdListWithPurchaseCount.addAll(abandonedCartBundleIdWithPurchaseCountList);
						
						if(bundleConfiguration.getIsMarginBundlingEnabled())
						{
							Collections.sort(abandonedCartBundleIdWithPurchaseCountList, new SortBundlesOnMarginPercent());
							
							for(BundleIdWithPurchaseCount abandonedCartBundleIdWithPurchaseCountItem :abandonedCartBundleIdWithPurchaseCountList)
							{
								tempAbandonedCartBundleIdList.add(abandonedCartBundleIdWithPurchaseCountItem.getBundleId());							
								abandonedCartBundleIdWithPurchaseCountList.remove(abandonedCartBundleIdWithPurchaseCountItem.getBundleId());
								
								if(tempAbandonedCartBundleIdList.size() >= 5)
								{
									break;
								}
							}
						}
						
						Collections.sort(abandonedCartBundleIdWithPurchaseCountList, new SortBundlesWithPurchaseRate());
						
						for(BundleIdWithPurchaseCount abandonedCartBundleIdWithPurchaseCountItem :abandonedCartBundleIdWithPurchaseCountList)
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
						
					if(bundleIdWithPurchaseCountList != null)
					{
						tempBundleIdListWithPurchaseCount.addAll(bundleIdWithPurchaseCountList);
						
						if(bundleConfiguration.getIsMarginBundlingEnabled())
						{
							Collections.sort(bundleIdWithPurchaseCountList, new SortBundlesOnMarginPercent());
							
							for(BundleIdWithPurchaseCount bundleIdWithPurchaseCountItem :bundleIdWithPurchaseCountList)
							{
								tempBundleIdList.add(bundleIdWithPurchaseCountItem.getBundleId());							
								bundleIdWithPurchaseCountList.remove(bundleIdWithPurchaseCountItem.getBundleId());
								
								if(tempBundleIdList.size() >= 5)
								{
									break;
								}
							}
						}
						
						Collections.sort(bundleIdWithPurchaseCountList, new SortBundlesWithPurchaseRate());
						
						for(BundleIdWithPurchaseCount bundleIdWithPurchaseCountItem :bundleIdWithPurchaseCountList)
						{
							if(!tempBundleIdList.contains(bundleIdWithPurchaseCountItem.getBundleId()))
							{
							 tempBundleIdList.add(bundleIdWithPurchaseCountItem.getBundleId());
							}
							
							if(tempBundleIdList.size() >= 30)
							{
								break;
							}
						}
					}
				}
			}
			else
			{	
				List<Long> bundlesServedForProductList = new ArrayList<Long>();
				
				maxNoOfBundles = bundleRequest.getNoOfBundles();
				
				if(maxNoOfBundles == 0)
				{
					maxNoOfBundles = bundleConfiguration.getMaxNoOfBundlesForCartPage();
				}
				
				List<BundleIdWithPurchaseCount> bundleIdWithPurchaseCountListAll = new ArrayList<BundleIdWithPurchaseCount>(); 
				List<BundleIdWithPurchaseCount> abandonedBundleIdWithPurchaseCountListAll = new ArrayList<BundleIdWithPurchaseCount>(); 
				
				for (long internalProductId : internalProductIdList)
				{
					
					if(bundleRequest.getPageType().equals(CART_PAGE))
					{
						List<BundleIdWithPurchaseCount> bundleIdWithPurchaseCountList = null;
						List<BundleIdWithPurchaseCount> abandonedBundleIdWithPurchaseCountList = null;
						
						if(reqBundleTypeId > 0)
						{
							bundleIdWithPurchaseCountList = CartProductIdBundleListWithBundleTypeCache.getCache().get(internalProductId+"_"+reqBundleTypeId);
						}
						else
						{
							bundleIdWithPurchaseCountList = CartProductIdBundleListCache.getCache().get(internalProductId);
						}
						
						if(bundleIdWithPurchaseCountList != null)
						{
						  bundleIdWithPurchaseCountListAll.addAll(bundleIdWithPurchaseCountList);
						}
						
						abandonedBundleIdWithPurchaseCountList = AbandonedCartBundleListCache.getCache().get(internalProductId);
						
						if(abandonedBundleIdWithPurchaseCountList != null)
						{
							abandonedBundleIdWithPurchaseCountListAll.addAll(abandonedBundleIdWithPurchaseCountList);
						}
					}
					else if(bundleRequest.getPageType().equals(CHECKOUT_PAGE))
					{
						List<BundleIdWithPurchaseCount> bundleIdWithPurchaseCountList = null;
						List<BundleIdWithPurchaseCount> abandonedBundleIdWithPurchaseCountList = null;
						
						if(reqBundleTypeId > 0)
						{
							bundleIdWithPurchaseCountList = CheckoutProductIdBundleListWithBundleTypeCache.getCache().get(internalProductId+"_"+reqBundleTypeId);
						}
						else
						{
							bundleIdWithPurchaseCountList = CheckoutProductIdBundleListCache.getCache().get(internalProductId);
						}
						
						if(bundleIdWithPurchaseCountList != null)
						{
						  bundleIdWithPurchaseCountListAll.addAll(bundleIdWithPurchaseCountList);
						}
						
						abandonedBundleIdWithPurchaseCountList = AbandonedCartBundleListCache.getCache().get(internalProductId);
						
						if(abandonedBundleIdWithPurchaseCountList != null)
						{
							abandonedBundleIdWithPurchaseCountListAll.addAll(abandonedBundleIdWithPurchaseCountList);
						}
					}					
				}
								
				List<Integer> tempFinalAllBundleIdList = new ArrayList<Integer>();
				List<Integer> tempAbandonedFinalAllBundleIdList = new ArrayList<Integer>();
				
				if(bundleIdWithPurchaseCountListAll != null)
				{			
					if(bundleConfiguration.getIsMarginBundlingEnabled())
					{
						Collections.sort(bundleIdWithPurchaseCountListAll, new SortBundlesOnMarginPercent());
						
						for(BundleIdWithPurchaseCount bundleIdWithPurchaseCountItem :bundleIdWithPurchaseCountListAll)
						{
							tempFinalAllBundleIdList.add(bundleIdWithPurchaseCountItem.getBundleId());							
							bundleIdWithPurchaseCountListAll.remove(bundleIdWithPurchaseCountItem.getBundleId());
							
							if(tempFinalAllBundleIdList.size() >= 5)
							{
								break;
							}
						}
					}
					
					Collections.sort(bundleIdWithPurchaseCountListAll, new SortBundlesWithPurchaseRate());
					
					for(BundleIdWithPurchaseCount bundleIdWithPurchaseCountItem :bundleIdWithPurchaseCountListAll)
					{
						if(!tempFinalAllBundleIdList.contains(bundleIdWithPurchaseCountItem.getBundleId()))
						{
							tempFinalAllBundleIdList.add(bundleIdWithPurchaseCountItem.getBundleId());
						}
						
						if(tempFinalAllBundleIdList.size() >= 30)
						{
							break;
						}
					}
				}
				
				if(abandonedBundleIdWithPurchaseCountListAll != null)
				{
					if(bundleConfiguration.getIsMarginBundlingEnabled())
					{
						Collections.sort(abandonedBundleIdWithPurchaseCountListAll, new SortBundlesOnMarginPercent());
						
						for(BundleIdWithPurchaseCount bundleIdWithPurchaseCountItem :abandonedBundleIdWithPurchaseCountListAll)
						{
							tempAbandonedFinalAllBundleIdList.add(bundleIdWithPurchaseCountItem.getBundleId());							
							abandonedBundleIdWithPurchaseCountListAll.remove(bundleIdWithPurchaseCountItem.getBundleId());
							
							if(tempAbandonedFinalAllBundleIdList.size() >= 5)
							{
								break;
							}
						}
					}
					
					Collections.sort(abandonedBundleIdWithPurchaseCountListAll, new SortBundlesWithPurchaseRate());
					
					for(BundleIdWithPurchaseCount bundleIdWithPurchaseCountItem :abandonedBundleIdWithPurchaseCountListAll)
					{
						if(!tempAbandonedFinalAllBundleIdList.contains(bundleIdWithPurchaseCountItem.getBundleId()))
						{
							tempAbandonedFinalAllBundleIdList.add(bundleIdWithPurchaseCountItem.getBundleId());
						}
						
						if(tempAbandonedFinalAllBundleIdList.size() >= 30)
						{
							break;
						}
					}
				}
				
				for (int i=1; i<=maxNoOfBundles; i++)			
				{	
					for (long internalProductId : internalProductIdList)
					{
						// List<BundleIdWithPurchaseCount> bundleIdWithPurchaseCountList = ProductIdBundleListCache.getCache().get(internalProductId);
						
						List<BundleIdWithPurchaseCount> bundleIdWithPurchaseCountList = null;
						
						if(bundleRequest.getPageType().equals(CART_PAGE))
						{
							if(reqBundleTypeId == 13)
							{
								bundleIdWithPurchaseCountList = CartProductIdMLBundleListCache.getCache().get(internalProductId);	
							}
							else
							{
								if(reqBundleTypeId > 0)
								{
									bundleIdWithPurchaseCountList = CartProductIdBundleListWithBundleTypeCache.getCache().get(internalProductId+"_"+reqBundleTypeId);
								}
								else
								{
									bundleIdWithPurchaseCountList = CartProductIdBundleListCache.getCache().get(internalProductId);
								}
							}
						}
						else if(bundleRequest.getPageType().equals(CHECKOUT_PAGE))
						{
							if(reqBundleTypeId == 13)
							{
								bundleIdWithPurchaseCountList = CheckoutProductIdMLBundleListCache.getCache().get(internalProductId);	
							}
							else
							{
								if(reqBundleTypeId > 0)
								{
									bundleIdWithPurchaseCountList = CheckoutProductIdBundleListWithBundleTypeCache.getCache().get(internalProductId+"_"+reqBundleTypeId);
								}
								else
								{
									bundleIdWithPurchaseCountList = CheckoutProductIdBundleListCache.getCache().get(internalProductId);
								}									
							}
						}
												
						abandonedCartBundleIdWithPurchaseCountList = AbandonedCartBundleListCache.getCache().get(internalProductId);
						
						if(abandonedCartBundleIdWithPurchaseCountList != null)
						{
							tempAbandonedCartBundleIdListWithPurchaseCountForCartPage.put(internalProductId,abandonedCartBundleIdWithPurchaseCountList);
							Collections.sort(abandonedCartBundleIdWithPurchaseCountList, new SortBundlesWithPurchaseRate());
							
							for(BundleIdWithPurchaseCount abandonedCartBundleIdWithPurchaseCountItem :abandonedCartBundleIdWithPurchaseCountList)
							{
								if(tempAbandonedFinalAllBundleIdList.contains(abandonedCartBundleIdWithPurchaseCountItem.getBundleId()))
								{
									tempAbandonedCartBundleIdList.add(abandonedCartBundleIdWithPurchaseCountItem.getBundleId());
								}
							}
						}
						
						if(bundleIdWithPurchaseCountList != null)
						{
							tempBundleIdListWithPurchaseCountForCartPage.put(internalProductId,bundleIdWithPurchaseCountList);
							Collections.sort(bundleIdWithPurchaseCountList, new SortBundlesWithPurchaseRate());
							
							for(BundleIdWithPurchaseCount bundleIdWithPurchaseCountItem :bundleIdWithPurchaseCountList)
							{
								if(tempFinalAllBundleIdList.contains(bundleIdWithPurchaseCountItem.getBundleId()))
								{
									tempBundleIdList.add(bundleIdWithPurchaseCountItem.getBundleId());
									tempCartBundleIdList.add(bundleIdWithPurchaseCountItem.getBundleId());
								}
							}
											
							if(bundleRequest.getPageType().equals(CART_PAGE) || bundleRequest.getPageType().equals(CHECKOUT_PAGE))
							{
								if(tempCartBundleIdList.size() > 0 || tempAbandonedCartBundleIdList.size() > 0)
								{
							    	List<Long> otherInternalProductIdList = new ArrayList<Long>(); 
							    					    	
							    	for (long iProductId: internalProductIdList)
							    	{
							    		if(iProductId != internalProductId)
							    		{
							    		 otherInternalProductIdList.add(iProductId);
							    		}
							    	}
								 	
							    	for(Integer bundleId: finalBundleIdList)
									{
										if(tempCartBundleIdList.contains(bundleId))
										{
											tempCartBundleIdList.remove(bundleId);
										}
									}
							    	
							    	int remainingBundles = maxNoOfBundles-finalBundleIdList.size();
									int cartBundleId = chooseRelevantBundles.getCartBundleIdList(tempCartBundleIdList,internalProductId,otherInternalProductIdList,bundleConfiguration,bundleResult,bundleRequest,tempAbandonedCartBundleIdList,bundlesServedForProductList,remainingBundles);
									
																		
									if(!finalBundleIdList.contains(cartBundleId) && cartBundleId > 0)
									{
									  finalBundleIdList.add(cartBundleId);
									}
									
									tempCartBundleIdList.clear();
									
									if(finalBundleIdList.size() == maxNoOfBundles)
									{
										break;
									}
								}
							}
						}
					}
					if(finalBundleIdList.size() == maxNoOfBundles)
					{
						break;
					}
				}
			}
			
			if(tempBundleIdList.size() > 0 || tempAbandonedCartBundleIdList.size() > 0)
			{
				if(bundleRequest.getPageType().equals(PRODUCT_PAGE))
				{
					finalBundleIdList.addAll(chooseRelevantBundles.getProductPageBundleIdList(tempBundleIdList,bundleConfiguration,bundleRequest,internalProductIdList,bundleResult,tempAbandonedCartBundleIdList));
				}					
				
				/*if((bundleRequest.getPageType().equals(CART_PAGE) || bundleRequest.getPageType().equals(CHECKOUT_PAGE)) && finalBundleIdList.size() < (int)bundleConfiguration.getMaxNoOfBundlesForCartPage())
				{
					int bundlesRequired = ((int)bundleConfiguration.getMaxNoOfBundlesForCartPage() - finalBundleIdList.size());
					
					for(Integer bundleId: finalBundleIdList)
					{
						if(tempBundleIdList.contains(bundleId))
						{
						  tempBundleIdList.remove(bundleId);
						}
					}
					
					finalBundleIdList.addAll(chooseRelevantBundles.getCartRemainingBundleIdList(tempBundleIdList,internalProductIdList,bundleConfiguration,bundlesRequired,bundleResult,tempAbandonedCartBundleIdList,bundleRequest));
				}*/
			}
			
			if(bundleRequest.getPageType().equals(CART_PAGE))
			{
				if(tempBundleIdListWithPurchaseCountForCartPage.size() > 0)
				{
					for(Entry<Long, List<BundleIdWithPurchaseCount>> entry: tempBundleIdListWithPurchaseCountForCartPage.entrySet())
					{
						tempBundleIdListWithPurchaseCount.addAll(entry.getValue());
					}
					
				}
				if(tempAbandonedCartBundleIdListWithPurchaseCountForCartPage.size() > 0)
				{
					for(Entry<Long, List<BundleIdWithPurchaseCount>> entry: tempAbandonedCartBundleIdListWithPurchaseCountForCartPage.entrySet())
					{
						tempAbandonedCartBundleIdListWithPurchaseCount.addAll(entry.getValue());
					}
				}

			}
			if(tempBundleIdListWithPurchaseCount.size() > 0 && finalBundleIdList.size() > 0)
			{								
				for(Integer bundleId :finalBundleIdList)
				{
					if(tempBundleIdListWithPurchaseCount.size() > 0)
					{
						for(BundleIdWithPurchaseCount bundleIdWithPurchaseCountItem :tempBundleIdListWithPurchaseCount)
						{
							if(bundleIdWithPurchaseCountItem.getBundleId().equals(bundleId))
							{
								productBundleIdList.add(bundleIdWithPurchaseCountItem.getBundleId());
							}
						}
					}
					
					if(tempAbandonedCartBundleIdListWithPurchaseCount.size() > 0)
					{
						for(BundleIdWithPurchaseCount bundleIdWithPurchaseCountItem :tempAbandonedCartBundleIdListWithPurchaseCount)
						{
							if(bundleIdWithPurchaseCountItem.getBundleId().equals(bundleId))
							{
								productBundleIdList.add(bundleIdWithPurchaseCountItem.getBundleId());
							}
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
						productBundleIdList.addAll(getBackFillBundles(bundleConfiguration,maxNoOfBundles));
					}
				}
				else if(bundleConfiguration.isBackFillBundlesEnabled())
				{
					productBundleIdList.addAll(getBackFillBundles(bundleConfiguration,maxNoOfBundles));
				}
			}
				
			bundleResponse.getResponseProcessTimes().setTimeTakenToGetBundleProductIdList(processTimes.getTimeTaken(startDatetime));
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR ,"getCachedBundleIdList" , "Error getting bundle id list for internal product id","");
			logger.error(errorMessage, ex);
		}	
		
		return productBundleIdList;
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
package com.increasingly.recommender.impl;


import static com.increasingly.recommender.constants.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Properties;

import com.increasingly.recommender.utils.GetProperties;
import com.increasingly.recommender.impl.collections.BundleIdProductListCache;
import com.increasingly.recommender.impl.collections.RequestCountForCartBundleCache;
import com.increasingly.recommender.impl.collections.RequestCountForCartProductCache;
import com.increasingly.recommender.impl.collections.RequestCountForCategoryCache;
import com.increasingly.recommender.impl.collections.RequestCountForProductCache;

public class ChooseRelevantBundles
{
	private List<Long> productItemsInChoosenBundlesForCart = new ArrayList<Long>();

	public Integer getCartBundleIdList(List<Integer> bundleIdList,long internalProductId,List<Long> otherInternalProductIdList,
			BundleConfiguration bundleConfiguration,BundleResult bundleResult,BundleRequest bundleRequest,List<Integer> abandonedCartBundleIdList,List<Long> bundlesServedForProductList,int remainingBundles) throws Exception
	{			
		int finalBundleId = 0;
		
		List<Integer> allBundleIdList = new ArrayList<Integer>();
		allBundleIdList.addAll(bundleIdList);
		allBundleIdList.addAll(abandonedCartBundleIdList);
		
		BundleIdProductListService bundleIdProductListService = new BundleIdProductListService();
		bundleIdProductListService.getBundleIdProductList(allBundleIdList);
		
		allBundleIdList = null;
		
		List<Integer> validFpBundleIdList = new ArrayList<Integer>();
		List<Integer> validAbandonedBundleIdList = new ArrayList<Integer>();
		validFpBundleIdList = getValidBundleList(bundleIdList,bundleConfiguration,bundleRequest,internalProductId,otherInternalProductIdList);
		validAbandonedBundleIdList = getValidBundleList(abandonedCartBundleIdList,bundleConfiguration,bundleRequest,internalProductId,otherInternalProductIdList);
		List<Long> disabledRandomRotationProductIdList = bundleConfiguration.getDisabledRandomRotationProductList();
	
		boolean isRandomRotationAllowed = true;
		boolean isLogicalRotationAllowed = bundleConfiguration.isLogicalRotationEnabled();
		
	 	if(bundleConfiguration.isRandomRotationEnabled())
	 	{		 	
		 	if(disabledRandomRotationProductIdList!=null && disabledRandomRotationProductIdList.contains(internalProductId))
		 	{
		 		isRandomRotationAllowed = false;
		 	}
	 	}
	 	else
	 	{
	 		isRandomRotationAllowed = false;
	 	}
		 
	 	if(bundleConfiguration.isLogicalRotationEnabled())
	 	{		 	
		 	if(disabledRandomRotationProductIdList!=null && disabledRandomRotationProductIdList.contains(internalProductId))
		 	{
		 		isLogicalRotationAllowed = false;
		 	}
	 	}
	 	else
	 	{
	 		isLogicalRotationAllowed = false;
	 	}
	 	
	 	if(isLogicalRotationAllowed == true && isRandomRotationAllowed==true)
	 	{
	 		isRandomRotationAllowed = false;
	 	}
	 	
		if(isRandomRotationAllowed)
		{
			if(bundleConfiguration.getBundleSalesList() != null)
			{
				for(Integer bundleId : bundleConfiguration.getBundleSalesList())
				{
					if(validFpBundleIdList.contains(bundleId) || (bundleConfiguration.getShowAbandondedCartProductBundles() && validAbandonedBundleIdList.contains(bundleId)))
					{		
						finalBundleId = bundleId;				
						break;				
					}
				}
			}
	
			if(finalBundleId == 0 && validFpBundleIdList.size() > 0)
			{ 
				Random randomBundle = new Random();
				finalBundleId = validFpBundleIdList.get(randomBundle.nextInt(validFpBundleIdList.size()));			
			}
			
			if(finalBundleId > 0)
			{
				List<Long> productItemList = new ArrayList<Long>();
				productItemList = BundleIdProductListCache.getCache().get(finalBundleId);
				
				for(long pid : productItemList)
				{
					if(pid != internalProductId)
					{
						productItemsInChoosenBundlesForCart.add(pid);
					}
				}
			}

	    }
		else if(isLogicalRotationAllowed)
		{
			if(bundleConfiguration.getBundleSalesList() != null)
			{
				for(Integer bundleId : bundleConfiguration.getBundleSalesList())
				{
					if(validFpBundleIdList.contains(bundleId) || (bundleConfiguration.getShowAbandondedCartProductBundles() && validAbandonedBundleIdList.contains(bundleId)))
					{		
						finalBundleId = bundleId;				
						break;				
					}
				}
			}
			int bundlesServedCounter = 0;
			boolean isAlreadyInvalidated = false;

			if(finalBundleId == 0)
			{
				Integer requestImpressionCount = RequestCountForCartProductCache.getCache().get(internalProductId).get(1);		
				int bundleRequestCount = 0;
				if(requestImpressionCount > 0)
				{
					int noOfTimesBundleToBeServed = (int)Math.ceil((double)requestImpressionCount/((double)Configuration.getMaxFpBundlesForCartPage()/(double)bundleConfiguration.getMaxNoOfBundlesForCartPage()));
					List<String> bundlesToBeInvalidated = new ArrayList<String>();
					for(Integer bundleId : validFpBundleIdList)
					{
						List<Integer> requestCountForBundles = RequestCountForCartBundleCache.getCache().get(internalProductId+"_"+bundleId);
						if(requestCountForBundles != null)
						{
							int currentBundleRequest = requestCountForBundles.get(0);
							int maxBundleImpressions = requestCountForBundles.get(1);
							if(currentBundleRequest < maxBundleImpressions)
							{
								finalBundleId = bundleId;
								if(finalBundleId > 0)
								{
									currentBundleRequest++;
									finalBundleId = bundleId;
									requestCountForBundles.set(0, currentBundleRequest);
									RequestCountForCartBundleCache.getCache().put(internalProductId+"_"+bundleId, requestCountForBundles);
									if(!bundlesServedForProductList.contains(internalProductId))
									{
										List<Integer> requestCartProductImpressionCountCache = RequestCountForCartProductCache.getCache().get(internalProductId);
										int currentProductRequest = requestCartProductImpressionCountCache.get(0);
										currentProductRequest++;
										requestCartProductImpressionCountCache.set(0, currentProductRequest);
										RequestCountForCartProductCache.getCache().put(internalProductId,requestCartProductImpressionCountCache);										
									}
								}
								break;
							}
							else
							{
								bundlesServedCounter++;
								bundlesToBeInvalidated.add(internalProductId+"_"+bundleId);
								
								if(bundlesServedCounter > 0 && bundlesServedCounter == validFpBundleIdList.size() && remainingBundles > 0 && remainingBundles < (int)bundleConfiguration.getMaxNoOfBundlesForCartPage())
                                {
                                    for(int i=0;i < remainingBundles; i++)
                                    {
                                    	if(i < bundlesToBeInvalidated.size())
                                    	{
                                    		RequestCountForCartBundleCache.getCache().invalidate(bundlesToBeInvalidated.get(i));
                                    	}
                                    }   
                                    
                                    isAlreadyInvalidated = true;
                                    
                                    finalBundleId = validFpBundleIdList.get(0);
        							if(finalBundleId > 0)
        							{
        								bundleRequestCount++;
        								bundlesServedForProductList.add(internalProductId);	
        								requestCountForBundles = new ArrayList<Integer>();
        								requestCountForBundles.add(bundleRequestCount);
        								requestCountForBundles.add(noOfTimesBundleToBeServed);
        								RequestCountForCartBundleCache.getCache().put(internalProductId+"_"+finalBundleId, requestCountForBundles);
        								if(!bundlesServedForProductList.contains(internalProductId))
        								{
        									List<Integer> requestCartProductImpressionCountCache = RequestCountForCartProductCache.getCache().get(internalProductId);
        									requestCartProductImpressionCountCache.set(0, 1);
        									RequestCountForCartProductCache.getCache().put(internalProductId,requestCartProductImpressionCountCache);
        								}
        							}
        							break;
                                   
                                }
							}
						}
						else
						{	
							
							finalBundleId = bundleId;
							if(finalBundleId > 0)
							{
								bundleRequestCount++;
								bundlesServedForProductList.add(internalProductId);	
								requestCountForBundles = new ArrayList<Integer>();
								requestCountForBundles.add(bundleRequestCount);
								requestCountForBundles.add(noOfTimesBundleToBeServed);
								RequestCountForCartBundleCache.getCache().put(internalProductId+"_"+bundleId, requestCountForBundles);
								if(!bundlesServedForProductList.contains(internalProductId))
								{
									List<Integer> requestCartProductImpressionCountCache = RequestCountForCartProductCache.getCache().get(internalProductId);
									requestCartProductImpressionCountCache.set(0, 1);
									RequestCountForCartProductCache.getCache().put(internalProductId,requestCartProductImpressionCountCache);
								}
							}
							break;
							
						}						
					}	
					if(bundlesServedCounter == validFpBundleIdList.size() && !isAlreadyInvalidated) 
					{
						RequestCountForCartBundleCache.getCache().invalidateAll(bundlesToBeInvalidated);
					}
				}
				else
				{
					for(Integer bundleId : validFpBundleIdList)
					{
						finalBundleId = bundleId;				
						break;
					}
				}
				
				if(finalBundleId > 0)
				{
					List<Long> productItemList = new ArrayList<Long>();
					productItemList = BundleIdProductListCache.getCache().get(finalBundleId);
					
					for(long pid : productItemList)
					{
						if(pid != internalProductId)
						{
							productItemsInChoosenBundlesForCart.add(pid);
						}
					}
				}
				
			}
		}
		else
		{						
			for(Integer bundleId : validFpBundleIdList)
			{
				finalBundleId = bundleId;				
				break;
			}
			
			if(finalBundleId > 0)
			{
				List<Long> productItemList = new ArrayList<Long>();
				productItemList = BundleIdProductListCache.getCache().get(finalBundleId);
				
				for(long pid : productItemList)
				{
					if(pid != internalProductId)
					{
						productItemsInChoosenBundlesForCart.add(pid);
					}
				}
			}
		}
		
		return finalBundleId;				
	}
		
	public List<Integer> getCartRemainingBundleIdList(List<Integer> bundleIdList,List<Long> internalProductIdList,
			BundleConfiguration bundleConfiguration,int bundlesRequired,BundleResult bundleResult,List<Integer> abandonedCartBundleIdList,BundleRequest bundleRequest) throws Exception 
	{
		List<Integer> remainingBundleIdList = new ArrayList<Integer>();	
		
		for(long internalProductId : internalProductIdList)
		{
			List<Long> otherInternalProductIdList = new ArrayList<Long>(); 
	    	
	    	for (long iProductId: internalProductIdList)
	    	{
	    		if(iProductId != internalProductId)
	    		{
	    		 otherInternalProductIdList.add(iProductId);
	    		}
	    	}
	    	
	    	List<Integer> validFpBundleIdList = new ArrayList<Integer>();
			List<Integer> validAbandonedBundleIdList = new ArrayList<Integer>();
			validFpBundleIdList = getValidBundleList(bundleIdList,bundleConfiguration,bundleRequest,internalProductId,otherInternalProductIdList);
			validAbandonedBundleIdList = getValidBundleList(abandonedCartBundleIdList,bundleConfiguration,bundleRequest,internalProductId,otherInternalProductIdList);
		    List<Long> disabledRandomRotationProductIdList = bundleConfiguration.getDisabledRandomRotationProductList();
		    
		    boolean isRandomRotationAllowed = true;
		 	
		 	if(bundleConfiguration.isRandomRotationEnabled())
		 	{		 	
			 	if(disabledRandomRotationProductIdList!=null && disabledRandomRotationProductIdList.contains(internalProductId))
			 	{
			 		isRandomRotationAllowed = false;
			 	}
		 	}
		 	else
		 	{
		 		isRandomRotationAllowed = false;
		 	}
				
		if(isRandomRotationAllowed)
		{
			if(bundleConfiguration.getBundleSalesList() != null)
			{
		    	for(Integer bundleId : bundleConfiguration.getBundleSalesList())
				{
					if(validFpBundleIdList.contains(bundleId) || (bundleConfiguration.getShowAbandondedCartProductBundles() && validAbandonedBundleIdList.contains(bundleId)))
					{				   
					   List<Long> productItemList = new ArrayList<Long>();
					   productItemList = BundleIdProductListCache.getCache().get(bundleId);
						
						for(long pid : productItemList)
						{
							if(pid != internalProductId && !productItemsInChoosenBundlesForCart.contains(pid))
							{
								remainingBundleIdList.add(bundleId);
								productItemsInChoosenBundlesForCart.add(pid);
							}
						}
					}
					
					if(remainingBundleIdList.size() == bundlesRequired)
					{
					  break;
					}	
				}
			}
	    	
	    	Integer remainingBundles = (bundlesRequired - remainingBundleIdList.size());
	    	if(remainingBundles > 0)
	    	{
	    		Random randomBundle = new Random();
	    		
	    		if(validFpBundleIdList.size() > 0)
	    		{
		    		for (Integer i = 0; i < remainingBundles; i++)
					{
		    			Integer finalBundleId = validFpBundleIdList.get(randomBundle.nextInt(validFpBundleIdList.size()));	
		    			
		    			List<Long> productItemList = new ArrayList<Long>();
						productItemList = BundleIdProductListCache.getCache().get(finalBundleId);
							
						for(long pid : productItemList)
						{
							if(pid != internalProductId && !productItemsInChoosenBundlesForCart.contains(pid))
							{
								remainingBundleIdList.add(finalBundleId);
								productItemsInChoosenBundlesForCart.add(pid);
							}
						}
		    			
		    			if(remainingBundleIdList.size() == bundlesRequired)
						{
						  break;
						}	
					}
	    		}
	    		
	    		remainingBundles = (bundlesRequired - remainingBundleIdList.size());
	    		
	    		if(remainingBundles > 0)
	    		{
	    			for(Integer bundleId : validFpBundleIdList)
	    			{
	    				List<Long> productItemList = new ArrayList<Long>();
						productItemList = BundleIdProductListCache.getCache().get(bundleId);
							
						for(long pid : productItemList)
						{
							if(pid != internalProductId && !productItemsInChoosenBundlesForCart.contains(pid))
							{
								remainingBundleIdList.add(bundleId);
								productItemsInChoosenBundlesForCart.add(pid);
							}
						}
		    			
		    			if(remainingBundleIdList.size() == bundlesRequired)
						{
						  break;
						}
	    			}
	    		}
	    		
	    		remainingBundles = (bundlesRequired - remainingBundleIdList.size());
	    		
	    		if(remainingBundles > 0 && bundleConfiguration.getShowAbandondedCartProductBundles())
		    	{
	    			if(validAbandonedBundleIdList.size() > 0)
	    			{
		    			for (Integer i = 0; i < remainingBundles; i++)
						{
			    			Integer finalBundleId = validAbandonedBundleIdList.get(randomBundle.nextInt(validAbandonedBundleIdList.size()));	
			    			
			    			List<Long> productItemList = new ArrayList<Long>();
							productItemList = BundleIdProductListCache.getCache().get(finalBundleId);
								
							for(long pid : productItemList)
							{
								if(pid != internalProductId && !productItemsInChoosenBundlesForCart.contains(pid))
								{
									remainingBundleIdList.add(finalBundleId);
									productItemsInChoosenBundlesForCart.add(pid);
								}
							}
			    			
			    			if(remainingBundleIdList.size() == bundlesRequired)
							{
							  break;
							}	
						}
	    			}
	    			
	    			remainingBundles = (bundlesRequired - remainingBundleIdList.size());
		    		
		    		if(remainingBundles > 0)
		    		{
		    			for(Integer bundleId : validAbandonedBundleIdList)
		    			{
		    				List<Long> productItemList = new ArrayList<Long>();
							productItemList = BundleIdProductListCache.getCache().get(bundleId);
								
							for(long pid : productItemList)
							{
								if(pid != internalProductId && !productItemsInChoosenBundlesForCart.contains(pid))
								{
									remainingBundleIdList.add(bundleId);
									productItemsInChoosenBundlesForCart.add(pid);
								}
							}
			    			
			    			if(remainingBundleIdList.size() == bundlesRequired)
							{
							  break;
							}
		    			}
		    		}
		    	}	    		
	    	}
	    	else
	    	{
	    		break;
	    	}
		}
		else
		{
			for(Integer bundleId : validFpBundleIdList)
			{
				List<Long> productItemList = new ArrayList<Long>();
				productItemList = BundleIdProductListCache.getCache().get(bundleId);
					
				for(long pid : productItemList)
				{
					if(pid != internalProductId && !productItemsInChoosenBundlesForCart.contains(pid))
					{
						remainingBundleIdList.add(bundleId);
						productItemsInChoosenBundlesForCart.add(pid);
					}
				}
    			
    			if(remainingBundleIdList.size() == bundlesRequired)
				{
				  break;
				}
			}
    		
			int remainingBundles = (bundlesRequired - remainingBundleIdList.size());
    		
    		if(remainingBundles > 0)
    		{
    			for(Integer bundleId : validAbandonedBundleIdList)
    			{
    				List<Long> productItemList = new ArrayList<Long>();
					productItemList = BundleIdProductListCache.getCache().get(bundleId);
						
					for(long pid : productItemList)
					{
						if(pid != internalProductId && !productItemsInChoosenBundlesForCart.contains(pid))
						{
							remainingBundleIdList.add(bundleId);
							productItemsInChoosenBundlesForCart.add(pid);
						}
					}
	    			
	    			if(remainingBundleIdList.size() == bundlesRequired)
					{
					  break;
					}
    			}
    		}
    		else
    		{
    			break;
    		}
		    		
			}

		}
				
		return remainingBundleIdList;
	}
	
	public List<Integer> getProductPageBundleIdList(List<Integer> bundleIdList,BundleConfiguration bundleConfiguration,BundleRequest bundleRequest,List<Long> internalProductIdList,BundleResult bundleResult,List<Integer> abandonedCartBundleIdList) throws Exception
	{			
		List<Integer> finalBundleIdList = new ArrayList<Integer>();
		
		List<Integer> allBundleIdList = new ArrayList<Integer>();
		allBundleIdList.addAll(bundleIdList);
		allBundleIdList.addAll(abandonedCartBundleIdList);
		
		BundleIdProductListService bundleIdProductListService = new BundleIdProductListService();
		bundleIdProductListService.getBundleIdProductList(allBundleIdList);
		
		allBundleIdList = null;
		
		List<Integer> validFpBundleIdList = new ArrayList<Integer>();
		List<Integer> validAbandonedBundleIdList = new ArrayList<Integer>();
		validFpBundleIdList = getValidBundleList(bundleIdList,bundleConfiguration,bundleRequest,0,null);
		validAbandonedBundleIdList = getValidBundleList(abandonedCartBundleIdList,bundleConfiguration,bundleRequest,0,null);
	 	List<Long> disabledRandomRotationProductIdList = bundleConfiguration.getDisabledRandomRotationProductList();
		boolean isRandomRotationAllowed = true;
		boolean isLogicalRotationAllowed = true;
		
		int maxNoOfBundles = bundleRequest.getNoOfBundles();
		
		if(maxNoOfBundles == 0)
		{
			maxNoOfBundles = bundleConfiguration.getMaxNoOfBundlesForProductPage();
		}
		
	 	if(bundleConfiguration.isRandomRotationEnabled())
	 	{		 	
		 	if(disabledRandomRotationProductIdList!=null && disabledRandomRotationProductIdList.containsAll(internalProductIdList))
		 	{
		 		isRandomRotationAllowed = false;
		 	}
	 	}
	 	else
	 	{
	 		isRandomRotationAllowed = false;
	 	}
	 	 
	 	if(bundleConfiguration.isLogicalRotationEnabled())
	 	{		 	
		 	if(disabledRandomRotationProductIdList!=null && disabledRandomRotationProductIdList.containsAll(internalProductIdList))
		 	{
		 		isLogicalRotationAllowed = false;
		 	}
	 	}
	 	else
	 	{
	 		isLogicalRotationAllowed = false;
	 	}
	 	
	 	if(isLogicalRotationAllowed == true && isRandomRotationAllowed==true)
	 	{
	 		isRandomRotationAllowed = false;
	 	}
	 	
	 	List<Integer> requestImpressionCount = null;			
		requestImpressionCount = RequestCountForProductCache.getCache().get(internalProductIdList.get(0));
		
		if(requestImpressionCount == null || requestImpressionCount.get(0) == 0)
		{
			isLogicalRotationAllowed = false;
		}
		
	 	if(isRandomRotationAllowed || isLogicalRotationAllowed)
	 	{
	 		if(bundleConfiguration.getBundleSalesList() != null)
			{
				for(Integer bundleId : bundleConfiguration.getBundleSalesList())
				{
					if(validFpBundleIdList.contains(bundleId) || (bundleConfiguration.getShowAbandondedCartProductBundles() && validAbandonedBundleIdList.contains(bundleId)))
					{				
						if(!finalBundleIdList.contains(bundleId))
						{
							finalBundleIdList.add(bundleId);
							validFpBundleIdList.remove(bundleId);
							validAbandonedBundleIdList.remove(bundleId);
						}
						
						if(finalBundleIdList.size() == maxNoOfBundles)
						{
							break;
						} 				
					}
				}
			}
	 		
			if(isRandomRotationAllowed)
			{	
				int remainingBundles = maxNoOfBundles-finalBundleIdList.size();
		
				if(remainingBundles > 0)
				{
					Random randomBundle = new Random();
					
					if(validFpBundleIdList.size() > 0)
					{
						for (Integer i = 0; i < remainingBundles; i++)
						{
							if(validFpBundleIdList.size() > 0)
							{
								Integer bundleId = validFpBundleIdList.get(randomBundle.nextInt(validFpBundleIdList.size()));					
								
								if(!finalBundleIdList.contains(bundleId))
								{
									finalBundleIdList.add(bundleId);
									validFpBundleIdList.remove(bundleId);
								}
								
								if(finalBundleIdList.size() == maxNoOfBundles)
								{
									break;
								} 
								
							}
							else
							{
								break;
							}
						}
						
						remainingBundles = maxNoOfBundles-finalBundleIdList.size();
						
						if(remainingBundles > 0)
						{
							for(Integer bundleId : validFpBundleIdList)
							{
								if(!finalBundleIdList.contains(bundleId))
								{
									finalBundleIdList.add(bundleId);							
								}
								
								if(finalBundleIdList.size() == maxNoOfBundles)
								{
									break;
								} 
							}
						}
					}
											
					remainingBundles = maxNoOfBundles-finalBundleIdList.size();
					
					if(remainingBundles > 0 && bundleConfiguration.getShowAbandondedCartProductBundles() && validAbandonedBundleIdList.size() > 0)
					{		
						if(validAbandonedBundleIdList.size() > 0)
						{
							for (Integer i = 0; i < remainingBundles; i++)
							{
								if(validAbandonedBundleIdList.size() > 0)
								{
									Integer bundleId = validAbandonedBundleIdList.get(randomBundle.nextInt(validAbandonedBundleIdList.size()));					
									
									if(!finalBundleIdList.contains(bundleId))
									{
										finalBundleIdList.add(bundleId);
										validAbandonedBundleIdList.remove(bundleId);
									}
									
									if(finalBundleIdList.size() == maxNoOfBundles)
									{
										break;
									} 						
								}
								else
								{
									break;
								}					
							}
		
						}
						
						remainingBundles = maxNoOfBundles-finalBundleIdList.size();
						
						if(remainingBundles > 0)
						{
							for(Integer bundleId : validAbandonedBundleIdList)
							{
								if(!finalBundleIdList.contains(bundleId))
								{
									finalBundleIdList.add(bundleId);							
								}
								
								if(finalBundleIdList.size() == maxNoOfBundles)
								{
									break;
								} 
							}
						}
					}
				  }
		    }
			else if(isLogicalRotationAllowed)
			{	
				int remainingBundles = maxNoOfBundles-finalBundleIdList.size();
		
				int requestCount = requestImpressionCount.get(1);
				requestCount++;
				
				//if(requestCount > requestImpressionCount.get(0)){
				//	requestCount = 1;
				//}
				
				if(remainingBundles > 0 && requestImpressionCount.get(0) > 0)
				{
					// Main logic for logical rotation
					int noOfBundleSets = (int) Math.ceil((double)validFpBundleIdList.size()/(double)remainingBundles);
					
					if(noOfBundleSets != 0)
					{
						int maxRequestsPerSet = (int) Math.ceil((double)requestImpressionCount.get(0)/(double)noOfBundleSets);
						
						int currentBundleSet = (int) Math.ceil((double)requestCount/(double)maxRequestsPerSet);
						
						if(currentBundleSet > noOfBundleSets)
						{
							currentBundleSet = currentBundleSet%noOfBundleSets;
						}
						
						if(currentBundleSet <= 0)
						{
							currentBundleSet = 1;
						}
						
						
						for(int i = (currentBundleSet-1)*remainingBundles; i < (currentBundleSet*remainingBundles); i++)
						{
							if(i < validFpBundleIdList.size())
							{
								finalBundleIdList.add(validFpBundleIdList.get(i));
							}
							
							if(finalBundleIdList.size() == maxNoOfBundles)
							{
								break;
							} 
						}
					}
				}
				
				remainingBundles = maxNoOfBundles-finalBundleIdList.size();
				
				if(remainingBundles > 0 && requestImpressionCount.get(0) > 0)
				{	
					// Main logic for logical rotation
					int noOfBundleSets = (int) Math.ceil((double)validAbandonedBundleIdList.size()/(double)remainingBundles);
					
					if(noOfBundleSets != 0)
					{
						int maxRequestsPerSet = (int) Math.ceil((double)requestImpressionCount.get(0)/(double)noOfBundleSets);
						
						int currentBundleSet = (int) Math.ceil((double)requestCount/(double)maxRequestsPerSet);
						
						if(currentBundleSet > noOfBundleSets)
						{
							currentBundleSet = currentBundleSet%noOfBundleSets;
						}
						
						if(currentBundleSet <= 0)
						{
							currentBundleSet = 1;
						}
						
						for(int i = (currentBundleSet-1)*remainingBundles; i < (currentBundleSet*remainingBundles); i++)
						{
							
							if(i < validAbandonedBundleIdList.size())
							{
								finalBundleIdList.add(validAbandonedBundleIdList.get(i));
							}
							
							if(finalBundleIdList.size() == maxNoOfBundles)
							{
								break;
							} 
						}			
					}		
				}
				requestImpressionCount.set(1, requestCount); 
				RequestCountForProductCache.getCache().put(internalProductIdList.get(0),requestImpressionCount);
				
				remainingBundles = maxNoOfBundles-finalBundleIdList.size();
				
				if(remainingBundles > 0)
				{
					for(Integer bundleId : validFpBundleIdList)
					{
						if(!finalBundleIdList.contains(bundleId))
						{
							finalBundleIdList.add(bundleId);							
						}
						
						if(finalBundleIdList.size() == maxNoOfBundles)
						{
							break;
						} 
					}
				}
		    }
			else
			{
				for(Integer bundleId : validFpBundleIdList)
				{
					if(!finalBundleIdList.contains(bundleId))
					{
						finalBundleIdList.add(bundleId);							
					}
					
					if(finalBundleIdList.size() == maxNoOfBundles)
					{
						break;
					} 
				}
				
				int remainingBundles = maxNoOfBundles-finalBundleIdList.size();
				
				if(remainingBundles > 0)
				{
					for(Integer bundleId : validAbandonedBundleIdList)
					{
						if(!finalBundleIdList.contains(bundleId))
						{
							finalBundleIdList.add(bundleId);							
						}
						
						if(finalBundleIdList.size() == maxNoOfBundles)
						{
							break;
						} 
					}
				}
			}
	 	}
	 	else
	 	{

			for(Integer bundleId : validFpBundleIdList)
			{
				if(!finalBundleIdList.contains(bundleId))
				{
					finalBundleIdList.add(bundleId);							
				}
				
				if(finalBundleIdList.size() == maxNoOfBundles)
				{
					break;
				} 
			}
			
			int remainingBundles = maxNoOfBundles-finalBundleIdList.size();
			
			if(remainingBundles > 0)
			{
				for(Integer bundleId : validAbandonedBundleIdList)
				{
					if(!finalBundleIdList.contains(bundleId))
					{
						finalBundleIdList.add(bundleId);							
					}
					
					if(finalBundleIdList.size() == maxNoOfBundles)
					{
						break;
					} 
				}
			}
		
	 	}
		return finalBundleIdList;				
	}
		
	public List<Integer> getCategoryPageBundleIdList(List<Integer> bundleIdList,BundleConfiguration bundleConfiguration,BundleResult bundleResult,List<Integer> abandonedCartBundleIdList,BundleRequest bundleRequest,int maxNoOfBundles) throws Exception
	{
		List<Integer> finalBundleIdList = new ArrayList<Integer>();
		
		List<Integer> allBundleIdList = new ArrayList<Integer>();
		allBundleIdList.addAll(bundleIdList);
		allBundleIdList.addAll(abandonedCartBundleIdList);
		
		BundleIdProductListService bundleIdProductListService = new BundleIdProductListService();
		bundleIdProductListService.getBundleIdProductList(allBundleIdList);
		
		allBundleIdList = null;
		
		List<Integer> validFpBundleIdList = new ArrayList<Integer>();
		List<Integer> validAbandonedBundleIdList = new ArrayList<Integer>();
		validFpBundleIdList = getValidBundleList(bundleIdList,bundleConfiguration,bundleRequest,0,null);
		validAbandonedBundleIdList = getValidBundleList(abandonedCartBundleIdList,bundleConfiguration,bundleRequest,0,null);
		boolean isRandomRotationAllowed = bundleConfiguration.isRandomRotationEnabled();
		boolean isLogicalRotationAllowed = bundleConfiguration.isLogicalRotationEnabled();
		List<Integer> internalCategoryIds = bundleResult.getInternalCategoryList();
		
		if(isLogicalRotationAllowed == true && isRandomRotationAllowed==true)
	 	{
	 		isRandomRotationAllowed = false;
	 	}
		
		List<Integer> requestImpressionCount = null;		
		requestImpressionCount = RequestCountForCategoryCache.getCache().get(internalCategoryIds.get(0));
		
		if(requestImpressionCount == null || requestImpressionCount.get(0) == 0)
		{
			isLogicalRotationAllowed = false;
		}

		if(isRandomRotationAllowed || isLogicalRotationAllowed)
		{
			if(bundleConfiguration.getBundleSalesList() != null)
			{
				for(Integer bundleId : bundleConfiguration.getBundleSalesList())
				{
					if(validFpBundleIdList.contains(bundleId) || (bundleConfiguration.getShowAbandondedCartProductBundles() && validAbandonedBundleIdList.contains(bundleId)))
					{				
						if(!finalBundleIdList.contains(bundleId))
						{
							finalBundleIdList.add(bundleId);
							validFpBundleIdList.remove(bundleId);
							validAbandonedBundleIdList.remove(bundleId);
						}
							
						if(finalBundleIdList.size() == maxNoOfBundles)
						{
							break;
						} 
						
					}
				}
			}
			
			if(isRandomRotationAllowed)
			{	
				int remainingBundles = maxNoOfBundles-finalBundleIdList.size();

				if(remainingBundles > 0)
				{
					Random randomBundle = new Random();
					
					if(validFpBundleIdList.size() > 0)
					{
						for (Integer i = 0; i < remainingBundles; i++)
						{
							if(validFpBundleIdList.size() > 0)
							{
								Integer bundleId = validFpBundleIdList.get(randomBundle.nextInt(validFpBundleIdList.size()));					
								
								if(!finalBundleIdList.contains(bundleId))
								{
									finalBundleIdList.add(bundleId);
									validFpBundleIdList.remove(bundleId);
								}
								
								if(finalBundleIdList.size() == maxNoOfBundles)
								{
									break;
								} 
								
							}
							else
							{
								break;
							}
						}
						
						remainingBundles = maxNoOfBundles-finalBundleIdList.size();
						
						if(remainingBundles > 0)
						{
							for(Integer bundleId : validFpBundleIdList)
							{
								if(!finalBundleIdList.contains(bundleId))
								{
									finalBundleIdList.add(bundleId);							
								}
								
								if(finalBundleIdList.size() == maxNoOfBundles)
								{
									break;
								} 
							}
						}
					}
											
					remainingBundles = maxNoOfBundles-finalBundleIdList.size();
					
					if(remainingBundles > 0 && bundleConfiguration.getShowAbandondedCartProductBundles() && validAbandonedBundleIdList.size() > 0)
					{		
						if(validAbandonedBundleIdList.size() > 0)
						{
							for (Integer i = 0; i < remainingBundles; i++)
							{
								if(validAbandonedBundleIdList.size() > 0)
								{
									Integer bundleId = validAbandonedBundleIdList.get(randomBundle.nextInt(validAbandonedBundleIdList.size()));					
									
									if(!finalBundleIdList.contains(bundleId))
									{
										finalBundleIdList.add(bundleId);
										validAbandonedBundleIdList.remove(bundleId);
									}
									
									if(finalBundleIdList.size() == maxNoOfBundles)
									{
										break;
									} 						
								}
								else
								{
									break;
								}					
							}
						}
						
						remainingBundles = maxNoOfBundles-finalBundleIdList.size();
						
						if(remainingBundles > 0)
						{
							for(Integer bundleId : validAbandonedBundleIdList)
							{
								if(!finalBundleIdList.contains(bundleId))
								{
									finalBundleIdList.add(bundleId);							
								}
								
								if(finalBundleIdList.size() == maxNoOfBundles)
								{
									break;
								} 
							}
						}
					}
				}
			}
			
			else if (isLogicalRotationAllowed)
			{	
				int remainingBundles = maxNoOfBundles-finalBundleIdList.size();
		
				int requestCount = requestImpressionCount.get(1);
				requestCount++;

				//if(requestCount > requestImpressionCount.get(0)){
				//	requestCount = 1;
				//}
				
				if(remainingBundles > 0 && requestImpressionCount.get(0) > 0)
				{
					// Main logic for logical rotation
					int noOfBundleSets = (int) Math.ceil((double)validFpBundleIdList.size()/(double)remainingBundles);
					
					if(noOfBundleSets != 0)
					{
						int maxRequestsPerSet = (int) Math.ceil((double)requestImpressionCount.get(0)/(double)noOfBundleSets);
						
						int currentBundleSet = (int) Math.ceil((double)requestCount/(double)maxRequestsPerSet);
						
						if(currentBundleSet > noOfBundleSets)
						{
							currentBundleSet = currentBundleSet%noOfBundleSets;
						}
						
						if(currentBundleSet <= 0)
						{
							currentBundleSet = 1;
						}
						
						for(int i = (currentBundleSet-1)*remainingBundles; i < (currentBundleSet*remainingBundles); i++)
						{
							
							if(i < validFpBundleIdList.size())
							{
								finalBundleIdList.add(validFpBundleIdList.get(i));
							}
							
							if(finalBundleIdList.size() == maxNoOfBundles)
							{
								break;
							} 
						}
					}
				}
				
				remainingBundles = maxNoOfBundles-finalBundleIdList.size();
				
				if(remainingBundles > 0 && requestImpressionCount.get(0) > 0)
				{	
					// Main logic for logical rotation
					int noOfBundleSets = (int) Math.ceil((double)validAbandonedBundleIdList.size()/(double)remainingBundles);
					
					if(noOfBundleSets != 0)
					{
						int maxRequestsPerSet =  (int) Math.ceil((double)requestImpressionCount.get(0)/(double)noOfBundleSets);
											
						int currentBundleSet = (int) Math.ceil((double)requestCount/(double)maxRequestsPerSet);
						
						if(currentBundleSet > noOfBundleSets)
						{
							currentBundleSet = currentBundleSet%noOfBundleSets;
						}
						
						if(currentBundleSet <= 0)
						{
							currentBundleSet = 1;
						}
						
						for(int i = (currentBundleSet-1)*remainingBundles; i < (currentBundleSet*remainingBundles); i++)
						{
							if(i < validAbandonedBundleIdList.size())
							{
								finalBundleIdList.add(validAbandonedBundleIdList.get(i));
							}							
							
							if(finalBundleIdList.size() == maxNoOfBundles)
							{
								break;
							} 
						}			
					}		
				}
				requestImpressionCount.set(1, requestCount); 
				RequestCountForCategoryCache.getCache().put(internalCategoryIds.get(0),requestImpressionCount);
		    }
			else
			{
				for(Integer bundleId : validFpBundleIdList)
				{
					if(!finalBundleIdList.contains(bundleId))
					{
						finalBundleIdList.add(bundleId);							
					}
					
					if(finalBundleIdList.size() == maxNoOfBundles)
					{
						break;
					} 
				}
				
				int remainingBundles = maxNoOfBundles-finalBundleIdList.size();
				
				if(remainingBundles > 0)
				{
					for(Integer bundleId : validAbandonedBundleIdList)
					{
						if(!finalBundleIdList.contains(bundleId))
						{
							finalBundleIdList.add(bundleId);							
						}
						
						if(finalBundleIdList.size() == maxNoOfBundles)
						{
							break;
						} 
					}
				}
			
			}
		}
		else
		{

			for(Integer bundleId : validFpBundleIdList)
			{
				if(!finalBundleIdList.contains(bundleId))
				{
					finalBundleIdList.add(bundleId);							
				}
				
				if(finalBundleIdList.size() == maxNoOfBundles)
				{
					break;
				} 
			}
			
			int remainingBundles = maxNoOfBundles-finalBundleIdList.size();
			
			if(remainingBundles > 0)
			{
				for(Integer bundleId : validAbandonedBundleIdList)
				{
					if(!finalBundleIdList.contains(bundleId))
					{
						finalBundleIdList.add(bundleId);							
					}
					
					if(finalBundleIdList.size() == maxNoOfBundles)
					{
						break;
					} 
				}
			}
		
		
		}
		return finalBundleIdList;				
	}
	
	private List<Integer> getValidBundleList(List<Integer> bundleIdList,BundleConfiguration bundleConfiguration,BundleRequest bundleRequest,long internalProductId,List<Long> otherInternalProductIdList)
	{
		List<Integer> validBundleIdList = new ArrayList<Integer>();
		
		for(Integer bundleId: bundleIdList)
		{
			List<Long> productItemList = new ArrayList<Long>();
			productItemList = BundleIdProductListCache.getCache().get(bundleId);
			
			if(bundleRequest.getPageType().equals(PRODUCT_PAGE))
			{				
				if(productItemList != null && productItemList.size() > 1 && 
						((bundleRequest.getNoOfProductsInBundle() > 0 && productItemList.size() == bundleRequest.getNoOfProductsInBundle() && productItemList.size() <= (int)bundleConfiguration.getMaxNoOfProductInProductPageBundle()) 
								|| (bundleRequest.getNoOfProductsInBundle() == 0 && productItemList.size() >= (int)bundleConfiguration.getMinNoOfProductInProductPageBundle() && productItemList.size() <= (int)bundleConfiguration.getMaxNoOfProductInProductPageBundle())))
				{
					validBundleIdList.add(bundleId);
				}
			}
			else if(bundleRequest.getPageType().equals(CART_PAGE) || bundleRequest.getPageType().equals(CHECKOUT_PAGE))
			{
				boolean isCartProductInBundle = false;
				
				if(productItemList != null && productItemList.size() > 1 && productItemList.contains(internalProductId) && productItemList.size() >= (int)bundleConfiguration.getMinNoOfProductInCartPageBundle() && productItemList.size() <= (int)bundleConfiguration.getMaxNoOfProductInCartPageBundle())
				{
					for(long productId : productItemList)
					{
						if(otherInternalProductIdList.contains(productId) || productItemsInChoosenBundlesForCart.contains(productId))
						{
							isCartProductInBundle = true;
							break;
						}
					}
				}			
							
				if(!isCartProductInBundle && productItemList.size() > 1 && productItemList.contains(internalProductId) && productItemList.size() >= (int)bundleConfiguration.getMinNoOfProductInCartPageBundle() && productItemList.size() <= (int)bundleConfiguration.getMaxNoOfProductInCartPageBundle())
				{			
					validBundleIdList.add(bundleId);
				}
				
			}
			else if(bundleRequest.getPageType().equals(CATEGORY_PAGE))
			{			
				if(productItemList != null && productItemList.size() > 1 && productItemList.size() >= (int)bundleConfiguration.getMinNoOfProductInCategoryPageBundle()
						&& productItemList.size() <= (int)bundleConfiguration.getMaxNoOfProductInCategoryPageBundle())
				{
					validBundleIdList.add(bundleId);
				}
			}
		}
		
		
		return validBundleIdList;
	}

}

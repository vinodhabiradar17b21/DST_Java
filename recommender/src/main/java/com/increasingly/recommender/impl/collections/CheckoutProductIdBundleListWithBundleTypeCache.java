package com.increasingly.recommender.impl.collections;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

import com.increasingly.recommender.impl.BundleIdWithPurchaseCount;
import com.increasingly.recommender.impl.Configuration;

public class CheckoutProductIdBundleListWithBundleTypeCache
{	
	private static final CheckoutProductIdBundleListWithBundleTypeCache instance = new CheckoutProductIdBundleListWithBundleTypeCache();	

	// Cache Object
	private Cache<String, List<BundleIdWithPurchaseCount>> cacheCheckoutProductIdBundleList = CacheBuilder.newBuilder().maximumSize(Configuration.getProductIdBundleListCacheMaxSize())
																	.expireAfterWrite(Configuration.getCacheTime(), TimeUnit.MINUTES).build();

	/**
	 * private constructor.
	 */
	private CheckoutProductIdBundleListWithBundleTypeCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the ProductIdBundleListCache object.
	 */
	public static CheckoutProductIdBundleListWithBundleTypeCache getCache()
	{
		return instance;
	}
	
	/**cacheProductIdBundleList
	 * Gets the BundleIdList object matching the internalProductId
	 * 
	 * @param internalProductId
	 * @return BundleIdList
	 */
	public List<BundleIdWithPurchaseCount> get(String internalProductId)
	{
		return cacheCheckoutProductIdBundleList.getIfPresent(internalProductId);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param internalProductId
	 * @param bundleIdList
	 */
	public void put(String internalProductId, List<BundleIdWithPurchaseCount> bundleIdList)
	{
		cacheCheckoutProductIdBundleList.put(internalProductId, bundleIdList);
	}
}
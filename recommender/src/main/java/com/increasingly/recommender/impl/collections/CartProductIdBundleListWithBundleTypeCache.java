package com.increasingly.recommender.impl.collections;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

import com.increasingly.recommender.impl.BundleIdWithPurchaseCount;
import com.increasingly.recommender.impl.Configuration;

public class CartProductIdBundleListWithBundleTypeCache
{	
	private static final CartProductIdBundleListWithBundleTypeCache instance = new CartProductIdBundleListWithBundleTypeCache();	

	// Cache Object
	private Cache<String, List<BundleIdWithPurchaseCount>> cacheCartProductIdBundleList = CacheBuilder.newBuilder().maximumSize(Configuration.getProductIdBundleListCacheMaxSize())
																	.expireAfterWrite(Configuration.getCacheTime(), TimeUnit.MINUTES).build();

	/**
	 * private constructor.
	 */
	private CartProductIdBundleListWithBundleTypeCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the ProductIdBundleListCache object.
	 */
	public static CartProductIdBundleListWithBundleTypeCache getCache()
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
		return cacheCartProductIdBundleList.getIfPresent(internalProductId);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param internalProductId
	 * @param bundleIdList
	 */
	public void put(String internalProductId, List<BundleIdWithPurchaseCount> bundleIdList)
	{
		cacheCartProductIdBundleList.put(internalProductId, bundleIdList);
	}
}
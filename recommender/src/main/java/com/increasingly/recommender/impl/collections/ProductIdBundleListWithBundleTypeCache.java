package com.increasingly.recommender.impl.collections;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

import com.increasingly.recommender.impl.BundleIdWithPurchaseCount;
import com.increasingly.recommender.impl.Configuration;

public class ProductIdBundleListWithBundleTypeCache
{	
	private static final ProductIdBundleListWithBundleTypeCache instance = new ProductIdBundleListWithBundleTypeCache();	

	// Cache Object
	private Cache<String, List<BundleIdWithPurchaseCount>> cacheProductIdBundleList = CacheBuilder.newBuilder().maximumSize(Configuration.getProductIdBundleListCacheMaxSize())
																	.expireAfterWrite(Configuration.getCacheTime(), TimeUnit.MINUTES).build();

	/**
	 * private constructor.
	 */
	private ProductIdBundleListWithBundleTypeCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the ProductIdBundleListCache object.
	 */
	public static ProductIdBundleListWithBundleTypeCache getCache()
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
		return cacheProductIdBundleList.getIfPresent(internalProductId);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param internalProductId
	 * @param bundleIdList
	 */
	public void put(String internalProductId, List<BundleIdWithPurchaseCount> bundleIdList)
	{
		cacheProductIdBundleList.put(internalProductId, bundleIdList);
	}
}
package com.increasingly.recommender.impl.collections;

import java.util.List;
import java.util.concurrent.TimeUnit;
import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;
import com.increasingly.recommender.impl.Configuration;

public class BundleIdProductListCache
{
	private static final BundleIdProductListCache instance = new BundleIdProductListCache();	

	// Cache Object
	private Cache<Integer, List<Long>> cacheBundleIdProductList = CacheBuilder.newBuilder().maximumSize(Configuration.getBundleIdProductListCacheMaxSize())
																	.expireAfterWrite(Configuration.getCacheTime(), TimeUnit.MINUTES).build();

	/**
	 * private constructor.
	 */
	private BundleIdProductListCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the BundleIdProductListCache object.
	 */
	public static BundleIdProductListCache getCache()
	{
		return instance;
	}
	
	/**
	 * Gets the List of Products for a particular bundleId 
	 * 
	 * @param objKey
	 * @return Integer
	 */
	public List<Long> get(Integer bundleId)
	{ 
		return cacheBundleIdProductList.getIfPresent(bundleId);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param key
	 */
	public void put(Integer bundleId,List<Long> internalProductIdList)
	{
		cacheBundleIdProductList.put(bundleId, internalProductIdList);
	}
}
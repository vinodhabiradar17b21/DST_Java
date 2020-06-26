package com.increasingly.recommender.impl.collections;

import java.util.concurrent.TimeUnit;

import com.increasingly.recommender.impl.Configuration;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;


public class ProductIdCache
{
	private static final ProductIdCache instance = new ProductIdCache();

	// Cache Object
	private Cache<String, Long> cacheProductId = CacheBuilder.newBuilder().maximumSize(Configuration.getProductIdCacheMaxSize())
												.expireAfterWrite(Configuration.getCacheTime(), TimeUnit.MINUTES).build();

	private ProductIdCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the ProductIdCache object.
	 */
	public static ProductIdCache getCache()
	{
		return instance;
	}

	/**
	 * Gets the internalproductId matching the key
	 * 
	 * @param objKey
	 * @return Long
	 */
	public Long get(String key)
	{
		return cacheProductId.getIfPresent(key);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param key
	 *            ,productId
	 */
	public void put(String key, Long productId)
	{
		cacheProductId.put(key, productId);
	}
}
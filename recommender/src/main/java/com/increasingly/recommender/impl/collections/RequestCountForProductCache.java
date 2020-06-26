package com.increasingly.recommender.impl.collections;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

import com.increasingly.recommender.impl.Configuration;

public class RequestCountForProductCache
{
	private static final RequestCountForProductCache instance = new RequestCountForProductCache();	

	// Cache Object
	private Cache<Long, List<Integer>> requestCountForProduct = CacheBuilder.newBuilder().maximumSize(Configuration.getRequestCountCacheMaxSize())
																	.expireAfterWrite(Configuration.getCacheTimeForImpressions(), TimeUnit.DAYS).build();

	/**
	 * private constructor.
	 */
	private RequestCountForProductCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the RequestCountForProductCache object.
	 */
	public static RequestCountForProductCache getCache()
	{
		return instance;
	}
	
	/**CacheRequestCount
	 * Gets the requestCountForProduct object matching the internalProductId
	 * 
	 * @param internalProductId
	 * @return requestCountForProduct
	 */
	public List<Integer> get(Long internalProductId)
	{
		return requestCountForProduct.getIfPresent(internalProductId);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param internalProductId
	 * @param requestCountForProduct
	 */
	public void put(Long internalProductId, List<Integer> requestCount)
	{
		requestCountForProduct.put(internalProductId, requestCount);
	}
}
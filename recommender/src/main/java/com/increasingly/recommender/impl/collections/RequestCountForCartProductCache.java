package com.increasingly.recommender.impl.collections;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;
import jersey.repackaged.com.google.common.cache.CacheStats;
import jersey.repackaged.com.google.common.collect.ImmutableMap;

import com.increasingly.recommender.impl.Configuration;
 
public class RequestCountForCartProductCache
{
	private static final RequestCountForCartProductCache instance = new RequestCountForCartProductCache();	

	// Cache Object
	private Cache<Long, List<Integer>> requestCountForCartProduct = CacheBuilder.newBuilder().maximumSize(Configuration.getRequestCountCacheMaxSize())
																	.expireAfterWrite(Configuration.getCacheTimeForImpressions(), TimeUnit.DAYS).build();

	/**
	 * private constructor.
	 */
	private RequestCountForCartProductCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the requestCountForCartProductCache object.
	 */
	public static RequestCountForCartProductCache getCache()
	{
		return instance;
	}
	
	/**CacheRequestCount
	 * Gets the requestCountForCartProduct object matching the internalProductId
	 * 
	 * @param internalProductId
	 * @return requestCountForCartProduct
	 */
	public List<Integer> get(Long internalProductId)
	{
		return requestCountForCartProduct.getIfPresent(internalProductId);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param internalProductId
	 * @param requestCountForCartProduct
	 */
	public void put(Long internalProductId, List<Integer> requestCount)
	{
		requestCountForCartProduct.put(internalProductId, requestCount);
	}


	public void invalidateAll() {
		requestCountForCartProduct.invalidateAll();
	}
}
package com.increasingly.recommender.impl.collections;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

import com.increasingly.recommender.impl.Configuration;

public class RequestCountForCategoryCache
{
	private static final RequestCountForCategoryCache instance = new RequestCountForCategoryCache();	

	// Cache Object
	private Cache<Integer, List<Integer>> requestCountForCategory = CacheBuilder.newBuilder().maximumSize(Configuration.getRequestCountCacheMaxSize())
																	.expireAfterWrite(Configuration.getCacheTimeForImpressions(), TimeUnit.DAYS).build();

	/**
	 * private constructor.
	 */
	private RequestCountForCategoryCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the RequestCountForCategoryCache object.
	 */
	public static RequestCountForCategoryCache getCache()
	{
		return instance;
	}
	
	/**CacheRequestCount
	 * Gets the requestCountForCategory object matching the internalCategoryId
	 * 
	 * @param internalCategoryId
	 * @return requestCountForCategory
	 */
	public List<Integer> get(Integer internalCategoryId)
	{
		return requestCountForCategory.getIfPresent(internalCategoryId);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param internalCategoryId
	 * @param requestCountForCategory
	 */
	public void put(Integer internalCategoryId, List<Integer> requestCount)
	{
		requestCountForCategory.put(internalCategoryId, requestCount);
	}
}
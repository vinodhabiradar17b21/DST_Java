package com.increasingly.recommender.impl.collections;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

import com.increasingly.recommender.impl.Configuration;

public class RequestCountForCartBundleCache
{
	private static final RequestCountForCartBundleCache instance = new RequestCountForCartBundleCache();	

	// Cache Object
	private Cache<String, List<Integer>> requestCountForCartProduct = CacheBuilder.newBuilder().maximumSize(Configuration.getRequestCountCacheMaxSize())
																	.expireAfterWrite(Configuration.getCacheTimeForImpressions(), TimeUnit.DAYS).build();

	/**
	 * private constructor.
	 */
	private RequestCountForCartBundleCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the RequestCountForCartBundleCache object.
	 */
	public static RequestCountForCartBundleCache getCache()
	{
		return instance;
	}
	
	/**CacheRequestCount
	 * Gets the requestCountForCartProduct object matching the internalProductId
	 * 
	 * @param internalProductId
	 * @return requestCountForCartProduct
	 */
	public List<Integer> get(String internalProductId)
	{
		return requestCountForCartProduct.getIfPresent(internalProductId);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param internalProductId
	 * @param requestCountForCartProduct
	 */
	public void put(String internalProductId, List<Integer> requestCount)
	{
		requestCountForCartProduct.put(internalProductId, requestCount);
	}
	
	public void invalidateAll(List<String> bundlesToBeInvalidated) {
		requestCountForCartProduct.invalidateAll(bundlesToBeInvalidated);
	}
	
	public void invalidate(String bundleToBeInvalidated) {
		requestCountForCartProduct.invalidate(bundleToBeInvalidated);
	}
}
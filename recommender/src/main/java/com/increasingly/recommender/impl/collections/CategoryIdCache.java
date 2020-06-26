package com.increasingly.recommender.impl.collections;

import java.util.concurrent.TimeUnit;

import com.increasingly.recommender.impl.Configuration;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;


public class CategoryIdCache
{
	private static final CategoryIdCache instance = new CategoryIdCache();
	
	// Cache object
	private Cache<String, Integer> cacheCategory = CacheBuilder.newBuilder().maximumSize(Configuration.getCategoryIdCacheMaxSize())
													.expireAfterWrite(Configuration.getLongCacheTime(), TimeUnit.MINUTES).build();
	
	private CategoryIdCache()
	{		
		
	}

	/**
	 * Gets the singleton instance of the object.
	 */
	public static CategoryIdCache getCache()
	{
		return instance;
	}

	/**
	 * Gets the internalcategoryId matching the key
	 * 
	 * @param objKey
	 * @return Integer
	 */
	public Integer get(String key)
	{ 
		return cacheCategory.getIfPresent(key);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param key
	 * @param categoryId
	 */
	public void put(String key, Integer categoryId)
	{
		cacheCategory.put(key, categoryId);
	}
		
}
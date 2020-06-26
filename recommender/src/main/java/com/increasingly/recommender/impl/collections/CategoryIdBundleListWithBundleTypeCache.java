package com.increasingly.recommender.impl.collections;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

import com.increasingly.recommender.impl.BundleIdWithPurchaseCount;
import com.increasingly.recommender.impl.Configuration;


public class CategoryIdBundleListWithBundleTypeCache
{
	private static final CategoryIdBundleListWithBundleTypeCache instance = new CategoryIdBundleListWithBundleTypeCache();	

	// Cache Object
	private Cache<String, List<BundleIdWithPurchaseCount>> cacheCategoryIdBundleList = CacheBuilder.newBuilder().maximumSize(Configuration.getCategoryIdBundleListCacheMaxSize())
																	.expireAfterWrite(Configuration.getCacheTime(), TimeUnit.MINUTES).build();

	/**
	 * private constructor.
	 */
	private CategoryIdBundleListWithBundleTypeCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the CategoryIdBundleListCache object.
	 */
	public static CategoryIdBundleListWithBundleTypeCache getCache()
	{
		return instance;
	}
	
	/**
	 * cacheCategoryIdBundleList
	 * Gets the BundleIdList object matching the internalCategoryId
	 * 
	 * @param internalCategoryId
	 * @return BundleIdList
	 */
	public List<BundleIdWithPurchaseCount> get(String internalCategoryId)
	{
		return cacheCategoryIdBundleList.getIfPresent(internalCategoryId);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param internalCategoryId
	 * @param bundleIdList
	 */
	public void put(String internalCategoryId, List<BundleIdWithPurchaseCount> bundleIdList)
	{
		cacheCategoryIdBundleList.put(internalCategoryId, bundleIdList);
	}
}
package com.increasingly.recommender.impl.collections;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

import com.increasingly.recommender.impl.BundleLinkProductInfo;
import com.increasingly.recommender.impl.Configuration;

public class BundleLinkProductInfoCache
{
	private static final BundleLinkProductInfoCache instance = new BundleLinkProductInfoCache();

	// Cache object
	private Cache<Integer, List<BundleLinkProductInfo>> cacheBundleLinkProductInfo = CacheBuilder.newBuilder().maximumSize(Configuration.getProductDetailsCacheMaxSize())
																.expireAfterWrite(Configuration.getShortCacheTime(), TimeUnit.MINUTES).build();

	/**
	 * private constructor.
	 */
	private BundleLinkProductInfoCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the BundleLinkProductInfoCache object.
	 */
	public static BundleLinkProductInfoCache getCache()
	{
		return instance;
	}

	/**
	 * Gets the BundleLinkProductInfo object matching the key
	 * 
	 * @param bundleId
	 * @return List<BundleLinkProductInfo>
	 */
	public List<BundleLinkProductInfo> get(Integer bundleId)
	{
		return cacheBundleLinkProductInfo.getIfPresent(bundleId);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param bundleId
	 * @param List<BundleLinkProductInfo>
	 */
	public void put(Integer bundleId, List<BundleLinkProductInfo> bundleLinkProductInfoList)
	{
		cacheBundleLinkProductInfo.put(bundleId, bundleLinkProductInfoList);
	}	
}
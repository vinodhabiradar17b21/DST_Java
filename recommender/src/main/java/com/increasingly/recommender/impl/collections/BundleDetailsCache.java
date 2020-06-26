package com.increasingly.recommender.impl.collections;

import java.util.concurrent.TimeUnit;

import com.increasingly.recommender.impl.BundleDetails;
import com.increasingly.recommender.impl.Configuration;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;


public class BundleDetailsCache
{
	private static final BundleDetailsCache instance = new BundleDetailsCache();

	// Cache object
	private Cache<Integer, BundleDetails> cacheBundleDetails = CacheBuilder.newBuilder().maximumSize(Configuration.getBundleDetailsListCacheMaxSize())
																.expireAfterWrite(Configuration.getShortCacheTime(), TimeUnit.MINUTES).build();

	/**
	 * private constructor.
	 */
	private BundleDetailsCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the BundleDetailsCache object.
	 */
	public static BundleDetailsCache getCache()
	{
		return instance;
	}

	/**
	 * Gets the BundleDetails object matching the bundleId
	 * 
	 * @param bundleId
	 * @return BundleDetails
	 */
	public BundleDetails get(Integer bundleId)
	{
		return cacheBundleDetails.getIfPresent(bundleId);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param bundleId
	 * @param BundleDetails
	 */
	public void put(Integer bundleId, BundleDetails bundleDetails)
	{
		cacheBundleDetails.put(bundleId, bundleDetails);
	}
}
package com.increasingly.recommender.impl.collections;

import java.util.List;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

import com.increasingly.recommender.impl.Configuration;


public class ProductOtherImagesCache
{
	private static final ProductOtherImagesCache instance = new ProductOtherImagesCache();

	// Cache object
	private Cache<Long, List<String>> cacheProductOtherImages = CacheBuilder.newBuilder().maximumSize(Configuration.getProductDetailsCacheMaxSize())
																.expireAfterWrite(Configuration.getLongCacheTime(), TimeUnit.MINUTES).build();

	/**
	 * private constructor.
	 */
	private ProductOtherImagesCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the ProductOtherImagesCache object.
	 */
	public static ProductOtherImagesCache getCache()
	{
		return instance;
	}

	/**
	 * Gets the ProductOtherImagesCache object matching the key
	 * 
	 * @param internalProductId
	 * @return productOtherImageList
	 */
	public List<String> get(Long internalProductId)
	{
		return cacheProductOtherImages.getIfPresent(internalProductId);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param internalProductId
	 * @param productOtherImageList
	 */
	public void put(Long internalProductId, List<String> productOtherImageList)
	{
		cacheProductOtherImages.put(internalProductId, productOtherImageList);
	}
}
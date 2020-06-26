package com.increasingly.recommender.impl.collections;

import java.util.concurrent.TimeUnit;

import com.increasingly.recommender.impl.Configuration;
import com.increasingly.recommender.impl.ProductDetails;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

public class ProductDetailsCache
{
	private static final ProductDetailsCache instance = new ProductDetailsCache();

	// Cache object
	private Cache<Long, ProductDetails> cacheProductDetails = CacheBuilder.newBuilder().maximumSize(Configuration.getProductDetailsCacheMaxSize())
																.expireAfterWrite(Configuration.getShortCacheTime(), TimeUnit.MINUTES).build();

	/**
	 * private constructor.
	 */
	private ProductDetailsCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the ProductDetailsCache object.
	 */
	public static ProductDetailsCache getCache()
	{
		return instance;
	}

	/**
	 * Gets the ProductDetails object matching the key
	 * 
	 * @param internalProductId
	 * @return ProductDetails
	 */
	public ProductDetails get(Long internalProductId)
	{
		return cacheProductDetails.getIfPresent(internalProductId);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param internalProductId
	 * @param ProductDetails
	 */
	public void put(Long internalProductId, ProductDetails productDetails)
	{
		cacheProductDetails.put(internalProductId, productDetails);
	}	
		
}
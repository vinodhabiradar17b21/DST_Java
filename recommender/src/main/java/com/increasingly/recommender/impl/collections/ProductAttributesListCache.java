package com.increasingly.recommender.impl.collections;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

import com.increasingly.recommender.impl.Configuration;
import com.increasingly.recommender.impl.ProductAttributeDetails;

public class ProductAttributesListCache
{	
	private static final ProductAttributesListCache instance = new ProductAttributesListCache();	

	// Cache Object
	private Cache<Long, Map<String,ProductAttributeDetails>> cacheProductAttributesList = CacheBuilder.newBuilder().maximumSize(Configuration.getProductIdBundleListCacheMaxSize())
																	.expireAfterWrite(Configuration.getCacheTime(), TimeUnit.MINUTES).build();

	/**
	 * private constructor.
	 */
	private ProductAttributesListCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the ProductAttributesListCache object.
	 */
	public static ProductAttributesListCache getCache()
	{
		return instance;
	}
	
	/**cacheProductAttributesList
	 * Gets the productAttributesList object matching the internalProductId
	 * 
	 * @param internalProductId
	 * @return productAttributesList
	 */
	public Map<String,ProductAttributeDetails> get(Long internalProductId)
	{
		return cacheProductAttributesList.getIfPresent(internalProductId);
	}

	/**
	 * Adds or updates the value in cache.
	 * 
	 * @param internalProductId
	 * @param productAttributesList
	 */
	public void put(Long internalProductId, Map<String,ProductAttributeDetails> productAttributesList)
	{
		cacheProductAttributesList.put(internalProductId, productAttributesList);
	}
}
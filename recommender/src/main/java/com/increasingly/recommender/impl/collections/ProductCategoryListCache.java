package com.increasingly.recommender.impl.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jersey.repackaged.com.google.common.base.Joiner;
import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

import com.increasingly.recommender.impl.Configuration;
import com.increasingly.recommender.impl.db.ProductCategoryList;
import com.increasingly.recommender.utils.GeneralUtils;

import static com.increasingly.recommender.constants.Constants.*;

public class ProductCategoryListCache
{
	private static final Logger logger = LoggerFactory.getLogger(ProductCategoryListCache.class.getClass());
	private static final ProductCategoryListCache instance = new ProductCategoryListCache();	

	// Cache Object
	private Cache<Long, List<Integer>> cacheProductCategoryList = CacheBuilder.newBuilder().maximumSize(Configuration.getProductCategoryListCacheMaxSize())
																	.expireAfterWrite(Configuration.getLongCacheTime(), TimeUnit.MINUTES).build();

	/**
	 * private constructor.
	 */
	private ProductCategoryListCache()
	{
		
	}

	/**
	 * Gets the singleton instance of the ProductCategoryListCache object.
	 */
	public static ProductCategoryListCache getCache()
	{
		return instance;
	}
	
	public List<Integer> getProductCategoryList(List<Long> internalProductIdList,Integer feedId) throws Exception
	{		
		Set<Integer> finalProductCategoryList = new HashSet<Integer>();
		List<Long> nonCachedProductCategoryList = new ArrayList<Long>();		
				
		for (long productId : internalProductIdList)
		{				
			List<Integer> productCategoryList = cacheProductCategoryList.getIfPresent(productId);

			if (productCategoryList != null && productCategoryList.size() > 0)
			{
				finalProductCategoryList.addAll(productCategoryList);				
			}
			else
			{
				nonCachedProductCategoryList.add(productId);						
			}
		}

		if (nonCachedProductCategoryList.size() > 0)
		{
			ArrayList<Map<String, Object>> productCategoryListFromDb = new ArrayList<Map<String, Object>>();
			Map<String, Object> input = new HashMap<String, Object>();

			input.put(FEED_ID, feedId);
			input.put(PRODUCT_ID_LIST, Joiner.on(",").join(nonCachedProductCategoryList).toString());

			ProductCategoryList productCategoryList = ProductCategoryList.getInstance();
			productCategoryListFromDb =  productCategoryList.runService(input);					
			
			Map<Long, List<Integer>> tmpcategoryList = new HashMap<Long, List<Integer>>();

			if (productCategoryListFromDb != null)
			{
				for (Map<String, Object> item : productCategoryListFromDb)
				{
					long internalProductId = Long.parseLong(item.get("ProductID").toString());
					int internalCategoryId = Integer.parseInt(item.get("CategoryID").toString());

					if (tmpcategoryList.containsKey(internalProductId))
					{
						List<Integer> tmpCatIdList = tmpcategoryList.get(internalProductId);
						tmpCatIdList.add(internalCategoryId);
						tmpcategoryList.put(internalProductId, tmpCatIdList);
					}
					else
					{
						List<Integer> tmpCatIdList = new ArrayList<Integer>();
						tmpCatIdList.add(internalCategoryId);
						tmpcategoryList.put(internalProductId, tmpCatIdList);
					}
				}								
				
				if (tmpcategoryList.size() > 0)
				{
					for (long productId : nonCachedProductCategoryList)
					{
						if (tmpcategoryList.containsKey(productId))
						{							
							finalProductCategoryList.addAll(tmpcategoryList.get(productId));
							cacheProductCategoryList.put(productId, tmpcategoryList.get(productId));
						}
					}
				}				
				
			}			
		}
		
		return (new ArrayList(finalProductCategoryList));
	}
	
}
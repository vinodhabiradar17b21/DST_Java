package com.increasingly.recommender.impl.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.increasingly.recommender.constants.Constants.*;

import com.increasingly.recommender.utils.GetProperties;
import com.increasingly.recommender.impl.BundleConfiguration;
import com.increasingly.recommender.impl.Configuration;
import com.increasingly.recommender.impl.db.AllowedGeoCountryIdList;
import com.increasingly.recommender.impl.db.BackFillBundles;
import com.increasingly.recommender.impl.db.BundleConfigurationDetails;
import com.increasingly.recommender.impl.db.BundleSalesQuickInfo;
import com.increasingly.recommender.impl.db.CategoryExclusionList;
import com.increasingly.recommender.impl.db.ClientRecommendationsProductIdList;
import com.increasingly.recommender.impl.db.DisabledRandomRotationProductIdList;
import com.increasingly.recommender.utils.FormatLoggerMessage;


public class BundleConfigurationCache
{
	private static final Logger logger = LoggerFactory.getLogger(BundleConfigurationCache.class.getClass());
	private static final BundleConfigurationCache instance = new BundleConfigurationCache();
	private static Properties increasinglyProperties = null;

	private static Object lock1 = new Object();
	
	// Cache object
	private Cache<Integer, BundleConfiguration> cacheClientDetails = CacheBuilder.newBuilder().maximumSize(Configuration.getBundleConfigurationCacheMaxSize())
																	.expireAfterWrite(Configuration.getLongCacheTime(), TimeUnit.MINUTES).build();
	
	public static BundleConfigurationCache getCache()
	{
		return instance;
	}

	/**
	 * Uses Guava cache for caching the bundle configuration details
	 * 
	 * @param clientId
	 * @return BundleConfiguration
	 * @throws ExecutionException
	 */
	public BundleConfiguration get(final Integer clientId)
	{			
		try
		{
			synchronized(lock1)
			{
				BundleConfiguration bundleConfiguration = cacheClientDetails.get(clientId, new Callable<BundleConfiguration>()
				{
					public BundleConfiguration call() throws Exception
					{
						return getBundleConfigurationsFromDb(clientId);
					}
				});
				
				return bundleConfiguration;
			}
			
		}
		catch (Exception ex)
		{
			return null;
		}
		
	}
	
	public BundleConfiguration getBundleConfigurationsFromDb(Integer clientId)
	{
		BundleConfiguration bundleConfiguration = null;
		try
		{
			logger.info("Retrieving bundle configuration details for the client id - " + clientId);
			BundleConfigurationDetails bundleConfigurationDetails = BundleConfigurationDetails.getInstance();
			
			Map<String, Object> input = new HashMap<String,Object>();
			input.put(CLIENT_ID, clientId);		
			ArrayList<Map<String,Object>> bundleConfigurationDetailsFromDb = bundleConfigurationDetails.runService(input);
			
			if(bundleConfigurationDetailsFromDb != null && bundleConfigurationDetailsFromDb.size() > 0)
			{
				bundleConfiguration = new BundleConfiguration(); 
				for (Map<String, Object> field : bundleConfigurationDetailsFromDb)
				{
					bundleConfiguration.setClientId(clientId);
					bundleConfiguration.setConfigId((Integer)field.get("config_id"));
					bundleConfiguration.setMaxNoOfBundles((Integer)field.get("max_no_of_bundles"));
					bundleConfiguration.setMaxNoOfProductInBundle((Integer)field.get("max_no_of_products_in_bundle"));
					bundleConfiguration.setDisplayPatternId((Integer)field.get("display_pattern_id"));
					bundleConfiguration.setBundleTypeId((Integer)field.get("bundle_type_id"));					
					bundleConfiguration.setUseProductRating((Boolean)field.get("use_product_rating"));
					bundleConfiguration.setIsPersonalizationEnabled((Boolean)field.get("is_personalization_enabled"));
					bundleConfiguration.setIsBrandExclusionEnabled((Boolean)field.get("is_brand_exclusion_enabled"));
					bundleConfiguration.setIsCategoryExclusionEnabled((Boolean)field.get("is_category_exclusion_enabled"));	
					bundleConfiguration.setIsProductExclusionEnabled((Boolean)field.get("is_product_exclusion_enabled"));
					bundleConfiguration.setShowCrossCategoryProducts((Boolean)field.get("show_cross_category_products"));
					bundleConfiguration.setShowBrandProducts((Boolean)field.get("show_multi_brand_products"));
					bundleConfiguration.setIsDiscountingEnabled((Boolean)field.get("is_discounting_enabled"));
					bundleConfiguration.setControlGroupVisitorIdCharSet((String)field.get("control_group_visitor_id_char_set"));
					bundleConfiguration.setFeedId((Integer)field.get("feed_id"));				
					bundleConfiguration.setIsFreeShippingActive((Boolean)field.get("is_free_shipping_active"));
					bundleConfiguration.setUseGeoCountryTargetting((Boolean)field.get("use_geo_country_targetting"));
					bundleConfiguration.setDecimalPrecision((Integer)field.get("decimal_precision"));
					bundleConfiguration.setHasClientRecommendations((Boolean)field.get("has_client_product_recommendations"));	
					bundleConfiguration.setShowAbandondedCartProductBundles((Boolean)field.get("show_abandoned_cart_product_bundles"));
					bundleConfiguration.setRandomRotationEnabled((Boolean)field.get("random_rotation_enabled"));
					bundleConfiguration.setLogicalRotationEnabled((Boolean)field.get("logical_rotation_enabled"));
					bundleConfiguration.setIsMarginBundlingEnabled((Boolean)field.get("is_margin_bundling_enabled"));
					bundleConfiguration.setBackFillBundlesEnabled((Boolean)field.get("is_back_fill_bundles_enabled"));
					
					if(field.get("free_shipping_subtotal") != null)
					{
					  bundleConfiguration.setFreeShippingSubTotal(Double.parseDouble(field.get("free_shipping_subtotal").toString()));
					}
					bundleConfiguration.setFreeShippingTitle((String)field.get("free_shipping_title"));
					
					if(field.get("max_no_of_bundles_for_home_page") != null)
					{
					    bundleConfiguration.setMaxNoOfBundlesForHomePage((Integer)field.get("max_no_of_bundles_for_home_page") );
					}
					else
					{
						bundleConfiguration.setMaxNoOfBundlesForHomePage((Integer)field.get("max_no_of_bundles"));
					}
					
					if(field.get("max_no_of_bundles_for_product_page") != null)
					{
					   bundleConfiguration.setMaxNoOfBundlesForProductPage((Integer)field.get("max_no_of_bundles_for_product_page"));
					}
					else
					{
					   bundleConfiguration.setMaxNoOfBundlesForProductPage((Integer)field.get("max_no_of_bundles"));
					}
					
					if(field.get("max_no_of_bundles_for_category_page") != null)
					{
					  bundleConfiguration.setMaxNoOfBundlesForCategoryPage((Integer)field.get("max_no_of_bundles_for_category_page"));
					}
					else
					{
						bundleConfiguration.setMaxNoOfBundlesForCategoryPage((Integer)field.get("max_no_of_bundles"));
					}
					
					if(field.get("max_no_of_bundles_for_cart_page") != null)
					{
					  bundleConfiguration.setMaxNoOfBundlesForCartPage((Integer)field.get("max_no_of_bundles_for_cart_page"));
					}
					else
					{
						bundleConfiguration.setMaxNoOfBundlesForCartPage((Integer)field.get("max_no_of_bundles"));
					}
					
					if(field.get("max_no_of_products_in_home_page_bundle") != null)
					{
					  bundleConfiguration.setMaxNoOfProductInHomePageBundle((Integer)field.get("max_no_of_products_in_home_page_bundle"));
					}
					else
					{
						bundleConfiguration.setMaxNoOfProductInHomePageBundle((Integer)field.get("max_no_of_products_in_bundle"));
					}
					
					if(field.get("max_no_of_products_in_product_page_bundle") != null)
					{
					   bundleConfiguration.setMaxNoOfProductInProductPageBundle((Integer)field.get("max_no_of_products_in_product_page_bundle"));
					}
					else
					{
						bundleConfiguration.setMaxNoOfProductInProductPageBundle((Integer)field.get("max_no_of_products_in_bundle"));	
					}
					
					if(field.get("max_no_of_products_in_category_page_bundle") != null)
					{
					   bundleConfiguration.setMaxNoOfProductInCategoryPageBundle((Integer)field.get("max_no_of_products_in_category_page_bundle"));
					}
					else
					{
						bundleConfiguration.setMaxNoOfProductInCategoryPageBundle((Integer)field.get("max_no_of_products_in_bundle"));	
					}					
					
					
					if(field.get("max_no_of_products_in_cart_page_bundle") != null)
					{
					  bundleConfiguration.setMaxNoOfProductInCartPageBundle((Integer)field.get("max_no_of_products_in_cart_page_bundle"));
					}
					else
					{
						bundleConfiguration.setMaxNoOfProductInCartPageBundle((Integer)field.get("max_no_of_products_in_bundle"));	
					}				
					
					
					if(field.get("min_no_of_products_in_product_page_bundle") != null)
					{
					   bundleConfiguration.setMinNoOfProductInProductPageBundle((Integer)field.get("min_no_of_products_in_product_page_bundle"));
					}					
					
					if(field.get("min_no_of_products_in_category_page_bundle") != null)
					{
					   bundleConfiguration.setMinNoOfProductInCategoryPageBundle((Integer)field.get("min_no_of_products_in_category_page_bundle"));
					}					
					
					if(field.get("min_no_of_products_in_cart_page_bundle") != null)
					{
					  bundleConfiguration.setMinNoOfProductInCartPageBundle((Integer)field.get("min_no_of_products_in_cart_page_bundle"));
					}
					
				}
				
				input.put(CONFIG_ID, bundleConfiguration.getConfigId());
				
				if(bundleConfiguration.getIsCategoryExclusionEnabled())
				{
					CategoryExclusionList getCategoryExclusionList = CategoryExclusionList.getInstance();
					try
					{					
						Set<Integer> categoryExclusionList = getCategoryExclusionList.runService(input);
						
						if (categoryExclusionList != null && categoryExclusionList.size() > 0)
						{
							bundleConfiguration.setCategoryExclusionList(categoryExclusionList);
						}
					}
					catch (Exception ex)
					{					
						String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"getBundleConfigurationsFromDb","Error Getting Category Exclusion List", 
								"config Id - " + bundleConfiguration.getConfigId());
						logger.error(errorMessage,ex);					
					}
				}
				
				if(bundleConfiguration.getUseGeoCountryTargetting())
				{
					AllowedGeoCountryIdList getAllowedCountryIdList = AllowedGeoCountryIdList.getInstance();
					try
					{					
						Set<Integer> allowedGeoCountryIdList = getAllowedCountryIdList.runService(input);
						
						if (allowedGeoCountryIdList != null && allowedGeoCountryIdList.size() > 0)
						{
							bundleConfiguration.setAllowedGeoCountryIdList(allowedGeoCountryIdList);
						}
					}
					catch (Exception ex)
					{					
						String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"getBundleConfigurationsFromDb","Error Occurred While Fetching Allowed Geo Country List", 
								"config Id - " + bundleConfiguration.getConfigId());
						logger.error(errorMessage,ex);					
					}
				}
				
				// Cache the Client Bundle Recommendations Internal ProductId
				if(bundleConfiguration.getHasClientRecommendations())
				{
					try
					{
						ClientRecommendationsProductIdList getClientBundleRecommendationsProductIdListFromDB = ClientRecommendationsProductIdList.getInstance();
						List<Long> recommendationsList = getClientBundleRecommendationsProductIdListFromDB.runService(input);
						
						if (recommendationsList != null && recommendationsList.size() > 0)
						{
							bundleConfiguration.setClientProductRecommendationList(recommendationsList);
						}
					}
					catch (Exception ex)
					{					
						String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"getBundleConfigurationsFromDb","Error Occurred While Fetching Client Product Recommendation List", 
								"config Id - " + bundleConfiguration.getConfigId());
						logger.error(errorMessage,ex);					
					}
				}
				
				try
				{
					ArrayList<Integer> bundleSalesList = new ArrayList<Integer>();
					
					BundleSalesQuickInfo bundleSalesQuickInfoDetailsFromDb = BundleSalesQuickInfo.getInstance();
					bundleSalesList = bundleSalesQuickInfoDetailsFromDb.runService(input);
					
					if(bundleSalesList != null && bundleSalesList.size() > 0)
					{
						bundleConfiguration.setBundleSalesList(bundleSalesList);
					}					
					
				}
				catch(Exception ex)
				{
					String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"getBundleConfigurationsFromDb","Error Occurred While Fetching Bundle Sales List", 
							"config Id - " + bundleConfiguration.getConfigId());
					logger.error(errorMessage,ex);
				}
				
				
				try
				{
					ArrayList<Long> productIdList = new ArrayList<Long>();
					
					DisabledRandomRotationProductIdList disabledRandomRotationProductIdListFromDb = DisabledRandomRotationProductIdList.getInstance();
					productIdList = disabledRandomRotationProductIdListFromDb.runService(input);
					
					if(productIdList != null && productIdList.size() > 0)
					{
						bundleConfiguration.setDisabledRandomRotationProductList(productIdList);
					}					
					
				}
				catch(Exception ex)
				{
					String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"getBundleConfigurationsFromDb","Error Occurred While Fetching Disabled Client Products for Random Rotation", 
							"config Id - " + bundleConfiguration.getConfigId());
					logger.error(errorMessage,ex);
				}
				
				try
				{
					ArrayList<Integer> backFillBundlesList = new ArrayList<Integer>();
					
					BackFillBundles backFillBundlesFromDb = BackFillBundles.getInstance();
					backFillBundlesList = backFillBundlesFromDb.runService(input);
					
					if(backFillBundlesList != null && backFillBundlesList.size() > 0)
					{
						bundleConfiguration.setBackFillBundlesList(backFillBundlesList);
					}					
					
				}
				catch(Exception ex)
				{
					String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"getBundleConfigurationsFromDb","Error Occurred While Fetching Back Fill Bundles List", 
							"config Id - " + bundleConfiguration.getConfigId());
					logger.error(errorMessage,ex);
				}
				
				// cache client visitor ids.
				try
				{
					increasinglyProperties = GetProperties.readProperties("webapp/WEB-INF/increasingly.properties");	
					String[] visitors = increasinglyProperties.getProperty("visitorIds").split(",");
					List<String> clientVisitorIds = Arrays.asList(visitors);
					if(clientVisitorIds != null && clientVisitorIds.size() > 0)
					{
						bundleConfiguration.setClientVisitorIds(clientVisitorIds);
					}
				}
				catch(Exception ex)
				{
					String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"getBundleConfigurationsFromDb","Error Occurred While reading Visitor Ids from properties file", 
							"config Id - " + bundleConfiguration.getConfigId());
					logger.error(errorMessage,ex);
				}
				
			}
		}
		catch(Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "getBundleConfigurationsFromDb" , "ErrorGettingBundleConfigurationDetailsFromDb" , "");
			logger.error(errorMessage, ex);			
		}
		
		return bundleConfiguration;
	}
}
package com.increasingly.recommender.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.utils.FormatLoggerMessage;
import com.increasingly.recommender.utils.GetProperties;

import static com.increasingly.recommender.constants.Constants.*;
/**
 * @author shreehari.padaki
 *
 * This class will keep all configuration details for the bundle recommender
 */
public class Configuration
{
	private static Properties increasinglyProperties = null;
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class.getClass());
	
	// Control
	private static Configuration current;
	
	//For Bundle Request Tracking
	private String isDBInsertion="";
	private String trackingFilePath="";
	
	
	// Local Path variables	
	private String trackingUrl;	
	private boolean enableRequestTimeLogging = false;
	private String increasinglyCookieName = "inc_vid"; 

	// Cache Times
	private Integer cacheTime = 20;
	private Integer shortCacheTime = 5;
	private Integer longCacheTime = 600;
	private Integer cacheTimeForImpressions = 1;

	// Cache Max Sizes
	private Integer productIdCacheMaxSize = 50000;
	private Integer categoryIdCacheMaxSize = 50000;	
	private Integer productCategoryListCacheMaxSize = 50000;
	private Integer productDetailsCacheMaxSize = 50000;
	private Integer productIdBundleListCacheMaxSize = 50000;
	private Integer categoryIdBundleListCacheMaxSize = 50000;
	private Integer bundleDetailsListCacheMaxSize = 50000;
	private Integer bundleIdProductListCacheMaxSize = 50000;
	private Integer bundleConfigurationCacheMaxSize = 10000;
	
	private String geoDatabaseCountryPath = "";
	private String geoDatabaseCityPath = "";
	private Integer clientRecommendationsCacheMaxSize = 10000;
	private Integer requestCountCacheMaxSize = 50000;
	
	// Internal Variables

	private String machineName = "";
	
	private DateTime loadDate = DateTime.now();
	private DateTime configDate = DateTime.now(); // Probably the same as loadDate
	
	private Integer maxFpBundlesForCartPage;

	private Configuration()
	{
	}

	public static void setConfiguration() 
	{		
	   Configuration conf = new Configuration();
	
		try
		{
			conf.machineName = InetAddress.getLocalHost().getHostName();
		}
		catch (UnknownHostException ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "setConfiguration" , "failed to read machine name","");
			logger.error(errorMessage, ex);
		}
		
		GetProperties getProperties = new GetProperties();
		increasinglyProperties = getProperties.readProperties("webapp/WEB-INF/increasingly.properties");			
    	
    	conf.increasinglyCookieName = increasinglyProperties.getProperty("increasinglyCookieName");
		
    	conf.trackingUrl = increasinglyProperties.getProperty("trackingURL");
    	
    	conf.enableRequestTimeLogging = Boolean.parseBoolean(increasinglyProperties.getProperty("enableRequestTimeLogging"));
    	conf.cacheTime =  Integer.parseInt(increasinglyProperties.getProperty("cacheTime"));
    	conf.cacheTimeForImpressions =  Integer.parseInt(increasinglyProperties.getProperty("cacheTimeForImpressions"));
    	
    	conf.shortCacheTime = Integer.parseInt(increasinglyProperties.getProperty("shortCacheTime"));
    	conf.longCacheTime = Integer.parseInt(increasinglyProperties.getProperty("longCacheTime"));
    	
    	conf.productIdCacheMaxSize = Integer.parseInt(increasinglyProperties.getProperty("productIdCacheMaxSize"));
    	conf.categoryIdCacheMaxSize = Integer.parseInt(increasinglyProperties.getProperty("categoryIdCacheMaxSize"));
    	conf.productCategoryListCacheMaxSize = Integer.parseInt(increasinglyProperties.getProperty("productCategoryListCacheMaxSize"));
    	conf.productDetailsCacheMaxSize = Integer.parseInt(increasinglyProperties.getProperty("productDetailsCacheMaxSize"));
    	conf.productIdBundleListCacheMaxSize = Integer.parseInt(increasinglyProperties.getProperty("productIdBundleListCacheMaxSize"));
    	conf.categoryIdBundleListCacheMaxSize = Integer.parseInt(increasinglyProperties.getProperty("categoryIdBundleListCacheMaxSize"));
    	conf.bundleDetailsListCacheMaxSize = Integer.parseInt(increasinglyProperties.getProperty("bundleDetailsListCacheMaxSize"));
		conf.bundleIdProductListCacheMaxSize = Integer.parseInt(increasinglyProperties.getProperty("bundleIdProductListCacheMaxSize"));
		conf.bundleConfigurationCacheMaxSize = Integer.parseInt(increasinglyProperties.getProperty("bundleConfigurationCacheMaxSize"));
		conf.geoDatabaseCountryPath = increasinglyProperties.getProperty("geoDatabaseCountryPath");
		conf.geoDatabaseCityPath = increasinglyProperties.getProperty("geoDatabaseCityPath");
		conf.clientRecommendationsCacheMaxSize = Integer.parseInt(increasinglyProperties.getProperty("productIdCacheMaxSize"));
		conf.requestCountCacheMaxSize = Integer.parseInt(increasinglyProperties.getProperty("requestCountCacheMaxSize"));
		conf.maxFpBundlesForCartPage = Integer.parseInt(increasinglyProperties.getProperty("maxFPBundlesForCartPage"));
		
		conf.isDBInsertion = increasinglyProperties.getProperty("isDBInsertion");
		conf.trackingFilePath = increasinglyProperties.getProperty("trackingFilePath");
		
		current = conf;		
		
	}
	

	public static String getTrackingFilePath() {
		
		return current.trackingFilePath;
	}

	public static String getIsDBInsertion() {
		
		return current.isDBInsertion;
	}

	public static String getIncreasinglyCookieName()
	{
		return current.increasinglyCookieName;
	}

	public static int getCacheTime()
	{
		return current.cacheTime;
	}
	
	public static int getShortCacheTime()
	{
		return current.shortCacheTime;
	}
	
	public static int getLongCacheTime()
	{
		return current.longCacheTime;
	}
	
	public static int getProductIdCacheMaxSize()
	{
		return current.productIdCacheMaxSize;
	}
	
	//
	
	public static int getCategoryIdCacheMaxSize()
	{
		return current.categoryIdCacheMaxSize;
	}
	
	public static int getProductCategoryListCacheMaxSize()
	{
		return current.productCategoryListCacheMaxSize;
	}
	
	public static int getProductIdBundleListCacheMaxSize()
	{
		return current.productIdBundleListCacheMaxSize;
	}
	
	public static int getCategoryIdBundleListCacheMaxSize()
	{
		return current.categoryIdBundleListCacheMaxSize;
	}
	public static int getBundleDetailsListCacheMaxSize()
	{
		return current.bundleDetailsListCacheMaxSize;
	}
	
	public static int getProductDetailsCacheMaxSize()
	{
		return current.productDetailsCacheMaxSize;
	}	
	
	public static int getBundleIdProductListCacheMaxSize()
	{
		return current.bundleIdProductListCacheMaxSize;
	}
	
	public static int getBundleConfigurationCacheMaxSize() 
	{
		return current.bundleConfigurationCacheMaxSize;
	}
	
	public static DateTime getLoadDateTime()
	{
		return current.loadDate;
	}

	public static DateTime getConfigLoadDateTime()
	{
		return current.configDate;
	}

	public static String getMachineName()
	{
		return current.machineName;
	}
	
	public static String getTrackingUrl()
	{
		return current.trackingUrl;
	}	

	public static String getTrackingUrl(Boolean isSecure)
	{
		return isSecure ? getTrackingUrl().replace("http://", "https://") : getTrackingUrl();
	}
	
	public static boolean getEnableRequestTimeLogging()
	{
		return current.enableRequestTimeLogging;
	}
	
	public static String getGeoDatabaseCountryPath()
	{
		return current.geoDatabaseCountryPath;
	}
	
	public static String getGeoDatabaseCityPath()
	{
		return current.geoDatabaseCityPath;
	}

	public Integer getClientRecommendationsCacheMaxSize() {
		return current.clientRecommendationsCacheMaxSize;
	}

	public static int getRequestCountCacheMaxSize() {
		return current.requestCountCacheMaxSize;
	}

	public static int getCacheTimeForImpressions() {
		return current.cacheTimeForImpressions;
	}

	public static Integer getMaxFpBundlesForCartPage() {
		return current.maxFpBundlesForCartPage;
	}
}
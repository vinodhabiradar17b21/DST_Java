package com.increasingly.recommender.impl;

import static com.increasingly.recommender.constants.Constants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.impl.collections.BundleConfigurationCache;
import com.increasingly.recommender.utils.FormatLoggerMessage;


public class BundleConfigurationService
{
  private static final Logger logger = LoggerFactory.getLogger(BundleConfigurationService.class.getClass());
	
	public BundleConfiguration getBundleConfiguration(BundleResponse bundleResponse, BundleRequest bundleRequest)
	{
		BundleConfiguration bundleConfiguration = null;

		try
		{
			if (!bundleResponse.getCanContinue())
			{
				return null;
			}			
						
			logger.info(LOG_APPLICATION_FLOW + "Retrieving bundle configuration details for the client id - " + bundleRequest.getClientId());
			bundleConfiguration = BundleConfigurationCache.getCache().get(bundleRequest.getClientId());		
			
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "getCachedBundleConfiguration" , "ErrorGettingBundleConfiguration" , "");
			logger.error(errorMessage, ex);
		}
		return bundleConfiguration;
}
}
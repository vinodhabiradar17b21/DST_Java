package com.increasingly.recommender.impl.collections;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import jersey.repackaged.com.google.common.cache.Cache;
import jersey.repackaged.com.google.common.cache.CacheBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.impl.Configuration;
import com.increasingly.recommender.impl.db.ClientApiKeyDetails;
import com.increasingly.recommender.utils.FormatLoggerMessage;

import static com.increasingly.recommender.constants.Constants.*;


public class ClientApiKeyDetailsCache
{
	private static final Logger logger = LoggerFactory.getLogger(ClientApiKeyDetailsCache.class.getClass());
	private static final ClientApiKeyDetailsCache instance = new ClientApiKeyDetailsCache();

	private static Object lock1 = new Object();
	
	// Cache object
	private Cache<String, Integer> cacheClientDetails = CacheBuilder.newBuilder().maximumSize(50000)
																	.expireAfterWrite(Configuration.getLongCacheTime(), TimeUnit.MINUTES).build();
	
	public static ClientApiKeyDetailsCache getCache()
	{
		return instance;
	}

	/**
	 * Uses Guava cache for caching the platform lists
	 * 
	 */
	public Integer get(final String apiKey)
	{			
		try
		{
			synchronized(lock1)
			{
				Integer clientId = cacheClientDetails.get(new String(apiKey), new Callable<Integer>()
				{
					public Integer call() throws Exception
					{
						return getClientIdFromDb(apiKey);
					}
				});
				
				return clientId;
			}
			
		}
		catch (Exception ex)
		{
			return null;
		}
		
	}
	
	public Integer getClientIdFromDb(String apiKey)
	{	
		Integer clientId = 0;
		try
		{			
			ClientApiKeyDetails clientApiKeyDetailsDb = ClientApiKeyDetails.getInstance();
			
			Map<String, Object> input = new HashMap<String,Object>();
			input.put(API_KEY, apiKey);		
			
			clientId = clientApiKeyDetailsDb.runService(input);
			
		}
		catch(Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR ,"getClientIdFromDb" , "Error occured while getting client id" ,"");
			logger.error(errorMessage,ex);
		}
		
		return clientId;
	}

}
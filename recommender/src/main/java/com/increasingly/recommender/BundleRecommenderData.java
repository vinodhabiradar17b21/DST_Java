package com.increasingly.recommender;


import java.net.MalformedURLException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.ThreadContext;
import org.eclipse.jetty.http.HttpStatus;
import org.glassfish.jersey.server.ParamException.CookieParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.impl.BundleRecommender;
import com.increasingly.recommender.impl.BundleRequest;
import com.increasingly.recommender.impl.Configuration;
import com.increasingly.recommender.server.Compress;
import com.increasingly.recommender.utils.FormatLoggerMessage;

import static com.increasingly.recommender.constants.Constants.*;


/**
 * All requests hits this end point
 * @author shreehari.padaki
 *
 */
@Path("")
public class BundleRecommenderData
{
	private static final Logger logger = LoggerFactory.getLogger(BundleRecommenderData.class.getClass());
	private static final Logger loggerRequestTimeLogging = LoggerFactory.getLogger("requestTimeDetailsLogger");

	//Metric
	//private final Timer responseTime = Metrics.newTimer(BundleRecommenderData.class, "Response-Time");
	
	@SuppressWarnings("finally")
	@GET
	@Compress
	@Path("/increasingly_bundles")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response recommendBundles(@Context HttpServletRequest request,
			@Context HttpHeaders headers) throws MalformedURLException,
			CookieParamException
	{
		//TimerContext timerContext = responseTime.time();		
		
		BundleRecommender bundleRecommender = new BundleRecommender();

		try 
		{
			BundleRequest bundleRequest = new BundleRequest();
			bundleRequest.setQueryString(request.getQueryString());
			bundleRequest.setCookieName(Configuration.getIncreasinglyCookieName());

			Map<String, Cookie> cookieData = headers.getCookies();
			bundleRequest.setCookieData(cookieData);
			
			bundleRequest.setUserAgent(headers.getHeaderString("User-Agent"));
				            
			bundleRequest.setUserIpAddress(headers.getHeaderString("X-Forwarded-For"));

			if (bundleRequest.getUserIpAddress() == null || bundleRequest.getUserIpAddress().length() == 0) 
			{
				bundleRequest.setUserIpAddress(request.getRemoteAddr());
			}

			if (headers.getHeaderString("x-forwarded-proto") != null && headers.getHeaderString("x-forwarded-proto").equals("https"))
			{
				bundleRequest.setIsSecure(true);
			} 
			else 
			{
				bundleRequest.setIsSecure((bundleRequest.getProtocol().equals("https")) ? true : false);
			}

			bundleRequest.setProtocol(request.getProtocol());
			bundleRecommender.getBundles(bundleRequest);
			
			// Time Logging

			if (Configuration.getEnableRequestTimeLogging())
			{
				String requestTime = FormatLoggerMessage.formatInfo("","RequestTime","ProcessRequestTime",
										  "Total Time: " + bundleRecommender.getResponseProcessTimes().getTotalProcessTime().toString() +
										  "	Time Taken to Get Internal Product Ids: " + bundleRecommender.getResponseProcessTimes().getTimeTakenToGetInternalProductIds().toString() +
										  "	Time Taken to Get Internal Category Ids: " + bundleRecommender.getResponseProcessTimes().getTimeTakenToGetInternalCategoryIds().toString() +
										  "	Time Taken to Get Category Product Id List of Requested Products: " + bundleRecommender.getResponseProcessTimes().getTimeTakenToGetCategoryProductIdListOfRequestedProducts().toString() +
										  "	Time Taken to Get Product Page Bundle Id List: " + bundleRecommender.getResponseProcessTimes().getTimeTakenToGetProductPageBundleIdList() +
										  "	Time Taken to Get Category Page Bundle Id List: " + bundleRecommender.getResponseProcessTimes().getTimeTakenToGetCategoryPageBundleIdList() +
										  "	Time Taken to Get Bundle Product Id List: " + bundleRecommender.getResponseProcessTimes().getTimeTakenToGetBundleProductIdList() +
										  "	Time Taken to Get Bundle Details: " + bundleRecommender.getResponseProcessTimes().getTimeTakenToGetBundleDetails() +
										  "	Time Taken to Get Product Details: " + bundleRecommender.getResponseProcessTimes().getTimeTakenToGetProductDetails() +
										  "	Time Taken to Insert Bundle Request Details: " + bundleRecommender.getResponseProcessTimes().getTimeTakenToInsertBundleRequestDetails());
				
				loggerRequestTimeLogging.info(requestTime);							
			}
			

			//timerContext.stop();
			ThreadContext.clearAll();
						
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"BundleRecommenderData", "Error processing request", "");
			logger.error(errorMessage, ex);

		}
		finally 
		{	
			// Response
			CacheControl cc = new CacheControl();
			cc.setNoCache(true);
			cc.setNoStore(true);
			cc.setMustRevalidate(true);		
			
			String responseContentType = "charSet=" + bundleRecommender.getResponseEncoding();
			responseContentType = "application/json;" + responseContentType;			
			
			String domain = "*";
			
			if(headers.getHeaderString("Origin") != null)
			{
				if(!headers.getHeaderString("Origin").isEmpty())
				{
					domain = headers.getHeaderString("Origin");
				}				
			}
			
			if(bundleRecommender.getBundleJsonResponseContent() != null)
			{
				 return Response.status(HttpStatus.OK_200).cacheControl(cc)						
						.header("Access-Control-Allow-Origin",domain)	
						.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept") 
						.header("X-Frame-Options", "SAMEORIGIN")
						.header("X-Content-Type-Options", "nosniff")
						.header("X-XSS-Protection", "1; mode=block")										
						.header("Content-Type", responseContentType)
						.header("p3p", "policyref=\"http://www.increasingly.co/w3c/p3p.xml\", CP=\"NOI DSP COR DEVa PSAa OUR BUS COM NAV\"")
						.entity(bundleRecommender.getBundleJsonResponseContent()).build();
			}
			else if(bundleRecommender.getBundleAvailabilityJsonResponse() != null)
			{
				return Response.status(HttpStatus.OK_200).cacheControl(cc)					
						.header("Access-Control-Allow-Origin",domain)
						.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept") 
						.header("X-Frame-Options", "SAMEORIGIN")
						.header("X-Content-Type-Options", "nosniff")
						.header("X-XSS-Protection", "1; mode=block")
						.header("Content-Type", responseContentType)
						.header("p3p", "policyref=\"http://www.increasingly.co/w3c/p3p.xml\", CP=\"NOI DSP COR DEVa PSAa OUR BUS COM NAV\"")
						.entity(bundleRecommender.getBundleAvailabilityJsonResponse()).build();
			}
			else if(bundleRecommender.getProductsListWithBundleAvailablityJsonResponse() != null)
			{
				return Response.status(HttpStatus.OK_200).cacheControl(cc)					
						.header("Access-Control-Allow-Origin",domain)
						.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept") 
						.header("X-Frame-Options", "SAMEORIGIN")
						.header("X-Content-Type-Options", "nosniff")
						.header("X-XSS-Protection", "1; mode=block")
						.header("Content-Type", responseContentType)
						.header("p3p", "policyref=\"http://www.increasingly.co/w3c/p3p.xml\", CP=\"NOI DSP COR DEVa PSAa OUR BUS COM NAV\"")
						.entity(bundleRecommender.getProductsListWithBundleAvailablityJsonResponse()).build();
			}
			else
			{
				return Response.status(HttpStatus.OK_200).cacheControl(cc)					
						.header("Access-Control-Allow-Origin",domain)	
						.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept") 
						.header("X-Frame-Options", "SAMEORIGIN")
						.header("X-Content-Type-Options", "nosniff")
						.header("X-XSS-Protection", "1; mode=block")
						.header("Content-Type", responseContentType)
						.header("p3p", "policyref=\"http://www.increasingly.co/w3c/p3p.xml\", CP=\"NOI DSP COR DEVa PSAa OUR BUS COM NAV\"")
						.entity("").build();
			}

		}

	}
	
}
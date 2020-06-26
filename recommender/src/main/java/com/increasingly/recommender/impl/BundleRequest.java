package com.increasingly.recommender.impl;

import static com.increasingly.recommender.constants.Constants.*;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Cookie;

import org.bouncycastle.util.encoders.UrlBase64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.increasingly.recommender.constants.PageType;
import com.increasingly.recommender.constants.RequestType;
import com.increasingly.recommender.utils.FormatLoggerMessage;
import com.increasingly.recommender.utils.SecurityUtil;

public class BundleRequest
{
	private static final Logger logger = LoggerFactory.getLogger(BundleRequest.class.getClass());
	private Boolean requireDecoding = true;
	private Boolean isSecure = false;

	private Integer requestType = 0;
	private String cookieName = "inc_bundles";

	private String queryString = "";
	private String queryStringSeparator = "&";
	private String parameters = "";
	private Map<String, String> parameterCollection;

	private Integer clientId = 0;
	private String visitorId = "";
	private String clientVisitorId = "";
	
	private String userIpAddress="";
	private String apiKey = "";
	private String productIds = "";
	private List<String> customerProductIdList = null;
		
	private String categoryIds = "";
	private List<String> customerCategoryIdList = null;
	private String pageType = "";
	private int pageTypeId = 100;
	private int noOfProductsInBundle = 0;
	
	private String increasinglyCookieData = "";
	private String cookieContent;
	private String userAgent = "";
	private String isPsku = "";
	private int bundleTypeId = 0;
	private int noOfBundles = 0;
	private Boolean backFillBundles = null;

	//CookieDetails cookieDetails = new CookieDetails();
	
	public int getNoOfBundles() {
		return noOfBundles;
	}

	public void setNoOfBundles(int noOfBundles) {
		this.noOfBundles = noOfBundles;
	}

	public int getNoOfProductsInBundle() {
		return noOfProductsInBundle;
	}

	public void setNoOfProductsInBundle(int noOfProductsInBundle) {
		this.noOfProductsInBundle = noOfProductsInBundle;
	}

	public int getPageTypeId() {
		return pageTypeId;
	}

	public void setPageTypeId(int pageTypeId) {
		this.pageTypeId = pageTypeId;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	private Map<String, Cookie> cookieData = null;
	private String protocol = "";

	public BundleRequest()
	{
	}

	/**
	 * @return requestType
	 */
	public Integer getRequestType()
	{
		return this.requestType;
	}

	/**
	 * @param requestType
	 */
	public void setRequestType(Integer requestType)
	{
		this.requestType = requestType;
	}
	
	public List<String> getCustomerProductIdList()
	{
		return this.customerProductIdList;
	}
	
	public void setCustomerProductIdList(List<String> customerProductIdList)
	{
		this.customerProductIdList = customerProductIdList;
	}

	
	public List<String> getCustomerCategoryIdList()
	{
		return this.customerCategoryIdList;
	}
	
	public void setCustomerCategoryIdList(List<String> customerCategoryIdList)
	{
		this.customerCategoryIdList = customerCategoryIdList;
	}

	
	/**
	 * @return QueryString
	 */
	public String getQueryString()
	{
		return this.queryString;
	}
	
	public void setQueryString(String queryStringWithParams)
	{
		this.queryString = queryStringWithParams;
	}

	public String getQueryStringParameter(String name)
	{
		return getQueryStringParameter(name, "");
	}

	public String getQueryStringParameter(String name, String defaultValue)
	{
		String value = parameterCollection.get(name);
		if (Strings.isNullOrEmpty(value))
		{
			value = defaultValue;
		}
		return value;
	}

	public int getQueryStringParameterCount()
	{
		if (parameterCollection != null)
		{
			return parameterCollection.size();
		}
		else
		{
			return 0;
		}
	}

	/**
	 * @return
	 */
	public String getQueryStringSeparator()
	{
		return this.queryStringSeparator;
	}

	/**
	

	 * @param queryStringSeparator
	 */
	public void setQueryStringSeparator(String queryStringSeparator)
	{
		this.queryStringSeparator = queryStringSeparator;
	}

	/**
	 * @return
	 */
	public String getParameters()
	{
		return this.parameters;
	}

	/**
	 * @param parameters
	 */
	public void setParameters(String parameters)
	{
		this.parameters = parameters;
	}

	
	/**
	 * @return cookieName
	 */
	public String getCookieName()
	{
		return this.cookieName;
	}

	/**
	 * @param cookieName
	 */
	public void setCookieName(String cookieName)
	{
		this.cookieName = cookieName;
	}

	/**
	 * @return
	 */
	public Boolean getIsSecure()
	{
		return this.isSecure;
	}

	/**
	 * @param isSecure
	 */
	public void setIsSecure(Boolean isSecure)
	{
		this.isSecure = isSecure;
	}

	/**
	 * @return
	 */
	public String getProtocol()
	{
		return this.protocol;
	}

	/**
	 * @param protocol
	 */
	public void setProtocol(String protocol)
	{
		this.protocol = protocol;
	}

	/**
	 * @return
	 */
	public Integer getClientId()
	{
		return this.clientId;
	}

	/**
	 * @param clientId
	 */
	public void setClientId(Integer clientId)
	{
		this.clientId = clientId;
	}
	
	/**
	 * @return
	 */
	public String getProductIds()
	{
		return this.productIds;
	}

	/**
	 * @param productId
	 */
	public void setProductIds(String productIds)
	{
		this.productIds = productIds;
	}
	
	/**
	 * @return
	 */
	public String getCategoryIds()
	{
		return this.categoryIds;
	}

	/**
	 * @param categoryId
	 */
	public void setCategoryIds(String categoryIds)
	{
		this.categoryIds = categoryIds;
	}
		
	/**
	 * @return
	 */
	public String getPageType()
	{
		return this.pageType;
	}

	/**
	 * @param PageType
	 */
	public void setPageType(String pageType)
	{
		this.pageType = pageType;
	}
	
	/**
	 * @return
	 */
	public Map<String, Cookie> getCookieData()
	{
		return this.cookieData;
	}

	/**
	 * @param cookieData
	 */
	public void setCookieData(Map<String, Cookie> cookieData)
	{
		this.cookieData = cookieData;
	}

	/**
	 * @return the userIPAddress
	 */
	public String getUserIpAddress()
	{
		return userIpAddress;
	}

	/**
	 * @param userIpAddress
	 *            the userIPAddress to set
	 */
	public void setUserIpAddress(String userIpAddress)
	{
		this.userIpAddress = userIpAddress;
	}

	
	/**
	 * @return cookieDetails
	 */
	/**
	public CookieDetails getCookieDetails()
	{
		return cookieDetails;
	} */

	/**
	 * @param cookieDetails
	 */
	/*
	public void setCookieDetails(CookieDetails cookieDetails)
	{
		this.cookieDetails = cookieDetails;
	} */

	
	/**
	 * @return
	 */
	public String getVisitorId()
	{
		return this.visitorId;
	}

	/**
	 * @param value
	 */
	public void setVisitorId(String value)
	{
		this.visitorId = value;
	}

	public String getClientVisitorId() {
		return clientVisitorId;
	}

	public void setClientVisitorId(String clientVisitorId) {
		this.clientVisitorId = clientVisitorId;
	}
	
	/**
	 
	 * @return
	 */
	public String getApiKey()
	{
		return this.apiKey;
	}

	/**
	 * @param value
	 */
	public void setApiKey(String value)
	{
		this.apiKey = value;
	}
	
	/**
	 * @param queryString
	 * @return
	 * @throws Exception
	 */
	public Boolean readAndValidateBundleRequest() throws Exception
	{
		try
		{
			if (Strings.isNullOrEmpty(queryString))
			{
				throw new Exception("No Query string");
			}
			
			// URL Encoded / (as %2F) fix
			if (queryString.indexOf("/") == -1 && (queryString.indexOf("%2F") > 3 || queryString.indexOf("%2f") > 3))
			{
				queryString = queryString.replace("%2F", "/");
				queryString = queryString.replace("%2f", "/");
			}
			if (queryString.indexOf("=") == -1 && (queryString.indexOf("%3D") > 3 || queryString.indexOf("%3d") > 3))
			{
				queryString = queryString.replace("%3D", "=");
				queryString = queryString.replace("%3d", "=");
			}

			if (queryString.indexOf("/") == -1 || queryString.indexOf("/") < 3)
			{
				throw new Exception("No Request Type Information");
			}

			switch (queryString.substring(0, queryString.indexOf("/")).toUpperCase())
			{
		
			case "IRB":
				requestType = RequestType.RECOMMEND_BUNDLES;
				parameters = queryString.substring(4);
				break;
			case "IBAC":
				requestType = RequestType.BUNDLES_AVAILABILITY_CHECK;
				parameters = queryString.substring(5);
				break;
			case "IBACM":
				requestType = RequestType.BUNDLES_AVAILABILITY_CHECK_MULTIPLE;
				parameters = queryString.substring(6);
				break;			
			default:
				// throw exception
				requestType = RequestType.INVALID;
				System.out.println("Invalid Request URL" + queryString + " -- Invalid Request Type");
				throw new Exception("Invalid Request Type");
			}

			if (requireDecoding && parameters.indexOf(queryStringSeparator) > 0)
			{
				parameters = parameters.substring(0, parameters.indexOf(queryStringSeparator));
			}

			if (Strings.isNullOrEmpty(parameters))
			{
				throw new Exception("Invalid parameters");
			}

		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "ValidateRequest" , "Error validating request" , "");
			logger.error(errorMessage,ex);
			return false;
		}

		
		return true;
	}
	
	public void setPageTypeId()
	{
		switch (this.pageType)
		{	
		case PRODUCT_PAGE:
			this.pageTypeId = PageType.PRODUCT_PAGE;		
			break;
		case CATEGORY_PAGE:
			this.pageTypeId = PageType.CATEGORY_PAGE;		
			break;
		case SEARCH_PAGE:
			this.pageTypeId = PageType.SEARCH_PAGE;	
		case CART_PAGE:
			this.pageTypeId = PageType.CART_PAGE;
			break;	
		case CHECKOUT_PAGE:
			this.pageTypeId = PageType.CHECKOUT_PAGE;
			break;	
		default:			
			this.pageTypeId = PageType.PRODUCT_PAGE;			
			
		}
	}

	/*
	 * To complete later (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "";
	}

	/**
	 * @return
	 */
	public boolean Decrypt()
	{
		try
		{
			if (requireDecoding)
			{
				parameters = SecurityUtil.base64Decode(parameters);
			}
			
			// Build Key Value Pair Collection
			String[] params = parameters.split("&");
			parameterCollection = new HashMap<String, String>();

			for (String param : params)
			{
				String[] p = param.split("=");
				if(p.length >= 2)
				{
					parameterCollection.put(p[0], p[1]);
				}
			}
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "Decryption error" , "Error decrypting request" , "");
			logger.error(errorMessage,ex);
			return false;			
		}		

		return true;
	}

	/**
	 * @return
	 */
	public String getIncreasinglyCookieData()
	{
		return increasinglyCookieData;
	}

	/**
	 * @param increasinglyCookieData
	 */
	public void setIncreasinglyCookieData(String increasinglyCookieData)
	{
		this.increasinglyCookieData = increasinglyCookieData;
	}

	/**
	 * @return the cookieContent
	 */
	public String getCookieContent()
	{
		return cookieContent;
	}

	/**
	 * @param cookieContent
	 *            the cookieContent to set
	 */
	public void setCookieContent(String cookieContent)
	{
		this.cookieContent = cookieContent;
	}

	public String getIsPsku() {
		return isPsku;
	}

	public void setIsPsku(String isPsku) {
		this.isPsku = isPsku;
	}

	public int getBundleTypeId() {
		return bundleTypeId;
	}

	public void setBundleTypeId(int bundleTypeId) {
		this.bundleTypeId = bundleTypeId;
	}

	public Boolean getBackFillBundles() {
		return backFillBundles;
	}

	public void setBackFillBundles(Boolean backFillBundles) {
		this.backFillBundles = backFillBundles;
	}
}
package com.increasingly.importbulkdata.util;

public class UrlUtil
{
	/**
	 * Validates a given URL
	 * @param url
	 * @throws Exception
	 */
	public static boolean isValidUrl(String url) 
	{
		return url.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
	}
}
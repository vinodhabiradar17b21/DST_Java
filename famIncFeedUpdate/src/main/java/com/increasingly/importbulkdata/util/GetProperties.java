package com.increasingly.importbulkdata.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to fetch values set in properties file
 *
 * @author Shreehari Padaki
 */
public class GetProperties
{
	private static final Logger logger = LogManager.getLogger(GetProperties.class.getName());

	/**
	 * Read Properties file
	 * 
	 * @param filePath - location of file
	 * @return -success or failure
	 * @throws Exception 
	 * @throws IOException
	 */
	public static Properties readProperties(String filePath) 
	{
		Properties properties = new Properties();

		FileInputStream is = null;
		try
		{
			is = new FileInputStream(filePath);
			properties.load(is);
		}
		catch (Exception e)
		{
			logger.error("Couldn't read properties file. Error message is : " + e.getMessage(),e);	
			throw new RuntimeException(e);
		}
		finally
		{
			if(is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					logger.error(e.getMessage(),e);
				}
			}
		}
		return properties;
	}
}
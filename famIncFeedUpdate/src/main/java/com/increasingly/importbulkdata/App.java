package com.increasingly.importbulkdata;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.increasingly.importbulkdata.util.AESBouncyCastle;
import com.increasingly.importbulkdata.util.GetProperties;

import static com.increasingly.importbulkdata.util.Constants.*;

import com.increasingly.importbulkdata.util.FormatLoggerMessage;

public class App 
{
	private static final Logger logger = LoggerFactory.getLogger(App.class.getClass());
	private static Properties properties = null;
	private final static String SERVERAPPLICATIONCONTEXT = "webapp/WEB-INF/applicationContext.xml";
	private final static String SERVERPROPSFILE = "webapp/WEB-INF/service.properties";
	public static AESBouncyCastle aes;
	
    public static void main( String[] args )
    {
    	//String baseFeedUrl = "http://127.0.0.1/increasingly/ProductsApi/products?authKey=123456";
		//feedId = 1;
    	try
		{		
    		aes = AESBouncyCastle.getInstance("AirFrameBegining");    		
			logger.info(LOG_INFO + "Application started..");
			new FileSystemXmlApplicationContext(SERVERAPPLICATIONCONTEXT);			
			properties = GetProperties.readProperties(SERVERPROPSFILE);			
			ImportClientData importClientDataObj = new ImportClientData();
			importClientDataObj.importClientData();
			logger.info(LOG_INFO + "Import data is scheduled to run at - "+ properties.getProperty("cron_timer"));
		}
		catch (Exception e)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "App" , "main", "Error reading prop file " ,"");
			logger.error(errorMessage,e);
			return;
		}
    }
    
}

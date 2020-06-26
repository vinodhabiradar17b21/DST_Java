package com.increasingly.importbulkdata;

import static com.increasingly.importbulkdata.util.Constants.*;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.importbulkdata.impl.ImportProductDataImpl;
import com.increasingly.importbulkdata.util.GetProperties;

public class ImportClientData
{
	private static final Logger logger = LoggerFactory.getLogger(ImportClientData.class.getClass());
	
	public void importClientData()
	{				
		logger.info(LOG_INFO + "Import of client data started.");
				
		ImportProductDataImpl importClientDataImpl = new ImportProductDataImpl();
		importClientDataImpl.runService();
		  
		logger.info(LOG_INFO + "Importing of client data completed.");		

	}
}
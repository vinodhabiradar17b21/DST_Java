package com.increasingly.importbulkdata.impl;

import static com.increasingly.importbulkdata.util.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.increasingly.importbulkdata.impl.db.BatchUpdateProductInventoryData;
import com.increasingly.importbulkdata.impl.db.DeleteTemporaryStorageData;
import com.increasingly.importbulkdata.impl.db.InsertOrUpdateProductInventoryDataImpl;
import com.increasingly.importbulkdata.util.FormatLoggerMessage;


public class UpdateProductInventoryDetails
{
  private static final Logger logger = LoggerFactory.getLogger(UpdateProductInventoryDetails.class.getName());
  ObjectMapper mapper = new ObjectMapper();
	
  public void updateProductInventory(Map<String,Object> input)
  {		
	    Integer feedId = (Integer)input.get(FEED_ID);
	    try 
	    {			
			BatchUpdateProductInventoryData batchUpdateProductInventoryData = BatchUpdateProductInventoryData.getInstance();
			Boolean isInventoryDataBulkInsertionSuccessful = batchUpdateProductInventoryData.runService(input);
			
			if(isInventoryDataBulkInsertionSuccessful)
			{  
				input.remove(PRODUCT_INVENTORY_LIST);
				InsertOrUpdateProductInventoryDataImpl insertOrUpdateProductInventoryDataImpl = InsertOrUpdateProductInventoryDataImpl.getInstance();
				Integer result = insertOrUpdateProductInventoryDataImpl.runService(input);
				
				if(result == 1)
				{
					logger.info(LOG_APPLICATION_FLOW + "Completed insertion /update of product inventory data to main table.");
				}
				else
				{
					logger.info(LOG_INFO + "Failed  to insert /update product inventory data to main tables.");
					DeleteTemporaryStorageData deleteTemporaryStorageData = new DeleteTemporaryStorageData();
					deleteTemporaryStorageData.deleteTemporaryProductInventoryData(input);
				}
			}
		
	    }
		catch(Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"updateProductInventory.java","failed to update the product inventory details for the feed id - " + feedId, "", "");
			logger.error(errorMessage, ex);	
			
			DeleteTemporaryStorageData deleteTemporaryStorageData = new DeleteTemporaryStorageData();
			deleteTemporaryStorageData.deleteTemporaryProductInventoryData(input);
		}
  }
}
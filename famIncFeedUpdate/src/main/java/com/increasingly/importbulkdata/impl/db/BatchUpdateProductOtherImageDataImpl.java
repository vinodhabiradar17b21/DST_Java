package com.increasingly.importbulkdata.impl.db;

import static com.increasingly.importbulkdata.util.Constants.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import com.increasingly.importbulkdata.impl.ProductImage;
import com.increasingly.importbulkdata.interfaces.ServiceInterface;
import com.increasingly.importbulkdata.util.FormatLoggerMessage;


public class BatchUpdateProductOtherImageDataImpl implements ServiceInterface<Boolean>
{
	private static BatchUpdateProductOtherImageDataImpl instance = null;
	private final static String dataSourceLookupName = "mysqlserver";
	private static final Logger logger = LoggerFactory.getLogger(BatchUpdateProductOtherImageDataImpl.class.getName());

	public static BatchUpdateProductOtherImageDataImpl getInstance()
	{
		if (instance == null)
		{
			instance = new BatchUpdateProductOtherImageDataImpl();
		}
		return instance;
	}

	public Boolean runService(Map<String, Object> input) 
	{
		JdbcTemplate jdbcTemplate = com.increasingly.db.BaseDB.getJdbcTemplate(dataSourceLookupName);

		final ArrayList<ProductImage> productOtherImageList = (ArrayList<ProductImage>) input.get(OTHER_IMAGE_LIST);		
		final Integer feedId = (Integer) input.get(FEED_ID);
	    
		String queryTmpl = "INSERT INTO client_product_other_images_temprory_list"
				+ "(product_id,image_url,feed_id) VALUES (?, ?, ?)";

		final int batchSize = 5000;

		try
		{
			return jdbcTemplate.execute(queryTmpl,new PreparedStatementCallback<Boolean>(){

				public Boolean doInPreparedStatement(PreparedStatement ps)
				throws SQLException, DataAccessException {

					int count = 0;

					for (ProductImage item : productOtherImageList)
					{
						ps.setBytes(1, item.getProductId().getBytes());
						ps.setNString(2, item.getImageUrl());						
						ps.setInt(3, feedId);
					
						ps.addBatch();

						if (++count % batchSize == 0)
						{
							ps.executeBatch();
						}
					}
					ps.executeBatch(); // insert remaining records
					return true;
				}
			});
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"BatchUpdateProductOtherImageDataImpl.java","failed to bulk insert the other product images for the feed id - " + feedId, "","");
			logger.error(errorMessage, ex);	

			Map<String, Object> inputFeedId = new HashMap<String,Object>();
			inputFeedId.put(FEED_ID, feedId);
			
			DeleteTemporaryStorageData deleteTemporaryStorageData = new DeleteTemporaryStorageData();
			deleteTemporaryStorageData.deleteTemporaryProductOtherImageDetails(inputFeedId);
			
			return false;
		}
	}
}
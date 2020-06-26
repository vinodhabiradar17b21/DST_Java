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

import com.increasingly.importbulkdata.impl.ProductCategoryMapping;
import com.increasingly.importbulkdata.interfaces.ServiceInterface;
import com.increasingly.importbulkdata.util.FormatLoggerMessage;


public class BatchUpdateCategoryDataImpl implements ServiceInterface<Boolean>
{
	private static BatchUpdateCategoryDataImpl instance = null;
	private final static String dataSourceLookupName = "mysqlserver";
	private static final Logger logger = LoggerFactory.getLogger(BatchUpdateCategoryDataImpl.class.getName());

	public static BatchUpdateCategoryDataImpl getInstance()
	{
		if (instance == null)
		{
			instance = new BatchUpdateCategoryDataImpl();
		}
		return instance;
	}

	public Boolean runService(Map<String, Object> input) 
	{
		JdbcTemplate jdbcTemplate = com.increasingly.db.BaseDB.getJdbcTemplate(dataSourceLookupName);

		final ArrayList<ProductCategoryMapping> categoryList = (ArrayList<ProductCategoryMapping>) input.get(CATEGORY_LIST);		
		final Integer feedId = (Integer) input.get(FEED_ID);
	    
		String queryTmpl = "INSERT INTO client_product_category_temprory_list"
				+ "(product_id,category_id,category_name,feed_id,category_level) VALUES (?, ?, ?, ?, ?)";

		final int batchSize = 5000;

		try
		{
			return jdbcTemplate.execute(queryTmpl,new PreparedStatementCallback<Boolean>(){

				public Boolean doInPreparedStatement(PreparedStatement ps)
				throws SQLException, DataAccessException {

					int count = 0;

					for (ProductCategoryMapping item : categoryList)
					{							
					
						ps.setBytes(1, item.getProductId().getBytes());
						ps.setNString(2, item.getCategoryId());
						ps.setNString(3, item.getCategoryName());						
						ps.setInt(4, feedId);
						ps.setInt(5, item.getCategoryLevel());
					
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
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"BatchUpdateCategoryDataImpl.java","failed to bulk insert the category details for the feed id - " + feedId, "", "");
			logger.error(errorMessage, ex);	
			
			Map<String, Object> inputFeedId = new HashMap<String,Object>();
			inputFeedId.put(FEED_ID, feedId);
			
			DeleteTemporaryStorageData deleteTemporaryStorageData = new DeleteTemporaryStorageData();
			deleteTemporaryStorageData.deleteTemporaryProductCategoryDetails(inputFeedId);

			return false;
		}
	}
}
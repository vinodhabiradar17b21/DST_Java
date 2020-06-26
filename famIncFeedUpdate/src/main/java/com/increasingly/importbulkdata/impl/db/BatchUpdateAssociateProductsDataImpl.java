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

import com.increasingly.importbulkdata.impl.AssociatedProducts;
import com.increasingly.importbulkdata.interfaces.ServiceInterface;
import com.increasingly.importbulkdata.util.FormatLoggerMessage;

public class BatchUpdateAssociateProductsDataImpl implements ServiceInterface<Boolean>
{
	private static BatchUpdateAssociateProductsDataImpl instance = null;
	private final static String dataSourceLookupName = "mysqlserver";
	private static final Logger logger = LoggerFactory.getLogger(BatchUpdateAssociateProductsDataImpl.class.getName());

	public static BatchUpdateAssociateProductsDataImpl getInstance()
	{
		if (instance == null)
		{
			instance = new BatchUpdateAssociateProductsDataImpl();
		}
		return instance;
	}

	public Boolean runService(Map<String, Object> input) 
	{
		JdbcTemplate jdbcTemplate = com.increasingly.db.BaseDB.getJdbcTemplate(dataSourceLookupName);

		final ArrayList<AssociatedProducts> associatedProductList = (ArrayList<AssociatedProducts>) input.get(ASSOCIATED_PRODUCT_LIST);		
		final Integer feedId = (Integer) input.get(FEED_ID);
	    
		String queryTmpl = "INSERT INTO client_associated_product_temprory_list"
				+ "(product_id,associated_product_id,association_type,feed_id) VALUES (?, ?, ?, ?)";

		final int batchSize = 5000;

		try
		{
			return jdbcTemplate.execute(queryTmpl,new PreparedStatementCallback<Boolean>(){

				public Boolean doInPreparedStatement(PreparedStatement ps)
				throws SQLException, DataAccessException {

					int count = 0;

					for (AssociatedProducts item : associatedProductList)
					{
						ps.setBytes(1, item.getProductId().getBytes());
						ps.setBytes(2, item.getAssociatedProductId().getBytes());
						ps.setNString(3, item.getAssociationType());						
						ps.setInt(4, feedId);
					
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
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"BatchUpdateAssociateProductsDataImpl.java","failed to bulk insert the category details for the feed id - " + feedId, "", "");
			logger.error(errorMessage, ex);	

			Map<String, Object> inputFeedId = new HashMap<String,Object>();
			inputFeedId.put(FEED_ID, feedId);
			
			DeleteTemporaryStorageData deleteTemporaryStorageData = new DeleteTemporaryStorageData();
			deleteTemporaryStorageData.deleteTemporaryAssociatedProductDetails(inputFeedId);
			return false;			
		}
	}
}
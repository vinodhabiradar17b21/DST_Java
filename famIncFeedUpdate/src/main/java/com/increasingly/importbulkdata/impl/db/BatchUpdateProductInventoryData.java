package com.increasingly.importbulkdata.impl.db;

import static com.increasingly.importbulkdata.util.Constants.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import com.increasingly.importbulkdata.impl.Product;
import com.increasingly.importbulkdata.interfaces.ServiceInterface;
import com.increasingly.importbulkdata.util.FormatLoggerMessage;

public class BatchUpdateProductInventoryData implements ServiceInterface<Boolean>
{
	private static BatchUpdateProductInventoryData instance = null;
	private final static String dataSourceLookupName = "mysqlserver";
	private static final Logger logger = LoggerFactory.getLogger(BatchUpdateProductInventoryData.class.getName());

	public static BatchUpdateProductInventoryData getInstance()
	{
		if (instance == null)
		{
			instance = new BatchUpdateProductInventoryData();
		}
		return instance;
	}

	public Boolean runService(Map<String, Object> input) 
	{
		JdbcTemplate jdbcTemplate = com.increasingly.db.BaseDB.getJdbcTemplate(dataSourceLookupName);

		final ArrayList<LinkedHashMap<String,Object>> productInventoryData = (ArrayList<LinkedHashMap<String,Object>>) input.get(PRODUCT_INVENTORY_LIST);		
		final Integer feedId = (Integer) input.get(FEED_ID);
	    
		String queryTmpl = "INSERT INTO client_product_inventory_details_temprory_list"
				+ "(feed_id,product_id,quantity,back_orders,use_config_backorders,min_sale_qty,use_config_min_sale_qty,"
				+ "max_sale_qty,use_config_max_sale_qty,is_in_stock,qty_to_identify_out_of_stock)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?)";

		final int batchSize = 5000;

		try
		{
			return jdbcTemplate.execute(queryTmpl,new PreparedStatementCallback<Boolean>(){

				public Boolean doInPreparedStatement(PreparedStatement ps)
				throws SQLException, DataAccessException {

					int count = 0;
										
					for (Map<String,Object> item : productInventoryData)
					{	
						String productId = (String)item.get(PRODUCT_ID);
						
						try
						{
							Integer quantity  = 0;
							
							if(item.get("qty") != null)
							{
								quantity  = (int)(Double.parseDouble((String)item.get("qty")));
							}
							
				        	Integer isBackOrdersAllowed = Integer.parseInt((String)item.get("backorders"));
				        	Integer useConfigBackOrders = Integer.parseInt((String)item.get("use_config_backorders"));	        	
				        	Integer minSaleQuantity = (int)(Double.parseDouble((String)item.get("min_sale_qty")));
				        	
				        	Integer useConfigMinSaleQty = Integer.parseInt(((String)item.get("use_config_min_sale_qty")));
				        	Integer maxSaleQuantity = (int)(Double.parseDouble((String)item.get("max_sale_qty")));
				        	Integer useConfigMaxSaleQty = Integer.parseInt(((String)item.get("use_config_max_sale_qty")));
				        	Integer isInStock = Integer.parseInt(((String)item.get("is_in_stock")));
				        	Integer qtyToIdentifyOutOfStockStatus = (int)(Double.parseDouble((String)item.get("min_qty")));
				        	
							ps.setInt(1, feedId);
							ps.setBytes(2, productId.getBytes());
							ps.setInt(3,quantity);
							
							if(isBackOrdersAllowed != null)
							{
							  ps.setInt(4, isBackOrdersAllowed);
							}
							else
							{
							  ps.setNull(4, Types.INTEGER);
							}
							
							if(useConfigBackOrders != null)
							{							
								if(useConfigBackOrders == 1)
								  ps.setBoolean(5, true);	
								else
								 ps.setBoolean(5, false);	
							}
							else
							{
							  ps.setNull(5, Types.BIT);
							}
							
							if(minSaleQuantity != null)
							{							
							  ps.setInt(6, minSaleQuantity);	
							}
							else
							{
							  ps.setNull(6, Types.INTEGER);
							}
							
							if(useConfigMinSaleQty != null)
							{							
								if(useConfigMinSaleQty == 1)
								 ps.setBoolean(7, true);	
								else
								 ps.setBoolean(7, false);	
							}
							else
							{
							  ps.setNull(7, Types.BIT);
							}
							
							if(maxSaleQuantity != null)
							{							
							  ps.setInt(8, maxSaleQuantity);	
							}
							else
							{
							  ps.setNull(8, Types.INTEGER);
							}
							
							if(useConfigMaxSaleQty != null)
							{							
								if(useConfigMaxSaleQty == 1)
								  ps.setBoolean(9, true);	
								else
								  ps.setBoolean(9, false);		
							}
							else
							{
							  ps.setNull(9, Types.BIT);
							}
							
							if(isInStock != null)
							{							
								if(isInStock == 1)
							     ps.setBoolean(10, true);	
								else
								 ps.setBoolean(10, false);			
							}
							else
							{
							  ps.setNull(10, Types.BIT);
							}
							
							if(qtyToIdentifyOutOfStockStatus != null)
							{							
							  ps.setInt(11, qtyToIdentifyOutOfStockStatus);	
							}
							else
							{
							  ps.setNull(11, Types.INTEGER);
							}
							
							ps.addBatch();
						}
						catch(Exception ex)
						{
							String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"BatchUpdateProductInventoryData.java","failed to add/update the product inventory details to bulk list for the feed id - " + feedId + " ,product id - "+ productId, "", "");
							logger.error(errorMessage, ex);	
						}

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
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"BatchUpdateProductInventoryData.java","failed to bulk insert the product inventory details for the feed id - " + feedId, "", "");
			logger.error(errorMessage, ex);	

			Map<String, Object> inputFeedId = new HashMap<String,Object>();
			inputFeedId.put(FEED_ID, feedId);
			
			DeleteTemporaryStorageData deleteTemporaryStorageData = new DeleteTemporaryStorageData();
			deleteTemporaryStorageData.deleteTemporaryProductDetails(inputFeedId);
			return false;
		}
	}
}
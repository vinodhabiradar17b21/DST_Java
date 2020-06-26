package com.increasingly.importbulkdata.impl.db;

import static com.increasingly.importbulkdata.util.Constants.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import com.increasingly.importbulkdata.impl.ProductOption;
import com.increasingly.importbulkdata.interfaces.ServiceInterface;
import com.increasingly.importbulkdata.util.FormatLoggerMessage;

public class BatchUpdateProductOptionsImpl implements ServiceInterface<Boolean>
{
	private static BatchUpdateProductOptionsImpl instance = null;
	private final static String dataSourceLookupName = "mysqlserver";
	private static final Logger logger = LoggerFactory.getLogger(BatchUpdateProductOptionsImpl.class.getName());

	public static BatchUpdateProductOptionsImpl getInstance()
	{
		if (instance == null)
		{
			instance = new BatchUpdateProductOptionsImpl();
		}
		return instance;
	}

	public Boolean runService(Map<String, Object> input) 
	{
		JdbcTemplate jdbcTemplate = com.increasingly.db.BaseDB.getJdbcTemplate(dataSourceLookupName);

		final List<ProductOption> productOptionsList = (ArrayList<ProductOption>) input.get(PRODUCT_OPTIONS_LIST);		
		final Integer feedId = (Integer) input.get(FEED_ID);
	    				
		String queryTmpl = "INSERT INTO product_attribute_details_temprory_list"
				+ "(feed_id,parent_product_id,child_product_id,child_product_sku,store_id,attribute_code,attribute_id,attribute_label,option_id"
				+",option_text,option_image_url,is_percent,pricing_value,color_code,quantity,price,special_price) VALUES (?, ?, ?, ?,?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		
		final int batchSize = 5000;

		try
		{
			return jdbcTemplate.execute(queryTmpl,new PreparedStatementCallback<Boolean>(){

				public Boolean doInPreparedStatement(PreparedStatement ps)
				throws SQLException, DataAccessException {

					int count = 0;

					for (ProductOption option : productOptionsList)
					{
						ps.setInt(1, feedId);
						ps.setBytes(2, option.getParentProductId().getBytes());
						
						if(option.getChildProductId() != null && !option.getChildProductId().isEmpty())
						{
						  ps.setBytes(3, option.getChildProductId().getBytes());
						}
						else
						{
						  ps.setNull(3,Types.BINARY);	
						}
						
						if(option.getChildProductSku() != null && !option.getChildProductSku().isEmpty())
						{
						  ps.setNString(4, option.getChildProductSku());		
						}
						else
						{
							ps.setNull(4,Types.NVARCHAR);
						}
						
						if(option.getStoreId() != null && !option.getStoreId().isEmpty())
						{
						  ps.setInt(5,Integer.parseInt(option.getStoreId()));		
						}
						else
						{
							ps.setNull(5,Types.INTEGER);
						}
						
						if(option.getAttributeCode() != null && !option.getAttributeCode().isEmpty())
						{
						  ps.setNString(6,option.getAttributeCode());		
						}
						else
						{
							ps.setNull(6,Types.NVARCHAR);
						}
						
						if(option.getAttributeId() != null && !option.getAttributeId().isEmpty())
						{
						  ps.setBytes(7,option.getAttributeId().getBytes());		
						}
						else
						{
							ps.setNull(7,Types.BINARY);
						}
						
						if(option.getAttributeLabel() != null && !option.getAttributeLabel().isEmpty())
						{
						  ps.setNString(8,option.getAttributeLabel());		
						}
						else
						{
							ps.setNull(8,Types.NVARCHAR);
						}
						
						if(option.getOptionId() != null && !option.getOptionId().isEmpty())
						{
						  ps.setBytes(9,option.getOptionId().getBytes());		
						}
						else
						{
							ps.setNull(9,Types.BINARY);
						}
																		
						if(option.getOptionText() != null && !option.getOptionText().isEmpty())
						{
						  ps.setNString(10,option.getOptionText());		
						}
						else
						{
							ps.setNull(10,Types.NVARCHAR);
						}
						
						if(option.getOptionImageUrl() != null && !option.getOptionImageUrl().isEmpty())
						{
						  ps.setNString(11,option.getOptionImageUrl());		
						}
						else
						{
							ps.setNull(11,Types.NVARCHAR);
						}
						
						if(option.getIsPercent() != null)
						{
						  ps.setInt(12,option.getIsPercent());		
						}
						else
						{
							ps.setNull(12,Types.INTEGER);
						}
						
						if(option.getPricingValue() != null && !option.getPricingValue().isEmpty())
						{
						  ps.setDouble(13,Double.parseDouble(option.getPricingValue()));		
						}
						else
						{
							ps.setNull(13,Types.DECIMAL);
						}
						
						if(option.getColorCode() != null && !option.getColorCode().isEmpty())
						{
						  ps.setNString(14,option.getColorCode());		
						}
						else
						{
							ps.setNull(14,Types.NVARCHAR);
						}
						
						if(option.getQuantity() != null)
                        {
                          ps.setInt(15,option.getQuantity());       
                        }
                        else
                        {
                            ps.setNull(15,Types.INTEGER);
                        }
                       
                        if(option.getPrice() != null && !option.getPrice().isEmpty())
                        {  
                            ps.setDouble(16, Double.parseDouble(option.getPrice()));
                        }
                        else
                        {
                            ps.setNull(16,Types.DOUBLE);
                        }
                       
                        if(option.getSpecialPrice() != null && !option.getSpecialPrice().isEmpty())
                        {  
                            ps.setDouble(17, Double.parseDouble(option.getSpecialPrice()));
                        }
                        else
                        {
                            ps.setNull(17,Types.DOUBLE);
                        }

						
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
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"BatchUpdateProductOptionsImpl.java","failed to bulk insert the product option details for the feed id - " + feedId, "", "");
			logger.error(errorMessage, ex);	

			Map<String, Object> inputFeedId = new HashMap<String,Object>();
			inputFeedId.put(FEED_ID, feedId);
			
			DeleteTemporaryStorageData deleteTemporaryStorageData = new DeleteTemporaryStorageData();
			deleteTemporaryStorageData.deleteTemporaryProductOptionDetails(inputFeedId);
			return false;			
		}
	}
}
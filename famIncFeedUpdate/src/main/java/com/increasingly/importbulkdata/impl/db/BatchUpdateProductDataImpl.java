package com.increasingly.importbulkdata.impl.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import com.increasingly.importbulkdata.impl.Product;
import com.increasingly.importbulkdata.interfaces.ServiceInterface;
import com.increasingly.importbulkdata.util.FormatLoggerMessage;

import static com.increasingly.importbulkdata.util.Constants.*;

public class BatchUpdateProductDataImpl implements ServiceInterface<Boolean>
{
	private static BatchUpdateProductDataImpl instance = null;
	private final static String dataSourceLookupName = "mysqlserver";
	private static final Logger logger = LoggerFactory.getLogger(BatchUpdateProductDataImpl.class.getName());

	public static BatchUpdateProductDataImpl getInstance()
	{
		if (instance == null)
		{
			instance = new BatchUpdateProductDataImpl();
		}
		return instance;
	}

	public Boolean runService(Map<String, Object> input) 
	{
		JdbcTemplate jdbcTemplate = com.increasingly.db.BaseDB.getJdbcTemplate(dataSourceLookupName);

		final ArrayList<Product> productList = (ArrayList<Product>) input.get(PRODUCT_LIST);		
		final Integer feedId = (Integer) input.get(FEED_ID);

		String queryTmpl = "INSERT INTO client_product_details_temprory_list"
				+ "(feed_id,product_id,product_name,product_sku,product_url,image_url,description,short_description,price,"
				+ "special_price,quantity,product_type,manufacturer,color,size,weight,client_product_status,created_date_at_source,updated_date_at_source,"
				+ "has_associated_products,has_related_products,has_up_sell_products,has_cross_sell_products,has_other_images,visibility,field1,field2,field3,field4,field5,field6)"
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		final int batchSize = 5000;

		try
		{
			return jdbcTemplate.execute(queryTmpl,new PreparedStatementCallback<Boolean>(){

				public Boolean doInPreparedStatement(PreparedStatement ps)
				throws SQLException, DataAccessException {

					int count = 0;

					for (Product item : productList)
					{				
						try
						{
							ps.setInt(1, feedId);
							ps.setBytes(2, item.getProductId().getBytes());
							ps.setNString(3, item.getProductName());
							
							if(item.getProductSku() != null && !item.getProductSku().isEmpty())
							{
							   ps.setNString(4, item.getProductSku());	
							}
							else
							{
							  ps.setNull(4, Types.NVARCHAR);
							}
							
							ps.setNString(5, item.getProductUrl());
							
							if(item.getImageUrl() != null && !item.getImageUrl().isEmpty())
							{
								ps.setNString(6, item.getImageUrl());
							}
							else
							{
								ps.setNull(6, Types.NVARCHAR);
							}
							
							if(item.getDescription() != null && !item.getDescription().isEmpty())
							{
								String description = "";
								
								if(item.getDescription().length() >= 8000)
								{
									description = item.getDescription().substring(0, 7995) + "...";								  
								}
								else
								{
									description = item.getDescription();									
								}
								
								ps.setNString(7, description);
							}
							else
							{
								ps.setNull(7, Types.NVARCHAR);
							}
							
							if(item.getShortDescription() != null && !item.getShortDescription().isEmpty())
							{ 
								String shortDescription = "";
								if(item.getShortDescription().length() >= 500)
								{
								  shortDescription = item.getShortDescription().substring(0, 495) + "...";								  
								}
								else
								{
									shortDescription = item.getShortDescription();									
								}
								
								ps.setNString(8, shortDescription);
							}
							else
							{
								ps.setNull(8, Types.NVARCHAR);
							}
							
							if(item.getProductPrice() != null && !item.getProductPrice().isEmpty())
							{   
								ps.setDouble(9, Double.parseDouble(item.getProductPrice()));
							}
							else
							{
								ps.setNull(9,Types.DOUBLE);
							}
							
							if(item.getSpecialPrice() != null && !item.getSpecialPrice().isEmpty())
							{   
								ps.setDouble(10, Double.parseDouble(item.getSpecialPrice()));
							}
							else
							{
								ps.setNull(10,Types.DOUBLE);
							}
							
							if(item.getQunatity() != null)
							{
								ps.setInt(11, item.getQunatity());
							}
							else
							{
								ps.setNull(11,Types.INTEGER);
							}						
							
							if(item.getProductType() != null && !item.getProductType().isEmpty())
							{
								ps.setNString(12, item.getProductType());
							}
							else
							{
								ps.setNString(12,"simple");
							}
							
							if(item.getManufacturer() != null && !item.getManufacturer().isEmpty())
							{   
								ps.setNString(13, item.getManufacturer());
							}
							else
							{
								ps.setNull(13,Types.NVARCHAR);
							}
							
							if(item.getColor() != null && !item.getColor().isEmpty())
							{   
								ps.setNString(14, item.getColor());
							}
							else
							{
								ps.setNull(14,Types.NVARCHAR);
							}
							
							if(item.getSize() != null && !item.getSize().isEmpty())
							{   
								ps.setNString(15, item.getSize());
							}
							else
							{
								ps.setNull(15,Types.NVARCHAR);
							}
							
							if(item.getWeight() != null && !item.getWeight().isEmpty())
							{   
								ps.setDouble(16, Double.parseDouble(item.getWeight()));
							}
							else
							{
								ps.setNull(16,Types.DOUBLE);
							}
													
							if(item.getProductStatus() != null)
							{   
								ps.setInt(17, item.getProductStatus());
							}
							else
							{
								ps.setInt(17,1);
							}
							
							if(item.getCreatedDate() != null && !item.getCreatedDate().isEmpty())
							{   
								ps.setString(18, item.getCreatedDate());
							}
							else
							{
								ps.setNull(18,Types.VARCHAR);
							}					
							
							
							if(item.getUpdatedDate() != null && !item.getUpdatedDate().isEmpty())
							{   
								ps.setString(19, item.getUpdatedDate());
							}
							else
							{
								ps.setNull(19,Types.VARCHAR);
							}												
							
							ps.setBoolean(20, item.getHasAssociatedProducts());
							ps.setBoolean(21, item.getHasRelatedProducts());
							ps.setBoolean(22, item.getHasUpSellProducts());
							ps.setBoolean(23, item.getHasCrossSellProducts());
							ps.setBoolean(24, item.getHasOtherImages());
							
							if(item.getVisibility() != null && !item.getVisibility().isEmpty())
							{
								ps.setNString(25, item.getVisibility());
							}
							else
							{
								ps.setNString(25, "Visible");
							}
							
							if(item.getField1() != null && !item.getField1().isEmpty())
							{
								ps.setNString(26, item.getField1());
							}
							else
							{
								ps.setNull(26, Types.NVARCHAR);
							}
							
							if(item.getField2() != null && !item.getField2().isEmpty())
							{
								ps.setNString(27, item.getField2());
							}
							else
							{
								ps.setNull(27, Types.NVARCHAR);
							}
							
							if(item.getField3() != null && !item.getField3().isEmpty())
							{
								ps.setNString(28, item.getField3());
							}
							else
							{
								ps.setNull(28, Types.NVARCHAR);
							}
							
							if(item.getField4() != null && !item.getField4().isEmpty())
							{
								ps.setNString(29, item.getField4());
							}
							else
							{
								ps.setNull(29, Types.NVARCHAR);
							}
							
							if(item.getField5() != null && !item.getField5().isEmpty())
							{
								ps.setNString(30, item.getField5());
							}
							else
							{
								ps.setNull(30, Types.NVARCHAR);
							}
							
							if(item.getField6() != null && !item.getField6().isEmpty())
							{
								ps.setNString(31, item.getField6());
							}
							else
							{
								ps.setNull(31, Types.NVARCHAR);
							}
							
							ps.addBatch();
						}
						catch(Exception ex)
						{
							String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"BatchUpdateProductDataImpl.java","failed to add the product details to bulk list for the feed id - " + feedId + " ,product id - "+ item.getProductId(), "", "");
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
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"BatchUpdateProductDataImpl.java","failed to bulk insert the product details for the feed id - " + feedId, "", "");
			logger.error(errorMessage, ex);	

			Map<String, Object> inputFeedId = new HashMap<String,Object>();
			inputFeedId.put(FEED_ID, feedId);
			
			DeleteTemporaryStorageData deleteTemporaryStorageData = new DeleteTemporaryStorageData();
			deleteTemporaryStorageData.deleteTemporaryProductDetails(inputFeedId);
			return false;
		}
	}
}
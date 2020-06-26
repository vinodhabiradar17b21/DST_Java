package com.increasingly.importbulkdata.impl.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.db.BaseDB;
import com.increasingly.importbulkdata.util.FormatLoggerMessage;

import static com.increasingly.importbulkdata.util.Constants.*;

public class DeleteTemporaryStorageData
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static final Logger logger = LoggerFactory.getLogger(DeleteTemporaryStorageData.class.getClass());
			
	public void deleteTemporaryProductDetailsAllData(Map<String,Object> input) 
	{
		deleteTemporaryProductDetails(input);	
		deleteTemporaryProductCategoryDetails(input);
		deleteTemporaryAssociatedProductDetails(input);
		deleteTemporaryProductOtherImageDetails(input);
	}
	
	public void deleteTemporaryProductDetails(Map<String,Object> input) 
	{
    
		Connection conn = null;
		
		try 
		{
			Integer feedId = (Integer)input.get(FEED_ID);
			conn = BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource().getConnection();
			CallableStatement cStmt = conn.prepareCall("delete from client_product_details_temprory_list where feed_id=?");
					
			cStmt.setInt(1, feedId);		
					
			cStmt.executeUpdate();
			
		} 
		catch (Exception ex) 
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "deleteTemporaryProductDetails" , "Error Occured while deleting inconsitant product data" ,"","");
			logger.error(errorMessage,ex);
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	public void deleteTemporaryProductCategoryDetails(Map<String,Object> input) 
	{
    
		Connection conn = null;
		
		try 
		{
			Integer feedId = (Integer)input.get(FEED_ID);
			conn = BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource().getConnection();
			CallableStatement cStmt = conn.prepareCall("delete from client_product_category_temprory_list where feed_id=?");
					
			cStmt.setInt(1, feedId);		
					
			cStmt.executeUpdate();
			
		} 
		catch (Exception ex) 
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "deleteTemporaryProductCategoryDetails" , "Error Occured while deleting inconsitant product category data" ,"","");
			logger.error(errorMessage,ex);
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	public void deleteTemporaryAssociatedProductDetails(Map<String,Object> input) 
	{
    
		Connection conn = null;
		
		try 
		{
			Integer feedId = (Integer)input.get(FEED_ID);
			conn = BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource().getConnection();
			CallableStatement cStmt = conn.prepareCall("delete from client_associated_product_temprory_list where feed_id=?");
					
			cStmt.setInt(1, feedId);		
					
			cStmt.executeUpdate();
			
		} 
		catch (Exception ex) 
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "deleteTemporaryAssociatedProductDetails" , "Error Occured while deleting inconsitant associated product list" ,"","");
			logger.error(errorMessage,ex);
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	public void deleteTemporaryProductOtherImageDetails(Map<String,Object> input) 
	{
    
		Connection conn = null;
		
		try 
		{
			Integer feedId = (Integer)input.get(FEED_ID);
			conn = BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource().getConnection();
			CallableStatement cStmt = conn.prepareCall("delete from client_product_other_images_temprory_list where feed_id=?");
					
			cStmt.setInt(1, feedId);		
					
			cStmt.executeUpdate();
			
		} 
		catch (Exception ex) 
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "deleteTemporaryProductOtherImageDetails" , "Error Occured while deleting inconsitant product other image list" ,"","");
			logger.error(errorMessage,ex);
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	public void deleteTemporaryOrderItemDetails(Map<String,Object> input) 
	{
    
		Connection conn = null;
		
		try 
		{
			Integer clientId = (Integer)input.get(CLIENT_ID);
			conn = BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource().getConnection();
			CallableStatement cStmt = conn.prepareCall("delete from order_item_details_temporary_storage where client_id=?");
					
			cStmt.setInt(1, clientId);		
					
			cStmt.executeUpdate();
			
		} 
		catch (Exception ex) 
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "deleteTemporaryOrderItemDetails" , "Error Occured while deleting inconsitant order item data" ,"","");
			logger.error(errorMessage,ex);
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	public void deleteTemporaryOrderDetails(Map<String,Object> input) 
	{
    
		Connection conn = null;
		
		try 
		{
			Integer clientId = (Integer)input.get(CLIENT_ID);
			conn = BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource().getConnection();
			CallableStatement cStmt = conn.prepareCall("delete from order_details_temporary_storage where client_id=?");
					
			cStmt.setInt(1, clientId);		
					
			cStmt.executeUpdate();
			
			deleteTemporaryOrderItemDetails(input);
			
		} 
		catch (Exception ex) 
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "deleteTemporaryOrderDetails" , "Error Occured while deleting inconsitant order data" ,"","");
			logger.error(errorMessage,ex);
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	

	}
	
	public void deleteTemporaryProductInventoryData(Map<String,Object> input) 
	{
    
		Connection conn = null;
		
		try 
		{
			Integer feedId = (Integer)input.get(FEED_ID);
			conn = BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource().getConnection();
			CallableStatement cStmt = conn.prepareCall("delete from client_product_inventory_details_temprory_list where feed_id=?");
					
			cStmt.setInt(1, feedId);		
					
			cStmt.executeUpdate();
			
		} 
		catch (Exception ex) 
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "deleteTemporaryProductInventoryData" , "Error Occured while deleting inconsitant product inventory data" ,"","");
			logger.error(errorMessage,ex);
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	public void deleteTemporaryProductOptionDetails(Map<String,Object> input) 
	{
    
		Connection conn = null;
		
		try 
		{
			Integer feedId = (Integer)input.get(FEED_ID);
			conn = BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource().getConnection();
			CallableStatement cStmt = conn.prepareCall("delete from product_attribute_details_temprory_list where feed_id=?");
					
			cStmt.setInt(1, feedId);		
					
			cStmt.executeUpdate();
			
		} 
		catch (Exception ex) 
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR , "deleteTemporaryProductOptionDetails" , "Error Occured while deleting inconsitant product option details" ,"","");
			logger.error(errorMessage,ex);
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
}
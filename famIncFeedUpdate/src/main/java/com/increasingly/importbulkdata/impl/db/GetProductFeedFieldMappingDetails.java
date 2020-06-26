package com.increasingly.importbulkdata.impl.db;


import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.db.BaseDB;
import com.increasingly.importbulkdata.interfaces.ServiceInterface;
import static com.increasingly.importbulkdata.util.Constants.*;


public class GetProductFeedFieldMappingDetails extends StoredProcedure implements ServiceInterface<ArrayList<Map<String, String>>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static String SPROC_NAME = "Get_Product_Feed_Field_Mapping_Details";	
	private static GetProductFeedFieldMappingDetails instance = null;
	
	private GetProductFeedFieldMappingDetails()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("FeedId", Types.VARCHAR));
		compile();		
	}

	public static GetProductFeedFieldMappingDetails getInstance()
	{
		if (instance == null)
		{
			instance = new GetProductFeedFieldMappingDetails();
		}
		return instance;
	}

	public ArrayList<Map<String, String>> runService(Map<String, Object> input)
	{
		Integer feedId = (Integer) input.get(FEED_ID);	
		ArrayList<Map<String, String>> fieldMappingDetails = (ArrayList<Map<String, String>>) execute(feedId).get("#result-set-1");
		return fieldMappingDetails;
	}
}
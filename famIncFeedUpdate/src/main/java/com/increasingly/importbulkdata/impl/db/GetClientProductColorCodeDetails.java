package com.increasingly.importbulkdata.impl.db;

import static com.increasingly.importbulkdata.util.Constants.FEED_ID;

import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.db.BaseDB;
import com.increasingly.importbulkdata.interfaces.ServiceInterface;

public class GetClientProductColorCodeDetails extends StoredProcedure implements ServiceInterface<LinkedHashMap<String, Object>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static String SPROC_NAME = "Get_Client_Product_Color_Code_Details";	
	private static GetClientProductColorCodeDetails instance = null;
	
	private GetClientProductColorCodeDetails()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("FeedId", Types.VARCHAR));
		compile();		
	}

	public static GetClientProductColorCodeDetails getInstance()
	{
		if (instance == null)
		{
			instance = new GetClientProductColorCodeDetails();
		}
		return instance;
	}

	public LinkedHashMap<String, Object> runService(Map<String, Object> input)
	{
		LinkedHashMap<String,Object> colorMappingList = new LinkedHashMap<String,Object>();
		Integer feedId = (Integer) input.get(FEED_ID);	
		ArrayList<Map<String, Object>> fieldMappingDetails = (ArrayList<Map<String, Object>>) execute(feedId).get("#result-set-1");
		
		for(Map<String, Object> colorMap:fieldMappingDetails)
		{
			colorMappingList.put((String)colorMap.get("option_id"), colorMap.get("color_code"));
		}
		
		return colorMappingList;
	}
}
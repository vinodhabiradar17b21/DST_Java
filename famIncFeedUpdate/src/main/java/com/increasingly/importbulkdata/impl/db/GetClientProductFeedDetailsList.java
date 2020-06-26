package com.increasingly.importbulkdata.impl.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.db.BaseDB;
import com.increasingly.importbulkdata.interfaces.ServiceInterface;

import static com.increasingly.importbulkdata.util.Constants.*;

public class GetClientProductFeedDetailsList extends StoredProcedure implements ServiceInterface<Map<Integer, Map<String, Object>>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static String SPROC_NAME = "Get_Client_Product_Feed_Details_List";	
	private static GetClientProductFeedDetailsList instance = null;
	
	private GetClientProductFeedDetailsList()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);	
		compile();		
	}

	public static GetClientProductFeedDetailsList getInstance()
	{
		if (instance == null)
		{
			instance = new GetClientProductFeedDetailsList();
		}
		return instance;
	}

	public Map<Integer, Map<String, Object>> runService(Map<String, Object> input)
	{
		ArrayList<Map<String, Object>> results = (ArrayList<Map<String, Object>>) execute().get("#result-set-1");
		Map<Integer, Map<String, Object>> feedList = new HashMap<Integer, Map<String,Object>>();
		for(Map<String, Object> item: results){
			feedList.put((Integer) item.get(FEED_ID), item);
		}
		return feedList;
	}
}
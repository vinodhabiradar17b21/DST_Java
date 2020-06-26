package com.increasingly.recommender.impl.db;

import static com.increasingly.recommender.constants.Constants.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.recommender.DB.BaseDB;
import com.increasingly.recommender.interfaces.ServiceInterface;

public class InternalCategoryIdList extends StoredProcedure implements ServiceInterface<ArrayList<LinkedHashMap<String, Object>>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Get_Internal_Category_Id_List";
	private static InternalCategoryIdList instance = null;
	
	private InternalCategoryIdList()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("FeedId", Types.INTEGER));
		declareParameter(new SqlParameter("CustomerCategoryIdList", Types.VARCHAR));
		compile();			
	}

	public static InternalCategoryIdList getInstance()
	{
		if (instance == null)
		{
			instance = new InternalCategoryIdList();
		}
		return instance;
	}

	public ArrayList<LinkedHashMap<String, Object>> runService(Map<String, Object> input) 
	{	
		Integer feedId = (Integer) input.get(FEED_ID);
		String categoryIdList = (String) input.get(CATEGORY_ID_LIST);
		
		ArrayList<LinkedHashMap<String, Object>> tempInternalCategoryIdList = (ArrayList<LinkedHashMap<String, Object>>)execute(feedId,categoryIdList).get("#result-set-1");
		return tempInternalCategoryIdList;
	}
}
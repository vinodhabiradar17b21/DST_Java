package com.increasingly.recommender.impl.db;

import static com.increasingly.recommender.constants.Constants.*;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.recommender.DB.BaseDB;
import com.increasingly.recommender.interfaces.ServiceInterface;

public class MultipleProductIdBundleList extends StoredProcedure implements ServiceInterface<ArrayList<Map<String, Object>>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Get_Multiple_Product_ID_BundleList";
	private static MultipleProductIdBundleList instance = null;
	
	private MultipleProductIdBundleList()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("ConfigId", Types.INTEGER));
		declareParameter(new SqlParameter("InternalProductIdList", Types.VARCHAR));
		compile();		
	}

	public static MultipleProductIdBundleList getInstance()
	{
		if (instance == null)
		{
			instance = new MultipleProductIdBundleList();
		}
		return instance;
	}

	public ArrayList<Map<String, Object>> runService(Map<String, Object> input) 
	{	
		String internalProductIdList = (String) input.get(PRODUCT_ID_LIST);		
		Integer configId = (Integer) input.get(CONFIG_ID);	
		ArrayList<Map<String, Object>> bundleIdList = (ArrayList<Map<String, Object>>)execute(configId,internalProductIdList).get("#result-set-1");
		return bundleIdList;
	}
}
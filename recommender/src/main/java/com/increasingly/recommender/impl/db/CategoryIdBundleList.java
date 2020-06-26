package com.increasingly.recommender.impl.db;

import static com.increasingly.recommender.constants.Constants.*;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.recommender.DB.BaseDB;
import com.increasingly.recommender.interfaces.ServiceInterface;

public class CategoryIdBundleList extends StoredProcedure implements ServiceInterface<Map<String,ArrayList<Map<String, Object>>>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Get_Category_ID_BundleList";
	private static CategoryIdBundleList instance = null;
	
	private CategoryIdBundleList()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("InternalCategoryIdList", Types.VARCHAR));
		declareParameter(new SqlParameter("ConfigId", Types.INTEGER));
		declareParameter(new SqlParameter("BundleTypeId", Types.INTEGER));
		declareParameter(new SqlParameter("NoOfBundles", Types.INTEGER));

		compile();		
	}

	public static CategoryIdBundleList getInstance()
	{
		if (instance == null)
		{
			instance = new CategoryIdBundleList();
		}
		return instance;
	}

	public Map<String,ArrayList<Map<String, Object>>> runService(Map<String, Object> input) 
	{	
		int internalCategoryId =  (Integer) input.get(CATEGORY_ID_LIST);	
		int clientId = (Integer) input.get(CLIENT_ID);
		int bundleTypeId = Integer.parseInt(input.get(BUNDLE_TYPE_ID).toString());
		Integer noOfBundles = (Integer) input.get(NO_OF_BUNDLES);
		Map<String,Object> resultSets = execute(internalCategoryId,clientId,bundleTypeId,noOfBundles);
		ArrayList<Map<String, Object>> bundleIdList = (ArrayList<Map<String, Object>>)resultSets.get("#result-set-1");
		ArrayList<Map<String, Object>> abandonedBundleIdList = (ArrayList<Map<String, Object>>)resultSets.get("#result-set-2");
		Map<String,ArrayList<Map<String, Object>>> finalBundleIdList = new HashMap<String, ArrayList<Map<String,Object>>>();
		finalBundleIdList.put(BUNDLE_ID_LIST,bundleIdList);
		finalBundleIdList.put(ABANDONED_CART_BUNDLE_ID_LIST,abandonedBundleIdList);
		return finalBundleIdList;
	}
}
package com.increasingly.recommender.impl.db;

import static com.increasingly.recommender.constants.Constants.*;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.recommender.DB.BaseDB;
import com.increasingly.recommender.interfaces.ServiceInterface;

public class ProductIdBundleList extends StoredProcedure implements ServiceInterface<Map<String,ArrayList<Map<String, Object>>>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Get_Product_ID_BundleList";
	private static ProductIdBundleList instance = null;
	
	private ProductIdBundleList()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("ConfigId", Types.INTEGER));
		declareParameter(new SqlParameter("InternalProductId", Types.INTEGER));		
		declareParameter(new SqlParameter("BundleTypeId", Types.INTEGER));
		declareParameter(new SqlParameter("NoOfBundles", Types.INTEGER));
		compile();		
	}

	public static ProductIdBundleList getInstance()
	{
		if (instance == null)
		{
			instance = new ProductIdBundleList();
		}
		return instance;
	}

	public Map<String,ArrayList<Map<String, Object>>> runService(Map<String, Object> input) 
	{	
		Integer internalProductId = Integer.parseInt(input.get(PRODUCT_ID_LIST).toString());		
		Integer configId = (Integer) input.get(CONFIG_ID);			
		Integer bundleTypeId = (Integer) input.get(BUNDLE_TYPE_ID);
		Integer noOfBundles = (Integer) input.get(NO_OF_BUNDLES);
		Map<String,Object> resultSets = execute(configId,internalProductId,bundleTypeId,noOfBundles);
		ArrayList<Map<String, Object>> bundleIdList = (ArrayList<Map<String, Object>>)resultSets.get("#result-set-1");
		ArrayList<Map<String, Object>> abandonedBundleIdList = (ArrayList<Map<String, Object>>)resultSets.get("#result-set-2");
		Map<String,ArrayList<Map<String, Object>>> finalBundleIdList = new HashMap<String, ArrayList<Map<String,Object>>>();
		finalBundleIdList.put(BUNDLE_ID_LIST,bundleIdList);
		finalBundleIdList.put(ABANDONED_CART_BUNDLE_ID_LIST,abandonedBundleIdList);
		return finalBundleIdList;
	}
}
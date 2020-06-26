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

public class CheckoutPageProductIdBundleList extends StoredProcedure implements ServiceInterface<Map<String,ArrayList<Map<String, Object>>>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Get_Checkout_Page_Product_ID_BundleList";
	private static CheckoutPageProductIdBundleList instance = null;
	
	private CheckoutPageProductIdBundleList()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("ConfigId", Types.INTEGER));
		declareParameter(new SqlParameter("InternalProductIdList", Types.VARCHAR));
		declareParameter(new SqlParameter("bundleTypeId", Types.INTEGER));
		declareParameter(new SqlParameter("NoOfBundles", Types.INTEGER));
		compile();		
	}

	public static CheckoutPageProductIdBundleList getInstance()
	{
		if (instance == null)
		{
			instance = new CheckoutPageProductIdBundleList();
		}
		return instance;
	}

	public Map<String,ArrayList<Map<String, Object>>> runService(Map<String, Object> input) 
	{	
		String internalProductIdList = (String) input.get(PRODUCT_ID_LIST);		
		Integer configId = (Integer) input.get(CONFIG_ID);	
		Integer bundleTypeId = (Integer) input.get(BUNDLE_TYPE_ID);
		Integer noOfBundles = (Integer) input.get(NO_OF_BUNDLES);
		Map<String,Object> resultSets = execute(configId,internalProductIdList,bundleTypeId,noOfBundles);
		ArrayList<Map<String, Object>> bundleIdList = (ArrayList<Map<String, Object>>)resultSets.get("#result-set-1");
		ArrayList<Map<String, Object>> abandonedBundleIdList = (ArrayList<Map<String, Object>>)resultSets.get("#result-set-2");
		Map<String,ArrayList<Map<String, Object>>> finalBundleIdList = new HashMap<String, ArrayList<Map<String,Object>>>();
		finalBundleIdList.put(BUNDLE_ID_LIST,bundleIdList);
		finalBundleIdList.put(ABANDONED_CART_BUNDLE_ID_LIST,abandonedBundleIdList);
		return finalBundleIdList;
	}
}
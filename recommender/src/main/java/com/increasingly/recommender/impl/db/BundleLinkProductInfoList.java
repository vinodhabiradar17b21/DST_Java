package com.increasingly.recommender.impl.db;

import static com.increasingly.recommender.constants.Constants.*;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.recommender.DB.BaseDB;
import com.increasingly.recommender.interfaces.ServiceInterface;

public class BundleLinkProductInfoList extends StoredProcedure implements ServiceInterface<ArrayList<Map<String, Object>>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Get_BundleLink_Product_Info_List";
	private static BundleLinkProductInfoList instance = null;
	
	private BundleLinkProductInfoList()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);	
		declareParameter(new SqlParameter("BundleIdList", Types.VARCHAR));
		compile();		
	}

	public static BundleLinkProductInfoList getInstance()
	{
		if (instance == null)
		{
			instance = new BundleLinkProductInfoList();
		}
		return instance;
	}

	public ArrayList<Map<String, Object>> runService(Map<String, Object> input) 
	{	
		String bundleIdList = (String) input.get(BUNDLE_ID_LIST);
		ArrayList<Map<String, Object>> bundleLinkProductInfoList = (ArrayList<Map<String, Object>>)execute(bundleIdList).get("#result-set-1");
		return bundleLinkProductInfoList;
	}
	
}
package com.increasingly.recommender.impl.db;

import static com.increasingly.recommender.constants.Constants.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.recommender.DB.BaseDB;
import com.increasingly.recommender.interfaces.ServiceInterface;

public class BundleProductItemList extends StoredProcedure implements ServiceInterface<ArrayList<Map<String, Object>>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Get_Bundle_Product_Item_List";
	private static BundleProductItemList instance = null;
	
	private BundleProductItemList()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("BundleIdList", Types.VARCHAR));
		//declareParameter(new SqlReturnResultSet("#result-set-1", bundleProductItemListRowMapper));
		compile();		
	}

	public static BundleProductItemList getInstance()
	{
		if (instance == null)
		{
			instance = new BundleProductItemList();
		}
		return instance;
	}

	public ArrayList<Map<String, Object>> runService(Map<String, Object> input) 
	{	
		String bundleIdList = (String) input.get(BUNDLE_ID_LIST);
		ArrayList<Map<String, Object>> bundleProductIdList = (ArrayList<Map<String, Object>>)execute(bundleIdList).get("#result-set-1");
		return bundleProductIdList;
		
		/*
		List<Long> bundleItemIdList = new ArrayList<Long>();
		Integer bundleId = (Integer) input.get(BUNDLE_ID);		
		List<Long> bundleItemListArray = (List<Long>)execute(bundleId).get("#result-set-1");
						
		for (Long internalProductId : bundleItemListArray)
		{
			bundleItemIdList.add(internalProductId);			
		}
				
		bundleItemListArray = null;
		
		return bundleItemIdList;*/
		
	
	}
	
	/*
	private static final RowMapper<Long> bundleProductItemListRowMapper;
	static
	{		
		bundleProductItemListRowMapper = new RowMapper<Long>()
		{
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException
			{	
				return rs.getLong("internal_product_id");	
			}
		};
	}*/
	
}
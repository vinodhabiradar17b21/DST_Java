package com.increasingly.recommender.impl.db;

import static com.increasingly.recommender.constants.Constants.*;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.recommender.DB.BaseDB;
import com.increasingly.recommender.interfaces.ServiceInterface;

public class ProductCategoryList extends StoredProcedure implements ServiceInterface<ArrayList<Map<String, Object>>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Get_Product_CategoryList";
	private static ProductCategoryList instance = null;
	
	private ProductCategoryList()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("FeedId", Types.INTEGER));
		declareParameter(new SqlParameter("InternalProductIdList", Types.VARCHAR));
		compile();		
	}

	public static ProductCategoryList getInstance()
	{
		if (instance == null)
		{
			instance = new ProductCategoryList();
		}
		return instance;
	}

	public ArrayList<Map<String, Object>> runService(Map<String, Object> input) 
	{
		Integer feedId = (Integer) input.get(FEED_ID);
		String productIdList = (String) input.get(PRODUCT_ID_LIST);
		
		ArrayList<Map<String, Object>> categoryIdList = (ArrayList<Map<String, Object>>)execute(feedId,productIdList).get("#result-set-1");
		return categoryIdList;
	}
}
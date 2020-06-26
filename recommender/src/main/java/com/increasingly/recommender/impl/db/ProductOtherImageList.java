package com.increasingly.recommender.impl.db;

import static com.increasingly.recommender.constants.Constants.PRODUCT_ID_LIST;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.recommender.DB.BaseDB;
import com.increasingly.recommender.interfaces.ServiceInterface;

public class ProductOtherImageList extends StoredProcedure implements ServiceInterface<ArrayList<Map<String, Object>>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static String SPROC_NAME = "Get_Multiple_Product_Other_Images";	
	private static ProductOtherImageList instance = null;
	
	private ProductOtherImageList()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("ProductIdList", Types.VARCHAR));
		compile();		
	}

	public static ProductOtherImageList getInstance()
	{
		if (instance == null)
		{
			instance = new ProductOtherImageList();
		}
		return instance;
	}

	public ArrayList<Map<String, Object>> runService(Map<String, Object> input)
	{
		String productIds = (String) input.get(PRODUCT_ID_LIST);	
		ArrayList<Map<String, Object>> productImageList = (ArrayList<Map<String, Object>>) execute(productIds).get("#result-set-1");
		return productImageList;
	}
}
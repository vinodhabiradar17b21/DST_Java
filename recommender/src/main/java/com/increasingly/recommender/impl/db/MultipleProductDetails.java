package com.increasingly.recommender.impl.db;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.recommender.DB.BaseDB;
import com.increasingly.recommender.interfaces.ServiceInterface;

import static com.increasingly.recommender.constants.Constants.*;

public class MultipleProductDetails extends StoredProcedure implements ServiceInterface<ArrayList<Map<String, Object>>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static String SPROC_NAME = "Get_Multiple_Product_Details";	
	private static MultipleProductDetails instance = null;
	
	private MultipleProductDetails()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("MultipleProductIdList", Types.VARCHAR));
		compile();		
	}

	public static MultipleProductDetails getInstance()
	{
		if (instance == null)
		{
			instance = new MultipleProductDetails();
		}
		return instance;
	}

	public ArrayList<Map<String, Object>> runService(Map<String, Object> input)
	{
		String multipleProductIds = (String) input.get(PRODUCT_ID_LIST);	
		ArrayList<Map<String, Object>> productDetailsList = (ArrayList<Map<String, Object>>) execute(multipleProductIds).get("#result-set-1");
		return productDetailsList;
	}
}
package com.increasingly.recommender.impl.db;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.recommender.DB.BaseDB;
import com.increasingly.recommender.interfaces.ServiceInterface;
import static com.increasingly.recommender.constants.Constants.*;


public class BundleConfigurationDetails extends StoredProcedure implements ServiceInterface<ArrayList<Map<String,Object>>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Retrieve_Bundle_Configuration_Details";
	private static BundleConfigurationDetails instance = null;
	
	private BundleConfigurationDetails()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("ClientId", Types.INTEGER));
		compile();		
	}

	public static BundleConfigurationDetails getInstance()
	{
		if (instance == null)
		{
			instance = new BundleConfigurationDetails();
		}
		return instance;
	}

	public ArrayList<Map<String,Object>> runService(Map<String, Object> input) 
	{
		Integer clientId = (Integer) input.get(CLIENT_ID);
		ArrayList<Map<String, Object>> results = (ArrayList<Map<String, Object>>)execute(clientId).get("#result-set-1");
		return results;
	}
}
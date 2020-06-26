package com.increasingly.recommender.impl.db;

import static com.increasingly.recommender.constants.Constants.*;

import java.sql.Types;
import java.util.Map;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.recommender.DB.BaseDB;
import com.increasingly.recommender.interfaces.ServiceInterface;

public class ClientApiKeyDetails extends StoredProcedure implements ServiceInterface<Integer>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Get_Client_Id";
	private static ClientApiKeyDetails instance = null;
	
	private ClientApiKeyDetails()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("ApiKey", Types.VARCHAR));	
		declareParameter(new SqlOutParameter("ClientId", Types.INTEGER));
		compile();				
	}

	public static ClientApiKeyDetails getInstance()
	{
		if (instance == null)
		{
			instance = new ClientApiKeyDetails();
		}
		return instance;
	}

	public Integer runService(Map<String, Object> input) 
	{	
		String apiKey = (String) input.get(API_KEY);		
		Map<String,Object> result = execute(apiKey);
		
		if (result.get("ClientId") == null)
		{
			return null;
		}
		else
		{
			return (Integer) result.get("ClientId");			
		}
	}
}
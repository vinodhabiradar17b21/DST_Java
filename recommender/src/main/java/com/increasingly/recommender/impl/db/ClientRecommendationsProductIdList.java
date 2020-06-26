package com.increasingly.recommender.impl.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import static com.increasingly.recommender.constants.Constants.*;

import com.increasingly.recommender.DB.BaseDB;
import com.increasingly.recommender.interfaces.ServiceInterface;

public class ClientRecommendationsProductIdList extends StoredProcedure implements ServiceInterface<ArrayList<Long>>
{

	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Get_Client_Recommendations_ProductId_List";
	private static ClientRecommendationsProductIdList instance = null;
	
	private ClientRecommendationsProductIdList()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);	
		declareParameter(new SqlParameter("ConfigId", Types.INTEGER));	
		declareParameter(new SqlReturnResultSet("#result-set-1", clientRecommendationProductIdListRowMapper));
		compile();		
	}

	public static ClientRecommendationsProductIdList getInstance()
	{
		if (instance == null)
		{
			instance = new ClientRecommendationsProductIdList();
		}
		return instance;
	}

	public ArrayList<Long> runService(Map<String, Object> input) 
	{		
		ArrayList<Long> clientRecommendationsProductIdList = new ArrayList<Long>();
		Integer configId = (Integer) input.get(CONFIG_ID);
		ArrayList<Long> tempClientRecommendationsProductIdList = (ArrayList<Long>) execute(configId).get("#result-set-1");
		
		for (Long internalProductId : tempClientRecommendationsProductIdList)
		{
			clientRecommendationsProductIdList.add(internalProductId);			
		}
		
		return clientRecommendationsProductIdList;
	}
	
	private static final RowMapper<Long> clientRecommendationProductIdListRowMapper;
	static
	{		
		clientRecommendationProductIdListRowMapper = new RowMapper<Long>()
		{
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException
			{	
				return rs.getLong("InternalProductId");	
			}
		};
	}

}

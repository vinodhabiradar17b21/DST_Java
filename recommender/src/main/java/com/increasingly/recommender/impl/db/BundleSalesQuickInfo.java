package com.increasingly.recommender.impl.db;

import static com.increasingly.recommender.constants.Constants.CONFIG_ID;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.recommender.DB.BaseDB;
import com.increasingly.recommender.interfaces.ServiceInterface;

public class BundleSalesQuickInfo extends StoredProcedure implements ServiceInterface<ArrayList<Integer>>
{
	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Get_Bundle_Sales_Quick_Info_Details";
	private static BundleSalesQuickInfo instance = null;
	
	private BundleSalesQuickInfo()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("ConfigId", Types.INTEGER));	
		declareParameter(new SqlReturnResultSet("#result-set-1", bundleSalesListRowMapper));
		compile();		
	}

	public static BundleSalesQuickInfo getInstance()
	{
		if (instance == null)
		{
			instance = new BundleSalesQuickInfo();
		}
		return instance;
	}

	public ArrayList<Integer> runService(Map<String, Object> input) 
	{		
		Integer configId = (Integer) input.get(CONFIG_ID);			
					
		ArrayList<Integer> bundleSalesList = (ArrayList<Integer>) execute(configId).get("#result-set-1");
		return bundleSalesList;
	}
	
	private static final RowMapper<Integer> bundleSalesListRowMapper;
	static
	{		
		bundleSalesListRowMapper = new RowMapper<Integer>()
		{
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
			{	
				return rs.getInt("BundleId");	
			}
		};
	}
}
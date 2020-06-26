package com.increasingly.recommender.impl.db;

import static com.increasingly.recommender.constants.Constants.*;

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

public class AllowedGeoCountryIdList extends StoredProcedure implements ServiceInterface<Set<Integer>>
{
	private static AllowedGeoCountryIdList instance = null;
	private final static String dataSourceLookupName = "mysqlserver";
	private static String SPROC_NAME = "Get_Bundle_Config_Allowed_Geo_Country_Id_List";
	
	public static AllowedGeoCountryIdList getInstance()
	{
		if (instance == null)
		{
			instance = new AllowedGeoCountryIdList();
		}
		return instance;
	}

	private AllowedGeoCountryIdList()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("ConfigId", Types.INTEGER));
		declareParameter(new SqlReturnResultSet("#result-set-1", allowedGeoCountryIdListRowMapper));

		compile();
		
	}

	public Set<Integer> runService(Map<String, Object> input) throws Exception
	{
		Set<Integer> allowedGeoCountryIdList = new HashSet<Integer>();
		Integer configId = (Integer) input.get(CONFIG_ID);
				
		ArrayList<Integer> geoCountryIdListArray = (ArrayList<Integer>) execute(configId).get("#result-set-1");
				
		for (Integer countryId : geoCountryIdListArray)
		{
			allowedGeoCountryIdList.add(countryId);			
		}
				
		geoCountryIdListArray = null;
		
		return allowedGeoCountryIdList;
	}
	
	private static final RowMapper<Integer> allowedGeoCountryIdListRowMapper;
	static
	{		
		allowedGeoCountryIdListRowMapper = new RowMapper<Integer>()
		{
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
			{	
				return rs.getInt("GeoCountryId");	
			}
		};
	}
}
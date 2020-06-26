package com.increasingly.recommender.impl.db;

import static com.increasingly.recommender.constants.Constants.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import com.increasingly.recommender.interfaces.ServiceInterface;
import com.increasingly.recommender.utils.FormatLoggerMessage;



public class TrackBundleSearchRequest implements ServiceInterface<Boolean>
{
	private static TrackBundleSearchRequest instance = null;
	private final static String dataSourceLookupName = "mysqlserver_main_db";
	private static final Logger logger = LoggerFactory.getLogger(TrackBundleSearchRequest.class.getClass());
	
	public static TrackBundleSearchRequest getInstance()
	{
		if (instance == null)
		{
			instance = new TrackBundleSearchRequest();
		}
		return instance;
	}

	public Boolean runService(final Map<String, Object> input) throws Exception
	{
		JdbcTemplate jdbcTemplate = com.increasingly.recommender.DB.BaseDB.getJdbcTemplate(dataSourceLookupName);

		String queryTmpl = "INSERT INTO bundle_request_tracking"
				+ "(client_id,product_ids,category_ids,page_type_id,bundle_id_list,client_visitor_id,user_agent,search_date,country_id)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";	

		try
		{
			return jdbcTemplate.execute(queryTmpl,new PreparedStatementCallback<Boolean>(){

				public Boolean doInPreparedStatement(PreparedStatement ps)
				throws SQLException, DataAccessException {
			
					ps.setInt(1, (Integer)input.get(CLIENT_ID));
					ps.setNString(2, (String)input.get(PRODUCT_IDS));
					
					ps.setNString(3, (String)input.get(CATEGORY_ID));
					ps.setInt(4, (Integer)input.get(PAGE_TYPE_ID));
					ps.setString(5, (String)input.get(BUNDLE_ID_LIST));
					ps.setString(6, (String)input.get(CLIENT_VISITOR_ID));
					ps.setNString(7, (String)input.get(USER_AGENT));
					ps.setString(8, (String)input.get(SEARCH_DATE));
					
					if(input.get(COUNTRY_ID) != null)
					{
						ps.setInt(9, (Integer)input.get(COUNTRY_ID));
					}
					else
					{
						ps.setNull(9, Types.INTEGER);
					}
					
					ps.execute(); 
					return true;
				}
			});
		}
		catch (Exception ex)
		{
			String errorMessage = FormatLoggerMessage.formatError(LOG_ERROR,"TrackBundleSearchRequest.java","failed to insert bundle request tracking data", "");
			logger.error(errorMessage, ex);
			return false;
		}
	}
}
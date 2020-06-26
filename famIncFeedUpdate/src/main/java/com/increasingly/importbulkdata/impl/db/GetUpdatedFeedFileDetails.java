package com.increasingly.importbulkdata.impl.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlReturnResultSet;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.db.BaseDB;
import com.increasingly.importbulkdata.interfaces.ServiceInterface;

import static com.increasingly.importbulkdata.util.Constants.*;

public class GetUpdatedFeedFileDetails  extends StoredProcedure implements ServiceInterface<List<String>>
{

	private final static String dataSourceLookupName = "mysqlserver";
	private static String SPROC_NAME = "Get_Client_Product_Feed_Update_File_Details";	
	private static GetUpdatedFeedFileDetails instance = null;
	
	private GetUpdatedFeedFileDetails()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("FeedId", Types.INTEGER));
		declareParameter(new SqlReturnResultSet("#result-set-1", fileNameListRowMapper));
		compile();		
	}

	public static GetUpdatedFeedFileDetails getInstance()
	{
		if (instance == null)
		{
			instance = new GetUpdatedFeedFileDetails();
		}
		return instance;
	}

	public List<String> runService(Map<String, Object> input)
	{
		int feedId = (Integer) input.get(FEED_ID);
		
		List<String> results = (ArrayList<String>) execute(feedId).get("#result-set-1");
		return results;
	}

	private static final RowMapper<String> fileNameListRowMapper;
	static
	{		
		fileNameListRowMapper = new RowMapper<String>()
		{
			public String mapRow(ResultSet rs, int rowNum) throws SQLException
			{	
				return rs.getString("FileName");	
			}
		};
	}
}

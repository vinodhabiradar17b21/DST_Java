package com.increasingly.importbulkdata.impl.db;

import static com.increasingly.importbulkdata.util.Constants.*;

import java.sql.Types;
import java.util.Map;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.db.BaseDB;
import com.increasingly.importbulkdata.interfaces.ServiceInterface;

public class InsertFeedUpdateFileDetailsImpl extends StoredProcedure implements ServiceInterface<Integer>
{
	private static InsertFeedUpdateFileDetailsImpl instance = null;	
	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Insert_Into_Client_Product_Feed_Update_File_Details";

	private InsertFeedUpdateFileDetailsImpl()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("FeedId", Types.INTEGER));		
		declareParameter(new SqlParameter("FileName", Types.VARCHAR));
		declareParameter(new SqlOutParameter("result", Types.INTEGER));
		compile();		
	}

	public static InsertFeedUpdateFileDetailsImpl getInstance()
	{
		if (instance == null)
		{
			instance = new InsertFeedUpdateFileDetailsImpl();
		}
		return instance;
	}

	public Integer runService(Map<String, Object> input) throws Exception
	{
		Integer feedId = (Integer) input.get(FEED_ID);
		String fileName = (String) input.get(UPDATED_FEED_FILE_NAME);
		Map<String, Object> results = execute(feedId,fileName);

		if (results.get("result") == null)
		{
			return 0;
		}
		else
		{
			Integer res = (Integer) results.get("result");
			return res;			
		}
	}

}

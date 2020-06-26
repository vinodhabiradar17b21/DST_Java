package com.increasingly.importbulkdata.impl.db;

import static com.increasingly.importbulkdata.util.Constants.*;

import java.sql.Types;
import java.util.Map;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.db.BaseDB;
import com.increasingly.importbulkdata.interfaces.ServiceInterface;

public class InsertProductOptionDetailsImpl extends StoredProcedure implements ServiceInterface<Integer>
{
	private static InsertProductOptionDetailsImpl instance = null;	
	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Bulk_Insert_OR_Update_Product_Options_Data";

	private InsertProductOptionDetailsImpl()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("feedId", Types.INTEGER));		
		declareParameter(new SqlOutParameter("result", Types.INTEGER));
		compile();		
	}

	public static InsertProductOptionDetailsImpl getInstance()
	{
		if (instance == null)
		{
			instance = new InsertProductOptionDetailsImpl();
		}
		return instance;
	}

	public Integer runService(Map<String, Object> input) throws Exception
	{
		Integer feedId = (Integer) input.get(FEED_ID);
	
		Map<String, Object> results = execute(feedId);

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
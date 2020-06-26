package com.increasingly.importbulkdata.impl.db;

import static com.increasingly.importbulkdata.util.Constants.*;

import java.sql.Types;
import java.util.Map;

import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.increasingly.db.BaseDB;
import com.increasingly.importbulkdata.interfaces.ServiceInterface;

public class InsertProductDataImpl extends StoredProcedure implements ServiceInterface<Integer>
{
	private static InsertProductDataImpl instance = null;	
	private final static String dataSourceLookupName = "mysqlserver";
	private static final String SPROC_NAME = "Insert_OR_Update_Bulk_Product_Data";

	private InsertProductDataImpl()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("feedId", Types.INTEGER));		
		declareParameter(new SqlOutParameter("result", Types.INTEGER));
		declareParameter(new SqlParameter("isFirstSetRecord", Types.BIT));
		declareParameter(new SqlParameter("isLastSetRecord", Types.BIT));
		declareParameter(new SqlParameter("isDifferentialFeed", Types.BIT));

		compile();		
	}

	public static InsertProductDataImpl getInstance()
	{
		if (instance == null)
		{
			instance = new InsertProductDataImpl();
		}
		return instance;
	}

	public Integer runService(Map<String, Object> input) throws Exception
	{
		Integer feedId = (Integer) input.get(FEED_ID);
		Boolean isFirstSetRecord = (Boolean) input.get(IS_FIRST_SET_RECORD);
		Boolean isLastSetRecord = (Boolean)input.get(IS_LAST_SET_RECORD);
		Map<String, Object> results = execute(feedId,isFirstSetRecord,isLastSetRecord,true);

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
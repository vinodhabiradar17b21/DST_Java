package com.increasingly.recommender.impl.db;

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
import static com.increasingly.recommender.constants.Constants.*;


public class CategoryExclusionList extends StoredProcedure implements ServiceInterface<Set<Integer>>
{
	private static CategoryExclusionList instance = null;
	private final static String dataSourceLookupName = "mysqlserver";
	private static String SPROC_NAME = "Get_Category_Exclusion_List";
	
	public static CategoryExclusionList getInstance()
	{
		if (instance == null)
		{
			instance = new CategoryExclusionList();
		}
		return instance;
	}

	private CategoryExclusionList()
	{
		super(BaseDB.getJdbcTemplate(dataSourceLookupName).getDataSource(), SPROC_NAME);
		declareParameter(new SqlParameter("ConfigId", Types.INTEGER));
		declareParameter(new SqlReturnResultSet("#result-set-1", categoryExclusionListRowMapper));

		compile();
		
	}

	public Set<Integer> runService(Map<String, Object> input) throws Exception
	{
		Set<Integer> categoryExclusionList = new HashSet<Integer>();
		Integer configId = (Integer) input.get(CONFIG_ID);
				
		ArrayList<Integer> categoryListArray = (ArrayList<Integer>) execute(configId).get("#result-set-1");
				
		for (Integer internalCategoryId : categoryListArray)
		{
			categoryExclusionList.add(internalCategoryId);			
		}
				
		categoryListArray = null;
		
		return categoryExclusionList;
	}
	
	private static final RowMapper<Integer> categoryExclusionListRowMapper;
	static
	{		
		categoryExclusionListRowMapper = new RowMapper<Integer>()
		{
			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException
			{	
				return rs.getInt("InternalCategoryId");	
			}
		};
	}
}
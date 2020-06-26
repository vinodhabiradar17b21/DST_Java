package com.increasingly.recommender.DB;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Base Class that stored the list of Datasources as defined in the Spring Application Context
 * See - http://docs.spring.io/autorepo/docs/spring/4.0.6.RELEASE/spring-framework-reference/html/jdbc.html
 */
public class BaseDB
{
	/**
	 * Holds the JDBCTemplates used for each Datasource.  Accessed by lookupName defined in Spring Application Context
	 */
	private static Map<String, JdbcTemplate> jdbcTemplateMap = new HashMap<String, JdbcTemplate>();

	
	/**
	 * Sets the Datasource map
	 * @param dataSourceList - List of Beans from App Context
	 */
    public void setDataSourceList(List<DriverManagerDataSource> dataSourceList) 
    {
    	for (DriverManagerDataSource dataSource : dataSourceList)
    	{
    		jdbcTemplateMap.put(dataSource.getDataSourceLookupName(), new JdbcTemplate((DataSource) dataSource));
    	}
    }
    
    /**
     * Gets the Datasource given a particular Datasource lookupName
     * @param dataSourceLookupName - as defined in Application Context Datasource bean
     * @return Spring JDBCTemplate for executing queries against the datasource
     */
    public static JdbcTemplate getJdbcTemplate(String dataSourceLookupName)
    {
    	return jdbcTemplateMap.get(dataSourceLookupName);
    }

}
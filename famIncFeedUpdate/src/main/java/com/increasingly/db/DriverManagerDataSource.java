package com.increasingly.db;

import com.increasingly.importbulkdata.App;



public class DriverManagerDataSource extends org.apache.tomcat.jdbc.pool.DataSource
{
	private String dataSourceLookupName;
	
	/**
	 * Class to extend the Spring DriverManagerDataSource to include the dataSourceLookupName
	 * @param url - url of the datasource
	 * @param username - username of datasource
	 * @param password - password of datasource
	 * @param driverClassName - driver used for datasource
	 * @throws Exception 
	 */
	public DriverManagerDataSource(String url, String username, String password, String driverClassName) throws Exception
	{			
		setUrl(url);
		setUsername(username);
		setPassword(App.aes.decrypt(password));		
		setDriverClassName(driverClassName);	
		setTestOnBorrow(true);		
	}
	
	public String getDataSourceLookupName()
	{
		return dataSourceLookupName;
	}

	public void setDataSourceLookupName(String dataSourceLookupName)
	{
		this.dataSourceLookupName = dataSourceLookupName;
	}
	
}
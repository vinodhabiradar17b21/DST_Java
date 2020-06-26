package com.increasingly.importbulkdata.interfaces;

import java.util.Map;

/**
 * Input is a Map of Parameter name to Object value.  Parameter names are defined as Constants.  Each Service must interpret the type of the parameter as well as the return object type
 */

public interface ServiceInterface<T> 
{
	public abstract T runService(Map<String, Object> input) throws Exception;
}

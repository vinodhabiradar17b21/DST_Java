package com.increasingly.recommender.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BundleAvailabilityCheckJsonResponse
{	
	private List<String> customerProductIdList = null;
	
			
	@JsonProperty("ProductListWithBundles")
	public List<String> getProductListWithBundles()
	{
		return this.customerProductIdList;
	}
	
	public void setProductListWithBundles(List<String> customerProductIdList)
	{
		this.customerProductIdList = customerProductIdList;
	}	
	
}
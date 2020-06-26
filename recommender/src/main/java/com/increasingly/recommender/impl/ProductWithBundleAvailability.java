package com.increasingly.recommender.impl;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductWithBundleAvailability
{
	private String customerProductId = "";
	private Integer numberOfBundles = 0;
	private String field1 = "";
	
	@JsonProperty("NumberOfBundles")
	public Integer getNumberOfBundles() {
		return numberOfBundles;
	}
	public void setNumberOfBundles(Integer numberOfBundles) {
		this.numberOfBundles = numberOfBundles;
	}
	
	@JsonProperty("ProductId")
	public String getCustomerProductId() {
		return customerProductId;
	}
	public void setCustomerProductId(String customerProductId) {
		this.customerProductId = customerProductId;
	}
	
	@JsonProperty("Field1")
	public String getField1() {
		return field1;
	}
	public void setField1(String field1) {
		this.field1 = field1;
	}
}
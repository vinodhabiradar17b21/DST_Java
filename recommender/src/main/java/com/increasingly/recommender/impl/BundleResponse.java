package com.increasingly.recommender.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.Charsets;

public class BundleResponse
{
	private String content = "";
	private final Charset encoding = Charsets.UTF_8;	
	private ProcessTimes processTimes = new ProcessTimes();
	private Boolean canContinue = true;
	private BundleJsonResponseContent bundleJsonResponseContent = null;
	private List<Integer> finalBundleList = new  ArrayList<Integer>();
	private BundleAvailabilityCheckJsonResponse bundleAvailabilityCheckJsonResponse = null;
	private ProductsListWithBundleAvailablityDetails productsListWithBundleAvailablityDetails = null;

	public BundleResponse()
	{
	}
		
	/**
	 * return content
	 * 
	 * @return String
	 */
	public String getContent()
	{
		return this.content;
	}

	/**
	 * Set content
	 * 
	 * @param content
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

	/**
	 * Return encoding type
	 * 
	 * @return Charset
	 */
	public Charset getEncoding()
	{
		return this.encoding;
	}
	
	public ProcessTimes getResponseProcessTimes()
	{
		return this.processTimes;
	}
	
	public Boolean getCanContinue()
	{
		return this.canContinue;
	}

	public void setCanContinue(Boolean value)
	{
		this.canContinue = value;
	}
	
	public BundleJsonResponseContent getBundleJsonResponseContent()
	{
		return this.bundleJsonResponseContent;
	}
	
	public void setBundleJsonResponseContent(BundleJsonResponseContent bundleJsonResponseContent)
	{
		this.bundleJsonResponseContent = bundleJsonResponseContent;
	}
	
	public BundleAvailabilityCheckJsonResponse getBundleAvailabilityJsonResponse()
	{
		return this.bundleAvailabilityCheckJsonResponse;
	}
	
	public void setBundleAvailabilityJsonResponse(BundleAvailabilityCheckJsonResponse bundleAvailabilityCheckJsonResponse)
	{
		this.bundleAvailabilityCheckJsonResponse = bundleAvailabilityCheckJsonResponse;
	}
	
	public List<Integer> getFinalBundleList() {
		return finalBundleList;
	}

	public void setFinalBundleList(Integer bundleId) {
		this.finalBundleList.add(bundleId);
	}
	
	public ProductsListWithBundleAvailablityDetails getProductsListWithBundleAvailablityJsonResponse()
	{
		return this.productsListWithBundleAvailablityDetails;
	}
	
	public void setProductsListWithBundleAvailablityJsonResponse(ProductsListWithBundleAvailablityDetails productsListWithBundleAvailablityDetails)
	{
		this.productsListWithBundleAvailablityDetails = productsListWithBundleAvailablityDetails;
	}

}
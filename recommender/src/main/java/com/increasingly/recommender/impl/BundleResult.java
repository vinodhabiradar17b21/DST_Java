package com.increasingly.recommender.impl;

import java.util.ArrayList;
import java.util.List;


public class BundleResult
{
	private BundleConfiguration bundleConfiguration = new BundleConfiguration();
	private List<Long> internalProductIdList = null;
	private List<Integer> internalCategoryList = null;
	private Integer feedId = 0;
	private List<Integer> abandonedCartBundleIdList = null;

	/**
	 * 
	 */
	public BundleResult()
	{
	}
	
	public Integer getFeedId()
	{
		return this.feedId;
	}

	public void setFeedId(Integer feedId)
	{
		this.feedId = feedId;
	}
	
	public List<Long> getInternalProductIdList()
	{
		return this.internalProductIdList;
	}
	
	public void setInternalProductIdList(List<Long> internalProductIdList)
	{
		this.internalProductIdList = internalProductIdList;
	}
	
	
	public List<Integer> getInternalCategoryList()
	{
		return this.internalCategoryList;
	}
	
	public void setInternalCategoryList(List<Integer> internalCategoryList)
	{
		this.internalCategoryList = internalCategoryList;
	}
	
	public void setInternalCategory(Integer internalCategoryId)
	{
		if(this.internalCategoryList == null)
		{
			this.internalCategoryList = new ArrayList<Integer>();
		}
		this.internalCategoryList.add(internalCategoryId);
	}
	
	/**
	 * Get Bundle configuration details
	 * 
	 * @return
	 */
	public BundleConfiguration getBundleConfiguration()
	{
		return this.bundleConfiguration;
	}

	/**
	 * @param value
	 */
	public void setBundleConfiguration(BundleConfiguration value)
	{
		this.bundleConfiguration = value;
	}
       
	public List<Integer> getAbandonedCartBundleIdList() {
		return abandonedCartBundleIdList;
	}

	public void setAbandonedCartBundleIdList(List<Integer> abandonedCartBundleIdList) {
		this.abandonedCartBundleIdList = abandonedCartBundleIdList;
	}
	

}
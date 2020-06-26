package com.increasingly.recommender.impl;

public class BundleIdWithPurchaseCount
{
	private Integer bundleId;
	private Integer purchaseCount;
	private double marginPercent;
	
	public Integer getBundleId()
	{
		return this.bundleId;
	}
	
	public void setBundleId(Integer bundleId)
	{
		this.bundleId = bundleId;
	}
	
	public Integer getPurchaseCount()
	{
		return this.purchaseCount;
	}
	
	public void setPurchaseCount(Integer purchaseCount)
	{
		this.purchaseCount = purchaseCount;
	}
	
	public double getMarginPercent() {
		return marginPercent;
	}

	public void setMarginPercent(double marginPercent) {
		this.marginPercent = marginPercent;
	}
}
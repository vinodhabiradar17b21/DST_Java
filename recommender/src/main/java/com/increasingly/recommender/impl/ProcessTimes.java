package com.increasingly.recommender.impl;

import org.joda.time.DateTime;
import org.joda.time.Interval;


public class ProcessTimes
{
	
	private Integer requestCookiesProcessTime = 0;
	private Integer totalProcessTime = 0;
	private Integer timeTakenToGetInternalProductIds = 0;
	private Integer timeTakenToGetCategoryProductIdListOfRequestedProducts = 0;
	private Integer timeTakenToGetInternalCategoryIds = 0;
	private Integer timeTakenToGetProductPageBundleIdList = 0;
	private Integer timeTakenToGetCategoryPageBundleIdList = 0;
	private Integer timeTakenToGetBundleProductIdList = 0;
	private Integer timeTakenToGetBundleDetails = 0;
	private Integer timeTakenToGetProductDetails = 0;
	private Integer timeTakenToGetOtherImages = 0;
	private Integer timeTakenToGetProductAttributeDetails = 0;
	private Integer timeTakenToInsertBundleRequestDetails = 0;
	
	/**
	 * Default Constructor
	 */
	public ProcessTimes()
	{
	}

	/*
	 * Get Time Taken from Start DateTime to Current DateTime
	 * 
	 * @param startDateTime
	 */
	public Integer getTimeTaken(DateTime startDateTime)
	{
		Integer timeTaken = 0;
		DateTime now = DateTime.now();
		Interval elapsed = new Interval(startDateTime, now);

		timeTaken = elapsed.toPeriod().getMillis();
		if (elapsed.toPeriod().getSeconds() > 0)
		{
			timeTaken += elapsed.toPeriod().getSeconds() * 1000;
		}
		if (elapsed.toPeriod().getMinutes() > 0)
		{
			timeTaken += elapsed.toPeriod().getMinutes() * 60 * 1000;
		}

		return timeTaken;
	}

	/**
	 * Get the total time to process the request
	 */
	public Integer getTotalProcessTime()
	{
		return this.totalProcessTime;
	}

	/**
	 * Set the total time to process the request
	 */
	public void setTotalProcessTime(Integer value)
	{
		this.totalProcessTime = value;
	}
	
	/**
	 * Get the time to Process Request Cookie Info
	 */
	public Integer getRequestCookiesProcessTime()
	{
		return this.requestCookiesProcessTime;
	}

	/**
	 * Set the time to Process Request Cookie Info
	 */
	public void setRequestCookiesProcessTime(Integer value)
	{
		this.requestCookiesProcessTime = value;
	}

	public Integer getTimeTakenToGetInternalProductIds() {
		return timeTakenToGetInternalProductIds;
	}

	public void setTimeTakenToGetInternalProductIds(
			Integer timeTakenToGetInternalProductIds) {
		this.timeTakenToGetInternalProductIds = timeTakenToGetInternalProductIds;
	}

	public Integer getTimeTakenToGetCategoryProductIdListOfRequestedProducts() {
		return timeTakenToGetCategoryProductIdListOfRequestedProducts;
	}

	public void setTimeTakenToGetCategoryProductIdListOfRequestedProducts(
			Integer timeTakenToGetCategoryProductIdListOfRequestedProducts) {
		this.timeTakenToGetCategoryProductIdListOfRequestedProducts = timeTakenToGetCategoryProductIdListOfRequestedProducts;
	}

	public Integer getTimeTakenToGetInternalCategoryIds() {
		return timeTakenToGetInternalCategoryIds;
	}

	public void setTimeTakenToGetInternalCategoryIds(
			Integer timeTakenToGetInternalCategoryIds) {
		this.timeTakenToGetInternalCategoryIds = timeTakenToGetInternalCategoryIds;
	}

	public Integer getTimeTakenToGetProductPageBundleIdList() {
		return timeTakenToGetProductPageBundleIdList;
	}

	public void setTimeTakenToGetProductPageBundleIdList(
			Integer timeTakenToGetProductPageBundleIdList) {
		this.timeTakenToGetProductPageBundleIdList = timeTakenToGetProductPageBundleIdList;
	}

	public Integer getTimeTakenToGetCategoryPageBundleIdList() {
		return timeTakenToGetCategoryPageBundleIdList;
	}

	public void setTimeTakenToGetCategoryPageBundleIdList(
			Integer timeTakenToGetCategoryPageBundleIdList) {
		this.timeTakenToGetCategoryPageBundleIdList = timeTakenToGetCategoryPageBundleIdList;
	}

	public Integer getTimeTakenToGetBundleProductIdList() {
		return timeTakenToGetBundleProductIdList;
	}

	public void setTimeTakenToGetBundleProductIdList(
			Integer timeTakenToGetBundleProductIdList) {
		this.timeTakenToGetBundleProductIdList = timeTakenToGetBundleProductIdList;
	}
	
	public Integer getTimeTakenToGetBundleDetails() {
		return timeTakenToGetBundleDetails;
	}

	public void setTimeTakenToGetBundleDetails(Integer timeTakenToGetBundleDetails) {
		this.timeTakenToGetBundleDetails = timeTakenToGetBundleDetails;
	}

	public Integer getTimeTakenToGetProductDetails() {
		return timeTakenToGetProductDetails;
	}

	public void setTimeTakenToGetProductDetails(Integer timeTakenToGetProductDetails) {
		this.timeTakenToGetProductDetails = timeTakenToGetProductDetails;
	}

	public Integer getTimeTakenToGetOtherImages() {
		return timeTakenToGetOtherImages;
	}

	public void setTimeTakenToGetOtherImages(Integer timeTakenToGetOtherImages) {
		this.timeTakenToGetOtherImages = timeTakenToGetOtherImages;
	}

	public Integer getTimeTakenToGetProductAttributeDetails() {
		return timeTakenToGetProductAttributeDetails;
	}

	public void setTimeTakenToGetProductAttributeDetails(
			Integer timeTakenToGetProductAttributeDetails) {
		this.timeTakenToGetProductAttributeDetails = timeTakenToGetProductAttributeDetails;
	}
	
	public Integer getTimeTakenToInsertBundleRequestDetails() {
		return timeTakenToInsertBundleRequestDetails;
	}

	public void setTimeTakenToInsertBundleRequestDetails(
			Integer timeTakenToInsertBundleRequestDetails) {
		this.timeTakenToInsertBundleRequestDetails = timeTakenToInsertBundleRequestDetails;
	}
}
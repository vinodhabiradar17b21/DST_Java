package com.increasingly.recommender.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BundleJsonResponseContent
{
	private Integer bundleCount = 0;
	private List<BundleDetailsResponseContent> bundles = null;
	private Boolean isFreeShippingActive = false;
	private Double freeShippingSubTotal = 0.0;
	private String freeShippingTitle = "";
	private boolean hasClientRecommendations = false;
	
	@JsonProperty("BundleCount")
	public Integer getBundleCount()
	{
		return this.bundleCount;
	}
	
	public void setBundleCount(Integer bundleCount)
	{
		this.bundleCount = bundleCount;
	}
	
	@JsonProperty("Bundles")
	public List<BundleDetailsResponseContent> getBundles()
	{
		return this.bundles;
	}
	
	public void setBundles(List<BundleDetailsResponseContent> bundles)
	{
		this.bundles = bundles;
	}
	
	@JsonProperty("IsFreeShippingActive")
	public Boolean getIsFreeShippingActive() {
		return isFreeShippingActive;
	}

	public void setIsFreeShippingActive(Boolean isFreeShippingActive) {
		this.isFreeShippingActive = isFreeShippingActive;
	}

	@JsonProperty("FreeShippingSubTotal")
	public Double getFreeShippingSubTotal() {
		return freeShippingSubTotal;
	}

	public void setFreeShippingSubTotal(Double freeShippingSubTotal) {
		this.freeShippingSubTotal = freeShippingSubTotal;
	}

	@JsonProperty("FreeShippingTitle")
	public String getFreeShippingTitle() {
		return freeShippingTitle;
	}

	public void setFreeShippingTitle(String freeShippingTitle) {
		this.freeShippingTitle = freeShippingTitle;
	}
	
	@JsonProperty("HasClientRecommendations")
	public boolean getHasClientRecommendations() {
		return hasClientRecommendations;
	}

	public void setHasClientBundleRecommendations(
			boolean hasClientRecommendations) {
		this.hasClientRecommendations = hasClientRecommendations;
	}

}
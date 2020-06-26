package com.increasingly.recommender.impl;

public class ProductAttributeValues
{		
	private String childProductId = "";
	private String childProductSku = "";
	private String childProductName = "";
	private String childProductImageUrl = "";
	private String childProductUrl = "";	
	private String childProductPrice = "";
	private String childProductSpecialPrice = "";
	private String childProductDescription = "";
	private String optionId = "";
	private String optionText = "";
	private String optionImageUrl = "";
	private String pricingValue = "";
	private String specialPricingValue = "";
	private Integer pricingIsPercent;
	private String colorCode="";
	
	
	public String getChildProductId() {
		return childProductId;
	}
	public void setChildProductId(String childProductId) {
		this.childProductId = childProductId;
	}
	public String getChildProductSku() {
		return childProductSku;
	}
	public void setChildProductSku(String childProductSku) {
		this.childProductSku = childProductSku;
	}
	public String getChildProductName() {
		return childProductName;
	}
	public void setChildProductName(String childProductName) {
		this.childProductName = childProductName;
	}
	public String getChildProductImageUrl() {
		return childProductImageUrl;
	}
	public void setChildProductImageUrl(String childProductImageUrl) {
		this.childProductImageUrl = childProductImageUrl;
	}
	
	public String getChildProductUrl() {
		return childProductUrl;
	}
	public void setChildProductUrl(String childProductUrl) {
		this.childProductUrl = childProductUrl;
	}
	
	public String getChildProductPrice() {
		return childProductPrice;
	}
	public void setChildProductPrice(String childProductPrice) {
		this.childProductPrice = childProductPrice;
	}
	
	public String getChildProductSpecialPrice() {
		return childProductSpecialPrice;
	}
	public void setChildProductSpecialPrice(String childProductSpecialPrice) {
		this.childProductSpecialPrice = childProductSpecialPrice;
	}
	
	public String getChildProductDescription() {
		return childProductDescription;
	}
	public void setChildProductDescription(String childProductDescription) {
		this.childProductDescription = childProductDescription;
	}
	
	public String getOptionId() {
		return optionId;
	}
	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}
	public String getOptionText() {
		return optionText;
	}
	public void setOptionText(String optionText) {
		this.optionText = optionText;
	}
	public String getOptionImageUrl() {
		return optionImageUrl;
	}
	public void setOptionImageUrl(String optionImageUrl) {
		this.optionImageUrl = optionImageUrl;
	}
	public String getPricingValue() {
		return pricingValue;
	}
	public void setPricingValue(String pricingValue) {
		this.pricingValue = pricingValue;
	}
	
	public String getSpecialPricingValue() {
		return specialPricingValue;
	}
	public void setSpecialPricingValue(String specialPricingValue) {
		this.specialPricingValue = specialPricingValue;
	}
	
	public Integer getPricingIsPercent() {
		return pricingIsPercent;
	}
	public void setPricingIsPercent(Integer pricingIsPercent) {
		this.pricingIsPercent = pricingIsPercent;
	}
	public String getColorCode() {
		return colorCode;
	}
	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}
}
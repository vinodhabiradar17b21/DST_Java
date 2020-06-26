package com.increasingly.importbulkdata.impl;

public class ProductOption
{	
	private String parentProductId;
	private String childProductId;
	private String childProductSku;
	private String storeId;
	private String attributeCode = "";
	private String attributeId = "";
	private String attributeLabel = "";
	private String fieldType = "";
	private String optionId = "";
	private String optionText = "";
	private String optionImageUrl = "";
	private Integer isPercent;
	private String pricingValue;
	private String colorCode;	
	private Integer quantity;
	private String price;
	private String specialPrice;
	
	
	public String getParentProductId() {
		return parentProductId;
	}
	public void setParentProductId(String parentProductId) {
		this.parentProductId = parentProductId;
	}
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
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getAttributeCode() {
		return attributeCode;
	}
	public void setAttributeCode(String attributeCode) {
		this.attributeCode = attributeCode;
	}
	public String getAttributeId() {
		return attributeId;
	}
	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}
	public String getAttributeLabel() {
		return attributeLabel;
	}
	public void setAttributeLabel(String attributeLabel) {
		this.attributeLabel = attributeLabel;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
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
	public Integer getIsPercent() {
		return isPercent;
	}
	public void setIsPercent(Integer isPercent) {
		this.isPercent = isPercent;
	}
	public String getPricingValue() {
		return pricingValue;
	}
	public void setPricingValue(String pricingValue) {
		this.pricingValue = pricingValue;
	}
	public String getColorCode() {
		return colorCode;
	}
	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getSpecialPrice() {
		return specialPrice;
	}
	public void setSpecialPrice(String specialPrice) {
		this.specialPrice = specialPrice;
	}
	
}
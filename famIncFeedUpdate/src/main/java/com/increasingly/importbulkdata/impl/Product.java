package com.increasingly.importbulkdata.impl;

public class Product
{		
	private String productId;
	private String productName = "";
	private String productSku = "";
	private String productPrice;
	private String specialPrice;
	private String imageUrl = "";
	private String productUrl = "";
	private String description = "";
	private String shortDescription = ""; 
	private Integer productStatus;
	private String productType = "";
	private String manufacturer = "";
	private Integer qunatity = 0;
	private String createdDate;
	private String updatedDate;		
	private Boolean hasAssociatedProducts = false;	
	private Boolean hasRelatedProducts = false;
	private Boolean hasUpSellProducts = false;
	private Boolean hasCrossSellProducts = false;
	private Boolean hasOtherImages = false;
	private String visibility = "";
	
	private String color = "";
	private String weight = "";
	private String size = "";
	private String field1 = "";
	private String field2 = "";
	private String field3 = "";
	private String field4 = "";
	private String field5 = "";
	private String field6 = "";
	
	
	public String getProductId() {
	    return productId;
	}
	public void setProductId(String productId) {
	    this.productId = productId;
	}
	
	public String getProductName() {
	    return productName;
	}
	public void setProductName(String productName) {
	    this.productName = productName;
	}
	
	public String getProductSku() {
	    return productSku;
	}
	public void setProductSku(String productSku) {
	    this.productSku = productSku;
	}
	
	public String getProductPrice() {
	    return productPrice;
	}
	public void setProductPrice(String productPrice) {
	    this.productPrice = productPrice;
	}
	
	public String getSpecialPrice() {
	    return specialPrice;
	}
	public void setSpecialPrice(String specialPrice) {
	    this.specialPrice = specialPrice;
	}
	
	public String getImageUrl() {
	    return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
	    this.imageUrl = imageUrl;
	}
	
	public String getProductUrl() {
	    return productUrl;
	}
	public void setProductUrl(String productUrl) {
	    this.productUrl = productUrl;
	}
	
	public String getDescription() {
	    return description;
	}
	public void setDescription(String description) {
	    this.description = description;
	}
	
	public String getShortDescription() {
	    return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
	    this.shortDescription = shortDescription;
	}
	
	public Integer getProductStatus() {
	    return productStatus;
	}
	public void setProductStatus(Integer productStatus) {
	    this.productStatus = productStatus;
	}
	
	public String getProductType() {
	    return productType;
	}
	public void setProductType(String productType) {
	    this.productType = productType;
	}
	
	public String getManufacturer() {
	    return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
	    this.manufacturer = manufacturer;
	}
	
	public Integer getQunatity() {
	    return qunatity;
	}
	public void setQunatity(Integer qunatity) {
	    this.qunatity = qunatity;
	}
	
	public String getColor() {
	    return color;
	}
	public void setColor(String color) {
	    this.color = color;
	}
	
	public String getWeight() {
	    return weight;
	}
	public void setWeight(String weight) {
	    this.weight = weight;
	}
	
	public String getSize() {
	    return size;
	}
	public void setSize(String size) {
	    this.size = size;
	}
	
	public String getCreatedDate() {
	    return createdDate;
	}
	public void setCreatedDate(String createdDate) {
	    this.createdDate = createdDate;
	}
	
	public String getUpdatedDate() {
	    return updatedDate;
	}
	public void setUpdatedDate(String updatedDate) {
	    this.updatedDate = updatedDate;
	}
	public Boolean getHasAssociatedProducts() {
		return hasAssociatedProducts;
	}
	public void setHasAssociatedProducts(Boolean hasAssociatedProducts) {
		this.hasAssociatedProducts = hasAssociatedProducts;
	}
	public Boolean getHasRelatedProducts() {
		return hasRelatedProducts;
	}
	public void setHasRelatedProducts(Boolean hasRelatedProducts) {
		this.hasRelatedProducts = hasRelatedProducts;
	}
	public Boolean getHasUpSellProducts() {
		return hasUpSellProducts;
	}
	public void setHasUpSellProducts(Boolean hasUpSellProducts) {
		this.hasUpSellProducts = hasUpSellProducts;
	}
	public Boolean getHasCrossSellProducts() {
		return hasCrossSellProducts;
	}
	public void setHasCrossSellProducts(Boolean hasCrossSellProducts) {
		this.hasCrossSellProducts = hasCrossSellProducts;
	}
	public Boolean getHasOtherImages() {
		return hasOtherImages;
	}
	public void setHasOtherImages(Boolean hasOtherImages) {
		this.hasOtherImages = hasOtherImages;
	}
	public String getVisibility() {
		return visibility;
	}
	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}
	
	public String getField1() {
	    return field1;
	}
	public void setField1(String field1) {
	    this.field1 = field1;
	}
	
	public String getField2() {
	    return field2;
	}
	public void setField2(String field2) {
	    this.field2= field2;
	}
	
	public String getField3() {
	    return field3;
	}
	public void setField3(String field3) {
	    this.field3 = field3;
	}
	
	public String getField4() {
	    return field4;
	}
	public void setField4(String field4) {
	    this.field4 = field4;
	}
	
	public String getField5() {
	    return field5;
	}
	public void setField5(String field5) {
	    this.field5 = field5;
	}
	
	public String getField6() {
	    return field6;
	}
	public void setField6(String field6) {
	    this.field6 = field6;
	}
}
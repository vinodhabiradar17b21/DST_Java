package com.increasingly.importbulkdata.impl;

public class ProductCategoryMapping
{
//	"categories":[{"id":"8","name":"Men"},{"id":"15","name":"Women"}],
	private String categoryId;
	private String categoryName = "";
	private String productId;
	private int categoryLevel;
	
	public String getCategoryId() {
	    return categoryId;
	}
	public void setCategoryId(String categoryId) {
	    this.categoryId = categoryId;
	}
	
	public String getCategoryName() {
	    return categoryName;
	}
	public void setCategoryName(String categoryName) {
	    this.categoryName = categoryName;
	}
	
	public String getProductId() {
	    return productId;
	}
	public void setProductId(String productId) {
	    this.productId = productId;
	}
	public int getCategoryLevel() {
		return categoryLevel;
	}
	public void setCategoryLevel(int categoryLevel) {
		this.categoryLevel = categoryLevel;
	}
}
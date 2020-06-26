package com.increasingly.importbulkdata.impl;

public class AssociatedProducts
{	
	private String productId;
	private String associatedProductId;
	private String associationType;
	
	
	public String getProductId() {
	    return productId;
	}
	public void setProductId(String productId) {
	    this.productId = productId;
	}
	
	public String getAssociatedProductId() {
	    return associatedProductId;
	}
	public void setAssociatedProductId(String associatedProductId) {
	    this.associatedProductId = associatedProductId;
	}
	
	public String getAssociationType() {
	    return associationType;
	}
	public void setAssociationType(String associationType) {
	    this.associationType = associationType;
	}
}
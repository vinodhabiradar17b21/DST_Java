package com.increasingly.recommender.impl;

import java.util.ArrayList;
import java.util.List;

public class ProductAttributeDetails
{
	
	private String attributeCode = "";
	private String attributeId = "";
	private String frontEndLabel = "";
	private List<ProductAttributeValues> attributeValues = new ArrayList<ProductAttributeValues>();
	
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
	
	public String getFrontEndLabel() {
		return frontEndLabel;
	}
	
	public void setFrontEndLabel(String frontEndLabel) {
		this.frontEndLabel = frontEndLabel;
	}
	
	public List<ProductAttributeValues> getAttributeValues() {
		return attributeValues;
	}
	
	public void setAttributeValues(ProductAttributeValues tempAttributeValues) {
		this.attributeValues.add(tempAttributeValues);
	}
	
	
	
}
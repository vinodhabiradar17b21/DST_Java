package com.increasingly.recommender.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "ProductId","ProductSku","ProductName","ImageURL","ProductUrl","Price","SpecialPrice","Description","ShortDescription",
	"Quantity","Manufacturer","OtherImageList","Category","ProductType","Color","Size","Weight","Field1","Field2","Field3","Field4","Field5","Field6","Attributes"})
public class ProductDetails
{	
	private String productId;
	private String productName;
	private String productSku = "";
	private String imageUrl = "";
	private String productUrl;
	private String price;
	private String specialPrice;
	private String description = "";
	private Integer quantity = 0;
	private String manufacturer = "";
	private List<String> otherImageList;
	private String category = "";
	private String productType = "";
	private String color = "";
	private String size;
	private String weight;
	private String shortDescription = "";
	private String field1 = "";
	private String field2 = "";
	private String field3 = "";
	private String field4 = "";
	private String field5 = "";
	private String field6 = "";
	private List<ProductAttributeDetails> productAttributeDeatilsList = null;
	private String categoryId = "";
	
	
	public ProductDetails()
	{

	}

	@JsonProperty("ProductId")
	public String getProductId()
	{
		return this.productId;
	}
	
	public void setProductId(String productId)
	{
		this.productId = productId;
	}

	@JsonProperty("ProductName")
	public String getProductName()
	{
		return this.productName;
	}

	public void setProductName(String productName)
	{
		this.productName = productName;
	}
	
	@JsonProperty("ProductSku")
	public String getProductSku()
	{
		return this.productSku;
	}

	public void setProductSku(String productSku)
	{
		this.productSku = productSku;
	}
	
	@JsonProperty("ImageURL")
	public String getImageUrl()
	{
		return this.imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}	
	
	@JsonProperty("ProductUrl")
	public String getProductUrl()
	{
		return this.productUrl;
	}

	public void setProductUrl(String productUrl)
	{
		this.productUrl = productUrl;
	}

	@JsonProperty("Price")
	public String getPrice()
	{
		return price;
	}

	public void setPrice(String price)
	{
		this.price = price;
	}

	@JsonProperty("SpecialPrice")
	public String getSpecialPrice()
	{
		return this.specialPrice;
	}

	public void setSpecialPrice(String specialPrice)
	{
		this.specialPrice = specialPrice;
	}
	
	
	@JsonProperty("Description")
	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	@JsonProperty("ShortDescription")
	public String getShortDescription()
	{
		return shortDescription;
	}

	public void setShortDescription(String shortDescription)
	{
		this.shortDescription = shortDescription;
	}
		
	@JsonProperty("Quantity")
	public Integer getQuantity()
	{
		return this.quantity;
	}

	public void setQuantity(Integer quantity)
	{
		this.quantity = quantity;
	}
		
	@JsonProperty("Manufacturer")
	public String getManufacturer()
	{
		return this.manufacturer;
	}

	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}
		
	@JsonProperty("OtherImageList")
	public List<String> getOtherImageList()
	{
		return this.otherImageList;
	}

	public void setOtherImageList(List<String> otherImageList)
	{
		this.otherImageList = otherImageList;
	}
	
	
	@JsonProperty("Category")
	public String getCategory()
	{
		return this.category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}	
	

	@JsonProperty("ProductType")
	public String getProductType()
	{
		return this.productType;
	}

	public void setProductType(String productType)
	{
		this.productType = productType;
	}
	
	@JsonProperty("Color")
	public String getColor()
	{
		return this.color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}
	
	@JsonProperty("Size")
	public String getSize()
	{
		return this.size;
	}

	public void setSize(String size)
	{
		this.size = size;
	}
	
	@JsonProperty("Weight")
	public String getWeight()
	{
		return this.weight;
	}

	public void setWeight(String weight)
	{
		this.weight = weight;
	}
	
	@JsonProperty("Field1")
	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	@JsonProperty("Field2")
	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	@JsonProperty("Field3")
	public String getField3() {
		return field3;
	}

	public void setField3(String field3) {
		this.field3 = field3;
	}

	@JsonProperty("Field4")
	public String getField4() {
		return field4;
	}

	public void setField4(String field4) {
		this.field4 = field4;
	}

	@JsonProperty("Field5")
	public String getField5() {
		return field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	@JsonProperty("Field6")
	public String getField6() {
		return field6;
	}

	public void setField6(String field6) {
		this.field6 = field6;
	}
	
	@JsonProperty("Attributes")
	public List<ProductAttributeDetails> getProductAttributeDeatilsList() {
		return productAttributeDeatilsList;
	}

	public void setProductAttributeDeatilsList(
			List<ProductAttributeDetails> productAttributeDeatilsList) {
		this.productAttributeDeatilsList = productAttributeDeatilsList;
	}

	@JsonProperty("Category Id")
	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	
	
}
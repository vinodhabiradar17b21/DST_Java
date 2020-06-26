package com.increasingly.recommender.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"BundleId","Products","TotalPrice","TotalSpecialPrice","BundlePrice","DiscountPrice","ProductCount"})
public class BundleDetails
{	
	private Integer bundleId = 0;	
	private String totalPrice;
	private String totalSpecialPrice;
	private String bundlePrice;
	private String discountPrice;
	private Double bundleDiscountPercentage;
	private Integer productCount = 0;
	private List<ProductDetails> bundleProductItemDetails;
		
	@JsonProperty("BundleId")
	public Integer getBundleId()
	{
		return this.bundleId;
	}
	
	public void setBundleId(Integer bundleId)
	{
		this.bundleId = bundleId;
	}
	
	@JsonProperty("TotalPrice")
	public String getTotalPrice()
	{
		return this.totalPrice;
	}
	
	public void setTotalPrice(String totalPrice)
	{
		this.totalPrice = totalPrice;
	}
		
	@JsonProperty("TotalSpecialPrice")
	public String getTotalSpecialPrice()
	{
		return this.totalSpecialPrice;
	}
	
	public void setTotalSpecialPrice(String totalSpecialPrice)
	{
		this.totalSpecialPrice = totalSpecialPrice;
	}
	
	@JsonProperty("BundlePrice")
	public String getBundlePrice()
	{
		return this.bundlePrice;
	}
	
	public void setBundlePrice(String bundlePrice)
	{
		this.bundlePrice = bundlePrice;
	}
	
	@JsonProperty("DiscountPrice")
	public String getDiscountPrice()
	{
		return this.discountPrice;
	}
	
	public void setDiscountPrice(String discountPrice)
	{
		this.discountPrice = discountPrice;
	}
	
	@JsonProperty("BundleDiscountPercentage")
	public Double getBundleDiscountPercentage()
	{
		return this.bundleDiscountPercentage;
	}
	
	public void setBundleDiscountPercentage(Double bundleDiscountPercentage)
	{
		this.bundleDiscountPercentage = bundleDiscountPercentage;
	}
	
	@JsonProperty("ProductCount")
	public Integer getProductCount()
	{
		return this.productCount;
	}
	
	public void setProductCount(Integer productCount)
	{
		this.productCount = productCount;
	}
	
	@JsonProperty("Products")
	public List<ProductDetails> getBundleProductItemDetails()
	{
		return this.bundleProductItemDetails;
	}
	
	public void setBundleProductItemDetails(List<ProductDetails> bundleProductItemDetails)
	{
		this.bundleProductItemDetails = bundleProductItemDetails;
	}
}
package com.increasingly.recommender.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"BundleId","Products","TotalPrice","TotalSpecialPrice","BundlePrice","DiscountPrice","ProductCount","IsAbandonedCartBundle"})
public class BundleDetailsResponseContent
{	
	private Integer bundleId = 0;	
	private String totalPrice;
	private String totalSpecialPrice;
	private String bundlePrice;
	private String discountPrice;
	private Double bundleDiscountPercentage;
	private Integer productCount = 0;
	private List<ProductDetailsResponseContent> bundleProductItemDetails;
	private Boolean isAbandonedCartBundle = false;

	public BundleDetailsResponseContent(BundleDetails bundleDetails) {
		
		this.bundleId = bundleDetails.getBundleId();
		this.totalPrice = bundleDetails.getTotalPrice();	
		this.totalSpecialPrice = bundleDetails.getTotalSpecialPrice();
		this.bundlePrice = bundleDetails.getBundlePrice();
		this.discountPrice = bundleDetails.getDiscountPrice();
		this.bundleDiscountPercentage = bundleDetails.getBundleDiscountPercentage();
		this.productCount = bundleDetails.getProductCount();	
		
		this.bundleProductItemDetails = new ArrayList<ProductDetailsResponseContent>();
		for(ProductDetails productdetails:bundleDetails.getBundleProductItemDetails())
		{
  		  ProductDetailsResponseContent productResponseContent = new ProductDetailsResponseContent(productdetails);
  		  this.bundleProductItemDetails.add(productResponseContent);
		}		
	}

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
	public List<ProductDetailsResponseContent> getBundleProductItemDetails()
	{
		return this.bundleProductItemDetails;
	}
	
	public void setBundleProductItemDetails(List<ProductDetailsResponseContent> bundleProductItemDetails)
	{
		this.bundleProductItemDetails = bundleProductItemDetails;
	}

	@JsonProperty("IsAbandonedCartBundle")
	public Boolean getIsAbandonedCartBundle() {
		return isAbandonedCartBundle;
	}

	public void setIsAbandonedCartBundle(Boolean isAbandonedCartBundle) {
		this.isAbandonedCartBundle = isAbandonedCartBundle;
	}	
	
}
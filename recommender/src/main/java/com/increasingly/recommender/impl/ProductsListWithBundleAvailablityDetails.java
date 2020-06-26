package com.increasingly.recommender.impl;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductsListWithBundleAvailablityDetails
{
	private List<ProductWithBundleAvailability> productListWithNoOfBundleAvailabilityDetails = new ArrayList<ProductWithBundleAvailability>();

	@JsonProperty("ProductListWithNumberOfBundles")
	public List<ProductWithBundleAvailability> getProductListWithNoOfBundleAvailabilityDetails() {
		return productListWithNoOfBundleAvailabilityDetails;
	}

	public void setProductListWithNoOfBundleAvailabilityDetails(
			List<ProductWithBundleAvailability> productListWithNoOfBundleAvailabilityDetails) {
		this.productListWithNoOfBundleAvailabilityDetails = productListWithNoOfBundleAvailabilityDetails;
	}
}
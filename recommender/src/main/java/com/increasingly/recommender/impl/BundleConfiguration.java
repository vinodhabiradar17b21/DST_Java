package com.increasingly.recommender.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BundleConfiguration
{
	private Integer configId = 0;
	private Integer clientId = 0;
	private Integer feedId = 0;

	private Integer maxNoOfBundles = 10;
	private Integer maxNoOfBundlesForHomePage = 3;
	private Integer maxNoOfBundlesForProductPage = 10;
	private Integer maxNoOfBundlesForCategoryPage = 10;
	private Integer maxNoOfBundlesForCartPage = 3;

	private Integer maxNoOfProductInBundle = 3;
	private Integer maxNoOfProductInHomePageBundle = 3;
	private Integer maxNoOfProductInProductPageBundle = 3;
	private Integer maxNoOfProductInCategoryPageBundle = 2;
	private Integer maxNoOfProductInCartPageBundle = 2;
	
	private Integer minNoOfProductInProductPageBundle = 2;
	private Integer minNoOfProductInCategoryPageBundle = 2;
	private Integer minNoOfProductInCartPageBundle = 2;
	
	private Integer displayPatternId = 0;
	

	private Integer bundleTypeId = 0;
	
	private boolean useProductRating = false;
	private boolean isPersonalizationEnabled = false;
	private boolean isBrandExclusionEnabled = false;
	private boolean isCategoryExclusionEnabled = false;
	private boolean isProductExclusionEnabled = false;
	private boolean showCrossCategoryProducts = false;
	private boolean showBrandProducts = false;
	private boolean isDiscountingEnabled = false;	
	private String controlGroupVisitorIdCharSet = "";
	private Boolean isFreeShippingActive = false;
	private Double freeShippingSubTotal = 0.0;
	private String freeShippingTitle = "";
	private boolean useGeoCountryTargetting = false;
	private Integer decimalPrecision = 2;
	
	
	private transient Set<Integer> categoryExclusionList;
	private transient Set<Integer> allowedGeoCountryIdList;
	private transient List<Long> clientProductRecommendationList;
	private boolean hasClientRecommendations=false;
    private boolean ShowAbandondedCartProductBundles = false;
    private transient ArrayList<Integer> bundleSalesList;
    private boolean randomRotationEnabled = false;
    private transient ArrayList<Long> disabledRandomRotationProductList;
    private boolean logicalRotationEnabled = false;
    private boolean isMarginBundlingEnabled = false;
    private boolean backFillBundlesEnabled = false;
    private transient ArrayList<Integer> backFillBundlesList;
    private transient List<String> clientVisitorIds;

	public Integer getConfigId()
	{
		return this.configId;
	}
	
	public void setConfigId(Integer configId)
	{
		this.configId = configId;
	}
	
	public Integer getClientId()
	{
		return this.clientId;
	}

	public void setClientId(Integer clientId)
	{
		this.clientId = clientId;
	}
	
	public Integer getMaxNoOfBundlesForProductPage() {
		return maxNoOfBundlesForProductPage;
	}

	public void setMaxNoOfBundlesForProductPage(Integer maxNoOfBundlesForProductPage) {
		this.maxNoOfBundlesForProductPage = maxNoOfBundlesForProductPage;
	}
	
	public Integer getMaxNoOfBundlesForCategoryPage() {
		return maxNoOfBundlesForCategoryPage;
	}

	public void setMaxNoOfBundlesForCategoryPage(
			Integer maxNoOfBundlesForCategoryPage) {
		this.maxNoOfBundlesForCategoryPage = maxNoOfBundlesForCategoryPage;
	}
	
	public Integer getMaxNoOfBundlesForCartPage() {
		return maxNoOfBundlesForCartPage;
	}

	public void setMaxNoOfBundlesForCartPage(Integer maxNoOfBundlesForCartPage) {
		this.maxNoOfBundlesForCartPage = maxNoOfBundlesForCartPage;
	}
	
	public Integer getMaxNoOfProductInProductPageBundle() {
		return maxNoOfProductInProductPageBundle;
	}

	public void setMaxNoOfProductInProductPageBundle(
			Integer maxNoOfProductInProductPageBundle) {
		this.maxNoOfProductInProductPageBundle = maxNoOfProductInProductPageBundle;
	}
	
	public Integer getMaxNoOfProductInCategoryPageBundle() {
		return maxNoOfProductInCategoryPageBundle;
	}

	public void setMaxNoOfProductInCategoryPageBundle(
			Integer maxNoOfProductInCategoryPageBundle) {
		this.maxNoOfProductInCategoryPageBundle = maxNoOfProductInCategoryPageBundle;
	}
	
	public Boolean getIsFreeShippingActive() {
		return isFreeShippingActive;
	}

	public void setIsFreeShippingActive(Boolean isFreeShippingActive) {
		this.isFreeShippingActive = isFreeShippingActive;
	}

	public Double getFreeShippingSubTotal() {
		return freeShippingSubTotal;
	}

	public void setFreeShippingSubTotal(Double freeShippingSubTotal) {
		this.freeShippingSubTotal = freeShippingSubTotal;
	}

    public String getFreeShippingTitle() {
		return freeShippingTitle;
	}

	public void setFreeShippingTitle(String freeShippingTitle) {
		this.freeShippingTitle = freeShippingTitle;
	}
	
	public Integer getFeedId()
	{
		return this.feedId;
	}

	public void setFeedId(Integer feedId)
	{
		this.feedId = feedId;
	}
	
	public Integer getMaxNoOfBundles()
	{
		return this.maxNoOfBundles;
	}

	public void setMaxNoOfBundles(Integer maxNoOfBundles)
	{
		this.maxNoOfBundles = maxNoOfBundles;
	}
	
	
	public Integer getMaxNoOfProductInBundle()
	{
		return this.maxNoOfProductInBundle;
	}

	public void setMaxNoOfProductInBundle(Integer maxNoOfProductInBundle)
	{
		this.maxNoOfProductInBundle = maxNoOfProductInBundle;
	}
	
	public Integer getMaxNoOfProductInCartPageBundle() {
		return maxNoOfProductInCartPageBundle;
	}

	public void setMaxNoOfProductInCartPageBundle(
			Integer maxNoOfProductInCartPageBundle) {
		this.maxNoOfProductInCartPageBundle = maxNoOfProductInCartPageBundle;
	}

	
	public Integer getDisplayPatternId()
	{
		return this.displayPatternId;
	}

	public void setDisplayPatternId(Integer displayPatternId)
	{
		this.displayPatternId = displayPatternId;
	}
	
	
	public Integer getBundleTypeId()
	{
		return this.bundleTypeId;
	}

	public void setBundleTypeId(Integer bundleTypeId)
	{
		this.bundleTypeId = bundleTypeId;
	}
	
	
	public boolean getUseProductRating()
	{
		return this.useProductRating;
	}

	public void setUseProductRating(boolean useProductRating)
	{
		this.useProductRating = useProductRating;
	}	
	
	public boolean getIsPersonalizationEnabled()
	{
		return this.isPersonalizationEnabled;
	}

	public void setIsPersonalizationEnabled(boolean isPersonalizationEnabled)
	{
		this.isPersonalizationEnabled = isPersonalizationEnabled;
	}
		
	public boolean getIsBrandExclusionEnabled()
	{
		return this.isBrandExclusionEnabled;
	}

	public void setIsBrandExclusionEnabled(boolean isBrandExclusionEnabled)
	{
		this.isBrandExclusionEnabled = isBrandExclusionEnabled;
	}	
	
	public boolean getIsCategoryExclusionEnabled()
	{
		return this.isCategoryExclusionEnabled;
	}

	public void setIsCategoryExclusionEnabled(boolean isCategoryExclusionEnabled)
	{
		this.isCategoryExclusionEnabled = isCategoryExclusionEnabled;
	}	
		
	public boolean getIsProductExclusionEnabled()
	{
		return this.isProductExclusionEnabled;
	}

	public void setIsProductExclusionEnabled(boolean isProductExclusionEnabled)
	{
		this.isProductExclusionEnabled = isProductExclusionEnabled;
	}	

	public boolean getShowCrossCategoryProducts()
	{
		return this.showCrossCategoryProducts;
	}

	public void setShowCrossCategoryProducts(boolean showCrossCategoryProducts)
	{
		this.showCrossCategoryProducts = showCrossCategoryProducts;
	}	
	
	public boolean getShowBrandProducts()
	{
		return this.showBrandProducts;
	}

	public void setShowBrandProducts(boolean showBrandProducts)
	{
		this.showBrandProducts = showBrandProducts;
	}	
	
	public boolean getIsDiscountingEnabled()
	{
		return this.isDiscountingEnabled;
	}

	public void setIsDiscountingEnabled(boolean isDiscountingEnabled)
	{
		this.isDiscountingEnabled = isDiscountingEnabled;
	}	
	
	public Set<Integer> getCategoryExclusionList()
	{
		return this.categoryExclusionList;
	}
	
	public void setCategoryExclusionList(Set<Integer> value)
	{
		this.categoryExclusionList = value;
	}
	
	public String getControlGroupVisitorIdCharSet() {
		return controlGroupVisitorIdCharSet;
	}

	public void setControlGroupVisitorIdCharSet(String controlGroupVisitorIdCharSet) {
		this.controlGroupVisitorIdCharSet = controlGroupVisitorIdCharSet;
	}
	
	public Integer getMaxNoOfBundlesForHomePage() {
		return maxNoOfBundlesForHomePage;
	}

	public void setMaxNoOfBundlesForHomePage(Integer maxNoOfBundlesForHomePage) {
		this.maxNoOfBundlesForHomePage = maxNoOfBundlesForHomePage;
	}

	public Integer getMaxNoOfProductInHomePageBundle() {
		return maxNoOfProductInHomePageBundle;
	}

	public void setMaxNoOfProductInHomePageBundle(
			Integer maxNoOfProductInHomePageBundle) {
		this.maxNoOfProductInHomePageBundle = maxNoOfProductInHomePageBundle;
	}
	
	public boolean getUseGeoCountryTargetting()
	{
		return this.useGeoCountryTargetting;
	}

	public void setUseGeoCountryTargetting(boolean useGeoCountryTargetting)
	{
		this.useGeoCountryTargetting = useGeoCountryTargetting;
	}
	
	public Set<Integer> getAllowedGeoCountryIdList()
	{
		return this.allowedGeoCountryIdList;
	}
	
	public void setAllowedGeoCountryIdList(Set<Integer> value)
	{
		this.allowedGeoCountryIdList = value;
	}
	
	public List<Long> getClientProductRecommendationList()
	{
		return this.clientProductRecommendationList;
	}
	
	public void setClientProductRecommendationList(List<Long> value)
	{
		this.clientProductRecommendationList = value;
	}
	

	public Integer getDecimalPrecision() {
		return decimalPrecision;
	}

	public void setDecimalPrecision(Integer decimalPrecision) {
		this.decimalPrecision = decimalPrecision;
	}
	
	public Integer getMinNoOfProductInProductPageBundle() {
		return minNoOfProductInProductPageBundle;
	}

	public void setMinNoOfProductInProductPageBundle(
			Integer minNoOfProductInProductPageBundle) {
		this.minNoOfProductInProductPageBundle = minNoOfProductInProductPageBundle;
	}

	public Integer getMinNoOfProductInCategoryPageBundle() {
		return minNoOfProductInCategoryPageBundle;
	}

	public void setMinNoOfProductInCategoryPageBundle(
			Integer minNoOfProductInCategoryPageBundle) {
		this.minNoOfProductInCategoryPageBundle = minNoOfProductInCategoryPageBundle;
	}

	public Integer getMinNoOfProductInCartPageBundle() {
		return minNoOfProductInCartPageBundle;
	}

	public void setMinNoOfProductInCartPageBundle(
			Integer minNoOfProductInCartPageBundle) {
		this.minNoOfProductInCartPageBundle = minNoOfProductInCartPageBundle;
	}

	public boolean getHasClientRecommendations() {
		return hasClientRecommendations;
	}

	public void setHasClientRecommendations(
			boolean hasClientRecommendations) {
		this.hasClientRecommendations = hasClientRecommendations;
	}
        
        public boolean getShowAbandondedCartProductBundles() {
		return ShowAbandondedCartProductBundles;
	}

	public void setShowAbandondedCartProductBundles(
			boolean showAbandondedCartProductBundles) {
		ShowAbandondedCartProductBundles = showAbandondedCartProductBundles;
	}
 
	public ArrayList<Integer> getBundleSalesList() {
		return bundleSalesList;
	}

	public void setBundleSalesList(ArrayList<Integer> bundleSalesList) {
		this.bundleSalesList = bundleSalesList;
	}

	public boolean isRandomRotationEnabled() {
		return randomRotationEnabled;
	}

	public void setRandomRotationEnabled(boolean randomRotationEnabled) {
		this.randomRotationEnabled = randomRotationEnabled;
	}

	public ArrayList<Long> getDisabledRandomRotationProductList() {
		return disabledRandomRotationProductList;
	}

	public void setDisabledRandomRotationProductList(
			ArrayList<Long> disabledRandomRotationProductList) {
		this.disabledRandomRotationProductList = disabledRandomRotationProductList;
	}

	public boolean isLogicalRotationEnabled() {
		return logicalRotationEnabled;
	}

	public void setLogicalRotationEnabled(boolean logicalRotationEnabled) {
		this.logicalRotationEnabled = logicalRotationEnabled;
	}	
	
	public boolean getIsMarginBundlingEnabled() {
		return isMarginBundlingEnabled;
	}

	public void setIsMarginBundlingEnabled(boolean isMarginBundlingEnabled) {
		this.isMarginBundlingEnabled = isMarginBundlingEnabled;
	}

	public boolean isBackFillBundlesEnabled() {
		return backFillBundlesEnabled;
	}

	public void setBackFillBundlesEnabled(boolean backFillBundlesEnabled) {
		this.backFillBundlesEnabled = backFillBundlesEnabled;
	}

	public ArrayList<Integer> getBackFillBundlesList() {
		return backFillBundlesList;
	}

	public void setBackFillBundlesList(ArrayList<Integer> backFillBundlesList) {
		this.backFillBundlesList = backFillBundlesList;
	}

	public List<String> getClientVisitorIds() {
		return clientVisitorIds;
	}

	public void setClientVisitorIds(List<String> clientVisitorIds) {
		this.clientVisitorIds = clientVisitorIds;
	}
	
}
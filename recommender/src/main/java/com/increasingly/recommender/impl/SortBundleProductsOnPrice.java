package com.increasingly.recommender.impl;

import java.util.Comparator;

public class SortBundleProductsOnPrice implements Comparator<ProductDetailsResponseContent>
{
	
	@Override
	public int compare(ProductDetailsResponseContent p1, ProductDetailsResponseContent p2)
	{
		double product1_price = 0;
    	double product1_specialPrice = 0;
    	double product1_finalPrice = 0;
    	
    	double product2_price = 0;
    	double product2_specialPrice = 0;
    	double product2_finalPrice = 0;
    	
    	if(p1.getPrice() != null)
    	{
    		product1_price = Double.parseDouble(p1.getPrice());
    	}
    	
    	if(p1.getSpecialPrice() != null)
    	{
    		product1_specialPrice = Double.parseDouble(p1.getSpecialPrice());
    	}
    	
    	if(product1_specialPrice > 0)
    	{
    		product1_finalPrice = product1_specialPrice;
    	}
    	else 
    	{
    		product1_finalPrice = product1_price;
    	}
    	
    	// Product 2
    	if(p2.getPrice() != null)
    	{
    		product2_price = Double.parseDouble(p2.getPrice());
    	}
    	
    	if(p2.getSpecialPrice() != null)
    	{
    		product2_specialPrice = Double.parseDouble(p2.getSpecialPrice());
    	}
    	
    	if(product2_specialPrice > 0)
    	{
    		product2_finalPrice = product2_specialPrice;
    	}
    	else 
    	{
    		product2_finalPrice = product2_price;
    	}
    	
		if (product1_finalPrice < product2_finalPrice)
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}
	
}
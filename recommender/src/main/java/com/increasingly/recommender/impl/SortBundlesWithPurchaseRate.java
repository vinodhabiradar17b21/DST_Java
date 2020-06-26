package com.increasingly.recommender.impl;

import java.util.Comparator;

public class SortBundlesWithPurchaseRate implements Comparator<BundleIdWithPurchaseCount>
{
	
	@Override
	public int compare(BundleIdWithPurchaseCount b1, BundleIdWithPurchaseCount b2)
	{		
		if (b1.getPurchaseCount() == b2.getPurchaseCount())
		{
			return 0;
		}
		
		if (b1.getPurchaseCount() < b2.getPurchaseCount())
		{
			return 1;
		}
		else
		{
			return -1;
		}			
		
	}
	
}
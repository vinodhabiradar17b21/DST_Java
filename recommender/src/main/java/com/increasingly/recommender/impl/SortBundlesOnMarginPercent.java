package com.increasingly.recommender.impl;

import java.util.Comparator;

public class SortBundlesOnMarginPercent implements Comparator<BundleIdWithPurchaseCount>
{
	
	@Override
	public int compare(BundleIdWithPurchaseCount b1, BundleIdWithPurchaseCount b2)
	{				
		if (b1.getMarginPercent() == b2.getMarginPercent())
		{
			return 0;
		}
		
		if (b1.getMarginPercent() < b2.getMarginPercent())
		{
			return 1;
		}
		else
		{
			return -1;
		}	
	
	}
	
}
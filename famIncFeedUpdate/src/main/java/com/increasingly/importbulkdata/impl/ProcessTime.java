package com.increasingly.importbulkdata.impl;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class ProcessTime
{
	private long totalProcessTime = 0;

	public ProcessTime()
	{
	}

	public long getTimeTaken(DateTime startDateTime)
	{
		long timeTaken = 0;
		Interval elapsed = new Interval(startDateTime, DateTime.now());
		timeTaken = elapsed.toDurationMillis();
		return timeTaken;
	}

	/**
	 * @return the totalProcessTime
	 */
	public long getTotalProcessTime()
	{
		return totalProcessTime;
	}

	/**
	 * @param totalProcessTime
	 */
	public void setTotalProcessTime(long totalProcessTime)
	{
		this.totalProcessTime = totalProcessTime;
	}
	
}
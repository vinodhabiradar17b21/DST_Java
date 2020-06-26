package com.increasingly.importbulkdata.util;

public class FormatLoggerMessage
{
	private FormatLoggerMessage()
	{
		
	}
	
    public static String formatError(String constantType, String errorOrigin, String errorMethod,String errorMsg, String data)
    {
       return (constantType + "\t" + errorOrigin + "\t" + errorMethod + "\t" + errorMsg + "\t" + data);
    }
    
    public static String formatInfo(String constantType,String infoOrigin, String infoMethod, String infoMsg,String data)
    {
    	return (constantType + "\t" + infoOrigin + "\t" + infoMethod + "\t" + infoMsg + "\t" + data);
    }
}
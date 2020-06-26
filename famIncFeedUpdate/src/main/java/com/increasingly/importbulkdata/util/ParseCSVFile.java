package com.increasingly.importbulkdata.util;

import java.io.File;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * This class implements CSV file parser functionality
 */
public class ParseCSVFile
{

	public ArrayList<LinkedHashMap<String, Object>> getCSVParsedData(String delimiter, String filePath, String characterSetEncoding) throws Exception
	{		
		ArrayList<LinkedHashMap<String, Object>> dataList = new ArrayList<LinkedHashMap<String, Object>>();

		ArrayList<String> headerList = new ArrayList<String>();
		
		CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(delimiter.charAt(0)).withIgnoreEmptyLines(true);
		File csvData = new File(filePath);
		CSVParser parser = CSVParser.parse(csvData, Charset.forName(characterSetEncoding), format);
		
		headerList.addAll(parser.getHeaderMap().keySet());

		if (headerList.size() > 0)
		{
			for (CSVRecord csvRecord : parser)
			{					
				LinkedHashMap<String, Object> dataRowObject = new LinkedHashMap<String, Object>();

				if (headerList.size() == csvRecord.size())
				{
					for (String headerName : headerList)
					{
						dataRowObject.put(headerName, csvRecord.get(headerName));
					}
				}
				else
				{
					throw new Exception("The number of Data columns does not match with the number of Header coulmns (At row no : "
							+ csvRecord.getRecordNumber() + " in the feed).  Please ensure that the data is separted with proper delimiter.");
				}
				dataList.add(dataRowObject);
			}
		}
		else
		{
			throw new Exception("CSV file doesn't contain any headers.");
		}
		parser.close();

		return dataList;
		
	}
}
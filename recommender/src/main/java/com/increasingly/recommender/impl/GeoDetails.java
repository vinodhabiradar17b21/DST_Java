package com.increasingly.recommender.impl;

import java.io.File;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.increasingly.recommender.utils.FormatLoggerMessage;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;

import static com.increasingly.recommender.constants.Constants.*;

public class GeoDetails
{
	private static final Logger logger = LoggerFactory.getLogger(GeoDetails.class.getClass());
	
	private String countryIsoCode = "";
	private String countryName = "";
	private Integer countryId = 0;
	
	public String getCountryIsoCode() {
		return countryIsoCode;
	}
		
	public Integer getCountryId() {
		return countryId;
	}
	
	public String getCountryName() {
		return countryName;
	}
	
	public void setUserGeoDetails(String userIpAddress)
	{
		try
		{	
		  // A File object pointing to your GeoLite2 database
	       File dbFile = new File(Configuration.getGeoDatabaseCountryPath());
	 
	       // This creates the DatabaseReader object,which should be reused across lookups.	 
	       DatabaseReader reader = new DatabaseReader.Builder(dbFile).build();
	 
	       // A IP Address
	       InetAddress ipAddress = InetAddress.getByName(userIpAddress);	 
	        
	       // Get Country info
	       CountryResponse response = reader.country(ipAddress);
	
	       // Country Info
	       Country country = response.getCountry();
	       this.countryId = country.getGeoNameId();
	       this.countryIsoCode = country.getIsoCode();
	       this.countryName = country.getName();
	       
	       //System.out.println("Country Code: "+ this.countryId); 
	       //System.out.println("Country IsoCode: "+ this.countryIsoCode); // 'US'
	      // System.out.println("Country Name: "+ country.getName()); // 'United States'
	       
	       /*
	       System.out.println(country.getNames().get("zh-CN")); // '美国'	 
	       Subdivision subdivision = response.getMostSpecificSubdivision();
	       System.out.println("Subdivision Name: " +subdivision.getName()); // 'Minnesota'
	       System.out.println("Subdivision IsoCode: "+subdivision.getIsoCode()); // 'MN'
	 
	       // City Info.
	       City city = response.getCity();
	       System.out.println("City Name: "+ city.getName()); // 'Minneapolis'
	 
	       // Postal info
	       Postal postal = response.getPostal();
	       System.out.println(postal.getCode()); // '55455'
	 
	       // Geo Location info.
	       Location location = response.getLocation();
	        
	       // Latitude
	       System.out.println("Latitude: "+ location.getLatitude()); // 44.9733
	       	        
	       // Longitude
	       System.out.println("Longitude: "+ location.getLongitude()); // -93.2323
	       */
		}
		catch(Exception ex)
		{  
			String errorMessage = FormatLoggerMessage.formatInfo(LOG_INFO , "setUserGeoDetails" , "Error Occured while setting geo details" , "User IP Address - " + userIpAddress);
			logger.info(errorMessage,ex);
		}
	}	

}
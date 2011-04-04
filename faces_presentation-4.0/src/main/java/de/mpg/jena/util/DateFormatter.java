package de.mpg.jena.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter 
{
	public static String format(String str)
	{
		Date d = parseDate(str, "yyyy-MM-dd");
		if (d == null) d = parseDate(str, "yyyy-MM");
		if (d == null) d = parseDate(str, "yyyy");
		
		if (d != null) return Long.toString(d.getTime());
		
		return ""; 
	}
	
	public static Date parseDate(String str, String pattern)
	{
		try 
    	{
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			return sdf.parse(str);
		} 
    	catch (ParseException e) 
    	{
    		return null;
		} 
	}
	
}

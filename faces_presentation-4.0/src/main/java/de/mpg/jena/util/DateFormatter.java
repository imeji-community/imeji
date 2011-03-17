package de.mpg.jena.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter 
{
	public static String format(String date)
	{
		if (isFormatCompliant("yyyy-MM-dd", date)) date = date.concat("T00:00:00");
		else if (isFormatCompliant("yyyy-MM", date)) date = date.concat("-01T00:00:00");
		else if (isFormatCompliant("yyyy", date)) date = date.concat("-01-01T00:00:00");
		
//		try 
//    	{
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
//			Date d = sdf.parse(date);
//			return sdf.format(d);
//		} 
//    	catch (ParseException e) 
//    	{
//    		/*ERROR FORMATTING*/
//		} 
		return date.concat("Z");
	}
	
	public static boolean isFormatCompliant(String pattern, String str)
	{
		try 
    	{
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			sdf.parse(str);
    		return true;
		} 
    	catch (ParseException e) 
    	{
    		return false;
		} 
	}
}

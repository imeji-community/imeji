package de.mpg.j2j.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper
{
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    public static Calendar getCurrentDate()
    {
        Calendar cal = Calendar.getInstance();  
        return cal;
    }

    public static Calendar parseDate(String dateString)
    {
        try
        {
            Date d = format.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            return cal;
        }
        catch (ParseException e)
        {
            throw new RuntimeException("Error parsing date. Format should be yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", e);
        }
    }
}

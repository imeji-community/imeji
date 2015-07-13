package de.mpg.j2j.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Methods related to {@link Date}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class DateHelper {
  /**
   * Return the current {@link Calendar} from the system
   * 
   * @return
   */
  public static Calendar getCurrentDate() {
    Calendar cal = Calendar.getInstance();
    return cal;
  }

  /**
   * Parse a {@link String} to a {@link Calendar}
   * 
   * @param dateString
   * @return
   */
  public static Calendar parseDate(String dateString) {
    try {
      SimpleDateFormat format =
          new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
      Date d = format.parse(dateString);
      Calendar cal = Calendar.getInstance();
      cal.setTime(d);
      return cal;
    } catch (ParseException e) {
      throw new RuntimeException(
          "Error parsing date. Format should be yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", e);
    }
  }
}

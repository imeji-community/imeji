package de.mpg.imeji.logic.search.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.model.FileTypes.Type;
import de.mpg.imeji.logic.util.DateFormatter;

/**
 * Utility class for the {@link Search}
 * 
 * @author bastiens
 * 
 */
public class SearchUtils {

  /**
   * PArse a date with the format yyyy-MM-dd and return a time, which can be search
   * 
   * @param date
   * @return
   * @throws UnprocessableError
   */
  public static long parseDateAsTime(String date) throws UnprocessableError {
    Date d = DateFormatter.parseDate(date, "yyyy-MM-dd");
    if (d != null) {
      return d.getTime();
    }
    throw new UnprocessableError("wrong date format: " + date);
  }


  /**
   * Parse the query for File types and return a {@link List} of extension
   * 
   * @param fileTypes
   * @return
   */
  public static List<String> parseFileTypesAsExtensionList(String fileTypes) {
    List<String> extensions = new ArrayList<>();
    for (String typeName : fileTypes.split(Pattern.quote("|"))) {
      Type type = Imeji.CONFIG.getFileTypes().getType(typeName);
      for (String ext : type.getExtensionArray()) {
        extensions.add(ext);
      }
    }
    return extensions;
  }
}

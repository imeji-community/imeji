package de.mpg.imeji.logic.controller;

import java.util.List;

import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.query.SPARQLQueries;

/**
 * Controller for all actions which are related to statistics
 * 
 * @author saquet
 *
 */
public class StatisticsController extends ImejiController {

  /**
   * Return the all institute names (define by the suffix of emails users)
   * 
   * @return
   */
  public List<String> getAllInstitute() {
    Search s = new SPARQLSearch(SearchType.USER, null);
    return s.searchSimpleForQuery(SPARQLQueries.selectAllInstitutes()).getResults();
  }

  /**
   * Return the size of the storage used by one institute (i.e. the size of all files in collections
   * belonging the institut)
   * 
   * @param instituteName
   * @return
   */
  public long getUsedStorageSizeForInstitute(String instituteName) {
    Search s = new SPARQLSearch(SearchType.ALL, null);
    List<String> result =
        s.searchSimpleForQuery(SPARQLQueries.selectInstituteFileSize(instituteName)).getResults();
    if (result.size() == 1 && result.get(0) != null) {
      String size = result.get(0).replace("^^http://www.w3.org/2001/XMLSchema#integer", "");
      return Long.parseLong(size);
    }
    return 0;
  }
}

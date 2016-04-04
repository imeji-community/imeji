/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.net.URI;
import java.util.Collection;

import de.mpg.imeji.logic.resource.vo.MetadataProfile;
import de.mpg.imeji.logic.resource.vo.Statement;
import de.mpg.imeji.logic.search.model.SearchElement;
import de.mpg.imeji.logic.search.model.SearchElement.SEARCH_ELEMENTS;
import de.mpg.imeji.logic.search.model.SearchGroup;
import de.mpg.imeji.logic.search.model.SearchIndex;
import de.mpg.imeji.logic.search.model.SearchMetadata;
import de.mpg.imeji.logic.search.model.SearchPair;

public class SearchFormularHelper {
  public static String getCollectionId(SearchGroup searchGroup) {
    String id = null;
    for (SearchElement se : searchGroup.getElements()) {
      if (se.getType().equals(SEARCH_ELEMENTS.PAIR)
          && SearchIndex.SearchFields.col == ((SearchPair) se).getField()) {
        return ((SearchPair) se).getValue();
      } else if (se.getType().equals(SEARCH_ELEMENTS.GROUP)) {
        id = getCollectionId((SearchGroup) se);
        if (id != null) {
          return id;
        }
      }
    }
    return id;
  }

  public static String getProfileIdFromStatement(SearchGroup searchGroup,
      Collection<MetadataProfile> profiles) {
    String id = null;
    for (SearchElement se : searchGroup.getElements()) {
      if (se.getType().equals(SEARCH_ELEMENTS.METADATA)) {
        for (MetadataProfile mdp : profiles)
          if (isStatementOfProfile(((SearchMetadata) se).getStatement(), mdp))
            return mdp.getId().toString();
      } else if (se.getType().equals(SEARCH_ELEMENTS.GROUP))
        return getProfileIdFromStatement((SearchGroup) se, profiles);
    }
    return id;
  }

  private static boolean isStatementOfProfile(URI statementId, MetadataProfile p) {
    for (Statement s : p.getStatements()) {
      if (s.getId().compareTo(statementId) == 0)
        return true;
    }
    return false;
  }
}

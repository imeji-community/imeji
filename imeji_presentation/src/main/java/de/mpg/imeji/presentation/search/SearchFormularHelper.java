/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import de.mpg.imeji.logic.search.vo.SearchElement;
import de.mpg.imeji.logic.search.vo.SearchElement.SEARCH_ELEMENTS;
import de.mpg.imeji.logic.search.vo.SearchGroup;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchPair;

public class SearchFormularHelper
{
    public static String getCollectionId(SearchGroup searchGroup)
    {
        String id = null;
        for (SearchElement se : searchGroup.getElements())
        {
            if (se.getType().equals(SEARCH_ELEMENTS.PAIR)
                    && SearchIndex.names.IMAGE_COLLECTION.name().equals(((SearchPair)se).getIndex().getName()))
            {
                return ((SearchPair)se).getValue();
            }
            else if (se.getType().equals(SEARCH_ELEMENTS.GROUP))
            {
                id = getCollectionId((SearchGroup)se);
                if (id != null)
                {
                    return id;
                }
            }
        }
        return id;
    }
}

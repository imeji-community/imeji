/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.net.URI;
import java.util.Collection;

import de.mpg.imeji.logic.search.vo.SearchElement;
import de.mpg.imeji.logic.search.vo.SearchElement.SEARCH_ELEMENTS;
import de.mpg.imeji.logic.search.vo.SearchGroup;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchMetadata;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;

public class SearchFormularHelper
{
    public static String getCollectionId(SearchGroup searchGroup)
    {
        String id = null;
        for (SearchElement se : searchGroup.getElements())
        {
            if (se.getType().equals(SEARCH_ELEMENTS.PAIR)
                    && SearchIndex.names.col.name().equals(((SearchPair)se).getIndex().getName()))
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

    public static String getProfileIdFromStatement(SearchGroup searchGroup, Collection<MetadataProfile> profiles)
    {
        String id = null;
        for (SearchElement se : searchGroup.getElements())
        {
            if (se.getType().equals(SEARCH_ELEMENTS.METADATA))
            {
                for (MetadataProfile mdp : profiles)
                    if (isStatementOfProfile(((SearchMetadata)se).getStatement(), mdp))
                        return mdp.getId().toString();
            }
            else if (se.getType().equals(SEARCH_ELEMENTS.GROUP))
                return getProfileIdFromStatement((SearchGroup)se, profiles);
        }
        return id;
    }

    private static boolean isStatementOfProfile(URI statementId, MetadataProfile p)
    {
        for (Statement s : p.getStatements())
        {
            if (s.getId().compareTo(statementId) == 0)
                return true;
        }
        return false;
    }
}

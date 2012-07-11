/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.search.vo.SearchElement;
import de.mpg.imeji.logic.search.vo.SearchGroup;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SearchElement.SEARCH_ELEMENTS;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;

public class SearchFormular
{
    private Map<String, CollectionImeji> collectionsMap;
    private Map<String, MetadataProfile> profilesMap;
    private List<FormularGroup> groups;
    private static Logger logger = Logger.getLogger(SearchFormular.class);

    public SearchFormular()
    {
        groups = new ArrayList<FormularGroup>();
        collectionsMap = new HashMap<String, CollectionImeji>();
        profilesMap = new HashMap<String, MetadataProfile>();
    }

    public SearchFormular(SearchQuery searchQuery, Map<String, CollectionImeji> collectionsMap,
            Map<String, MetadataProfile> profilesMap)
    {
        this();
        this.collectionsMap = collectionsMap;
        this.profilesMap = profilesMap;
        for (SearchElement se : searchQuery.getElements())
        {
            if (se.getType().equals(SEARCH_ELEMENTS.GROUP))
            {
                String collectionId = SearchFormularHelper.getCollectionId((SearchGroup)se);
                groups.add(new FormularGroup((SearchGroup)se, profilesMap.get(collectionId), collectionId));
            }
        }
    }

    public SearchQuery getFormularAsSearchQuery()
    {
        SearchQuery searchQuery = new SearchQuery();
        for (FormularGroup g : groups)
        {
            if (!searchQuery.isEmpty())
            {
                searchQuery.addLogicalRelation(LOGICAL_RELATIONS.AND);
            }
            searchQuery.addGroup(g.getAsSearchGroup());
        }
        return searchQuery;
    }

    public void addSearchGroup(int pos)
    {
        FormularGroup fg = new FormularGroup();
        if (pos >= groups.size())
        {
            groups.add(fg);
        }
        else
        {
            groups.add(pos + 1, fg);
        }
    }

    public void changeSearchGroup(int pos)
    {
        FormularGroup fg = groups.get(pos);
        fg.getStatementMenu().clear();
        fg.setElements(new ArrayList<FormularElement>());
        if (fg.getCollectionId() != null)
        {
            fg.initStatementsMenu(profilesMap.get(fg.getCollectionId()));
            addElement(pos, 0);
        }
    }

    public void removeSearchGroup(int pos)
    {
        groups.remove(pos);
    }

    public void addElement(int groupPos, int elPos)
    {
        FormularGroup group = groups.get(groupPos);
        FormularElement fe = new FormularElement();
        String namespace = (String)group.getStatementMenu().get(0).getValue();
        fe.setNamespace(namespace);
        fe.initStatement(profilesMap.get(group.getCollectionId()), namespace);
        fe.initFiltersMenu();
        if (elPos >= group.getElements().size())
        {
            group.getElements().add(fe);
        }
        else
        {
            group.getElements().add(elPos + 1, fe);
        }
    }

    /**
     * Change the statement type of the element
     * 
     * @param groupPos
     * @param elPos
     */
    public void changeElement(int groupPos, int elPos, boolean keepValue)
    {
        FormularGroup group = groups.get(groupPos);
        FormularElement fe = group.getElements().get(elPos);
        String collectionId = group.getCollectionId();
        String namespace = fe.getNamespace();
        fe.initStatement(profilesMap.get(collectionId), namespace);
        fe.initFiltersMenu();
        if (!keepValue)
        {
            fe.setSearchValue("");
        }
    }

    public void removeElement(int groupPos, int elPos)
    {
        groups.get(groupPos).getElements().remove(elPos);
    }

    public List<FormularGroup> getGroups()
    {
        return groups;
    }

    public void setGroups(List<FormularGroup> groups)
    {
        this.groups = groups;
    }

    public Map<String, CollectionImeji> getCollectionsMap()
    {
        return collectionsMap;
    }

    public void setCollectionsMap(Map<String, CollectionImeji> collectionsMap)
    {
        this.collectionsMap = collectionsMap;
    }

    public Map<String, MetadataProfile> getProfilesMap()
    {
        return profilesMap;
    }

    public void setProfilesMap(Map<String, MetadataProfile> profilesMap)
    {
        this.profilesMap = profilesMap;
    }
}

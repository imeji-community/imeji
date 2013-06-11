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

/**
 * The form for the Advanced search. Is composed of {@link SearchGroupForm}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SearchForm
{
    private Map<String, CollectionImeji> collectionsMap;
    private Map<String, MetadataProfile> profilesMap;
    private List<SearchGroupForm> groups;
    private static Logger logger = Logger.getLogger(SearchForm.class);

    /**
     * Default Constructor
     */
    public SearchForm()
    {
        groups = new ArrayList<SearchGroupForm>();
        collectionsMap = new HashMap<String, CollectionImeji>();
        profilesMap = new HashMap<String, MetadataProfile>();
    }

    /**
     * Constructor for a {@link SearchQuery}: initialize the form from a query
     * 
     * @param searchQuery
     * @param collectionsMap
     * @param profilesMap
     */
    public SearchForm(SearchQuery searchQuery, Map<String, CollectionImeji> collectionsMap,
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
                groups.add(new SearchGroupForm((SearchGroup)se, profilesMap.get(collectionId), collectionId));
            }
        }
    }

    /**
     * Transform the {@link SearchForm} in a {@link SearchQuery}
     * 
     * @return
     */
    public SearchQuery getFormularAsSearchQuery()
    {
        SearchQuery searchQuery = new SearchQuery();
        for (SearchGroupForm g : groups)
        {
            if (!searchQuery.isEmpty())
            {
                searchQuery.addLogicalRelation(LOGICAL_RELATIONS.OR);
            }
            searchQuery.addGroup(g.getAsSearchGroup());
        }
        return searchQuery;
    }

    /**
     * Add a {@link SearchGroup} to the form
     * 
     * @param pos
     */
    public void addSearchGroup(int pos)
    {
        SearchGroupForm fg = new SearchGroupForm();
        if (pos >= groups.size())
        {
            groups.add(fg);
        }
        else
        {
            groups.add(pos + 1, fg);
        }
    }

    /**
     * Method called when the selected collection is changed in the select menu
     * 
     * @param pos
     */
    public void changeSearchGroup(int pos)
    {
        SearchGroupForm fg = groups.get(pos);
        fg.getStatementMenu().clear();
        fg.setSearchElementForms(new ArrayList<SearchMetadataForm>());
        if (fg.getCollectionId() != null)
        {
            fg.initStatementsMenu(profilesMap.get(fg.getCollectionId()));
            addElement(pos, 0);
        }
    }

    /**
     * Method called when the buttom remove group is called
     * 
     * @param pos
     */
    public void removeSearchGroup(int pos)
    {
        groups.remove(pos);
    }

    /**
     * Method called when the button add element is called
     * 
     * @param groupPos
     * @param elPos
     */
    public void addElement(int groupPos, int elPos)
    {
        SearchGroupForm group = groups.get(groupPos);
        SearchMetadataForm fe = new SearchMetadataForm();
        String namespace = (String)group.getStatementMenu().get(0).getValue();
        fe.setNamespace(namespace);
        fe.initStatement(profilesMap.get(group.getCollectionId()), namespace);
        fe.initOperatorMenu();
        if (elPos >= group.getSearchElementForms().size())
        {
            group.getSearchElementForms().add(fe);
        }
        else
        {
            group.getSearchElementForms().add(elPos + 1, fe);
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
        SearchGroupForm group = groups.get(groupPos);
        SearchMetadataForm fe = group.getSearchElementForms().get(elPos);
        String collectionId = group.getCollectionId();
        String namespace = fe.getNamespace();
        fe.initStatement(profilesMap.get(collectionId), namespace);
        fe.initOperatorMenu();
        if (!keepValue)
        {
            fe.setSearchValue("");
        }
    }

    public void removeElement(int groupPos, int elPos)
    {
        groups.get(groupPos).getSearchElementForms().remove(elPos);
    }

    public List<SearchGroupForm> getGroups()
    {
        return groups;
    }

    public void setGroups(List<SearchGroupForm> groups)
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

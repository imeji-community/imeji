/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.vo.SearchElement;
import de.mpg.imeji.logic.search.vo.SearchElement.SEARCH_ELEMENTS;
import de.mpg.imeji.logic.search.vo.SearchGroup;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.util.BeanHelper;

public class FormularGroup
{
    private List<FormularElement> elements;
    private String collectionId;
    private List<SelectItem> statementMenu;
    private static Logger logger = Logger.getLogger(FormularGroup.class);

    public FormularGroup()
    {
        elements = new ArrayList<FormularElement>();
        statementMenu = new ArrayList<SelectItem>();
    }

    public FormularGroup(SearchGroup searchGroup, MetadataProfile profile, String collectionId)
    {
        this();
        this.collectionId = collectionId;
        for (SearchElement se : searchGroup.getElements())
        {
            if (se.getType().equals(SEARCH_ELEMENTS.GROUP))
            {
                elements.add(new FormularElement((SearchGroup)se, profile));
            }
        }
        initStatementsMenu(profile);
    }

    public SearchGroup getAsSearchGroup()
    {
        SearchGroup searchGroup = new SearchGroup();
        searchGroup.addPair(new SearchPair(Search.getIndex(SearchIndex.names.col), SearchOperators.URI, collectionId));
        searchGroup.addLogicalRelation(LOGICAL_RELATIONS.AND);
        for (FormularElement e : elements)
        {
            searchGroup.addGroup(e.getAsSearchGroupNew());
            if (!searchGroup.isEmpty())
            {
                searchGroup.addLogicalRelation(e.getLogicalRelation());
            }
        }
        return searchGroup;
    }

    public void initStatementsMenu(MetadataProfile p)
    {
        if (p.getStatements() != null)
        {
            for (Statement st : p.getStatements())
            {
                String stName = ((MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class))
                        .getInternationalizedLabels().get(st.getId());
                statementMenu.add(new SelectItem(st.getId().toString(), stName));
            }
        }
    }

    public int getSize()
    {
        return elements.size();
    }

    public List<FormularElement> getElements()
    {
        return elements;
    }

    public void setElements(List<FormularElement> elements)
    {
        this.elements = elements;
    }

    public String getCollectionId()
    {
        return collectionId;
    }

    public void setCollectionId(String collectionId)
    {
        this.collectionId = collectionId;
    }

    public List<SelectItem> getStatementMenu()
    {
        return statementMenu;
    }

    public void setStatementMenu(List<SelectItem> statementMenu)
    {
        this.statementMenu = statementMenu;
    }
}

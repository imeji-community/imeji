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
import de.mpg.imeji.logic.search.vo.SearchGroup;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.util.MetadataTypesHelper;
import de.mpg.j2j.misc.LocalizedString;

public class FormularElement
{
    private String searchValue;
    private SearchOperators operator;
    private LOGICAL_RELATIONS logicalRelation;
    private boolean not = false;
    private String namespace;
    private List<SelectItem> filtersMenu;
    private List<SelectItem> predefinedValues;
    private Statement statement;
    private static Logger logger = Logger.getLogger(FormularElement.class);

    public FormularElement()
    {
        this.logicalRelation = LOGICAL_RELATIONS.OR;
        this.operator = SearchOperators.EQUALS;
    }

    public FormularElement(SearchGroup searchGroup, MetadataProfile profile)
    {
        this();
        for (SearchElement se : searchGroup.getElements())
        {
            switch (se.getType())
            {
                case PAIR:
                    if (SearchIndex.names.IMAGE_METADATA_STATEMENT.name().equals(((SearchPair)se).getIndex().getName()))
                    {
                        this.namespace = ((SearchPair)se).getValue();
                    }
                    else
                    {
                        this.searchValue = ((SearchPair)se).getValue();
                    }
                    operator = ((SearchPair)se).getOperator();
                    break;
                case LOGICAL_RELATIONS:
                    logicalRelation = ((SearchLogicalRelation)se).getLogicalRelation();
                    break;
                default:
                    break;
            }
        }
        initStatement(profile, namespace);
        initFiltersMenu();
    }

    public void initFiltersMenu()
    {
        filtersMenu = new ArrayList<SelectItem>();
        switch (MetadataTypesHelper.getTypesForNamespace(statement.getType().toString()))
        {
            case DATE:
                filtersMenu.add(new SelectItem(SearchOperators.EQUALS_DATE, "="));
                filtersMenu.add(new SelectItem(SearchOperators.GREATER_DATE, ">="));
                filtersMenu.add(new SelectItem(SearchOperators.LESSER_DATE, "<="));
                break;
            case NUMBER:
                filtersMenu.add(new SelectItem(SearchOperators.EQUALS_NUMBER, "="));
                filtersMenu.add(new SelectItem(SearchOperators.GREATER_NUMBER, ">="));
                filtersMenu.add(new SelectItem(SearchOperators.LESSER_NUMBER, "<="));
                break;
            default:
                operator = SearchOperators.REGEX;
                filtersMenu = null;
        }
    }

    public void initStatement(MetadataProfile p, String namespace)
    {
        for (Statement st : p.getStatements())
        {
            if (st.getId().toString().equals(namespace))
            {
                statement = st;
            }
        }
        initPredefinedValues(p);
    }

    public void initPredefinedValues(MetadataProfile profile)
    {
        if (statement.getLiteralConstraints().size() > 0)
        {
            predefinedValues = new ArrayList<SelectItem>();
            predefinedValues.add(new SelectItem(null, "Select"));
            for (String s : statement.getLiteralConstraints())
            {
                predefinedValues.add(new SelectItem(s, s));
            }
        }
        else
            predefinedValues = null;
    }

    public SearchGroup getAsSearchGroup()
    {
        SearchGroup group = new SearchGroup();
        if (searchValue == null || "".equals(searchValue.trim()))
        {
            return group;
        }
        switch (MetadataTypesHelper.getTypesForNamespace(statement.getType().toString()))
        {
            case DATE:
                group.addPair(new SearchPair(Search.getIndex("IMAGE_METADATA_TIME"), operator, searchValue, not));
                break;
            case GEOLOCATION:
                SearchGroup geoGroup = new SearchGroup();
                geoGroup.setNot(not);
                geoGroup.addPair(new SearchPair(Search.getIndex("METADATA_GEOLOCATION_LATITUDE"), operator, searchValue));
                geoGroup.addLogicalRelation(LOGICAL_RELATIONS.AND);
                geoGroup.addPair(new SearchPair(Search.getIndex("METADATA_GEOLOCATION_LONGITUDE"), operator,
                        searchValue));
                group.addGroup(geoGroup);
                break;
            case LICENSE:
                group.addPair(new SearchPair(Search.getIndex("IMAGE_METADATA_LICENSE"), operator, searchValue, not));
                break;
            case NUMBER:
                group.addPair(new SearchPair(Search.getIndex("IMAGE_METADATA_NUMBER"), operator, searchValue, not));
                break;
            case CONE_PERSON:
                SearchGroup personGroup = new SearchGroup();
                group.setNot(not);
                personGroup.addPair(new SearchPair(Search.getIndex("IMAGE_METADATA_PERSON_FAMLILYNAME"), operator,
                        searchValue));
                personGroup.addLogicalRelation(LOGICAL_RELATIONS.OR);
                personGroup.addPair(new SearchPair(Search.getIndex("IMAGE_METADATA_PERSON_GIVENNAME"), operator,
                        searchValue));
                personGroup.addLogicalRelation(LOGICAL_RELATIONS.OR);
                personGroup.addPair(new SearchPair(Search.getIndex("IMAGE_METADATA_PERSON_ORGANIZATION_TITLE"),
                        operator, searchValue));
                group.addGroup(personGroup);
                break;
            case PUBLICATION:
                group.addPair(new SearchPair(Search.getIndex("IMAGE_METADATA_PUBLICATION"), operator, searchValue, not));
                break;
            case TEXT:
                group.addPair(new SearchPair(Search.getIndex("IMAGE_METADATA_TEXT"), operator, searchValue, not));
                break;
            case LINK:
                group.addPair(new SearchPair(Search.getIndex("IMAGE_METADATA_URI"), operator, searchValue, not));
                break;
        }
        group.addLogicalRelation(LOGICAL_RELATIONS.AND);
        group.addPair(new SearchPair(Search.getIndex(SearchIndex.names.IMAGE_METADATA_STATEMENT), SearchOperators.URI,
                this.namespace, false));
        return group;
    }

    public String getSearchValue()
    {
        return searchValue;
    }

    public void setSearchValue(String searchValue)
    {
        this.searchValue = searchValue;
    }

    public String getNamespace()
    {
        return namespace;
    }

    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    public LOGICAL_RELATIONS getLogicalRelation()
    {
        return logicalRelation;
    }

    public void setLogicalRelation(LOGICAL_RELATIONS lr)
    {
        this.logicalRelation = lr;
    }

    public SearchOperators getFilter()
    {
        return operator;
    }

    public void setFilter(SearchOperators op)
    {
        this.operator = op;
    }

    public List<SelectItem> getFiltersMenu()
    {
        return filtersMenu;
    }

    public void setFiltersMenu(List<SelectItem> filtersMenu)
    {
        this.filtersMenu = filtersMenu;
    }

    public List<SelectItem> getPredefinedValues()
    {
        return predefinedValues;
    }

    public void setPredefinedValues(List<SelectItem> predefinedValues)
    {
        this.predefinedValues = predefinedValues;
    }

    public void setInverse(String str)
    {
        this.not = str.equals("true");
    }

    public String getInverse()
    {
        return Boolean.toString(not);
    }
}

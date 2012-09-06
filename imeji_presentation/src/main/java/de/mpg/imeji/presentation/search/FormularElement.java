/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.vo.SearchElement;
import de.mpg.imeji.logic.search.vo.SearchGroup;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation;
import de.mpg.imeji.logic.search.vo.SearchMetadata;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.util.MetadataTypesHelper;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

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

    public FormularElement()
    {
        this.logicalRelation = LOGICAL_RELATIONS.OR;
        this.operator = SearchOperators.REGEX;
    }

    public FormularElement(SearchGroup searchGroup, MetadataProfile profile)
    {
        this();
        for (SearchElement se : searchGroup.getElements())
        {
            switch (se.getType())
            {
                case PAIR:
                    // No use case so far with simple pairs
                    operator = ((SearchPair)se).getOperator();
                    searchValue = ((SearchPair)se).getValue();
                    not = ((SearchPair)se).isNot();
                    break;
                case METADATA:
                    operator = ((SearchMetadata)se).getOperator();
                    searchValue = ((SearchMetadata)se).getValue();
                    not = ((SearchMetadata)se).isNot();
                    namespace = ((SearchMetadata)se).getStatement().toString();
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
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        switch (MetadataTypesHelper.getTypesForNamespace(statement.getType().toString()))
        {
            case DATE:
                this.operator = SearchOperators.EQUALS;
                filtersMenu.add(new SelectItem(SearchOperators.EQUALS_DATE, "="));
                filtersMenu.add(new SelectItem(SearchOperators.GREATER_DATE, ">="));
                filtersMenu.add(new SelectItem(SearchOperators.LESSER_DATE, "<="));
                break;
            case NUMBER:
                this.operator = SearchOperators.EQUALS;
                filtersMenu.add(new SelectItem(SearchOperators.EQUALS_NUMBER, "="));
                filtersMenu.add(new SelectItem(SearchOperators.GREATER_NUMBER, ">="));
                filtersMenu.add(new SelectItem(SearchOperators.LESSER_NUMBER, "<="));
                break;
            default:
                filtersMenu.add(new SelectItem(SearchOperators.REGEX, "--"));
                filtersMenu.add(new SelectItem(SearchOperators.EQUALS, sessionBean.getLabel("exactly")));
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

    public SearchGroup getAsSearchGroupNew()
    {
        SearchGroup group = new SearchGroup();
        if (searchValue == null || "".equals(searchValue.trim()))
        {
            return group;
        }
        if (namespace != null)
        {
            URI ns = URI.create(namespace);
            switch (MetadataTypesHelper.getTypesForNamespace(statement.getType().toString()))
            {
                case DATE:
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.IMAGE_METADATA_TIME.name()),
                            operator, searchValue, ns, not));
                    break;
                case GEOLOCATION:
                    group.setNot(not);
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.IMAGE_METADATA_TITLE.name()),
                            operator, searchValue, ns));
                    break;
                case LICENSE:
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.IMAGE_METADATA_LICENSE.name()),
                            operator, searchValue, ns, not));
                    break;
                case NUMBER:
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.IMAGE_METADATA_NUMBER.name()),
                            operator, searchValue, ns, not));
                    break;
                case CONE_PERSON:
                  
                    group.setNot(not);
                    group.addPair(new SearchMetadata(Search
                            .getIndex(SearchIndex.names.IMAGE_METADATA_PERSON_FAMLILYNAME.name()), operator,
                            searchValue, ns));
                    group.addLogicalRelation(LOGICAL_RELATIONS.OR);
                    group.addPair(new SearchMetadata(Search
                            .getIndex(SearchIndex.names.IMAGE_METADATA_PERSON_GIVENNAME.name()), operator, searchValue,
                            ns));
                    group.addLogicalRelation(LOGICAL_RELATIONS.OR);
                    group.addPair(new SearchMetadata(Search
                            .getIndex(SearchIndex.names.IMAGE_METADATA_PERSON_ORGANIZATION_TITLE.name()), operator,
                            searchValue, ns));
                    break;
                case PUBLICATION:
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.IMAGE_METADATA_CITATION.name()),
                            operator, searchValue, ns, not));
                    break;
                case TEXT:
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.IMAGE_METADATA_TEXT.name()),
                            operator, searchValue, ns, not));
                    break;
                case LINK:
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.IMAGE_METADATA_URI.name()),
                            operator, searchValue, ns, not));
                    break;
            }
        }
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

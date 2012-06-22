/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.search.vo.SearchIndexes;
import de.mpg.imeji.logic.search.vo.SearchCriterion;
import de.mpg.imeji.logic.search.vo.SearchCriterion.Filtertype;
import de.mpg.imeji.logic.search.vo.SearchCriterion.Operator;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.util.MetadataTypesHelper;
import de.mpg.j2j.misc.LocalizedString;

public class FormularElement
{
    private String searchValue;
    private Filtertype filter;
    private Operator operator;
    private String namespace;
    private List<SelectItem> filtersMenu;
    private List<SelectItem> predefinedValues;
    private Statement statement;
    private static Logger logger = Logger.getLogger(FormularElement.class);

    public FormularElement()
    {
        this.operator = Operator.OR;
        this.filter = Filtertype.EQUALS;
    }

    public FormularElement(List<SearchCriterion> scList, MetadataProfile profile)
    {
        this();
        for (SearchCriterion sc : scList)
        {
            if (SearchIndexes.IMAGE_METADATA_STATEMENT.equals(sc.getNamespace()))
            {
                this.namespace = sc.getValue();
            }
            else if (sc.getValue() == null && sc.getChildren() != null && sc.getChildren().size() > 0)
            {
                this.searchValue = sc.getChildren().get(0).getValue();
                this.filter = sc.getFilterType();
            }
            else
            {
                this.searchValue = sc.getValue();
                this.filter = sc.getFilterType();
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
                filtersMenu.add(new SelectItem(Filtertype.EQUALS_DATE, "="));
                filtersMenu.add(new SelectItem(Filtertype.GREATER_DATE, ">="));
                filtersMenu.add(new SelectItem(Filtertype.LESSER_DATE, "<="));
                break;
            case NUMBER:
                filtersMenu.add(new SelectItem(Filtertype.EQUALS_NUMBER, "="));
                filtersMenu.add(new SelectItem(Filtertype.GREATER_NUMBER, ">="));
                filtersMenu.add(new SelectItem(Filtertype.LESSER_NUMBER, "<="));
                break;
            default:
                filter = Filtertype.REGEX;
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
            for (LocalizedString s : statement.getLiteralConstraints())
            {
                predefinedValues.add(new SelectItem(s, s.toString()));
            }
        }
        else
            predefinedValues = null;
    }

    public List<SearchCriterion> getAsSCList()
    {
        List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
        if (searchValue == null || "".equals(searchValue.trim()))
        {
            return scList;
        }
        switch (MetadataTypesHelper.getTypesForNamespace(statement.getType().toString()))
        {
            case DATE:
                scList.add(new SearchCriterion(operator, SearchIndexes.IMAGE_METADATA_DATE, searchValue, filter));
                break;
            case GEOLOCATION:
                // TODO can not work, should find a solution for searching in geolocation
                SearchCriterion scLat = new SearchCriterion(operator,
                        SearchIndexes.IMAGE_METADATA_GEOLOCATION_LATITUDE, searchValue, filter);
                SearchCriterion scLong = new SearchCriterion(operator,
                        SearchIndexes.IMAGE_METADATA_GEOLOCATION_LONGITUDE, searchValue, filter);
                scList.add(scLat);
                scList.add(scLong);
                break;
            case LICENSE:
                scList.add(new SearchCriterion(operator, SearchIndexes.IMAGE_METADATA_TYPE_URI, searchValue, filter));
                break;
            case NUMBER:
                scList.add(new SearchCriterion(operator, SearchIndexes.IMAGE_METADATA_NUMBER, searchValue, filter));
                break;
            case CONE_PERSON:
                List<SearchCriterion> subList = new ArrayList<SearchCriterion>();
                subList.add(new SearchCriterion(Operator.OR, SearchIndexes.IMAGE_METADATA_PERSON_FAMILY_NAME,
                        searchValue, filter));
                subList.add(new SearchCriterion(Operator.OR, SearchIndexes.IMAGE_METADATA_PERSON_GIVEN_NAME,
                        searchValue, filter));
                subList.add(new SearchCriterion(Operator.OR, SearchIndexes.IMAGE_METADATA_PERSON_ORGANIZATION_NAME,
                        searchValue, filter));
                scList.add(new SearchCriterion(Operator.AND, subList));
                break;
            case PUBLICATION:
                scList.add(new SearchCriterion(operator, SearchIndexes.IMAGE_METADATA_TYPE_URI, searchValue, filter));
                break;
            case TEXT:
                scList.add(new SearchCriterion(operator, SearchIndexes.IMAGE_METADATA_TEXT, searchValue, filter));
                break;
            case LINK:
                scList.add(new SearchCriterion(operator, SearchIndexes.IMAGE_METADATA_TYPE_URI, searchValue, filter));
                break;
        }
        scList.add(new SearchCriterion(Operator.AND, SearchIndexes.IMAGE_METADATA_STATEMENT, namespace,
                Filtertype.URI));
        return scList;
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

    // public URI getStatementType()
    // {
    // return statementType;
    // }
    //
    // public void setStatementType(URI statementType)
    // {
    // this.statementType = statementType;
    // }
    public Operator getOperator()
    {
        return operator;
    }

    public void setOperator(Operator operator)
    {
        this.operator = operator;
    }

    public Filtertype getFilter()
    {
        return filter;
    }

    public void setFilter(Filtertype filter)
    {
        this.filter = filter;
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
}

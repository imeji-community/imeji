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
import de.mpg.imeji.logic.util.DateFormatter;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.util.MetadataTypesHelper;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * An element in the advanced search form
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SearchMetadataForm
{
    private String searchValue;
    private SearchOperators operator;
    private LOGICAL_RELATIONS logicalRelation;
    private boolean not = false;
    private String namespace;
    private List<SelectItem> operatorMenu;
    private List<SelectItem> predefinedValues;
    private Statement statement;

    /**
     * Default constructor, create empty {@link SearchMetadataForm}
     */
    public SearchMetadataForm()
    {
        this.logicalRelation = LOGICAL_RELATIONS.OR;
        this.operator = SearchOperators.REGEX;
    }

    /**
     * Create a new {@link SearchMetadataForm} from a {@link SearchGroup}
     * 
     * @param searchGroup
     * @param profile
     */
    public SearchMetadataForm(SearchGroup searchGroup, MetadataProfile profile)
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
        initOperatorMenu();
    }

    /**
     * Intialize the filtrsMenu
     */
    public void initOperatorMenu()
    {
        operatorMenu = new ArrayList<SelectItem>();
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        switch (MetadataTypesHelper.getTypesForNamespace(statement.getType().toString()))
        {
            case DATE:
                operatorMenu.add(new SelectItem(SearchOperators.EQUALS, "="));
                operatorMenu.add(new SelectItem(SearchOperators.GREATER, ">="));
                operatorMenu.add(new SelectItem(SearchOperators.LESSER, "<="));
                break;
            case NUMBER:
                operatorMenu.add(new SelectItem(SearchOperators.EQUALS, "="));
                operatorMenu.add(new SelectItem(SearchOperators.GREATER, ">="));
                operatorMenu.add(new SelectItem(SearchOperators.LESSER, "<="));
                break;
            default:
                operatorMenu.add(new SelectItem(SearchOperators.REGEX, "--"));
                operatorMenu.add(new SelectItem(SearchOperators.EQUALS, sessionBean.getLabel("exactly")));
        }
    }

    /**
     * @param p
     * @param namespace
     */
    public void initStatement(MetadataProfile p, String namespace)
    {
        for (Statement st : p.getStatements())
        {
            if (st.getId().toString().equals(namespace))
            {
                statement = st;
            }
        }
        if (statement == null)
            throw new RuntimeException("Statement with namespace \"" + namespace + "\" not found in profile "
                    + p.getId());
        initPredefinedValues();
    }

    /**
     * Initialize the predefined values if there are some defined in the profile
     */
    public void initPredefinedValues()
    {
        if (statement.getLiteralConstraints() != null && statement.getLiteralConstraints().size() > 0)
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

    /**
     * Return the {@link SearchMetadataForm} as a {@link SearchGroup}
     * 
     * @return
     */
    public SearchGroup getAsSearchGroup()
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
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.time.name()), operator,
                            DateFormatter.format(searchValue), ns, not));
                    break;
                case GEOLOCATION:
                    group.setNot(not);
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.title.name()), operator,
                            searchValue, ns));
                    break;
                case LICENSE:
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.license.name()), operator,
                            searchValue, ns, not));
                    break;
                case NUMBER:
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.number.name()), operator,
                            searchValue, ns, not));
                    break;
                case CONE_PERSON:
                    group.setNot(not);
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.person_family.name()), operator,
                            searchValue, ns));
                    group.addLogicalRelation(LOGICAL_RELATIONS.OR);
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.person_given.name()), operator,
                            searchValue, ns));
                    group.addLogicalRelation(LOGICAL_RELATIONS.OR);
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.person_org_title.name()),
                            operator, searchValue, ns));
                    break;
                case PUBLICATION:
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.citation.name()), operator,
                            searchValue, ns, not));
                    break;
                case TEXT:
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.text.name()), operator,
                            searchValue, ns, not));
                    break;
                case LINK:
                    group.addPair(new SearchMetadata(Search.getIndex(SearchIndex.names.link.name()), operator,
                            searchValue, ns, not));
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

    public SearchOperators getOperator()
    {
        return operator;
    }

    public void setOperator(SearchOperators op)
    {
        this.operator = op;
    }

    public List<SelectItem> getOperatorMenu()
    {
        return operatorMenu;
    }

    public void setOperatorMenu(List<SelectItem> filtersMenu)
    {
        this.operatorMenu = filtersMenu;
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

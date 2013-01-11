package de.mpg.imeji.logic.search.vo;

import java.util.List;

import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;

/**
 * Element of a {@link SearchQuery}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class SearchElement
{
    public enum SEARCH_ELEMENTS
    {
        LOGICAL_RELATIONS, PAIR, GROUP, QUERY, METADATA;
    }

    public abstract SEARCH_ELEMENTS getType();

    public abstract List<SearchElement> getElements();

    public void addLogicalRelation(LOGICAL_RELATIONS lr)
    {
        if (!hasElements())
        {
            throw new RuntimeException("Operation not allowed for " + getType());
        }
        if (!isEmpty() && !SEARCH_ELEMENTS.LOGICAL_RELATIONS.equals(getTypeOfLastElement()))
        {
            getElements().add(new SearchLogicalRelation(lr));
        }
        else if (SEARCH_ELEMENTS.LOGICAL_RELATIONS.equals(getTypeOfLastElement()))
        {
            throw new RuntimeException(
                    "Wrong search query: Logical relations can not be added after a logical relation");
        }
    }

    public void addPair(SearchPair pair)
    {
        if (!hasElements())
        {
            throw new RuntimeException("Operation not allowed for " + getType());
        }
        if (isEmpty() || SEARCH_ELEMENTS.LOGICAL_RELATIONS.equals(getTypeOfLastElement()))
        {
            getElements().add(pair);
        }
        else
        {
            throw new RuntimeException(
                    "Wrong search query. A pair should be added after a logical relation or a the begining of the query!");
        }
    }

    public void addGroup(SearchGroup group)
    {
        if (!hasElements())
        {
            throw new RuntimeException("Operation not allowed for " + getType());
        }
        if (isEmpty() || SEARCH_ELEMENTS.LOGICAL_RELATIONS.equals(getTypeOfLastElement()))
        {
            getElements().add(group);
        }
        else
        {
            throw new RuntimeException(
                    "Wrong search query. A group should be added after a logical relation or a the begining of the query!");
        }
    }

    public SEARCH_ELEMENTS getTypeOfLastElement()
    {
        SearchElement se = getLastElement();
        if (se == null)
        {
            return null;
        }
        return se.getType();
    }

    private SearchElement getLastElement()
    {
        if (!hasElements())
        {
            throw new RuntimeException("Operation not allowed for " + getType());
        }
        if (!isEmpty())
        {
            return getElements().get(getElements().size() - 1);
        }
        return null;
    }

    public boolean isEmpty()
    {
        if (!hasElements())
        {
            throw new RuntimeException("Operation not allowed for " + getType());
        }
        return getElements().size() == 0;
    }

    private boolean hasElements()
    {
        return getType().equals(SEARCH_ELEMENTS.QUERY) || getType().equals(SEARCH_ELEMENTS.GROUP);
    }
}

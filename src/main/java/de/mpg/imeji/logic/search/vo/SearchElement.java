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

    /**
     * Add a {@link LOGICAL_RELATIONS} after a {@link SearchElement}
     * 
     * @param lr
     */
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

    /**
     * Add a {@link SearchPair} after a {@link SearchElement}
     * 
     * @param pair
     */
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

    /**
     * Add a {@link SearchGroup} after a {@link SearchElement}
     * 
     * @param group
     */
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

    /**
     * Get the {@link SEARCH_ELEMENTS} of the last element of a {@link SearchElement} (if it is a {@link SearchGroup} or
     * a {@link SearchQuery})
     * 
     * @return
     */
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

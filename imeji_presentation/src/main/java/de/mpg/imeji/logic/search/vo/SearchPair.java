package de.mpg.imeji.logic.search.vo;

import java.util.List;

public class SearchPair extends SearchElement
{
    private boolean not = false;
    private SearchIndex index;
    private SearchOperators operator;
    private String value;

    public SearchPair(SearchIndex index, SearchOperators operator, String value)
    {
        this(index, operator, value, false);
    }

    public SearchPair(SearchIndex index, SearchOperators operator, String value, boolean not)
    {
        this.index = index;
        this.operator = operator;
        this.value = value;
        this.not = not;
    }

    public SearchIndex getIndex()
    {
        return index;
    }

    public void setIndex(SearchIndex index)
    {
        this.index = index;
    }

    public SearchOperators getOperator()
    {
        return operator;
    }

    public void setOperator(SearchOperators operator)
    {
        this.operator = operator;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public SEARCH_ELEMENTS getType()
    {
        return SEARCH_ELEMENTS.PAIR;
    }

    @Override
    public List<SearchElement> getElements()
    {
        return null;
    }

    public void setNot(boolean not)
    {
        this.not = not;
    }

    public boolean isNot()
    {
        return not;
    }
}

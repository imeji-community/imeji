package de.mpg.imeji.logic.search.vo;

import java.util.ArrayList;
import java.util.List;

public class SearchQuery extends SearchElement
{
    private List<SearchElement> elements = null;

    public SearchQuery()
    {
        elements = new ArrayList<SearchElement>();
    }

    public SearchQuery(List<SearchElement> elements)
    {
        this.elements = new ArrayList<SearchElement>(elements);
    }

    public void clear()
    {
        elements.clear();
    }

    public void setElements(List<SearchElement> elements)
    {
        this.elements = elements;
    }

    public List<SearchElement> getElements()
    {
        return elements;
    }

    @Override
    public SEARCH_ELEMENTS getType()
    {
        return SEARCH_ELEMENTS.QUERY;
    }
}

package de.mpg.imeji.logic.search.vo;

import java.util.ArrayList;
import java.util.List;

public class SearchIndex
{
    private String namespace;
    private SearchIndex parent;
    private List<SearchIndex> children = new ArrayList<SearchIndex>();
    private boolean listType = false;;

    public SearchIndex(String namespace)
    {
        this.setNamespace(namespace);
    }

    public SearchIndex(String namespace, SearchIndex parent)
    {
        this.setNamespace(namespace);
        this.parent = parent;
        if (parent != null && !parent.getChildren().contains(this))
        {
            parent.getChildren().add(this);
        }
    }

    public SearchIndex(String namespace, SearchIndex parent, boolean listType)
    {
        this.setNamespace(namespace);
        this.parent = parent;
        this.setListType(listType);
        if (parent != null && !parent.getChildren().contains(this))
        {
            parent.getChildren().add(this);
        }
    }
    
    public SearchIndex getParent()
    {
        return parent;
    }

    public void setParent(SearchIndex parent)
    {
        this.parent = parent;
    }

    public void setChildren(List<SearchIndex> children)
    {
        this.children = children;
    }

    public List<SearchIndex> getChildren()
    {
        return children;
    }
    
    public void setListType(boolean listType)
    {
        this.listType = listType;
    }

    public boolean isListType()
    {
        return listType;
    }

    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    public String getNamespace()
    {
        return namespace;
    }
}

package de.mpg.jena.controller;

import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;

public class SortCriterion
{
    
    public enum SortOrder {
        ASCENDING, DESCENDING;
    }
    
    private ImejiNamespaces sortingCriterion;
    
    private SortOrder sortOrder;

    public SortCriterion(ImejiNamespaces sc, SortOrder so)
    {
        this.sortingCriterion = sc;
        this.sortOrder = so;
    }
    
    public SortCriterion()
    {
        this.sortOrder = SortOrder.ASCENDING;
    }
    
    public void setSortOrder(SortOrder sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public SortOrder getSortOrder()
    {
        return sortOrder;
    }

    public void setSortingCriterion(ImejiNamespaces sortingCriterion)
    {
        this.sortingCriterion = sortingCriterion;
    }

    public ImejiNamespaces getSortingCriterion()
    {
        return sortingCriterion;
    }
    
}

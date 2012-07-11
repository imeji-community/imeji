///**
// * License: src/main/resources/license/escidoc.license
// */
//package de.mpg.imeji.logic.search.vo;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SearchCriterion
//{
//    public enum Operator
//    {
//        AND, OR, NOTAND, NOTOR;
//    }
//
//    public enum Filtertype
//    {
//        REGEX, URI, EQUALS, BOUND, EQUALS_NUMBER, GREATER_NUMBER, LESSER_NUMBER, EQUALS_DATE, GREATER_DATE, LESSER_DATE, NOT;
//    }
//
//    private SearchIndexes namespace;
//    private String value;
//    private Operator operator = Operator.AND;
//    private Filtertype filterType = Filtertype.REGEX;
//    private List<SearchCriterion> children = new ArrayList<SearchCriterion>();
//    private SearchCriterion parent;
//    private boolean inverse = false;
//    private boolean bound = false;
//
//    public SearchCriterion()
//    {
//    }
//
//    public SearchCriterion(SearchIndexes namespace, String value)
//    {
//        this.namespace = namespace;
//        this.value = value;
//    }
//
//    public SearchCriterion(Operator op, SearchIndexes namespace, String value, Filtertype filterType)
//    {
//        this.namespace = namespace;
//        this.value = value;
//        this.operator = op;
//        this.filterType = filterType;
//    }
//
//    public SearchCriterion(Operator op, List<SearchCriterion> children)
//    {
//        this.operator = op;
//        this.children = children;
//    }
//
//    public SearchIndexes getNamespace()
//    {
//        return namespace;
//    }
//
//    public void setNamespace(SearchIndexes namespace)
//    {
//        this.namespace = namespace;
//    }
//
//    public String getValue()
//    {
//        return value;
//    }
//
//    public void setValue(String value)
//    {
//        this.value = value;
//    }
//
//    public Operator getOperator()
//    {
//        return operator;
//    }
//
//    public void setOperator(Operator operator)
//    {
//        this.operator = operator;
//    }
//
//    public void setFilterType(Filtertype filterType)
//    {
//        this.filterType = filterType;
//    }
//
//    public Filtertype getFilterType()
//    {
//        return filterType;
//    }
//
//    public void setChildren(List<SearchCriterion> children)
//    {
//        this.children = children;
//    }
//
//    public List<SearchCriterion> getChildren()
//    {
//        return children;
//    }
//
//    public void setInverse(boolean inverse)
//    {
//        this.inverse = inverse;
//    }
//
//    public boolean isInverse()
//    {
//        return inverse;
//    }
//
//    public void setParent(SearchCriterion parent)
//    {
//        this.parent = parent;
//    }
//
//    public SearchCriterion getParent()
//    {
//        return parent;
//    }
//
//    public boolean isBound()
//    {
//        return bound;
//    }
//
//    public void setBound(boolean bound)
//    {
//        this.bound = bound;
//    }
//}

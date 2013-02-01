///**
// * License: src/main/resources/license/escidoc.license
// */
//
//package de.mpg.imeji.logic.controller;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import de.mpg.imeji.logic.search.vo.SearchIndexes;
//
//public class ImejiQueryVariable implements Comparable<ImejiQueryVariable>
//{
//    
//    private SearchIndexes namespace;
//    
//    private List<ImejiQueryVariable> children = new ArrayList<ImejiQueryVariable>();
//    
//    private String variable;
//
//    public ImejiQueryVariable(SearchIndexes namespace, List<ImejiQueryVariable> children)
//    {
//        super();
//        this.namespace = namespace;
//        this.children = children;
//    }
//
//    public void setNamespace(SearchIndexes namespace)
//    {
//        this.namespace = namespace;
//    }
//
//    public SearchIndexes getNamespace()
//    {
//        return namespace;
//    }
//
//  
//    public void setVariable(String variable)
//    {
//        this.variable = variable;
//    }
//
//    public String getVariable()
//    {
//        return variable;
//    }
//
//    public int compareTo(ImejiQueryVariable o)
//    {
//        
//        return o.getNamespace().compareTo(this.getNamespace());
//    }
//
//    public void setChildren(List<ImejiQueryVariable> children)
//    {
//        this.children = children;
//    }
//
//    public List<ImejiQueryVariable> getChildren()
//    {
//        return children;
//    }
//    
//    
// }

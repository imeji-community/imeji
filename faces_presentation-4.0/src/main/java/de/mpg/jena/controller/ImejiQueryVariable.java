/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.controller;

import java.util.ArrayList;
import java.util.List;

import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;

public class ImejiQueryVariable implements Comparable<ImejiQueryVariable>
{
    
    private ImejiNamespaces namespace;
    
    private List<ImejiQueryVariable> children = new ArrayList<ImejiQueryVariable>();
    
    private String variable;

    public ImejiQueryVariable(ImejiNamespaces namespace, List<ImejiQueryVariable> children)
    {
        super();
        this.namespace = namespace;
        this.children = children;
    }

    public void setNamespace(ImejiNamespaces namespace)
    {
        this.namespace = namespace;
    }

    public ImejiNamespaces getNamespace()
    {
        return namespace;
    }

  
    public void setVariable(String variable)
    {
        this.variable = variable;
    }

    public String getVariable()
    {
        return variable;
    }

    public int compareTo(ImejiQueryVariable o)
    {
        
        return o.getNamespace().compareTo(this.getNamespace());
    }

    public void setChildren(List<ImejiQueryVariable> children)
    {
        this.children = children;
    }

    public List<ImejiQueryVariable> getChildren()
    {
        return children;
    }
    
    
}

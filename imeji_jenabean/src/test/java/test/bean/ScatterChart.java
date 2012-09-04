package test.bean;

import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Namespace;

@Namespace("http://code.google.com/apis/visualization/") 
public class ScatterChart extends Visualization 
{ 
    private String xBinding = null; 
    private Collection<String> yBindings = new LinkedList<String>(); 
    public String getXBinding() 
    { 
        return xBinding; 
    } 
    public void setXBinding(String xBinding) 
    { 
        this.xBinding = xBinding; 
    } 
    public Collection<String> getYBindings() 
    { 
        return yBindings; 
    } 
    public void setYBindings(Collection<String> yBindings) 
    { 
        this.yBindings = yBindings; 
    } 
    public void addYBinding(String yBinding) 
    { 
        yBindings.add(yBinding); 
    } 
    
}

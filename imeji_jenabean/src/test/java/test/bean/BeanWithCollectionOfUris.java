package test.bean;

import java.net.URI;
import java.util.Collection;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

@Namespace("http://inqle.org/ns/0.1/") 
public class BeanWithCollectionOfUris { 


    private String id; 
    private Collection<URI> uriCollection; 


    @Id 
    public String getId() { 
        return id; 
    } 


    public void setId(String id) { 
        this.id = id; 
    } 


    public Collection<URI> getUriCollection() { 
        return uriCollection; 
    } 


    public void setUriCollection(Collection<URI> uriCollection) { 
        this.uriCollection = uriCollection; 
    } 

}
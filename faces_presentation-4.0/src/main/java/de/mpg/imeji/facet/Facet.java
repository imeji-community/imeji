package de.mpg.imeji.facet;

import java.net.URI;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;

public class Facet
{
    private URI uri;
    private String label;
    private int count;

    public Facet(URI uri,String label, int count)
    {
        this.count = count;
        this.label = label;
        this.uri = uri;
    }

    public URI getUri()
    {
        return uri;
    }

    public void setUri(URI uri)
    {
        this.uri = uri;
    }
    
    public String getinternationalizedLabel()
    {
    	String s = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("facet_" + label.toLowerCase());
    	
    	if (s.equals("facet_" + label.toLowerCase()))
    	{
    		return label;
    	}
    	
    	return s;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }
}

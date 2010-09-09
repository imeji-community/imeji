package de.mpg.imeji.facet;

import java.net.URI;

public class FacetBean
{
    private URI uri;
    private String label;
    private int count;

    public FacetBean(URI uri, String label, int count)
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

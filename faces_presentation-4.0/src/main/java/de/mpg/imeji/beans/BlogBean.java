package de.mpg.imeji.beans;

import de.mpg.escidoc.services.framework.PropertyReader;

public class BlogBean
{
    private String rssFeedUrl = null;
    private String blogUrl = null;
    private String googleKey = null;
    
    public BlogBean()
    {
        
    }

    public String getRssFeedUrl()
    {
        try
        {
            rssFeedUrl = PropertyReader.getProperty("escidoc.faces.blog.rss.url");
        }
        catch (Exception e)
        {
           throw new RuntimeException("Error reading property  ", e);
        }
        return rssFeedUrl;
    }

    public void setRssFeedUrl(String rssFeedUrl)
    {
       
        this.rssFeedUrl = rssFeedUrl;
    }

    public String getBlogUrl()
    {
        try
        {
            blogUrl = PropertyReader.getProperty("escidoc.faces.blog.url");
        }
        catch (Exception e)
        {
           throw new RuntimeException("Error reading property ", e);
        }
        return blogUrl;
    }

    public void setBlogUrl(String blogUrl)
    {
        this.blogUrl = blogUrl;
    }

    public String getGoogleKey()
    {
        try
        {
            googleKey = PropertyReader.getProperty("escidoc.faces.blog.rss.google.key");

        }
        catch (Exception e)
        {
           throw new RuntimeException("Error reading property  ", e);
        }
        return googleKey;
    }

    public void setGoogleKey(String googleKey)
    {
        this.googleKey = googleKey;
    }
    
    
    
}

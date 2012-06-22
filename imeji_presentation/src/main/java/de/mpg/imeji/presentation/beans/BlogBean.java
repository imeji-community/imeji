/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.beans;

import de.mpg.imeji.presentation.util.PropertyReader;


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
            rssFeedUrl = PropertyReader.getProperty("escidoc.imeji.blog.rss.url");
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
            blogUrl = PropertyReader.getProperty("escidoc.imeji.blog.url");
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
            googleKey = PropertyReader.getProperty("escidoc.imeji.blog.rss.google.key");

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

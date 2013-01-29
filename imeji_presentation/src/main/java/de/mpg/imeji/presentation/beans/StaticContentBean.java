/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.beans;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * JavaBean to read static content externally stored and to display it in imeji
 * 
 * @author saquet
 */
public class StaticContentBean
{
    private boolean about = true;
    private boolean legal = true;
    private boolean blog = true;

    /**
     * Construct the {@link StaticContentBean} by reading in the imeji.properties which external content are defined
     * 
     * @throws IOException
     * @throws URISyntaxException
     */
    public StaticContentBean() throws IOException, URISyntaxException
    {
        if ("".equals(PropertyReader.getProperty("escidoc.imeji.about.url")))
        {
            about = false;
        }
        if ("".equals(PropertyReader.getProperty("escidoc.imeji.legal.url")))
        {
            legal = false;
        }
        if ("".equals(PropertyReader.getProperty("escidoc.imeji.blog.url")))
        {
            blog = false;
        }
    }

    /**
     * Read the URL of the logo from the imeji.preporties and return an CSS snippet
     * 
     * @return
     */
    public String getHeaderLogo()
    {
        try
        {
            String html = "background-image: url( " + PropertyReader.getProperty("escidoc.imeji.logo.url") + ");";
            return html;
        }
        catch (Exception e)
        {
            return " ";
        }
    }

    /**
     * Read the link to use hover the logo from the imeji.propertis.
     * 
     * @return
     */
    public String getLogoLink()
    {
        try
        {
            return PropertyReader.getProperty("escidoc.imeji.logo.link.url");
        }
        catch (Exception e)
        {
            return "#";
        }
    }

    /**
     * Get the HTML content of the Help page. URL of the Help page is defined in properties.
     * 
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public String getHelpContent() throws IOException, URISyntaxException
    {
        String html = "";
        try
        {
            String helpProp = PropertyReader.getProperty("escidoc.imeji.help.url");
            html = getContent(new URL(helpProp));
            System.out.println(html);
        }
        catch (Exception e)
        {
            html = PropertyReader.getProperty("escidoc.imeji.help.url")
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

    /**
     * Get the HTML content of the Home page. URL of the Home page is defined in properties.
     * 
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public String getHomeContent() throws IOException, URISyntaxException
    {
        String html = "";
        try
        {
            html = getContent(new URL(PropertyReader.getProperty("escidoc.imeji.home.url")));
        }
        catch (Exception e)
        {
            html = PropertyReader.getProperty("escidoc.imeji.help.url")
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

    /**
     * Get the HTML content of the About page. URL of the About page is defined in properties.
     * 
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public String getAboutContent() throws IOException, URISyntaxException
    {
        String html = "";
        try
        {
            html = getContent(new URL(PropertyReader.getProperty("escidoc.imeji.about.url")));
        }
        catch (Exception e)
        {
            html = PropertyReader.getProperty("escidoc.imeji.help.url")
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

    /**
     * Get the HTML content of the Legal page. URL of the Legal page is defined in properties.
     * 
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    public String getLegalContent() throws IOException, URISyntaxException
    {
        String html = "";
        try
        {
            html = getContent(new URL(PropertyReader.getProperty("escidoc.imeji.legal.url")));
        }
        catch (Exception e)
        {
            html = PropertyReader.getProperty("escidoc.imeji.help.url")
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

    public String getConfirmationContent() throws IOException, URISyntaxException
    {
        String html = "";
        try
        {
            html = getContent(new URL(PropertyReader.getProperty("escidoc.imeji.confirmation.url")));
        }
        catch (Exception e)
        {
            html = PropertyReader.getProperty("escidoc.imeji.confirmation.url")
                    + " couldn't be loaded. Url might be either wrong or protected." + "<br/><br/>" + "Error message:"
                    + "<br/><br/>" + e.toString();
        }
        return html;
    }

    /**
     * Get the html content of an {@link URL}
     * 
     * @param url
     * @return
     * @throws Exception
     */
    private String getContent(URL url) throws Exception
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String inputLine = "";
        String content = "";
        while (inputLine != null)
        {
            inputLine = in.readLine();
            if (inputLine != null)
            {
                content += inputLine + "  ";
            }
        }
        in.close();
        return content;
    }

    public boolean isAbout()
    {
        return about;
    }

    public void setAbout(boolean about)
    {
        this.about = about;
    }

    public boolean isLegal()
    {
        return legal;
    }

    public void setLegal(boolean legal)
    {
        this.legal = legal;
    }

    public boolean isBlog()
    {
        return blog;
    }

    public void setBlog(boolean blog)
    {
        this.blog = blog;
    }
}

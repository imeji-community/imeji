/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.beans;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;

import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.PropertyReader;
import de.mpg.imeji.util.UrlHelper;

public class Navigation implements Serializable
{
    public final String LOGIN_URL = "/aa/login?target=$1";
    public final String LOGOUT_URL = "/aa/logout?target=$1";
    public final String USERHANDLE_PARAMETER_NAME = "eSciDocUserHandle";
    // Url of the FW
    public final String frameworkUrl;
    // Url of the application
    public final String applicationUrl;
    // Pages of Imeji
    public final Page HOME_PAGE = new Page("HomePage", "pictures");
    public final Page ABOUT = new Page("About", "about");
    public final Page LEGAL = new Page("Legal", "legal");
    public final Page SEARCH = new Page("Search", "search");
    public final Page DETAIL = new Page("Details", "details");
    public final Page COMPARISON = new Page("Comparison", "comparison");
    public final Page HELP = new Page("Help", "help");
    public final Page MYALBUMS = new Page("MyAlbums", "myalbums");
    public final Page PUBLISHEDALBUM = new Page("publishedalbums", "publishedalbums");
    public final Page CREATEALBUM = new Page("CreateAlbum", "album/new");
    public final Page ALBUM = new Page("album", "album");
    public final Page EXPORT = new Page("export", "export");
    public final Page CONFIRMATION = new Page("confirmation", "confirmation");
    public final Page EDITALBUM = new Page("edit", "edit");
    public final Page SEARCHRESULT = new Page("searchresult", "search/result");
    public final Page LOGOFF = new Page("logoff", "logoff");
    public final Page STATISTICS = new Page("statistics", "statistics");
    public final Page PERSON = new Page("person", "person");
    public final Page ALBUM_INTERFACE = new Page("Album Interface", "do");
    public final Page IMAGES = new Page("Images", "images");
    private SessionBean sessionBean = null;

    public class Page implements Serializable
    {
        private String name;
        private String file;

        public Page(String name, String file)
        {
            super();
            this.name = name;
            this.file = file;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getFile()
        {
            return file;
        }

        public void setFile(String file)
        {
            this.file = file;
        }
    }

    public Navigation() throws Exception
    {
        frameworkUrl = PropertyReader.getProperty("escidoc.framework_access.framework.ur");
        applicationUrl = PropertyReader.getProperty("escidoc.imeji.instance.url");
    }

    public String getDomain()
    {
    	return applicationUrl.replaceAll("imeji/", "");
    }
    
    public String getAboutUrl()
    {
        return applicationUrl + ABOUT.getFile();
    }

    public String getHomePageUrl()
    {
        return applicationUrl + HOME_PAGE.getFile();
    }

    public String getComparisonUrl()
    {
        return applicationUrl + COMPARISON.getFile();
    }

    public String getLegalUrl()
    {
        return applicationUrl + LEGAL.getFile();
    }

    public String getSearchUrl()
    {
        return applicationUrl + SEARCH.getFile();
    }

    public String getDetailUrl()
    {
        return applicationUrl + DETAIL.getFile();
    }

    public String getHelpUrl()
    {
        return applicationUrl + HELP.getFile() + getContext();
    }

    public String getApplicationUrl()
    {
        return applicationUrl;
    }
    
    public String getApplicationUri()
    {
        return applicationUrl.substring(0, applicationUrl.length() - 1);
    }

    public String getMyAlbumsUrl()
    {
        return applicationUrl + MYALBUMS.getFile();
    }

    public String getPublishedAlbums()
    {
        return applicationUrl + PUBLISHEDALBUM.getFile();
    }

    public String getAlbumsUrl()
    {
        return applicationUrl + "albums";
    }

    public String getCollectionsUrl()
    {
        return applicationUrl + "collections";
    }

    public String getCollectionUrl()
    {
        return applicationUrl + "collection";
    }

    public String getPublicationsUrl()
    {
        return applicationUrl + "publications";
    }

    public String getAlbumsSearchUrl()
    {
        return applicationUrl + "albums/search";
    }

    public String getCreateAlbumUrl()
    {
        return applicationUrl + CREATEALBUM.getFile();
    }

    public String getAlbumUrl()
    {
        return applicationUrl + ALBUM.getFile();
    }

    public String getExportUrl()
    {
        return applicationUrl + EXPORT.getFile();
    }

    public String getConfirmationUrl()
    {
        return applicationUrl + CONFIRMATION.getFile();
    }

    public String getEditAlbumUrl()
    {
        return applicationUrl + EDITALBUM.getFile();
    }

    public String getSearchResultUrl()
    {
        return applicationUrl + SEARCHRESULT.getFile();
    }

    public String getPersonUrl()
    {
        return applicationUrl + PERSON.getFile();
    }

    public String getLogOffUrl()
    {
        return applicationUrl + LOGOFF.getFile();
    }

    public String getStatisticsUrl()
    {
        return applicationUrl + STATISTICS.getFile();
    }

    public String getBlogUrl() throws IOException, URISyntaxException
    {
        return PropertyReader.getProperty("escidoc.imeji.blog.url");
    }

    public String getImpressumUrl() throws IOException, URISyntaxException
    {
        return PropertyReader.getProperty("escidoc.imeji.impressum.url");
    }

    public String getAlbumInterfaceUrl()
    {
        return applicationUrl + ALBUM_INTERFACE.getFile();
    }
    
    public String getImagesUrl()
    {
        return applicationUrl + IMAGES.getFile();
    }

    /**
     * Get the context for the context sensitive search.
     * 
     * @return
     */
    public String getContext()
    {
        String context = "#";
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if ("help".equals(sessionBean.getCurrentPage()))
        {
            context += "";
        }
        if ("welcome".equals(sessionBean.getCurrentPage()) || "about".equals(sessionBean.getCurrentPage())
                || "legal".equals(sessionBean.getCurrentPage()))
        {
            context += "1._Home";
        }
        if ("home".equals(sessionBean.getCurrentPage()))
        {
            context += "2._Pictures";
        }
        if ("search".equals(sessionBean.getCurrentPage()) || "searchResult".equals(sessionBean.getCurrentPage()))
        {
            context += "4.1_Advanced_Search";
        }
        if ("albumssearch".equals(sessionBean.getCurrentPage()))
        {
            context += "4.2_Public_Album_Search";
        }
        if ("details".equals(sessionBean.getCurrentPage()) || "comparison".equals(sessionBean.getCurrentPage())
                || "detailsFromAlbum".equals(sessionBean.getCurrentPage())
                || "comparisonFromAlbum".equals(sessionBean.getCurrentPage())
                || "person".equals(sessionBean.getCurrentPage()))
        {
            context += "2.2_Picture_View";
        }
        if ("albums".equals(sessionBean.getCurrentPage()) || "createalbum".equals(sessionBean.getCurrentPage())
                || "editalbum".equals(sessionBean.getCurrentPage()))
        {
            context += "3._Album";
        }
        if ("viewAlbum".equals(sessionBean.getCurrentPage()))
        {
            context += "3.2_Album_View";
        }
        if (("confirmation".equals(sessionBean.getCurrentPage()) && "delete".equals(UrlHelper.getParameterValue("action"))))
        {
            context += "Delete_album";
        }
        if (("confirmation".equals(sessionBean.getCurrentPage()) && "publish".equals(UrlHelper.getParameterValue("action"))))
        {
            context += "Publish_album";
        }
        if (("confirmation".equals(sessionBean.getCurrentPage()) && "withdraw".equals(UrlHelper.getParameterValue("action"))))
        {
            context += "Withdraw_album";
        }
        if ("export".equals(sessionBean.getCurrentPage())
                || ("confirmation".equals(sessionBean.getCurrentPage()) && "export".equals(UrlHelper.getParameterValue("action"))))
        {
            context += "3.3_Export";
        }
        if ("statistics".equals(sessionBean.getCurrentPage()))
        {
            context += "5._Usage_Statistics";
        }
        return context;
    }
}

/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.beans;

import java.io.IOException;
import java.net.URISyntaxException;

import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.imeji.presentation.util.UrlHelper;

/**
 * Defines the page names and Path for imeji. All changes here must be synchronized with WEB-INF/pretty-config.xml The
 * Pages are used by the History
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Navigation
{
    // Url of the FW
    public final String frameworkUrl;
    // Url of the application
    public final String applicationUrl;
    // Pages of imeji
    public final Page HOME = new Page("HomePage", "");
    public final Page SEARCH = new Page("Search", "search");
    public final Page HELP = new Page("Help", "help");
    public final Page BROWSE = new Page("Browse", "browse");
    public final Page ITEM = new Page("Item", "item");
    public final Page COLLECTION = new Page("collection", "collection");
    public final Page ALBUM = new Page("album", "album");
    public final Page PROFILE = new Page("Profile", "profile");
    public final Page ALBUMS = new Page("albums", "albums");
    public final Page COLLECTIONS = new Page("Collections", "collections");
    public final Page EXPORT = new Page("export", "export");
    public final Page EDIT = new Page("Edit", "edit");
    public final Page INFOS = new Page("Info", "infos");
    public final Page CREATE = new Page("Create", "create");
    public final Page UPLOAD = new Page("Upload collection", "upload");
    // session
    private SessionBean sessionBean = null;

    /**
     * Application bean managing navigation
     * @throws Exception
     */
    public Navigation() throws Exception
    {
        frameworkUrl = PropertyReader.getProperty("escidoc.framework_access.framework.ur");
        applicationUrl = PropertyReader.getProperty("escidoc.imeji.instance.url");
    }

    public String getApplicationUrl()
    {
        return applicationUrl;
    }

    public String getApplicationUri()
    {
        return applicationUrl.substring(0, applicationUrl.length() - 1);
    }

    public String getDomain()
    {
        return applicationUrl.replaceAll("imeji/", "");
    }

    public String getHomeUrl()
    {
        return applicationUrl + HOME.getPath();
    }

    public String getBrowseUrl()
    {
        return applicationUrl + BROWSE.getPath();
    }

    public String getItemUrl()
    {
        return applicationUrl + ITEM.getPath() + "/";
    }

    public String getCollectionUrl()
    {
        return applicationUrl + COLLECTION.getPath() + "/";
    }

    public String getAlbumUrl()
    {
        return applicationUrl + ALBUM.getPath() + "/";
    }

    public String getProfileUrl()
    {
        return applicationUrl + PROFILE.getPath() + "/";
    }

    public String getAlbumsUrl()
    {
        return applicationUrl + ALBUMS.getPath();
    }

    public String getCollectionsUrl()
    {
        return applicationUrl + COLLECTIONS.getPath();
    }

    public String getCreateCollectionUrl()
    {
        return applicationUrl + CREATE.getPath() + COLLECTION.getPath();
    }

    public String getCreateAlbumUrl()
    {
        return applicationUrl + CREATE.getPath() + ALBUM.getPath();
    }

    public String getSearchUrl()
    {
        return applicationUrl + SEARCH.getPath();
    }

    public String getHelpUrl()
    {
        return applicationUrl + HELP.getPath() + getContext();
    }

    public String getExportUrl()
    {
        return applicationUrl + EXPORT.getPath();
    }

    public String getBlogUrl() throws IOException, URISyntaxException
    {
        return PropertyReader.getProperty("escidoc.imeji.blog.url");
    }

    /*
     * Paths
     */
    public String getCollectionPath()
    {
        return COLLECTION.path;
    }

    public String getInfosPath()
    {
        return INFOS.path;
    }

    public String getBrowsePath()
    {
        return BROWSE.path;
    }

    public String getEditPath()
    {
        return EDIT.path;
    }

    public String getItemPath()
    {
        return ITEM.path;
    }

    public String getUploadPath()
    {
        return UPLOAD.path;
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
        if (("confirmation".equals(sessionBean.getCurrentPage()) && "delete".equals(UrlHelper
                .getParameterValue("action"))))
        {
            context += "Delete_album";
        }
        if (("confirmation".equals(sessionBean.getCurrentPage()) && "publish".equals(UrlHelper
                .getParameterValue("action"))))
        {
            context += "Publish_album";
        }
        if (("confirmation".equals(sessionBean.getCurrentPage()) && "withdraw".equals(UrlHelper
                .getParameterValue("action"))))
        {
            context += "Withdraw_album";
        }
        if ("export".equals(sessionBean.getCurrentPage())
                || ("confirmation".equals(sessionBean.getCurrentPage()) && "export".equals(UrlHelper
                        .getParameterValue("action"))))
        {
            context += "3.3_Export";
        }
        if ("statistics".equals(sessionBean.getCurrentPage()))
        {
            context += "5._Usage_Statistics";
        }
        return context;
    }

    /**
     * An html page
     * 
     * @author saquet (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    public class Page
    {
        private String name;
        private String path;

        public Page(String name, String file)
        {
            this.name = name;
            this.path = file;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getPath()
        {
            return path;
        }

        public void setPath(String file)
        {
            this.path = file;
        }
    }
}

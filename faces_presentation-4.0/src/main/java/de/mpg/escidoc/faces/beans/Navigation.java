/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.faces.beans;



import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import de.mpg.escidoc.faces.beans.SessionBean.pageContextEnum;
import de.mpg.escidoc.faces.container.album.AlbumSession;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.UrlHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class Navigation
{
    
    public final String LOGIN_URL = "/aa/login?target=$1";
    public final String LOGOUT_URL = "/aa/logout?target=$1";
    
    public final String USERHANDLE_PARAMETER_NAME = "eSciDocUserHandle";
    
    // Url of the FW
    public final String frameworkUrl;
    // Url of the application
    public final String applicationUrl;
    
    // Pages of Faces
    public final Page HOME_PAGE = new Page("HomePage", "pictures");
    public final Page ABOUT = new Page("About", "about");
    public final Page LEGAL = new Page("Legal", "legal");
    public final Page SEARCH = new Page("Search", "search");
    public final Page DETAIL = new Page("Details", "details");
    public final Page COMPARISON = new Page("Comparison", "comparison");
    public final Page HELP = new Page("Help", "help");
    public final Page MYALBUMS = new Page("MyAlbums","myalbums");
    public final Page PUBLISHEDALBUM = new Page("publishedalbums", "publishedalbums");
    public final Page CREATEALBUM = new Page("CreateAlbum", "album/new");
    public final Page ALBUM = new Page("album", "album");
    public final Page EXPORT = new Page("export", "export");
    public final Page CONFIRMATION = new Page("confirmation" , "confirmation");
    public final Page EDITALBUM  = new Page("edit", "edit");
    public final Page SEARCHRESULT = new Page("searchresult","search/result");
    public final Page LOGOFF = new Page("logoff","logoff");
    public final Page STATISTICS = new Page("statistics","statistics");
    public final Page PERSON = new Page("person", "person");
    public final Page ALBUM_INTERFACE = new Page("Album Interface","do");
    
    private SessionBean sessionBean = null;
    private UrlHelper urlHelper = null;
    private AlbumSession albumSession = null;
       
    public class Page
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
        frameworkUrl = ServiceLocator.getFrameworkUrl();
        applicationUrl = PropertyReader.getProperty("escidoc.faces.instance.url");
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
        return PropertyReader.getProperty("escidoc.faces.blog.url");
    }
    
    public String getImpressumUrl() throws IOException, URISyntaxException
    {
        return PropertyReader.getProperty("escidoc.faces.impressum.url");
    }
    
    public String getAlbumInterfaceUrl()
    {
        return applicationUrl + ALBUM_INTERFACE.getFile();
    }
   
    /**
     * Manage the URL's of Faces and store the address for the backToResultList features
     * @param page : the page number
     * @param itemsPerPage : the number of items per page
     * @param sort : the list of sorting criteria 
     * @param order : the list of order criteria
     * @param selection : the id of the current album
     * @param album : the id of the view album
     * @return : the complete url
     * @throws Exception
     */
    public String createURL(String page, String itemsPerPage, List<String> sort, List<String> order, String selection, String album) throws Exception
    {
        sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        urlHelper = (UrlHelper) BeanHelper.getRequestBean(UrlHelper.class);
        // Write the address of the application (for example: http://localhost:8080/faces) 
        String URL = applicationUrl;
        // Set to true when the url is valid
        boolean validUrl = false;
        
        
        // Initialization of the current Url
        if (sessionBean.getCurrentUrl() == null)
        {
            sessionBean.setCurrentUrl("home");
            sessionBean.setLastBrowsing("home");
            validUrl = true;
        }
        if (sessionBean.getCurrentUrl() != null)
        {
            if ((sessionBean.getCurrentUrl().equals("home") || (album != null && album.equals("home"))))
            {
                URL += "pictures";
                sessionBean.setLastBrowsing("home");
                validUrl = true;
            }
            if (sessionBean.getCurrentUrl().equals("viewAlbum") 
                    || sessionBean.getCurrentUrl().equals("detailsFromAlbum") 
                    || sessionBean.getCurrentUrl().equals("comparisonFromAlbum"))
            {
                if (album != null && !album.equals("home") && urlHelper.getResource() == null)
                {
                    URL += "album/" + album ;
                    sessionBean.setLastBrowsing("album/" + album);
                    validUrl = true;
                }
                if ( sessionBean.getCurrentUrl().equals("detailsFromAlbum"))
                {
                    URL += "/";
                }
            }
            
            if ((album == null || (album != null && !album.equals("home")))
                    && "resource".equals(sessionBean.getCurrentUrl()))
            {
                URL+= urlHelper.getResource();
                validUrl = true;
            }
            
            // Add Search/result case your are in a search result
            if (((album != null && !album.equals("home")) || album == null) 
                    && (sessionBean.getCurrentUrl().equals("searchResult") 
                            || sessionBean.getCurrentUrl().equals("searchResultInAlbum")))
            {
                URL += "search/result";
                sessionBean.setLastBrowsing("search/result");
                validUrl = true;
            }
            // Add view details information
            if (sessionBean.getCurrentUrl().equals("details") && (album == null 
                    || (album != null && !album.equals("home")))
                    || sessionBean.getCurrentUrl().equals("detailsFromAlbum") 
                    && (album == null || (album != null && !album.equals("home"))))
            {
                URL += "details/" +  sessionBean.getDetailItem().getItem().getObjid();
                validUrl = true;
            }
            // add person
            if (sessionBean.getCurrentUrl().equals("person") && (album == null || (album != null && !album.equals("home"))))
            {
                URL += "person/" + urlHelper.getPerson();
                validUrl = true;
            }
            // Add picture for comparison information
            if (sessionBean.getCurrentUrl().equals("comparison") && (album == null || (album != null && !album.equals("home"))))
            {
                URL += "comparison/" + sessionBean.getDetailItem().getItem().getObjid();
                validUrl = true;
            }
            
            // From here, all others case are implemented
            if (sessionBean.getCurrentUrl().equals("myAlbums") && (album == null || (album != null && !album.equals("home"))))
            {
                URL += "myalbums"; 
                validUrl = true;
            }
            
            if ((sessionBean.getCurrentUrl().equals("about") || sessionBean.getCurrentUrl().equals("help")
                    || sessionBean.getCurrentUrl().equals("legal") 
                    || sessionBean.getCurrentUrl().equals("publishedalbums") 
                    || sessionBean.getCurrentUrl().equals("confirmation")) && (album == null || (album != null && !album.equals("home"))))
            {
                URL += sessionBean.getCurrentUrl();
                validUrl = true;
            }
            
            if (sessionBean.getCurrentUrl().equals("createalbum") && (album == null || (album != null && !album.equals("home"))))
            {
                URL += "createalbum";
                validUrl = true;
            }
            if (sessionBean.getCurrentUrl().equals("search") && (album == null || (album != null && !album.equals("home"))))
            {
                URL += "search";
                validUrl = true;
            }
            
            // If the url is still not valid, then by default set the base url to /home
            if (!validUrl)
            {
                URL += sessionBean.getLastBrowsing();
            }
            
            // write the browsing parameters
            if (sessionBean.getCurrentUrl().equals("home")
                    || sessionBean.getCurrentUrl().equals("searchResult")
                    || sessionBean.getCurrentUrl().equals("searchResultInAlbum")
                    || sessionBean.getCurrentUrl().equals("viewAlbum")
                    || sessionBean.getCurrentUrl().equals("person")
                    || (album != null && album.equals("home")))
            {
//                if (sort.get(0) != null && order.get(0) != null && (album == null || (album != null && !album.equals("home"))))
//                {
//                    URL += "/" + page + "/" + itemsPerPage + "/" + formatSortParameter(sort.get(0)) + "/" + order.get(0);
//                }
//                // case of link to Home(Browse)
//                if (sort.get(0) != null && order.get(0) != null &&  (album != null && album.equals("home")))
//                {
//                    URL += "/1/" + itemsPerPage + "/" + formatSortParameter(sort.get(0)) + "/" + order.get(0);
//                }
//                if (sort.get(1) != null && order.get(1) != null)
//                {
//                    URL += "/" + formatSortParameter(sort.get(1)) + "/" + order.get(1);
//                }
//                if (sort.get(2) != null && order.get(2) != null)
//                {
//                    URL += "/" + formatSortParameter(sort.get(2)) + "/" + order.get(2);
//                }
//                if (sort.get(3) != null && order.get(3) != null && !(sort.get(3).equals("--")))
//                {
//                    URL += "/" + formatSortParameter(sort.get(3)) + "/" + order.get(3);
//                }   
//                
                
                if (album != null && album.equals("home"))
                {
                    page = "1";
                }
                
                URL += "/" + page + "/" + itemsPerPage;
                
                for (int i = 0; i < sort.size(); i++)
                {
                    if (i < 3)
                    {
                        URL += "/" + sort.get(i) + "/" + order.get(i);
                    }
                }
            }
        }
//        // Write the album selected
//        if (selection != null && !selection.equals("--Select an album--") 
//                && (sessionBean.getCurrentUrl().equals("home") 
//                        || sessionBean.getCurrentUrl().equals("searchResult") 
//                        || (album!= null && album.equals("home"))
//                        || sessionBean.getCurrentUrl().equals("details")
//                        || sessionBean.getCurrentUrl().equals("comparison")
//                        || sessionBean.getCurrentUrl().equals("person")))
//        {
//            URL += "/active/" + selection;
//        }
        
        if (sessionBean.getCurrentUrl().equals("searchResult") 
                && (album == null || (album != null && !album.equals("home"))))
        { 
            URL += "/" + sessionBean.getUrlQuery();
        }
        
        // Store the URL in the sessionBean (only in browsing cases)
        if (!sessionBean.getCurrentUrl().equals("comparison")
                && !sessionBean.getCurrentUrl().equals("details") 
                && !sessionBean.getCurrentUrl().equals("detailsFromAlbum")
                && !sessionBean.getCurrentUrl().equals("comparisonFromAlbum")
                && !sessionBean.getCurrentUrl().equals("person")
                && !sessionBean.getCurrentUrl().equals("confirmation")
                && (album == null || (album != null && !album.equals("home"))))
        {
            sessionBean.setBackToResultList(URL);
        }
        
        if (album == null || (album != null && !album.equals("home")))
        {
            sessionBean.setBackUrl(URL.replace("//", "/").replace("http:/", "http://"));
        }
        
        sessionBean.setFullUrl(URL.replace("//", "/").replace("http:/", "http://"));
        
        return URL.replace("//", "/").replace("http:/", "http://");
    }
    
    /**
     * Create the URL part for default browsing (i.e 1/12/sort1/order1/sort2/order2/sort3/order3)
     * @return
     */
    public String getDefaultBrowsing()
    {
        sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        String URL = "1/12";
        
        for (int i = 0; i <  sessionBean.getSortList().size(); i++)
        {
            if (i < 3)
            {
                URL += "/" +  sessionBean.getSortList().get(i) + "/" +  sessionBean.getOrderList().get(i);
            }
        }
        
        return URL;
    }
    
    /**
     * Create the URL part for default browsing (i.e 1/12/sort1/order1/sort2/order2/sort3/order3)
     * with the stored show value for the pictures context
     * @return
     */
    public String getDefaultBrowsingKeepShow()
    {
        sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        String URL = "1/" +sessionBean.getItemsPerPageBrowse() ;
        
        for (int i = 0; i <  sessionBean.getSortList().size(); i++)
        {
            if (i < 3)
            {
                URL += "/" +  sessionBean.getSortList().get(i) + "/" +  sessionBean.getOrderList().get(i);
            }
        }
        
        return URL;
    }
    
    /**
     * Create the URL part for default browsing (i.e 1/12/sort1/order1/sort2/order2/sort3/order3)
     * with the stored show value for the album context
     * @return
     */
    public String getDefaultBrowsingKeepShowAlbum()
    {
        sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        String URL = "1/" + sessionBean.getItemsPerPageAlbum();
        
        for (int i = 0; i <  sessionBean.getSortList().size(); i++)
        {
            if (i < 3)
            {
                URL += "/" +  sessionBean.getSortList().get(i) + "/" +  sessionBean.getOrderList().get(i);
            }
        }
        
        return URL;
    }
    
    /**
     * Get the context for the context sensitive search.
     * @return
     */
    public String getContext()
    {
        String context = "#";
        sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        
        if ("help".equals(sessionBean.getCurrentUrl()))
        {
            context += "";
        }
        if ("welcome".equals(sessionBean.getCurrentUrl()) 
        		||"about".equals(sessionBean.getCurrentUrl())
				|| "legal".equals(sessionBean.getCurrentUrl()))
        {
            context += "1._Home";
        }
        if ("home".equals(sessionBean.getCurrentUrl()))
        {
            context += "2._Pictures";
        }
        if ("search".equals(sessionBean.getCurrentUrl())
                || "searchResult".equals(sessionBean.getCurrentUrl()))
        {
            context += "4.1_Advanced_Search";
        }        
        if ("albumssearch".equals(sessionBean.getCurrentUrl()))
        {
            context += "4.2_Public_Album_Search";
        }
        if ("details".equals(sessionBean.getCurrentUrl())
                || "comparison".equals(sessionBean.getCurrentUrl())
                || "detailsFromAlbum".equals(sessionBean.getCurrentUrl())
                || "comparisonFromAlbum".equals(sessionBean.getCurrentUrl())
                || "person".equals(sessionBean.getCurrentUrl()))
        {
            context += "2.2_Picture_View";
        }
        if ("albums".equals(sessionBean.getCurrentUrl())
        		|| "createalbum".equals(sessionBean.getCurrentUrl())
        		|| "editalbum".equals(sessionBean.getCurrentUrl()))
        {
            context += "3._Album";
        }
        if ("viewAlbum".equals(sessionBean.getCurrentUrl()))
        {
            context += "3.2_Album_View";
        }
        if (("confirmation".equals(sessionBean.getCurrentUrl()) 
                && "delete".equals(sessionBean.getAction())))
		{
		    context += "Delete_album";
		}
        if (("confirmation".equals(sessionBean.getCurrentUrl()) 
                        && "publish".equals(sessionBean.getAction())))
        {
            context += "Publish_album";
        }
        if (("confirmation".equals(sessionBean.getCurrentUrl()) 
                && "withdraw".equals(sessionBean.getAction())))
        {
            context += "Withdraw_album";
        }
        if ("export".equals(sessionBean.getCurrentUrl())
                || ("confirmation".equals(sessionBean.getCurrentUrl()) 
                        && "export".equals(sessionBean.getAction())))
        {
            context += "3.3_Export";
        }
        if ("statistics".equals(sessionBean.getCurrentUrl()))
        {
            context += "5._Usage_Statistics";
        }
        
        return context;
    }
    
    /**
     * Generate the url for the link Browse.
     * Initialize to the first page but with the sorting parameter kept.
     * @return the url 
     * @throws Exception
     */
    public String getBrowseUrl() throws Exception 
    { 
        albumSession = (AlbumSession)BeanHelper.getSessionBean(AlbumSession.class);
        if (albumSession.getActive().getVersion().getObjectId() == null)
        {
            return createURL(Integer.toString(1),  sessionBean.getItemsPerPageBrowse(), sessionBean.getSortList()
                    , sessionBean.getOrderList(), null, "home");
        }
        else
        {
            return createURL(Integer.toString(1),  sessionBean.getItemsPerPageBrowse(), sessionBean.getSortList()
                    , sessionBean.getOrderList(), albumSession.getActive().getVersion().getObjectId(), "home");
        }      
    }
    
    /**
     * Generate the currentUrl according to the parameters read in the url
     * @return the currentUrl
     */
    public String getCurrentUrl()
    {
        sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        urlHelper = (UrlHelper) BeanHelper.getRequestBean(UrlHelper.class);
        // Initialize the url with url of the application
        String URL = applicationUrl;
        
        if (urlHelper.getCurrentUrl() != null)
        {
            if (urlHelper.getCurrentUrl().equals("home"))
            {
                URL += "pictures";
            }
            if (("viewAlbum".equals(urlHelper.getCurrentUrl()) 
                    || "detailsFromAlbum".equals(urlHelper.getCurrentUrl()) 
                    || "comparisonFromAlbum".equals(urlHelper.getCurrentUrl()))
                    && urlHelper.getAlbumId() != null)
            {
                URL += "album/" + urlHelper.getAlbumId();
                sessionBean.setLastBrowsing("album/" + urlHelper.getAlbumId());
            }
            // Add Search/result case your are in a search result
            if ("searchResult".equals(urlHelper.getCurrentUrl())
                            || urlHelper.getCurrentUrl().equals("searchResultInAlbum"))
            {
                URL += "search/result";
            }
            // Add view details information
            if ("details".equals(urlHelper.getCurrentUrl()))
            {
                URL += "details/" + sessionBean.getDetailItem().getItem().getObjid();
            }
            // add person
            if ("person".equals(urlHelper.getCurrentUrl()))
            {
                URL += "person/" + urlHelper.getPerson();
                // test if the person has been found : useful when non logged user try to see a private person
//                if (sessionBean.getDetailItem().getItem() != null)
//                {
//                    URL += "person/" + sessionBean.getDetailItem().getPersonId();
//                }
            }
            // Add picture for comparison information
            if ("comparison".equals(urlHelper.getCurrentUrl()))
            {
                URL += "comparison/" + sessionBean.getDetailItem().getItem().getObjid();
            }
            
            if ("about".equals(urlHelper.getCurrentUrl())
                    || "help".equals(urlHelper.getCurrentUrl())
                    || "legal".equals(urlHelper.getCurrentUrl()) 
                    || "publishedalbums".equals(urlHelper.getCurrentUrl())
                    || "confirmation".equals(urlHelper.getCurrentUrl())
                    || "myAlbums".equals(urlHelper.getCurrentUrl())
                    || "createalbum".equals(urlHelper.getCurrentUrl())
                    || "search".equals(urlHelper.getCurrentUrl()))
            {
                URL += urlHelper.getCurrentUrl().toLowerCase();
            }
            
            // write the browsing parameters in case allowed
            if ("home".equals(urlHelper.getCurrentUrl())
                    || "searchResult".equals(urlHelper.getCurrentUrl())
                    || "searchResultInAlbum".equals(urlHelper.getCurrentUrl())
                    || "viewAlbum".equals(urlHelper.getCurrentUrl())
                    || "person".equals(urlHelper.getCurrentUrl()))
            {                
                if (sessionBean.getPageContext().equals(pageContextEnum.browsePage.toString()))
                {
                    URL += "/" + urlHelper.getPage() + "/" + sessionBean.getItemsPerPageBrowse();
                }
                if (sessionBean.getPageContext().equals(pageContextEnum.albumPage.toString()))
                {
                    URL += "/" + urlHelper.getPage() + "/" + sessionBean.getItemsPerPageAlbum();
                }
                
                if (urlHelper.getSortingUrl() != null)
                {
                    URL += "/" + urlHelper.getSortingUrl();
                }
            }
            
            // Write the album selected in cases allowed
            if (urlHelper.getSelection() != null 
                    && ("home".equals(urlHelper.getCurrentUrl()) 
                            || "searchResult".equals(urlHelper.getCurrentUrl()) 
                            || "details".equals(urlHelper.getCurrentUrl())
                            || "comparison".equals(urlHelper.getCurrentUrl())))
            {
                //TODO To be removed
                //URL += "/currentalbum/" + urlHelper.getSelection();
            }
            
            if (sessionBean.getCurrentUrl().equals("searchResult"))
            { 
                URL += "/" + urlHelper.getQuery();
            }
        }
        return URL;
    }
     
    public String getActiveAlbumUrl()
    {
        if (sessionBean.getUser() != null)
        {
            albumSession = (AlbumSession)BeanHelper.getSessionBean(AlbumSession.class);
            
            if (albumSession.getActive().getVersion().getObjectId() != null)
            {
                return "/active/" + albumSession.getActive().getVersion().getObjectId();
            }
        }
        
        return "";
    }
    
    /**
     * Generate the url to the login page with the redirect to the current page
     * @return url to login with redirect
     */
    public String getLoginUrl()
    {
        String backUrl = null;
        backUrl =  getCurrentUrl();
        return frameworkUrl + LOGIN_URL.replace("$1", backUrl);
    }
    
    /**
     * Generate the url to the logout page with the redirect to the current page
     * @return url to logout with redirect
     */
    public String getLogoutUrl()
    {
        String backUrl = null;
        //backUrl =  getApplicationUrl() + "logout";
        backUrl = getApplicationUrl();
        return frameworkUrl +  LOGOUT_URL.replace("$1", backUrl) + "?action=logout";
    }
}

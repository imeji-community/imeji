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
* Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.faces.beans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.faces.beans.SessionBean.pageContextEnum;
import de.mpg.escidoc.faces.container.album.AlbumController;
import de.mpg.escidoc.faces.container.album.AlbumSession;
import de.mpg.escidoc.faces.container.album.AlbumVO;
import de.mpg.escidoc.faces.item.ItemVO;
import de.mpg.escidoc.faces.pictures.Browse;
import de.mpg.escidoc.faces.pictures.BrowseSession;
import de.mpg.escidoc.faces.search.Search;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.QueryHelper;
import de.mpg.escidoc.faces.util.UrlHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class HomePage
{
    private static Logger logger = Logger.getLogger(HomePage.class);
    private SessionBean sessionBean = null;
    private Navigation navigation;
    private UrlHelper urlHelper = null;
    private QueryHelper queryHelper = null;
    private AlbumController albumController = new AlbumController();
    private AlbumSession albumSession = null;
    /**
     * The total number of items of the current page request.
     */
    public static int totalNumberOfItems = 0;
    /**
     * The album displayed.
     */
    private AlbumVO album = null;
    /**
     * The list of items displayed.
     */
    private List<ItemVO> items = null;
    /**
     * The search query.
     */
    private String query = null;
    /**
     * The search query displayed to the user.
     */
    private String queryDisplayed = null;
    
    private String collectionName = null;
    private boolean executeQuery = true;
    private String facesCollectionUrl = null;
    private int itemsInAlbum = 0;
    private int itemsOnPage = 0;
    private String resolveAlbum = "";
    private List<SelectItem> multipleAddList  = null;
    private List<SelectItem> multipleDeleteList = null;
    private HttpServletRequest request = null;
    
    private BrowseSession browseSession = null;
    private Browse browse = null;
    
    /**
     * The Bean for the Pictures.jsp.
     * @throws Exception
     */
    public HomePage() throws Exception
    {   
        sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);    
        navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
        urlHelper = (UrlHelper) BeanHelper.getRequestBean(UrlHelper.class);
        queryHelper = new QueryHelper();
        albumSession = (AlbumSession)BeanHelper.getSessionBean(AlbumSession.class);
        request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        browseSession = (BrowseSession) BeanHelper.getSessionBean(BrowseSession.class);
    }
    
    public String getInit()
    {
    	try
	{
    	    initSessionBean();
    	    initBrowse();
    	    initQuery();
    	    
    	    if(executeQuery)
            {
    		this.executeQuery(query, browse.getShow(), browse.getStartRecord(), browse.getSorkeys());
            }
    	   
    	    setVariables();
	} 
    	catch (Exception e)
	{
	    throw new RuntimeException("Error initializing Browse Page: ", e);
	}
	
    	return "";
    }
    
    /**
     * Initialize the Browse of the current Page
     * @throws Exception
     */
    public void initBrowse() throws Exception
    {
	if (sessionBean.getPageContext().equals(pageContextEnum.browsePage.toString()))
        {
            browse = browseSession.getBrowsePictures();
        }
        if (sessionBean.getPageContext().equals(pageContextEnum.albumPage.toString()))
        {
            browse = browseSession.getBrowseAlbums();
        } 
    	
    	browse.parseUrl(request);
    }
    
    /**
     * Initialize the query according to browse type
     * @throws Exception
     */
    public void initQuery() throws Exception
    {
	switch (browse.getType())
	{
    	case ALBUM:
    	    initAlbumPage();
    	    break;
    	
    	case COLLECTION:
    	    initCollectionPage();
    	    break;
    	
    	case PERSON:
    	    initPersonPage();
    	    break;
    	
    	case SEARCHRESULT:
    	    initSearchResultPage();
    	    break;
    	
    	case PICTURES:
    	 
    	    break;
    	    
    	default:
    	    initResourcePage();
    	    break;
	}
    }
    
    /**
     * Set all variables which are needed for JSPF page
     * @throws Exception
     */
    public void setVariables() throws Exception
    {
	itemsInAlbum = 0;
        itemsOnPage = 0;
        
        // Calculate the number of items in album
        if (items != null)
        {
            for (int i = 0; i < items.size(); i++)
            {
                itemsOnPage ++;
                
                if (items.get(i).getInAlbum())
                {
                    itemsInAlbum ++;
                }
            }
        }
    }

    public void initCollectionPage()
    {
	
    }
    
    public void initAlbumPage()
    {
	 album = albumSession.getCurrent();
         query = queryHelper.createAlbumQuery(album);
    }
    
    public void initPersonPage()
    {
	 query = "(escidoc.face.identifier=" + urlHelper.getPerson() + "*)";
    }
    
    public void initSearchResultPage() throws Exception
    {
	Search search = new Search();
	query = search.run();
	queryDisplayed = search.getPrettyQuery();
       
        if (search.getCollectionVO() != null)
        {
            collectionName = search.getCollectionVO().getMdRecord().getTitle().getValue();
        }
        
        sessionBean.setUrlQuery(urlHelper.getQuery());
        
        if (search.getError() != null)
        {
            executeQuery = false;
            sessionBean.setUrlQuery("error");
        }
    }
    
    private void initResourcePage() throws Exception
    {
        if (urlHelper.getResource() != null)
        {
            String resourceType= retrieveResource(urlHelper.getResource());
            if ("album".equals(resourceType))
            {
                if (sessionBean.getUserHandle() != null)
                {
                    query = queryHelper.createAlbumQuery(album);
                }
                else
                {
                    query = "(escidoc.objid=000)";
                    totalNumberOfItems = 0;
                    executeQuery = false;
                }
                albumSession.setCurrent(album);
            }
            
            if ("item".equals(resourceType))
            {
                query = "escidoc.objid=" + urlHelper.getResource();
                album = new AlbumVO();
                albumSession.setCurrent(album);
                sessionBean.setMessage(null);
            }
            
            if (resourceType == null)
            {
                query = "(escidoc.objid=000)";
                totalNumberOfItems = 0;
                executeQuery = false;
                album = new AlbumVO();
                albumSession.setCurrent(album);
                sessionBean.setMessage("The resource " + urlHelper.getResource() 
                        + " was not found. Please check the ID.");
            }
        }
    }
    
    /**
     * Initialization method called from jsp.
     * @return
     * @deprecated
     */
    public String getInitOld()
    {
        try
        {
        	// Set the different values relevant for the sessionBean
            this.initSessionBean();
            
            // create the sorting query
            String sortQuery = queryHelper.createSortingQuery(
                                    urlHelper.getSort1()
                                    , urlHelper.getOrder1()
                                    , urlHelper.getSort2()
                                    , urlHelper.getOrder2()
                                    , urlHelper.getSort3()
                                    , urlHelper.getOrder3());
            
            // Initialize browse parameters

            int itemsPerPage = Integer.parseInt(this.getItemPerPage());
            
            if (urlHelper.getShow() != null) 
            {
            	itemsPerPage = Integer.parseInt(urlHelper.getShow());
            }
            
            int pageNumber = ((Integer.parseInt(urlHelper.getPage()) - 1)* Integer.parseInt(sessionBean.getItemsPerPageBrowse())) + 1;
            
            // create the query
            query = createQuery();
            
            // Execute the query with the current parameters
            if (executeQuery)
            {
                this.executeQuery(query, itemsPerPage, pageNumber, sortQuery);

            }
            
            itemsInAlbum = 0;
            itemsOnPage = 0;
            
            // Calculate the number of items in album
            if (items != null)
            {
                for (int i = 0; i < items.size(); i++)
                {
                    itemsOnPage ++;
                    
                    if (items.get(i).getInAlbum())
                    {
                        itemsInAlbum ++;
                    }
                }
            }
            
            // set the urls in Navigation according to current parameters
            navigation.createURL( urlHelper.getPage(), urlHelper.getShow(), sessionBean.getSortList(), 
                    sessionBean.getOrderList(), urlHelper.getSelection(), urlHelper.getAlbumId());
        }
        catch (Exception e)
        {
           throw new RuntimeException("Error executing query for the HomePage (Browsing feature)", e);
        }
        
        return "";
    }
    
    /**
     * Create the query used by the FW according to current values in URL+ session.
     * @return the query.
     * @throws Exception
     * @deprecated
     */
    public String createQuery() throws Exception
    {
        String currentQuery = "";
        sessionBean.setUrlQuery(null);
        
        // Create Search query for Search Result Page.
       if (urlHelper.getQuery() != null)
        {
            Search search = new Search();
            currentQuery = search.run();
            queryDisplayed = search.getPrettyQuery();
           
            if (search.getCollectionVO() != null)
            {
                collectionName = search.getCollectionVO().getMdRecord().getTitle().getValue();
            }
            
            sessionBean.setUrlQuery(urlHelper.getQuery());
            
            if (search.getError() != null)
            {
                executeQuery = false;
                sessionBean.setUrlQuery("error");
            }
        }
       
        // View all picture of this person page
        if (urlHelper.getPerson() != null)
        {
            currentQuery = "(escidoc.face.identifier=" + urlHelper.getPerson() + "*)";
        }
        
        // View album page logged in        
        if (urlHelper.getAlbumId() != null)
        {   
            album = albumSession.getCurrent();
            currentQuery = queryHelper.createAlbumQuery(album);
        }
                
        // Resource requested.
        if (urlHelper.getResource() != null)
        {
            String resourceType= retrieveResource(urlHelper.getResource());
            if ("album".equals(resourceType))
            {
                if (sessionBean.getUserHandle() != null)
                {
                    currentQuery = queryHelper.createAlbumQuery(album);
                }
                else
                {
                    currentQuery = "(escidoc.objid=000)";
                    totalNumberOfItems = 0;
                    executeQuery = false;
                }
                albumSession.setCurrent(album);
            }
            
            if ("item".equals(resourceType))
            {
                currentQuery = "escidoc.objid=" + urlHelper.getResource();
                album = new AlbumVO();
                albumSession.setCurrent(album);
                sessionBean.setMessage(null);
            }
            
            if (resourceType == null)
            {
                currentQuery = "(escidoc.objid=000)";
                totalNumberOfItems = 0;
                executeQuery = false;
                album = new AlbumVO();
                albumSession.setCurrent(album);
                sessionBean.setMessage("The resource " + urlHelper.getResource() 
                        + " was not found. Please check the ID.");
            }
        }
        
        return currentQuery;
    }
    
    /**
     * Retrieve the resource from this ID, whatever the type of the resource (item, album).
     * @return Return the type of the resource if found, otherwise null
     * @param ressourceId
     * @throws Exception 
     */
    private String retrieveResource(String resourceId) throws Exception 
    {
        album = null;
        String itemXml = null;
        boolean resourceFound = false;
        try
        {
            album = (AlbumVO) albumController.retrieve(resourceId, sessionBean.getUserHandle());
            resourceFound = true;
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        if (!resourceFound)
        {
            ItemHandler itemHandler = null;
            
            if (sessionBean.getUserHandle() == null)
            {
                itemHandler = ServiceLocator.getItemHandler();
            }
            else
            {
                itemHandler = ServiceLocator.getItemHandler(sessionBean.getUserHandle());
            }
            try
            {
                itemXml = itemHandler.retrieve(resourceId);
            }
            catch (Exception e)
            {
               itemXml = null;
            }
        }
        if (album != null)
        {
            return "album";
        }
        else if (itemXml != null)
        {
            return "item";
        }
        else
        {
            return null;
        }
    }

    /**
     * Execute the query with browsing parameters.
     * @param query
     * @param itemsPerPage
     * @param pageNumber
     * @param sortedBy
     * @throws Exception
     */
    protected void executeQuery(String query, int itemsPerPage, int pageNumber, String sortedBy) throws Exception
    {
        if (urlHelper.getCurrentUrl() == null)
        {
            sessionBean.setCurrentUrl("home");
            query = "";
        }
        else if ("set".equals(urlHelper.getAction()))
        {
            items = sessionBean.getItems();
            
            for (int i = 0; i < items.size(); i++)
            {
                items.get(i).init();
            }
        }
        else
        {
            queryHelper.executeQuery(query, itemsPerPage, pageNumber, sortedBy);
            sessionBean.setItems(queryHelper.getItems());
            items = queryHelper.getItems();
            this.totalNumberOfItems = queryHelper.getTotalNumberOfItems();
            sessionBean.setQuery(query);
            sessionBean.setPageNumber(pageNumber);
            sessionBean.setSortedBy(sortedBy);
        }
        
        sessionBean.setTotalNumberOfItems(this.totalNumberOfItems);
        Pagination paginator = new Pagination();
    }
    
    /**
     * Set all values/variable that need to be stored/modified in session. 
     */
    private void initSessionBean()
    {
        // check the action the user want to do : delete, publish, export or withdraw
        if (urlHelper.getAction() != null)
        {
            sessionBean.setAction(urlHelper.getAction());
        }
        // Each page has that value which indicate what page is being called (see WEB-INF/urlrewrite.xml for the different values)
        if (urlHelper.getCurrentUrl() != null)
        {
            sessionBean.setCurrentUrl(urlHelper.getCurrentUrl());
        }
        
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        
        if (request.getParameter("tab") != null)
        {
            sessionBean.setSelectedMenu(request.getParameter("tab").toString());
        }
    }
    
    /**
     * Resolve an album if the user is not logged in
     * @return
     * @throws NamingException
     * @throws IOException
     * @throws URISyntaxException
     */
    public String getResolveAlbum() throws NamingException, IOException, URISyntaxException
    {
        try
        {
            String resourceType = retrieveResource(urlHelper.getAlbumId());
            
            if ("album".equals(resourceType))
            {
                albumSession.setCurrent(album);
            }
            else
            {
                albumSession.setCurrent(new AlbumVO());
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            sessionBean.setMessage(sessionBean.getMessage("message_album_not_found"));
        }
        return resolveAlbum;
    }
    
    public String getAlbumDescriptionFullAsHtml()
    {
		if ( albumSession.getCurrent().getMdRecord().getAbstracts().get(0) != null)
	    {
	          
	        return  albumSession.getCurrent().getMdRecord().getAbstracts().get(0).getValue().replaceAll("\n", "<br/>");
	    }
	    else
	    {
	        return "";
    	} 
    }
    
    public void setResolveAlbum(String resolveAlbum)
    {
        this.resolveAlbum = resolveAlbum;
    }
    
    public int getTotalNumberOfItems()
    {
        return totalNumberOfItems;
    }

    public List<ItemVO> getItems() throws Exception
    {
        return items;
    }
  
    public void setItems(List<ItemVO> items)
    {
        this.items = items;
    }
    
    public String getItemPerPage()
    {
    	 if (sessionBean.getPageContext().equals(pageContextEnum.browsePage.toString()))
         {
             return sessionBean.getItemsPerPageBrowse();
         }
         if (sessionBean.getPageContext().equals(pageContextEnum.albumPage.toString()))
         {
             return sessionBean.getItemsPerPageAlbum();
         } 
         
         return "12";
    }
    
    /**
     * List of the values of the menu for adding several page in an album in one click
     * @return
     * @throws Exception 
     */
    public List<SelectItem> getMultipleAddList() throws Exception
    {
        multipleAddList = new ArrayList<SelectItem>();
        
        if (itemsInAlbum < itemsOnPage)
        {
            multipleAddList.add(new SelectItem( "page", sessionBean.getLabel("batch_add_page")));
        }
        
        multipleAddList.add(new SelectItem( "all", sessionBean.getLabel("batch_add_all")));
        
        if (multipleAddList.size() == 0)
        {
            return null;
        }
        
        return multipleAddList;
    }

    public void setMultipleAddList(List<SelectItem> multipleAddList)
    {
        this.multipleAddList = multipleAddList;
    }
    
    /**
     * List of the values for batch removal of items from an album
     * @return
     */
    public List<SelectItem> getMultipleDeleteList()
    {
        multipleDeleteList = new ArrayList<SelectItem>();
        multipleDeleteList.add(new SelectItem("page", sessionBean.getLabel("batch_remove_page")));
        multipleDeleteList.add(new SelectItem("all", sessionBean.getLabel("batch_remove_all")));
        
        return multipleDeleteList;
    }


    public void setMultipleDeleteList(List<SelectItem> multipleDeleteList)
    {
        this.multipleDeleteList = multipleDeleteList;
    }
    
    public String getQuery()
    {
        return sessionBean.getQuery();
    }
    
    public int getNumberOfPrivatePictures()
    {
    	return album.getSize() - totalNumberOfItems;
    }
    
    /**
     * Reads contents from text file and returns it as String.
     * 
     * @param fileName The name of the input file.
     * @return The entire contents of the filename as a String.
     * @throws FileNotFoundException
     */
    protected String readFile(String fileName) throws IOException, FileNotFoundException
    {
        boolean isFileNameNull = (fileName == null);
        StringBuffer fileBuffer;
        String fileString = null;
        String line;
        if (!isFileNameNull)
        {
            File file = new File(fileName);
            FileReader in = new FileReader(file);
            BufferedReader dis = new BufferedReader(in);
            fileBuffer = new StringBuffer();
            while ((line = dis.readLine()) != null)
            {
                fileBuffer.append(line + "\n");
            }
            in.close();
            fileString = fileBuffer.toString();
        }
        return fileString;
    }
    
    /**
     * Check that the search didn't generate an error and then return the user friendly query.
     * @return
     */
    public String getQueryDisplayed()
    {        
        return queryDisplayed;
    }

    public void setQueryDisplayed(String queryDisplayed)
    {
        this.queryDisplayed = queryDisplayed;
    }


    public String getCollectionName()
    {
        return collectionName;
    }


    public void setCollectionName(String collectionName)
    {
        this.collectionName = collectionName;
    }


    public String getFacesCollectionUrl() throws IOException, URISyntaxException
    {
        facesCollectionUrl = navigation.getApplicationUrl();
        String collectionId = PropertyReader.getProperty("escidoc.faces.collection.id");
        
        if (collectionId.equals("${escidoc.faces.collection.id}"))
        {
            return null;
        }
        
        return facesCollectionUrl + collectionId;
    }


    public void setFacesCollectionUrl(String facesCollectionUrl)
    {
        this.facesCollectionUrl = facesCollectionUrl;
    }


    public int getItemsInAlbum()
    {
        return itemsInAlbum;
    }


    public void setItemsInAlbum(int itemsInAlbum)
    {
        this.itemsInAlbum = itemsInAlbum;
    }


    public int getItemsOnPage()
    {
        return itemsOnPage;
    }


    public void setItemsOnPage(int itemsOnPage)
    {
        this.itemsOnPage = itemsOnPage;
    }
}

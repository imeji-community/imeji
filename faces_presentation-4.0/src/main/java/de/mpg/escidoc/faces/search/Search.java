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
* Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.faces.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.myfaces.trinidad.model.ChildPropertyTreeModel;

import de.mpg.escidoc.faces.album.AlbumVO;
import de.mpg.escidoc.faces.album.list.AlbumsListController;
import de.mpg.escidoc.faces.beans.Navigation;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.container.album.AlbumListVO;
import de.mpg.escidoc.faces.container.album.AlbumSession;
import de.mpg.escidoc.faces.container.list.FacesContainerListController;
import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.metadata.ScreenConfiguration;
import de.mpg.escidoc.faces.metadata.wrapper.MetadataWrapped;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.UrlHelper;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;

/**
 * Bean for the Faces Search Page and Search Result Page
 * @author saquet
 *
 */
public class Search
{
   /**
    *	The tree object used by Trinidad tree in a List. 
    */
    private List<ChildPropertyTreeModel> formular = null; 
    /**
     * The cql query for the search
     */
    private String cqlQuery = null;
    /**
     * The user readable query out of the cql query
     */
    private String prettyQuery = null;
    /**
     * The {@link AlbumVO} in with the search is done.
     */
    private AlbumVO collectionVO = null;
    /**
     * The list of published albums (collections) the user can search within.
     */
    private List<SelectItem> collectionMenu = null;
	/**
     * The error message of the search
     */
    private String error = null;
    
    // Class definition
    private SessionBean sessionBean = null;
    private SearchSession session = null;
    private Navigation navigation = null;
    private UrlHelper urlHelper = null;
    private ScreenConfiguration screen = null;
    private AlbumSession albumSession = null;

    /**
     * Default constructor
     */
    public Search()
    {
        // Beans initialization
        sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        session = (SearchSession) BeanHelper.getSessionBean(SearchSession.class);
        navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
        urlHelper = (UrlHelper) BeanHelper.getRequestBean(UrlHelper.class);
        albumSession = (AlbumSession)BeanHelper.getSessionBean(AlbumSession.class);
        
        // Object Initialization
        screen = new ScreenConfiguration("search");
        
        if ("search".equals(sessionBean.getCurrentUrl()))
        {
        	initializeTreeStructure();
        	initializeCollectionMenu();
        }
    }

	/**
     * Initialize the Search formular on call of the page.
     * @return
     */
    public String getInit()
    {      
        if (!"revise".equals(urlHelper.getAction()))
        {
            reset();
        }
        return "";
    }
    
    /**
     * Initialize the collection menu.
     * @throws Exception 
     * @throws Exception 
     */
    private void initializeCollectionMenu()
    {
		collectionMenu = new ArrayList<SelectItem>();
		FacesContainerListController controller =new FacesContainerListController();
		collectionMenu.add(new SelectItem("", "All images"));
		
		try 
		{
			albumSession.setPublished((AlbumListVO) controller.retrieve(albumSession.getPublished(), sessionBean.getUserHandle()));
		} 
		catch (Exception e) 
		{
			sessionBean.setMessage("Error updating list of collections");
		}
		
		for (int i = 0; i < albumSession.getPublished().getSize(); i++) 
		{
			if ( albumSession.getPublished().getList().get(i).getMdRecord().getTitle() == null) 
			{
				albumSession.getPublished().getList().get(i).getMdRecord().setTitle(new TextVO("No name"));
			}
			collectionMenu
				.add(new SelectItem(
						albumSession.getPublished().getList().get(i).getVersion().getObjectId()
						, albumSession.getPublished().getList().get(i).getMdRecord().getTitle().getValue()));
		}
	}

    /**
     * Run the search:<br/> 
     * - Parse url query<br/> 
     * - Process cql query out of parsed url query<br/> 
     * - Save search values in search session<br/> 
     * - Redirect to appropriate page<br/> 
     * - If needed, write error message in sessionBean<br/> 
     * @return the cql query
     * @throws Exception All type of exception
     */
    public String run() throws Exception
    {   
    	UrlQueryParser urlQueryParser = new UrlQueryParser(screen);
    	
    	urlQueryParser.parse(urlHelper.getQuery());
        
        CqlQueryProcessor cqlQueryProcessor = new CqlQueryProcessor(urlQueryParser);
        
        cqlQuery = cqlQueryProcessor.process();
        prettyQuery = cqlQueryProcessor.getPrettyQuery();
        collectionVO = cqlQueryProcessor.getCollectionVO();
        
        sessionBean.setQuery(cqlQuery);
        saveSearchParametersInSession(urlQueryParser.getSearchParameterMap());
        
        session.setCollectionVO(collectionVO);
        
        // redirect to search result
        if (cqlQuery != null && (sessionBean.getMessage() == null || sessionBean.isPageNotFound()))
        {
            sessionBean.setPageNotFound(false);
            return cqlQuery;
        }
        else if (sessionBean.getMessage() != null)
        {
            sessionBean.setUrlQuery("error");
            error = sessionBean.getMessage();
            FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getApplicationUrl() + "search");
        }
        else
        {
            sessionBean.setUrlQuery("error");
            sessionBean.setMessage(sessionBean.getMessage("message_search_empty"));
            error = sessionBean.getMessage();
            FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getApplicationUrl() + "search");
        }
        
        return null;
    }
    
    /**
     * Initialize The Search Formular elements:
     * - the tree.
     * - the search parameters values
     */
    private void initializeSearchFormular()
    {
    	initializeTreeStructure();
    }
    
    /**
     * Initialize the structure of the search tree.
     */
    private void initializeTreeStructure()
    {
    	formular = new ArrayList<ChildPropertyTreeModel>();
    	
    	for (int i = 0; i < screen.getParent().sizeOfStatementArray(); i++)
        {
            MetadataWrapped mdw = new MetadataWrapped(screen.getParent().getStatementArray(i));
            
            // Prepare it to use it for the search formular
            mdw = prepareSearchFormular(mdw);
            
            // Initialize the tree
            ChildPropertyTreeModel m = new ChildPropertyTreeModel(mdw, "child");
            
            // Add this tree to the list of tree.
            formular.add(m);
        }  
    }
    
    /**
     * Set the values of the GUI components of the search page.
     * The method is called on the search result page.
     * The components are initialized with the values of the search parameters.
     * @param searchParameterMap 
     */
    private void saveSearchParametersInSession(Map<String, Metadata> searchParameterMap)
    {        
        for (int i = 0; i < screen.getMdList().size(); i++)
        {
            Metadata mdToInit = searchParameterMap.get(screen.getMdList().get(i).getIndex());
            
            if (mdToInit != null)
            {
                session.saveSearchParameter(mdToInit.clone());
            }
        }
    }
    
    /**
     * Set all the id of the MetadataWrapped with the correct indexes.
     * @param mdw
     * @return
     */
    private MetadataWrapped prepareSearchFormular(MetadataWrapped mdw)
    {
        Metadata metadata = screen.getMdMap().get( mdw.getName() );
        
        if (metadata != null)
        {
              mdw.getNode().setId(metadata.getIndex());
              mdw.setIndex(metadata.getIndex());
              mdw.setGroup("title");
        }
        
        for (int i = 0; i < mdw.getChild().size(); i++)
        {
            Metadata md = screen.getMdMap().get( mdw.getChild().get(i).getName() );
           
        	mdw.getChild().get(i).getNode().setId(md.getIndex());
            
            if (mdw.getChild().get(i).hasChild())
            {
                mdw.getChild().set(i, prepareSearchFormular(mdw.getChild().get(i)));
            }
            
        }
        
        return mdw;
    }

    /**
     * Erase all values from the search form.
     * 
     * @return null.
     */
    public String reset()
    {        
        session.getCheckBox().clear();
        session.getTextfield().clear();
        session.setCollectionVO(null);
        
        for (int i = 0; i < screen.getMdList().size(); i++)
        {
            // Initialize check boxes
            Map<String, Boolean> contraints = new HashMap<String, Boolean>();
            for (int j = 0; j < screen.getMdList().get(i).getNode().sizeOfConstraintArray(); j++)
            {
                contraints.put(screen.getMdList().get(i).getNode().getConstraintArray(j).xmlText(), false);
            }
            session.getCheckBox().put(screen.getMdList().get(i).getIndex(), contraints);
            
            // Initialize text fields
            session.getTextfield().put(screen.getMdList().get(i).getIndex(), new ArrayList<String>());
            session.getTextfield().get(screen.getMdList().get(i).getIndex()).add("");
            
            session.getMdMap().clear();
        }
        
        return null;
    }
    
    /**
     * initialization of the search query.
     * Called when the user click on the link "Search"
     * @return
     * @throws IOException
     */
    public String resetAndRedirect() throws IOException
    {
        this.reset();
        FacesContext fc = FacesContext.getCurrentInstance();
        Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
        fc.getExternalContext().redirect(navigation.getSearchUrl());
        return null;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public SearchSession getSession()
    {
        return session;
    }

    public void setSession(SearchSession session)
    {
        this.session = session;
    }

    public ScreenConfiguration getScreen()
    {
        return screen;
    }

    public void setScreen(ScreenConfiguration screen)
    {
        this.screen = screen;
    }

    public List<ChildPropertyTreeModel> getFormular()
    {
        return formular;
    }

    public void setFormular(List<ChildPropertyTreeModel> formular)
    {
        this.formular = formular;
    }

    public String getPrettyQuery()
    {
        return prettyQuery;
    }

    public void setPrettyQuery(String prettyQuery)
    {
        this.prettyQuery = prettyQuery;
    }

    public AlbumVO getCollectionVO()
    {
        return collectionVO;
    }

    public void setCollectionVO(AlbumVO collectionVO)
    {
        this.collectionVO = collectionVO;
    }
    
    public List<SelectItem> getCollectionMenu() 
    {
		return collectionMenu;
	}

	public void setCollectionMenu(List<SelectItem> collectionMenu) 
	{
		this.collectionMenu = collectionMenu;
	}

    
    public String getTextFieldSize()
    {
        return this.getSession().getAddedFields()+"";        
    }

}

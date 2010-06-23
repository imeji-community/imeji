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
* Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.faces.pictures;

import javax.faces.context.FacesContext;

import de.escidoc.schemas.item.x09.ItemDocument;
import de.escidoc.schemas.item.x09.ItemDocument.Item;
import de.mpg.escidoc.faces.beans.Navigation;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.item.FacesItemVO;
import de.mpg.escidoc.faces.item.ItemVO;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.ItemHelper;
import de.mpg.escidoc.faces.util.QueryHelper;
import de.mpg.escidoc.faces.util.UrlHelper;

public class Detail
{
    private UrlHelper urlHelper = null;
    private Navigation navigation = null;
    private SessionBean sessionBean =null;
    
    /**
     * The item currently browsed in Detail view.
     */
    private ItemVO item = null;
    
    /**
     * The faces item currently browsed. Used for the alternative image 
     * and all possible specific method of the FACES Collection.
     */
    private FacesItemVO facesItem = null;
    
    /**
     * The url of the next item in the browsing list.
     */
    private String next = null;
    
    /**
     * The url of the previous item in the browsing list.
     */
    private String previous = null;
    
    /**
     * Default constructor.
     */
    public Detail()
    {
        try
        {
            urlHelper = (UrlHelper) BeanHelper.getRequestBean(UrlHelper.class);
            sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
            navigation = (Navigation) BeanHelper.getRequestBean(Navigation.class);
            
            // Update current Album
            //TODO To be remove
//            if (urlHelper.getSelection() != null)
//            {
//                sessionBean.setCurrentAlbum(albumList.retrieveOneAlbumFromFW(urlHelper.getSelection()));
//            }
//            else 
//            {
//                sessionBean.setCurrentAlbum(new FacesContainerVO(sessionBean.getLabel("activeAlbum_default")));
//            }
//            albumList.setCurrentAlbum(sessionBean.getCurrentAlbum());
            // Retrieve the item
            String itemId = urlHelper.getItem();
            String itemXml = ItemHelper.getItemXml(itemId);
            
            // Parse the item
            ItemDocument itemDoc = ItemDocument.Factory.parse(itemXml);
            
            // Create the Face item with mdRecords
            item = new ItemVO(itemDoc);
            facesItem = new FacesItemVO(itemDoc);
            
            sessionBean.setDetailItem(item);
            navigation.createURL(null, null, null, null, urlHelper.getSelection(), urlHelper.getAlbumId());
            setDetailPageNumber();
            
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error initialising Detail", e);
        }
    }
    
    public String getInit()
    {
        return "";
    }
    
    public ItemVO getItem()
    {
        return item;
    }
    
    public void setItem(ItemVO item)
    {
        this.item = item;
    }

    public String showPersonsImages() throws Exception
    {
        SessionBean sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
        
        sessionBean.setQuery("(escidoc.identifier=" + item.getMdRecords().getValue("identifier") + "*)");
        
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect(navigation.getHomePageUrl());
        
        return null;
    }
    
    private ItemVO getItem(int page) throws Exception
    {
        QueryHelper queryHelper = new QueryHelper();
        queryHelper.executeQuery(sessionBean.getQuery(),1, page, sessionBean.getSortedBy());
        return queryHelper.getFirstItem();
    }
    
    private void setDetailPageNumber() throws Exception
    {
        boolean  pageFound = false;
        if (sessionBean.getItems() == null)
        {
            QueryHelper queryHelper = new QueryHelper();
            queryHelper.executeQuery(sessionBean.getQuery(),1, 1, sessionBean.getSortedBy());
            sessionBean.setTotalNumberOfItems(queryHelper.getTotalNumberOfItems());
            sessionBean.setItems(queryHelper.getItems());
        }
        if (sessionBean.getItems() != null)
        {
            for (int i = 0; i < sessionBean.getItems().size(); i++)
            {
                if (this.item.getItem().getObjid().equals(
                        sessionBean.getItems().get(i).getItem().getObjid()))
                {
                    sessionBean.setDetailPage(i + sessionBean.getPageNumber());
                    pageFound = true;
                }
            }
            if (!pageFound)
            {
                if (sessionBean.getDetailPage() > 1 && sessionBean.getDetailPage()<sessionBean.getTotalNumberOfItems())
                {
                    if (this.getItem().getItem().getObjid().equals(
                            this.getItem(sessionBean.getDetailPage() - 1).getItem().getObjid()))
                    {
                        sessionBean.setDetailPage(sessionBean.getDetailPage() - 1);
                    }
                    else 
                    {
                        sessionBean.setDetailPage(sessionBean.getDetailPage() + 1);
                    }
                    pageFound = true;
                }
                if (sessionBean.getDetailPage() == 1 && !pageFound)
                {
                    if (this.getItem().getItem().getObjid().equals(
                            this.getItem(2).getItem().getObjid()))
                    {
                        sessionBean.setDetailPage(2);
                    }
                    else 
                    {
                        sessionBean.setDetailPage(sessionBean.getTotalNumberOfItems());
                    }
                    pageFound = true;
                }
                if (sessionBean.getDetailPage() == sessionBean.getTotalNumberOfItems() && !pageFound)
                {
                    if (this.getItem().getItem().getObjid().equals(
                            this.getItem(1).getItem().getObjid()))
                    {
                        sessionBean.setDetailPage(1);
                    }
                    else 
                    {
                        sessionBean.setDetailPage(sessionBean.getTotalNumberOfItems() - 1);
                    }
                    pageFound = true;
                }
            }
        }
        setNext(retrieveNext());
        setPrevious(retievePrevious());
        
    }
    
    public String getNext()
    {
        return next;
    }
    
    public String getPrevious()
    {
        return previous;
    }

    public void setPrevious(String previous)
    {
        this.previous = previous;
    }

    public void setNext(String next)
    {
        this.next = next;
    }
    
    public String retrieveNext() throws Exception
    {
        Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
        String urlRequest = navigation.getApplicationUrl();
        Item nextItem = null;
        
        if (urlHelper.getAlbumId() != null)
        {
            urlRequest += "album/" + urlHelper.getAlbumId() + "/";
        }
        
        urlRequest += "details";
        
        if (sessionBean.getItems() != null)
        {
            for (int i = 0; i < sessionBean.getItems().size(); i++)
            {
                if (this.item.getItem().getObjid().equals(
                        sessionBean.getItems().get(i).getItem().getObjid()))
                {
                    if (i < sessionBean.getItems().size() - 1)
                    {
                        nextItem = sessionBean.getItems().get(i+1).getItem();  
                    }
                }
            }
        }
        
        if (nextItem == null)
        {
            if (sessionBean.getDetailPage() + 1 < sessionBean.getTotalNumberOfItems())
            {
                nextItem = getItem(sessionBean.getDetailPage() + 1).getItem();
            }
            else
            {
               return null;
            }
        }
        
        urlRequest += "/" + nextItem.getObjid();
        
        //TODO TO be removed
//        if (urlHelper.getSelection() != null)
//        {
//            urlRequest += "/currentalbum/" + urlHelper.getSelection();
//        }
        
        return urlRequest;
    }
    
    public String retievePrevious() throws Exception
    {
        Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
        String urlRequest = navigation.getApplicationUrl();
        Item previousItem = null;
        
        if (urlHelper.getAlbumId() != null)
        {
            urlRequest += "album/" + urlHelper.getAlbumId() + "/";
        }
        
        urlRequest += "details";
        
        if (sessionBean.getItems() != null)
        {
            for (int i = 0; i < sessionBean.getItems().size(); i++)
            {
                if (this.item.getItem().getObjid().equals(
                        sessionBean.getItems().get(i).getItem().getObjid()))
                {
                    if (i > 1)
                    {
                        previousItem = sessionBean.getItems().get(i-1).getItem();
                    }
                }
            }
        }
        
        if (previousItem == null)
        {
            if (sessionBean.getDetailPage() > 1)
            {
                previousItem = getItem(sessionBean.getDetailPage() - 1).getItem();
            }
            else 
            {
                return null;
            }
        }
                
        urlRequest += "/" + previousItem.getObjid();
        
        //TODO to be removed
        
//        if (urlHelper.getSelection() != null)
//        {
//            urlRequest += "/currentalbum/" + urlHelper.getSelection();
//        }
        return urlRequest;
    }
    
    public FacesItemVO getFacesItem()
    {
        return facesItem;
    }

    public void setFacesItem(FacesItemVO facesItem)
    {
        this.facesItem = facesItem;
    }
    
    
//    public String getFormatedAgeGroup()
//    {
//        String ageGroup= "Young";
//        
//        if ("old".equals(item.getItem().getMetadata().getAgeGroup().toString()))
//        {
//            ageGroup= "Older";
//        }
//        if ("middle_age".equals(item.getItem().getMetadata().getAgeGroup().toString()))
//        {
//            ageGroup= "Middle-Aged";
//        }
//        
//        return ageGroup;
//    }

   
}

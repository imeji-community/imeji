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

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.escidoc.faces.beans.SessionBean.pageContextEnum;
import de.mpg.escidoc.faces.container.album.AlbumSession;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.UrlHelper;

/**
 * Bean for a Faces Paginator.
 * @author saquet
 *
 */
public class Pagination
{
    private Navigation navigation = null;
    private FacesContext context;
    private UrlHelper urlHelper = null;
    private SessionBean sessionBean = null;
    private AlbumSession albumSession = null;
    
    //Navigation Links for the paginator
    private String first = "";
    private String previous = "";
    private String next = "";
    private String last = "";
   
    // current page number
    private int currentPageInt = 1;
    
    // number total of pages
    private int numberOfPages = 0;
    
    // number total of items
    private int numberOfItems = 0;
    
    private List<SelectItem> pageList = null;
    private String currentPageValue = "";
    private int paginatorSize = 5;
    
    // List of values for the select menu
    private List<SelectItem> itemsParPageList = null;
    
    // Variable for Menu "Show"
    private String itemsPerPageSelectedValue = null;
    private String itemsPerPageLabel1 = "12";
    private String itemsPerPageLabel2 = "24";
    private String itemsPerPageLabel3 = "60";
    private String itemsPerPageLabel4 = "90";
    
    /**
     * Default constructor.
     * @throws Exception
     */
    public Pagination() throws Exception
    {
        navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        albumSession = (AlbumSession)BeanHelper.getSessionBean(AlbumSession.class);
        context = FacesContext.getCurrentInstance();
        
        urlHelper = (UrlHelper) BeanHelper.getRequestBean(UrlHelper.class);
        if (urlHelper != null && urlHelper.getPage() != null)
        {
            currentPageInt = Integer.parseInt(urlHelper.getPage());
        }   
        if (urlHelper != null && urlHelper.getShow() != null)
        {
            this.setItemsPerPageSelectedValue(urlHelper.getShow());
        }
        
        // NEW
        initPageList();
    }
    
    /**
     * Initialize the list of pages of the paginator.
     */
    private void initPageList()
    {
        pageList = new ArrayList<SelectItem>();
        List<Integer> pageRangeList = getPageRangeAsList();
        
        for (int i = 0; i < pageRangeList.size(); i++)
        {
           pageList.add( new SelectItem( generateUrlFromPageNumber( pageRangeList.get(i) ) , pageRangeList.get(i).toString() ) );
        }
    }
    
    /**
     * Generate a list of all pages value allowed for the paginator.
     * @return
     */
    private List<Integer> getPageRangeAsList()
    {
        List<Integer> list = new ArrayList<Integer>();
        
        int min = 1;
        int max = paginatorSize;
        
        if (currentPageInt > (paginatorSize / 2))
        {
            min = currentPageInt - (paginatorSize / 2);
            max = currentPageInt + (paginatorSize / 2);
        }
        
        if (max > getNumberOfPages())
        {
            max = numberOfPages;
            
            if (numberOfPages - paginatorSize > 0)
            {
                min = numberOfPages - paginatorSize + 1;
            }
            else 
            {
                min = 1;
            }
        }
        
        while (max >= min)
        {
            list.add(min);
            min++;
        }
        
        return list;
    }
    

    /**
     * Returns the url for the page number requested.
     * @param pageNumber
     * @return
     */
    private String generateUrlFromPageNumber(int pageNumber)
    {
        String url = urlHelper.getBaseCurrentUrl() + "/";
        
        url += pageNumber;
        
        url += setOtherBrowsingParameters();
        
        return url;
    }                      

    /**
     * Returns the number total of pages of the current browsed collection/album/search. 
     * @return
     */
    public int getNumberOfPages()
    {
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (sessionBean.getPageContext().equals(pageContextEnum.browsePage.toString()))
        {
            numberOfPages = (int) Math.ceil(((double) HomePage.totalNumberOfItems)
                    / ((double) Integer.parseInt(sessionBean.getItemsPerPageBrowse())));
        }
        if (sessionBean.getPageContext().equals(pageContextEnum.albumPage.toString()))
        {
            numberOfPages = (int) Math.ceil(((double) HomePage.totalNumberOfItems)
                    / ((double) Integer.parseInt(sessionBean.getItemsPerPageAlbum())));
        }

        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages)
    {
        this.numberOfPages = numberOfPages;
    }
  
    /**
     * Redirect to the page number passed in the new value of vce
     * @param vce - ValueChangeEvent 
     * @throws Exception
     */
    public void goToPage(ValueChangeEvent vce) throws Exception
    {
        if (vce.getNewValue() != null && !vce.getNewValue().equals(vce.getOldValue()) && !"".equals(vce.getNewValue().toString()))
        {
            navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
            
            int newPage = 1;
            
            try
            {
                newPage = Integer.parseInt(vce.getNewValue().toString());
            }
            catch (Exception e)
            {
                sessionBean.setPageNotFound(true);
                sessionBean.setMessage("Please enter a number value!");
                context.getExternalContext().redirect(sessionBean.getBackToResultList());
            }
            
//            String show = vce.getComponent().getAttributes().get("show").toString();
//            urlHelper.setShow(show);
            
            String show = "12";           
            if (sessionBean.getPageContext().equals(pageContextEnum.browsePage.toString()))
            {
                show = sessionBean.getItemsPerPageBrowse();
            }
            if (sessionBean.getPageContext().equals(pageContextEnum.albumPage.toString()))
            {
                show = sessionBean.getItemsPerPageAlbum();                
            }
            
            // Generate the urls of the paginator to use the one we wants
            if (newPage > 0 && newPage < getNumberOfPages() + 1)
            {                
                String selectionId = null;
                String albumId = null;
                
                if (albumSession.getCurrent() != null )
                {
                    albumId = albumSession.getCurrent().getVersion().getObjectId();
                }
               
                context.getExternalContext().redirect(
                        navigation.createURL(Integer.toString(newPage), show,
                                sessionBean.getSortList(), sessionBean.getOrderList(), selectionId, albumId));
            }
            else 
            {
                // case page doesn't exist
                sessionBean.setPageNotFound(true);
                sessionBean.setMessage(sessionBean.getMessage("message_go_to_page_01") + "  "
                        + newPage + "  " +  sessionBean.getMessage("message_go_to_page_02"));
                context.getExternalContext().redirect(sessionBean.getBackToResultList());
            }
        }
    }
    
    /**
     * Set the in the urls the sorting parameters and the current album
     * return the url from the page number until the last parameters
     */
    private String setOtherBrowsingParameters()
    {
        String otherBrowsingParamters = "";
        if (sessionBean.getPageContext().equals(pageContextEnum.browsePage.toString()))
        {
            otherBrowsingParamters += "/" + sessionBean.getItemsPerPageBrowse();
        }
        if (sessionBean.getPageContext().equals(pageContextEnum.albumPage.toString()))
        {
            otherBrowsingParamters += "/" + sessionBean.getItemsPerPageAlbum();
        }
        
        if (urlHelper.getSortingUrl() != null)
        {
            otherBrowsingParamters += "/" + urlHelper.getSortingUrl();
        }
        
        if (urlHelper.getQuery() != null)
        {
            otherBrowsingParamters += "/" + urlHelper.getQuery();
        }
        
        return otherBrowsingParamters;
    }
    
    public String getFirst()
    {
        first = generateUrlFromPageNumber(1);
        return first;
    }

    public void setFirst(String first)
    {
        this.first = first;
    }

    public String getPrevious()
    {
        if (currentPageInt - 1 > 0)
        {
            previous = generateUrlFromPageNumber(currentPageInt -1);
        }
        else 
        {
            previous = first;
        }
        return previous;
    }

    public void setPrevious(String previous)
    {
        this.previous = previous;
    }
    public String getNext()
    {
        if (currentPageInt + 1  <= numberOfPages)
        {
            next = generateUrlFromPageNumber(currentPageInt + 1);
        }
        else 
        {
            next = last;
        }
        return next;
    }

    public void setNext(String next)
    {
        this.next = next;
    }

    public String getLast()
    {
        last = generateUrlFromPageNumber(numberOfPages);
        return last;
    }

    public void setLast(String last)
    {
        this.last = last;
    }

    public int getCurrentPageInt()
    {
        return currentPageInt;
    }

    public void setCurrentPageInt(int currentPageInt)
    {
        this.currentPageInt = currentPageInt;
    }

    public int getNumberOfItems()
    {
        numberOfItems = HomePage.totalNumberOfItems;
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems)
    {
        this.numberOfItems = numberOfItems;
    }

    public String getItemsPerPageLabel1()
    {
        return itemsPerPageLabel1;
    }

    public void setItemsPerPageLabel1(String itemsPerPageLabel1)
    {
        this.itemsPerPageLabel1 = itemsPerPageLabel1;
    }

    public String getItemsPerPageLabel2()
    {
        return itemsPerPageLabel2;
    }

    public void setItemsPerPageLabel2(String itemsPerPageLabel2)
    {
        this.itemsPerPageLabel2 = itemsPerPageLabel2;
    }

    public String getItemsPerPageLabel3()
    {
        return itemsPerPageLabel3;
    }

    public void setItemsPerPageLabel3(String itemsPerPageLabel3)
    {
        this.itemsPerPageLabel3 = itemsPerPageLabel3;
    }

    public String getItemsPerPageLabel4()
    {
        return itemsPerPageLabel4;
    }

    public void setItemsPerPageLabel4(String itemsPerPageLabel4)
    {
        this.itemsPerPageLabel4 = itemsPerPageLabel4;
    }

    public List<SelectItem> getItemsParPageList()
    {
        itemsParPageList = new ArrayList<SelectItem>();   
        itemsParPageList.add(new SelectItem(itemsPerPageUrl(itemsPerPageLabel1), itemsPerPageLabel1));
        itemsParPageList.add(new SelectItem(itemsPerPageUrl(itemsPerPageLabel2), itemsPerPageLabel2));
        itemsParPageList.add(new SelectItem(itemsPerPageUrl(itemsPerPageLabel3), itemsPerPageLabel3));
        itemsParPageList.add(new SelectItem(itemsPerPageUrl(itemsPerPageLabel4), itemsPerPageLabel4));
        
        return itemsParPageList;
    }
    
    /**
     * Create the url for the itemPerPage menu according to the value
     * @param itemPerPageValue
     * @return
     */
    private String itemsPerPageUrl(String itemPerPageValue)
    {
        String url =urlHelper.getBaseCurrentUrl() + "/1/" + itemPerPageValue;
        
        if (urlHelper.getSortingUrl() != null)
        {
            url += "/" + urlHelper.getSortingUrl();
        }
        if (urlHelper.getQuery() != null)
        {
            url += "/" + urlHelper.getQuery();
        }
        return url;
    }

    public void setItemsParPageList(List<SelectItem> itemsParPageList)
    {
        this.itemsParPageList = itemsParPageList;
    }

    public String getItemsPerPageSelectedValue()
    {
        if (sessionBean.getPageContext().equals(pageContextEnum.browsePage.toString()))
        {
            itemsPerPageSelectedValue = sessionBean.getItemsPerPageBrowse();           
        }
        if (sessionBean.getPageContext().equals(pageContextEnum.albumPage.toString()))
        {
            itemsPerPageSelectedValue = sessionBean.getItemsPerPageAlbum();
        }     
        return itemsPerPageSelectedValue;
    }

    public void setItemsPerPageSelectedValue(String show)
    {
        if ("home".equals(sessionBean.getCurrentUrl()) 
                || "searchresult".equalsIgnoreCase(sessionBean.getCurrentUrl())
                || "person".equals(sessionBean.getCurrentUrl()))
        {
            sessionBean.setPageContext(pageContextEnum.browsePage.toString());
            sessionBean.setItemsPerPageBrowse(show);
        }
        if ("viewAlbum".equals(sessionBean.getCurrentUrl()))
        {
            sessionBean.setPageContext(pageContextEnum.albumPage.toString());
            sessionBean.setItemsPerPageAlbum(show);
        }
    }
    
    public String getItemsPerPageSelectedValueMenu()
    {
        if (sessionBean.getPageContext().equals(pageContextEnum.browsePage.toString()))
        {
            return itemsPerPageUrl(sessionBean.getItemsPerPageBrowse());
        }
        if (sessionBean.getPageContext().equals(pageContextEnum.albumPage.toString()))
        {
            return itemsPerPageUrl(sessionBean.getItemsPerPageAlbum());
        } 
        return "";
    }

    public List<SelectItem> getPageList()
    {
        return pageList;
    }

    public void setPageList(List<SelectItem> pageList)
    {
        this.pageList = pageList;
    }

    public String getCurrentPageValue()
    {
        currentPageValue = this.generateUrlFromPageNumber(currentPageInt);
        return currentPageValue;
    }

    public void setCurrentPageValue(String currentPageValue)
    {
        this.currentPageValue = currentPageValue;
    }

    public int getPaginatorSize()
    {
        return paginatorSize;
    }

    public void setPaginatorSize(int paginatorSize)
    {
        this.paginatorSize = paginatorSize;
    }
}

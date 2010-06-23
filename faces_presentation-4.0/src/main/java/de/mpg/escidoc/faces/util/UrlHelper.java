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

package de.mpg.escidoc.faces.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.faces.beans.Navigation;


/**
 * Read the current Url and parse all the parameters
 */
public class UrlHelper
{
    // all the parameters from the url of Faces
    private String person = null;
    private String page = "1";
    private String show = "12";
    private String sortBy = null;
    private String order = null;
    private String order1 =  null;
    private String order2 = null;
    private String order3 = null;
    private String sort1 = null;
    private String sort2 = null;
    private String sort3 = null;
    private String query = null;
    private String albumId = null;
    private String selection = null;
    private String currentUrl = "welcome";
    private String action = null;
    private String item = null;
    private String resource = null;
    private String redirect = null;
    
    // String with all sorting parameters
    private String sortingUrl = null;
    
    // String of the first page of the url of the current page
    private String baseCurrentUrl = null;
    
    private FacesContext context = null;
    private HttpServletRequest request = null;
    
    Navigation navigation = null;
    
    
    public UrlHelper()
    {
        context = FacesContext.getCurrentInstance();
        request = (HttpServletRequest) context.getExternalContext().getRequest();
        navigation =  (Navigation) BeanHelper.getApplicationBean(Navigation.class);
        // initialize values
        this.getPage();
        this.getSort1();
        this.getSort2();
        this.getSort3();
        this.getOrder1();
        this.getOrder2();
        this.getOrder3();
        this.getSortBy();
        this.getOrder();
        this.getCurrentUrl();
        this.getPerson();
        this.getQuery();
        this.getAlbumId();
        this.getSelection();
        this.getAction();
        this.getItem();
        this.getRedirect();
        this.getShow();
        getResource();
    }
    
    /**
     * Set the value of the parameter if it is non null
     * Use for parameter which should always have a default value
     * @param parameterName - the name of the parameter in the Url
     * @param DefaultValue - the default value of the parameter
     * @return - the new value of the parameter
     */
    private String setNonNullParameter(String parameterName, String DefaultValue)
    {
        request = (HttpServletRequest) context.getExternalContext().getRequest();
        String parameterValue = DefaultValue;
        if (StringHelper.encodeCqlParameter(request.getParameter(parameterName)) != null)
        {
            parameterValue = StringHelper.encodeCqlParameter(request.getParameter(parameterName));
        }
        return parameterValue;
    }
    
    /**
     * Read the parameter in the url
     * @param parameterName
     * @return
     */
    private String readSimpleParameter(String parameterName)
    {
        request = (HttpServletRequest) context.getExternalContext().getRequest();
        return request.getParameter(parameterName);
    }
    
    /**
     * transform the value displayed in label to paramater in the urls
     * @param parameter the label
     * @return the url parameter
     */
    public String formatSortParameter(String parameter)
    {
      /*  if ("Picture Set".equals(parameter))
        {
            parameter = "pictureset";
        }
        if ("Person-ID".equals(parameter))
        {
            parameter = "personid";
        }
        else
        {
            parameter = parameter.toLowerCase();
        }*/
        return parameter;
    }

    public String getPerson()
    {
        
        person = StringHelper.encodeCqlParameter(this.readSimpleParameter("person"));
        return person;
    }

    public void setPerson(String person)
    {
        this.person = person;
    }

    public String getPage()
    {
        page = setNonNullParameter("page", page);
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    public String getShow()
    {
        String ret = StringHelper.encodeCqlParameter(this.readSimpleParameter("show")); 

        return ret;
    }

    public void setShow(String show)
    {
        this.show    = show;
    }

    public String getSortBy()
    {
        sortBy = StringHelper.encodeCqlParameter(this.readSimpleParameter("sortBy"));
        return sortBy;
    }

    public void setSortBy(String sortBy)
    {
        this.sortBy = sortBy;
    }

    public String getOrder()
    {
        order = StringHelper.encodeCqlParameter(this.readSimpleParameter("order"));
        return order;
    }

    public void setOrder(String order)
    {
        this.order = order;
    }

    public String getOrder1()
    {
        order1 = readSimpleParameter("order1");
        return order1;
    }

    public void setOrder1(String order1)
    {
        this.order1 = order1;
    }

    public String getOrder2()
    {
        order2 = readSimpleParameter("order2");
        return order2;
    }

    public void setOrder2(String order2)
    {
        this.order2 = order2;
    }

    public String getOrder3()
    {
        order3 = readSimpleParameter("order3");
        return order3;
    }

    public void setOrder3(String order3)
    {
        this.order3 = order3;
    }

    public String getSort1()
    {
        sort1 = readSimpleParameter("sort1");
        sort1 = formatSortParameter(sort1);
        return sort1;
    }

    public void setSort1(String sort1)
    {
        this.sort1 = sort1;
    }

    public String getSort2()
    {
        sort2 = readSimpleParameter("sort2");
        sort2 = formatSortParameter(sort2);
        return sort2;
    }

    public void setSort2(String sort2)
    {
        this.sort2 = sort2;
    }

    public String getSort3()
    {
        sort3 = readSimpleParameter("sort3");
        sort3 = formatSortParameter(sort3);
        return sort3;
    }

    public void setSort3(String sort3)
    {
        this.sort3 = sort3;
    }


    public String getQuery()
    {
        query = readSimpleParameter("query");
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getAlbumId()
    {
        albumId = StringHelper.encodeCqlParameter(this.readSimpleParameter("album"));
        return albumId;
    }

    public void setAlbumId(String albumId)
    {
        this.albumId = albumId;
    }

    public String getSelection()
    {
        selection = StringHelper.encodeCqlParameter(this.readSimpleParameter("selection"));
        return selection;
    }

    public void setSelection(String selection)
    {
        this.selection = selection;
    }
    
    /**
     * Return the parameter "currentUrl" from the url.
     * If you want to get the complete current url, please use the method getCompleteUrl()
     * @return the parameter
     */
    public String getCurrentUrl()
    {
        currentUrl = setNonNullParameter("currentUrl", currentUrl);
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl)
    {
        this.currentUrl = currentUrl;
    }

    public String getAction()
    {
        action =  StringHelper.encodeCqlParameter(this.readSimpleParameter("action"));
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    /**
     * create the part of the url with sorting parameters
     * @return the url as a string
     */
    public String getSortingUrl()
    {        
        if (sort1 != null)
        {
            sortingUrl = sort1 + "/" + order1;
        }
        if (sort2 != null)
        {
            sortingUrl += "/" + sort2 + "/" + order2;
        }
        if (sort3 != null)
        {
            sortingUrl += "/" + sort3 + "/" + order3;
        }
                
        return sortingUrl;
    }

    public void setSortingUrl(String sortingUrl)
    {
        this.sortingUrl = sortingUrl;
    }

    /**
     * Generate the first of the url before the browsing parameters
     * ex: "dev-faces.mpdl.mpg.de/home" or "localHost:8080/album/escidoc:0001"
     * @return the url
     */
    public String getBaseCurrentUrl()
    {
        baseCurrentUrl = navigation.getApplicationUrl();
        
        if (!"searchResult".equals(currentUrl)
                && !"viewAlbum".equals(currentUrl)
                && !"detailsFromAlbum".equals(currentUrl)
                && !"comparisonFromAlbum".equals(currentUrl)
                && !"searchResultInAlbum".equals(currentUrl)
                && !"comparisonFromAlbum".equals(currentUrl)
                && !"resource".equals(currentUrl))
        {
            baseCurrentUrl += currentUrl.toLowerCase();
        }
        
        if ("home".equals(currentUrl))
        {
            baseCurrentUrl = navigation.getHomePageUrl();
        }
        
        if ("person".equals(currentUrl))
        {
            baseCurrentUrl += "/" + person;
        }
        
        if ("resource".equals(currentUrl))
        {
            baseCurrentUrl += resource;
        }
        
        if (("viewAlbum".equals(currentUrl) 
                || "detailsFromAlbum".equals(currentUrl) 
                || "comparisonFromAlbum".equals(currentUrl)))
        {
            baseCurrentUrl += "album/" + albumId;;
        }
        
        if ("searchResult".equals(currentUrl)
                        || currentUrl.equals("searchResultInAlbum"))
        {
            baseCurrentUrl += "search/result";
        }
        return baseCurrentUrl;
    }
    
    /**
     * Create the url with the following parameters
     * @param currentUrl
     * @param Page
     * @param itemsPerPage
     * @param query
     * @return
     */
    public String getCompleteUrl(String currentUrl, String page, String itemsPerPage, String query)
    {
        String url = null;
        String originalValues[] = {this.currentUrl, this.page, this.show, this.query}; 
        //Set the values
        if (currentUrl != null)
        {
            this.currentUrl = currentUrl;
        }
        if (page != null)
        {
            this.page = page;
        }
        if (itemsPerPage != null)
        {
            this.show = itemsPerPage;
        }
        
        this.query = query;
        if (query != null)
        {
            
        }
        // Create the url
        url = this.getCompleteUrl();
        // Set original values
        this.currentUrl = originalValues[0];
        this.page = originalValues[1];
        this.show = originalValues[2];
        this.query = originalValues[3];
        
        return url;
    }
    
    /**
     * Generate the complete current url according to the current parameters in the url.
     * @return the complete url.
     */
    public String getCompleteUrl()
    {
        String completeUrl = getBaseCurrentUrl();
        
        // case with sorting value
        if ("home".equals(currentUrl) 
                || "searchResult".equals(currentUrl)
                || currentUrl.equals("searchResultInAlbum")
                || "viewAlbum".equals(currentUrl)
                || "person".equals(currentUrl))
        {
            completeUrl += "/" + page + "/" + show ;
            
            if (getSortingUrl() != null)
            {
                completeUrl += "/" + getSortingUrl();
            }
        }
        
        // for detail view
        if ("details".equals(currentUrl) || "comparison".equals(currentUrl))
        {
            completeUrl += "/" +  item;
        }
        
        // for add to another album
        if ("detailsFromAlbum".equalsIgnoreCase(currentUrl))
        {
            completeUrl += "/details/" + item;
        }
        
        // if an active album is selected
        if (selection != null)
        {
            completeUrl += "/currentalbum/" + selection;  
        }
        // If there is a search query
        if (query != null)
        {
            completeUrl += "/" + query;
        }
        return completeUrl;
    }

    public void setBaseCurrentUrl(String baseCurrentUrl)
    {
        this.baseCurrentUrl = baseCurrentUrl;
    }

    public String getItem()
    {
        item = StringHelper.encodeCqlParameter(this.readSimpleParameter("item"));
        return item;
    }

    public void setItem(String item)
    {
        this.item = item;
    }

    public String getRedirect()
    {
        redirect = StringHelper.encodeCqlParameter(this.readSimpleParameter("redirect"));
        return redirect;
    }

    public void setRedirect(String redirect)
    {
        this.redirect = redirect;
    }

    public String getResource()
    {
        resource = readSimpleParameter("resource");
        return resource;
    }

    public void setResource(String resource)
    {
        this.resource = resource;
    }
    
}

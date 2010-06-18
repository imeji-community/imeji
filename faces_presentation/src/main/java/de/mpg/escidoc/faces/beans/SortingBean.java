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

import javax.faces.model.SelectItem;

import de.mpg.escidoc.faces.beans.SessionBean.pageContextEnum;
import de.mpg.escidoc.faces.metadata.ScreenManager;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.UrlHelper;

public class SortingBean
{
    UrlHelper urlHelper;
    
    private ScreenManager sm = null;
    
    // Lists for the sorting select menus 
    private List<SelectItem> sortcriterialist1 = null;
    private List<SelectItem> sortcriterialist2 = null;
    private List<SelectItem> sortcriterialist3 = null;
    
    // Value selected in the sorting select menus
    private String selectedValue1 = null;
    private String selectedValue2 = null;
    private String selectedValue3 = null;
    
    // Values of the link for the order 
    private String order1 = null;
    private String order2 = null;
    private String order3 = null;
    
    // Values of the Styleclass of the sorting links (asc or desc)
    private String style1 = null;
    private String style2 = null;
    private String style3 = null;
    
    
    public SortingBean()
    {
        urlHelper = (UrlHelper) BeanHelper.getRequestBean(UrlHelper.class);
        
        // Get the ScreenManager for the sort features.
        sm = new ScreenManager("sort");
    }
    
    public String getBaseUrl()
    {
        int show = 0;
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (sessionBean.getPageContext().equals(pageContextEnum.browsePage.toString()))
        {
            show = Integer.parseInt(sessionBean.getItemsPerPageBrowse());
        }
        if (sessionBean.getPageContext().equals(pageContextEnum.albumPage.toString()))
        {
            show = Integer.parseInt(sessionBean.getItemsPerPageAlbum());
        }
        
        //return urlHelper.getBaseCurrentUrl() + "/" + urlHelper.getPage() + "/" + urlHelper.getShow();
        return urlHelper.getBaseCurrentUrl() + "/" + urlHelper.getPage() + "/" + show;
    }
    
    public String getSelection()
    {
        return urlHelper.getSelection();
    }
    
    public String getEndUrl()
    {
        String URL ="";
        
              
        if (urlHelper.getQuery() != null && !"".equals(urlHelper.getQuery()))
        {
            URL += "/"+ urlHelper.getQuery();
        }
        
        return URL;
    }
    
    /**
     * Initialize the position'th sorting select menu with the url where it redirects and the 
     * Label which should be displayed
     * @param position : number of the sortcriterialist
     * @param sortCriteriaList 
     * @return
     */
    private List<SelectItem> initializeSelectMenu(String position, List<SelectItem> sortCriteriaList)
    {
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        
        for (int i = 0; i < sm.getMdList().size(); i++)
        {
            if (sm.getMdList().get(i).getNode().getDisplay())
            {
                sortCriteriaList.add(new SelectItem(sm.getMdList().get(i).getIndex()
                        , sm.getMdList().get(i).getLabel()));
            }
        }
        
        selectedValue1 = urlHelper.getSort1();
        order1 = urlHelper.getOrder1();
        selectedValue2 = urlHelper.getSort2();
        order2 = urlHelper.getOrder2();
        selectedValue3 = urlHelper.getSort3();
        order3 = urlHelper.getOrder3();
        
        return sortCriteriaList;
    }
    
    /**
     * Get the first sorting select menu
     * This criterium is change with the default sorting behavior, therefore we have to reset the values of the other sort menu
     * @return 
     */
    public List<SelectItem> getSortcriterialist1()
    {
        sortcriterialist1 = new ArrayList<SelectItem>();
        sortcriterialist1 = initializeSelectMenu("1", sortcriterialist1);
        
        return sortcriterialist1;
    }
    
    /**
     * Get the second sorting select menu
     * @return 
     */
    public List<SelectItem> getSortcriterialist2()
    {
        sortcriterialist2 = new ArrayList<SelectItem>();
        sortcriterialist2 = initializeSelectMenu("2", sortcriterialist2);
        return sortcriterialist2;
    }
    
    /**
     * Get the third sorting select menu
     * @return 
     */
    public List<SelectItem> getSortcriterialist3()
    {
        sortcriterialist3 = new ArrayList<SelectItem>();
        sortcriterialist3 = initializeSelectMenu("3", sortcriterialist3);
        return sortcriterialist3;
    }
    
    public void setSortcriterialist1(List<SelectItem> sortcriterialist)
    {
        this.sortcriterialist1 = sortcriterialist;
    }
    
    public void setSortcriterialist2(List<SelectItem> sortcriterialist2)
    {
        this.sortcriterialist2 = sortcriterialist2;
    }

    public void setSortcriterialist3(List<SelectItem> sortcriterialist3)
    {
        this.sortcriterialist3 = sortcriterialist3;
    }

    /**
     * This value is used by the html component to know which value is actually selected
     * This one is for the first drop down menu.
     * The order values are all set to asc because this component works with a default behavior.
     * @return
     */
    public String getSelectedValue1()
    {        
        return selectedValue1;
    }

    public void setSelectedValue1(String selectedValue1)
    {
        this.selectedValue1 = selectedValue1;
    }
    
    /**
     * This value is used by the html component to know which value is actually selected
     * This one is for the second drop down menu.
     * @return
     */
    public String getSelectedValue2()
    {
        return selectedValue2;
    }

    public void setSelectedValue2(String selectedValue2)
    {
        this.selectedValue2 = selectedValue2;
    }
    
    /**
     * This value is used by the html component to know which value is actually selected
     * This one is for the third drop down menu.
     * @return
     */
    public String getSelectedValue3()
    {
        return selectedValue3;
    }

    public void setSelectedValue3(String selectedValue3)
    {
        this.selectedValue3 = selectedValue3;
    }
    

    public String getOrder1()
    {        
        return order1;
    }

    public void setOrder1(String order1)
    {
        this.order1 = order1;
    }

    public String getOrder2()
    {
        return order2;
    }

    public void setOrder2(String order2)
    {
        this.order2 = order2;
    }

    public String getOrder3()
    {
        return order3;
    }

    public void setOrder3(String order3)
    {
        this.order3 = order3;
    }

    public String getStyle1()
    {
        style1 = setStyle(urlHelper.getOrder1());
        return style1;
    }

    public void setStyle1(String style1)
    {
        this.style1 = style1;
    }

    public String getStyle2()
    {
        style2 = setStyle(urlHelper.getOrder2());
        return style2;
    }

    public void setStyle2(String style2)
    {
        this.style2 = style2;
    }

    public String getStyle3()
    {
        style3 = setStyle(urlHelper.getOrder3());
        return style3;
    }

    public void setStyle3(String style3)
    {
        this.style3 = style3;
    }

    
    /**
     * Set the correct value for the style of the order buttons
     * @param - order the order of the button
     * @return - the style
     */
    private String setStyle(String order)
    {
        if ("dsc".equals(order))
        {
            return "desSort";
        }
        else
        {
            return "ascSort";
        }
    }

}


/*
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

package de.mpg.escidoc.faces.affiliation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.faces.album.AlbumVO;
import de.mpg.escidoc.faces.album.beans.AlbumSession;
import de.mpg.escidoc.faces.beans.Navigation;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.search.Search;
import de.mpg.escidoc.services.search.query.OrgUnitsSearchResult;
import de.mpg.escidoc.services.search.query.PlainCqlQuery;

/**
 * Request bean to handle the organizational units.
 */
public class AffiliationBean
{
    //Bean Stuff
    public static final String BEAN_NAME = "AffiliationBean";
    private HttpServletRequest request = null;
    private FacesContext fc = null;
    private Navigation navigation = null;
    private SessionBean sessionBean = null;
    private AlbumSession albumSession = null;

    //UI Stuff
    private List<SelectItem> affSelectItemList;
    private HtmlSelectOneMenu affSelectOneMenu = new HtmlSelectOneMenu();
    private boolean integrateAffList = false;

    //Other
    private List<AffiliationVOPresentation> affVOs;
    private Map<String, AffiliationVOPresentation> affVOsMap;
    private String prefix = "";
    private boolean firstOrg = true;

    /**
     * Default constructor.
     */
    public AffiliationBean() throws Exception
    {
        this.fc =  FacesContext.getCurrentInstance();
        this.request = (HttpServletRequest) this.fc.getExternalContext().getRequest();
        this.navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
        this.sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        this.albumSession = (AlbumSession) BeanHelper.getSessionBean(AlbumSession.class);
    }
    
    /**
     * Adds the list of the given affiliations to the filter select
     * @param affs
     * @param affSelectItems
     * @param level
     * @throws Exception
     */
    private void addChildAffiliationsToMenu(List<AffiliationVOPresentation> affs, List<SelectItem> affSelectItems, int level) throws Exception
    {
        this.prefix = "";
        for (int i = 0; i < level; i++)
        {
            //3 save blanks
            this.prefix += '\u00A0';
            this.prefix += '\u00A0';
            this.prefix += '\u00A0';
        }
        //1 right angle
        this.prefix+='\u2514';
        for(AffiliationVOPresentation aff : affs)
        {            
            if (!aff.getIsClosed())
            {
                affSelectItems.add(new SelectItem(aff.getReference().getObjectId(), this.prefix+" "+aff.getName()));               
                this.affVOsMap.put(aff.getReference().getObjectId(), aff);
                addChildAffiliationsToMenu(aff.getChildren(), affSelectItems, level+1);
            }
        }
    }
    
    /**
     * Converts a list of AffiliationVOs to a list of AffiliationVOPresentations.
     * @param list the list of AffiliationVOs
     * @return the list of AffiliationVOPresentations
     */
    public static List<AffiliationVOPresentation> convertToAffiliationVOPresentationList(List<AffiliationVO> list)
    {
        List<AffiliationVOPresentation> affiliationList = new ArrayList<AffiliationVOPresentation>();
        
        for (int i = 0; i < list.size(); i++)
        {
            affiliationList.add(new AffiliationVOPresentation(list.get(i)));
        }
        AffiliationVOPresentation[] affiliationArray = affiliationList.toArray(new AffiliationVOPresentation[]{});
        Arrays.sort(affiliationArray);
        
        return Arrays.asList(affiliationArray);
    }
    
    /**
     * Returns all top-level affiliations.
     * 
     * @return all top-level affiliations
     * @throws Exception if framework access fails
     */
    public List<AffiliationVO> searchTopLevelAffiliations() throws Exception
    {
        InitialContext initialContext = new InitialContext();
        Search search = (Search) initialContext.lookup(Search.SERVICE_NAME);
        PlainCqlQuery cqlQuery = new PlainCqlQuery("(escidoc.objid=e* not escidoc.parent.objid>\"''\")");
        OrgUnitsSearchResult results = search.searchForOrganizationalUnits(cqlQuery);
        return results.getResults();
    }
    
    /**
     * Fills the fields according to the selected affiliation
     * @param event
     * @throws IOException
     */
    public void selectAffiliation(ValueChangeEvent event) throws IOException
    {
        // Load the album currently edited/created from the sessionBean
        AlbumVO viewAlbum = this.albumSession.getCurrent();
        // Get the author which should get a selected affiliation
        Object author = event.getComponent().getAttributes().get("creator");
        Object affiliation = event.getComponent().getAttributes().get("affObject");
        
        // set the selected Affiliation for the right author from the list of author 
        for (int i = 0; i < viewAlbum.getMdRecord().getCreators().size(); i++)
        {
            CreatorVO creator  =  viewAlbum.getMdRecord().getCreators().get(i);
            if (author != null && creator.equals(author))
            {
                for (int x = 0; x < viewAlbum.getMdRecord().getCreators().get(i).getPerson().getOrganizations().size(); x++)
                {
                    OrganizationVO org = viewAlbum.getMdRecord().getCreators().get(i).getPerson().getOrganizations().get(x);
                    if (affiliation != null && org.equals(affiliation))
                    {
                        String selectedAffId = this.affSelectOneMenu.getSubmittedValue().toString();
                        SelectItem selectedItem = new SelectItem("","---");
                        for (int y = 0; y < this.sessionBean.getAffiliationSelectItems().size(); y++)
                        {
                            if (this.sessionBean.getAffiliationSelectItems().get(y).getValue().equals(selectedAffId))
                            {
                                selectedItem = this.sessionBean.getAffiliationSelectItems().get(y);
                            }
                        }
                        String name = selectedItem.getLabel().replace('\u00A0'+"", "");
                        viewAlbum.getMdRecord().getCreators().get(i).getPerson().getOrganizations().get(x)
                            .setName(new TextVO(name.substring(2))); //substring(2) because of: 1 blank and the prefix                       
                        viewAlbum.getMdRecord().getCreators().get(i).getPerson().getOrganizations().get(x)
                            .setIdentifier(selectedItem.getValue().toString());
                    }
                }
             }
        }
        
        // Save the result in the sessionBean
        this.albumSession.setCurrent(viewAlbum);
        
        if ("/faces/jsf/AddAlbum.xhtml".equals(this.request.getRequestURI()))
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect(this.navigation.getCreateAlbumUrl());
        }
        else if ("/faces/jsf/EditAlbum.xhtml".equals(this.request.getRequestURI()))
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect(
                    this.navigation.getEditAlbumUrl() + "/" + viewAlbum.getLatestVersion().getObjectId());
        }
    }
    
    /**
     * Add a new affiliation to an author on the create album page
     * @param event
     * @throws IOException
     */
    public void addAffiliation(ActionEvent event) throws IOException
    {
        // Load the album currently edited/created from the sessionBean
        AlbumVO viewAlbum = this.albumSession.getCurrent();
        // Get the author which should get a new affiliation
        Object author = event.getComponent().getAttributes().get("creator");
        // Add the new Affiliation on the right author from the list of author 
        for (int i = 0; i < viewAlbum.getMdRecord().getCreators().size(); i++)
        {
           CreatorVO creator  =  viewAlbum.getMdRecord().getCreators().get(i);
            if (author != null && creator.equals(author))
           {
               viewAlbum.getMdRecord().getCreators().get(i).getPerson().getOrganizations().add(new OrganizationVO());
               viewAlbum.getMdRecord().getCreators().get(i).getPerson().getOrganizations().get(
                       viewAlbum.getMdRecord().getCreators().get(i).getPerson().getOrganizations().size() - 1).setName(new TextVO(""));
           }
        }
        // Save the result in the sessionBean
        this.albumSession.setCurrent(viewAlbum);
        // reload the page
        if ("/faces/jsf/AddAlbum.xhtml".equals(this.request.getRequestURI()))
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect(this.navigation.getCreateAlbumUrl());
        }
        else if ("/faces/jsf/EditAlbum.xhtml".equals(this.request.getRequestURI()))
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect(
                    this.navigation.getEditAlbumUrl() + "/" + viewAlbum.getLatestVersion().getObjectId());
        }
       
    }
    
    /**
     * Remove the last affiliation of the authors
     * @param event
     * @throws IOException 
     */
    public void removeAffiliation(ActionEvent event) throws IOException
    {
        // Load the album currently edited/created from the sessionBean
        AlbumVO viewAlbum = this.albumSession.getCurrent();
        // Get the author on whom should be add an affiliation
        Object author = event.getComponent().getAttributes().get("creator");
        Object affiliation = event.getComponent().getAttributes().get("affiliation");
        
       // Add the new Affiliation on the right author from the list of author 
        for (int i = 0; i < viewAlbum.getMdRecord().getCreators().size(); i++)
        {
           if (author != null 
                   && viewAlbum.getMdRecord().getCreators().get(i).equals(author) 
                   && viewAlbum.getMdRecord().getCreators().get(i).getPerson().getOrganizations().size() > 1)
           {
               for (int j = 0; j < viewAlbum.getMdRecord().getCreators().get(i).getPerson().getOrganizations().size(); j++)
               {
                   OrganizationVO organization =   viewAlbum.getMdRecord().getCreators().get(i).getPerson().getOrganizations().get(j);
                   if (organization.equals(affiliation))
                    {
                        viewAlbum.getMdRecord().getCreators().get(i).getPerson().getOrganizations().remove(j);
                    }
               }
           }
        }
        // Save the result in the sessionBean
        this.albumSession.setCurrent(viewAlbum);
        // reload the page
        if ("/faces/jsf/AddAlbum.xhtml".equals(this.request.getRequestURI()))
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect(this.navigation.getCreateAlbumUrl());
        }
        else if ("/faces/jsf/EditAlbum.xhtml".equals(this.request.getRequestURI()))
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect(
                    this.navigation.getEditAlbumUrl() + "/" + viewAlbum.getLatestVersion().getObjectId());
        }
    }
    
    /**
     * Returns SelectItems for a menu with all organizational units.
     * @return List of SelectItem Objects
     * @throws Exception
     */
    public List<SelectItem> getAffSelectItemList() throws Exception
    {
        if (this.sessionBean.getAffiliationSelectItems() == null)
        {
            this.affVOsMap = new HashMap<String, AffiliationVOPresentation>();
            this.affVOs = convertToAffiliationVOPresentationList(searchTopLevelAffiliations());
            this.affSelectItemList = new ArrayList<SelectItem>();
            this.affSelectItemList.add(new SelectItem("","---"));
            
            List<AffiliationVOPresentation> topLevelAffs = getAffVOs();
            addChildAffiliationsToMenu(topLevelAffs, this.affSelectItemList, 0);
            this.setAffSelectItemList(this.affSelectItemList);
        }
        
        return this.sessionBean.getAffiliationSelectItems();
    }
    
    public void setAffSelectItemList(List<SelectItem> affSelectItemList)
    {
        this.sessionBean.setAffiliationSelectItems(affSelectItemList);
    }

    public void setAffMap(Map<String, AffiliationVOPresentation> affiliationMap)
    {
        this.affVOsMap = affiliationMap;
    }

    /**
     * Returns a Map that contains all affiliations with their id as key. Only fully available if getAffiliationSelectItems() is called before.
     * @return
     */
    public Map<String, AffiliationVOPresentation> getAffMap()
    {
        return this.affVOsMap;
    }
    
    public List<AffiliationVOPresentation> getAffVOs()
    {
        return this.affVOs;
    }

    public void setAffVOs(List<AffiliationVOPresentation> affVOs)
    {
        this.affVOs = affVOs;
    }
    
    public HtmlSelectOneMenu getAffSelectOneMenu() {
        return this.affSelectOneMenu;
    }

    public void setAffSelectOneMenu(HtmlSelectOneMenu affSelectOneMenu) {
        this.affSelectOneMenu = affSelectOneMenu;
    }   
    
    public boolean getIntegrateAffList()
    {
        try
        {
            String prop = PropertyReader.getProperty("escidoc.organizationTree.integration");
            if (prop.equalsIgnoreCase("yes") || prop.equalsIgnoreCase("y"))
            {
                return true;
            }
            else 
            {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public void setIntegrateAffList(boolean integrateAffList)
    {
        this.integrateAffList = integrateAffList;
    }
    
    
    
    public boolean getFirstOrg()
    {
        return this.firstOrg;
    }

    public void setFirstOrg(boolean firstOrg)
    {
        this.firstOrg = firstOrg;
    }
}

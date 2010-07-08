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
package de.mpg.escidoc.faces.container.beans;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.faces.beans.Navigation;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.container.album.AlbumController;
import de.mpg.escidoc.faces.container.album.AlbumListVO;
import de.mpg.escidoc.faces.container.album.AlbumSession;
import de.mpg.escidoc.faces.container.album.AlbumVO;
import de.mpg.escidoc.faces.container.list.FacesContainerListController;
import de.mpg.escidoc.faces.item.ItemVO;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.OrganizationVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.PersonVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.TextVO;
import de.mpg.escidoc.services.common.valueobjects.metadata.CreatorVO.CreatorRole;

/**
 * 
 * @author saquet
 *
 */

public class ViewAlbumBean
{
    /**
     * The current album display in the formular.
     */
    private AlbumVO album = null;
    /**
     * The title of the album
     */
    private String title = null;
    /**
     * The description of the album
     */
    private String description = "";
    /**
     * The type of this formular
     */
    private FormularType type;
    /**
     * Check if the formular has to be reinitialize.
     */
    private String action = null;
    
    /**
     * Type of a formular.
     * @author saquet
     *
     */
    public enum FormularType {
        CREATE, EDIT;
    }
    
    // Class declaration of other beans
    private FacesContainerListController controller = null;
    private AlbumController albumController = null;
    private SessionBean sessionBean = null;
    private AlbumSession albumSession = null;
    private Navigation navigation = null;
    private HttpServletRequest request = null;
    private FacesContext fc = null;
   
    /**
     * The Bean for all albums Formular (Create and Edit).
     * @throws NamingException
     */
    public ViewAlbumBean() throws NamingException
    {
        // Get the beans instance
        sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        albumSession = (AlbumSession) BeanHelper.getSessionBean(AlbumSession.class);
        navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
        fc =  FacesContext.getCurrentInstance();
        request = (HttpServletRequest) fc.getExternalContext().getRequest();
        album = albumSession.getCurrent();
        controller = new FacesContainerListController();
        albumController = new AlbumController();
        initAlbum();
    }
    
    /**
     * Initialize the album value.
     */
    public void initAlbum()
    {
        String albumId = request.getParameter("album");
        
        if (albumId != null)
        {
            if (albumId.equals(albumSession.getCurrent().getVersion().getObjectId()))
            {
                album = albumSession.getCurrent();
            }
            else 
            {
                try 
                {
                    album = (AlbumVO) albumController.retrieve(albumId, sessionBean.getUserHandle());
		} 
                catch (Exception e) 
                {
                    sessionBean.setMessage(albumId + " not found!");
		}

                albumSession.setCurrent(album);
            }           
            type = FormularType.EDIT;
        }
        else if (albumId == null && "init".equals(request.getParameter("action")))
        {
            newFormular();
            albumSession.setCurrent(album);
            sessionBean.setItems(new ArrayList<ItemVO>());
            type = FormularType.CREATE;
           
        }
        else
        {
            album = albumSession.getCurrent();
            if (album.getVersion().getObjectId() != null)
            {
                type = FormularType.EDIT;
            }
            else 
            {
                type = FormularType.CREATE;
            }
        }
    }
    
    /**
     * Initialize a new leer formular.
     */
    private void newFormular()
    {
        album = new AlbumVO();
        // Initialize title
        album.getMdRecord().setTitle(new TextVO(""));
        // Initialize Abstract
        album.getMdRecord().getAbstracts().add(new TextVO(""));
        // Initialize creator
        CreatorVO creator = new CreatorVO();
        PersonVO person = new PersonVO();
        person.setFamilyName("");
        person.setGivenName("");
        person.getOrganizations().add(new OrganizationVO());
        person.getOrganizations().get(0).setName(new TextVO(""));
        person.getOrganizations().get(0).setIdentifier("");
        creator.setPerson(person);
        album.getMdRecord().getCreators().add(creator);
    }
    
    /**
     * Method Save called on click by user.
     */
    public void save()
    {
    	switch (this.type) {
		case CREATE:
			this.create();
			break;
		case EDIT:
			this.edit();
			break;
		default:
			break;
		}
    }
    
    /**
     * add a creator to the current album viewed. 
     * If you want to valid your change on the Fw, you have to call edit()
     * @return
     */
    public String addAuthor()
    {
        // Initialize the new author
    	PersonVO newAuthor = new PersonVO();
        // Add the affiliation
        newAuthor.getOrganizations().add(new OrganizationVO());
        // initialization of values
        newAuthor.setFamilyName("");
        newAuthor.setGivenName("");
        newAuthor.getOrganizations().get(0).setName(new TextVO(""));
        // Set the value in the album
        album.getMdRecord().getCreators().add(new CreatorVO(newAuthor, CreatorRole.CONTRIBUTOR));
        // Save the album in the sessionBean
        albumSession.setCurrent(album);
        // Reload the page
        reload();
        
        return null;
    }
    
    /**
     * Remove the last author from the list of author on create album page
     * @return
     * @throws IOException 
     */
    public void removeAuthor(ActionEvent event) throws IOException
    {
        // Get the author which should be removed
        Object author = event.getComponent().getAttributes().get("creator");
        // Remove the creator according to the actionevent
        if (album.getMdRecord().getCreators().size() > 1)
        {
            for (int i = 0; i < album.getMdRecord().getCreators().size(); i++)
            {
                if (album.getMdRecord().getCreators().get(i).equals(author))
                {
                    album.getMdRecord().getCreators().remove(i);
                }
            }
            albumSession.setCurrent(album);
        }
        else
        {
            sessionBean.setMessage("deactivate button");
        }
        reload();
    }
    
    /**
     * Add a new affiliation to an author on the create album page
     * @param event
     * @throws IOException
     */
    public void addAffiliation(ActionEvent event) throws IOException
    {
        // Load the album currently edited/created from the sessionBean
        album = albumSession.getCurrent();
        // Get the author which should get a new affiliation
        Object author = event.getComponent().getAttributes().get("creator");
        // Add the new Affiliation on the right author from the list of author 
        for (int i = 0; i < album.getMdRecord().getCreators().size(); i++)
        {
           CreatorVO creator  =  album.getMdRecord().getCreators().get(i);
            if (author != null && creator.equals(author))
           {
               album.getMdRecord().getCreators().get(i).getPerson().getOrganizations().add(new OrganizationVO());
               album.getMdRecord().getCreators().get(i).getPerson().getOrganizations().get(
                       album.getMdRecord().getCreators().get(i).getPerson().getOrganizations().size() - 1).setName(new TextVO(""));
           }
        }
        // Save the result in the sessionBean
        albumSession.setCurrent(album);
        // reload the page
        reload();
       
    }
    
    /**
     * Remove the last affiliation of the authors
     * @param event
     * @throws IOException 
     */
    public void removeAffiliation(ActionEvent event) throws IOException
    {
        // Get the author on whom should remove an affiliation
        Object author = event.getComponent().getAttributes().get("creator");
        Object affiliation = event.getComponent().getAttributes().get("affiliation");

        
        for (int i = 0; i < this.album.getMdRecord().getCreators().size(); i++)
        {
           if (author != null 
                   && album.getMdRecord().getCreators().get(i).equals(author) 
                   && album.getMdRecord().getCreators().get(i).getPerson().getOrganizations().size() > 1)
           {
               for (int j = 0; j < album.getMdRecord().getCreators().get(i).getPerson().getOrganizations().size(); j++)
               {
                   OrganizationVO organization =   album.getMdRecord().getCreators().get(i).getPerson().getOrganizations().get(j);
                   if (organization.equals(affiliation))
                    {
                        album.getMdRecord().getCreators().get(i).getPerson().getOrganizations().remove(j);
                    }
               }
           }
        }
        // Save the result in the sessionBean
        albumSession.setCurrent(album);
        reload();
    }

    /**
     * Create an album from values of the form
     * @return
     */
    public String create()
    {        
        if (validForm())
        {
            try
            {
                List<PersonVO> creators = new ArrayList<PersonVO>();
                
                for (CreatorVO creatorVO : album.getMdRecord().getCreators())
                {
                    creators.add(creatorVO.getPerson());
                }
                                
                AlbumController controller = new AlbumController();
                album.getMetadataSets().add(album.getMdRecord());
                album = (AlbumVO) controller.create(album, sessionBean.getUserHandle());
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error creating an album", e);
            }
            try
            {
                FacesContext.getCurrentInstance().getExternalContext().redirect(
                		navigation.getAlbumUrl() 
                		+ "/" + album.getVersion().getObjectId() 
                		+ "/" + navigation.getDefaultBrowsingKeepShowAlbum());
                // Set to null
                albumSession.setCurrent(null);
            }
            catch (IOException e)
            {
                throw new RuntimeException("Error redirect after creation of the album", e);
            }
            
            if (sessionBean.isActive()) 
            {
				albumSession.setActive(album);
				sessionBean.setActive(false);
			}
        }
        else
        {
        	reload();
        }
        return null;
    }
    
    /**
     * Edit (i.e update) the album in the FW
     * @return
     */
    public String edit()
    {
        if (validForm())
        {
            try
            {
                List<PersonVO> creators = new ArrayList<PersonVO>();
                for (CreatorVO creatorVO : album.getMdRecord().getCreators())
                {
                    creators.add(creatorVO.getPerson());
                }
                // edit in the FW
                AlbumController albumController = new AlbumController();
                albumController.edit(album.clone(), sessionBean.getUserHandle());
                
                // update the lists
                albumSession.setMyAlbums((AlbumListVO) controller.retrieve(
                                albumSession.getMyAlbums()
                                , sessionBean.getUserHandle()));
               
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error editing an album", e);
            }
            try
            {
                // redirect to the page of the album
                fc.getExternalContext().redirect(navigation.getAlbumUrl() + "/" + album.getVersion().getObjectId() + "/" + navigation.getDefaultBrowsingKeepShowAlbum());
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error redirect by edition of an album", e);
            }
            // Set to null
            albumSession.setCurrent(null);
        }
        else
        {
            reload();
        }
        
        return null;
    }
    
    /**
     * Valid the form
     * @return Boolean
     */
    private Boolean validForm()
    {
        for (int i = 0; i < album.getMdRecord().getCreators().size(); i++)
        {
            if ("".equals(album.getMdRecord().getCreators().get(i).getPerson().getFamilyName()) 
                    || "".equals(album.getMdRecord().getCreators().get(i).getPerson().getOrganizations().get(0).getName().getValue()))
            {
                sessionBean.setMessage("Album must have an author with at least one family name and one affiliation!");
                return false;
            }
            album.getMdRecord()
            	 .getCreators()
            	 .get(i)
            	 .getPerson()
            	 .setCompleteName(album.getMdRecord().getCreators().get(i).getPerson().getGivenName() 
            			 + " "
            			 + album.getMdRecord().getCreators().get(i).getPerson().getFamilyName());
        }
        if (album.getMdRecord().getTitle() != null 
                && !"".equals(album.getMdRecord().getTitle()))
        {
            return true;
        }
        else
        {
        	sessionBean.setMessage("Album must have a title");
        	return false;
        }
    }
    
    /**
     * Reload the formular.
     */
    public void reload()
    {
        try
        {
            if (FormularType.CREATE.equals(type))
            {
                FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getCreateAlbumUrl());
            }
            else if (FormularType.EDIT.equals(type))
            {
                FacesContext.getCurrentInstance().getExternalContext().redirect(
                		navigation.getEditAlbumUrl() 
                		+ "/" + album.getVersion().getObjectId());
            }
        }
        catch (Exception e)
        {
            sessionBean.setMessage("An error occurs: " + e);
        }
    }
    
    /**
     * Used for rendering purpose
     * @return
     */
    public String getNumberOfCreators()
    {
        return Integer.toString(album.getMdRecord().getCreators().size());
    }

    /**
     * JSF Listener for the title value
     * @param event
     */
    public void titleListener(ValueChangeEvent event)
    {
    	if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) 
    	{
			albumSession.getCurrent().getMdRecord().getTitle().setValue(event.getNewValue().toString());
			albumSession.setCurrent(album);
		}
    }
    
    /**
     * JSF Listener for the abstract value
     * @param event
     */
    public void abstractsListener(ValueChangeEvent event)
    {
    	if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) 
    	{
			albumSession.getCurrent().getMdRecord().getAbstracts().get(0).setValue(event.getNewValue().toString());
		   	albumSession.setCurrent(album);
		}
    }
    
    /**
     * JSF Listener for the family Name value.
     * @param event
     */
    public void familyNameListener(ValueChangeEvent event)
    {
    	if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) 
    	{
    	    int index = 0;
            if (event.getComponent().getAttributes().get("index") == null)
            {
                CreatorVO newCreator = new CreatorVO();
                PersonVO newPerson = new PersonVO();
                OrganizationVO org = new OrganizationVO();
                org.setName(new TextVO(""));
                newPerson.setFamilyName(event.getNewValue()+"");
                newPerson.getOrganizations().add(org);
                newCreator.setPerson(newPerson);
                album.getMdRecord().getCreators().add(newCreator);
                //This hack is needed to remove the empty creator wgich was created by default.
                int i= this.getInfexOfEmptyCreator(this.album.getMdRecord().getCreators());
                if (i >= 0)
                {
                    this.album.getMdRecord().getCreators().remove(i);
                }
            }
            else
            {
                index = Integer.parseInt(event.getComponent().getAttributes().get("index").toString());
                album.getMdRecord().getCreators().get(index).getPerson().setFamilyName(event.getNewValue().toString());               
            }
            albumSession.setCurrent(album);
		}
    }
    
    /**
     * JSF Listener for the given Name value.
     * @param event
     */
    public void givenNameListener(ValueChangeEvent event)
    {
    	if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) 
    	{
            if (event.getComponent().getAttributes().get("index") == null)
            {
                album.getMdRecord().getCreators().get(album.getMdRecord().getCreators().size()-1).getPerson()
                    .setGivenName(event.getNewValue().toString());
            }
            else
            {
                int index =  Integer.parseInt(event.getComponent().getAttributes().get("index").toString());
                album.getMdRecord().getCreators().get(index).getPerson().setGivenName(event.getNewValue().toString());               
            }
            albumSession.setCurrent(album);
		}
    }
    
    /**
     * JSF Listener for the affiliation value
     * @param event
     */
    public void affiliationListener(ValueChangeEvent event)
    {
    	if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) 
    	{
    	    int indexAffiliation = 0;
			int indexCreator =  0;
			if (event.getComponent().getAttributes().get("indexCreator") != null)
			{ 
			    indexCreator = Integer.parseInt(event.getComponent().getAttributes().get("indexCreator").toString());
    			if (event.getComponent().getAttributes().get("indexAffiliation") == null)
    			{
    			    OrganizationVO newOrg = new OrganizationVO();
    			    newOrg.setName(new TextVO(event.getNewValue()+""));
    			    this.album.getMdRecord().getCreators().get(indexCreator).getPerson().getOrganizations().add(newOrg);
    			  //This hack is needed to remove the empty org which is created by default.
    			    this.album.getMdRecord().getCreators().get(indexCreator).getPerson().setOrganizations(
    			            this.removeEmptyAffiliations(this.album.getMdRecord().getCreators().get(indexCreator).getPerson().getOrganizations()));
    			}
    			else
    			{
    			    indexAffiliation =  Integer.parseInt(event.getComponent().getAttributes().get("indexAffiliation").toString());
    			    this.album.getMdRecord()
    	                .getCreators().get(indexCreator).getPerson()
    	                    .getOrganizations().get(indexAffiliation)
    	                        .setName(new TextVO(event.getNewValue().toString()));
    			}       		
			}
			else
			{
                OrganizationVO newOrg = new OrganizationVO();
                newOrg.setName(new TextVO(event.getNewValue()+""));
                this.album.getMdRecord().getCreators().get(album.getMdRecord().getCreators().size()-1).getPerson().getOrganizations().add(newOrg);
                //This hack is needed to remove the empty org which is created by default.
                this.album.getMdRecord().getCreators().get(album.getMdRecord().getCreators().size()-1).getPerson().setOrganizations(
                        this.removeEmptyAffiliations(this.album.getMdRecord().getCreators().get(album.getMdRecord().getCreators().size()-1)
                                .getPerson().getOrganizations()));
			}
		}
    	albumSession.setCurrent(this.album);
    }
    
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String nameNewAlbum)
    {
        this.title = nameNewAlbum;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public AlbumVO getViewAlbum()
    {
        return album;
    }

    public void setViewAlbum(AlbumVO viewAlbum)
    {
        this.album = viewAlbum;
    }

    public AlbumVO getAlbum()
    {
        return album;
    }

    public void setAlbum(AlbumVO album)
    {
        this.album = album;
    }

    public FormularType getType()
    {
        return type;
    }

    public void setType(FormularType type)
    {
        this.type = type;
    }
    
    /**
     * This method takes a list of organizational units and seletes all empty ones.
     * @param orgList
     * @return
     */
    public List <OrganizationVO> removeEmptyAffiliations(List <OrganizationVO> orgList)
    {
        for (int i = 0; i< orgList.size(); i++)
        {
            OrganizationVO org = orgList.get(i);
            if (org.getName().getValue().equals(""))
            {
                orgList.remove(i);
            }
        }
        return orgList;
    }
    
    /**
     * @param creatorList
     * @return
     */
    public int getInfexOfEmptyCreator(List <CreatorVO> creatorList)
    {
        for (int i = 0; i< creatorList.size(); i++)
        {
            CreatorVO creator = creatorList.get(i);
            if (creator.getPerson().getFamilyName().equals("") &&
                    creator.getPerson().getGivenName().equals(""))
            {
                return i;
            }
        }
        return -1;
    }
}

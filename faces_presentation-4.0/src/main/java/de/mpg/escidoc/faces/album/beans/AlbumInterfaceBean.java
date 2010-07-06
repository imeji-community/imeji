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
package de.mpg.escidoc.faces.album.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;

import de.mpg.escidoc.faces.album.AlbumController;
import de.mpg.escidoc.faces.album.AlbumVO;
import de.mpg.escidoc.faces.beans.Navigation;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.item.ItemVO;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.QueryHelper;
import de.mpg.escidoc.faces.util.UrlHelper;

/**
 * Interface for the method to manage an album.
 * @author saquet
 *
 */
public class AlbumInterfaceBean 
{
    /**
     * The requested method.
     */
    private MethodType method = null;
    /**
     * The requested album
     */
    private AlbumVO album = null;
    /**
     * The identifier of the requested album
     */
    private String albumId = null;
	/**
     * The identifier of the requested item
     */
    private String itemId= null;
    /**
     * The url for redirect after method processing.
     */
    private String url = null;
    /**
     * The comment of a album withdrawal
     */
    private String withdrawComment = "";

    /**
     * Type of the method that are managed by this bean.
     * @author saquet
     *
     */
    public static enum MethodType{
        ADDPICTURE, REMOVEPICTURE, REMOVEPAGE, REMOVEALL, ADDPAGE, ADDALL, DELETEALBUM, CREATE, UPDATE;
    }
    
    private SessionBean sessionBean = null;
    private AlbumSessionOld albumSession = null;
    private AlbumController albumController = null;
    private Navigation navigation = null;
    
    /**
     * Default constructor
     */
    public AlbumInterfaceBean()
    {
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        albumSession = (AlbumSessionOld)BeanHelper.getSessionBean(AlbumSessionOld.class);
        albumController = new AlbumController();        
    }
    
    /**
     * Initialization of the class called from jsp
     */
    public String getInit()
    {
        readParameters();
        getRequestedObjects();
        
        if (method != null
                && album != null)
        {
            run();
        }
        try
        {
            redirect();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }    
        
        return "";
    }
    
    /**
     * Read the parameters in the url
     */
    private void readParameters()
    {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        
        itemId = request.getParameter("item");
        albumId = request.getParameter("album");
        url = request.getParameter("redirect");
        
        for (int i = 0; i < MethodType.values().length; i++)
        {
            if (MethodType.values()[i].toString().equalsIgnoreCase(request.getParameter("method")))
            {
                method = MethodType.values()[i];
            }
        }
    }
    
    /**
     * Get the objects requested into the url.
     */
    private void getRequestedObjects()
    {
        if (albumId != null && !isActiveAlbum(albumId))
        {
            try 
            {
				album = albumController.retrieve(albumId, sessionBean.getUserHandle());
			} 
            catch (Exception e) 
            {
            	sessionBean.setMessage(albumId + " not found!");
			}
        }
        else if (albumId != null && isCurrentAlbum(albumId))
        {
            album = albumSession.getCurrent();
        }
        else
        {
            album = albumSession.getActive();
        }
    }
    
    /**
     * Run the method called
     */
    private void run()
    {
        switch (method)
        {
            case ADDPICTURE:
                addPicture();
                break;
            case REMOVEPICTURE:
                removePicture();
                break;
            case ADDALL:
				QueryHelper queryHelper;
				try 
				{
					queryHelper = new QueryHelper();
					queryHelper.executeQuery(sessionBean.getQuery(), sessionBean.getTotalNumberOfItems(), 1, sessionBean.getSortedBy());
				} 
				catch (Exception e) 
				{
					throw new RuntimeException(e);
				}
            	addPictures(convertToListOfIdsAsString(queryHelper.getItems()));
                break;
            case ADDPAGE:
                addPictures(convertToListOfIdsAsString(sessionBean.getItems()));
                break;
            case REMOVEALL:
                removePictures(album.getMembersId());
                break;
            case REMOVEPAGE:
                removePictures(convertToListOfIdsAsString(sessionBean.getItems()));
                break;
            case CREATE:
            	break;
            case UPDATE:
            	break;
            default:
                sessionBean.setMessage("Method " + method + " not managed");
                break;
        }
    }
    
    /**
     * Redirect to a page of Faces after method called.
     * @throws Exception
     */
    private void redirect() throws Exception
    {
        redirect(url);
    }
    
    /**
     * redirect to the url.
     * @param url
     * @throws Exception
     */
    private void redirect(String url) throws Exception
    {
    	if (url == null && sessionBean.getBackUrl() != null)
        {
            url = sessionBean.getBackUrl();
        }
        if (url.equals("back") && sessionBean.getBackToResultList() != null)
        {
            url = sessionBean.getBackToResultList();
        }
        try
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        }
        catch (Exception e)
        {
            FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getHomePageUrl());
        }
    }
    
    /**
     * Add the item to the album.
     * @throws IOException
     */
    private void addPicture()
    {
        List<String> list = new ArrayList<String>();
        list.add(itemId);
        
        this.addPictures(list);
    }
    
    /**
     * Remove the item of the album.
     * @throws IOException
     */
    private void removePicture()
    {
        List<String> list = new ArrayList<String>();
        list.add(itemId);
        this.removePictures(list);
    }
    
    /**
     * Add a list of item to the album
     * @param itemList
     */
    private void addPictures(List<String> itemList)
    {
        int sizeInitial = album.getSize();
        
        try
        {
            album = albumController.addMembers(album, itemList, sessionBean.getUserHandle());
            if (isActiveAlbum(album.getVersion().getObjectId()))
            {
                albumSession.setActive(album);
            }
        }
        catch (Exception e)
        {
            sessionBean.setMessage(sessionBean.getMessage("error_add_item") + e);
        }
        
        int sizeFinal = album.getSize();
        
        switch (sizeFinal - sizeInitial)
        {
            case 0:
                sessionBean.setInformation(sessionBean.getMessage("error_add_item_none") + "  " + album.getMdRecord().getTitle().getValue());
                break;
            case 1:
                sessionBean.setInformation(sessionBean.getMessage("success_add_picture_album") + "  " + album.getMdRecord().getTitle().getValue());
                break;
            default:
                sessionBean.setInformation(sizeFinal - sizeInitial  + " " + sessionBean.getMessage("success_add_pictures_albums")
                                            + album.getMdRecord().getTitle().getValue());
                break;
        }
    }
    
    private void removePictures(List<String> itemList)
    {
        int sizeInitial = album.getSize();
        
        try
        {
            album = albumController.removeMembers(album, itemList, sessionBean.getUserHandle());
            if (isActiveAlbum(album.getVersion().getObjectId()))
            {
                albumSession.setActive(album);
            }
        }
        catch (Exception e)
        {
            sessionBean.setMessage("Error adding the image to the active album: " + e);
        }
        
        int sizeFinal = album.getSize();
        
        switch (sizeInitial - sizeFinal)
        {
            case 0:
                sessionBean.setInformation("No Picture deleted of " + album.getMdRecord().getTitle().getValue());
                break;
            case 1:
                sessionBean.setInformation("One Picture deleted of "+ album.getMdRecord().getTitle().getValue());
                break;
            default:
                sessionBean.setInformation(sizeInitial - sizeFinal  + " " + sessionBean.getMessage("success_delete_items"));
                break;
        }
        
        url = navigation.getAlbumUrl() + "/" + albumSession.getCurrent().getVersion().getObjectId()+ "/" + navigation.getDefaultBrowsingKeepShowAlbum();
    }
    
    /**
     * Delete the current album.
     * @throws Exception
     */
    public void deleteAlbum() throws Exception
    {
    	try 
    	{
			albumController.delete(albumSession.getCurrent(), sessionBean.getUserHandle());
		} 
    	catch (Exception e) 
    	{
			sessionBean.setMessage("Error deleting the album: " + e);
		}
		redirect(navigation.getAlbumsUrl());
    }
    
    /**
     * Delete the album that have been selected in the list of albums.
     * @throws Exception
     */
    public void deleteAlbumsSelected() throws Exception
    {    	
    	for (int i = 0; i < albumSession.getMyAlbums().getList().size(); i++)
    	{
             if (albumSession.getMyAlbums().getList().get(i).isSelected())
             {
            	 try 
            	 {
            		 albumController.delete(albumSession.getMyAlbums().getList().get(i), sessionBean.getUserHandle());
            		 // Check if the active album has been deleted.
            		 if (albumSession.getActive() != null && 
            				 albumSession.getMyAlbums().getList().get(i).getVersion().getObjectId().equals(
            						 albumSession.getActive().getVersion().getObjectId())) 
            		 {
            			 albumSession.setActive(new AlbumVO());
            		 }
            	 }
            	 catch (Exception e) 
            	 {
					sessionBean.setMessage(sessionBean.getMessage("message_delete_multiple_select"));
            	 }
             }
    	}
    	
    	redirect(navigation.getAlbumsUrl());
    }
    
    /**
     * JSF EL to publish the current Album.
     * @throws Exception 
     */
    public void publishAlbum() throws Exception
    {
    	try 
    	{
			albumController.publish(albumSession.getCurrent(), sessionBean.getUserHandle());
			
			if (albumSession.getCurrent().getVersion().getObjectId().equals(albumSession.getActive().getVersion().getObjectId())) 
			{
				albumSession.setActive(new AlbumVO());
			}
		} 
    	catch (Exception e) 
		{
			sessionBean.setMessage("Error publishing the album: " + e);
		}
    	redirect(navigation.getAlbumsUrl());
    }
    
    /**
     * JSL EL to withdraw an album
     * @throws Exception 
     */
    public void withdrawAlbum() throws Exception
    {
    	try 
    	{
    		albumController.withdraw(albumSession.getCurrent(), this.withdrawComment, sessionBean.getUserHandle());
		} 
    	catch (Exception e) 
		{
			sessionBean.setMessage("Error publishing the album: " + e);
		}
    	redirect(navigation.getAlbumsUrl());
    }
    
    /**
     * JSF Listener for the withdraw comment.
     */
    public void withdrawCommentListener(ValueChangeEvent event)
    {
    	if (event.getNewValue() != event.getOldValue()) 
    	{
			this.withdrawComment = event.getNewValue().toString();
		}
    }
    
    public void export() throws Exception
    {
         if (sessionBean.isAgreement())
         {
             albumSession.getExportManager().setUserHandle(sessionBean.getUserHandle());
             albumSession.getExportManager().setUserId(sessionBean.getUser().getReference().getObjectId());
             albumController.export(albumSession.getCurrent(), albumSession.getExportManager());
         }
         else
         {
             Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
             sessionBean.setMessage(sessionBean.getMessage("message_export_agreement"));
             FacesContext
                .getCurrentInstance()
                .getExternalContext()
                .redirect(navigation.getConfirmationUrl() + "/export/" + albumSession.getCurrent().getLatestVersion().getObjectId());
         }
         sessionBean.setAgreement(false);
    }
    
    /**
     * Converts a List of ItemVO to a List of their id as String
     * @param itemVOList
     * @return
     */
    private List<String> convertToListOfIdsAsString(List<ItemVO> itemVOList)
    {
    	List<String> list = new ArrayList<String>();
        
        for (int i = 0; i < itemVOList.size(); i++)
        {
            list.add(itemVOList.get(i).getItem().getObjid());
        }
        
        return list;
    }
    
    /**
     * Check if the album is the same as the active one. 
     * @param album
     * @return
     */
    private boolean isActiveAlbum(String objectId)
    {
        if (objectId.equals(albumSession.getActive().getVersion().getObjectId()))
        {
            return true;
        }
        return false;
    }
    
    /**
     * Check if the album is the same as the current one. 
     * @param album
     * @return
     */
    private boolean isCurrentAlbum(String objectId)
    {
        if (objectId.equals(albumSession.getCurrent().getVersion().getObjectId()))
        {
            return true;
        }
        return false;
    }
    
    public String getAlbumId() 
    {
		return albumId;
	}

	public void setAlbumId(String albumId) 
	{
		this.albumId = albumId;
	}

	public String getWithdrawComment() 
	{
		return withdrawComment;
	}

	public void setWithdrawComment(String withdrawComment) 
	{
		this.withdrawComment = withdrawComment;
	}
}


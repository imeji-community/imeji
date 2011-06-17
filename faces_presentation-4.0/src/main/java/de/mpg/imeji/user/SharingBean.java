package de.mpg.imeji.user;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Grant.GrantType;

public class SharingBean 
{
	private SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	private String email= null;
	private List<SelectItem> grantsMenu;
	private GrantType selectedGrant = GrantType.PRIVILEGED_VIEWER;
	private String status = "closed";
	
	public SharingBean() 
	{
		grantsMenu = new ArrayList<SelectItem>();
		grantsMenu.add(new SelectItem(GrantType.PRIVILEGED_VIEWER,  ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_viewer"), "Can view all images for this collection"));
		grantsMenu.add(new SelectItem(GrantType.CONTAINER_EDITOR,  ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_collection_editor"), "Can edit informations about the collection"));
		grantsMenu.add(new SelectItem(GrantType.IMAGE_EDITOR,  ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_image_editor"), "Can view and edit all images for this collection"));
		grantsMenu.add(new SelectItem(GrantType.PROFILE_EDITOR,  ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_profile_editor"), "Can edit the metadata profile"));
	}

	public List<SelectItem> getGrantsMenu() {
		return grantsMenu;
	}

	public void setGrantsMenu(List<SelectItem> grantsMenu) {
		this.grantsMenu = grantsMenu;
	}
	
	public String share(ActionEvent event)
	{
		String colId =  null;
		String albId = null;
		
		if (event.getComponent().getAttributes().get("collectionId") != null)
		{
			colId = event.getComponent().getAttributes().get("collectionId").toString();
		}
		else if (event.getComponent().getAttributes().get("albumId") != null)
		{
			albId = event.getComponent().getAttributes().get("albumId").toString();
		}

		SharingManager sm = new SharingManager();
		boolean shared = false;
		String message = "";
		
		if (colId != null)
		{
			if (!GrantType.PROFILE_EDITOR.equals(selectedGrant))
			{
				shared = sm.share(retrieveCollection(colId), sb.getUser(), email, selectedGrant);
				message = "Collection " + colId + " shared with " + email;
			}
			else
			{
				shared = sm.share(retrieveProfile(colId), sb.getUser(), email, selectedGrant);
				message = "Profile shared with " + email;
			}
		}
		else if (albId != null)
		{
			shared = sm.share(retrieveAlbum(albId), sb.getUser(), email, selectedGrant);
			message = "Album shared with " + email;
		}

		if (shared)
		{
			BeanHelper.info(message);
			cancel();
		}
		return "pretty:";
	}
	
	public String cancel()
	{
		status = "closed";
		return "";
	}
	
	public CollectionImeji retrieveCollection(String id)
	{
		CollectionController cl = new CollectionController(sb.getUser());
		try 
		{
			return cl.retrieve(URI.create(id));
		} 
		catch (Exception e) 
		{
			BeanHelper.error("Collection " + id + " not found!");
		}
		return null;
	}
	
	public MetadataProfile retrieveProfile(String collId)
	{
		CollectionImeji c = retrieveCollection(collId);
		ProfileController pc = new ProfileController(sb.getUser());
		try 
		{
			return pc.retrieve(c.getProfile());
		} 
		catch (Exception e) 
		{
			BeanHelper.error("Profile " + c.getProfile() + " not found!");
		}
		return null;
	}
	
	public Album retrieveAlbum(String albId)
	{
		AlbumController c = new AlbumController(sb.getUser());
		try 
		{
			return c.retrieve(albId);
		} 
		catch (Exception e) 
		{
			BeanHelper.error("Album " + albId + " not found!");
		}
		return null;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public GrantType getSelectedGrant() {
		return selectedGrant;
	}

	public void setSelectedGrant(GrantType selectedGrant) {
		this.selectedGrant = selectedGrant;
	}
	
	
	
	
	
}

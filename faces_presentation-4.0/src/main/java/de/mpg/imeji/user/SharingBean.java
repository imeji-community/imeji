package de.mpg.imeji.user;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ProfileController;
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
	
	public SharingBean() {
		grantsMenu = new ArrayList<SelectItem>();
		grantsMenu.add(new SelectItem(GrantType.PRIVILEGED_VIEWER, "Viewer", "Can view all images for this collection"));
		grantsMenu.add(new SelectItem(GrantType.IMAGE_EDITOR, "Image Editor", "Can view and edit all images for this collection"));
		grantsMenu.add(new SelectItem(GrantType.CONTAINER_EDITOR, "Collection Editor", "Can edit informations about the collection"));
		grantsMenu.add(new SelectItem(GrantType.PROFILE_EDITOR, "Profile Editor", "Can edit the metadata profile"));
	}

	public List<SelectItem> getGrantsMenu() {
		return grantsMenu;
	}

	public void setGrantsMenu(List<SelectItem> grantsMenu) {
		this.grantsMenu = grantsMenu;
	}
	
	public String share(ActionEvent event)
	{
		String id =  event.getComponent().getAttributes().get("collectionId").toString();
		SharingManager sm = new SharingManager();
		boolean shared = false;
		String message = "";
		
		if (!GrantType.PROFILE_EDITOR.equals(selectedGrant))
		{
			shared = sm.share(retrieveCollection(id), sb.getUser(), email, selectedGrant);
			message = "Collection " + id + " shared with " + email;
		}
		else
		{
			shared = sm.share(retrieveProfile(id), sb.getUser(), email, selectedGrant);
			message = "Profile shared with " + email;
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

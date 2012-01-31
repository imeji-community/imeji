/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.user;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.user.util.EmailClient;
import de.mpg.imeji.user.util.EmailMessages;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ObjectLoader;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.User;

public class SharingBean 
{
	private SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	private String email= null;
	private List<SelectItem> grantsMenu;
	private GrantType selectedGrant = GrantType.PRIVILEGED_VIEWER;
	private String status = "closed";
	private String colId = null;
	private static Logger logger = Logger.getLogger(SharingBean.class);

	public SharingBean() 
	{
		grantsMenu = new ArrayList<SelectItem>();
		grantsMenu.add(new SelectItem(GrantType.PRIVILEGED_VIEWER,  ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_viewer"), "Can view all images for this collection"));
		grantsMenu.add(new SelectItem(GrantType.CONTAINER_EDITOR,  ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_collection_editor"), "Can edit informations about the collection"));
		grantsMenu.add(new SelectItem(GrantType.IMAGE_EDITOR,  ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_image_editor"), "Can view and edit all images for this collection"));
		grantsMenu.add(new SelectItem(GrantType.PROFILE_EDITOR,  ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_profile_editor"), "Can edit the metadata profile"));
	}


	public String share(ActionEvent event)
	{
		String name = event.getComponent().getAttributes().get("name").toString();

		if (event.getComponent().getAttributes().get("collectionId") != null)
		{
			colId = event.getComponent().getAttributes().get("collectionId").toString();
			shareCollection(colId, name);
		}
		else if (event.getComponent().getAttributes().get("albumId") != null)
		{
			String albId = event.getComponent().getAttributes().get("albumId").toString();
			shareAlbum(albId, name);
		}

		return "";
	}

	private void shareCollection(String id, String name)
	{
		SharingManager sm = new SharingManager();
		boolean shared = false;
		String message = "";

		if (!GrantType.PROFILE_EDITOR.equals(selectedGrant))
		{
			shared = sm.share(retrieveCollection(id), sb.getUser(), email, selectedGrant, true);
			message = sb.getLabel("collection") + " " + id + " " + sb.getLabel("shared_with")+ " " + email + " " + sb.getLabel("as") + " " + selectedGrant.toString();
		}
		else
		{
			shared = sm.share(retrieveProfile(id), sb.getUser(), email, selectedGrant, true);
			shared =  sm.share(retrieveCollection(id), sb.getUser(), email, GrantType.PRIVILEGED_VIEWER, true);
			message = sb.getLabel("profile") + " " + id + " " + sb.getLabel("shared_with")+ " " + email+ " " + sb.getLabel("as") + " " + selectedGrant.toString();
		}

		if (shared)
		{
			User dest = ObjectLoader.loadUser(email, sb.getUser());
			
			EmailMessages emailMessages = new EmailMessages();

			sendEmail(dest, sb.getMessage("email_shared_collection_subject")
					, emailMessages.getSharedCollectionMessage(sb.getUser().getName(), dest.getName(), name, getRealLink(id)));

			BeanHelper.info(sb.getMessage("success_share"));
			BeanHelper.info(message);

			cancel();
		}
	}

	private void shareAlbum(String id, String name)
	{
		SharingManager sm = new SharingManager();
		boolean shared = false;
		String message = "";

		shared = sm.share(retrieveAlbum(id), sb.getUser(), email, selectedGrant, true);
		message = sb.getLabel("album") + " " + id + " " + sb.getLabel("shared_with")+ " " + email + " " + sb.getLabel("as") + " "  + selectedGrant.toString();

		if (shared)
		{
			User dest = ObjectLoader.loadUser(email, sb.getUser());

			EmailMessages emailMessages = new EmailMessages();

			sendEmail(dest, sb.getMessage("email_shared_album_subject")
					, emailMessages.getSharedAlbumMessage(sb.getUser().getName(), dest.getName(), name, getRealLink(id)));

			BeanHelper.info(sb.getMessage("success_share"));
			BeanHelper.info(message);
			cancel();
		}
	}

	private void sendEmail(User dest, String subject, String message)
	{
		EmailClient emailClient = new EmailClient();	

		try 
		{
			emailClient.sendMail(dest.getEmail(), null, subject ,message);
		} 
		catch (Exception e) 
		{
			logger.error("Error sending email", e);
			BeanHelper.error(sb.getMessage("error") + ": Email not sent");
		} 
	}

	private String getRealLink(String id)
	{
		Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
		return navigation.getApplicationUri() + URI.create(id).getPath();
	}

	public String cancel()
	{
		status = "closed";
		return "";
	}

	public CollectionImeji retrieveCollection(String id)
	{
		return ObjectLoader.loadCollection(URI.create(id), sb.getUser());
	}

	public MetadataProfile retrieveProfile(String collId)
	{
		return ObjectLoader.loadProfile(retrieveCollection(collId).getProfile(), sb.getUser());
	}

	public Album retrieveAlbum(String albId)
	{
		return ObjectLoader.loadAlbum(ObjectHelper.getURI(Album.class, albId), sb.getUser());
	}

	public String getStatus() 
	{
		return status;
	}

	public void setStatus(String status) 
	{
		this.status = status;
	}

	public String getEmail() 
	{
		return email;
	}

	public void setEmail(String email) 
	{
		this.email = email;
	}

	public GrantType getSelectedGrant() 
	{
		return selectedGrant;
	}

	public void setSelectedGrant(GrantType selectedGrant) 
	{
		this.selectedGrant = selectedGrant;
	}

	public String getColId()
	{
		return colId;
	}

	public List<SelectItem> getGrantsMenu() 
	{
		return grantsMenu;
	}

	public void setGrantsMenu(List<SelectItem> grantsMenu) 
	{
		this.grantsMenu = grantsMenu;
	}



}

/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.album;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.security.Authorization;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.image.ImageBean;
import de.mpg.imeji.presentation.user.SharingBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ObjectLoader;

public class AlbumBean implements Serializable
{
	private SessionBean sessionBean = null;
	private Album album = null;
	private String id = null;
	private int authorPosition;
	private int organizationPosition;
	private List<SelectItem> profilesMenu = new ArrayList<SelectItem>();
	private boolean active;
	private boolean save;
	private boolean selected;
	private static Logger logger = Logger.getLogger(AlbumBean.class);

	public AlbumBean(Album album)
	{
		this.album = album;
		sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class); 
		this.id = ObjectHelper.getId(album.getId());
		if(sessionBean.getActiveAlbum()!=null && sessionBean.getActiveAlbum().getAlbum().getId().equals(album.getId()))
		{
			active = true;
		}
	}

	public AlbumBean()
	{
		sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
	}

	public void initView()
	{
		try 
		{			
			if (id != null)
			{
				setAlbum(ObjectLoader.loadAlbum(ObjectHelper.getURI(Album.class, id), sessionBean.getUser()));

				if (getAlbum() != null)
				{
					if(sessionBean.getActiveAlbum()!=null && sessionBean.getActiveAlbum().equals(album.getId()))
					{
						active = true;
					}

					List<SelectItem> grantsMenu = new ArrayList<SelectItem>();
					grantsMenu.add(new SelectItem(GrantType.PRIVILEGED_VIEWER, ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_viewer"), "Can view all images for this collection"));
					grantsMenu.add(new SelectItem(GrantType.CONTAINER_EDITOR, ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("role_album_editor"), "Can edit informations about the collection"));
					((SharingBean)BeanHelper.getRequestBean(SharingBean.class)).setGrantsMenu(grantsMenu);
				}
			}
		} 
		catch (Exception e) 
		{
			logger.error("Error init album view", e);
		}
	}

	public void initEdit()
	{
		AlbumController ac = new AlbumController(sessionBean.getUser());

		try 
		{
			setAlbum(ac.retrieve(id));
			save=false;

			if(sessionBean.getActiveAlbum()!=null && sessionBean.getActiveAlbum().equals(album.getId()))
			{
				active = true;
			}
		} 
		catch (Exception e) 
		{
			BeanHelper.error(e.getMessage());
			logger.error("Error init album edit", e);
		}
	}

	public void initCreate()
	{
		setAlbum(new Album());
		getAlbum().getMetadata().setTitle("");
		getAlbum().getMetadata().setDescription("");
		getAlbum().getMetadata().getPersons().clear();
		getAlbum().getMetadata().getPersons().add(ImejiFactory.newPerson());
		save = true;
	}

	public boolean valid()
	{
		boolean valid = true;
		boolean hasAuthor = false;
		if (getAlbum().getMetadata().getTitle() == null || "".equals(getAlbum().getMetadata().getTitle()))
		{
			BeanHelper.error(sessionBean.getMessage("collection_create_error_title"));
			valid = false;
		}
		for (Person c : getAlbum().getMetadata().getPersons())
		{
			boolean hasOrganization = false;
			if (!"".equals(c.getFamilyName()))
			{
				hasAuthor = true;
			}
			for (Organization o : c.getOrganizations())
			{
				if (!"".equals(o.getName()) || "".equals(c.getFamilyName()))
				{
					hasOrganization = true;
				}
				if (hasOrganization && "".equals(c.getFamilyName()))
				{
					BeanHelper.error(sessionBean.getMessage("collection_create_error_family_name"));
					valid = false;
				}
			}
			if (!hasOrganization)
			{
				BeanHelper.error(sessionBean.getMessage("collection_create_error_organization"));
				valid = false;
			}
		}
		if (!hasAuthor)
		{
			BeanHelper.error(sessionBean.getMessage("collection_create_error_author"));
			valid = false;
		}
		return valid;
	}

	public String addAuthor()
	{
		List<Person> list = (List<Person>) getAlbum().getMetadata().getPersons(); 
		list.add(authorPosition + 1, ImejiFactory.newPerson());
		return "";
	}

	public String removeAuthor()
	{
		List<Person> list = (List<Person>) getAlbum().getMetadata().getPersons();
		if (list.size() > 1) list.remove(authorPosition);
		else BeanHelper.error(sessionBean.getMessage("error_album_need_one_author"));
		return "";
	}

	public String addOrganization()
	{
		Collection<Person> persons = getAlbum().getMetadata().getPersons();

		List<Organization> orgs = (List<Organization>) ((List<Person>) persons).get(authorPosition).getOrganizations();
		orgs.add(organizationPosition + 1, ImejiFactory.newOrganization());
		return "";
	}

	public String removeOrganization()
	{
		List<Person> persons = (List<Person>) getAlbum().getMetadata().getPersons();
		List<Organization> orgs = (List<Organization>) persons.get(authorPosition).getOrganizations();
		if (orgs.size() > 1) orgs.remove(organizationPosition);
		else BeanHelper.error(sessionBean.getMessage("error_author_need_one_organization"));
		return "";
	}

	protected String getNavigationString()
	{
		return "pretty:";
	}

	public int getAuthorPosition()
	{
		return authorPosition;
	}

	public void setAuthorPosition(int pos)
	{
		this.authorPosition = pos;
	}

	/**
	 * @return the collectionPosition
	 */
	public int getOrganizationPosition()
	{
		return organizationPosition;
	}

	/**
	 * @param collectionPosition the collectionPosition to set
	 */
	public void setOrganizationPosition(int organizationPosition)
	{
		this.organizationPosition = organizationPosition;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	public List<SelectItem> getProfilesMenu()
	{
		return profilesMenu;
	}

	public void setProfilesMenu(List<SelectItem> profilesMenu)
	{
		this.profilesMenu = profilesMenu;
	}

	public int getSize()
	{
		ItemController ic = new ItemController(sessionBean.getUser());
		if (album == null) return 0;
		return ic.countImagesInContainer(this.getAlbum().getId(), null);
	}

	public boolean getIsOwner()
	{
		if (sessionBean.getUser() != null)
		{
			return getAlbum().getProperties().getCreatedBy().equals(ObjectHelper.getURI(User.class, sessionBean.getUser().getEmail()));
		}
		else 
			return false;
	}


	public List<ImageBean> getImages() throws Exception
	{
		ItemController ic = new ItemController(sessionBean.getUser()); 
		if (getAlbum() != null)
		{
			SearchResult r = ic.searchImagesInContainer(getAlbum().getId(), null, null, 5, 0);
			return ImejiFactory.imageListToBeanList(ic.loadImages(r.getResults(), 5, 0));
		}
		return null;	
	}

	public String save() throws Exception
	{
		if(save)
		{ 
			AlbumController ac = new AlbumController(sessionBean.getUser());
			if (valid())
			{
				ac.create(getAlbum());
				UserController uc = new UserController(sessionBean.getUser());
				sessionBean.setUser(uc.retrieve(sessionBean.getUser().getEmail()));
				BeanHelper.info(sessionBean.getMessage("success_album_create"));
				return "pretty:albums";
			}
		}
		else
		{
			update();
		}

		return "";

	}

	public String update() throws Exception
	{
		AlbumController ac = new AlbumController(sessionBean.getUser());
		if (valid())
		{
			ac.update(getAlbum());
			BeanHelper.info(sessionBean.getMessage("success_album_update"));
			Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
			FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getApplicationUri() + getAlbum().getId().getPath() + "/details?init=1");
		}
		return "";
	}

	public void setAlbum(Album album)
	{
		this.album = album;
	}

	public Album getAlbum()
	{
		return album; 
	}

	public String getPersonString()
	{
		String personString = "";
		for (Person p : album.getMetadata().getPersons())
		{
			personString += p.getFamilyName() + ", " + p.getGivenName();
		}
		return personString;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public boolean getActive()
	{
		return active;
	}

	public String makeActive()
	{
		sessionBean.setActiveAlbum(this); 
		this.setActive(true);
		return "pretty:";
	}

	public String makeInactive()
	{
		sessionBean.setActiveAlbum(null);
		this.setActive(false);
		return "pretty:";
	}

	public String release() 
	{
		makeInactive();
		AlbumController ac = new AlbumController(sessionBean.getUser());
		try 
		{
			ac.release(album);
			BeanHelper.info(sessionBean.getMessage("success_album_release"));
		} 
		catch (Exception e) 
		{
			BeanHelper.error(sessionBean.getMessage("error_album_release"));
			BeanHelper.error(e.getMessage());
			e.printStackTrace();
		}
		return "pretty:";
	}

	public String delete()
	{
		AlbumController c = new AlbumController(sessionBean.getUser());

		try 
		{
			makeInactive();
			c.delete(album, sessionBean.getUser());
			BeanHelper.info(sessionBean.getMessage("success_album_delete"));
		} 
		catch (Exception e) 
		{
			BeanHelper.error(sessionBean.getMessage("error_album_delete"));
			BeanHelper.error(e.getMessage());
			e.printStackTrace();
		}

		return "pretty:albums";
	}

	public String withdraw() throws Exception
	{
		AlbumController c = new AlbumController(sessionBean.getUser());

		try 
		{
			c.withdraw(album);
			BeanHelper.info(sessionBean.getMessage("success_album_withdraw"));
		} 
		catch (Exception e) 
		{
			BeanHelper.error(sessionBean.getMessage("error_album_withdraw"));
			BeanHelper.error(e.getMessage());
			e.printStackTrace();
		}

		return "pretty:";
	}

	public boolean getSelected() 
	{
		if(sessionBean.getSelectedAlbums().contains(album.getId()))
			selected = true;
		else
			selected = false;
		return selected;
	}

	public void setSelected(boolean selected) 
	{
		if(selected)
		{	
			if(!(sessionBean.getSelectedAlbums().contains(album.getId())))
				sessionBean.getSelectedAlbums().add(album.getId());
		}
		else
			sessionBean.getSelectedAlbums().remove(album.getId());
		this.selected = selected;
	}

	public boolean isEditable() 
	{
		Security security = new Security();
		return security.check(OperationsType.UPDATE, sessionBean.getUser(), album);
	}

	public boolean isVisible() 
	{
		Security security = new Security();
		return security.check(OperationsType.READ, sessionBean.getUser(), album);
	}

	public boolean isDeletable() 
	{
		Security security = new Security();
		return security.check(OperationsType.DELETE, sessionBean.getUser(), album);
	}

	public boolean isAdmin()
	{
		Authorization auth = new Authorization();
		return auth.isContainerAdmin(sessionBean.getUser(), album);
	}

}

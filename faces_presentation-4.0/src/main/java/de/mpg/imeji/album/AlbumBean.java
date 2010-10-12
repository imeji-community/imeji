package de.mpg.imeji.album;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import thewebsemantic.JenaHelper;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;

import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.CollectionController;

import de.mpg.jena.controller.ImageController;

import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.Image;

import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;
import de.mpg.jena.vo.User;

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

		public AlbumBean(Album album)
        {
            this.setAlbum(album);
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
            AlbumController ac = new AlbumController(sessionBean.getUser()); 
            setAlbum(ac.retrieve(id));
            if(sessionBean.getActiveAlbum()!=null && sessionBean.getActiveAlbum().equals(album.getId()))
            {
                active = true;
            }
        }
        
        public void initEdit()
        {
            AlbumController ac = new AlbumController(sessionBean.getUser());
            setAlbum(ac.retrieve(id));
            save=false;
            if(sessionBean.getActiveAlbum()!=null && sessionBean.getActiveAlbum().equals(album.getId()))
            {
                active = true;
            }
        }
        
        public void initCreate()
        {
            setAlbum(new Album());
            getAlbum().getMetadata().setTitle("");
            getAlbum().getMetadata().setDescription("");
            getAlbum().getMetadata().getPersons().clear();
            getAlbum().getMetadata().getPersons().add(ImejiFactory.newPerson());
            save=true;
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
            List<Person> list = getAlbum().getMetadata().getPersons(); 
            list.add(authorPosition + 1, ImejiFactory.newPerson());
            return "";
        }

        public String removeAuthor()
        {
           
                List<Person> list = getAlbum().getMetadata().getPersons();
                list.remove(authorPosition);
            return "";
        }

        public String addOrganization()
        {
            List<Person> persons = getAlbum().getMetadata().getPersons();
            List<Organization> orgs = persons.get(authorPosition).getOrganizations();
            orgs.add(organizationPosition + 1, ImejiFactory.newOrganization());
            return "";
        }

        public String removeOrganization()
        {
            
                List<Person> persons = getAlbum().getMetadata().getPersons();
                List<Organization> orgs = persons.get(authorPosition).getOrganizations();
                orgs.remove(organizationPosition);
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
            return getAlbum().getImages().size();
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

        /*
        public String release() throws Exception
        {
            CollectionController cc = new CollectionController(sessionBean.getUser());
            cc.release(collection);
            return "pretty:";
        }
        */
        public List<ImageBean> getImages() throws Exception
        {
            ImageController ic = new ImageController(sessionBean.getUser()); 
            
            Collection<Image> imgList = ic.searchImageInContainer(getAlbum().getId(), null, null, 5, 0);
            return ImejiFactory.imageListToBeanList(imgList); 
        }
        
        public String save() throws Exception
        {
            if(save)
            { 
                AlbumController ac = new AlbumController(sessionBean.getUser());
                if (valid())
                {
                    ac.create(getAlbum());
                    BeanHelper.info("Album created successfully");
                }
            }
            else
            {
                update();
            }
            
            return "pretty:albums";
           
        }
        
        public String update() throws Exception
        {
            AlbumController ac = new AlbumController(sessionBean.getUser());
            if (valid())
            {
                ac.update(getAlbum());
                BeanHelper.info("Album updated successfully");
            }
            return "pretty:albums";
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
        
        public String release() throws Exception
        {
            AlbumController ac = new AlbumController(sessionBean.getUser());
            ac.release(album);
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
       
    

}

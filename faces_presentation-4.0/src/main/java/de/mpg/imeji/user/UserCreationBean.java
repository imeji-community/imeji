package de.mpg.imeji.user;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import thewebsemantic.NotFoundException;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.GrantController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.security.Authorization;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.Properties.Status;
import de.mpg.jena.vo.User;

public class UserCreationBean
{
    private User user;
    private String password;
    private String repeatedPassword;
    private SessionBean sb;
    
    private Logger logger = Logger.getLogger(UserCreationBean.class);
    
    public UserCreationBean()
    {
    	sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
    	this.setUser(new User());
    }
    
    public String create() throws Exception
    {
        UserController uc = new UserController(sb.getUser());
        String regexEmailMatch ="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        
        if (!user.getEmail().matches(regexEmailMatch))
        {
            BeanHelper.error(sb.getMessage("error_user_email_not_valid"));
        }
        else if(!getPassword().equals(getRepeatedPassword()))
        {
        	BeanHelper.error(sb.getMessage("error_user_repeat_password"));
        }
        else
        {
            try
            {
                if (uc.retrieve(user.getEmail()) != null)
                {
                	BeanHelper.error(sb.getMessage("error_user_already_exists"));
                }
            }
            catch (NotFoundException e) 
            {
            	user.setEncryptedPassword(UserController.convertToMD5(getPassword()));
                uc.create(user);
                logger.info("USER email created: " + user.getEmail());
                BeanHelper.info(sb.getMessage("success_user_create"));
			}
            catch (Exception e) 
            {
            	BeanHelper.error(sb.getMessage("error") + ": " + e);
			}
        }
        return "pretty:";
    }
    
    public List<User> getAllUsers() throws IllegalArgumentException, IllegalAccessException
    {
    	List<User> users = new ArrayList<User>();
    	UserController uc = new UserController(user);
    	users.addAll(uc.retrieveAll());
    	for(User u : users)
    	{
    		u = (User) ObjectHelper.castAllHashSetToList(u);
    	}
    	return users;
    }
    
    public List<CollectionImeji> getAllcollections()
    {
    	CollectionController cc = new CollectionController(sb.getUser());
    	return (List<CollectionImeji>) cc.retrieveAll();
    }
    
    public List<Album> getAllAlbums()
    {
    	AlbumController ac = new AlbumController(sb.getUser());
    	return (List<Album>) ac.retrieveAll();
    }
    
    public int getAllAlbumsSize()
    {
    	return this.getAllAlbums().size();
    }
    
    public int getAllCollectionsSize()
    {
    	return getAllcollections().size();
    }
    
    public int getAllImagesSize()
    {
    	ImageController ic = new ImageController(sb.getUser());
    	return ic.allImagesSize();
    }
    
    public int getAllUsersSize() 
    {
    	try { return this.getAllUsers().size(); }
		catch (Exception e) { return 0; }
    }
    
    public String transformMd() throws Exception
    {
    	ImageController ic = new ImageController(sb.getUser());
    	CollectionController cc = new CollectionController(sb.getUser());
    	Collection<Image> allImages = ic.retrieveAll();
    	for(Image im : allImages) {
    		ObjectHelper.castAllHashSetToList(im);
    		CollectionImeji col = cc.retrieve(im.getCollection());
    		im.getProperties().setStatus(col.getProperties().getStatus());
    	}
    	ic.update(allImages);
    	return "";
    }
    
    public String cleanContainers() throws Exception
    {
    	UserController uc = new UserController(sb.getUser());
    	CollectionController cc = new CollectionController(sb.getUser());
    	ImageController ic = new ImageController(sb.getUser());
    	Collection<CollectionImeji> cols = cc.retrieveAll();
    	for (CollectionImeji c : cols)
    	{
//    		c = (CollectionImeji) ObjectHelper.castAllHashSetToList(c);
//    		for (int i=0; i < ((List<URI>)c.getImages()).size(); i++)
//    		{
//    			try{ ic.retrieve(((List<URI>)c.getImages()).get(i));}
//    			catch (NotFoundException e) {
//    				((List<URI>)c.getImages()).remove(i);
//    				i--;
//				}
//    		}
//    		cc.update(c);
    		
    		// SET CORRECT STATUS TO EXISTING PROFILE
    		
    		User user = uc.retrieve(c.getProperties().getCreatedBy());
    		ProfileController pc1 = new ProfileController(user);
    		if (c.getProperties().getStatus().equals(Status.RELEASED))
    		{
    			pc1.release(pc1.retrieve(c.getProfile()));
    		}
    		
    	}
    	
    	ProfileController pc = new ProfileController(sb.getUser());
    	List<MetadataProfile> profiles = pc.retrieveAll();
    	
    	// REMOVE PROFILES WITHOUT A COLLECTION 
    	// TOO DANGEROUS TRY LATER
    	for(MetadataProfile mdp : profiles)
    	{
    		boolean found = false;
    		for (CollectionImeji c : cols)
    		{
    			if(c.getProfile().equals(mdp.getId()))
    			{
    				found = true;
    			}
    		}
    		if (!found)
    		{
    			pc.delete(mdp, sb.getUser());
    		}
    	}
    	
    	// REMOVE NON EXISTING GRANTS
    	Collection<User> users = uc.retrieveAll();
    	
    	for(User u : users)
    	{
    		List<Grant> deadGrants = new ArrayList<Grant>();
    		for (Grant g : u.getGrants())
    		{
    			boolean found = false;
    			for(MetadataProfile mdp : profiles)
    			{
    				if (mdp.getId().equals(g.getGrantFor()))
    				{
    					found = true;
    				}
    			}
    			for(CollectionImeji c : cols)
    			{
    				if (c.getId().equals(g.getGrantFor()))
    				{
    					found = true;
    				}
    			}
    			
    			if(!found && g.getGrantFor()!= null && !g.getGrantFor().toString().contains("album"))
    			{
    				deadGrants.add(g);
    			}
    		}
    		
    		for (Grant g : deadGrants) 
    		{
    			GrantController gc = new GrantController(sb.getUser());
				gc.removeGrant(u, g);
			}
    	}
    		
    	
    	return "";
    }
    
    public String reInitializeUserRights() throws Exception
    {
    	List<CollectionImeji> allColls = getAllcollections();
    	List<Album> allAlbs = getAllAlbums();
    	UserController uc = new UserController(sb.getUser());
    	Authorization authorization = new Authorization();
    	GrantController gc = new GrantController(sb.getUser());
    	List<User> allUsers = getAllUsers();
    	for (User u : allUsers)
    	{
    		if (!authorization.isSysAdmin(u))
    		{
	    		List<Grant> grants = new ArrayList<Grant>();
	    		grants.addAll(u.getGrants());
    			for (Grant g: grants)
	    		{
	    			gc.removeGrant(u, g);
	    		}
	    		for (CollectionImeji c : allColls)
	    		{
	    			String creatorEMail= uc.retrieve(c.getProperties().getCreatedBy()).getEmail();
	    			if (u.getEmail().equals(creatorEMail))
	    			{
	    				gc.addGrant(u, new Grant(GrantType.CONTAINER_ADMIN, c.getId()));
	    				gc.addGrant(u, new Grant(GrantType.PROFILE_ADMIN, c.getProfile()));
	    			}
	    		}
	    		for (Album a : allAlbs)
	    		{
	    			String creatorEMail= uc.retrieve(a.getProperties().getCreatedBy()).getEmail();
	    			if (u.getEmail().equals(creatorEMail))
	    			{
	    				gc.addGrant(u, new Grant(GrantType.CONTAINER_ADMIN, a.getId()));
	    			}
	    		}
    		}
    	}
    	logger.info("All Users grants reniitialized");
    	return "pretty:";
    }
    
    public String removeDeadGrants() throws Exception
    {
    	Authorization authorization = new Authorization();
    	GrantController gc = new GrantController(sb.getUser());
    	List<User> allUsers = getAllUsers();
    	
    	CollectionController cc = new CollectionController(sb.getUser());
    	AlbumController ac = new AlbumController(sb.getUser());
    	ProfileController pc = new ProfileController(sb.getUser());
    	
    	for (User u : allUsers)
    	{
    		if (!authorization.isSysAdmin(u))
    		{
	    		List<Grant> grants = new ArrayList<Grant>();
	    		grants.addAll(u.getGrants());
    			for (Grant g: grants)
	    		{
    				boolean exists = false;
    				try{
    					cc.retrieve(g.getGrantFor());
    					exists =true;
    				}
    				catch (Exception e) {
						// Not FOUND
					}
    				try{
    					ac.retrieve(g.getGrantFor());
    					exists =true;
    				}
    				catch (Exception e) {
						// notFOUND
					}
    				try{
    					pc.retrieve(g.getGrantFor());
    					exists =true;
    				}
    				catch (Exception e) {
						// Not found
					}
    				if (!exists)
    				{
    					logger.info("deleting " + g.getGrantFor());
    					gc.removeGrant(u, g);
    					
    				}
	    			
	    		}
    		}
    	}
    	return "pretty:";
    }
    

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

    public void setRepeatedPassword(String repeatedPassword)
    {
        this.repeatedPassword = repeatedPassword;
    }

    public String getRepeatedPassword()
    {
        return repeatedPassword;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }
    
    public boolean isSysAdmin()
    {
    	return sb.isAdmin();
    }
}

package de.mpg.imeji.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import thewebsemantic.NotFoundException;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.GrantController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.security.Authorization;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Grant.GrantType;
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
            BeanHelper.error("The email is not valid!");
        }
        else if(!getPassword().equals(getRepeatedPassword()))
        {
            BeanHelper.error("Password and repeated password are not the same");
        }
        else
        {
            try
            {
                uc.retrieve(user.getEmail());
                BeanHelper.error("User already exists!");
            }
            catch (NotFoundException e)
            {
                user.setEncryptedPassword(UserController.convertToMD5(getPassword()));
                uc.create(user);
                BeanHelper.error("User created successfully");
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
	    				gc.addGrant(u, new Grant(GrantType.PROFILE_ADMIN, c.getProfile().getId()));
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

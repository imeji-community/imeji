package de.mpg.imeji.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import thewebsemantic.NotFoundException;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.user.util.EmailClient;
import de.mpg.imeji.user.util.EmailMessages;
import de.mpg.imeji.user.util.PasswordGenerator;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.User;

public class UserCreationBean
{
    private User user;
    private SessionBean sb;
    private boolean sendEmail = false;
    
    private static Logger logger = Logger.getLogger(UserCreationBean.class);
    
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
            	PasswordGenerator generator = new PasswordGenerator();
            	String password = generator.generatePassword();
            	
            	user.setEncryptedPassword(UserController.convertToMD5(password));
                uc.create(user);
                
                if (sendEmail) 
                {	
                	 sendNewAccountEmail(password);
				}
                
                logger.info("New user created: " + user.getEmail());
                BeanHelper.info(sb.getMessage("success_user_create"));
			}
            catch (Exception e) 
            {
            	BeanHelper.error(sb.getMessage("error") + ": " + e);
			}
        }
        return "pretty:";
    }
    
    public void sendNewAccountEmail(String password)
    {
    	EmailClient emailClient = new EmailClient();
    	EmailMessages emailMessages = new EmailMessages();
    	
        try 
        {
			emailClient.sendMail(user.getEmail(), null, sb.getMessage("email_new_user_subject")
					, emailMessages.getNewAccountMessage(password, user.getEmail(), user.getName()));
		} 
        catch (Exception e) 
		{
        	logger.error("Error sending email", e);
        	BeanHelper.error(sb.getMessage("error") + ": Email not sent");
		} 
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

	public boolean isSendEmail() 
	{
		return sendEmail;
	}

	public void setSendEmail(boolean sendEmail) 
	{
		this.sendEmail = sendEmail;
	}
    
}

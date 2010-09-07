package de.mpg.imeji.user;

import thewebsemantic.NotFoundException;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.vo.User;

public class UserCreationBean
{
    private User user;
    
    private String password;
    private String repeatedPassword;
    
    public UserCreationBean()
    {
        this.setUser(new User());
    }
    
    
    public String create() throws Exception
    {
        UserController uc = new UserController(null);
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
    
    
    
    
}

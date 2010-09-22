package de.mpg.imeji.init;

import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import thewebsemantic.NotFoundException;

import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.User;
import de.mpg.jena.vo.Grant.GrantType;

/**
 * Initialize application on server start
 * @author saquet
 *
 */
public class InitializerServlet extends HttpServlet
{
	private static Logger logger = Logger.getLogger(InitializerServlet.class);
    
	@Override
    public void init() throws ServletException
    {
        super.init();
        try
        {
            createSysadminUser();
            createTestUser();
        }
        catch (Exception e)
        {
            logger.error("Could not create sysadmin user", e);
        }
       
    }
	
	public void createTestUser() throws Exception
    {
	    
        UserController uc = new UserController(null);
        try
        {
            User u = uc.retrieve("imeji@mpdl.mpg.de");
            logger.info("Test User " + u.getEmail() + " already exists!");
        }
        catch (NotFoundException e)
        {
            User user = new User();
            user.setEmail("imeji@mpdl.mpg.de");
            user.setName("Imeji Test User");
            user.setNick("itu");
            user.setEncryptedPassword(UserController.convertToMD5("test"));
            user.getGrants().add(new Grant(GrantType.CONTAINER_ADMIN, URI.create("http://test.de")));
            uc.create(user);
            logger.info("Created test user successfully");
        }
        
        
    }
    
	public void createSysadminUser() throws Exception
    {
	    if(PropertyReader.getProperty("imeji.sysadmin.email")!=null)
	    {
	        UserController uc = new UserController(null);
	        User user = new User();
	        user.setEmail(PropertyReader.getProperty("imeji.sysadmin.email"));
	        user.setName("Imeji Sysadmin");
	        user.setNick("sysadmin");
	        user.setEncryptedPassword(UserController.convertToMD5(PropertyReader.getProperty("imeji.sysadmin.password")));
	        user.getGrants().add(new Grant(GrantType.SYSADMIN, null));
	        uc.create(user);
	        logger.info("Created sysadmin successfully");
	    }
	   
    }
  
    public void destroy()
    {
        super.destroy();
    }
    
}

/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.init;

import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.ImejiJena;
import de.mpg.imeji.logic.concurrency.locks.LocksSurveyor;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.exceptions.AlreadyExistsException;
import de.mpg.j2j.exceptions.NotFoundException;

/**
 * Initialize application on server start
 * 
 * @author saquet
 */
public class InitializerServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(InitializerServlet.class);

    @Override
    public void init() throws ServletException
    {
        super.init();
        initModel();
        startLocksSurveyor();
        createSysadminUser();
        // EscidocInitializer escidocInitializer = new EscidocInitializer();
        // escidocInitializer.run();
    }

    public void initModel()
    {
        try
        {
            ImejiJena.init();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error Initializing model: ", e);
        }
    }

    public void startLocksSurveyor()
    {
        LocksSurveyor locksSurveyor = new LocksSurveyor();
        locksSurveyor.start();
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
            user.setName("imeji Test User");
            user.setNick("itu");
            user.setEncryptedPassword(StringHelper.convertToMD5("test"));
            user.getGrants().add(new Grant(GrantType.CONTAINER_ADMIN, URI.create("http://test.de")));
            uc.create(user);
            logger.info("Created test user successfully");
        }
    }

    public void createSysadminUser()
    {
        try
        {
            UserController uc = new UserController(ImejiJena.adminUser);
            uc.create(ImejiJena.adminUser);
            logger.info("Created sysadmin successfully");
        }
        catch (AlreadyExistsException e)
        {
            logger.info("sysadmin already exists 1");
        }
        catch (Exception e)
        {
            if (e.getCause() instanceof AlreadyExistsException)
            {
                logger.info("sysadmin already exists 2");
            }
            else
            {
                throw new RuntimeException("Error initializing Admin user! ", e);
            }
        }
    }

    @Override
    public void destroy()
    {
        super.destroy();
    }
}

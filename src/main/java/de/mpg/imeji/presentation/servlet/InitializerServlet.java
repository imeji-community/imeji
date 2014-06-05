/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;

import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.tdb.sys.TDBMaker;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.concurrency.locks.LocksSurveyor;
import de.mpg.imeji.logic.controller.GrantController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.util.IdentifierUtil;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.PropertyBean;
import de.mpg.j2j.exceptions.AlreadyExistsException;
import de.mpg.j2j.exceptions.NotFoundException;

/**
 * Initialize application on server start
 * 
 * @author saquet
 */
public class InitializerServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private final static Logger logger = Logger.getLogger(InitializerServlet.class);
    /**
     * The {@link Thread} which controls the locking in imeji
     */
    private LocksSurveyor locksSurveyor = new LocksSurveyor();

    @Override
    public void init() throws ServletException
    {
        super.init();
        new PropertyBean();
        initModel();
        startLocksSurveyor();
        createSysadminUser();
    }

    /**
     * Initialize the imeji jena tdb
     */
    public void initModel()
    {
        try
        {
            Imeji.init();
            runMigration();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error Initializing model: ", e);
        }
    }

    /**
     * Start thread {@link LocksSurveyor}
     */
    private void startLocksSurveyor()
    {
        locksSurveyor.start();
    }

    /**
     * Create the imeji system user if it doesn't exists
     */
    private void createSysadminUser()
    {
        try
        {
            UserController uc = new UserController(Imeji.adminUser);
            List<User> admins = uc.retrieveAllAdmins();
            if (admins.size() == 0)
            {
                try
                {
                    User admin = uc.retrieve(Imeji.adminUser.getEmail());
                    logger.info("Add admin grant to admin@imeji.org user");
                    GrantController gc = new GrantController();
                    gc.addGrants(admin, AuthorizationPredefinedRoles.imejiAdministrator(admin.getId().toString()),
                            admin);
                }
                catch (NotFoundException e)
                {
                    logger.info("!!! IMPORTANT !!! Create admin@imeji.org as system administrator with password admin. !!! CHANGE PASSWORD !!!");
                    uc.create(Imeji.adminUser);
                    logger.info("Created admin user successfully:" + Imeji.adminUser.getEmail());
                }
            }
            else
            {
                logger.info("Admin user already exists:");
                for (User admin : admins)
                {
                    logger.info(admin.getEmail() + " is admin + (" + admin.getId() + ")");
                }
            }
        }
        catch (AlreadyExistsException e)
        {
            logger.warn(Imeji.adminUser.getEmail() + " already exists");
        }
        catch (Exception e)
        {
            if (e.getCause() instanceof AlreadyExistsException)
            {
                logger.warn(Imeji.adminUser.getEmail() + " already exists");
            }
            else
            {
                throw new RuntimeException("Error initializing Admin user! ", e);
            }
        }
    }

    /**
     * look to the migration File (migration.txt)
     * 
     * @throws IOException
     */
    private void runMigration() throws IOException
    {
        File f = new File(Imeji.tdbPath + StringHelper.urlSeparator + "migration.txt");
        FileInputStream in = null;
        try
        {
            in = new FileInputStream(f);
        }
        catch (FileNotFoundException e)
        {
            logger.info("No " + f.getAbsolutePath() + " found, no migration runs");
        }
        if (in != null)
        {
            String migrationRequests = new String(StreamUtils.getBytes(in), "UTF-8");
            migrationRequests = migrationRequests.replaceAll("XXX_BASE_URI_XXX", PropertyBean.baseURI());
            migrationRequests = addNewIdToMigration(migrationRequests);
            logger.info("Running migration with query: ");
            logger.info(migrationRequests);
            ImejiSPARQL.execUpdate(migrationRequests);
            logger.info("Migration done!");
        }
    }

    private String addNewIdToMigration(String migrationRequests)
    {
        Pattern p = Pattern.compile("XXX_NEW_ID_XXX");
        Matcher m = p.matcher(migrationRequests);
        StringBuffer sb = new StringBuffer();
        while (m.find())
        {
            m.appendReplacement(sb, IdentifierUtil.newId());
        }
        m.appendTail(sb);
        return sb.toString();
    }

    @Override
    public void destroy()
    {
        logger.info("Shutting down imeji!");
        logger.info("Make Garbage collector!");
        System.gc();
        System.runFinalization();
        logger.info("Shutting down thread executor...");
        Imeji.executor.shutdown();
        logger.info("executor shutdown status: " + Imeji.executor.isShutdown());
        logger.info("Closing Jena TDB...");
        TDB.sync(Imeji.dataset);
        Imeji.dataset.close();
        TDB.closedown();
        TDBMaker.releaseLocation(new Location(Imeji.tdbPath));
        logger.info("...done");
        logger.info("Ending LockSurveyor...");
        locksSurveyor.terminate();
        logger.info("...done");
        super.destroy();
        logger.info("imeji is down");
        System.exit(0);
    }
}

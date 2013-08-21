/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;

import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.tdb.sys.TDBMaker;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.concurrency.locks.LocksSurveyor;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.j2j.exceptions.AlreadyExistsException;

/**
 * Initialize application on server start
 * 
 * @author saquet
 */
public class InitializerServlet extends HttpServlet
{
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(InitializerServlet.class);
    /**
     * The {@link Thread} which controls the locking in imeji
     */
    private LocksSurveyor locksSurveyor = new LocksSurveyor();

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
            uc.create(Imeji.adminUser);
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
            logger.info("No" + f.getAbsolutePath() + " found, no migration runs");
        }
        if (in != null)
        {
            String migrationRequests = new String(StreamUtils.getBytes(in), "UTF-8");
            logger.info("Running migration with query: ");
            logger.info(migrationRequests);
            ImejiSPARQL.execUpdate(migrationRequests);
            logger.info("Migration done!");
        }
    }

    @Override
    public void destroy()
    {
        logger.info("Shutting down imeji!");
        Imeji.executor.shutdown();
        logger.info("Closing Jena TDB...");
        Imeji.imejiDataSet.end();
        TDB.sync(Imeji.imejiDataSet);
        Imeji.imejiDataSet.close();
        TDB.closedown();
        TDBMaker.releaseLocation(new Location(Imeji.tdbPath));
        logger.info("...done");
        logger.info("Ending LockSurveyor...");
        locksSurveyor.terminate();
        logger.info("...done");
        System.runFinalization();
        super.destroy();
        logger.info("imeji is down");
    }
}

package de.mpg.imeji.init;

import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.sparql.sse.Item;
import com.hp.hpl.jena.tdb.solver.stats.StatsCollector;
import com.hp.hpl.jena.tdb.store.GraphTDB;

import thewebsemantic.NotFoundException;
import de.mpg.imeji.util.PropertyReader;
import de.mpg.jena.ImejiJena;
import de.mpg.jena.concurrency.locks.LocksSurveyor;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.vo.Grant;
import de.mpg.jena.vo.Grant.GrantType;
import de.mpg.jena.vo.User;

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
		initModel();
		startLocksSurveyor();
		createSysadminUser();
		//        EscidocInitializer escidocInitializer = new EscidocInitializer();
		//		escidocInitializer.run();
	}

	public void initModel()
	{
		try
		{
			ImejiJena.init();            
			//			String tdbPath = PropertyReader.getProperty("imeji.tdb.path");
			//            Model base = DataFactory.model(tdbPath);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error Initializing model: " +e);
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
			user.setName("Imeji Test User");
			user.setNick("itu");
			user.setEncryptedPassword(UserController.convertToMD5("test"));
			user.getGrants().add(new Grant(GrantType.CONTAINER_ADMIN, URI.create("http://test.de")));
			uc.create(user);
			logger.info("Created test user successfully");
		}
	}

	public void createSysadminUser()
	{
		try
		{
			if(PropertyReader.getProperty("imeji.sysadmin.email") != null)
			{
				User user = new User();
				user.setEmail(PropertyReader.getProperty("imeji.sysadmin.email"));
				user.setName("Imeji Sysadmin");
				user.setNick("sysadmin");
				user.setEncryptedPassword(UserController.convertToMD5(PropertyReader.getProperty("imeji.sysadmin.password")));
				user.getGrants().add(new Grant(GrantType.SYSADMIN, null));
				UserController uc = new UserController(user);
				uc.create(user);
				logger.info("Created sysadmin successfully");
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException("Error initializing Admin user! " + e);
		}

	}

	public void destroy()
	{
		super.destroy();
	}

}

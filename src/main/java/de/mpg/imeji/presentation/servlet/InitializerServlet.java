/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.jena.atlas.lib.AlarmClock;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;

import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.tdb.sys.TDBMaker;

import de.mpg.imeji.exceptions.AlreadyExistsException;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.auth.authorization.AuthorizationPredefinedRoles;
import de.mpg.imeji.logic.concurrency.locks.LocksSurveyor;
import de.mpg.imeji.logic.controller.ShareController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserController.USER_TYPE;
import de.mpg.imeji.logic.util.IdentifierUtil;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.ConfigurationBean;
import de.mpg.imeji.presentation.beans.PropertyBean;

/**
 * Initialize application on server start
 * 
 * @author saquet
 */
public class InitializerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger
			.getLogger(InitializerServlet.class);
	/**
	 * The {@link Thread} which controls the locking in imeji
	 */
	private LocksSurveyor locksSurveyor = new LocksSurveyor();

	@Override
	public void init() throws ServletException {
		super.init();
		new PropertyBean();
		try {
			new ConfigurationBean();
		} catch (IOException | URISyntaxException e) {
			logger.error("Error reading Configuration", e);
		}
		startLocksSurveyor();
		initModel();
	}

	/**
	 * Initialize the imeji jena tdb
	 */
	public void initModel() {
		try {
			Imeji.init();
			runMigration();
		} catch (Exception e) {
			throw new RuntimeException("Error Initializing model: ", e);
		}
	}

	/**
	 * Start thread {@link LocksSurveyor}
	 */
	private void startLocksSurveyor() {
		locksSurveyor.start();
	}

	/**
	 * look to the migration File (migration.txt)
	 * 
	 * @throws IOException
	 */
	private void runMigration() throws IOException {
		File f = new File(Imeji.tdbPath + StringHelper.urlSeparator
				+ "migration.txt");
		FileInputStream in = null;
		try {
			in = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			logger.info("No " + f.getAbsolutePath()
					+ " found, no migration runs");
		}
		if (in != null) {
			String migrationRequests = new String(StreamUtils.getBytes(in),
					"UTF-8");
			migrationRequests = migrationRequests.replaceAll(
					"XXX_BASE_URI_XXX", PropertyBean.baseURI());
			migrationRequests = addNewIdToMigration(migrationRequests);
			logger.info("Running migration with query: ");
			logger.info(migrationRequests);
			ImejiSPARQL.execUpdate(migrationRequests);
			logger.info("Migration done!");
		}
	}

	private String addNewIdToMigration(String migrationRequests) {
		Pattern p = Pattern.compile("XXX_NEW_ID_XXX");
		Matcher m = p.matcher(migrationRequests);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, IdentifierUtil.newId());
		}
		m.appendTail(sb);
		return sb.toString();
	}

	@Override
	public void destroy() {

		logger.info("Shutting down imeji!");

		logger.info("Shutting down thread executor...");
		Imeji.executor.shutdown();
		logger.info("executor shutdown status: " + Imeji.executor.isShutdown());

		logger.info("Closing Jena! TDB...");
		TDB.sync(Imeji.dataset);
		Imeji.dataset.close();
		TDB.closedown();
		TDBMaker.releaseLocation(new Location(Imeji.tdbPath));
		logger.info("...done");

		// This is a bug of com.hp.hpl.jena.sparql.engine.QueryExecutionBase
		// implementation:
		// AlarmClock is not correctly released, it leads to the memory leaks
		// after tomcat stop
		// see https://github.com/imeji-community/imeji/issues/966!
		logger.info("Release AlarmClock...");
		AlarmClock alarmClock = AlarmClock.get();
		alarmClock.release();
		logger.info("done");

		logger.info("Ending LockSurveyor...");
		locksSurveyor.terminate();
		logger.info("...done");

		logger.info("imeji is down");

		super.destroy();

	}

}

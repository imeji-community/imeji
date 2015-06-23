package de.mpg.imeji.logic.jobs;

import java.awt.Dimension;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.storage.internal.InternalStorageManager;
import de.mpg.imeji.logic.storage.util.ImageUtils;
import de.mpg.imeji.logic.vo.Item;

/**
 * Job which read all Items, read for each {@link Item} the size of the original
 * File, and write the items in Jena back with the file size;
 * 
 * @author saquet
 *
 */
public class CleanInactiveUsersJob implements Callable<Integer> {
	private static Logger logger = Logger.getLogger(CleanInactiveUsersJob.class);

	@Override
	public Integer call() {
		logger.info("Starting Cleaning of Inactive Users");
		UserController userController = new UserController(Imeji.adminUser);
		int numCleaned= userController.cleanInactiveUsers();
		logger.info("Cleaning of inactive users successfully finished !"+numCleaned+" inactive users have been deleted!");
		return 1;
	}
}

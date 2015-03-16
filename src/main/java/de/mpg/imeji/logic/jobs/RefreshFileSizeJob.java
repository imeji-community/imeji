package de.mpg.imeji.logic.jobs;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.storage.internal.InternalStorageManager;
import de.mpg.imeji.logic.vo.Item;

/**
 * Job which read all Items, read for each {@link Item} the size of the original
 * File, and write the items in Jena back with the file size;
 * 
 * @author saquet
 *
 */
public class RefreshFileSizeJob implements Callable<Integer> {
	private static Logger logger = Logger.getLogger(RefreshFileSizeJob.class);


	@Override
	public Integer call() throws Exception {
		logger.info("Starting refreshing the file size of all Items");
		ItemController itemController = new ItemController();
		InternalStorageManager storageManager = new InternalStorageManager();
		logger.info("Retrieving all items...");
		Collection<Item> items = itemController.retrieveAll(Imeji.adminUser);
		logger.info("...done (found  " + items.size() + ")");
		logger.info("Reading the original file of each item:");
		int count = 1;
		for (Item item : items) {
			logger.info(count + "/" + items.size());
			String path = storageManager.transformUrlToPath(item
					.getFullImageUrl().toString());
			File f = new File(path);
			item.setFileSize(f.length());
			count++;
		}
		logger.info("Updating the Items with the file size...");
		try {
			itemController.update(items, Imeji.adminUser);
		} catch (ImejiException e) {
			logger.error(e);
		}
		logger.info("...done");
		return 1;
	}
}

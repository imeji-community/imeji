/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.admin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.rdf.model.Resource;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.jobs.ImportFileFromEscidocToInternalStorageJob;
import de.mpg.imeji.logic.jobs.RefreshFileSizeJob;
import de.mpg.imeji.logic.jobs.StorageUsageAnalyseJob;
import de.mpg.imeji.logic.reader.ReaderFacade;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.SearchFactory;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.storage.Storage;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.administrator.StorageAdministrator;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.writer.WriterFacade;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.j2j.annotations.j2jId;

/**
 * Bean for the administration page. Methods working on data
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class AdminBean {
	private SessionBean sb;
	private static Logger logger = Logger.getLogger(AdminBean.class);
	private boolean clean = false;
	private String numberOfFilesInStorage;
	private String sizeOfFilesinStorage;
	private String freeSpaceInStorage;
	private String lastUpdateStorageStatistics;
	private Future<Integer> storageAnalyseStatus;
	private String cleanDatabaseReport = "";

	public AdminBean() throws IOException, URISyntaxException {

		sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		StorageUsageAnalyseJob storageUsageAnalyse;
		storageUsageAnalyse = new StorageUsageAnalyseJob();
		this.numberOfFilesInStorage = Integer.toString(storageUsageAnalyse
				.getNumberOfFiles());
		this.sizeOfFilesinStorage = FileUtils
				.byteCountToDisplaySize(storageUsageAnalyse.getStorageUsed());
		this.freeSpaceInStorage = FileUtils
				.byteCountToDisplaySize(storageUsageAnalyse.getFreeSpace());
		this.lastUpdateStorageStatistics = storageUsageAnalyse.getLastUpdate();
	}

	/**
	 * Refresh the file size of all items
	 * 
	 * @return
	 */
	public String refreshFileSize() {
		Imeji.executor.submit(new RefreshFileSizeJob());
		return "";
	}

	/**
	 * Clean the {@link Storage}
	 * 
	 * @return
	 */
	public String cleanStorage() {
		StorageController controller = new StorageController();
		controller.getAdministrator().clean();
		return "pretty:";
	}

	/**
	 * Return the location of the internal storage
	 * 
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String getInternalStorageLocation() throws IOException,
			URISyntaxException {
		return PropertyReader.getProperty("imeji.storage.path");
	}

	/**
	 * Make the same as clean, but doesn't remove the resources
	 * 
	 * @throws Exception
	 */
	public void status() throws Exception {
		clean = false;
		invokeCleanMethods();
	}

	/**
	 * Here are called all methods related to data cleaning
	 * 
	 * @throws Exception
	 */
	public void clean() throws Exception {
		clean = true;
		invokeCleanMethods();
	}

	/**
	 * Start the job {@link StorageUsageAnalyseJob}
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public String analyseStorageUsage() throws IOException, URISyntaxException {
		storageAnalyseStatus = Imeji.executor
				.submit(new StorageUsageAnalyseJob());
		return "";
	}

	/**
	 * Import the files in an external storage (for instance escidoc) into the
	 * internal storage
	 * 
	 * @throws Exception
	 */
	public String importToInternalStorage() {
		Imeji.executor.submit(new ImportFileFromEscidocToInternalStorageJob(sb
				.getUser()));
		return "";
	}

	/**
	 * Invoke all clean methods available
	 * 
	 * @throws Exception
	 */
	private void invokeCleanMethods() throws Exception {
		cleanStatement();
		/*
		 * TODO Clean Metadata not working: the metadata is not completely
		 * removed. All element in the metadata are removed, but the metadata it
		 * self not. Since a metadata is a abstract class, j2j can not instance
		 * a new metadata since it doesn't know the type
		 */
		cleanMetadata();
		cleanGrants();
	}

	/**
	 * Find all {@link Metadata} which are not related to a {@link Statement}
	 */
	private void cleanMetadata() {
		Search search = SearchFactory.create();
		List<String> uris = search.searchSimpleForQuery(
				SPARQLQueries.selectMetadataUnbounded()).getResults();
		cleanDatabaseReport += "Metadata Without Statement: " + uris.size()
				+ " found  <br/> ";
	}

	/**
	 * Clean {@link Statement} which are not bound a {@link MetadataProfile}
	 * 
	 * @throws Exception
	 */
	private void cleanStatement() throws Exception {
		Search search = SearchFactory.create();
		List<String> uris = search.searchSimpleForQuery(
				SPARQLQueries.selectStatementUnbounded()).getResults();
		logger.info("...found " + uris.size());
		cleanDatabaseReport += "Unbounded Statements: " + uris.size()
				+ " found  <br/> ";
		removeResources(uris, Imeji.profileModel, new Statement());
	}

	/**
	 * Clean grants which are not related to a user
	 * 
	 * @throws Exception
	 */
	private void cleanGrants() throws Exception {
		logger.info("Searching not bounded grants...");
		Search search = SearchFactory.create();
		List<String> uris = search.searchSimpleForQuery(
				SPARQLQueries.selectGrantWithoutUser()).getResults();
		cleanDatabaseReport += "Unbounded Grants: " + uris.size()
				+ " found  <br/>";
		removeResources(uris, Imeji.userModel, new Grant());
		uris = search.searchSimpleForQuery(SPARQLQueries.selectGrantBroken())
				.getResults();
		cleanDatabaseReport += "Broken Grants: " + uris.size() + " found <br/>";
		removeResources(uris, Imeji.userModel, new Grant());
		logger.info("Searching emtpy grants...");
		if (clean)
			ImejiSPARQL.execUpdate(SPARQLQueries.removeGrantEmtpy());
		uris = search.searchSimpleForQuery(SPARQLQueries.selectGrantEmtpy())
				.getResults();
		cleanDatabaseReport += "Empty Grants: " + uris.size() + " found  <br/>";
	}

	/**
	 * Remove Exception a {@link List} of {@link Resource}
	 * 
	 * @param uris
	 * @param modelName
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws Exception
	 */
	private synchronized void removeResources(List<String> uris,
			String modelName, Object obj) throws InstantiationException,
			IllegalAccessException, Exception {
		removeObjects(loadResourcesAsObjects(uris, modelName, obj), modelName);
	}

	/**
	 * Load the {@link Resource} as {@link Object}
	 * 
	 * @param uris
	 * @param modelName
	 * @param obj
	 * @return
	 */
	private List<Object> loadResourcesAsObjects(List<String> uris,
			String modelName, Object obj) {
		ReaderFacade reader = new ReaderFacade(modelName);
		List<Object> l = new ArrayList<Object>();
		for (String uri : uris) {
			try {
				logger.info("Resource to be removed: " + uri);
				l.add(reader.read(uri, sb.getUser(), obj.getClass()
						.newInstance()));
			} catch (Exception e) {
				logger.error("ERROR LOADING RESOURCE " + uri + " !!!!!", e);
			}
		}
		return l;
	}

	/**
	 * Remove an {@link Object}, it must have a {@link j2jId}
	 * 
	 * @param l
	 * @param modelName
	 * @throws Exception
	 */
	private void removeObjects(List<Object> l, String modelName)
			throws Exception {
		if (clean) {
			WriterFacade writer = new WriterFacade(modelName);
			writer.delete(l, sb.getUser());
		}
	}

	/**
	 * return count of all {@link Album}
	 * 
	 * @return
	 */
	public int getAllAlbumsSize() {
		Search search = SearchFactory.create(SearchType.ALBUM);
		return search.searchSimpleForQuery(SPARQLQueries.selectAlbumAll())
				.getNumberOfRecords();
	}

	/**
	 * return count of all {@link CollectionImeji}
	 * 
	 * @return
	 */
	public int getAllCollectionsSize() {
		Search search = SearchFactory.create(SearchType.COLLECTION);
		return search.searchSimpleForQuery(SPARQLQueries.selectCollectionAll())
				.getNumberOfRecords();
	}

	/**
	 * return count of all {@link Item}
	 * 
	 * @return
	 */
	public int getAllImagesSize() {
		Search search = SearchFactory.create(SearchType.ITEM);
		return search.searchSimpleForQuery(SPARQLQueries.selectItemAll())
				.getNumberOfRecords();
	}

	/**
	 * True if the current {@link Storage} has implemted a
	 * {@link StorageAdministrator}
	 * 
	 * @return
	 */
	public boolean isAdministrate() {
		StorageController sc = new StorageController();
		return sc.getAdministrator() != null;
	}

	/**
	 * Return all {@link User}
	 * 
	 * @return
	 */
	public List<User> getAllUsers() {
		UserController uc = new UserController(Imeji.adminUser);
		return (List<User>) uc.searchUserByName("");
	}

	/**
	 * return count of all {@link User}
	 * 
	 * @return
	 */
	public int getAllUsersSize() {
		try {
			return this.getAllUsers().size();
		} catch (Exception e) {
			return 0;
		}
	}

	public String getNumberOfFilesInStorage() {
		return numberOfFilesInStorage;
	}

	public String getSizeOfFilesinStorage() {
		return sizeOfFilesinStorage;
	}

	public String getFreeSpaceInStorage() {
		return freeSpaceInStorage;
	}

	public String getLastUpdateStorageStatistics() {
		return lastUpdateStorageStatistics;
	}

	public boolean getStorageAnalyseStatus() {
		if (storageAnalyseStatus != null)
			return storageAnalyseStatus.isDone();
		return true;
	}

	public String getCleanDatabaseReport() {
		return cleanDatabaseReport;
	}
}

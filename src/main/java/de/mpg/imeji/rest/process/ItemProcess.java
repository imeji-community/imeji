package de.mpg.imeji.rest.process;

import static com.google.common.base.Strings.isNullOrEmpty;
import static javax.ws.rs.core.Response.Status.OK;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchObjectTypes;
import de.mpg.imeji.logic.search.jenasearch.JenaCustomQueries;
import de.mpg.imeji.logic.search.jenasearch.JenaSearch;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.util.TempFileUtil;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.DefaultItemService;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.JSONResponse;
import de.mpg.imeji.rest.to.MetadataProfileTO;
import de.mpg.imeji.rest.to.defaultItemTO.DefaultItemTO;
import de.mpg.imeji.rest.to.defaultItemTO.DefaultItemWithFileTO;

/**
 * Process the REST requests for Items
 * 
 * @author bastiens
 *
 */
public class ItemProcess {
  private static final Logger LOGGER = LoggerFactory.getLogger(ItemProcess.class);
  public static final String METADATA_KEY = "metadata";

  /**
   * Create an Item with its file
   * 
   * @param req
   * @param file
   * @param json
   * @param origName
   * @return
   */
  public static JSONResponse createItem(HttpServletRequest req, InputStream file, String json,
      String origName) {
    try {
      User u = BasicAuthentication.auth(req);
      DefaultItemWithFileTO defaultItemWithFileTO = (DefaultItemWithFileTO) RestProcessUtils
          .buildTOFromJSON(json, DefaultItemWithFileTO.class);
      defaultItemWithFileTO = uploadAndValidateFile(file, defaultItemWithFileTO, origName);
      DefaultItemTO defaultItemTO =
          new DefaultItemService().create((DefaultItemTO) defaultItemWithFileTO, u);
      return RestProcessUtils.buildResponse(Status.CREATED.getStatusCode(), defaultItemTO);
    } catch (Exception e) {
      LOGGER.error("Error creating item", e);
      return RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
  }

  /**
   * Read an Item according to its Id
   * 
   * @param req
   * @param id
   * @return
   */
  public static JSONResponse readItem(HttpServletRequest req, String id) {
    DefaultItemService service = new DefaultItemService();
    try {
      User u = BasicAuthentication.auth(req);
      return RestProcessUtils.buildResponse(Status.OK.getStatusCode(), service.read(id, u));
    } catch (Exception e) {
      return RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
  }

  /**
   * Delete an Item according to its id
   * 
   * @param req
   * @param id
   * @return
   */
  public static JSONResponse deleteItem(HttpServletRequest req, String id) {
    DefaultItemService service = new DefaultItemService();
    try {
      User u = BasicAuthentication.auth(req);
      service.delete(id, u);
      return RestProcessUtils.buildResponse(Status.NO_CONTENT.getStatusCode(), null);
    } catch (Exception e) {
      return RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
  }

  /**
   * Read Items according to the query
   * 
   * @param req
   * @param q
   * @return
   */
  public static JSONResponse readItems(HttpServletRequest req, String q, int offset, int size) {
    DefaultItemService service = new DefaultItemService();
    try {
      User u = BasicAuthentication.auth(req);
      return RestProcessUtils.buildResponse(OK.getStatusCode(),
          service.searchAndRetrieveItems(u, q, offset, size));
    } catch (Exception e) {
      return RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
  }



  /**
   * Update an Item, update file if provided
   * 
   * @param req
   * @param id
   * @param fileInputStream
   * @param json
   * @param filename
   * @return
   * @throws BadRequestException
   */
  public static JSONResponse updateItem(HttpServletRequest req, String id,
      InputStream fileInputStream, String json, String filename) {
    try {
      DefaultItemService service = new DefaultItemService();
      DefaultItemWithFileTO to = (DefaultItemWithFileTO) RestProcessUtils.buildTOFromJSON(json,
          DefaultItemWithFileTO.class);
      validateId(id, to);
      to.setId(id);
      boolean fileUpdate = !isNullOrEmpty(json) && (fileInputStream != null
          || json.indexOf("fetchUrl") > 0 || json.indexOf("referenceUrl") > 0);
      User u = BasicAuthentication.auth(req);
      if (fileUpdate) {
        to = uploadAndValidateFile(fileInputStream, (DefaultItemWithFileTO) to, filename);
      }
      return RestProcessUtils.buildResponse(Status.OK.getStatusCode(), service.update(to, u));
    } catch (Exception e) {
      e.printStackTrace();
      return RestProcessUtils.localExceptionHandler(e, e.getMessage());
    }
  }

  private static void validateId(String id, DefaultItemTO to) throws BadRequestException {
    if (!isNullOrEmpty(id) && !isNullOrEmpty(to.getId()) && !id.equals(to.getId())) {
      throw new BadRequestException(
          "Ambiguous item id: <" + id + "> in path; <" + to.getId() + "> in JSON");
    }
  }

  private static void validateId(String id, ItemTO to) throws BadRequestException {
    if (!isNullOrEmpty(id) && !isNullOrEmpty(to.getId()) && !id.equals(to.getId())) {
      throw new BadRequestException(
          "Ambiguous item id: <" + id + "> in path; <" + to.getId() + "> in JSON");
    }
  }

  //
  // private static ItemWithFileTO uploadAndValidateFile(InputStream file, ItemWithFileTO to,
  // String origName) throws IOException, UnprocessableError, BadRequestException {
  // if (file != null) {
  // String calculatedFilename = (!isNullOrEmpty(to.getFilename())) ? to.getFilename()
  // : (to.getFilename() == null ? origName : to.getFilename());
  // String calculatedExtension = FilenameUtils.getExtension(calculatedFilename);
  // calculatedExtension = !isNullOrEmpty(calculatedExtension) ? "." + calculatedExtension : null;
  //
  // // Note: createTempFile suffix must be provided in order not to rename the file to .tmp
  // File tmp = TempFileUtil.createTempFile("imejiAPI", calculatedExtension);
  // IOUtils.copy(file, new FileOutputStream(tmp));
  //
  // StorageController c = new StorageController();
  // // convenience call to stop the processing here asap. Item controller checks it anyway
  // String guessNotAllowedFormatUploaded = c.guessNotAllowedFormat(tmp);
  // if (StorageUtils.BAD_FORMAT.equals(guessNotAllowedFormatUploaded)) {
  // throw new UnprocessableError(
  // "upload_format_not_allowed: " + " (" + calculatedExtension + ")");
  // }
  // to.setFile(tmp);
  // to.setFilename(calculatedFilename);
  //
  // if (to.getFile() == null && isNullOrEmpty(to.getFetchUrl())
  // && isNullOrEmpty(to.getReferenceUrl())) {
  // throw new BadRequestException(
  // "A file must be uploaded, referenced or fetched from external location.");
  // }
  // }
  // return to;
  //
  // }

  /**
   * Upload the File to imeji, and return a valid {@link DefaultItemWithFileTO}
   * 
   * @param file
   * @param to
   * @param origName
   * @return
   * @throws IOException
   * @throws UnprocessableError
   * @throws BadRequestException
   */
  private static DefaultItemWithFileTO uploadAndValidateFile(InputStream file,
      DefaultItemWithFileTO to, String origName)
          throws IOException, UnprocessableError, BadRequestException {
    if (file != null) {
      String calculatedFilename =
          StringHelper.isNullOrEmptyTrim(to.getFilename()) ? origName : to.getFilename();
      String calculatedExtension = FilenameUtils.getExtension(calculatedFilename);
      calculatedExtension = !isNullOrEmpty(calculatedExtension) ? "." + calculatedExtension : null;
      // Note: createTempFile suffix must be provided in order not to rename the file to .tmp
      File tmp = TempFileUtil.createTempFile("imejiAPI", calculatedExtension);
      IOUtils.copy(file, new FileOutputStream(tmp));
      StorageController c = new StorageController();
      // convenience call to stop the processing here asap. Item controller checks it anyway
      String guessNotAllowedFormatUploaded = c.guessNotAllowedFormat(tmp);
      if (StorageUtils.BAD_FORMAT.equals(guessNotAllowedFormatUploaded)) {
        throw new UnprocessableError(
            "upload_format_not_allowed: " + " (" + calculatedExtension + ")");
      }
      to.setFile(tmp);
      to.setFilename(calculatedFilename);

      if (to.getFile() == null && isNullOrEmpty(to.getFetchUrl())
          && isNullOrEmpty(to.getReferenceUrl())) {
        throw new BadRequestException(
            "A file must be uploaded, referenced or fetched from external location.");
      }
    }
    return to;

  }


  private static MetadataProfileTO getMetadataProfileTO(ItemTO to, User u) throws ImejiException {
    Search s = new JenaSearch(SearchObjectTypes.ALL, null);
    String query = null;
    // First collectionId has to be queried, else tests with false item ID fail
    // for default Item
    // Logic stays same, only order of queries to Jena has been switched
    if (to.getCollectionId() != null) {
      query = JenaCustomQueries.selectProfileIdOfCollection(
          ObjectHelper.getURI(CollectionImeji.class, to.getCollectionId()).toString());
    } else if (to.getId() != null) {
      query = JenaCustomQueries
          .selectProfileIdOfItem(ObjectHelper.getURI(Item.class, to.getId()).toString());
    }
    if (query != null) {
      List<String> r = s.searchString(query, null, null, 0, -1).getResults();
      if (!r.isEmpty()) {
        return new ProfileService().read(ObjectHelper.getId(URI.create(r.get(0))), u);
      }
    }
    throw new UnprocessableError("Item's profile not found");
  }

  /**
   * TODO Remove Parse a DEfault JSON as ItemTO
   * 
   * @param fileUpdate
   * @param json
   * @param to
   * @param id
   * @param u
   * @return
   * @throws IOException
   * @throws UnprocessableError
   * @throws ImejiException
   */
  // public static ItemTO prepareDefaultItemTOAsItemTO(boolean fileUpdate, String json, ItemTO to,
  // String id, User u) throws IOException, UnprocessableError, ImejiException {
  // // extract metadata node
  // LinkedHashMap<String, Object> itemMap = (LinkedHashMap<String, Object>) jsonToPOJO(json);
  // LinkedHashMap<String, Object> metadata =
  // (LinkedHashMap<String, Object>) itemMap.remove(METADATA_KEY);
  // // parse as normal ItemTO
  // to = fileUpdate
  // ? (ItemWithFileTO) RestProcessUtils.buildTOFromJSON(buildJSONFromObject(itemMap),
  // ItemWithFileTO.class)
  // : (ItemTO) RestProcessUtils.buildTOFromJSON(buildJSONFromObject(itemMap), ItemTO.class);
  //
  // validateId(id, to);
  // to.setId(id);
  // // update metadata part
  // DefaultItemTO defaultTO = (DefaultItemTO) buildTOFromJSON(
  // "{\"" + METADATA_KEY + "\":" + buildJSONFromObject(metadata) + "}", DefaultItemTO.class);
  //
  // ReverseTransferObjectFactory.transferDefaultMetadata2(getMetadataProfileTO(to, u), defaultTO,
  // to, true);
  //
  // return to;
  // }

}

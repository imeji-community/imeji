package de.mpg.imeji.rest.process;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.rest.process.CommonUtils.USER_MUST_BE_LOGGED_IN;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildJSONAndExceptionResponse;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildJSONFromObject;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildResponse;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildTOFromJSON;
import static de.mpg.imeji.rest.process.RestProcessUtils.jsonToPOJO;
import static de.mpg.imeji.rest.process.RestProcessUtils.localExceptionHandler;
import static de.mpg.imeji.rest.to.ItemTO.SYNTAX.RAW;
import static de.mpg.imeji.rest.to.ItemTO.SYNTAX.guessType;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.Search.SearchType;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.TempFileUtil;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.defaultTO.DefaultItemTO;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.rest.to.JSONResponse;
import de.mpg.imeji.rest.to.MetadataProfileTO;

public class ItemProcess {


  private static final Logger LOGGER = LoggerFactory.getLogger(ItemProcess.class);
  public static final String METADATA_KEY = "metadata";

  public static JSONResponse deleteItem(HttpServletRequest req, String id) {
    User u = BasicAuthentication.auth(req);
    JSONResponse resp;

    ItemService icrud = new ItemService();
    try {
      icrud.delete(id, u);
      resp = RestProcessUtils.buildResponse(Status.NO_CONTENT.getStatusCode(), null);
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }


  public static JSONResponse readDefaultItem(HttpServletRequest req, String id) {
    User u = BasicAuthentication.auth(req);
    JSONResponse resp;

    ItemService icrud = new ItemService();
    try {
      resp = RestProcessUtils.buildResponse(Status.OK.getStatusCode(), icrud.readDefault(id, u));
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;

  }

  public static JSONResponse readItem(HttpServletRequest req, String id) {
    User u = BasicAuthentication.auth(req);
    JSONResponse resp;

    ItemService icrud = new ItemService();
    try {
      resp = RestProcessUtils.buildResponse(Status.OK.getStatusCode(), icrud.read(id, u));
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;

  }

  public static JSONResponse readItems(HttpServletRequest req, String q) {
    JSONResponse resp;

    User u = BasicAuthentication.auth(req);

    ItemService is = new ItemService();
    try {
      resp = RestProcessUtils.buildResponse(OK.getStatusCode(), is.readItems(u, q));
    } catch (Exception e) {
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
    }
    return resp;
  }

  public static JSONResponse createItem(HttpServletRequest req, InputStream file, String json,
      String syntax, String origName) {
    // / write response
    JSONResponse resp;

    // Load User (if provided)
    User u = BasicAuthentication.auth(req);

    // Parse json into to
    ItemWithFileTO itemTO = null;
    ItemTO.SYNTAX SYNTAX_TYPE = guessType(syntax);
    try {

      switch (SYNTAX_TYPE) {
        case RAW:
          itemTO = (ItemWithFileTO) RestProcessUtils.buildTOFromJSON(json, ItemWithFileTO.class);
          break;
        case DEFAULT:
          // extract metadata node
          Map<String, Object> itemMap = jsonToPOJO(json);
          HashMap<String, Object> metadata =
              (LinkedHashMap<String, Object>) itemMap.remove(METADATA_KEY);
          // parse as normal ItemTO
          itemTO =
              (ItemWithFileTO) RestProcessUtils.buildTOFromJSON(buildJSONFromObject(itemMap),
                  ItemWithFileTO.class);
          // update metadata part
          DefaultItemTO easyTO =
              (DefaultItemTO) buildTOFromJSON("{\"" + METADATA_KEY + "\":"
                  + buildJSONFromObject(metadata) + "}", DefaultItemTO.class);
          ReverseTransferObjectFactory.transferDefaultItemTOtoItemTO(
              getMetadataProfileTO(itemTO, u), easyTO, itemTO);
          break;
        default:
          throw new BadRequestException("Bad syntax type: " + syntax);
      }

      if (file != null) {
        itemTO = uploadAndValidateFile(file, itemTO, origName);
      }

    } catch (Exception e) {
      return RestProcessUtils.localExceptionHandler(e, e.getMessage());
    }

    // create item with the file
    ItemService is = new ItemService();
    try {
      ItemTO createdItem = is.create(itemTO, u);
      resp =
          RestProcessUtils.buildResponse(Status.CREATED.getStatusCode(),
              SYNTAX_TYPE == RAW ? createdItem : is.readDefault(createdItem.getId(), u));

    } catch (Exception e) {
      // System.out.println("MESSAGE= "+e.getLocalizedMessage());
      resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());

    }

    return resp;
  }

  private static MetadataProfileTO getMetadataProfileTO(ItemTO to, User u) throws ImejiException {
    Search s = new SPARQLSearch(SearchType.ITEM, null);
    List<String> r =
        s.searchSimpleForQuery(
            SPARQLQueries.selectProfileIdOfItem(ObjectHelper.getURI(Item.class, to.getId())
                .toString())).getResults();
    if (!r.isEmpty()) {
      return new ProfileService().read(ObjectHelper.getId(URI.create(r.get(0))), u);
    }
    throw new UnprocessableError("Item's profile not found");
  }

  public static JSONResponse easyUpdateItem(HttpServletRequest req, String id) throws IOException {
    JSONResponse resp = null;
    User u = BasicAuthentication.auth(req);
    if (u == null) {
      resp = buildJSONAndExceptionResponse(UNAUTHORIZED.getStatusCode(), USER_MUST_BE_LOGGED_IN);
    } else {
      try {
        ItemService icrud = new ItemService();
        DefaultItemTO defaultTO = (DefaultItemTO) buildTOFromJSON(req, DefaultItemTO.class);
        ItemTO itemTO = (ItemTO) icrud.read(id, u);
        ReverseTransferObjectFactory.transferDefaultItemTOtoItemTO(getMetadataProfileTO(itemTO, u),
            defaultTO, itemTO);

        icrud.update(itemTO, u);

        resp = buildResponse(Status.OK.getStatusCode(), icrud.readDefault(itemTO.getId(), u));
      } catch (ImejiException e) {
        resp = localExceptionHandler(e, e.getLocalizedMessage());
      }
    }
    return resp;
  }


  public static JSONResponse updateItem(HttpServletRequest req, String id,
      InputStream fileInputStream, String json, String filename, String syntax) {
    User u = BasicAuthentication.auth(req);

    ItemService service = new ItemService();
    ItemTO to = new ItemTO();

    boolean fileUpdate =
        !isNullOrEmpty(json)
            && (fileInputStream != null || json.indexOf("fetchUrl") > 0 || json
                .indexOf("referenceUrl") > 0);
    ItemTO.SYNTAX SYNTAX_TYPE = guessType(syntax);

    JSONResponse response;

    try {
      if (SYNTAX_TYPE == null) {
        throw new BadRequestException("Bad syntax type: " + syntax);
      }
      switch (SYNTAX_TYPE) {
        case RAW:
          to =
              fileUpdate ? (ItemWithFileTO) RestProcessUtils.buildTOFromJSON(json,
                  ItemWithFileTO.class) : (ItemTO) RestProcessUtils.buildTOFromJSON(json,
                  ItemTO.class);

          validateId(id, to);
          to.setId(id);
          break;
        case DEFAULT:
          // extract metadata node
          Map<String, Object> itemMap = jsonToPOJO(json);
          HashMap<String, Object> metadata =
              (LinkedHashMap<String, Object>) itemMap.remove(METADATA_KEY);
          // parse as normal ItemTO
          to =
              fileUpdate ? (ItemWithFileTO) RestProcessUtils.buildTOFromJSON(
                  buildJSONFromObject(itemMap), ItemWithFileTO.class) : (ItemTO) RestProcessUtils
                  .buildTOFromJSON(buildJSONFromObject(itemMap), ItemTO.class);

          validateId(id, to);
          to.setId(id);

          // update metadata part
          DefaultItemTO easyTO =
              (DefaultItemTO) buildTOFromJSON("{\"" + METADATA_KEY + "\":"
                  + buildJSONFromObject(metadata) + "}", DefaultItemTO.class);
          ReverseTransferObjectFactory.transferDefaultItemTOtoItemTO(getMetadataProfileTO(to, u),
              easyTO, to);
          break;
      }

      if (fileUpdate) {
        to = uploadAndValidateFile(fileInputStream, (ItemWithFileTO) to, filename);
      }

      service.update(to, u);

      response =
          RestProcessUtils.buildResponse(Status.OK.getStatusCode(), SYNTAX_TYPE == RAW ? to
              : service.readDefault(to.getId(), u));

      return response;
    } catch (Exception e) {
      return RestProcessUtils.localExceptionHandler(e, e.getMessage());
    }
  }

  private static void validateId(String id, ItemTO to) throws BadRequestException {
    if (!isNullOrEmpty(id) && !isNullOrEmpty(to.getId()) && !id.equals(to.getId())) {
      throw new BadRequestException("Ambiguous item id: <" + id + "> in path; <" + to.getId()
          + "> in JSON");
    }
  }


  private static ItemWithFileTO uploadAndValidateFile(InputStream file, ItemWithFileTO to,
      String origName) throws IOException, UnprocessableError, BadRequestException {
    if (file != null) {
      String calculatedFilename =
          (!isNullOrEmpty(to.getFilename())) ? to.getFilename()
              : (to.getFilename() == null ? origName : to.getFilename());
      String calculatedExtension = FilenameUtils.getExtension(calculatedFilename);
      calculatedExtension = !isNullOrEmpty(calculatedExtension) ? "." + calculatedExtension : null;

      // Note: createTempFile suffix must be provided in order not to rename the file to .tmp
      File tmp = TempFileUtil.createTempFile("imejiAPI", calculatedExtension);
      IOUtils.copy(file, new FileOutputStream(tmp));

      StorageController c = new StorageController();
      // convenience call to stop the processing here asap. Item controller checks it anyway
      String guessNotAllowedFormatUploaded = c.guessNotAllowedFormat(tmp);
      if (StorageUtils.BAD_FORMAT.equals(guessNotAllowedFormatUploaded)) {
        throw new UnprocessableError("upload_format_not_allowed: " + " (" + calculatedExtension
            + ")");
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

}

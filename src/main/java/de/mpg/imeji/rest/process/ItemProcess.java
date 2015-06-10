package de.mpg.imeji.rest.process;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.rest.process.CommonUtils.USER_MUST_BE_LOGGED_IN;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildJSONAndExceptionResponse;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildResponse;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildTOFromJSON;
import static de.mpg.imeji.rest.process.RestProcessUtils.localExceptionHandler;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.storage.StorageController;
import de.mpg.imeji.logic.storage.util.StorageUtils;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.defaultTO.DefaultItemTO;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.rest.to.JSONResponse;
import de.mpg.imeji.rest.to.MetadataProfileTO;

public class ItemProcess {


	private static final Logger LOGGER = LoggerFactory.getLogger(ItemProcess.class);
	
	public static JSONResponse deleteItem(HttpServletRequest req, String id) {
		User u = BasicAuthentication.auth(req);
		JSONResponse resp; 

		ItemService icrud = new ItemService();
			try {
				icrud.delete(id, u);
				resp= RestProcessUtils.buildResponse(Status.NO_CONTENT.getStatusCode(), null);
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
			resp= RestProcessUtils.buildResponse(Status.OK.getStatusCode(), icrud.readDefault(id, u));
		} catch (Exception e) {
			resp= RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
		}
		return resp;

	}
	
	public static JSONResponse readItem(HttpServletRequest req, String id) {
		User u = BasicAuthentication.auth(req);
		JSONResponse resp; 

		ItemService icrud = new ItemService();
		try {
			resp= RestProcessUtils.buildResponse(Status.OK.getStatusCode(), icrud.read(id, u));
		} catch (Exception e) {
			resp= RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
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

	public static JSONResponse createItem(HttpServletRequest req,
			InputStream file, String json, String origName) {
		// / write response
		JSONResponse resp; 

		// Load User (if provided)
		User u = BasicAuthentication.auth(req);
		
		// Parse json into to
		ItemWithFileTO to = null;
		try {
			to = (ItemWithFileTO) RestProcessUtils.buildTOFromJSON(json,
					ItemWithFileTO.class);
			if (file != null){
				to = uploadAndValidateFile(file, to, origName);
			}
			
		} 
		catch (Exception e) {
			return  RestProcessUtils.localExceptionHandler(e, e.getMessage());
		}
		
		// create item with the file
			ItemService service = new ItemService();

			try {
				resp= RestProcessUtils.buildResponse(Status.CREATED.getStatusCode(), service.create(to, u));
			} catch (Exception e) {
				//System.out.println("MESSAGE= "+e.getLocalizedMessage());
				resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());

			}
			
		return resp;
	}
	
	public static JSONResponse easyUpdateItem(HttpServletRequest req, String id) throws IOException {
		JSONResponse resp = null;  
		User u = BasicAuthentication.auth(req);
		if (u == null) {    
			resp = buildJSONAndExceptionResponse(UNAUTHORIZED.getStatusCode(), USER_MUST_BE_LOGGED_IN);
		} else {
			try {
				ItemService icrud = new ItemService();		
				DefaultItemTO defaultTO = (DefaultItemTO)buildTOFromJSON(req, DefaultItemTO.class);
				ItemTO itemTO = (ItemTO) icrud.read(id, u);
				CollectionService ccrud = new CollectionService();			
				CollectionTO col = ccrud.read(itemTO.getCollectionId(), u);
				ProfileService pcrud = new ProfileService();
				MetadataProfileTO profileTO = pcrud.read(col.getProfile().getId(), u);
				ReverseTransferObjectFactory.transferDefaultItemTOtoItemTO(profileTO, defaultTO, itemTO);
	            resp = buildResponse(OK.getStatusCode(), icrud.update(itemTO, u));
	            } catch (ImejiException  e) {
	            	resp = localExceptionHandler(e, e.getLocalizedMessage());
	            	}
			}
	return resp;
	}


	public static JSONResponse updateItem(HttpServletRequest req, String id, InputStream fileInputStream, String json, String filename) {

		User u = BasicAuthentication.auth(req);

		ItemService service = new ItemService();
		ItemTO to = new ItemTO();
		boolean fileUpdate = !isNullOrEmpty(json) && (fileInputStream != null || json.indexOf("fetchUrl") > 0 || json.indexOf("referenceUrl") > 0);
		try {
			to = fileUpdate ?
					(ItemWithFileTO) RestProcessUtils.buildTOFromJSON(json, ItemWithFileTO.class) :
					(ItemTO) RestProcessUtils.buildTOFromJSON(json, ItemTO.class);

			validateId(id, to);
			to.setId(id);

			if (fileUpdate){
				to = uploadAndValidateFile(fileInputStream, (ItemWithFileTO)to, filename);
 			 }
				
			return RestProcessUtils.buildResponse(Status.OK.getStatusCode(), service.update(to, u));
		} catch (Exception e) {
			return  RestProcessUtils.localExceptionHandler(e, e.getMessage());
		}

	}

	private static void validateId(String id, ItemTO to) throws BadRequestException {
		if ( !isNullOrEmpty(id) && !isNullOrEmpty(to.getId()) && !id.equals(to.getId())) {
			throw new BadRequestException ("Ambiguous item id: <" + id +"> in path; <" + to.getId() + "> in JSON");
		}
	}
	
	private static ItemWithFileTO uploadAndValidateFile(InputStream file, ItemWithFileTO to, String origName) throws IOException, UnprocessableError, BadRequestException{
		if (file != null){
			String calculatedFilename= (!isNullOrEmpty(to.getFilename()))?to.getFilename():(to.getFilename()==null?origName:to.getFilename());
			String calculatedExtension = FilenameUtils.getExtension(calculatedFilename);
			calculatedExtension = !isNullOrEmpty(calculatedExtension)?"."+calculatedExtension:null;

			//Note: createTempFile suffix must be provided in order not to rename the file to .tmp
			File tmp = File.createTempFile("imejiAPI", calculatedExtension);
			IOUtils.copy(file, new FileOutputStream(tmp));

			StorageController c = new StorageController();
			//convenience call to stop the processing here asap. Item controller checks it anyway
			String guessNotAllowedFormatUploaded = c.guessNotAllowedFormat(tmp);
			if (StorageUtils.BAD_FORMAT.equals(guessNotAllowedFormatUploaded)) {
				throw new UnprocessableError("upload_format_not_allowed: "	+ " (" + calculatedExtension + ")");
			}
			to.setFile(tmp);
			to.setFilename(calculatedFilename);
			
			if (to.getFile() == null && isNullOrEmpty(to.getFetchUrl()) && isNullOrEmpty(to.getReferenceUrl()) ) {
				throw new BadRequestException("A file must be uploaded, referenced or fetched from external location.");
			}
		}
		return to;

	}

}

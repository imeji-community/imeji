package de.mpg.imeji.rest.process;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.mpg.imeji.logic.auth.exception.AuthenticationError;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.rest.to.JSONResponse;
import de.mpg.j2j.exceptions.NotFoundException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ItemProcess {


	private static final Logger LOGGER = LoggerFactory.getLogger(ItemProcess.class);

	public static JSONResponse deleteItem(HttpServletRequest req, String id) {
		User u = BasicAuthentication.auth(req);
		JSONResponse resp; 

		if (u == null) {
			Exception e = new AuthenticationError(CommonUtils.USER_MUST_BE_LOGGED_IN);
			resp = RestProcessUtils.localExceptionHandler(e, CommonUtils.USER_MUST_BE_LOGGED_IN);
		} else {
			ItemService icrud = new ItemService();
			try {
				icrud.delete(id, u);
				resp= RestProcessUtils.buildResponse(Status.NO_CONTENT.getStatusCode(), null);
			} catch (Exception e) {
				resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
			}
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

	public static JSONResponse createItem(HttpServletRequest req,
			InputStream file, String json, String origName) {
		// / write response
		JSONResponse resp; 

		// Load User (if provided)
		User u = BasicAuthentication.auth(req);
		
		if (u == null) {
			Exception e = new AuthenticationError(CommonUtils.USER_MUST_BE_LOGGED_IN);
			resp = RestProcessUtils.localExceptionHandler(e, CommonUtils.USER_MUST_BE_LOGGED_IN);
			return resp;
		} 
		
		// Parse json into to
		ItemWithFileTO to = null;
		try {
			to = (ItemWithFileTO) RestProcessUtils.buildTOFromJSON(json,
					ItemWithFileTO.class);
		} catch (Exception e) {
			e = new BadRequestException();
			resp= RestProcessUtils.localExceptionHandler(e, CommonUtils.JSON_Invalid);
			return resp;
		}
		// set file in to (if provided)

		if ("".equals(to.getFilename())) {
			Exception e = new BadRequestException();
			resp = RestProcessUtils.localExceptionHandler(e, CommonUtils.FILENAME_RENAME_EMPTY);
			return resp;
		} else if (to.getFilename() != null
				&& !"".equals(FilenameUtils.getExtension(to.getFilename()))
				&& !FilenameUtils.getExtension(to.getFilename()).equals(
						FilenameUtils.getExtension(origName))) {
			Exception e = new BadRequestException();
			resp = RestProcessUtils.localExceptionHandler(e, CommonUtils.FILENAME_RENAME_INVALID_SUFFIX);
			return resp;
		} else {
			if (file != null) {
				try {
					File tmp = File.createTempFile("imejiAPI", null);
					IOUtils.copy(file, new FileOutputStream(tmp));
					to.setFile(tmp);
					if (to.getFilename() == null)
						to.setFilename(origName);
					else
						to.setFilename(to.getFilename() + "."
								+ FilenameUtils.getExtension(origName));

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			// create item with the file
			ItemService service = new ItemService();

			try {
				resp= RestProcessUtils.buildResponse(Status.CREATED.getStatusCode(), service.create(to, u) );
			} catch (Exception e) {
				resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());

			}
			
		}

		return resp;
	}


	public static JSONResponse updateItem(HttpServletRequest req, String id, InputStream fileInputStream, String json, String filename) {

		User u = BasicAuthentication.auth(req);

		JSONResponse resp; 
		ItemService service = new ItemService();

		try {
			ItemTO to = !isNullOrEmpty(json) && (fileInputStream != null || json.indexOf("fetchUrl") > 0 || json.indexOf("referenceUrl") > 0) ?
					(ItemWithFileTO) RestProcessUtils.buildTOFromJSON(json, ItemWithFileTO.class) :
					(ItemTO) RestProcessUtils.buildTOFromJSON(json, ItemTO.class);

			validateId(id, to);
			to.setId(id);
			if (fileInputStream != null) {

				if (isNullOrEmpty(to.getFilename()) && !isNullOrEmpty(filename)) {
					to.setFilename(filename);
				}

				File tmpPath = (File)req.getServletContext().getAttribute(CommonUtils.JAVAX_SERVLET_CONTEXT_TEMPDIR);
				File tmpFile = File.createTempFile("imejiAPI", "." + Files.getFileExtension(to.getFilename()), tmpPath);

				ByteStreams.copy(fileInputStream, new FileOutputStream(tmpFile));

				((ItemWithFileTO)to).setFile(tmpFile);

			}
			resp = RestProcessUtils.buildResponse(Status.OK.getStatusCode(), service.update(to, u));
		} catch (Exception e) {
			resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
		}

		return resp;
	}

	private static void validateId(String id, ItemTO to) throws NotFoundException {
		if ( !isNullOrEmpty(id) && !isNullOrEmpty(to.getId()) && !id.equals(to.getId())) {
			throw new NotFoundException("Ambiguous item id: <" + id +"> in path; <" + to.getId() + "> in JSON");
		}
	}

}

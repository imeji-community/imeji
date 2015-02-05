package de.mpg.imeji.rest.process;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.rest.to.JSONResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static com.google.common.base.Strings.isNullOrEmpty;

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

		// Parse json into to
		ItemWithFileTO to = null;
		try {
			to = (ItemWithFileTO) RestProcessUtils.buildTOFromJSON(json,
					ItemWithFileTO.class);
			File tmp = File.createTempFile("imejiAPI", null);
			
			if(!(file == null)){
				IOUtils.copy(file, new FileOutputStream(tmp));
				to.setFile(tmp);
				to.setFilename((!isNullOrEmpty(to.getFilename()))?to.getFilename():(to.getFilename()==null?origName:to.getFilename()));

			}
			
		} catch (Exception e) {
			e = new BadRequestException("");
			resp= RestProcessUtils.localExceptionHandler(e, CommonUtils.JSON_Invalid);
			return resp;
		}
		// create item with the file
			ItemService service = new ItemService();

			try {
				resp= RestProcessUtils.buildResponse(Status.CREATED.getStatusCode(), service.create(to, u));
			} catch (Exception e) {
				resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());

			}
			
		return resp;
	}


	public static JSONResponse updateItem(HttpServletRequest req, String id, InputStream fileInputStream, String json, String filename) {

		User u = BasicAuthentication.auth(req);

		JSONResponse resp; 
		ItemService service = new ItemService();
		ItemTO to = new ItemTO();
		try {
			to = !isNullOrEmpty(json) && (fileInputStream != null || json.indexOf("fetchUrl") > 0 || json.indexOf("referenceUrl") > 0) ?
					(ItemWithFileTO) RestProcessUtils.buildTOFromJSON(json, ItemWithFileTO.class) :
					(ItemTO) RestProcessUtils.buildTOFromJSON(json, ItemTO.class);
		} catch (Exception e) {
			e = new BadRequestException("");
			resp= RestProcessUtils.localExceptionHandler(e, CommonUtils.JSON_Invalid);
			return resp;
		}
		try {
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

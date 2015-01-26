package de.mpg.imeji.rest.process;

import de.mpg.imeji.rest.to.JSONResponse;
import de.mpg.imeji.rest.to.StorageTO;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.logic.storage.StorageController.UPLOAD_BLACKLIST_PROPERTY;
import static de.mpg.imeji.logic.storage.StorageController.UPLOAD_WHITELIST_PROPERTY;
import static de.mpg.imeji.presentation.util.PropertyReader.getProperty;
import static javax.ws.rs.core.Response.Status;


/**
 * Created by vlad on 14.01.15.
 */
public class StorageProcess {

    public static JSONResponse getStorageProperties() {

        JSONResponse resp; 

        StorageTO sto = new StorageTO();

        try {
            final String black = getProperty(UPLOAD_BLACKLIST_PROPERTY);
            if (!isNullOrEmpty(black)) {
                sto.setUploadBlackList(black);
            }
            final String white = getProperty(UPLOAD_WHITELIST_PROPERTY);
            if (!isNullOrEmpty(white)) {
                sto.setUploadWhiteList(white);
            }
            resp = RestProcessUtils.buildResponse(Status.OK.getStatusCode(), sto);
        } catch (Exception e) {
        	resp = RestProcessUtils.localExceptionHandler(e, e.getLocalizedMessage());
        }
        return resp;
    }

}

/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.util;

import java.net.URI;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.resource.ProfileController;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;

/**
 * imeji objects (item, collection, album, profile) loader. This loader should be used to loads
 * objects from a Java bean, since it include error message. Doesn't use caching (unlike
 * {@link ObjectCachedLoader})
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ObjectLoader {
  private static final Logger LOGGER = Logger.getLogger(ObjectLoader.class);

  /**
   * Private Constructor
   */
  private ObjectLoader() {}



  /**
   * Load a {@link MetadataProfile}
   * 
   * @param id
   * @param user
   * @return
   */
  public static MetadataProfile loadProfile(URI id, User user) {
    try {
      ProfileController pc = new ProfileController();
      MetadataProfile p = pc.retrieve(id, user);
      return p;
    } catch (Exception e) {
      LOGGER.info("There was a problem loading the profile with id " + id.toString());
    }
    return null;
  }

  /**
   * Write {@link NotFoundException} in JSF messages and in logs
   * 
   * @param objectType
   * @param id
   */
  private static void writeErrorNotFound(String objectType, URI id) {
    BeanHelper.error(Imeji.RESOURCE_BUNDLE.getLabel(objectType, BeanHelper.getLocale()) + " " + id
        + " " + Imeji.RESOURCE_BUNDLE.getLabel("not_found", BeanHelper.getLocale()));
  }

  /**
   * Write {@link Exception} in jsf messages and in logs
   * 
   * @param e
   * @param id
   * @throws Exception
   */
  private static void writeException(ImejiException e, String id) throws ImejiException {
    // LOGGER.error("Error Object loader for " + id, e);
    // No need to log handled ImejiException
    throw e;
  }
}

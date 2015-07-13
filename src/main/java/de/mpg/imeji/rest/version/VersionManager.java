package de.mpg.imeji.rest.version;

import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.rest.api.API;
import de.mpg.imeji.rest.version.exception.DeprecatedAPIVersionException;
import de.mpg.imeji.rest.version.exception.UnknowAPIVersionException;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements the version management of the api
 * 
 * @author saquet
 *
 */
public class VersionManager {
  private int requestedVersion = API.CURRENT_VERSION;
  private Pattern p = Pattern.compile(".*/rest/v([0-9]+)/.*");
  private String path;
  private boolean hasVersion = false;
  private static Logger logger = Logger.getLogger(VersionManager.class);

  /**
   * Default constructor
   */
  public VersionManager() {}

  /**
   * Check if the version is correct, else return the appropriate error
   * 
   * @param path
   * @throws UnknowAPIVersionException
   * @throws DeprecatedAPIVersionException
   */
  public void checkVersion(String path) throws UnknowAPIVersionException,
      DeprecatedAPIVersionException {
    this.path = path;
    requestedVersion = parseVersionNumber(path);
    if (!isAnExistingVersion()) {
      throw new UnknowAPIVersionException("API version v" + requestedVersion
          + " is not a valid version. Please use the latest version v" + API.CURRENT_VERSION
          + ". For more information see " + linkToAPIDoc());
    } else if (isOldVersion()) {
      throw new DeprecatedAPIVersionException("API version v" + requestedVersion
          + " is no longer supported. Please use the latest version v" + API.CURRENT_VERSION
          + ". For more information see " + linkToAPIDoc());
    }
    // everything fine!
  }

  private String linkToAPIDoc() {
    try {
      Navigation navigation = new Navigation();
      return navigation.getApplicationUrl() + "rest-doc/index.html";
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
    return "http://imeji.org/development/technical-specification/api";

  }

  /**
   * Transform the requested path (with version in the url) into a path to latest version (without
   * version in the url)
   * 
   * @return
   */
  public String getPathToLatestVersion() {
    return path.replace("rest/v" + requestedVersion + "/", "rest/");
  }

  /**
   * Parse the version from the path /rest/v1/... -> is version 1
   * 
   * @param path
   * @return
   */
  private int parseVersionNumber(String path) {
    Matcher m = p.matcher(path);
    if (m.matches()) {
      hasVersion = true;
      return Integer.parseInt(m.group(1));
    }
    hasVersion = false;
    return API.CURRENT_VERSION;
  }

  /**
   * Check if the requested version is an older version
   * 
   * @return
   */
  private boolean isOldVersion() {
    return API.CURRENT_VERSION > requestedVersion;
  }

  /**
   * Check if the requested version exists (older or current)
   * 
   * @return
   */
  private boolean isAnExistingVersion() {
    return API.CURRENT_VERSION >= requestedVersion;
  }

  /**
   * True if the requested version is the same as the current version
   * 
   * @return
   */
  public boolean isCurrentVersion() {
    return API.CURRENT_VERSION == requestedVersion;
  }

  public boolean hasVersion() {
    return hasVersion;
  }

}

/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;
import org.jose4j.lang.JoseException;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.auth.ImejiRsaKeys;
import de.mpg.imeji.logic.jobs.ReadMaxPlanckIPMappingJob;
import de.mpg.imeji.logic.util.IdentifierUtil;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.presentation.beans.ConfigurationBean;
import de.mpg.imeji.presentation.beans.PropertyBean;

/**
 * Initialize application on server start
 * 
 * @author saquet
 */
public class InitializerServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;
  private final static Logger logger = Logger.getLogger(InitializerServlet.class);
  private ConfigurationBean config;

  @Override
  public void init() throws ServletException {
    super.init();
    new PropertyBean();
    try {
      config = new ConfigurationBean();
    } catch (IOException | URISyntaxException e) {
      logger.error("Error reading Configuration", e);
    }
    Imeji.locksSurveyor.start();
    initModel();
    Imeji.executor.submit(new ReadMaxPlanckIPMappingJob());
    initRsaPublicKey();
  }

  /**
   * Initialize the imeji jena tdb
   */
  public void initModel() {
    try {
      Imeji.init();
      runMigration();
    } catch (Exception e) {
      throw new RuntimeException("Error Initializing model: ", e);
    }
  }

  private void initRsaPublicKey() {
    try {
      ImejiRsaKeys.init(ConfigurationBean.getRsaPublicKey(), ConfigurationBean.getRsaPrivateKey());
      ConfigurationBean.setRsaPublicKey(ImejiRsaKeys.getPublicKeyJson());
      ConfigurationBean.setRsaPrivateKey(ImejiRsaKeys.getPrivateKeyString());
      config.saveConfig();
    } catch (JoseException | NoSuchAlgorithmException | InvalidKeySpecException e) {
      logger.error("!!! Error initalizing API Key !!!", e);
    }
  }

  /**
   * look to the migration File (migration.txt)
   * 
   * @throws IOException
   */
  private void runMigration() throws IOException {
    File f = new File(Imeji.tdbPath + StringHelper.urlSeparator + "migration.txt");
    FileInputStream in = null;
    try {
      in = new FileInputStream(f);
    } catch (FileNotFoundException e) {
      logger.info("No " + f.getAbsolutePath() + " found, no migration runs");
    }
    if (in != null) {
      String migrationRequests = new String(StreamUtils.getBytes(in), "UTF-8");
      migrationRequests = migrationRequests.replaceAll("XXX_BASE_URI_XXX", PropertyBean.baseURI());
      migrationRequests = addNewIdToMigration(migrationRequests);
      logger.info("Running migration with query: ");
      logger.info(migrationRequests);
      ImejiSPARQL.execUpdate(migrationRequests);
      logger.info("Migration done!");
    }
  }

  private String addNewIdToMigration(String migrationRequests) {
    Pattern p = Pattern.compile("XXX_NEW_ID_XXX");
    Matcher m = p.matcher(migrationRequests);
    StringBuffer sb = new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb, IdentifierUtil.newId());
    }
    m.appendTail(sb);
    return sb.toString();
  }

  /**
   * Return the {@link ConfigurationBean}
   * 
   * @param req
   * @return
   */
  private ConfigurationBean getConfiguration(HttpServletRequest req) {
    return (ConfigurationBean) req.getSession(true)
        .getAttribute(ConfigurationBean.class.getSimpleName());
  }

  @Override
  public void destroy() {
    Imeji.shutdown();
    super.destroy();
  }

}

/*
 * 
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution
 * License, Version 1.0 only (the "License"). You may not use this file except in compliance with
 * the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License
 * file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with
 * the fields enclosed by brackets "[]" replaced with your own identifying information: Portions
 * Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 */
/*
 * Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft für
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur Förderung der
 * Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */
package de.mpg.imeji.presentation.beans;

import static de.mpg.imeji.logic.util.StringHelper.isNullOrEmptyTrim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;

import de.mpg.imeji.logic.storage.util.MediaUtils;
import de.mpg.imeji.presentation.lang.InternationalizationBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.PropertyReader;
import de.mpg.imeji.presentation.session.SessionBean;

/**
 * JavaBean managing the imeji configuration which is made directly by the administrator from the
 * web (i.e. not in the property file)
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "Configuration")
@ApplicationScoped
public class ConfigurationBean {
  /**
   * Enumeration of available configuration
   * 
   * @author saquet (initial creation)
   * @author $Author$ (last modification)
   * @version $Revision$ $LastChangedDate$
   */
  private enum CONFIGURATION {
    SNIPPET, CSS_DEFAULT, CSS_ALT, MAX_FILE_SIZE, FILE_TYPES, STARTPAGE_HTML, DATA_VIEWER_FORMATS, DATA_VIEWER_URL, AUTOSUGGEST_USERS, AUTOSUGGEST_ORGAS, STARTPAGE_FOOTER_LOGOS, META_DESCRIPTION, INSTANCE_NAME, CONTACT_EMAIL, EMAIL_SERVER, EMAIL_SERVER_USER, EMAIL_SERVER_PASSWORD, EMAIL_SERVER_ENABLE_AUTHENTICATION, EMAIL_SERVER_SENDER, EMAIL_SERVER_PORT, STARTPAGE_CAROUSEL_ENABLED, STARTPAGE_CAROUSEL_QUERY, STARTPAGE_CAROUSEL_QUERY_ORDER, UPLOAD_WHITE_LIST, UPLOAD_BLACK_LIST, LANGUAGES, IMPRESSUM_URL, IMPRESSUM_TEXT, FAVICON_URL, LOGO, REGISTRATION_TOKEN_EXPIRY, REGISTRATION_ENABLED, DEFAULT_DISK_SPACE_QUOTA, RSA_PUBLIC_KEY, RSA_PRIVATE_KEY, BROWSE_DEFAULT_VIEW, DOI_SERVICE_URL, DOI_USER, DOI_PASSWORD, QUOTA_LIMITS;
  }

  private static Properties config;
  private File configFile;
  private static FileTypes fileTypes;
  private String lang = "en";
  private final static Logger logger = Logger.getLogger(ConfigurationBean.class);
  // A list of predefined file types, which is set when imeji is initialized
  private final static String predefinedFileTypes =
      "[Image@en,Bilder@de=jpg,jpeg,tiff,tiff,jp2,pbm,gif,png,psd][Video@en,Video@de=wmv,swf,rm,mp4,mpg,m4v,avi,mov.asf,flv,srt,vob][Audio@en,Ton@de=aif,iff,m3u,m4a,mid,mpa,mp3,ra,wav,wma][Document@en,Dokument@de=doc,docx,odt,pages,rtf,tex,rtf,bib,csv,ppt,pps,pptx,key,xls,xlr,xlsx,gsheet,nb,numbers,ods,indd,pdf,dtx]";
  private final static String predefinedUploadBlackList =
      "386,aru,atm,aut,bat,bin,bkd,blf,bll,bmw,boo,bqf,buk,bxz,cc,ce0,ceo,cfxxe,chm,cih,cla,class,cmd,com,cpl,cxq,cyw,dbd,dev,dlb,dli,dll,dllx,dom,drv,dx,dxz,dyv,dyz,eml,exe,exe1,exe_renamed,ezt,fag,fjl,fnr,fuj,hlp,hlw,hsq,hts,ini,iva,iws,jar,js,kcd,let,lik,lkh,lnk,lok,mfu,mjz,nls,oar,ocx,osa,ozd,pcx,pgm,php2,php3,pid,pif,plc,pr,qit,rhk,rna,rsc_tmp,s7p,scr,scr,shs,ska,smm,smtmp,sop,spam,ssy,swf,sys,tko,tps,tsa,tti,txs,upa,uzy,vb,vba,vbe,vbs,vbx,vexe,vsd,vxd,vzr,wlpginstall,wmf,ws,wsc,wsf,wsh,wss,xdu,xir,xlm,xlv,xnt,zix,zvz";
  private final static String predefinedLanguages = "en,de,ja,es";
  private final static String predefinedRegistrationTokenExpirationDays = "1";
  private final static String predefinedCarouselConfig = "true";
  // default quota is 25GB
  private final static String predefinedDefaultDiskSpaceQuota =
      Long.toString(25l * 1024l * 1024l * 1024l);
  private String dataViewerUrl;

  public enum BROWSE_VIEW {
    LIST, THUMBNAIL;
  }

  private final static BROWSE_VIEW predefinedBrowseView = BROWSE_VIEW.THUMBNAIL;


  /**
   * Constructor, create the file if not existing
   * 
   * @throws URISyntaxException
   * @throws IOException
   */
  public ConfigurationBean() throws IOException, URISyntaxException {
    configFile = new File(PropertyReader.getProperty("imeji.tdb.path") + "/conf.xml");
    if (!configFile.exists())
      configFile.createNewFile();
    readConfig();
  }

  /**
   * Load the imeji configuration from a {@link File}
   * 
   * @param f
   * @throws IOException
   */
  private String readConfig() {
    try {
      config = new Properties();
      FileInputStream in = new FileInputStream(configFile);
      config.loadFromXML(in);
    } catch (Exception e) {
      logger.info("conf.xml can not be read, probably emtpy");
    }
    this.dataViewerUrl = (String) config.get(CONFIGURATION.DATA_VIEWER_URL.name());
    Object ft = config.get(CONFIGURATION.FILE_TYPES.name());
    if (ft == null) {
      fileTypes = new FileTypes(predefinedFileTypes);
    } else
      fileTypes = new FileTypes((String) ft);

    Object o = config.get(CONFIGURATION.DEFAULT_DISK_SPACE_QUOTA.name());
    if (o == null)
      initPropertyWithDefaultValue(CONFIGURATION.DEFAULT_DISK_SPACE_QUOTA,
          predefinedDefaultDiskSpaceQuota);

    initPropertyWithDefaultValue(CONFIGURATION.UPLOAD_BLACK_LIST, predefinedUploadBlackList);
    initPropertyWithDefaultValue(CONFIGURATION.LANGUAGES, predefinedLanguages);
    initPropertyWithDefaultValue(CONFIGURATION.BROWSE_DEFAULT_VIEW, predefinedBrowseView.name());
    initPropertyWithDefaultValue(CONFIGURATION.STARTPAGE_CAROUSEL_ENABLED, predefinedCarouselConfig);
    saveConfig();
    return "";
  }

  /**
   * Init a property with its default value if null or empty
   * 
   * @param c
   * @param defaultValue
   */
  private void initPropertyWithDefaultValue(CONFIGURATION c, String defaultValue) {
    String currentValue = (String) config.get(c.name());
    if (currentValue != null && !"".equals(currentValue)) {
      setProperty(c.name(), currentValue);
    } else {
      setProperty(c.name(), defaultValue);
    }

  }

  /**
   * Save the configuration in the config file
   */
  public void saveConfig() {
    try {
      if (fileTypes != null)
        setProperty(CONFIGURATION.FILE_TYPES.name(), fileTypes.toString());
      if (dataViewerUrl != null)
        setProperty(CONFIGURATION.DATA_VIEWER_URL.name(), dataViewerUrl);
      config.storeToXML(new FileOutputStream(configFile), "imeji configuration File", "UTF-8");
      logger.info("saving imeji config");
      // BeanHelper.removeBeanFromMap(this.getClass());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Set the value of a configuration property, and save it on disk
   * 
   * @param name
   * @param value
   */
  private void setProperty(String name, String value) {
    config.setProperty(name, value);
  }

  /**
   * Return a property as a non null String to avoid null pointer exception
   * 
   * @param name
   * @return
   */
  private String getPropertyAsNonNullString(String name) {
    String v = (String) config.get(name);
    return v == null ? "" : v;
  }

  /**
   * Return a property as a non null String to avoid null pointer exception for static methods
   * 
   * @param name
   * @return
   */
  private static String getPropertyAsNonNullStringStatic(String name) {
    String v = (String) config.get(name);
    return v == null ? "" : v;
  }

  /**
   * Set the Snippet in the configuration
   * 
   * @param str
   */
  public void setSnippet(String str) {
    setProperty(CONFIGURATION.SNIPPET.name(), str);
  }

  /**
   * Read the snippet from the configuration
   * 
   * @return
   */
  public String getSnippet() {
    return (String) config.get(CONFIGURATION.SNIPPET.name());
  }

  public boolean isImageMagickInstalled() throws IOException, URISyntaxException {
    return MediaUtils.verifyImageMagickInstallation();
  }

  /**
   * Set the url of the default CSS
   * 
   * @param url
   */
  public void setDefaultCss(String url) {
    setProperty(CONFIGURATION.CSS_DEFAULT.name(), url);
  }

  /**
   * Return the url of the default CSS
   * 
   * @return
   */
  public String getDefaultCss() {
    return (String) config.get(CONFIGURATION.CSS_DEFAULT.name());
  }

  /**
   * Set the url of the default CSS
   * 
   * @param url
   */
  public void setAlternativeCss(String url) {
    setProperty(CONFIGURATION.CSS_ALT.name(), url);
  }

  /**
   * Return the url of the default CSS
   * 
   * @return
   */
  public String getAlternativeCss() {
    return (String) config.get(CONFIGURATION.CSS_ALT.name());
  }

  /**
   * Set the url of the default CSS
   * 
   * @param md_url
   */
  public void setUploadMaxFileSize(String size) {
    try {
      Integer.parseInt(size);
    } catch (Exception e) {
      setProperty(CONFIGURATION.MAX_FILE_SIZE.name(), "");
    }
    setProperty(CONFIGURATION.MAX_FILE_SIZE.name(), size);
  }

  /**
   * Return the url of the default CSS
   * 
   * @return
   */
  public String getUploadMaxFileSize() {
    String size = (String) config.get(CONFIGURATION.MAX_FILE_SIZE.name());
    if (size == null || size.equals(""))
      return "0";
    return size;
  }

  /**
   * Get the type of Files
   * 
   * @return
   */
  public FileTypes getFileTypes() {
    return fileTypes;
  }

  /**
   * Set the type of Files
   * 
   * @param types
   */
  public void setFileTypes(FileTypes types) {
    fileTypes = types;
  }

  /**
   * Get the type of Files
   * 
   * @return
   */
  public static FileTypes getFileTypesStatic() {
    return fileTypes;
  }
  
  public static boolean getStartPageCarouselEnabledStatic(){
    return Boolean.valueOf(config.getProperty(CONFIGURATION.STARTPAGE_CAROUSEL_ENABLED.name()));
  }
  
  public void setStartPageCarouselEnabled(boolean input){
    setProperty(CONFIGURATION.STARTPAGE_CAROUSEL_ENABLED.name(), String.valueOf(input));
  }
  
  public boolean getStartPageCarouselEnabled(){
    return Boolean.valueOf(config.getProperty(CONFIGURATION.STARTPAGE_CAROUSEL_ENABLED.name()));
  }

  /**
   * Get the html snippet for a specified lang
   * 
   * @param lang
   * @return
   */
  public String getStartPageHTML(String lang) {
    String html = (String) config.get(CONFIGURATION.STARTPAGE_HTML.name() + "_" + lang);
    return html != null ? html : "";
  }

  /**
   * Get the html snippet for the footer of the startpage
   * 
   * @return
   */
  public String getStartPageFooterLogos() {
    String html = (String) config.get(CONFIGURATION.STARTPAGE_FOOTER_LOGOS.name());
    return html != null ? html : "";
  }

  /**
   * 
   * @param html
   */
  public void setStartPageFooterLogos(String html) {
    setProperty(CONFIGURATION.STARTPAGE_FOOTER_LOGOS.name(), html);
  }

  /**
   * Utility class to parse the html snippets
   * 
   * @author saquet
   * 
   */
  public class HtmlSnippet {
    private String html;
    private String lang;

    public HtmlSnippet(String lang, String html) {
      this.lang = lang;
      this.html = html;
    }

    public void listener(ValueChangeEvent event) {
      html = (String) event.getNewValue();
      setProperty(CONFIGURATION.STARTPAGE_HTML.name() + "_" + lang, html);
    }

    public String getLang() {
      return lang;
    }

    public void setLang(String lang) {
      this.lang = lang;
    }

    public String getHtml() {
      return html;
    }

    public void setHtml(String html) {
      this.html = html;
    }
  }

  /**
   * Read all the html snippets in the config and retunr it as a {@link List} {@link HtmlSnippet}
   * 
   * @return
   */
  public List<HtmlSnippet> getSnippets() {
    InternationalizationBean internationalizationBean =
        (InternationalizationBean) BeanHelper.getApplicationBean(InternationalizationBean.class);
    List<HtmlSnippet> snippets = new ArrayList<ConfigurationBean.HtmlSnippet>();
    for (SelectItem lang : internationalizationBean.getLanguages()) {
      String html =
          (String) config.get(CONFIGURATION.STARTPAGE_HTML.name() + "_" + lang.getValue());
      snippets.add(new HtmlSnippet((String) lang.getValue(), html != null ? html : ""));
    }
    return snippets;
  }

  /**
   * @return the lang
   */
  public String getLang() {
    return lang;
  }

  /**
   * @param lang the lang to set
   */
  public void setLang(String lang) {
    this.lang = lang;
  }

  /**
   * @return the list of all formats supported by the data viewer service
   */
  public String getDataViewerFormatListString() {
    return config.getProperty(CONFIGURATION.DATA_VIEWER_FORMATS.name());
  }

  /**
   * @param str
   * 
   */
  public void setDataViewerFormatListString(String str) {
    config.setProperty(CONFIGURATION.DATA_VIEWER_FORMATS.name(), str);

  }

  /**
   * true if the format is supported by the data viewer service
   * 
   * @param format
   * @return
   */
  public boolean isDataViewerSupportedFormats(String format) {
    String l = getDataViewerFormatListString();
    if (l == null || "".equals(format))
      return false;
    return l.contains(format);
  }

  /**
   * @return the url of the data viewer service
   */
  public String getDataViewerUrl() {
    return config.getProperty(CONFIGURATION.DATA_VIEWER_URL.name());
  }

  /**
   * @return the url of the data viewer service in a static way
   */
  public static String getDataViewerUrlStatic() {
    return config.getProperty(CONFIGURATION.DATA_VIEWER_URL.name());
  }

  /**
   * @param str
   * 
   */
  public void setDataViewerUrl(String str) {
    dataViewerUrl = str;
  }

  public String fetchDataViewerFormats() throws JSONException {
    String connURL;
    if (dataViewerUrl.endsWith("/")) {
      connURL = dataViewerUrl + "api/explain/formats";
    } else {
      connURL = dataViewerUrl + "/api/explain/formats";
    }
    // String connURL = dataViewerUrl + "/api/explain/formats";
    DefaultHttpClient httpclient = new DefaultHttpClient();
    HttpGet httpget = new HttpGet(connURL);
    HttpResponse resp;
    String str = "";
    try {
      resp = httpclient.execute(httpget);

      if (200 == resp.getStatusLine().getStatusCode()) {
        HttpEntity entity = resp.getEntity();
        if (entity != null) {
          String retSrc = EntityUtils.toString(entity);
          JSONArray array = new JSONArray(retSrc);

          int i = 0;
          while (i < array.length()) {
            str += array.get(i) + ", ";
            i++;
          }

        }
      }
    } catch (ClientProtocolException e) {
      logger.error(e.getMessage(), e.fillInStackTrace());
    } catch (IOException e) {
      logger.error(e.getMessage(), e.fillInStackTrace());
    } catch (IllegalStateException e) {
      logger.error(e.getMessage(), e.fillInStackTrace());
    }
    setDataViewerFormatListString(str);
    return "";
  }

  // public boolean isEnableAutosuggestForUsers() {
  // return Boolean.parseBoolean(config
  // .getProperty(CONFIGURATION.AUTOSUGGEST_USERS.name()));
  // }
  //
  // public void setEnableAutosuggestForUsers(boolean b) {
  // config.setProperty(CONFIGURATION.AUTOSUGGEST_USERS.name(),
  // Boolean.toString(b));
  // }

  public String getAutosuggestForOrganizations() {
    return config.getProperty(CONFIGURATION.AUTOSUGGEST_ORGAS.name());
  }

  public void setAutosuggestForOrganizations(String s) {
    config.setProperty(CONFIGURATION.AUTOSUGGEST_ORGAS.name(), s);

  }

  public String getAutoSuggestForUsers() {
    return config.getProperty(CONFIGURATION.AUTOSUGGEST_USERS.name());
  }

  public void setAutoSuggestForUsers(String s) {
    config.setProperty(CONFIGURATION.AUTOSUGGEST_USERS.name(), s);

  }

  /**
   * Set the meta description
   * 
   * @param md_url
   */
  public void setMetaDescription(String s) {
    setProperty(CONFIGURATION.META_DESCRIPTION.name(), s);
  }

  /**
   * Return the meta description
   * 
   * @return
   */
  public String getMetaDescription() {
    return (String) config.get(CONFIGURATION.META_DESCRIPTION.name());
  }

  /**
   * Set the name of the instance
   * 
   * @param md_url
   */
  public void setInstanceName(String s) {
    setProperty(CONFIGURATION.INSTANCE_NAME.name(), s);
  }

  /**
   * Return the name of the instance
   * 
   * @return
   */
  public String getInstanceName() {
    return getPropertyAsNonNullString(CONFIGURATION.INSTANCE_NAME.name());
  }

  /**
   * Set the contact email
   * 
   * @param md_url
   */
  public void setContactEmail(String s) {
    setProperty(CONFIGURATION.CONTACT_EMAIL.name(), s);
  }

  /**
   * Return contact email
   * 
   * @return
   */
  public String getContactEmail() {
    if ((String) config.get(CONFIGURATION.CONTACT_EMAIL.name()) != null)
      return (String) config.get(CONFIGURATION.CONTACT_EMAIL.name());
    return "";
  }

  public static String getContactEmailStatic() {
    return (String) config.get(CONFIGURATION.CONTACT_EMAIL.name());
  }


  public void setEmailServer(String s) {
    setProperty(CONFIGURATION.EMAIL_SERVER.name(), s);
  }

  public String getEmailServer() {
    return (String) config.get(CONFIGURATION.EMAIL_SERVER.name());
  }

  public static String getEmailServerStatic() {
    return (String) config.get(CONFIGURATION.EMAIL_SERVER.name());
  }

  public void setEmailServerUser(String s) {
    setProperty(CONFIGURATION.EMAIL_SERVER_USER.name(), s);
  }

  public String getEmailServerUser() {
    return (String) config.get(CONFIGURATION.EMAIL_SERVER_USER.name());
  }

  public static String getEmailServerUserStatic() {
    return (String) config.get(CONFIGURATION.EMAIL_SERVER_USER.name());
  }

  public void setEmailServerPassword(String s) {
    setProperty(CONFIGURATION.EMAIL_SERVER_PASSWORD.name(), s);
  }

  public String getEmailServerPassword() {
    return (String) config.get(CONFIGURATION.EMAIL_SERVER_PASSWORD.name());
  }

  public static String getEmailServerPasswordStatic() {
    return (String) config.get(CONFIGURATION.EMAIL_SERVER_PASSWORD.name());
  }

  public void setEmailServerEnableAuthentication(boolean b) {
    setProperty(CONFIGURATION.EMAIL_SERVER_ENABLE_AUTHENTICATION.name(), Boolean.toString(b));
  }

  public boolean getEmailServerEnableAuthentication() {
    return Boolean
        .parseBoolean((String) config.get(CONFIGURATION.EMAIL_SERVER_ENABLE_AUTHENTICATION.name()));
  }

  public static boolean getEmailServerEnableAuthenticationStatic() {
    return Boolean
        .parseBoolean((String) config.get(CONFIGURATION.EMAIL_SERVER_ENABLE_AUTHENTICATION.name()));
  }

  public void setEmailServerSender(String s) {
    setProperty(CONFIGURATION.EMAIL_SERVER_SENDER.name(), s);
  }

  public String getEmailServerSender() {
    return (String) config.get(CONFIGURATION.EMAIL_SERVER_SENDER.name());
  }

  public static String getEmailServerSenderStatic() {
    return (String) config.get(CONFIGURATION.EMAIL_SERVER_SENDER.name());
  }

  public void setEmailServerPort(String s) {
    setProperty(CONFIGURATION.EMAIL_SERVER_PORT.name(), s);
  }

  public String getEmailServerPort() {
    return (String) config.get(CONFIGURATION.EMAIL_SERVER_PORT.name());
  }

  public static String getEmailServerPortStatic() {
    return (String) config.get(CONFIGURATION.EMAIL_SERVER_PORT.name());
  }

  public void setStartPageCarouselQuery(String s) {
    setProperty(CONFIGURATION.STARTPAGE_CAROUSEL_QUERY.name(), s);
  }

  public String getStartPageCarouselQuery() {
    return (String) config.get(CONFIGURATION.STARTPAGE_CAROUSEL_QUERY.name());
  }

  public void setStartPageCarouselQueryOrder(String s) {
    setProperty(CONFIGURATION.STARTPAGE_CAROUSEL_QUERY_ORDER.name(), s);
  }

  public String getStartPageCarouselQueryOrder() {
    return (String) config.get(CONFIGURATION.STARTPAGE_CAROUSEL_QUERY_ORDER.name());
  }

  public void setUploadBlackList(String s) {
    setProperty(CONFIGURATION.UPLOAD_BLACK_LIST.name(), s);
  }

  public String getUploadBlackList() {
    return (String) config.get(CONFIGURATION.UPLOAD_BLACK_LIST.name());
  }

  public static String getUploadBlackListStatic() {
    return (String) config.get(CONFIGURATION.UPLOAD_BLACK_LIST.name());
  }

  public void setUploadWhiteList(String s) {
    setProperty(CONFIGURATION.UPLOAD_WHITE_LIST.name(), s);
  }

  public String getUploadWhiteList() {
    if (config.get(CONFIGURATION.UPLOAD_WHITE_LIST.name()) != null)
      return (String) config.get(CONFIGURATION.UPLOAD_WHITE_LIST.name());
    return "";
  }

  public static String getUploadWhiteListStatic() {
    if (config.get(CONFIGURATION.UPLOAD_WHITE_LIST.name()) != null)
      return (String) config.get(CONFIGURATION.UPLOAD_WHITE_LIST.name());
    return "";
  }

  public String getLanguages() {
    return getPropertyAsNonNullString(CONFIGURATION.LANGUAGES.name());
  }

  public static String getLanguagesStatic() {
    return getPropertyAsNonNullStringStatic(CONFIGURATION.LANGUAGES.name());
  }

  public void setLanguages(String value) {
    setProperty(CONFIGURATION.LANGUAGES.name(), value);
    // InternationalizationBean internationalizationBean =
    // (InternationalizationBean) BeanHelper
    // .getApplicationBean(InternationalizationBean.class);
    // internationalizationBean.init();
    // // internationalizationBean.readSupportedLanguagesProperty();
    // // internationalizationBean.initLanguagesMenu();
  }
  
  public static String getDoiUserStatic(){
    return (String) config.get(CONFIGURATION.DOI_USER.name());
  }
  
  public String getDoiUser(){
    return (String) config.get(CONFIGURATION.DOI_USER.name());
  }
  
  public void setDoiUser(String s){
    setProperty(CONFIGURATION.DOI_USER.name(), s);
  }

  public static String getDoiPasswordStatic(){
    return (String) config.get(CONFIGURATION.DOI_PASSWORD.name());
  }
  
  public String getDoiPassword(){   
    return (String) config.get(CONFIGURATION.DOI_PASSWORD.name());  
  }
  
  public void setDoiPassword(String s){
    setProperty(CONFIGURATION.DOI_PASSWORD.name(), s);
  }
  
  public static String getDoiServiceUrlStatic(){
    return (String) config.get(CONFIGURATION.DOI_SERVICE_URL.name());
  } 
  
  public String getDoiServiceUrl(){   
    return (String) config.get(CONFIGURATION.DOI_SERVICE_URL.name());  
  }
  
  public void setDoiServiceUrl(String s){
    setProperty(CONFIGURATION.DOI_SERVICE_URL.name(), s);
  }
  
  public void setImpressumUrl(String s) {
    setProperty(CONFIGURATION.IMPRESSUM_URL.name(), s);
  }

  public String getImpressumUrl() {
    return (String) config.get(CONFIGURATION.IMPRESSUM_URL.name());
  }

  public void setImpressumText(String s) {
    setProperty(CONFIGURATION.IMPRESSUM_TEXT.name(), s);
  }

  public String getImpressumText() {
    return (String) config.get(CONFIGURATION.IMPRESSUM_TEXT.name());
  }

  public void setFaviconUrl(String s) {
    setProperty(CONFIGURATION.FAVICON_URL.name(), s);
  }

  public void setRegistrationTokenExpiry(String s) {
    try {
      Integer.valueOf(s);
    } catch (NumberFormatException e) {
      logger.info(
          "Could not understand the Registration Token Expiry Setting, setting it to default ("
              + predefinedRegistrationTokenExpirationDays + " day).");
      s = predefinedRegistrationTokenExpirationDays;
    }

    setProperty(CONFIGURATION.REGISTRATION_TOKEN_EXPIRY.name(), s);
  }

  public static int getRegistrationTokenExpiryStatic() {
    return Integer.valueOf(registrationTokenCompute());
  }

  public String getRegistrationTokenExpiry() {
    return registrationTokenCompute();
  }

  public boolean isRegistrationEnabled() {
    return Boolean.parseBoolean((String) config.get(CONFIGURATION.REGISTRATION_ENABLED.name()));
  }

  public void setRegistrationEnabled(boolean enabled) {
    config.setProperty(CONFIGURATION.REGISTRATION_ENABLED.name(), Boolean.toString(enabled));
  }

  public void setDefaultDiskSpaceQuota(String s) {
    try {
      Long.parseLong(s);
    } catch (NumberFormatException e) {
      logger.info(
          "Could not understand the Default Disk Space Quota property, setting it to default ("
              + predefinedDefaultDiskSpaceQuota + " bytes).");
      s = predefinedDefaultDiskSpaceQuota;
    }
    setProperty(CONFIGURATION.DEFAULT_DISK_SPACE_QUOTA.name(), s);
  }

  public static long getDefaultDiskSpaceQuotaStatic() {
    return Long.valueOf(calculateDefaultDiskSpaceQuota());
  }

  public String getDefaultDiskSpaceQuota() {
    return calculateDefaultDiskSpaceQuota();
  }

  public static String calculateDefaultDiskSpaceQuota() {
    String quota =
        config != null ? (String) config.get(CONFIGURATION.DEFAULT_DISK_SPACE_QUOTA.name()) : null;
    return isNullOrEmptyTrim(quota) ? predefinedDefaultDiskSpaceQuota : quota;
  }

  /**
   * Return the url of the favicon
   * 
   * @return
   */
  public String getFaviconUrl() {
    String myFavicon = (String) config.get(CONFIGURATION.FAVICON_URL.name());
    if (myFavicon == null || "".equals(myFavicon)) {
      Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
      return navigation.getApplicationUrl() + "resources/icon/imeji.ico";
    } else {
      return (String) config.get(CONFIGURATION.FAVICON_URL.name());
    }
  }

  public void setLogoUrl(String s) {
    setProperty(CONFIGURATION.LOGO.name(), s);
  }

  /**
   * Return the url of the favicon
   * 
   * @return
   */
  public String getLogoUrl() {
    return (String) config.get(CONFIGURATION.LOGO.name());
  }

  private static String registrationTokenCompute() {
    String myToken = (String) config.get(CONFIGURATION.REGISTRATION_TOKEN_EXPIRY.name());
    return isNullOrEmptyTrim(myToken) ? predefinedRegistrationTokenExpirationDays : myToken;
  }

  public static String getRsaPublicKey() {
    return (String) config.get(CONFIGURATION.RSA_PUBLIC_KEY.name());
  }

  public static void setRsaPublicKey(String string) {
    config.put(CONFIGURATION.RSA_PUBLIC_KEY.name(), string);
  }

  public static String getRsaPrivateKey() {
    return (String) config.get(CONFIGURATION.RSA_PRIVATE_KEY.name());
  }

  public static void setRsaPrivateKey(String string) {
    config.put(CONFIGURATION.RSA_PRIVATE_KEY.name(), string);
  }

  public String getDefaultBrowseView() {
    return (String) config.get(CONFIGURATION.BROWSE_DEFAULT_VIEW.name());
  }

  public void setDefaultBrowseView(String string) {
    config.put(CONFIGURATION.BROWSE_DEFAULT_VIEW, BROWSE_VIEW.valueOf(string).name());
  }
  
  public void setQuotaLimits(String limits){
    try {
      String[] limitArray = limits.split(",");
      for(int i= 0; i<limitArray.length; i++){
        Double.parseDouble(limitArray[i]);
      }   
      setProperty(CONFIGURATION.QUOTA_LIMITS.name(), limits);
    } catch (NumberFormatException e) {
      logger.info(
          "Wrong format for quota definition! Has to be comma separated list");
      BeanHelper.error("Wrong format for quota definition! Has to be comma separated list. " + "Wrong input " + e.getMessage());
    }
  }
  
  public String getQuotaLimits(){
    return (String) config.get(CONFIGURATION.QUOTA_LIMITS.name());
  }
  
  public static LinkedHashMap<String, Long> getQuotaLimitsStatic(){
    SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
    int bytesPerGigabyte = 1073741824;
    
    String limits = (String) config.get(CONFIGURATION.QUOTA_LIMITS.name()); 
    String[] limitArray = limits != null ? limits.split(",") : new String[0];

    LinkedHashMap<String, Long> quotaLimits = new LinkedHashMap<String, Long>();
    quotaLimits.put(sb.getLabel("unlimited"), Long.MAX_VALUE);
    for(int i=0; i<limitArray.length; i++){
      quotaLimits.put(limitArray[i], (long) ((Double.parseDouble(limitArray[i]))*bytesPerGigabyte));
    }
    return quotaLimits;
    
  }

}

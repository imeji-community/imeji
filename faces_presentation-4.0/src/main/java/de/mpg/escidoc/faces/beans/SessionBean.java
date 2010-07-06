/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2007 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package de.mpg.escidoc.faces.beans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ThisExpression;

import de.mpg.escidoc.faces.container.album.AlbumSession;
import de.mpg.escidoc.faces.item.FacesItemVO;
import de.mpg.escidoc.faces.item.ItemVO;
import de.mpg.escidoc.faces.metadata.ScreenConfiguration;
import de.mpg.escidoc.faces.pictures.Detail;
import de.mpg.escidoc.faces.statistics.StatisticsBean;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.StringHelper;
import de.mpg.escidoc.faces.util.UrlHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.statistics.UserAgentAnalyser;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.AffiliationVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;


/**
 * 
 * This bean holds all information about a users session.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class SessionBean
{
    private static Logger logger = Logger.getLogger(SessionBean.class); 
    private XmlTransforming xmlTransforming;
    private AlbumSession albumSession = null;
    
    // The logged in user
    private AccountUserVO user = null;
    private List<AffiliationVO> affiliations = new ArrayList<AffiliationVO>();
    private List<String> affiliationsName = new ArrayList<String>();
    private boolean firstLoad = true;
    
    // His locale
    private Locale locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
    
    // His userHandle
    private String userHandle;
    
    // The current search query used by the HomePage.
    private String query = null;;
    private String itemsPerPageBrowse;
    private String itemsPerPageAlbum;
    private String pageContext = "browsePage";
    private String currentPage = null;
    private int pageNumber = 1;
    private String sortedBy = null;
    private int detailPage = 1;

    private List<String> sortList = new ArrayList<String>();
    private List<String> orderList = new ArrayList<String>();
    private String currentUrl = "home";
    private String backUrl = null;
    private String fullUrl = null;
    private String lastBrowsing = "home";
    private String action = null;
    
    // link for the details view
    private String backToResultList = "";
    private String viewPictureForComparisonLink ="";
    private String viewImageAttributesLink ="";
    
    private String collectionSelectedName = "Whole collection";
    
    // use to add several items at one time
    private String batchAddValue = "";
    private String batchRemoveValue = "";
    
    // Last list viewed
    private String lastListViewed;
    
    //Detail item
    private ItemVO detailItem = null;
    private FacesItemVO alternativeItem = null;
   
    private String descriptionWithdraw = "";
    
    private int totalNumberOfItems;

    // value set to true if user logged in
    private boolean allowed = false;
    private boolean admin = false;
     
    // Navigation bean
    private Navigation navigation;
    
    // Bundle
    public static final String LABEL_BUNDLE = "labels";
    public static final String MESSAGES_BUNDLE = "messages";    
    public static final String METADATA_BUNDLE = "metadata";
    
    // Search
    private String urlQuery = null;
    private String person = null;
    private boolean pageNotFound = false;
    
    // User agreement for the export
    private boolean agreement = false;
    private boolean active = false;
    
    // The list of items currently displayed
    private List<ItemVO> items = null;
    
    // The message to be displayed in Faces
    private String message = null;
    private String information = null;
    private boolean messageDisplayed = false;
    boolean firstDisplay = true;
    
    // Selected menu in pictures.jsp
    private String selectedMenu = "SORTING";
    /**
     * position on the page
     */
    private String pageX = "0";
    private String pageY = "0";
    
    /**
     * The search index base!!!
     */
    private String indexBase = null;
    private String indexBaseAlbum = null;
    
    private List<SelectItem> affiliationSelectItems;
    
    private String browsePage = "browsePage";
    private String albumPage = "albumPage";

    public enum pageContextEnum 
    {
        browsePage, albumPage
    }
    
    public SessionBean()
    {
        try
        {
            //system.out.println("Session bean");
            InitialContext context = new InitialContext();
            xmlTransforming = (XmlTransforming) context.lookup(XmlTransforming.SERVICE_NAME);
            navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
            
            //currentAlbum = new FacesContainerVO(this.getLabel("activeAlbum_default"));
            //viewAlbum = new FacesContainerVO();
            
            // Override user language initially
            locale = new Locale("en");
            
            // initialization of the sort criteria
            ScreenConfiguration sm = new ScreenConfiguration("sort");
            
            for (int i = 0; i < sm.getMdList().size(); i++)
            {
                sortList.add(i, sm.getMdList().get(i).getIndex());
                orderList.add(i, "asc");
               
            }
            
            indexBase = " " + "escidoc.face";
            indexBaseAlbum = "escidoc.publication";                        
        }
        catch (Exception e)
        {
            logger.error("Error initializing session bean", e);
        }
    }

	// Getters and Setters
    public String getSelectedLabelBundle()
    {
        return LABEL_BUNDLE + "_" + locale.getLanguage();
    }

    public String getSelectedMessagesBundle()
    {
        return MESSAGES_BUNDLE + "_" + locale.getLanguage();
    }
    
    public String getSelectedMetadataBundle()
    {
        return METADATA_BUNDLE + "_" + locale.getLanguage();
    }

    public void toggleLocale(ActionEvent event)
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        //
        // toggle the locale
        Locale locale = null;
        Map<String, String> map = fc.getExternalContext().getRequestParameterMap();
        String language = (String) map.get("language");
        String country = (String) map.get("country");
        try
        {
            locale = new Locale(language, country);
            fc.getViewRoot().setLocale(locale);
            Locale.setDefault(locale);
            this.locale = locale;
            logger.debug("New locale: " + language + "_" + country + " : " + locale);
        }
        catch (Exception e)
        {
            logger.error("unable to switch to locale using language = " + language + " and country = " + country, e);
        }
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setUserLocale(final Locale userLocale)
    {
        this.locale = userLocale;
    }

    public String getQueryExtension() throws Exception
    {
        if (allowed)
        {
            return "(escidoc.content-model.objid=" + PropertyReader.getProperty("escidoc.faces.content-model.id") + ")";
        }
        else
        {
            return "(escidoc.content-model.objid=" + PropertyReader.getProperty("escidoc.faces.content-model.id") + ") and ( escidoc.component.visibility=public)";
        }
    }
    
    /**
     * TODO FrM: Check this
     * Converts an enum to a String for output.
     * @param enumObject the enum to convert
     * @return the converted String for output
     */
    public String convertEnumToString(final Object enumObject)
    {
        if (enumObject != null)
        {
            return "ENUM_" + enumObject.getClass().getSimpleName().toUpperCase() + "_" + enumObject;
        }
        else
        {
            return "ENUM_EMPTY";
        }
    }
    
    /**
     * Returns the label according to the current user locale.
     * @param placeholder A string containing the name of a label.
     * @return The label.
     */
    public String getLabel(String placeholder)
    {
        return ResourceBundle.getBundle(this.getSelectedLabelBundle()).getString(placeholder);
    }
    
    /**
     * Returns the message according to the current user locale.
     * @param placeholder A string containing the name of a message.
     * @return The label.
     */
    public String getMessage(String placeholder)
    {
        return ResourceBundle.getBundle(this.getSelectedMessagesBundle()).getString(placeholder);
    }
    
    /**
     * Returns the metadata label according to the current user locale.
     * @param placeholder
     * @return the label of the metadata
     */
    public String getMetadata(String placeholder)
    {
        String bundle = placeholder;
        try
        {
            bundle = ResourceBundle.getBundle(this.getSelectedMetadataBundle()).getString(placeholder);
        }
        catch (Exception e)
        {
            // nothing
        }
        
        return bundle;
    }

    /**
     * Check if the user has logged in
     * @return
     */
    public boolean getCheckLogin()
    {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        
        if (request.getParameter(navigation.USERHANDLE_PARAMETER_NAME) != null)
        {
            String newUserHandle = null;
            try
            {
                newUserHandle = new String(Base64.decode(request.getParameter(
                        navigation.USERHANDLE_PARAMETER_NAME)), "UTF-8");
            }
            catch (Exception e)
            {
                logger.error("Error decoding userHandle", e);
            }
            
            if (newUserHandle != null && !"".equals(newUserHandle) && !newUserHandle.equals(userHandle))
            {
                // Call FrameWork method
                try
                {
                    userHandle = newUserHandle;
                    String xmlUser = ServiceLocator.getUserAccountHandler(userHandle).retrieve(userHandle);
                    this.user = xmlTransforming.transformToAccountUser(xmlUser);
                    // add the user handle to the transformed account user
                    this.user.setHandle(userHandle);
                    String userGrantXML = ServiceLocator
                        .getUserAccountHandler(userHandle)
                        .retrieveCurrentGrants(this.user.getReference().getObjectId());
                    List<GrantVO> grants = xmlTransforming.transformToGrantVOList(userGrantXML);
                    List<GrantVO> userGrants = this.user.getGrants();
                    if (grants != null)
                    {
                        for (GrantVO grant : grants)
                        {
                            userGrants.add(grant);
                            
                            System.out.println(grant.getRole() + " for " + grant.getObjectRef() + " = " +  PropertyReader.getProperty("escidoc.faces.context.id"));
                            
                            if ("escidoc:role-depositor".equals(grant.getRole()) 
                                    && PropertyReader.getProperty("escidoc.faces.context.id").equals(grant.getObjectRef()))
                            {
                                allowed = true;
                                this.information = this.getMessage("success_log_in");
                                createLoginStatisticData();
                                // Check if user is admin
                                for (GrantVO grantAdmin : grants)
                                {
                                    if ("escidoc:role-administrator".equals(grantAdmin.getRole()) 
                                            && PropertyReader.getProperty("escidoc.faces.context.id").equals(grantAdmin.getObjectRef()))
                                    {
                                        admin = true;
                                    }
                                }
                                
                                return true;
                            }
                        }
                   
                    }
                    
                   if (user.getAffiliations() != null)
                   {
                       for (int j = 0; j < user.getAffiliations().size(); j++)
                       {
                          String affiliationXml = ServiceLocator.getOrganizationalUnitHandler(userHandle).retrieve(user.getAffiliations().get(j).getObjectId());
                          this.affiliations.add(j, xmlTransforming.transformToAffiliation(affiliationXml));  
                         
                       }
                   } 
                  
                }
                catch (Exception e)
                {
                    this.user = null;
                    this.userHandle = null;
                    this.allowed = false;
                    logger.error("Error authenticating user", e);
                }
    
            }
            else if (newUserHandle == null || "".equals(newUserHandle))
            {
                return false;
            }
            else
            {
                return allowed;
            }
        }
        return false;
    }
    
    private void  createLoginStatisticData() throws HttpException, ServiceException, IOException, URISyntaxException
    {
        // Create a statistic data "visit" for statistics "number of visits"
        String statisticDataXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<statistic-record><scope objid=\"1\"/>" +
                "<parameter name=\"handler\"><stringvalue>"+
                StatisticsBean.INSTANCE_ID +
                "</stringvalue></parameter>" +
                "<parameter name=\"request\"><stringvalue>login</stringvalue></parameter>" +
                "<parameter name=\"interface\"><stringvalue>SOAP</stringvalue></parameter>" +
                "<parameter name=\"successful\"><stringvalue>1</stringvalue></parameter>" +
                "<parameter name=\"internal\"><stringvalue>0</stringvalue></parameter>" +
                "<parameter name=\"user_id\"><stringvalue>" + this.user.getReference().getObjectId() + "</stringvalue></parameter>" +
                "</statistic-record>";
//        StatisticsBean statisticsBean = (StatisticsBean) BeanHelper.getRequestBean(StatisticsBean.class);
//        ServiceLocator.getStatisticDataHandler(statisticsBean.getStatisitcsEditorHandle()).create(statisticDataXml);
        StatisticsBean statisticsBean = (StatisticsBean) BeanHelper.getApplicationBean(StatisticsBean.class);
        
        ServiceLocator.getStatisticDataHandler(statisticsBean.getAdminUserHandle()).create(statisticDataXml);
    }
    
    public String getCreateVisitStatisticData() throws Exception
    {
    	UrlHelper urlHelper = (UrlHelper) BeanHelper.getRequestBean(UrlHelper.class);
    	
    	if (urlHelper.getAction() != null && urlHelper.getAction().equals("logout")) 
		{
    		firstLoad = false;
		}
    	
    	FacesContext context = FacesContext.getCurrentInstance();
    	HttpServletRequest request =  (HttpServletRequest) context.getExternalContext().getRequest();    	
    	String userAgent = request.getHeader("user-agent");
    	
    	if (firstLoad && user == null) 
    	{
    		createVisitStatisticData(userAgent);
		}
    	
    	return "";
    }
    
	private void createVisitStatisticData(String userAgent) throws Exception
	{
		// initialize the statisticsbean
        StatisticsBean statisticsBean = (StatisticsBean) BeanHelper.getApplicationBean(StatisticsBean.class);
        
        if (userAgent.indexOf("OpenNMS HttpMonitor")==-1
        		&& userAgent.indexOf("internal dummy connection")==-1
        		&& userAgent.indexOf("check_http/v1.4.14 (nagios-plugins 1.4.14)") == -1) 
        {
			firstLoad = false;
				
		    // Create a statistic data "visit" for statistics "number of visits"
	        String statisticDataXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
		                "<statistic-record><scope objid=\"1\"/>" +
		                "<parameter name=\"handler\"><stringvalue>"+
		                StatisticsBean.INSTANCE_ID +
		                "</stringvalue></parameter>" +
		                "<parameter name=\"request\"><stringvalue>visit</stringvalue></parameter>" +
		                "<parameter name=\"interface\"><stringvalue>SOAP</stringvalue></parameter>" +
		                "<parameter name=\"successful\"><stringvalue>1</stringvalue></parameter>" +
		                "<parameter name=\"internal\"><stringvalue>0</stringvalue></parameter>" +
		                "</statistic-record>";
		        
		//	        StatisticDataHandler statisticDataHandler = ServiceLocator.getStatisticDataHandler(statisticsBean.getStatisitcsEditorHandle());
		//	        
		//	        statisticDataHandler.create(statisticDataXml);
		//	        
	          ServiceLocator.getStatisticDataHandler(statisticsBean.getAdminUserHandle()).create(statisticDataXml);
		//	        StatisticsBean statisticsBean = (StatisticsBean) BeanHelper.getApplicationBean(StatisticsBean.class);
		//	        ServiceLocator.getStatisticDataHandler(statisticsBean.getStatisitcsEditorHandle()).create(statisticDataXml);
		
    	}	
	}

    /**
     * Logout the current user. Closes the user session on the frameworks and invalidates the HttpSession.
     * @return
     */
    public String logout()
    {
        if (user != null)
        {
            FacesContext fc = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
            session.invalidate();
            user = null;
            userHandle = null;
            allowed = false;
            this.message = this.getMessage("success_log_out");
           
            try
            {
                fc.getExternalContext().redirect(navigation.getLogoutUrl());
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error redirect after login", e);
            }
        }
        return "";
        //return navigation.HOME_PAGE.getName();
    }

    public String getBackToResultList()
    {
        return backToResultList;
    }
        
    
    public void setBackToResultList(String backToResultList)
    {
        this.backToResultList = backToResultList;
    }
    
    
    public String getViewPictureForComparisonLink() throws Exception
    {
        viewPictureForComparisonLink = navigation.getComparisonUrl() 
                                    + "/" 
                                    + detailItem.getItem().getObjid();
        
        return viewPictureForComparisonLink;
    }

    public void setViewPictureForComparisonLink(String viewPictureForComparisonLink)
    {
        this.viewPictureForComparisonLink = viewPictureForComparisonLink;
    }

    public String getViewImageAttributesLink() throws Exception
    {
        viewImageAttributesLink = navigation.getDetailUrl() 
                                + "/" 
                                + detailItem.getItem().getObjid();
        
        return viewImageAttributesLink;
    }

    public void setViewImageAttributesLink(String viewImageAttributesLink)
    {
        this.viewImageAttributesLink = viewImageAttributesLink;
    }
    public AccountUserVO getUser()
    {
        return user;
    }

    public void setUser(AccountUserVO user)
    {
        this.user = user;
    }

    public String getUserHandle()
    {
        return userHandle;
    }

    public void setUserHandle(String userHandle)
    {
        this.userHandle = userHandle;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public boolean getAllowed()
    {
        return allowed;
    }
    
    public String getPerson()
    {
        return person;
    }

    public void setPerson(String person)
    {
        this.person = person.trim();
    }

    public List<ItemVO> getItems()
    {
        return items;
    }

    public void setItems(List<ItemVO> items)
    {
        this.items = items;
    }
    
    public List<String> getSortList()
    {
        return sortList;
    }

    public void setSortList(List<String> sortList)
    {
        this.sortList = sortList;
    }

    public List<String> getOrderList()
    {
        return orderList;
    }

    public void setOrderList(List<String> orderList)
    {
        this.orderList = orderList;
    }

    public String getCurrentUrl()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        
        String url = request.getParameter("currentUrl");
        
        if(url != null && !"actionAlbum ".equals(url))
        {
            currentUrl = url;
        }
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl)
    {
        this.currentUrl = currentUrl;
    }

    public ItemVO getDetailItem()
    {
        if (detailItem == null || detailItem.getItem() == null)
        {
            new Detail();
        }
        return detailItem;
    }

    public void setDetailItem(ItemVO item)
    {
        this.detailItem = item;
    }

    public FacesItemVO getAlternativeItem()
    {
        return alternativeItem;
    }

    public void setAlternativeItem(FacesItemVO alternativeItem)
    {
        this.alternativeItem = alternativeItem;
    }

    public List<AffiliationVO> getAffiliations()
    {
        return affiliations;
    }

    public void setAffiliations(List<AffiliationVO> affiliations)
    {
        this.affiliations = affiliations;
    }

    public List<String> getAffiliationsName()
    {
        for (int i = 0; i < affiliations.size(); i++)
        {
            affiliationsName.add(i, affiliations.get(i).getDefaultMetadata().getName());
        }
        return affiliationsName;
    }

    public void setAffiliationsName(List<String> affiliationsName)
    {
        this.affiliationsName = affiliationsName;
    }

    public String getBatchAddValue()
    {
        return batchAddValue;
    }

    public void setBatchAddValue(String batchAddValue)
    {
        this.batchAddValue = batchAddValue;
    }

    /**
     * Return method/action value requested in the url.
     * @return
     */
    public String getAction()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        String actionFromUrl =  StringHelper.encodeCqlParameter(request.getParameter("method")); 
        if (actionFromUrl != null)
        {
            action = actionFromUrl;
        }
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }
    
    /**
     * Toggle the value of the agreement according to the checkbox
     * @param event
     */
    public void agree(ValueChangeEvent event)
    {
        this.agreement = ((Boolean)event.getNewValue()).booleanValue();
    }

    /**
     * Parameter for export agreement
     * true: the user agree the agreement
     * False: the suer doesn't agree.
     * @return true/false
     */
    public boolean isAgreement()
    {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        if (request.getParameter("agree") != null)
        {
            if ("1".equals(request.getParameter("agree")))
            {
                agreement = true;
            }
            if ("0".equals(request.getParameter("agree")))
            {
                agreement = false;
            }
        }
        
        return agreement;
    }
    
    /**
     * Parameter for export agreement
     * true: the user agree the agreement
     * False: the suer doesn't agree.
     * @param boolean agreement
     */
    public void setAgreement(boolean agreement)
    {
        this.agreement = agreement;
    }
    
    public String getLastBrowsing()
    {
        return lastBrowsing;
    }

    public void setLastBrowsing(String lastBrowsing)
    {
        this.lastBrowsing = lastBrowsing;
    }

    public String getBackUrl()
    {
        return backUrl;
    }

    public void setBackUrl(String backUrl)
    {
        this.backUrl = backUrl;
    }
    /**
     * call when the export is not allowed
     * @return
     * @throws IOException 
     */
    public String exportForbidden() throws Exception
    {
        albumSession = (AlbumSession)BeanHelper.getSessionBean(AlbumSession.class);
        this.setMessage(this.getMessage("message_export_empty_album"));
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect(navigation.getAlbumUrl() 
                + "/" + albumSession.getCurrent().getLatestVersion().getObjectId() 
                + "/" + navigation.getDefaultBrowsingKeepShowAlbum());
        return null;
    }
    
    public String publishForbidden() throws Exception
    {
        albumSession = (AlbumSession)BeanHelper.getSessionBean(AlbumSession.class);
        this.setMessage(this.getMessage("message_publish_empty_album"));
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().redirect(navigation.getAlbumUrl() 
                + "/" + albumSession.getCurrent().getLatestVersion().getObjectId()
                + "/" + navigation.getDefaultBrowsingKeepShowAlbum());
        return null;
    }
    
    /**
     * The current Url is always stored in here 
     * @return
     */
    public String getFullUrl()
    {
        return fullUrl;
    }
    
    /**
     * Return the current Url
     * @param fullUrl
     */
    public void setFullUrl(String fullUrl)
    {
        this.fullUrl = fullUrl;
    }
    
    public void redirectToAlbum() throws Exception
    {
        Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        FacesContext.getCurrentInstance().getExternalContext().redirect(
        		navigation.getAlbumUrl() 
        		+ "/" + albumSession.getCurrent().getLatestVersion().getObjectId()
        		+ "/" + navigation.getDefaultBrowsingKeepShowAlbum());
    }

    public void descriptionWithdrawListener(ValueChangeEvent event)
    {
        descriptionWithdraw = event.getNewValue().toString();
    }
    
    /**
     * Get the comment by the user when he wants to withdraw an album
     * @return
     */
    public String getDescriptionWithdraw()
    {
        return descriptionWithdraw;
    }
    
    /**
     * Set the comment by the user when he wants to withdraw an album.
     * @param descriptionWithdraw
     */
    public void setDescriptionWithdraw(String descriptionWithdraw)
    {
        this.descriptionWithdraw = descriptionWithdraw;
    }
    /**
     * value to make a new album active after his creation
     */ 
    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }
    /**
     * Make an album active after his creation
     * @param event
     */
    public void makeActive(ValueChangeEvent event)
    {
        this.active = ((Boolean)event.getNewValue()).booleanValue();
    }

    public String getMessage()
    {   
        if (message != null)
        {
            messageDisplayed = true;
        }
        
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
    
    /**
     * initialize the message
     * @return
     */
    public String getInitializationMessage()
    {
        this.message = null;
        this.information = null;
        this.messageDisplayed = false;
        return null;
    }
    
    public String getDisplayed()
    {
        messageDisplayed = true;
        
        return null;
    }

    public boolean isMessageDisplayed()
    {
        return messageDisplayed;
    }

    public void setMessageDisplayed(boolean messageDisplayed)
    {
        this.messageDisplayed = messageDisplayed;
    }

    public String getInformation()
    {
        UrlHelper urlHelper = (UrlHelper) BeanHelper.getRequestBean(UrlHelper.class);
        
        if ("logout".equals(urlHelper.getAction()))
        {
            information = this.getMessage("success_log_out");
        }
        
        if (information != null)
        {
            messageDisplayed = true;
        }
        return information;
    }

    public void setInformation(String information)
    {
        this.information = information;
    }

    public String getLastListViewed()
    {
        return lastListViewed;
    }

    public void setLastListViewed(String lastListViewed)
    {
        this.lastListViewed = lastListViewed;
    }

    public String getCurrentPage()
    {
        UrlHelper urlHelper = (UrlHelper) BeanHelper.getRequestBean(UrlHelper.class);
        currentPage = urlHelper.getPage();
        return currentPage;
    }

    public void setCurrentPage(String currentPage)
    {
        this.currentPage = currentPage;
    }
    
    public String getLogoutAndRedirect() throws Exception
    {
        user = null;
        userHandle = null;
        allowed = false;
        FacesContext fc = FacesContext.getCurrentInstance();
        Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
        HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
        session.invalidate();
        FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getLogoutUrl());
        
        return "";
    }

    public String getSortedBy()
    {
        return sortedBy;
    }

    public void setSortedBy(String sortedBy)
    {
        this.sortedBy = sortedBy;
    }

    public int getPageNumber()
    {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber)
    {
        this.pageNumber = pageNumber;
    }

    public int getTotalNumberOfItems()
    {
        return totalNumberOfItems;
    }

    public void setTotalNumberOfItems(int totalNumberOfItems)
    {
        this.totalNumberOfItems = totalNumberOfItems;
    }

    public int getDetailPage()
    {
        return detailPage;
    }

    public void setDetailPage(int detailPage)
    {
        this.detailPage = detailPage;
    }

    public String getBatchRemoveValue()
    {
        return batchRemoveValue;
    }

    public void setBatchRemoveValue(String batchRemoveValue)
    {
        this.batchRemoveValue = batchRemoveValue;
    }

    public String getUrlQuery()
    {
        return urlQuery;
    }

    public void setUrlQuery(String urlQuery)
    {
        this.urlQuery = urlQuery;
    }

    public boolean isAdmin()
    {
        return admin;
    }

    public void setAdmin(boolean admin)
    {
        this.admin = admin;
    }

    public String getCollectionSelectedName()
    {
        return collectionSelectedName;
    }

    public void setCollectionSelectedName(String collectionSelectedName)
    {
        this.collectionSelectedName = collectionSelectedName;
    }


    public boolean isPageNotFound()
    {
        return pageNotFound;
    }

    public void setPageNotFound(boolean pageNotFound)
    {
        this.pageNotFound = pageNotFound;
    }

    public String getIndexBase()
    {
        return indexBase;
    }
    
    public void setIndexBase(String indexBase)
    {
        this.indexBase = indexBase;
    }
    
    public String getIndexBaseAlbum()
    {
        return indexBaseAlbum;
    }
    
    public void setIndexBaseAlbum(String indexBaseAlbum)
    {
        this.indexBaseAlbum = indexBaseAlbum;
    }

    public String getPageX()
    {
        if (!"myalbums".equalsIgnoreCase(currentUrl))
        {
            pageX = "0";
        }
        
        return pageX;
    }

    public void setPageX(String pageX)
    {
        this.pageX = pageX;
    }

    public String getPageY()
    {
        if (!"myalbums".equalsIgnoreCase(currentUrl))
        {
            pageY = "0";
        }
        
        return pageY;
    }

    public void setPageY(String pageY)
    {
        this.pageY = pageY;
    }
    
    public void changePageX(ValueChangeEvent event)
    {
        this.pageX = event.getNewValue().toString();
    }
    
    public void changePageY(ValueChangeEvent event)
    {
        this.pageY = event.getNewValue().toString();
    }     
    
    public List<SelectItem> getAffiliationSelectItems()
    {
        return affiliationSelectItems;
    }

    public void setAffiliationSelectItems(List<SelectItem> affiliationSelectItems)
    {
        this.affiliationSelectItems = affiliationSelectItems;
    }
    
    public String getItemsPerPageAlbum()
    {
        if (this.itemsPerPageAlbum == null)
        {
            //return default value
            return "12";
        }
       
        return itemsPerPageAlbum;
    }
    
    public void setItemsPerPageAlbum(String itemsPerPageAlbum)
    {
        if (itemsPerPageAlbum != null)
        {
            this.itemsPerPageAlbum = itemsPerPageAlbum;
        }
    }   

    public String getItemsPerPageBrowse()
    {
        if (this.itemsPerPageBrowse == null)
        {
            //return default value
            return "12";
        }
        return itemsPerPageBrowse;
    }

    public void setItemsPerPageBrowse(String itemsPerPageBrowse)
    {
        if (itemsPerPageBrowse != null)
        {
            this.itemsPerPageBrowse = itemsPerPageBrowse;
        }
    }
    
    public String getPageContext()
    {
        return this.pageContext;
    }

    public void setPageContext(String pageContextEnum)
    {
        this.pageContext = pageContextEnum;
    }

    public String getSelectedMenu()
    {
        return selectedMenu;
    }

    public void setSelectedMenu(String selectedMenu)
    {
        this.selectedMenu = selectedMenu;
    }

    
    
}

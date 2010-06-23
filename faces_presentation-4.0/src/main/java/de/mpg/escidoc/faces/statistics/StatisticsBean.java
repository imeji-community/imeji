package de.mpg.escidoc.faces.statistics;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.axis.encoding.Base64;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpec;
import org.apache.commons.httpclient.methods.PostMethod;

import de.escidoc.www.services.aa.UserAccountHandler;
import de.escidoc.www.services.sm.ReportDefinitionHandler;
import de.escidoc.www.services.sm.ReportHandler;
import de.escidoc.www.services.sm.ScopeHandler;
import de.mpg.escidoc.faces.beans.Navigation;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.UrlHelper;
import de.mpg.escidoc.services.common.XmlTransforming;
import de.mpg.escidoc.services.common.statistics.UserAgentAnalyser;
import de.mpg.escidoc.services.common.valueobjects.AccountUserVO;
import de.mpg.escidoc.services.common.valueobjects.GrantVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportDefinitionVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportParamsVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordParamVO;
import de.mpg.escidoc.services.common.valueobjects.statistics.StatisticReportRecordVO;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * JSF bean for Faces statistics
 */
public class StatisticsBean 
{
    private XmlTransforming xmlTransforming;
    private SessionBean sessionBean = null;
    
    private ScopeHandler scopeHandler = null;
    private String facesScopeID = null;
   
    private String adminUserHandle = null;
    private String statisitcsEditorHandle= null;
    
    private HashMap<String, StatisticReportDefinitionVO> reportDefinitionMap = null;
    
    private Charts numberOfVisitCharts = null;
    private Charts numberOfLoginCharts = null;
    private Charts numberOfExportCharts = null;
    private Charts numberOfImagesExportedCharts = null;
    
    private FacesStatisticsTable loginTable = null;
    private FacesStatisticsTable visitTable = null;
    private FacesStatisticsTable exportTable = null;
    private FacesStatisticsTable imagesTable = null;
    
    private int firstMonth;
    private int lastMonth;
    private int firstYear;
    private int lastYear;
    
	private static String REPORTDEFINITION_IMAGESEXPORT = "select * from sm._1_object_statistics where handler = 'INSTANCE_ID' and request ILIKE 'Faces export item' ";
    private static String REPORTDEFINITION_EXPORT = "select * from sm._1_request_statistics where handler='INSTANCE_ID' and request ILIKE 'Faces export';";
    private static String REPORTDEFINITION_NUMBER_OF_VISITS = "select * from sm._1_request_statistics where handler='INSTANCE_ID' and request='visit';";
    private static String REPORTDEFINITION_LOGIN = "select * from sm._1_request_statistics where handler = 'INSTANCE_ID' and request ='login';";
    
    public static String INSTANCE_ID = null;

	private String scopeXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
    		"<scope xmlns=\"http://www.escidoc.de/schemas/scope/0.3\"> <name>Scope for Faces solution</name> <type>normal</type> </scope>";
    
    /**
     * Default constructor
     */
    public StatisticsBean() 
    {
        InitialContext initialContext = null;
        
        sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        try
        {
            initialContext = new InitialContext();
            xmlTransforming = (XmlTransforming) initialContext.lookup(XmlTransforming.SERVICE_NAME);
            
            INSTANCE_ID = PropertyReader.getProperty("escidoc.faces.instance.id");
            
            REPORTDEFINITION_LOGIN = REPORTDEFINITION_LOGIN.replaceAll("INSTANCE_ID", INSTANCE_ID);
            REPORTDEFINITION_NUMBER_OF_VISITS = REPORTDEFINITION_NUMBER_OF_VISITS.replaceAll("INSTANCE_ID", INSTANCE_ID);
            REPORTDEFINITION_EXPORT = REPORTDEFINITION_EXPORT.replaceAll("INSTANCE_ID", INSTANCE_ID);
            REPORTDEFINITION_IMAGESEXPORT = REPORTDEFINITION_IMAGESEXPORT.replaceAll("INSTANCE_ID", INSTANCE_ID);
            
            // Get userHandle for admin and statistics user
            try
            {
                statisitcsEditorHandle = loginUser(
                        PropertyReader.getProperty("escidoc.faces.statistics.username"),
                        PropertyReader.getProperty("escidoc.faces.statistics.password"));
            }
            catch (Exception e)
            {
                sessionBean.setMessage("Statistics user doesn't exist on FW. Check config file or FW");
            }
            
            
            adminUserHandle = loginUser(
                    PropertyReader.getProperty("framework.admin.username"),
                    PropertyReader.getProperty("framework.admin.password"));
            
            // initialize FW configuration
            if (validFrameworkConfig())
            { 
                // initialize report definition
                initReportDefinitionFromFW();
                // Get sopeHandler
                scopeHandler = ServiceLocator.getScopeHandler(statisitcsEditorHandle);
            }
        }
        catch (Exception e)
        {
           throw new RuntimeException("Error initialization Statistics Bean", e);
        }
    }

	private boolean validFrameworkConfig() throws Exception
    {        
        //TODO: make the statistics working with a special Faces scope!
        // retrieve the scope for faces
        //facesScope = retrieveFacesScope();
        
        // define the default scope for statistics
        facesScopeID = "1";
        
        if (statisitcsEditorHandle != null && facesScopeID != null)
        {
                      
            // retrieve userAccountHandler
            String xmlUser = ServiceLocator.getUserAccountHandler(adminUserHandle).retrieve(statisitcsEditorHandle);
            AccountUserVO userVO = xmlTransforming.transformToAccountUser(xmlUser);
            
            // retrieve grants of the user
            String userGrantXML = ServiceLocator
                .getUserAccountHandler(adminUserHandle)
                    .retrieveCurrentGrants(userVO.getReference().getObjectId());
            
            List<GrantVO> grants = xmlTransforming.transformToGrantVOList(userGrantXML);
            
            // check rights
            boolean editor = false;
            for (GrantVO grantVO : grants)
            {
                if ("escidoc:role-statistics-editor".equals(grantVO.getRole()))
                {
                    // config ok
                    editor = true;
                    return true;
                    
                    // For testing 
                    //revokeGrants(userVO.getReference().getObjectId(), grantVO);
                }
            }
            
            // Add statistic editor grant to the user
            if (!editor)
            {
                createStatisticsEditorGrant(userVO.getReference().getObjectId());
            }
            
            return true;
        }
        
        return false;
    }
    
    private void createStatisticsEditorGrant(String userID) throws Exception
    {
        UserAccountHandler userAccountHandler = ServiceLocator.getUserAccountHandler(adminUserHandle);
        
        GrantVO grant = new GrantVO();
        grant.setRole("escidoc:role-statistics-editor");
        grant.setObjectRef(facesScopeID);
        
        String grantXML = xmlTransforming.transformToGrant(grant);
        
        userAccountHandler.createGrant(userID, grantXML);
    }
    
    private void revokeGrants(String userID, GrantVO grant) throws Exception
    {
        UserAccountHandler userAccountHandler = ServiceLocator.getUserAccountHandler(adminUserHandle);
        
        String  grantsFilterXml = "<param>" +
        		"<filter name=\"http://purl.org/dc/elements/1.1/identifier\">" +
        		"<id>" + grant.getReference().getObjectId() + "</id></filter>" +
        		"<revocation-remark>Test</revocation-remark>" +
        		"</param>";
        
        String grantFilterXml = "<param last-modification-date=\"" + "2009-01-27T09:50:18.342Z" + "\">" +
        "<revocation-remark>Test</revocation-remark>" +
        "</param>";
        
        //userAccountHandler.revokeGrants(userID, grantsFilterXml);
        
        userAccountHandler.revokeGrant(userID, grant.getReference().getObjectId(), grantFilterXml);
    }
    
    /**
     * Retrieve the Faces Scope if it exists. If not, create one
     * @return the objid of the Faces scope
     */
    private String retrieveFacesScope()
    {
        String filterXML = "<param><filter name=\"/id\">4</filter></param>";
        String filterToGetLeerList = "Nothing for Faces solution</filter></param>";
        String scopeRetrievedXml = null;
        String noScopeFoundXml = null;
        try
        {
            noScopeFoundXml= scopeHandler.retrieveScopes(filterToGetLeerList);
            scopeRetrievedXml = scopeHandler.retrieveScopes(filterXML);
        }
        catch (Exception e)
        {
            throw new RuntimeException("error retrieving Faces statistics scope", e);
        }
        
        if (scopeRetrievedXml.equals(noScopeFoundXml))
        {
            createScope();
        }
        
        return "4";
       
    }
    
    /**
     * Create the scope for Faces if it doesn't exists
     * @throws Exception
     */
    private void createScope()
    {
        try
        {
            scopeHandler.create(scopeXML);
        }
        
        catch (Exception e)
        {
            throw new RuntimeException("error creating Faces statistics scope", e);
        }
        
    }
    
    
    public String getStatisticsNumberOfVisits() throws Exception
    { 
    	// Initialize the report definition without ID
        StatisticReportDefinitionVO reportDefinitionVisitVO = new StatisticReportDefinitionVO();
        reportDefinitionVisitVO.setName("Faces page visits");
        reportDefinitionVisitVO.setScopeID(facesScopeID);
        reportDefinitionVisitVO.setSql(REPORTDEFINITION_NUMBER_OF_VISITS);
        // Set the id of the report definition
        reportDefinitionVisitVO = setReportDefinitionID(reportDefinitionVisitVO);
        // Get the report Record list
        List<StatisticReportRecordVO> reportRecordList = getStatisticReportRecord(reportDefinitionVisitVO.getObjectId());
        //Creation of the chart
        numberOfVisitCharts = new Charts(reportRecordList, "Visit", this.firstMonth, this.firstYear);
        this.firstMonth = numberOfVisitCharts.getFirstMonth();
        this.firstYear = numberOfVisitCharts.getFirstYear();
        // Creation of the table of statistics
        visitTable = new FacesStatisticsTable(reportRecordList, this.firstMonth, this.firstYear);
        
        return "";
    }
        
    public String getStatisticsNumberOfImagesExported() throws Exception
    {
        // Initialize the report definition without ID
        StatisticReportDefinitionVO reportDefinitionVisitVO = new StatisticReportDefinitionVO();
        reportDefinitionVisitVO.setName("Faces images exported");
        reportDefinitionVisitVO.setScopeID(facesScopeID);
        reportDefinitionVisitVO.setSql(REPORTDEFINITION_IMAGESEXPORT);
        // Set the id of the report definition
        reportDefinitionVisitVO = setReportDefinitionID(reportDefinitionVisitVO);
        // Get the report Record list
        List<StatisticReportRecordVO> reportRecordList = getStatisticReportRecord(reportDefinitionVisitVO.getObjectId());
        //Creation of the chart
        numberOfImagesExportedCharts = new Charts(reportRecordList, "Image" , this.firstMonth, this.firstYear);
        this.firstMonth = numberOfImagesExportedCharts.getFirstMonth();
        this.firstYear = numberOfImagesExportedCharts.getFirstYear();
        // Creation of the table of statistics
        imagesTable = new FacesStatisticsTable(reportRecordList, this.firstMonth, this.firstYear);
        
        return "";
    }
    
    public String getStatisticsNumberOfExport() throws Exception
    {
        // Initialize the report definition without ID
        StatisticReportDefinitionVO reportDefinitionVisitVO = new StatisticReportDefinitionVO();
        reportDefinitionVisitVO.setName("Faces export");
        reportDefinitionVisitVO.setScopeID(facesScopeID);
        reportDefinitionVisitVO.setSql(REPORTDEFINITION_EXPORT);
        // Set the id of the report definition
        reportDefinitionVisitVO = setReportDefinitionID(reportDefinitionVisitVO);
        // Get the report Record list
        List<StatisticReportRecordVO> reportRecordList = getStatisticReportRecord(reportDefinitionVisitVO.getObjectId());
        //Creation of the chart
        numberOfExportCharts = new Charts(reportRecordList, "Export" , this.firstMonth, this.firstYear);
        this.firstMonth = numberOfExportCharts.getFirstMonth();
        this.firstYear = numberOfExportCharts.getFirstYear();
        // Creation of the table of statistics
        exportTable = new FacesStatisticsTable(reportRecordList, this.firstMonth, this.firstYear);
        
        return "";
    }
    
    public String getStatisticsNumberOfLogin() throws Exception
    {
        // Initialize the report definition without ID
        StatisticReportDefinitionVO reportDefinitionVisitVO = new StatisticReportDefinitionVO();
        reportDefinitionVisitVO.setName("Faces login");
        reportDefinitionVisitVO.setScopeID(facesScopeID);
        reportDefinitionVisitVO.setSql(REPORTDEFINITION_LOGIN);
        // Set the id of the report definition
        reportDefinitionVisitVO = setReportDefinitionID(reportDefinitionVisitVO);
        List<StatisticReportRecordVO> reportRecordList = getStatisticReportRecord(reportDefinitionVisitVO.getObjectId());
        // Creation of the chart
        numberOfLoginCharts = new Charts(reportRecordList, "Login" , this.firstMonth, this.firstYear);
        this.firstMonth = numberOfLoginCharts.getFirstMonth();
        this.firstYear = numberOfLoginCharts.getFirstYear();
        // Creation of the table of statistics
        loginTable = new FacesStatisticsTable(reportRecordList, this.firstMonth, this.firstYear);
        
        return "";
    }
    
    
    public  List<StatisticReportRecordVO> getStatisticReportRecord(String reportDefinitionID) throws Exception
    {
        StatisticReportParamsVO repParams = new StatisticReportParamsVO();
        repParams.setReportDefinitionId(reportDefinitionID);
        repParams.setParamList(new ArrayList<StatisticReportRecordParamVO>());
        
        String xmlParams = xmlTransforming.transformToStatisticReportParameters(repParams);
        
        ReportHandler repHandler;
        
       if (adminUserHandle == null )
        {
            repHandler = ServiceLocator.getReportHandler();
        }
        else
        {
            repHandler = ServiceLocator.getReportHandler(adminUserHandle);
        }
        
        String xmlReport = repHandler.retrieve(xmlParams);
        
        List<StatisticReportRecordVO> reportRecordList = xmlTransforming.transformToStatisticReportRecordList(xmlReport);
        
        return reportRecordList;
    }
    
    private StatisticReportDefinitionVO setReportDefinitionID(StatisticReportDefinitionVO reportDefinitionWithoutId) throws Exception
    {
        StatisticReportDefinitionVO repDefFW = null;
        
        if (reportDefinitionMap != null)
        {
            reportDefinitionMap.get(reportDefinitionWithoutId.getSql());
        }
        
        //Report Definition already existing
        if(repDefFW != null) 
        {
            return repDefFW;           
        }
        else
        {
            return createReportDefinitionId(reportDefinitionWithoutId);
            
        }
    }
    
    /**
     * Retrieve all the report definition from the FW
     * @throws Exception
     */
    private void initReportDefinitionFromFW() throws Exception
    {
        ReportDefinitionHandler repDefHandler = ServiceLocator.getReportDefinitionHandler(adminUserHandle);
        HashMap<String, String> hm = new HashMap<String, String>();
        hm.put("default", "anonymous");
        String repDefFrameworkListXML = repDefHandler.retrieveReportDefinitions(hm);
        List<StatisticReportDefinitionVO> reportDefinitionList = xmlTransforming.transformToStatisticReportDefinitionList(repDefFrameworkListXML);
        
        reportDefinitionMap = new HashMap<String, StatisticReportDefinitionVO>();
        
        for (StatisticReportDefinitionVO repDef : reportDefinitionList)
        {
            reportDefinitionMap.put(repDef.getSql(), repDef);
        }
    }
    
    /**
     * Create a report definition on the FW and return its id
     * @param userHandle
     * @param reportDefVO
     * @return
     * @throws Exception
     */
    private StatisticReportDefinitionVO createReportDefinitionId(StatisticReportDefinitionVO reportDefVO) throws Exception
    {
        ReportDefinitionHandler repDefHandler = ServiceLocator.getReportDefinitionHandler(adminUserHandle);
        
        reportDefVO.setObjectId("1");
        String repDefFileXML = xmlTransforming.transformToStatisticReportDefinition(reportDefVO);
        repDefFileXML = repDefFileXML.replaceAll("</report-definition:sql>", "</report-definition:sql> <report-definition:allowed-roles><report-definition:allowed-role objid=\"default-user\"/></report-definition:allowed-roles>");
        String repDefFWXMLNew = repDefHandler.create(repDefFileXML);
        StatisticReportDefinitionVO repDefFWNew = xmlTransforming.transformToStatisticReportDefinition(repDefFWXMLNew);
        
        return repDefFWNew;
    }
    
    /**
     * log in to the FW
     * @param userid
     * @param password
     * @return
     * @throws ServiceException
     * @throws HttpException
     * @throws IOException
     * @throws URISyntaxException
     */
    private static String loginUser(String userid, String password) throws ServiceException, HttpException, IOException, URISyntaxException
    {
        String frameworkUrl = ServiceLocator.getFrameworkUrl();
        StringTokenizer tokens = new StringTokenizer( frameworkUrl, "//" );
                
        tokens.nextToken();
        StringTokenizer hostPort = new StringTokenizer(tokens.nextToken(), ":");
        
        String host = hostPort.nextToken();
        int port = Integer.parseInt( hostPort.nextToken() );
        
        HttpClient client = new HttpClient();
        client.getHostConfiguration().setHost( host, port, "http");
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        
        PostMethod login = new PostMethod( frameworkUrl + "/aa/j_spring_security_check");
        login.addParameter("j_username", userid);
        login.addParameter("j_password", password);
        
        client.executeMethod(login);
        //System.out.println("Login form post: " + login.getStatusLine().toString());
                
        login.releaseConnection();
        CookieSpec cookiespec = CookiePolicy.getDefaultSpec();
        Cookie[] logoncookies = cookiespec.match(
                host, port, "/", false, 
                client.getState().getCookies());
        
        Cookie sessionCookie = logoncookies[0];
        
        PostMethod postMethod = new PostMethod("/aa/login");
        postMethod.addParameter("target", frameworkUrl);
        client.getState().addCookie(sessionCookie);
        client.executeMethod(postMethod);
        //System.out.println("Login second post: " + postMethod.getStatusLine().toString());
      
        if (HttpServletResponse.SC_SEE_OTHER != postMethod.getStatusCode())
        {
            throw new HttpException("Wrong status code: " + login.getStatusCode());
        }
        
        String userHandle = null;
        Header headers[] = postMethod.getResponseHeaders();
        for (int i = 0; i < headers.length; ++i)
        {
            if ("Location".equals(headers[i].getName()))
            {
                String location = headers[i].getValue();
                int index = location.indexOf('=');
                userHandle = new String(Base64.decode(location.substring(index + 1, location.length())));
                //System.out.println("location: "+location);
                //System.out.println("handle: "+userHandle);
            }
        }
        
        if (userHandle == null)
        {
            throw new ServiceException("User not logged in.");
        }
        return userHandle;
    }

    public Charts getNumberOfVisitCharts()
    {
        return numberOfVisitCharts;
    }

    public void setNumberOfVisitCharts(Charts numberOfVisitCharts)
    {
        this.numberOfVisitCharts = numberOfVisitCharts;
    }

    public Charts getNumberOfLoginCharts()
    {
        return numberOfLoginCharts;
    }

    public void setNumberOfLoginCharts(Charts numberOfLoginCharts)
    {
        this.numberOfLoginCharts = numberOfLoginCharts;
    }
    
    public String getStatisticsType()
    {
        UrlHelper urlHelper = (UrlHelper) BeanHelper.getRequestBean(UrlHelper.class);
        sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        String action = urlHelper.getAction();
        
        if (action != null) 
        {
        	sessionBean.setAction(action);
		}
        
        return action;
    }

    public String getAdminUserHandle() throws HttpException, ServiceException, IOException, URISyntaxException
    {
        adminUserHandle = loginUser(
                PropertyReader.getProperty("framework.admin.username"),
                PropertyReader.getProperty("framework.admin.password"));
        
        return adminUserHandle;
    }

    public void setAdminUserHandle(String adminUserHandle)
    {
        this.adminUserHandle = adminUserHandle;
    }

    public Charts getNumberOfExportCharts()
    {
        return numberOfExportCharts;
    }

    public void setNumberOfExportCharts(Charts numberOfExportCharts)
    {
        this.numberOfExportCharts = numberOfExportCharts;
    }

    public Charts getNumberOfImagesExportedCharts()
    {
        return numberOfImagesExportedCharts;
    }

    public void setNumberOfImagesExportedCharts(Charts numberOfImagesExportedCharts)
    {
        this.numberOfImagesExportedCharts = numberOfImagesExportedCharts;
    }

    public FacesStatisticsTable getLoginTable()
    {
        return loginTable;
    }

    public void setLoginTable(FacesStatisticsTable loginTable)
    {
        this.loginTable = loginTable;
    }

    public FacesStatisticsTable getVisitTable()
    {
        return visitTable;
    }

    public void setVisitTable(FacesStatisticsTable visitTable)
    {
        this.visitTable = visitTable;
    }

    public FacesStatisticsTable getExportTable()
    {
        return exportTable;
    }

    public void setExportTable(FacesStatisticsTable exportTable)
    {
        this.exportTable = exportTable;
    }

    public FacesStatisticsTable getImagesTable()
    {
        return imagesTable;
    }

    public void setImagesTable(FacesStatisticsTable imagesTable)
    {
        this.imagesTable = imagesTable;
    }

    public String getStatisitcsEditorHandle()
    {
        return statisitcsEditorHandle;
    }

    public void setStatisitcsEditorHandle(String statisitcsEditorHandle)
    {
        this.statisitcsEditorHandle = statisitcsEditorHandle;
    }
    
    public int getFirstMonth() throws Exception 
    {
    	if (firstMonth == 0) 
		{
    		this.getStatisticsNumberOfExport();
		}
    	return firstMonth;
	}

	public void setFirstMonth(int firstMonth) 
	{
		this.firstMonth = firstMonth;
	}

	public int getLastMonth() 
	{
		return lastMonth;
	}

	public void setLastMonth(int lastMonth) 
	{
		this.lastMonth = lastMonth;
	}

	public int getFirstYear() throws Exception 
	{
		if (firstYear == 0) 
		{
			this.getStatisticsNumberOfExport();
		}
		return firstYear;
	}

	public void setFirstYear(int firstYear) 
	{
		this.firstYear = firstYear;
	}

	public int getLastYear() 
	{
		return lastYear;
	}

	public void setLastYear(int lastYear) 
	{
		this.lastYear = lastYear;
	}
	
	public void monthListener(ValueChangeEvent event)
	{
		if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) 
    	{
			this.firstMonth = Integer.parseInt(event.getNewValue().toString());
		}
	}
	
	public void yearListener(ValueChangeEvent event)
	{
		if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) 
    	{
			this.firstYear = Integer.parseInt(event.getNewValue().toString());
		}
	}

	public void changeDate() throws IOException
	{
		Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
		String statisiticType = sessionBean.getAction();
		FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getStatisticsUrl() + "/" + statisiticType);
	}
}

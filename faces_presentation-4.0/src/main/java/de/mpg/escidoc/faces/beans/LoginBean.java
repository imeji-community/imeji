package de.mpg.escidoc.faces.beans;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.httpclient.HttpException;

import de.mpg.escidoc.faces.metastore.controller.UserController;
import de.mpg.escidoc.faces.metastore.vo.User;
import de.mpg.escidoc.faces.statistics.StatisticsBean;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class LoginBean {
	
	private String login;
	
	private String passwd;

	private SessionBean sb;
	
	public LoginBean()
	{
		this.sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	}
	
	public void setLogin(String login) {
		this.login = login;
	}

	public String getLogin() {
		return login;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public String getPasswd() {
		return passwd;
	}
	
	public String login()
	{
		UserController uc = new UserController(null);
		
		try {
			User user = uc.retrieve(getLogin());
			if(user.getEncryptedPassword().equals(UserController.convertToMD5(getPasswd())))
			{
				sb.setUser(user);
				createLoginStatisticData(user);
				sb.setInformation(sb.getMessage("success_log_in"));
			}
			
		} catch (Exception e) {
			sb.setInformation("User or password are wrong.");
		}
		
		return "";
	}
	
	public String logout()
	{
		sb.setUser(null);
		sb.setInformation(sb.getMessage("success_log_out"));
		return "";
	}
	
	 private void  createLoginStatisticData(User user) throws HttpException, ServiceException, IOException, URISyntaxException
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
	                "<parameter name=\"user_id\"><stringvalue>" + user.getEmail() + "</stringvalue></parameter>" +
	                "</statistic-record>";
//	        StatisticsBean statisticsBean = (StatisticsBean) BeanHelper.getRequestBean(StatisticsBean.class);
//	        ServiceLocator.getStatisticDataHandler(statisticsBean.getStatisitcsEditorHandle()).create(statisticDataXml);
	        StatisticsBean statisticsBean = (StatisticsBean) BeanHelper.getApplicationBean(StatisticsBean.class);
	        
	        ServiceLocator.getStatisticDataHandler(statisticsBean.getAdminUserHandle()).create(statisticDataXml);
	    }
	
	

}

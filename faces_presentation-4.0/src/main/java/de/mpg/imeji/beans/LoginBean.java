package de.mpg.imeji.beans;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import org.apache.commons.httpclient.HttpException;
import org.apache.log4j.Logger;

import de.mpg.escidoc.services.framework.ServiceLocator;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.UserController;
import de.mpg.jena.vo.User;

public class LoginBean
{
    private String login;
    private String passwd;
    private SessionBean sb;
    
    private Logger logger = Logger.getLogger(LoginBean.class);

    public LoginBean()
    {
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getLogin()
    {
        return login;
    }

    public void setPasswd(String passwd)
    {
        this.passwd = passwd;
    }

    public String getPasswd()
    {
        return passwd;
    }

    public String login() throws Exception
    {
        UserController uc = new UserController(null);
        try
        {
            User user = uc.retrieve(getLogin());
            if (user.getEncryptedPassword().equals(UserController.convertToMD5(getPasswd())))
            {
                sb.setUser(user);
                BeanHelper.info(sb.getMessage("success_log_in"));
                try
                {
                    createLoginStatisticData(user);
                }
                catch (Exception e)
                {
                    logger.error("Error creating statistical data", e);
                }
            }
            else
            {
                BeanHelper.error(sb.getMessage("error_log_in"));
            }
        }
        catch (Exception e)
        {
            BeanHelper.error(sb.getMessage("error_log_in"));
            logger.error("Problem logging in User", e);
        }
        return "pretty:";
    }

    public String logout()
    {
        sb.setUser(null);
        BeanHelper.info(sb.getMessage("success_log_out"));
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fc.getExternalContext().getSession(false);
        session.invalidate();
        return "pretty:home";
    }

    private void createLoginStatisticData(User user) throws HttpException, ServiceException, IOException,
            URISyntaxException
    {
        
//        // Create a statistic data "visit" for statistics "number of visits"
//        String statisticDataXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
//                + "<statistic-record><scope objid=\"1\"/>" + "<parameter name=\"handler\"><stringvalue>"
//                + StatisticsBean.INSTANCE_ID + "</stringvalue></parameter>"
//                + "<parameter name=\"request\"><stringvalue>login</stringvalue></parameter>"
//                + "<parameter name=\"interface\"><stringvalue>SOAP</stringvalue></parameter>"
//                + "<parameter name=\"successful\"><stringvalue>1</stringvalue></parameter>"
//                + "<parameter name=\"internal\"><stringvalue>0</stringvalue></parameter>"
//                + "<parameter name=\"user_id\"><stringvalue>" + user.getEmail() + "</stringvalue></parameter>"
//                + "</statistic-record>";
//        // StatisticsBean statisticsBean = (StatisticsBean) BeanHelper.getRequestBean(StatisticsBean.class);
//        // ServiceLocator.getStatisticDataHandler(statisticsBean.getStatisitcsEditorHandle()).create(statisticDataXml);
//        StatisticsBean statisticsBean = (StatisticsBean)BeanHelper.getApplicationBean(StatisticsBean.class);
//        ServiceLocator.getStatisticDataHandler(statisticsBean.getAdminUserHandle()).create(statisticDataXml);
        
    }
}

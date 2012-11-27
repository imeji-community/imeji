/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.beans;

import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.BeanHelper;

public class LoginBean
{
    private String login;
    private String passwd;
    private SessionBean sb;
    private static Logger logger = Logger.getLogger(LoginBean.class);

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

    public String doLogin() throws Exception
    {
        UserController uc = new UserController(null);
        try
        {
            User user = uc.retrieve(getLogin());
            if (user.getEncryptedPassword().equals(UserController.convertToMD5(getPasswd())))
            {
                sb.setUser(user);
                BeanHelper.info(sb.getMessage("success_log_in"));
            }
            else
            {
                BeanHelper.error(sb.getMessage("error_log_in"));
                BeanHelper.error(sb.getMessage("error_log_in_description"));
            }
        }
        catch (Exception e)
        {
            BeanHelper.error(sb.getMessage("error_log_in"));
            BeanHelper.error(sb.getMessage("error_log_in_description"));
            logger.error("Problem logging in User", e);
        }
        return "pretty:";
    }

    public boolean loginWithEscidocAccount()
    {
        return false;
    }

    public boolean loginWithImejiAccount()
    {
        return false;
    }

    public String logout()
    {
        Locale locale = sb.getLocale();
        sb.setUser(null);
        BeanHelper.info(sb.getMessage("success_log_out"));
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession)fc.getExternalContext().getSession(false);
        session.invalidate();
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        sb.setLocale(locale);
        return "pretty:home";
    }
}

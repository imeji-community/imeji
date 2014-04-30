/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.user;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.auth.Authentication;
import de.mpg.imeji.logic.auth.AuthenticationFactory;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.history.HistorySession;
import de.mpg.imeji.presentation.history.Page;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * Bean for login features
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class LoginBean
{
    private String login;
    private String passwd;
    private SessionBean sb;
    private static Logger logger = Logger.getLogger(LoginBean.class);

    /**
     * Constructor
     */
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

    public void doLogin() throws Exception
    {
        Authentication auth = AuthenticationFactory.factory(getLogin(), getPasswd());
        User user = auth.doLogin();
        if (user != null)
        {
            sb.setUser(user);
            BeanHelper.info(sb.getMessage("success_log_in"));
        }
        else
        {
            BeanHelper.error(sb.getMessage("error_log_in").replace("XXX_INSTANCE_NAME_XXX",
                    PropertyReader.getProperty("imeji.instance.name")));
            BeanHelper.error(sb.getMessage("error_log_in_description").replace("XXX_INSTANCE_NAME_XXX",
                    PropertyReader.getProperty("imeji.instance.name")));
        }
        Page current = ((HistorySession)BeanHelper.getSessionBean(HistorySession.class)).getCurrentPage();
        String redirectAfterLogin = "";
        if (current != null)
        {
            redirectAfterLogin = current.getCompleteUrl();
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect(redirectAfterLogin);
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
        sb.setUser(null);
        BeanHelper.info(sb.getMessage("success_log_out"));
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession)fc.getExternalContext().getSession(false);
        session.invalidate();
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        return "pretty:home";
    }
}

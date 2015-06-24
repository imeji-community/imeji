package de.mpg.imeji.presentation.user;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import static de.mpg.imeji.logic.util.StringHelper.isNullOrEmptyTrim;

/**
 * Bean for registration workflow
 *
 * @author makarenko (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "RegistrationBean")
@ViewScoped
public class RegistrationBean {

    private static Logger logger = Logger.getLogger(RegistrationBean.class);

    private SessionBean sb;
    private User user;
    private String token = null;
    private String orga = null;

    @PostConstruct
    public void init()
    {
        sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        //get token etc
        this.token = UrlHelper.getParameterValue("token");
        if (isNullOrEmptyTrim(token)) {
            doActivation();
        } else {
        }
    }

    /*public String registrate() {

        if (user.getPerson() == null
                || isNullOrEmptyTrim(user.getPerson().getFamilyName())) {
            BeanHelper.error(sb.getMessage("error_user_name_unfilled"));
            if (!isValidEmail(user.getEmail()))
                BeanHelper.error(sb.getMessage("error_user_email_not_valid"));
        } else if (!isValidEmail(user.getEmail())) {
            BeanHelper.error(sb.getMessage("error_user_email_not_valid"));
        } else {
            try {
                if (userAlreadyExists(user)) {
                    BeanHelper
                            .error(sb.getMessage("error_user_already_exists"));
                } else {
                    String password = createNewUser();
                    if (sendEmail) {
                        sendNewAccountEmail(password);
                    }
                    logger.info("New user created: " + user.getEmail());
                    BeanHelper.info(sb.getMessage("success_user_create"));
                    return sb.getPrettySpacePage("pretty:users");
                }
            } catch (Exception e) {
                BeanHelper.error(sb.getMessage("error") + ": " + e);
            }
        }
        return "pretty:";
    }*/
    
    private void doActivation() {
        //retrieve
        UserController uc = new UserController(Imeji.adminUser);
        try {
            User user = uc.activate(this.token);
            BeanHelper.info(sb.getMessage("success_activation"));
        } catch (ImejiException e) {
            BeanHelper.error(e.getLocalizedMessage());
            //TODO: redirect?
        }
    }


}

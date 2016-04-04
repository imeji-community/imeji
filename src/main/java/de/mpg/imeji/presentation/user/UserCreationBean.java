/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.user;

import java.io.IOException;
import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.util.PasswordGenerator;
import de.mpg.imeji.logic.collaboration.email.EmailMessages;
import de.mpg.imeji.logic.collaboration.email.EmailService;
import de.mpg.imeji.logic.resource.controller.UserController;
import de.mpg.imeji.logic.resource.controller.UserController.USER_TYPE;
import de.mpg.imeji.logic.resource.util.ImejiFactory;
import de.mpg.imeji.logic.resource.vo.Organization;
import de.mpg.imeji.logic.resource.vo.User;
import de.mpg.imeji.logic.util.QuotaUtil;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Java Bean for the Create new user page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class UserCreationBean extends QuotaSuperBean {
  private User user;
  private SessionBean sb;
  private boolean sendEmail = false;
  private static final Logger LOGGER = Logger.getLogger(UserCreationBean.class);
  private boolean allowedToCreateCollection = true;


  /**
   * Construct new bean
   */
  public UserCreationBean() {
    super();
    sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
    this.setUser(new User());
  }

  /**
   * Method called when user create a new user
   * 
   * @return
   * @throws Exception
   */
  public String create() {
    try {
      String password = createNewUser();
      if (sendEmail) {
        sendNewAccountEmail(password);
      }
      BeanHelper.info(Imeji.RESOURCE_BUNDLE.getMessage("success_user_create", sb.getLocale()));
      reloadUserPage();
    } catch (UnprocessableError e) {
      BeanHelper.cleanMessages();
      BeanHelper
          .error(Imeji.RESOURCE_BUNDLE.getMessage("error_during_user_create", sb.getLocale()));
      for (String errorM : e.getMessages()) {
        BeanHelper.error(Imeji.RESOURCE_BUNDLE.getMessage(errorM, sb.getLocale()));
      }
    } catch (Exception e) {
      LOGGER.error("Error creating user:", e);
      BeanHelper.error(Imeji.RESOURCE_BUNDLE.getMessage(e.getMessage(), sb.getLocale()));
    }
    return "";
  }

  /**
   * Create a new {@link User}
   * 
   * @throws Exception
   */
  private String createNewUser() throws Exception {
    UserController uc = new UserController(sb.getUser());
    PasswordGenerator generator = new PasswordGenerator();
    String password = generator.generatePassword();
    user.setEncryptedPassword(StringHelper.convertToMD5(password));
    user.setQuota(QuotaUtil.getQuotaInBytes(getQuota()));
    uc.create(user, allowedToCreateCollection ? USER_TYPE.DEFAULT : USER_TYPE.RESTRICTED);
    return password;
  }



  /**
   * True if the {@link User} exists
   * 
   * @return
   * @throws Exception
   */
  public static boolean userAlreadyExists(User user) throws Exception {
    try {
      SessionBean session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
      UserController uc = new UserController(session.getUser());
      uc.retrieve(user.getEmail());
      return true;
    } catch (NotFoundException e) {
      LOGGER.info("User not found: " + user.getEmail());
      return false;
    }
  }

  /**
   * Send an email to the current {@link User}
   * 
   * @param password
   */
  public void sendNewAccountEmail(String password) {
    EmailService emailClient = new EmailService();
    try {
      emailClient.sendMail(user.getEmail(), null,
          EmailMessages.getEmailOnAccountAction_Subject(true, sb.getLocale()),
          EmailMessages.getNewAccountMessage(password, user.getEmail(),
              user.getPerson().getCompleteName(), sb.getLocale()));
    } catch (Exception e) {
      LOGGER.error("Error sending email", e);
      BeanHelper.error("Error: Email not sent");
    }
  }

  /**
   * Add a new empty organization
   * 
   * @param index
   */
  public void addOrganization(int index) {
    ((List<Organization>) this.user.getPerson().getOrganizations()).add(index,
        ImejiFactory.newOrganization());
  }

  /**
   * Remove an nth organization
   * 
   * @param index
   */
  public void removeOrganization(int index) {
    List<Organization> orgas = (List<Organization>) this.user.getPerson().getOrganizations();
    if (orgas.size() > 1) {
      orgas.remove(index);
    }
  }

  /**
   * setter
   * 
   * @param user
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * getter
   * 
   * @return
   */
  public User getUser() {
    return user;
  }

  /**
   * getter - True if the selectbox "send email to user" has been selected
   * 
   * @return
   */
  public boolean isSendEmail() {
    return sendEmail;
  }

  /**
   * setter
   * 
   * @param sendEmail
   */
  public void setSendEmail(boolean sendEmail) {
    this.sendEmail = sendEmail;
  }

  /**
   * @return the allowedToCreateCollection
   */
  public boolean isAllowedToCreateCollection() {
    return allowedToCreateCollection;
  }

  /**
   * @param allowedToCreateCollection the allowedToCreateCollection to set
   */
  public void setAllowedToCreateCollection(boolean allowedToCreateCollection) {
    this.allowedToCreateCollection = allowedToCreateCollection;
  }


  private void reloadUserPage() {
    try {
      Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
      FacesContext.getCurrentInstance().getExternalContext()
          .redirect(navigation.getUserUrl() + "?id=" + user.getEmail());
    } catch (IOException e) {
      Logger.getLogger(UserBean.class).info("Some reloadPage exception", e);
    }
  }


}



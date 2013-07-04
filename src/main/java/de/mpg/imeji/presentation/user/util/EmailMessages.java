/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.user.util;

import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * List of text (messages) sent from imeji to users via email
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class EmailMessages
{
    /**
     * Email content when a new Account is sent
     * 
     * @param password
     * @param email
     * @param username
     * @return
     */
    public String getNewAccountMessage(String password, String email, String username)
    {
        return getEmailMessage(password, email, username, "email_new_user");
    }

    /**
     * Email content when a new password is sent
     * 
     * @param password
     * @param email
     * @param username
     * @return
     */
    public String getNewPasswordMessage(String password, String email, String username)
    {
        return getEmailMessage(password, email, username, "email_new_password");
    }

    /**
     * Email content when a collection has been shared with the addressee by the sender
     * 
     * @param sender
     * @param dest
     * @param collectionName
     * @param collectionLink
     * @return
     */
    public String getSharedCollectionMessage(String sender, String dest, String collectionName, String collectionLink, String role)
    {
        String message = getBundle("email_shared_collection");
        message = message.replace("XXX_USER_NAME_XXX,", dest).replace("XXX_NAME_XXX", collectionName)
                .replace("XXX_LINK_XXX", collectionLink).replace("XXX_SENDER_NAME_XXX", sender).replace("XXX_ROLE_XXX", role);
        return message;
    }

    /**
     * Email content when an album has been shared with the addressee by the sender
     * 
     * @param sender
     * @param dest
     * @param collectionName
     * @param collectionLink
     * @return
     */
    public String getSharedAlbumMessage(String sender, String dest, String collectionName, String collectionLink, String role)
    {
        String message = getBundle("email_shared_album");
        message = message.replace("XXX_USER_NAME_XXX,", dest).replace("XXX_NAME_XXX", collectionName)
                .replace("XXX_LINK_XXX", collectionLink).replace("XXX_SENDER_NAME_XXX", sender).replace("XXX_ROLE_XXX", role);
        return message;
    }

    /**
     * Read the message bundle (for example: messages_en.properties)
     * 
     * @param messageBundle
     * @return
     */
    private String getBundle(String messageBundle)
    {
        SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        return session.getMessage(messageBundle);
    }

    /**
     * Create the content of an email according to the parameters
     * 
     * @param password
     * @param email
     * @param username
     * @param message_bundle
     * @return
     */
    private String getEmailMessage(String password, String email, String username, String message_bundle)
    {
        Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        String userPage = navigation.getApplicationUrl() + "user?id=" + email;
        String emailMessage = session.getMessage(message_bundle);
        if ("email_new_user".equals(message_bundle))
        {
            emailMessage = emailMessage.replace("XXX_LINK_TO_APPLICATION_XXX", navigation.getApplicationUrl());
        }
        emailMessage = emailMessage.replace("XXX_USER_NAME_XXX,", username).replace("XXX_LOGIN_XXX", email)
                .replace("XXX_PASSWORD_XXX", password).replace("XXX_LINK_TO_USER_PAGE_XXX", userPage)
                .replaceAll("XXX_INSTANCE_NAME_XXX", session.getInstanceName());
        return emailMessage;
    }

    /**
     * Create the subject of the email being send, for either new account or new password
     * 
     * @param newAccount
     * @return
     */
    public String getEmailSubject(boolean newAccount)
    {
        SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        String emailsubject = "";
        if (newAccount)
        {
            emailsubject = session.getMessage("email_new_user_subject");
        }
        else
        {
            emailsubject = session.getMessage("email_new_password_subject");
        }
        return emailsubject.replaceAll("XXX_INSTANCE_NAME_XXX", session.getInstanceName());
    }
}

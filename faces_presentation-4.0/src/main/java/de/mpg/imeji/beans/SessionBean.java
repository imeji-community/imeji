package de.mpg.imeji.beans;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import de.mpg.jena.vo.User;

public class SessionBean
{
    private static Logger logger = Logger.getLogger(SessionBean.class);
    private User user = null;
    // Bundle
    public static final String LABEL_BUNDLE = "labels";
    public static final String MESSAGES_BUNDLE = "messages";
    public static final String METADATA_BUNDLE = "metadata";
    // His locale
    private Locale locale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();

    public SessionBean()
    {
        // TODO Auto-generated constructor stub
    }

    /**
     * Returns the label according to the current user locale.
     * 
     * @param placeholder A string containing the name of a label.
     * @return The label.
     */
    public String getLabel(String placeholder)
    {
        return ResourceBundle.getBundle(this.getSelectedLabelBundle()).getString(placeholder);
    }

    /**
     * Returns the message according to the current user locale.
     * 
     * @param placeholder A string containing the name of a message.
     * @return The label.
     */
    public String getMessage(String placeholder)
    {
        try
        {
            return ResourceBundle.getBundle(this.getSelectedMessagesBundle()).getString(placeholder);
        }
        catch (Exception e)
        {
            return placeholder;
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
        String language = (String)map.get("language");
        String country = (String)map.get("country");
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

    public void setLocale(final Locale userLocale)
    {
        this.locale = userLocale;
    }

    /**
     * @return the user
     */
    public User getUser()
    {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user)
    {
        this.user = user;
    }
}

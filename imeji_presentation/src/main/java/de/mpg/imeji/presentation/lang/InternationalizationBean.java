/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.PropertyReader;

public class InternationalizationBean
{
    private List<SelectItem> languages = null;
    List<SelectItem> isolanguages = null;
    private String currentLanguage = "en";
    private SessionBean session = null;
    private List<SelectItem> internationalizedLanguages;

    public InternationalizationBean()
    {
        session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        init();
        internationalizeLanguages();
        setCurrentLanguage(session.getLocale().getLanguage());
    }

    private void init()
    {
        try
        {
            languages = new ArrayList<SelectItem>();
            languages.add(new SelectItem("", "--"));
            Iso639_1Helper iso639_1Helper = new Iso639_1Helper();
            isolanguages = iso639_1Helper.getList();
            String supportedLanguages = null;
            try
            {
                supportedLanguages = PropertyReader.getProperty("imeji.i18n.languages");
            }
            catch (Exception e)
            {
                throw new RuntimeException("Error reading property imeji.i18n.languages. Check Propety file: " + e);
            }
            // Add first languages out of properties
            for (SelectItem iso : isolanguages)
            {
                for (int i = 0; i < supportedLanguages.split(",").length; i++)
                {
                    if (supportedLanguages.split(",")[i].equals(iso.getValue().toString()))
                    {
                        languages.add(iso);
                    }
                }
            }
            // Add than the other languages
            languages.add(new SelectItem("", "--"));
            for (SelectItem iso : isolanguages)
            {
                boolean isSupported = false;
                for (int i = 0; i < supportedLanguages.split(",").length; i++)
                {
                    if (supportedLanguages.split(",")[i].equals(iso.getValue().toString()))
                    {
                        isSupported = true;
                    }
                }
                if (!isSupported)
                    languages.add(iso);
                ;
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Languages for imeji internationalization
     */
    private void internationalizeLanguages()
    {
        internationalizedLanguages = new ArrayList<SelectItem>();
        try
        {
            String supportedLanguages = PropertyReader.getProperty("imeji.i18n.languages");
            for (SelectItem iso : isolanguages)
            {
                for (int i = 0; i < supportedLanguages.split(",").length; i++)
                {
                    if (supportedLanguages.split(",")[i].equals(iso.getValue().toString()))
                    {
                        internationalizedLanguages.add(new SelectItem(iso.getValue().toString(), iso.getLabel().split(
                                "-")[1]));
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error reading property imeji.i18n.languages. Check Propety file: " + e);
        }
    }

    public void setCurrentLanguage(String currentLanguage)
    {
        this.currentLanguage = currentLanguage;
        session.setLocale(new Locale(currentLanguage));
        internationalizeLanguages();
    }

    public String getCurrentLanguage()
    {
        return currentLanguage;
    }

    public List<SelectItem> getLanguages()
    {
        return languages;
    }

    public void setLanguages(List<SelectItem> languages)
    {
        this.languages = languages;
    }

    public List<SelectItem> getInternationalizedLanguages()
    {
        return internationalizedLanguages;
    }

    public void setInternationalizedLanguages(List<SelectItem> internationalizedLanguages)
    {
        this.internationalizedLanguages = internationalizedLanguages;
    }
}

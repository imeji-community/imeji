package de.mpg.j2j.misc;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Java representation for rdf label with a lang property
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@XmlType(name = "LocalizedString")
@XmlRootElement(name = "LocalizedString")
public class LocalizedString
{
    private String value;
    private String lang;

    /**
     * Default constructor
     */
    public LocalizedString()
    {
    }

    /**
     * New {@link LocalizedString} with a {@link String} in one language
     * 
     * @param value
     * @param lang
     */
    public LocalizedString(String value, String lang)
    {
        this.value = value;
        this.lang = lang;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getValue()
    {
        return value;
    }

    /**
     * setter
     * 
     * @param value
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getLang()
    {
        return lang;
    }

    /**
     * setter
     * 
     * @param lang
     */
    public void setLang(String lang)
    {
        this.lang = lang;
    }
}

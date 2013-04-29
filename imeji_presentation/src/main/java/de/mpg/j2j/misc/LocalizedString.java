package de.mpg.j2j.misc;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Java representation for rdf label with a lang property
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@XmlRootElement(name = "label", namespace = "http://imeji.org/terms")
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
    @XmlAttribute(name = "value")
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
    @XmlAttribute(name = "lang")
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

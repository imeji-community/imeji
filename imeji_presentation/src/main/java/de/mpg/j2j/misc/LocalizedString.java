package de.mpg.j2j.misc;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Java representation for rdf label with a lang property
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@XmlRootElement(name = "localizedString", namespace="http://www.w3.org/2000/01/rdf-schema-label")
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

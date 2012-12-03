package de.mpg.j2j.misc;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="LocalizedString")
@XmlRootElement(name="LocalizedString")
public class LocalizedString
{
    private String value;
    private String lang;

    public LocalizedString()
    {
        // TODO Auto-generated constructor stub
    }

    public LocalizedString(String value, String lang)
    {
        this.value = value;
        this.lang = lang;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getLang()
    {
        return lang;
    }

    public void setLang(String lang)
    {
        this.lang = lang;
    }
}

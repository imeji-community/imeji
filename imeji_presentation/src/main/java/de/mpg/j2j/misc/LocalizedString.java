package de.mpg.j2j.misc;

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

    public String getString()
    {
        return value;
    }

    public void setString(String value)
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

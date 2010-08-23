package de.mpg.imeji.mdProfile.wrapper;

import thewebsemantic.LocalizedString;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.md.ComplexType.AllowedTypes;

public class StatementWrapper extends Statement
{
    private boolean required = false;
    private boolean multiple = false;
    private AllowedTypes mdType = null;
    private String defaultLabel = "";
    private String defaultLanguage = "eng";

    public StatementWrapper(Statement st)
    {
        if (Integer.parseInt(st.getMinOccurs()) > 0)
        {
            required = true;
        }
        if (Integer.parseInt(st.getMaxOccurs()) > 1)
        {
            multiple = true;
        }
        this.getLabels().add(new LocalizedString(defaultLabel, defaultLanguage));
    }

    public AllowedTypes getMdType()
    {
        return mdType;
    }

    public void setMdType(AllowedTypes mdType)
    {
        this.mdType = mdType;
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }

    public boolean isMultiple()
    {
        return multiple;
    }

    public void setMultiple(boolean multiple)
    {
        this.multiple = multiple;
    }

    public String getDefaultLabel()
    {
        return defaultLabel;
    }

    public void setDefaultLabel(String defaultLabel)
    {
        this.defaultLabel = defaultLabel;
        this.getLabels().set(0, new LocalizedString(defaultLabel, defaultLanguage));
    }
}

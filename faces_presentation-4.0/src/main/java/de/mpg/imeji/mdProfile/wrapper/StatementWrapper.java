package de.mpg.imeji.mdProfile.wrapper;

import java.net.URI;

import javax.faces.event.ValueChangeEvent;

import thewebsemantic.LocalizedString;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.ComplexType.AllowedTypes;

public class StatementWrapper extends Statement
{
    private boolean required = false;
    private boolean multiple = false;
    private AllowedTypes mdType;
    private String defaultLabel = "";
    private String defaultLanguage = "eng";

    public StatementWrapper(Statement st)
    {
        this.setLiteralConstraints(st.getLiteralConstraints());
        if (Integer.parseInt(st.getMinOccurs()) > 0)
        {
            required = true;
        }
        if ("unbounded".equals(st.getMaxOccurs()) || Integer.parseInt(st.getMaxOccurs()) > 1)
        {
            multiple = true;
        }
        this.getLabels().add(new LocalizedString(defaultLabel, defaultLanguage));
        this.setType(st.getType());
        if (st.getType() != null)
        {
            for (AllowedTypes type : AllowedTypes.values())
            {
                URI uri = URI.create(type.getNamespace() + type.getRdfType());
                if (st.getType().equals(uri))
                {
                    this.setMdType(type);
                }
            }
        }
    }

    public void typeListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            this.mdType = AllowedTypes.valueOf(event.getNewValue().toString());
            this.setType(URI.create(mdType.getNamespace() + mdType.getRdfType()));
        }
    }

    public void requiredListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            if ((Boolean)event.getNewValue())
            {
                this.setMinOccurs("1");
            }
        }
    }

    public void multipleListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            if ((Boolean)event.getNewValue())
            {
                this.setMaxOccurs("unbounded");
            }
        }
    }

    public int getConstraintsSize()
    {
        return this.getLiteralConstraints().size();
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

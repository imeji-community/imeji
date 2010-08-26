package de.mpg.imeji.mdProfile.wrapper;

import java.net.URI;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import thewebsemantic.LocalizedString;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.ComplexType.AllowedTypes;

public class StatementWrapper extends Statement
{
    private boolean required = false;
    private boolean multiple = false;
    private AllowedTypes mdType;
    private String defaultLabel;

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
        if (st.getLabels().size() > 0)
        {
            this.defaultLabel = st.getLabels().get(0).toString();
        }
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
    
    public void constraintListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            int pos = Integer.parseInt(event.getComponent().getAttributes().get("position").toString());
            ((List<LocalizedString>)this.getLiteralConstraints()).set(pos, new LocalizedString(event.getNewValue().toString(), "eng"));
        }
    }
    
    public void labelListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            this.defaultLabel = event.getNewValue().toString();
            this.getLabels().clear();
            this.getLabels().add(new LocalizedString(defaultLabel, "eng"));
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
    }
}

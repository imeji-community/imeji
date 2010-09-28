package de.mpg.imeji.mdProfile.wrapper;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.richfaces.json.JSONCollection;
import org.richfaces.json.JSONException;

import thewebsemantic.LocalizedString;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.ComplexType.ComplexTypes;

public class StatementWrapper
{
    private boolean required = false;
    private boolean multiple = false;
    private Statement statement;
    private ComplexTypes mdType;
    private String defaultLabel;
    private String vocabularyString = null;

    public StatementWrapper(Statement st)
    {
        statement = st;
        statement.setLiteralConstraints(st.getLiteralConstraints());
        statement.setMinOccurs(st.getMinOccurs());
        statement.setMaxOccurs(st.getMaxOccurs());
        statement.setLabels(st.getLabels());
        statement.setType(st.getType());
        statement.setName(st.getName());
        statement.setVocabulary(st.getVocabulary());
        // Wrapper variable initialization
        if (Integer.parseInt(st.getMinOccurs()) > 0)
            required = true;
        if ("unbounded".equals(st.getMaxOccurs()) || Integer.parseInt(st.getMaxOccurs()) > 1)
            multiple = true;
        if (st.getLabels().size() > 0)
        {
            for (LocalizedString lstr : st.getLabels())
                this.defaultLabel = lstr.toString();
        }
        if (st.getType() != null)
        {
            for (ComplexTypes type : ComplexTypes.values())
            {
                URI uri = URI.create(type.getNamespace() + type.getRdfType());
                if (st.getType().equals(uri))
                    this.setMdType(type);
            }
        }
    }

    public void vocabularyListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
            this.statement.setVocabulary(URI.create(event.getNewValue().toString()));
        vocabularyString = statement.getVocabulary().toString();
    }

    public void constraintListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            int pos = Integer.parseInt(event.getComponent().getAttributes().get("position").toString());
            ((List<LocalizedString>)statement.getLiteralConstraints()).set(pos, new LocalizedString(event.getNewValue()
                    .toString(), "eng"));
        }
    }

    public void nameListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            this.defaultLabel = event.getNewValue().toString();
            this.getStatement().setName(defaultLabel);
            statement.getLabels().clear();
            statement.getLabels().add(new LocalizedString(defaultLabel, "eng"));
        }
    }

    public void typeListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            this.mdType = ComplexTypes.valueOf(event.getNewValue().toString());
            statement.setType(URI.create(mdType.getNamespace() + mdType.getRdfType()));
        }
    }

    public void requiredListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            if ((Boolean)event.getNewValue())
            {
                statement.setMinOccurs("1");
            }
        }
    }

    public void multipleListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            if ((Boolean)event.getNewValue())
            {
                statement.setMaxOccurs("unbounded");
            }
        }
    }

    public Statement getStatement()
    {
        return statement;
    }

    public void setStatement(Statement statement)
    {
        this.statement = statement;
    }

    public int getConstraintsSize()
    {
        return statement.getLiteralConstraints().size();
    }

    public ComplexTypes getMdType()
    {
        return mdType;
    }

    public void setMdType(ComplexTypes mdType)
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

    public String getVocabularyString()
    {
        return statement.getVocabulary().toString();
    }

    public void setVocabularyString(String vocabularyString)
    {
        this.vocabularyString = vocabularyString;
    }
}

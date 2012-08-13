/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.mdProfile.wrapper;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.util.VocabularyHelper;
import de.mpg.j2j.misc.LocalizedString;

public class StatementWrapper
{
    private boolean required = false;
    private boolean multiple = false;
    private Statement statement;
    private String vocabularyString = null;
    private String typeString;
    private boolean preview = true;
    private URI profile;
    private List<LocalizedString> labels = new ArrayList<LocalizedString>();
    private VocabularyHelper vocabularyHelper = null;
    private boolean description = false;

    public StatementWrapper(Statement st, URI profile)
    {
        statement = st;
        this.profile = profile;
        statement.setLiteralConstraints(st.getLiteralConstraints());
        statement.setMinOccurs(st.getMinOccurs());
        statement.setMaxOccurs(st.getMaxOccurs());
        statement.setLabels(st.getLabels());
        statement.setType(st.getType());
        statement.setVocabulary(st.getVocabulary());
        description = st.isDescription();
        this.preview = st.isPreview();
        if (Integer.parseInt(st.getMinOccurs()) > 0)
        {
            required = true;
        }
        if ("unbounded".equals(st.getMaxOccurs()) || Integer.parseInt(st.getMaxOccurs()) > 1)
        {
            multiple = true;
        }
        else
        {
            multiple = false;
        }
        labels.addAll(st.getLabels());
        vocabularyHelper = new VocabularyHelper();
        if (statement.getVocabulary() != null)
        {
            vocabularyString = statement.getVocabulary().toString();
            if ("unknown".equals(vocabularyHelper.getVocabularyName(statement.getVocabulary())))
            {
                vocabularyHelper.getVocabularies().add(
                        new SelectItem(statement.getVocabulary().toString(), vocabularyString));
            }
        }
    }

    public Statement getAsStatement()
    {
//        if (statement.getId() == null)
//        {
//            statement.setId(URI.create(profile + "/" + labels.get(0).getString().replace(" ", "_")));
//        }
        //statement.getLabels().clear();
        statement.setLabels(labels);
        if (vocabularyString != null)
        {
            statement.setVocabulary(URI.create(vocabularyString));
        }
        statement.setDescription(description);
        return statement;
    }

    public void vocabularyListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            this.statement.setVocabulary(URI.create(event.getNewValue().toString()));
        }
        vocabularyString = statement.getVocabulary().toString();
    }

    public void constraintListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            int pos = Integer.parseInt(event.getComponent().getAttributes().get("position").toString());
            ((List<String>)statement.getLiteralConstraints()).set(pos, event.getNewValue()
                    .toString());
        }
    }

    public void typeListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            statement.setType(URI.create(event.getNewValue().toString()));
        }
    }

    public void previewListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            statement.setPreview((Boolean)event.getNewValue());
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
            else
            {
                statement.setMaxOccurs("1");
            }
        }
    }

    public void descriptionListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            description = (Boolean)event.getNewValue();
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

    public URI getType()
    {
        return statement.getType();
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

    public String getVocabularyString()
    {
        return vocabularyString;
    }

    public void setVocabularyString(String vocabularyString)
    {
        this.vocabularyString = vocabularyString;
    }

    public String getTypeString()
    {
        return statement.getType().toString();
    }

    public void setTypeString(String typeString)
    {
        this.typeString = typeString;
    }

    public VocabularyHelper getVocabularyHelper()
    {
        return vocabularyHelper;
    }

    public void setVocabularyHelper(VocabularyHelper vocabularyHelper)
    {
        this.vocabularyHelper = vocabularyHelper;
    }

    public boolean isDescription()
    {
        return description;
    }

    public void setDescription(boolean description)
    {
        this.description = description;
    }

    public List<LocalizedString> getLabels()
    {
        return labels;
    }

    public void setLabels(List<LocalizedString> labels)
    {
        this.labels = labels;
    }

    public void setPreview(boolean preview)
    {
        this.preview = preview;
    }

    public boolean isPreview()
    {
        return preview;
    }
}

/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.mdProfile.wrapper;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import thewebsemantic.LocalizedString;
import de.mpg.imeji.mdProfile.EditMdProfileBean;
import de.mpg.imeji.mdProfile.MdProfileBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.LocalizedStringHelper;
import de.mpg.imeji.util.VocabularyHelper;
import de.mpg.jena.vo.Statement;

public class StatementWrapper
{
    private boolean required = false;
    private boolean multiple = false;
    private Statement statement;
    private String vocabularyString = null;
    private String typeString;
    private URI profile;
    private List<LocalizedStringHelper> labels = null;
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
        statement.setName(st.getName());
        statement.setVocabulary(st.getVocabulary());
        
        description = st.isDescription();

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

        labels = new ArrayList<LocalizedStringHelper>();
        for (LocalizedString  locString : st.getLabels())
    	{
    		labels.add(new LocalizedStringHelper(locString));
    	}
        
        vocabularyHelper = new VocabularyHelper();
        if (statement.getVocabulary() != null)
        {
	        vocabularyString = statement.getVocabulary().toString();
	        if ("unknown".equals(vocabularyHelper.getVocabularyName(statement.getVocabulary())))
	        {
	        	vocabularyHelper.getVocabularies().add(new SelectItem(statement.getVocabulary().toString(), vocabularyString));
	        }
        }
    }
    
    public Statement getAsStatement()
    {
    	if (statement.getName() == null)
    	{
    		statement.setName(URI.create(profile + "/"+ labels.get(0).getString().replace(" ", "_")));
    	}
    	statement.getLabels().clear();
    	for(LocalizedStringHelper lsh : labels)
    	{
    		statement.getLabels().add(lsh.getAsLocalizedString());
    	}
    	if(vocabularyString != null)
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
            ((List<LocalizedString>)statement.getLiteralConstraints()).set(pos, new LocalizedString(event.getNewValue().toString(), "eng"));
        }
    }

    public void typeListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            statement.setType(URI.create(event.getNewValue().toString()));
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
    		description =(Boolean) event.getNewValue();
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

	public String getTypeString() {
		return statement.getType().toString();
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public List<LocalizedStringHelper> getLabels() {
		return labels;
	}

	public void setLabels(List<LocalizedStringHelper> labels) {
		this.labels = labels;
	}

	public VocabularyHelper getVocabularyHelper() {
		return vocabularyHelper;
	}

	public void setVocabularyHelper(VocabularyHelper vocabularyHelper) {
		this.vocabularyHelper = vocabularyHelper;
	}

	public boolean isDescription() {
		return description;
	}

	public void setDescription(boolean description) {
		this.description = description;
	}
	
	
    
}

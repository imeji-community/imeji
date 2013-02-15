/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.mdProfile.wrapper;

import java.net.URI;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.hp.hpl.jena.util.Metadata;

import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.mdProfile.MdProfileBean;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.VocabularyHelper;

/**
 * Wrapper for {@link Statement}, used in java bean {@link MdProfileBean}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class StatementWrapper
{
    private Statement statement;
    private boolean multiple = false;
    private String vocabularyString;
    private VocabularyHelper vocabularyHelper;
    private boolean showRemoveWarning = false;

    /**
     * Create a new {@link StatementWrapper}
     * 
     * @param st
     * @param profile
     */
    public StatementWrapper(Statement st, URI profile)
    {
        init(st);
    }

    /**
     * Reset this {@link StatementWrapper} with emtpy {@link Statement}
     */
    public void reset()
    {
        Statement newSt = ImejiFactory.newStatement();
        newSt.setType(statement.getType());
        init(newSt);
    }

    /**
     * Initialize the {@link StatementWrapper} fields with {@link Statement} fields
     * 
     * @param st
     */
    public void init(Statement st)
    {
        statement = st;
        initMultiple();
        initVocabulary();
    }

    /**
     * Initialize the multiple value according to the {@link Statement}
     */
    private void initMultiple()
    {
        if ("unbounded".equals(statement.getMaxOccurs()) || Integer.parseInt(statement.getMaxOccurs()) > 1)
        {
            multiple = true;
        }
        else
        {
            multiple = false;
        }
    }

    /**
     * Initialize the {@link VocabularyHelper} according to the {@link Statement}
     */
    private void initVocabulary()
    {
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
        else
        {
            vocabularyString = null;
        }
    }

    /**
     * Return the {@link StatementWrapper} as a {@link Statement}
     * 
     * @return
     */
    public Statement getAsStatement()
    {
        if (vocabularyString != null)
        {
            statement.setVocabulary(URI.create(vocabularyString));
        }
        return statement;
    }

    /**
     * True if at least one item in imeji has one {@link Metadata} defined with the {@link Statement} attached to this
     * {@link StatementWrapper}
     * 
     * @return
     */
    public boolean isUsedByAtLeastOnItem()
    {
        return ImejiSPARQL
                .exec("PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s <http://imeji.org/terms/metadata> ?md . ?md <http://imeji.org/terms/statement> <"
                        + statement.getId() + ">} LIMIT 1 ", null).size() > 0;
    }

    /**
     * Return count of defined labels
     * 
     * @return
     */
    public int getLabelsCount()
    {
        return statement.getLabels().size();
    }

    /**
     * Listener for the vocabulary menu
     * 
     * @param event
     */
    public void vocabularyListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            statement.setVocabulary(URI.create(event.getNewValue().toString()));
        }
        vocabularyString = statement.getVocabulary().toString();
    }

    /**
     * Listener for the constraints fields
     * 
     * @param event
     */
    public void constraintListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            int pos = Integer.parseInt(event.getComponent().getAttributes().get("position").toString());
            ((List<String>)statement.getLiteralConstraints()).set(pos, event.getNewValue().toString());
        }
    }

    /**
     * Listener for metadata type menu
     * 
     * @param event
     */
    public void typeListener(ValueChangeEvent event)
    {
        System.out.println("listener");
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            statement.setType(URI.create(event.getNewValue().toString()));
        }
    }

    /**
     * Listener for the preview select box
     * 
     * @param event
     */
    public void previewListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            statement.setPreview((Boolean)event.getNewValue());
        }
    }

    /**
     * Listener for the multiple select box
     * 
     * @param event
     */
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

    /**
     * Listener for the description select box
     * 
     * @param event
     */
    public void descriptionListener(ValueChangeEvent event)
    {
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            statement.setDescription((Boolean)event.getNewValue());
        }
    }

    /**
     * getter
     * 
     * @return
     */
    public Statement getStatement()
    {
        return statement;
    }

    /**
     * setter
     * 
     * @param statement
     */
    public void setStatement(Statement statement)
    {
        this.statement = statement;
    }

    /**
     * Return the count of the constraints
     * 
     * @return
     */
    public int getConstraintsSize()
    {
        return statement.getLiteralConstraints().size();
    }

    /**
     * getter
     * 
     * @return
     */
    public boolean isMultiple()
    {
        return multiple;
    }

    /**
     * setter
     * 
     * @param statement
     */
    public void setMultiple(boolean multiple)
    {
        this.multiple = multiple;
    }

    /**
     * getter
     * 
     * @return
     */
    public String getVocabularyString()
    {
        return vocabularyString;
    }

    /**
     * setter
     * 
     * @param statement
     */
    public void setVocabularyString(String vocabularyString)
    {
        this.vocabularyString = vocabularyString;
    }

    /**
     * getter
     * 
     * @return
     */
    public VocabularyHelper getVocabularyHelper()
    {
        return vocabularyHelper;
    }

    /**
     * setter
     * 
     * @param statement
     */
    public void setVocabularyHelper(VocabularyHelper vocabularyHelper)
    {
        this.vocabularyHelper = vocabularyHelper;
    }

    /**
     * setter
     * 
     * @param statement
     */
    public void setShowRemoveWarning(boolean showRemoveWarning)
    {
        this.showRemoveWarning = showRemoveWarning;
    }

    /**
     * getter
     * 
     * @return
     */
    public boolean isShowRemoveWarning()
    {
        return showRemoveWarning;
    }
}

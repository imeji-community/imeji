package de.mpg.imeji.vo;

import java.util.ArrayList;
import java.util.List;

public class StatementVO extends MetadataVO
{ 
    private boolean required = false;
    private boolean multiple = false;
    private String vocabulary = null;
    private List<String> constraints = null;
    
    public StatementVO()
    {
        super();
        constraints = new ArrayList<String>();
    }

    /**
     * @return the required
     */
    public boolean isRequired()
    {
        return required;
    }

    /**
     * @param required the required to set
     */
    public void setRequired(boolean required)
    {
        this.required = required;
    }

    /**
     * @return the multiple
     */
    public boolean isMultiple()
    {
        return multiple;
    }

    /**
     * @param multiple the multiple to set
     */
    public void setMultiple(boolean multiple)
    {
        this.multiple = multiple;
    }

    /**
     * @return the vocabulary
     */
    public String getVocabulary()
    {
        return vocabulary;
    }

    /**
     * @param vocabulary the vocabulary to set
     */
    public void setVocabulary(String vocabulary)
    {
        this.vocabulary = vocabulary;
    }

    /**
     * @return the constraints
     */
    public List<String> getConstraints()
    {
        return constraints;
    }

    /**
     * @param constraints the constraints to set
     */
    public void setConstraints(List<String> constraints)
    {
        this.constraints = constraints;
    }
    
    
}

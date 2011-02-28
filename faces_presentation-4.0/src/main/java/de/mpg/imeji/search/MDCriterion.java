package de.mpg.imeji.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import de.mpg.imeji.lang.labelHelper;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.ComplexType.ComplexTypes;
import de.mpg.jena.vo.complextypes.util.ComplexTypeHelper;

public class MDCriterion extends Criterion implements Serializable{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String selectedMdName;
	private Statement selectedStatement;
	private String mdText;
	private List<SelectItem> mdList;
    private Collection<Statement> statements;
    private String numberOperator;
    private List<SelectItem> numberOpList;
    private List<SelectItem> dateOpList;
    private String dateOperator;

	public MDCriterion(Collection<Statement> statements)
	{
		this.statements = statements;
        setMdList(newMdList());
		setSelectedMdName(getMdList().get(0).getValue().toString());
        setMdText("");
        
        numberOpList = new ArrayList<SelectItem>();
        numberOpList.add(new SelectItem(Filtertype.EQUALS_NUMBER, "="));
        numberOpList.add(new SelectItem(Filtertype.GREATER_NUMBER, ">="));
        numberOpList.add(new SelectItem(Filtertype.LESSER_NUMBER, "<="));
        numberOperator = Filtertype.EQUALS_NUMBER.name();
        
        dateOpList = new ArrayList<SelectItem>();
        dateOpList.add(new SelectItem(Filtertype.EQUALS_DATE, "="));
        dateOpList.add(new SelectItem(Filtertype.GREATER_DATE, ">="));
        dateOpList.add(new SelectItem(Filtertype.LESSER_DATE, "<="));
        dateOperator = Filtertype.EQUALS_DATE.name();
        
	}
	

    public List<SelectItem> newMdList()
    {
    	List<SelectItem> newMdList = new ArrayList<SelectItem>();
        try
        {
        	for (Statement s : statements)
        	{
        		newMdList.add(new SelectItem(s.getName(), labelHelper.getDefaultLabel(s.getLabels().iterator())));
        	}
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }		
        return newMdList;
    }
	
	public List<SelectItem> getMdList() {
		return mdList;
	}


	public void setMdList(List<SelectItem> mdList) {
		this.mdList = mdList;
	}
	
	
	public String getMdText() {
		return mdText;
	}
	public void setMdText(String mdText) {
		this.mdText = mdText;  
	}
	public boolean clearCriterion() {
		setSearchString("");
		setSelectedMdName("");
		setMdText("");
		return true;
	}

    public void setSelectedMdName(String selectedMdName) 
    {
        if(selectedMdName!=null && !selectedMdName.equals(this.selectedMdName)){ 
           
            for(Statement s : statements)
            {
                if(s.getName().equals(selectedMdName))
                {
                    this.setSelectedStatement(s);
                }
            }
           
        }
        this.selectedMdName = selectedMdName;
    }

    public String getSelectedMdName()
    {
        return selectedMdName;
    }

    public void setStatements(Collection<Statement> statements)
    {
        this.statements = statements;
    }

    public Collection<Statement> getStatements()
    {
        return statements;
    }

    public void setSelectedStatement(Statement selectedStatement)
    {
        this.selectedStatement = selectedStatement;
    }

    public Statement getSelectedStatement()
    {
        return selectedStatement;
    }
    
    public ComplexTypes getType()
    {
        if(getSelectedStatement()!=null) 
        {
            return ComplexTypeHelper.getComplexTypesEnum(getSelectedStatement().getType());    
        }
        return null;
       
    }
    
    public void mdTypeChanged(ValueChangeEvent ev)
    {
        if(ev.getNewValue()!=null)
        {
            setSelectedMdName((String)ev.getNewValue());
        }
        
    }

    public void setNumberOperator(String numberOperator)
    {
        this.numberOperator = numberOperator;
    }

    public String getNumberOperator()
    {
        return numberOperator;
    }

    public void setNumberOpList(List<SelectItem> numberOpList)
    {
        this.numberOpList = numberOpList;
    }

    public List<SelectItem> getNumberOpList()
    {
        return numberOpList;
    }


    public void setDateOperator(String dateOperator)
    {
        this.dateOperator = dateOperator;
    }


    public String getDateOperator()
    {
        return dateOperator;
    }


    public void setDateOpList(List<SelectItem> dateOpList)
    {
        this.dateOpList = dateOpList;
    }


    public List<SelectItem> getDateOpList()
    {
        return dateOpList;
    }
    
   
}
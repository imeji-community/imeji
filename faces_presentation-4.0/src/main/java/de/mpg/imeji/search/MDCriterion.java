package de.mpg.imeji.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.faces.model.SelectItem;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Statement;

public class MDCriterion extends Criterion implements Serializable{
	private String selectedMdName;
	private Statement selectedStatement;
	private String mdText;
	private List<SelectItem> mdList;
    private Collection<Statement> statements;

	public MDCriterion(Collection<Statement> statements){
		this.statements = statements;
		setSelectedMdName("");
        setMdList(newMdList());
        setMdText("");

	}
	
    // TODO use default mdList ?
    public List<SelectItem> newMdList(){
    	List<SelectItem> newMdList = new ArrayList<SelectItem>();
        try{
        	for (Statement s : statements){
                        newMdList.add(new SelectItem(s.getName(), s.getName()));
        	}
                   
        		
        }catch (Exception e){
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
}
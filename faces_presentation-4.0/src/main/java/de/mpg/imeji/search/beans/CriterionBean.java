package de.mpg.imeji.search.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;
import de.mpg.imeji.util.BeanHelper;



public class CriterionBean extends BeanHelper{
	
	private String logicOperator;
	enum LogicOperator
	{
		AND ("and"),
		OR ("or"),
		NOT ("not");
		
		private String query;

		LogicOperator(String query)
		{
			this.query = query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public String getQuery() {
			return query;
		}
	}
	
    public List<SelectItem> getLogicOperatorItems() 
    {
    	List<SelectItem> selectItems = new ArrayList<SelectItem>();
    	for (LogicOperator op :LogicOperator.values())
    	{
    		selectItems.add(new SelectItem(op.name(), op.name()));
    	}
    	return selectItems;
    }
    
	public final String getLogicOperator()
	{
		return logicOperator;
	}
	
	public final void setLogicOperator(String logicOperator)
	{
		this.logicOperator = logicOperator;
		if (logicOperator.equals("LOGIC_AND"))
		{
			logicOperator = "AND";
		}
		else if (logicOperator.equals("LOGIC_OR"))
		{
			logicOperator = "OR";
		}
		else if (logicOperator.equals("LOGIC_NOT"))
		{
			logicOperator = "NOT";
		}
	}

}

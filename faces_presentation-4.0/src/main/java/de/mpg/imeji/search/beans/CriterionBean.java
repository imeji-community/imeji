package de.mpg.imeji.search.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.escidoc.services.search.query.MetadataSearchCriterion.LogicalOperator;
import de.mpg.imeji.util.BeanHelper;



public class CriterionBean extends BeanHelper{
	
	private String logicOperator;
	private List<SelectItem> logicOperatorItems;
	enum LogicOperatorItems
	{
		AND ("and"),
		OR ("or"),
		NOT ("not");
		
		private String query;

		LogicOperatorItems(String query)
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
	public void setLogicOperatorItems(List<SelectItem> logicOperatorItems){
		this.logicOperatorItems = logicOperatorItems;
	}
	
    public List<SelectItem> getLogicOperatorItems() 
    {
    	List<SelectItem> selectItems = new ArrayList<SelectItem>();
    	for (LogicOperatorItems op :LogicOperatorItems.values())
    	{
    		selectItems.add(new SelectItem(op.name(), op.name()));
    	}
    	return selectItems;
    }
    
	public final String getLogicOperator(){
		return logicOperator;
	}
	
	public final void setLogicOperator(String logicOperator){
		this.logicOperator = logicOperator;
		if (logicOperator.equals("AND")){
			logicOperator = "AND";
		}
		else if (logicOperator.equals("OR")){
			logicOperator = "OR";
		}
		else if (logicOperator.equals("NOT")){
			logicOperator = "NOT";
		}
	}

}

package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

public abstract class Criterion {
	
	/**
	 * Returns a search criterion which can be used in a search query towards the search service.
	 * @return a metadata serach criterion
	 * @throws TechnicalException if MetadataSearchCriterion cannot be instantiated
	 */
//	public abstract ArrayList<String> createSearchCriterion();

	//logic operator between the search criteria
	private String logicOperator;
    //the string to search for
	private String searchString = null;
	
	private List<SelectItem> logicOperatorItems;

	public Criterion(){
	}
	
	enum LogicOperatorItems{
		AND ("and"),
		OR ("or");
		//NOT ("not");
		
		private String query;

		LogicOperatorItems(String query){
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
	
    public List<SelectItem> getLogicOperatorItems() {
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
	
	public final void setLogicOperator(String logicOperator)
	{
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

	public String getSearchString()
    {
		return searchString;
	}

	public void setSearchString(String newVal)
    {
		searchString = newVal;
	}
	
	protected boolean isSearchStringEmpty() {
		if ( searchString == null || searchString.trim().equals("") ) {
			return true;
		}
		else {
			return false;
		}
	}
}

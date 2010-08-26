package de.mpg.imeji.search;

import java.util.ArrayList;

import de.mpg.jena.controller.SearchCriterion;



public abstract class Criterion {
	
	/**
	 * Returns a search criterion which can be used in a search query towards the search service.
	 * @return a metadata serach criterion
	 * @throws TechnicalException if MetadataSearchCriterion cannot be instantiated
	 */
	public abstract ArrayList<SearchCriterion> createSearchCriterion();

	//logic operator between the search criteria
	private String logicOperator;
    //the string to search for
	private String searchString = null;

	public Criterion()
    {
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

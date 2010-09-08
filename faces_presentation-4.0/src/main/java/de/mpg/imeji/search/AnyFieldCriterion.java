package de.mpg.imeji.search;

import java.util.ArrayList;

public class AnyFieldCriterion extends Criterion{
	private String searchTerm;
	
	public AnyFieldCriterion(){
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public ArrayList<String> createSearchCriterion() {
		ArrayList <String> criterions = new ArrayList<String>();
		if(isSearchStringEmpty() == true)
			return criterions;
		else{
			String criterion = new String();
			criterion += "searchTerm="+ getSearchTerm();
			criterions.add(criterion);
		}
		return criterions;
	}

}

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
}

package de.mpg.imeji.search;

import java.io.Serializable;
import java.util.ArrayList;

public class AnyFieldCriterion extends Criterion implements Serializable{
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

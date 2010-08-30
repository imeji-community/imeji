package de.mpg.imeji.search;

import java.util.ArrayList;

import de.mpg.jena.controller.SearchCriterion;

public class CollectionCriterion extends Criterion{
	private String collection;
	
	public CollectionCriterion(){
		
	}
	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}
	@Override
	public ArrayList<String> createSearchCriterion() {
		ArrayList<String> criterions = new ArrayList<String>();
		if(isSearchStringEmpty() == true)
			return criterions;
		else{
			String criterion = new String();
			criterion += "Collection=" + getCollection();
			System.err.println(criterion);
			criterions.add(criterion);
		}

			
		return criterions;
	}
}

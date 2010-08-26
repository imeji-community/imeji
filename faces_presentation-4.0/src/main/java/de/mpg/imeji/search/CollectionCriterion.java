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
	public ArrayList<SearchCriterion> createSearchCriterion() {
		ArrayList<SearchCriterion> criterions = new ArrayList<SearchCriterion>();
		if(this.collection != null){
//			SearchCriterion creterionPre = new SeachCriterion();
//			criterions.add(getCollection());
		}
			
			
		return criterions;
	}
}

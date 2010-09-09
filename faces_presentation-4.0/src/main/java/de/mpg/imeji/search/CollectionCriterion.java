package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.mpg.jena.controller.SearchCriterion;

public class CollectionCriterion extends Criterion{
	private String collection;
	
	protected List<MDCriterion> mds = new ArrayList<MDCriterion>();

	
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
//			for(int i=0; i<mdList.size(); i++){
//				criterion += "Collection=" + getCollection()+ "&MD=" + getMdList().get(i).getMd() + "&MDText=" + getMdList().get(i).getMdText();
//				criterions.add(criterion);
//			}
		}
		return criterions;
	}

	public List<MDCriterion> getMds() {
		return mds;
	}

	public void setMds(List<MDCriterion> mds) {
		this.mds = mds;
	}


}

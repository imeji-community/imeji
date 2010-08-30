package de.mpg.imeji.search.beans;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import de.mpg.imeji.search.CollectionCriterion;
import de.mpg.imeji.search.simulator.CollectionSimulator;
import de.mpg.imeji.search.simulator.Simulator;
import de.mpg.jena.controller.SearchCriterion;

public class CollectionCriterionBean extends CriterionBean {
	
	public static final String BEAN_NAME = "CollectionCriterionBean";
	
	private CollectionCriterion collectionCriterionVO;
	private Simulator s;
	
	public CollectionCriterionBean(){
		this(new CollectionCriterion());

		
	}
	
	public CollectionCriterionBean(CollectionCriterion collectionCriterionVO) {
		setCollectionCriterionVO(collectionCriterionVO);
	}
	
	public List<SelectItem> getCollectionList() {
		List<SelectItem> collectionList = new ArrayList<SelectItem>();
		//TODO: remove static collection list
		s = new Simulator();
		collectionList.add(new SelectItem(null,"--"));		
		collectionList.add(new SelectItem(s.getCollection1().getTitle(),s.getCollection1().getTitle()));
		collectionList.add(new SelectItem(s.getCollection2().getTitle(),s.getCollection2().getTitle()));
		collectionList.add(new SelectItem(s.getCollection3().getTitle(),s.getCollection3().getTitle()));

		return collectionList;
	}
	
	public CollectionCriterion getCollectionCriterionVO() {
		return collectionCriterionVO;
	}

	public void setCollectionCriterionVO(CollectionCriterion collectionCriterionVO) {
		this.collectionCriterionVO = collectionCriterionVO;
	}

	public ArrayList<SearchCriterion> createSearchCriterion(){
		return null;
		
	}

	public boolean clearCriterion() {
		collectionCriterionVO = new CollectionCriterion();
		collectionCriterionVO.setSearchString("");
		return true;
	}


	public void collectionChanged(ValueChangeEvent event){
		String selectedCollection = event.getNewValue().toString();
		if(selectedCollection.equals("Birds"))
			s.setSelectedCollection(s.getCollection1());
		else if(selectedCollection.equals("Faces"))
			s.setSelectedCollection(s.getCollection2());
		else if(selectedCollection.equals("Diamonds"))
			s.setSelectedCollection(s.getCollection3());
		else
			s.setSelectedCollection(s.getDefaultCollection());
			

	}









	
	
	
	
	

}


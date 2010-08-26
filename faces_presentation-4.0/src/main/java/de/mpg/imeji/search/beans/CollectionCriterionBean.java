package de.mpg.imeji.search.beans;

import java.util.ArrayList;

import java.util.List;

import javax.faces.model.SelectItem;


import de.mpg.imeji.search.CollectionCriterion;
import de.mpg.imeji.search.MDCriterion;
import de.mpg.jena.controller.SearchCriterion;

public class CollectionCriterionBean extends CriterionBean{
	
	public static final String BEAN_NAME = "CollectionCriterionBean";
	
	private List<SelectItem> collectionList;
	private CollectionCriterion collectionCriterionVO;
	
	public CollectionCriterionBean(){
		this(new CollectionCriterion());
	}
	
	public CollectionCriterionBean(CollectionCriterion collectionCriterionVO) {
		setCollectionCriterionVO(collectionCriterionVO);
	}
	
	public List<SelectItem> getCollectionList() {
		collectionList = new ArrayList<SelectItem>();
		//TODO: remove static collection list
		collectionList.add(new SelectItem("test1","test1"));
		collectionList.add(new SelectItem("test2","test2"));
		collectionList.add(new SelectItem("test3","test3"));
		
		return collectionList;
	}
	
	public void setCollectionList(List<SelectItem> collectionList) {
		this.collectionList = collectionList;
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

	public void clearCriterion() {
		collectionCriterionVO.setCollection(null);
		
	}


	
	
	
	
	

}


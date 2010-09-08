package de.mpg.imeji.search.beans;

import java.util.ArrayList;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import de.mpg.imeji.search.CollectionCriterion;
import de.mpg.jena.controller.SearchCriterion;

public class CollectionCriterionBean extends CriterionBean {
	
	public static final String BEAN_NAME = "CollectionCriterionBean";
	
	private CollectionCriterion collectionCriterionVO;

	public CollectionCriterionBean(){
		this(new CollectionCriterion());
	}
	
	public CollectionCriterionBean(CollectionCriterion collectionCriterionVO) {
		setCollectionCriterionVO(collectionCriterionVO);
	}
	
	public CollectionCriterion getCollectionCriterionVO() {
		return collectionCriterionVO;
	}

	public void setCollectionCriterionVO(CollectionCriterion collectionCriterionVO) {
		this.collectionCriterionVO = collectionCriterionVO;
	}

	public boolean clearCriterion() {
		collectionCriterionVO = new CollectionCriterion();
		collectionCriterionVO.setSearchString("");
		return true;
	}












	
	
	
	
	

}


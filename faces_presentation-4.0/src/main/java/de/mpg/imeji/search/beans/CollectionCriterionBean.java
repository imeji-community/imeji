package de.mpg.imeji.search.beans;

import java.util.ArrayList;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import de.mpg.imeji.search.CollectionCriterion;
import de.mpg.imeji.search.MDCriterion;
import de.mpg.jena.controller.SearchCriterion;

public class CollectionCriterionBean extends CriterionBean {
	
	public static final String BEAN_NAME = "CollectionCriterionBean";
	
	private CollectionCriterion collectionCriterionVO = new CollectionCriterion();
	private List<MDCriterionBean> mdCriterionBeanList = null;
 
	public CollectionCriterionBean(){
		this(new CollectionCriterion(), new ArrayList<MDCriterionBean>());
	}

	public CollectionCriterionBean(CollectionCriterion collectionCriterionVO, List<MDCriterionBean> mdCriterionBeanList) {
		mdCriterionBeanList = new ArrayList<MDCriterionBean>();
		mdCriterionBeanList.add(new MDCriterionBean());
		this.mdCriterionBeanList = mdCriterionBeanList;
//		setCollectionCriterionVO(collectionCriterionVO);
	}
	

	
	public CollectionCriterion getCollectionCriterionVO() {
		return collectionCriterionVO;
	}

	public void setCollectionCriterionVO(CollectionCriterion collectionCriterionVO) {
		this.collectionCriterionVO = collectionCriterionVO;
	}
	
	public List<MDCriterionBean> getMdCriterionBeanList() {
		return mdCriterionBeanList;
	}

	public void setMdCriterionBeanList(List<MDCriterionBean> mdCriterionBeanList) {
		this.mdCriterionBeanList = mdCriterionBeanList;
	}
	
	public MDCriterionBean addObject(){
		MDCriterion newVO = new MDCriterion();
		MDCriterionBean newBean = new MDCriterionBean(newVO);
		mdCriterionBeanList.add(newBean);
		return newBean;		
	}
	
	public MDCriterionBean removeObject(){
		int i = mdCriterionBeanList.size();
		MDCriterionBean beanToRemove = mdCriterionBeanList.get(i-1);
		mdCriterionBeanList.remove(beanToRemove);
		return beanToRemove;
	}

	public boolean clearCriterion() {
		collectionCriterionVO = new CollectionCriterion();
		collectionCriterionVO.setSearchString("");
		for(MDCriterionBean bean: mdCriterionBeanList)
			bean.clearCriterion();
		return true;
	}
	
	public int getSize(){
		return mdCriterionBeanList.size();
	}












	
	
	
	
	

}


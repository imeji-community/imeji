package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.mpg.imeji.search.beans.AnyFieldCriterionBean;
import de.mpg.imeji.search.beans.CollectionCriterionBean;
import de.mpg.imeji.search.beans.MDCriterionBean;




public class CollectionCriterionController {
	
	private List<CollectionCriterionBean> collectionCriterionBeanList;
	private int flag;

	public CollectionCriterionController(){
		collectionCriterionBeanList =new ArrayList<CollectionCriterionBean>();
		collectionCriterionBeanList.add(new CollectionCriterionBean());
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public CollectionCriterionController(List<CollectionCriterionBean> collectionCriterionBean){
		setCollectionCriterionBeanList(collectionCriterionBean);
	}
	 
	public CollectionCriterionBean getCurrentCollectionCriterionBean(){
		return collectionCriterionBeanList.get(getFlag());
	}
	
	public List<CollectionCriterionBean> getCollectionCriterionBeanList(){
		return collectionCriterionBeanList;
	}
	
    public void setCollectionCriterionBeanList(List<CollectionCriterionBean> collectionCriterionBeanList){
        this.collectionCriterionBeanList = collectionCriterionBeanList;
    }

	public void clearAllForms() {
        for (CollectionCriterionBean bean : collectionCriterionBeanList)
            bean.clearCriterion();
    }
	
	public CollectionCriterionBean addObject() {
		CollectionCriterion newVO = new CollectionCriterion();
		List<MDCriterionBean> newMdList = new ArrayList<MDCriterionBean>();
		CollectionCriterionBean newBean = new CollectionCriterionBean(newVO, newMdList);
		collectionCriterionBeanList.add(newBean);
		return newBean;
	}
	
	public CollectionCriterionBean removeObject(){
		int i = collectionCriterionBeanList.size();
		CollectionCriterionBean beanToRemove = collectionCriterionBeanList.get(i-1);
		collectionCriterionBeanList.remove(beanToRemove);
		return beanToRemove;
	}
		
	public List<String> getSearchCriterion() {
		List<String> criterions = new ArrayList<String>();

		for(int i=0; i<collectionCriterionBeanList.size(); i++){
			String criterion = new String();
			criterion +=collectionCriterionBeanList.get(i).getCriterionVO().getLogicOperator()+ "collection=" + collectionCriterionBeanList.get(i).getCollectionCriterionVO().getCollection() ;
			System.err.println(criterion);
			criterions.add(criterion);			
		}
		return criterions;
	}
	
	public int getSize(){
		return collectionCriterionBeanList.size();
	}
}
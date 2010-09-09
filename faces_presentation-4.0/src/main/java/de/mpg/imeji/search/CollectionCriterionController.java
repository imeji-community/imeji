package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import de.mpg.imeji.search.beans.AnyFieldCriterionBean;
import de.mpg.imeji.search.beans.CollectionCriterionBean;
import de.mpg.imeji.search.beans.MDCriterionBean;




public class CollectionCriterionController {
	
	private List<CollectionCriterionBean> collectionCriterionBeanList;
	private int collectionPosition;
	private int mdPosition;

	public CollectionCriterionController(){
		collectionCriterionBeanList = new ArrayList<CollectionCriterionBean>();
		collectionCriterionBeanList.add(new CollectionCriterionBean());
	}

	public String addCollection() {
		CollectionCriterion newVO = new CollectionCriterion();
		List<MDCriterionBean> newMdList = new ArrayList<MDCriterionBean>();
		CollectionCriterionBean newBean = new CollectionCriterionBean(newVO, newMdList);
		collectionCriterionBeanList.add(collectionPosition+1,newBean);
		return getNavigationString();
	}
	
	public String removeCollection(){
		if(collectionPosition > 0)
			collectionCriterionBeanList.remove(collectionPosition);
		return getNavigationString();
	}
	
	public String addMd(){
		ArrayList<MDCriterionBean> mds = (ArrayList<MDCriterionBean>)collectionCriterionBeanList.get(collectionPosition).getMdCriterionBeanList();
		MDCriterion newVO = new MDCriterion();
		MDCriterionBean newBean = new MDCriterionBean(newVO);		
		mds.add(mdPosition +1, newBean);	
		return getNavigationString();
	}
	
	public String removeMd(){
		if(mdPosition >0){
			ArrayList<MDCriterionBean> mds = (ArrayList<MDCriterionBean>)collectionCriterionBeanList.get(collectionPosition).getMdCriterionBeanList();
			mds.remove(mdPosition);			
		}
		return getNavigationString();
	}
	  


	public int getCollectionPosition() {
		return collectionPosition;
	}

	public void setCollectionPosition(int collectionPosition) {
		this.collectionPosition = collectionPosition;
	}

	public int getMdPosition() {
		return mdPosition;
	}

	public void setMdPosition(int mdPosition) {
		this.mdPosition = mdPosition;
	}

	public CollectionCriterionController(List<CollectionCriterionBean> collectionCriterionBean){
		setCollectionCriterionBeanList(collectionCriterionBean);
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
	
    protected String getNavigationString()
    {
        return "pretty:";
    }
	
}
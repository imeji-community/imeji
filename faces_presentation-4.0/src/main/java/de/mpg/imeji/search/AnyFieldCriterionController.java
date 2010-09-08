package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.List;
import de.mpg.imeji.search.beans.AnyFieldCriterionBean;

public class AnyFieldCriterionController {
	private List<AnyFieldCriterionBean> anyFieldCriterionBeanList = null;
	
	public AnyFieldCriterionController(){
		anyFieldCriterionBeanList = new ArrayList<AnyFieldCriterionBean>();
		anyFieldCriterionBeanList.add(new AnyFieldCriterionBean());
	}
	
	public AnyFieldCriterionController(List<AnyFieldCriterionBean> anyFieldCriterionBean){
		setAnyFieldCriterionBeanList(anyFieldCriterionBean);
	}
	
	public List<AnyFieldCriterionBean> getAnyFieldCriterionBeanList(){
		return anyFieldCriterionBeanList;
	}
	
    public void setAnyFieldCriterionBeanList(List<AnyFieldCriterionBean> anyFieldCriterionBeanList){
        this.anyFieldCriterionBeanList = anyFieldCriterionBeanList;
    }

	public void clearAllForms() {
        for (AnyFieldCriterionBean bean : anyFieldCriterionBeanList)
            bean.clearCriterion();
    }
	
	public AnyFieldCriterionBean addObject() {
		AnyFieldCriterion newVO = new AnyFieldCriterion();
		AnyFieldCriterionBean newBean = new AnyFieldCriterionBean(newVO);
		int i = anyFieldCriterionBeanList.size();
		anyFieldCriterionBeanList.add(newBean);
		return newBean;
	}
	
	public AnyFieldCriterionBean removeObject(){
		int i = anyFieldCriterionBeanList.size();
		AnyFieldCriterionBean beanToRemove = anyFieldCriterionBeanList.get(i-1);
		anyFieldCriterionBeanList.remove(beanToRemove);
		return beanToRemove;
	}
		
	public List<String> getSearchCriterion() {
		List<String> criterions = new ArrayList<String>();
		for(int i=0; i<anyFieldCriterionBeanList.size(); i++){
			String criterion = new String();
			criterion +=anyFieldCriterionBeanList.get(i).getCriterionVO().getLogicOperator()+ "any=" + anyFieldCriterionBeanList.get(i).getAnyFieldCriterionVO().getSearchTerm() ;
			System.err.println(criterion);
			criterions.add(criterion);			
		}
		return criterions;
	}
	
	public int getSize(){
		return anyFieldCriterionBeanList.size();
	}
}

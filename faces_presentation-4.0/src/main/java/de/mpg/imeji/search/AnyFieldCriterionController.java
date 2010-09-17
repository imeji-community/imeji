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
	 
	public String addObject() {
		AnyFieldCriterion newVO = new AnyFieldCriterion();
		AnyFieldCriterionBean newBean = new AnyFieldCriterionBean(newVO);
		anyFieldCriterionBeanList.add(newBean);
		return "";
	}
	
	public String removeObject(){
		int i = anyFieldCriterionBeanList.size();
		AnyFieldCriterionBean beanToRemove = anyFieldCriterionBeanList.get(i-1);
		anyFieldCriterionBeanList.remove(beanToRemove);
		return "";
	}
		
	public String getSearchCriterion() {
	
		String criterion = "";
		
		int i = 0;
		for(AnyFieldCriterionBean afc : anyFieldCriterionBeanList){
			
			if(!(afc.getAnyFieldCriterionVO().getSearchTerm().trim().equals("")))
			{
			    if (i==0)
			    {    
			        criterion += " " + afc.getAnyFieldCriterionVO().getLogicOperator();
			    }
			    criterion +=" ( ANY_METADATA.REGEX=" + afc.getAnyFieldCriterionVO().getSearchTerm() +" )";
			}

			i++;
		}
		return criterion;
	}
	
	public int getSize(){
		return anyFieldCriterionBeanList.size();
	}
}

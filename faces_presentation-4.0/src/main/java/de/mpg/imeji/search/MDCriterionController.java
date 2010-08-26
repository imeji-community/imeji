package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.search.beans.MDCriterionBean;
import de.mpg.imeji.search.MDCriterion;

public class MDCriterionController {
	
	private List<MDCriterion> parentVO;
	private MDCriterionManager mdCriterionManager;
	
	public MDCriterionController(){
		List<MDCriterion> controllerList = new ArrayList<MDCriterion>();
		controllerList.add(new MDCriterion());
		setParentVO(controllerList);
	}
	
	public MDCriterionController(List<MDCriterion> parentVO){
		setParentVO(parentVO);
	}
	
	public List<MDCriterion> getParentVO(){
		return parentVO;
	}
	
	
    public void setParentVO(List<MDCriterion> parentVO)
    {
        this.parentVO = parentVO;
        // ensure proper initialization of our DataModelManager
        mdCriterionManager = new MDCriterionManager(parentVO);
    }
	
	
	public class MDCriterionManager extends DataModelManager<MDCriterionBean>{
		List<MDCriterion> parentVO;


		public MDCriterionManager(List<MDCriterion> parentVO) {
			setParentVO(parentVO);
		}
		
		public MDCriterionBean createNewObject() {
			MDCriterion newVO = new MDCriterion();
			MDCriterionBean mdCriterionBean = new MDCriterionBean(newVO);
			parentVO.add(newVO);
			return mdCriterionBean;
		}
		
		protected void removeObjectAtIndex(int i){
			super.removeObjectAtIndex(i);
			parentVO.remove(i);
		}
		
		public List<MDCriterionBean> getDataListFromVO(){
			if(parentVO == null ) return null;
			List<MDCriterionBean> beanList = new ArrayList<MDCriterionBean>();
			for(MDCriterion mdCriterionVo: parentVO)
				beanList.add(new MDCriterionBean(mdCriterionVo));
			return beanList;
		}
		
		public void setParent(List<MDCriterion> parentVO){
			this.parentVO = parentVO;
			List<MDCriterionBean> beanList = new ArrayList<MDCriterionBean>();
            for (MDCriterion mdCriterionVO : parentVO)
            {
                beanList.add(new MDCriterionBean(mdCriterionVO));
            }
            setObjectList(beanList);			
		}
        public int getSize()
        {
            return getObjectDM().getRowCount();
        }
		
	}
	
    public MDCriterionManager getMdCriterionManager()
    {
        return mdCriterionManager;
    }

    public void setMdCriterionManager(MDCriterionManager mdCriterionManager)
    {
        this.mdCriterionManager = mdCriterionManager;
    }
    
    
    public void clearAllForms()
    {        
        for (MDCriterionBean gcb : mdCriterionManager.getObjectList())
        {
            gcb.clearCriterion();
        }
    }

	public List<MDCriterion> getFilledCriterion() {
		List<MDCriterion> returnList = new ArrayList<MDCriterion>();
		for (MDCriterion vo: parentVO){
			if(!(vo.getMd() == null && vo.getMdText()== null))
				returnList.add(vo);
		}
		
		return returnList;
	}



}

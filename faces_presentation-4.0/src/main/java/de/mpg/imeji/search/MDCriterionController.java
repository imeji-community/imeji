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
		
		parentVO = new ArrayList<MDCriterion>();
		parentVO.add(new MDCriterion());
		mdCriterionManager = new MDCriterionManager(parentVO);
	}
	
	public MDCriterionController(List<MDCriterion> parentVO){
		setParentVO(parentVO);
	}
	
	public List<MDCriterion> getParentVO(){
		return parentVO;
	}
	
	
    public void setParentVO(List<MDCriterion> parentVO){
        this.parentVO = parentVO;
//        // ensure proper initialization of our DataModelManager
//        mdCriterionManager = new MDCriterionManager(parentVO);
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
		
		public List<String> getSearchCriterion() {
			List<String> criterions = new ArrayList<String>();

			for(int i=0; i<mdCriterionManager.getObjectList().size(); i++){
				for(int j=0; j<parentVO.size();j++){
					if(i==j){	
						String criterion = new String();
						criterion += mdCriterionManager.getObjectList().get(i).getLogicOperator() + "MD=" + parentVO.get(j).getMd() +"&MDText=" + parentVO.get(j).getMdText();
						System.err.println(criterion);
						criterions.add(criterion);			
					}						
				}
			}

			
//	        for (MDCriterionBean bean : mdCriterionManager.getObjectList()){
//	        	criterions.add(bean.getLogicOperator());
//	        }
//			for (MDCriterion vo: parentVO){
//				if(!(vo.getMd() == null && vo.getMdText()== null)){
//					String criterion = new String();
//					criterion += "MD=" + vo.getMd() +"&MDText=" + vo.getMdText();
//					System.err.println(criterion);
//					criterions.add(criterion);
//				}
//			}
			return criterions;
		}

		public int getSize(){
            return getObjectDM().getRowCount();
        }
		
	}
	
    public MDCriterionManager getMdCriterionManager(){
        return mdCriterionManager;
    }

    public void setMdCriterionManager(MDCriterionManager mdCriterionManager){
        this.mdCriterionManager = mdCriterionManager;
    }
    
    
    public boolean clearAllForms(){        
        for (MDCriterionBean bean : mdCriterionManager.getObjectList()){
            bean.clearCriterion();
        }
        return true;
    }





}

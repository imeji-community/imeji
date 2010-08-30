package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.List;
import de.mpg.imeji.search.beans.CollectionCriterionBean;
import de.mpg.imeji.search.beans.MDCriterionBean;



public class CollectionCriterionController {
	
	private List<CollectionCriterion> parentVO;
	private CollectionCriterionManager collectionCriterionManager;
	
	public CollectionCriterionController(){
		parentVO =new ArrayList<CollectionCriterion>();
		parentVO.add(new CollectionCriterion());
		collectionCriterionManager = new CollectionCriterionManager(parentVO);
	}
	
	public CollectionCriterionController(List<CollectionCriterion> parentVO){
		setParentVO(parentVO);
	}
	
	public List<CollectionCriterion> getParentVO(){
		return parentVO;
	}
	
	
    public void setParentVO(List<CollectionCriterion> parentVO){
        this.parentVO = parentVO;
    }

	public void clearAllForms() {
		
        for (CollectionCriterionBean bean : collectionCriterionManager.getObjectList())
            bean.clearCriterion();

    }
		
	
	
	public class CollectionCriterionManager extends DataModelManager<CollectionCriterionBean>{
		List<CollectionCriterion> parentVO;
		
		public CollectionCriterionManager(List<CollectionCriterion> parentVO) {
			setParentVO(parentVO);
		}
		
		public CollectionCriterionBean createNewObject() {
			CollectionCriterion newVO = new CollectionCriterion();
			CollectionCriterionBean collectionCriterionBean = new CollectionCriterionBean(newVO);
			parentVO.add(newVO);
			return collectionCriterionBean;
		}
		
		public List<String> getSearchCriterion() {
			List<String> criterions = new ArrayList<String>();

			for(int i=0; i<collectionCriterionManager.getObjectList().size(); i++){
				for(int j=0; j<parentVO.size();j++){
					if(i==j){		
						String criterion = new String();
						criterion +=collectionCriterionManager.getObjectList().get(i).getLogicOperator()+ "Collection=" + parentVO.get(j).getCollection() ;
						System.err.println(criterion);
						criterions.add(criterion);			
					}						
				}
			}
			
//	        for (CollectionCriterionBean bean : collectionCriterionManager.getObjectList()){
//	        	criterions.add(bean.getLogicOperator());
//	        }
//			for (CollectionCriterion vo: parentVO){
//				if(vo.getCollection() != null && vo.getCollection()!= ""){
//					String criterion = new String();
//					criterion += "Collection=" + vo.getCollection();
//					System.err.println(criterion);
//					criterions.add(criterion);
//					
//			}
//			}
			
			return criterions;
		}
		
        public int getSize(){
            return getObjectDM().getRowCount();
        }
	
	}

    public CollectionCriterionManager getCollectionCriterionManager(){
        return collectionCriterionManager;
    }

    public void setCollectionCriterionManager(CollectionCriterionManager collectionCriterionManager){
        this.collectionCriterionManager = collectionCriterionManager;
    }




}
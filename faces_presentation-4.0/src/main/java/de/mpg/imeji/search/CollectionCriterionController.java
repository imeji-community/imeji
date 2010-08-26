package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.Collection;

import java.util.List;
import de.mpg.imeji.search.beans.CollectionCriterionBean;
import de.mpg.imeji.search.beans.CriterionBean;


public class CollectionCriterionController {
	
	private List<CollectionCriterion> parentVO;
	
	private CollectionCriterionManager collectionCriterionManager;
	
	

	public void clearAllForms() {
		
        for (CollectionCriterionBean gcb : collectionCriterionManager.getObjectList())
            gcb.clearCriterion();

    }
		
	
	
	public class CollectionCriterionManager extends DataModelManager<CollectionCriterionBean>{

		@Override
		public CollectionCriterionBean createNewObject() {
			CollectionCriterion newVO = new CollectionCriterion();
			CollectionCriterionBean collectionCriterionBean = new CollectionCriterionBean(newVO);
			parentVO.add(newVO);
			return collectionCriterionBean;
		}
		
	
	}



	public List<CollectionCriterion> getFilledCriterion() {
		List<CollectionCriterion> returnList = new ArrayList<CollectionCriterion>();
		for (CollectionCriterion vo: parentVO){
			if(vo.getCollection() != null && vo.getCollection()!= "")
				returnList.add(vo);
		}
		
		return returnList;
	}


}
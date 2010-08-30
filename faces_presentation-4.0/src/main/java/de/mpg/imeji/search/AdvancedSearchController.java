package de.mpg.imeji.search;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.component.UIXIterator;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.SearchCriterion;





public class AdvancedSearchController extends BeanHelper{

	private static Logger logger = Logger.getLogger(AdvancedSearchController.class);
	private String userHandle;	
	
	private CollectionCriterionController collectionCriterionController = null;
	private MDCriterionController mdCriterionController = null;
	
	private UIXIterator mdCriterionIterator= new UIXIterator();
	
	public AdvancedSearchController(){
		collectionCriterionController = new CollectionCriterionController();
		mdCriterionController = new MDCriterionController();
	}
	
	public void clearAndInitialAllForms(){
		collectionCriterionController = new CollectionCriterionController();
		mdCriterionController = new MDCriterionController();
		this.getCollectionCriterionController().getCollectionCriterionManager().getObjectDM();
	}
	
	public boolean clearAllForms(){
		collectionCriterionController.clearAllForms();
		mdCriterionController.clearAllForms();
		return true;
	}
	
	public String startSearch(){
		String searchString = "";
		for(String c : collectionCriterionController.getCollectionCriterionManager().getSearchCriterion())
			searchString += c + " ";
		for (String c : mdCriterionController.getMdCriterionManager().getSearchCriterion())
			searchString += c + " ";
		
		System.err.println("searchString = " + searchString);
		
		return searchString;
	}
	
	private ArrayList<SearchCriterion> transformToSparklSearchCriteria(Criterion predecessor, Criterion transformer){
		return null;
		
	}

	public CollectionCriterionController getCollectionCriterionController() {
		return collectionCriterionController;
	}

	public void setCollectionCriterionController(
			CollectionCriterionController collectionCriterionController) {
		this.collectionCriterionController = collectionCriterionController;
	}

	public MDCriterionController getMdCriterionController() {
		return mdCriterionController;
	}

	public void setMdCriterionController(MDCriterionController mdCriterionController) {
		this.mdCriterionController = mdCriterionController;
	}

	public UIXIterator getMdCriterionIterator() {
		return mdCriterionIterator;
	}

	public void setMdCriterionIterator(UIXIterator mdCriterionIterator) {
		this.mdCriterionIterator = mdCriterionIterator;
	}


	

	
	

	public String getUserHandle() {
		return userHandle;
	}
    public void setUserHandle(String userHandle) {
		this.userHandle = userHandle;
	}



	


	
//	private List<ResultVO> resultList;
	
	
	

}

package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.component.UIXIterator;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.collection.CollectionsBean;
import de.mpg.imeji.search.simulator.Simulator;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Statement;





public class AdvancedSearchController<ci> extends BeanHelper{

	private static Logger logger = Logger.getLogger(AdvancedSearchController.class);
	private SessionBean sb;	
	
	private CollectionCriterionController collectionCriterionController = null;
	private MDCriterionController mdCriterionController = null;
	
	private UIXIterator mdCriterionIterator= new UIXIterator();
	
	private CollectionController controller;
	private Collection<CollectionImeji> collections ;
	private CollectionImeji selectedCollection;;
	
	public AdvancedSearchController(){
		collectionCriterionController = new CollectionCriterionController();
		mdCriterionController = new MDCriterionController();
		sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
		controller = new CollectionController(sb.getUser());
	}
	
	public List<SelectItem> getCollectionList() {
		List<SelectItem> collectionList = new ArrayList<SelectItem>();
		collectionList.add(new SelectItem(null,"--"));		
		collections = new ArrayList<CollectionImeji>();
		try{
			collections = controller.search(sb.getUser(), new ArrayList<SearchCriterion>(), null, -1, 0);
			for(CollectionImeji ci  :  collections){
				collectionList.add(new SelectItem(ci.getMetadata().getTitle(), ci.getMetadata().getTitle()));
			}
		}catch(Exception e){
			
		}
		return collectionList;
	}
	
	public void collectionChanged(ValueChangeEvent event){
		try{
			String coTitle = event.getNewValue().toString();
			for(CollectionImeji ci : collections){
				if(ci.getMetadata().getTitle().equalsIgnoreCase(coTitle))
					this.selectedCollection = ci;
		}
		}catch(Exception e){
			this.selectedCollection = new CollectionImeji();
		}
	}
	
	
	public List<SelectItem> getMdList(){ 
		List<SelectItem> mdList = new ArrayList<SelectItem>();
		try{
		Collection<Statement> s = selectedCollection.getProfile().getStatements();
		if(s.size()!=0){
			for(Statement statement: selectedCollection.getProfile().getStatements())
				mdList.add(new SelectItem(statement.getName(),statement.getName()));
		}
		else
			//TODO use default mdList ?
			mdList.add(new SelectItem("title","title"));
		}catch(Exception e){
			//TODO use default mdList ?
			mdList.add(new SelectItem("title","title"));
		}
		return mdList;
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
		String searchQuery = "";
		for(String c : collectionCriterionController.getCollectionCriterionManager().getSearchCriterion())
			searchQuery += c + " ";
		for (String c : mdCriterionController.getMdCriterionManager().getSearchCriterion())
			searchQuery += c + " ";
		
		System.err.println("searchString = " + searchQuery);
		
		return searchQuery;
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


	

	
	




	


	
//	private List<ResultVO> resultList;
	
	
	

}

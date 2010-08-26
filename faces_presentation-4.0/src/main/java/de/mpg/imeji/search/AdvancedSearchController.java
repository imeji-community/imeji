package de.mpg.imeji.search;

import java.io.Serializable;


import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.component.UIXIterator;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryFactory;

import de.mpg.imeji.search.beans.CriterionBean;
import de.mpg.imeji.util.BeanHelper;





public class AdvancedSearchController extends BeanHelper{



	private static Logger logger = Logger.getLogger(AdvancedSearchController.class);
	private String userHandle;	
	
	private CollectionCriterionController collectionCriterionController;
	private MDCriterionController mdCriterionController;
	
	private UIXIterator mdCriterionIterator;
	
	public AdvancedSearchController(){
		collectionCriterionController = new CollectionCriterionController();
		mdCriterionController = new MDCriterionController();
		
		mdCriterionIterator = new UIXIterator();
	}
	
	public void clearAndInitialAllForms(){
		collectionCriterionController = new CollectionCriterionController();
		mdCriterionController = new MDCriterionController();
	}
	
	public String clearAllForms(){
		collectionCriterionController.clearAllForms();
		mdCriterionController.clearAllForms();
		return null;
	}
	
	public String startSearch(){
		ArrayList<Criterion> criterionList = new ArrayList<Criterion>();
		
		criterionList.addAll(collectionCriterionController.getFilledCriterion());
		criterionList.addAll(mdCriterionController.getFilledCriterion());
		
		String q = "SELECT ?v00 WHERE { ?s a <http://imeji.mpdl.mpg.de/collection> . ?s <http://imeji.mpdl.mpg.de/container/metadata> ?v10 . ?v10 <http://purl.org/dc/elements/1.1/title> ?v00 }";
		
//		Query queryObject = QueryFactory.create(q);
//		QueryExecution qe = QueryExecutionFactory.create(queryObject, base);
//		ResultSet results = qe.execSelect();
//		ResultSetFormatter.out(System.out, results);
//		qe.close();
		
		return q;
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

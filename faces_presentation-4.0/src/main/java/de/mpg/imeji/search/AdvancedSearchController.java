package de.mpg.imeji.search;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.mpg.imeji.image.ImagesBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.SearchCriterion;

public class AdvancedSearchController extends BeanHelper implements Serializable
{
    private static Logger logger = Logger.getLogger(AdvancedSearchController.class);

    private CollectionCriterionController collectionCriterionController = null;
    private AnyFieldCriterionController anyFieldCriterionController = null;
    
    public AdvancedSearchController(){
        collectionCriterionController = new CollectionCriterionController(); 
        anyFieldCriterionController = new AnyFieldCriterionController();
    }

    public boolean clearAllForms(){
        collectionCriterionController.clearAllForms();
        anyFieldCriterionController.clearAllForms();
        return true;
    }

    public String startSearch() throws IOException{
        
        ImagesBean bean = (ImagesBean)BeanHelper.getSessionBean(ImagesBean.class); 
        bean.setQuery(collectionCriterionController.getSearchCriterion());
        if (bean.getQuery() == null || "".equals(bean.getQuery().trim()))
        {
        	return "";
        }
        return "pretty:images";
    }

    private ArrayList<SearchCriterion> transformToSparqlSearchCriteria(Criterion predecessor, Criterion transformer){
        return null;
    }

	public CollectionCriterionController getCollectionCriterionController(){
        return collectionCriterionController;
    }

    public AnyFieldCriterionController getAnyFieldCriterionController() {
		return anyFieldCriterionController;
	}

	public void setAnyFieldCriterionController(AnyFieldCriterionController anyFieldCriterionController) {
		this.anyFieldCriterionController = anyFieldCriterionController;
	}

	public void setCollectionCriterionController(CollectionCriterionController collectionCriterionController){
        this.collectionCriterionController = collectionCriterionController;
    }
}

package de.mpg.imeji.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.component.UIXIterator;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Statement;

public class AdvancedSearchController extends BeanHelper
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
        String q = "q=";
        for(String s : anyFieldCriterionController.getSearchCriterion())
        	q += s + "";
        for (String c : collectionCriterionController.getSearchCriterion())
            q += c + " ";

        System.err.println("q = " + q);
        FacesContext.getCurrentInstance().getExternalContext().redirect("Images.xhtml?"+ q);
        return q;
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

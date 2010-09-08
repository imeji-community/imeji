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
    private SessionBean sb;
    private CollectionCriterionController collectionCriterionController = null;
    private AnyFieldCriterionController anyFieldCriterionController = null;
    
    private CollectionController controller;

    private Collection<CollectionImeji> collections;
    private CollectionImeji selectedCollection;
    
    public AdvancedSearchController(){
        collectionCriterionController = new CollectionCriterionController();
        anyFieldCriterionController = new AnyFieldCriterionController();
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        controller = new CollectionController(sb.getUser());
    }

    public List<SelectItem> getCollectionList(){
        List<SelectItem> collectionList = new ArrayList<SelectItem>();
        controller = new CollectionController(sb.getUser());
        collectionList.add(new SelectItem(null, "--"));
        collections = new ArrayList<CollectionImeji>();
        try{
            collections = controller.search(new ArrayList<SearchCriterion>(), null, -1, 0);
            for (CollectionImeji ci : collections)
            {
                collectionList.add(new SelectItem(ci.getMetadata().getTitle(), ci.getMetadata().getTitle()));
            }
        }catch (Exception e){
        }
        return collectionList;
    }

    public void collectionChanged(ValueChangeEvent event){
        try{
            String coTitle = event.getNewValue().toString();
            for (CollectionImeji ci : collections)
            {
                if (ci.getMetadata().getTitle().equalsIgnoreCase(coTitle))
                    this.selectedCollection = ci;
            }
        }
        catch (Exception e)
        {
            this.selectedCollection = new CollectionImeji();
        }
    }

    public List<SelectItem> getMdList()
    {
        List<SelectItem> mdList = new ArrayList<SelectItem>();
        try
        {
            Collection<Statement> s = selectedCollection.getProfile().getStatements();
            if (s.size() != 0)
            {
                for (Statement statement : selectedCollection.getProfile().getStatements())
                    mdList.add(new SelectItem(statement.getName(), statement.getName()));
            }
            else
                // TODO use default mdList ?
                mdList.add(new SelectItem("title", "title"));
        }
        catch (Exception e)
        {
            // TODO use default mdList ?
            mdList.add(new SelectItem("title", "title"));
        }
        return mdList;
    }

    public void clearAndInitialAllForms(){
        collectionCriterionController = new CollectionCriterionController();
        anyFieldCriterionController = new AnyFieldCriterionController();
        this.getCollectionCriterionController().getCollectionCriterionManager().getObjectDM();
    }

    public boolean clearAllForms(){
        collectionCriterionController.clearAllForms();
        anyFieldCriterionController.clearAllForms();
        return true;
    }

    public String startSearch() throws IOException{
        String searchQuery = "";
        for (String c : collectionCriterionController.getCollectionCriterionManager().getSearchCriterion())
            searchQuery += c + " ";

        System.err.println("searchString = " + searchQuery);
        FacesContext.getCurrentInstance().getExternalContext().redirect("SearchResult.xhtml?"+searchQuery);
        return searchQuery;
    }

    private ArrayList<SearchCriterion> transformToSparkqlSearchCriteria(Criterion predecessor, Criterion transformer){
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

	public void setCollectionCriterionController(CollectionCriterionController collectionCriterionController)
    {
        this.collectionCriterionController = collectionCriterionController;
    }


    // private List<ResultVO> resultList;
}

package de.mpg.imeji.search;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.apache.myfaces.trinidad.component.UIXIterator;

import de.mpg.imeji.search.simulator.Simulator;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.SearchCriterion;

public class AdvancedSearchController extends BeanHelper
{
    private static Logger logger = Logger.getLogger(AdvancedSearchController.class);
    private String userHandle;
    private CollectionCriterionController collectionCriterionController = null;
    private MDCriterionController mdCriterionController = null;
    private UIXIterator mdCriterionIterator = new UIXIterator();
    private Simulator s = new Simulator();

    public List<SelectItem> getMdList()
    {
        List<SelectItem> mdList = new ArrayList<SelectItem>();
        // TODO: remove static mdprofile list
        try
        {
            for (int i = 0; i < s.getSelectedCollection().getMdList().size(); i++)
            {
                mdList.add(new SelectItem(s.getSelectedCollection().getMdList().get(i).getValue(), s
                        .getSelectedCollection().getMdList().get(i).getLabel()));
            }
        }
        catch (Exception e)
        {
            for (int i = 0; i < s.getDefaultCollection().getMdList().size(); i++)
            {
                mdList.add(new SelectItem(s.getDefaultCollection().getMdList().get(i).getValue(), s
                        .getDefaultCollection().getMdList().get(i).getLabel()));
            }
        }
        return mdList;
    }

    public List<SelectItem> getCollectionList()
    {
        List<SelectItem> collectionList = new ArrayList<SelectItem>();
        // TODO: remove static collection list
        collectionList.add(new SelectItem(null, "--"));
        collectionList.add(new SelectItem(s.getCollection1().getTitle(), s.getCollection1().getTitle()));
        collectionList.add(new SelectItem(s.getCollection2().getTitle(), s.getCollection2().getTitle()));
        collectionList.add(new SelectItem(s.getCollection3().getTitle(), s.getCollection3().getTitle()));
        return collectionList;
    }

    public void collectionChanged(ValueChangeEvent event)
    {
        String selectedCollection = event.getNewValue().toString();
        if (selectedCollection.equals("Birds"))
            s.setSelectedCollection(s.getCollection1());
        else if (selectedCollection.equals("Faces"))
            s.setSelectedCollection(s.getCollection2());
        else if (selectedCollection.equals("Diamonds"))
            s.setSelectedCollection(s.getCollection3());
        else
            s.setSelectedCollection(s.getDefaultCollection());
    }

    public AdvancedSearchController()
    {
        collectionCriterionController = new CollectionCriterionController();
        mdCriterionController = new MDCriterionController();
    }

    public void clearAndInitialAllForms()
    {
        collectionCriterionController = new CollectionCriterionController();
        mdCriterionController = new MDCriterionController();
        this.getCollectionCriterionController().getCollectionCriterionManager().getObjectDM();
    }

    public boolean clearAllForms()
    {
        collectionCriterionController.clearAllForms();
        mdCriterionController.clearAllForms();
        return true;
    }

    public String startSearch()
    {
        String searchQuery = "";
        for (String c : collectionCriterionController.getCollectionCriterionManager().getSearchCriterion())
            searchQuery += c + " ";
        for (String c : mdCriterionController.getMdCriterionManager().getSearchCriterion())
            searchQuery += c + " ";
        System.err.println("searchString = " + searchQuery);
        return searchQuery;
    }

    private ArrayList<SearchCriterion> transformToSparklSearchCriteria(Criterion predecessor, Criterion transformer)
    {
        return null;
    }

    public CollectionCriterionController getCollectionCriterionController()
    {
        return collectionCriterionController;
    }

    public void setCollectionCriterionController(CollectionCriterionController collectionCriterionController)
    {
        this.collectionCriterionController = collectionCriterionController;
    }

    public MDCriterionController getMdCriterionController()
    {
        return mdCriterionController;
    }

    public void setMdCriterionController(MDCriterionController mdCriterionController)
    {
        this.mdCriterionController = mdCriterionController;
    }

    public UIXIterator getMdCriterionIterator()
    {
        return mdCriterionIterator;
    }

    public void setMdCriterionIterator(UIXIterator mdCriterionIterator)
    {
        this.mdCriterionIterator = mdCriterionIterator;
    }

    public String getUserHandle()
    {
        return userHandle;
    }

    public void setUserHandle(String userHandle)
    {
        this.userHandle = userHandle;
    }
    // private List<ResultVO> resultList;
}

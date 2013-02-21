package de.mpg.imeji.presentation.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * the Java Bean for the Start Page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class StartPageBean
{
    private List<Item> carousselImages;
    private SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    private final static int CAROUSSEL_SIZE = 6;

    /**
     * Constructor for the bean
     */
    public StartPageBean()
    {
        loadCarrousselImages();
    }

    /**
     * Load the images for the caroussel, and init the bean objects with it
     */
    private void loadCarrousselImages()
    {
        carousselImages = new ArrayList<Item>();
        ItemController ic = new ItemController(session.getUser());
        List<Item> items = (List<Item>)ic.loadItems(getRandomResults(ic.search(null, null, null, null)), -1, 0);
        carousselImages = new ArrayList<Item>(items);
    }

    /**
     * Load the last images uploaded
     */
    private void loadLastImagesUploaded()
    {
        carousselImages = new ArrayList<Item>();
        ItemController ic = new ItemController(session.getUser());
        SortCriterion sc = new SortCriterion(Search.getIndex(SearchIndex.names.created), SortOrder.DESCENDING);
        List<Item> items = (List<Item>)ic.loadItems(ic.search(null, null, sc, null).getResults(), CAROUSSEL_SIZE, 0);
        carousselImages = new ArrayList<Item>(items);
    }

    /**
     * Takes a number ({@link StartPageBean}.CAROUSSEL_SIZE) of results from a {@link SearchResult}
     * 
     * @param sr
     * @return
     */
    private List<String> getRandomResults(SearchResult sr)
    {
        List<String> l = new ArrayList<String>();
        Random r = new Random();
        for (int i = 0; i < CAROUSSEL_SIZE; i++)
        {
            if (sr.getNumberOfRecords() > 1)
            {
                l.add(sr.getResults().get(r.nextInt(sr.getNumberOfRecords() - 1)));
            }
        }
        return l;
    }

    /**
     * setter
     * 
     * @param carousselImages
     */
    public void setCarousselImages(List<Item> carousselImages)
    {
        this.carousselImages = carousselImages;
    }

    /**
     * getter
     * 
     * @return
     */
    public List<Item> getCarousselImages()
    {
        return carousselImages;
    }
}

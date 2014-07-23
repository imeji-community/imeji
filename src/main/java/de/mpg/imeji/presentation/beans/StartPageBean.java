package de.mpg.imeji.presentation.beans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.search.vo.SortCriterion.SortOrder;
import de.mpg.imeji.logic.util.DateFormatter;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.image.ThumbnailBean;
import de.mpg.imeji.presentation.search.URLQueryTransformer;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * the Java Bean for the Start Page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class StartPageBean
{
    private List<ThumbnailBean> carousselImages = new ArrayList<ThumbnailBean>();
    private SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    private final static int CAROUSSEL_SIZE = 6;
    // in hours
    private int searchforItemCreatedForLessThan = 0;

    /**
     * Constructor for the bean
     * 
     * @throws IOException
     * @throws URISyntaxException
     */
    public StartPageBean() throws IOException, URISyntaxException
    {
        SearchQuery query = readSearchQueryInProperty();
        SortCriterion order = readSortCriterionInProperty();
        SearchResult result = searchItems(query, order);
        loadItemInCaroussel(result, order == null);// if order is null, then it is random
    }

    /**
     * Read the search query defined in the imeji.properties
     * 
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    private SearchQuery readSearchQueryInProperty() throws IOException, URISyntaxException
    {
        String prop = PropertyReader.getProperty("imeji.home.caroussel.query");
        if (prop != null)
        {
            return URLQueryTransformer.parseStringQuery(prop);
        }
        return null;
    }

    /**
     * Read the order defined in the imeji.properties
     * 
     * @return
     * @throws URISyntaxException
     * @throws IOException
     */
    private SortCriterion readSortCriterionInProperty() throws IOException, URISyntaxException
    {
        try
        {
            String[] prop = PropertyReader.getProperty("imeji.home.caroussel.sort").split("-");
            if ("".equals(prop[0]) && "".equals(prop[1]))
                return new SortCriterion(SPARQLSearch.getIndex(prop[0]), SortOrder.valueOf(prop[1].toUpperCase()));
        }
        catch (Exception e)
        {
            // no sort order defined
        }
        return null;
    }

    /**
     * Search the item for the caroussel
     * 
     * @param sq
     * @param sc
     * @return
     */
    private SearchResult searchItems(SearchQuery sq, SortCriterion sc)
    {
        ItemController ic = new ItemController(session.getUser());
        if (sq.isEmpty() && searchforItemCreatedForLessThan > 0)
        {
            // Search for item which have been for less than n hours
            sq.addPair(new SearchPair(SPARQLSearch.getIndex(SearchIndex.names.created), SearchOperators.GREATER,
                    getTimeforNDaybeforeNow(searchforItemCreatedForLessThan)));
            return new SearchResult(ic.search(null, sq, sc, null).getResults(), null);
        }
        return ic.search(null, sq, sc, null);
    }

    /**
     * Return the time of the nth day before the current time
     * 
     * @param day
     * @return
     */
    private String getTimeforNDaybeforeNow(int n)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -n);
        return DateFormatter.formatToSparqlDateTime(cal);
    }

    /**
     * Load the item the {@link SearchResult} in the caroussel. If random true, will load some random items
     * 
     * @param sr
     * @param random
     */
    private void loadItemInCaroussel(SearchResult sr, boolean random)
    {
        ItemController ic = new ItemController(session.getUser());
        List<String> uris = new ArrayList<String>();
        if (random)
        {
            uris = getRandomResults(sr);
        }
        else
        {
            int sublistSize = CAROUSSEL_SIZE;
            if (sr.getResults().size() < CAROUSSEL_SIZE)
                sublistSize = sr.getResults().size();
            if (sublistSize > 0)
                uris = sr.getResults().subList(0, sublistSize);
        }
        List<Item> items = (List<Item>)ic.loadItems(uris, -1, 0);
        carousselImages = ImejiFactory.imageListToThumbList(items);
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
    public void setCarousselImages(List<ThumbnailBean> carousselImages)
    {
        this.carousselImages = carousselImages;
    }

    /**
     * getter
     * 
     * @return
     */
    public List<ThumbnailBean> getCarousselImages()
    {
        return carousselImages;
    }
}

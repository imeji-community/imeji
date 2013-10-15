/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.facet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Java Bean for the {@link Facet}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class FacetsBean implements Callable<Boolean>
{
    private List<List<Facet>> facets = new ArrayList<List<Facet>>();
    private boolean loaded = false;
    private boolean running = false;
    private Facets facetsClass;

    /*
     * (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public Boolean call() throws Exception
    {
        System.out.println("loading");
        running = true;
        facetsClass.init();
        facets = facetsClass.getFacets();
        loaded = true;
        running = false;
        System.out.println("Done");
        return loaded;
    }

    /**
     * Initialize the {@link FacetsBean} for one {@link SearchQuery} from the item browse page
     * 
     * @param searchQuery
     */
    public FacetsBean(SearchQuery searchQuery)
    {
        try
        {
            loaded = false;
            facetsClass = new TechnicalFacets(searchQuery);
        }
        catch (Exception e)
        {
            BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("error")
                    + ", Facets intialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize the {@link FacetsBean} for one {@link SearchQuery} from the collection browse page
     * 
     * @param col
     * @param searchQuery
     */
    public FacetsBean(CollectionImeji col, SearchQuery searchQuery)
    {
        try
        {
            loaded = false;
            facetsClass = new CollectionFacets(col, searchQuery);
        }
        catch (Exception e)
        {
            BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("error")
                    + ", Facets intialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * getter
     * 
     * @return
     */
    public List<List<Facet>> getFacets()
    {
        return facets;
    }

    /**
     * setter
     * 
     * @param facets
     */
    public void setFacets(List<List<Facet>> facets)
    {
        this.facets = facets;
    }

    /**
     * @return the loaded
     */
    public boolean isLoaded()
    {
        System.out.println(loaded);
        return loaded;
    }

    /**
     * @return the running
     */
    public boolean isRunning()
    {
        return running;
    }
}

/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * This abstract bean class is used to manage lists with one or two paginators. It can work together with different
 * BaseListRetrieverRequestBeans that are responsible to retrieve the list elements. On a jsp page, at first the
 * BaseListRetrieverRequestBean has to be initialized. It then automatically updates the list in this bean (by calling
 * update) whenever necessary and reads required GET parameters (via readOutParamaters()). This bean has to be managed
 * in the session scope of JSF. The list only refreshes if any GET parameters have changed or new parameters have been
 * added. If you want to refresh the list anyway, please call the hasChanged method.
 * 
 * @author Markus Haarlaender (initial creation)
 * @author $Author: haarlaender $ (last modification)
 * @version $Revision: 3185 $ $LastChangedDate: 2009-11-20 14:30:15 +0100 (Fri, 20 Nov 2009) $
 * @param <ListElementType> The Type of the list elements managed by this bean
 * @param <FilterType> The type of filters managed by this bean that are usable for every ListRetriever, eg. sorting of
 *            PubItems.
 */
public abstract class BasePaginatorListSessionBean<ListElementType>
{
    protected static Logger logger = Logger.getLogger(BasePaginatorListSessionBean.class);
    /**
     * A list that contains the menu entries of the elements per page menu.
     */
    private List<SelectItem> elementsPerPageSelectItems;
    /**
     * A list containing the PaginatorPage objects
     */
    private List<PaginatorPage> paginatorPageList;
    /**
     * The list containing the current elements of the displayed list
     */
    private List<ListElementType> currentPartList;
    private List<ListElementType> previousPartList;
    /**
     * The current number of elements per page
     */
    private int elementsPerPage = 0;
    /**
     * Bound to the selected value of the lower elementsPerPage selection menu
     */
    private int elementsPerPageBottom;
    /**
     * Bound to the selected value of the upper elementsPerPage selection menu
     */
    private int elementsPerPageTop;
    /**
     * The current paginator page number
     */
    private int currentPageNumber;
    /**
     * The current value of the 'go to' input fields
     */
    private String goToPage;
    /**
     * This attribute is bound to the 'go to' input field of the lower paginator
     */
    private String goToPageBottom;
    /**
     * This attribute is bound to the 'go to' input field of the upper paginator
     */
    private String goToPageTop;
    /**
     * The total number of elements that are in the complete list (without any limit or offset filters). corresponding
     * BaseListRetrieverRequestBean.
     */
    private int totalNumberOfElements = 0;
    private boolean ajaxMode = true;

    /**
     * Initializes a new BasePaginatorListSessionBean
     */
    public BasePaginatorListSessionBean()
    {
        paginatorPageList = new ArrayList<PaginatorPage>();
        currentPartList = new ArrayList<ListElementType>();
        try
        {
            ajaxMode = Boolean.valueOf(PropertyReader.getProperty("imeji.pagination.ajaxmodus"));
        }
        catch (Exception e)
        {
            logger.error("Error reading property imeji.pagination.ajaxmodus", e);
        }
        elementsPerPageSelectItems = new ArrayList<SelectItem>();
    }

    // Must be called by PrettyFaces action method
    public void reset()
    {
        setCurrentPageNumber(1);
        setElementsPerPage(24);
    }

    // Must be called by PrettyFaces action method
    public String getInitPaginator()
    {
        update();
        return "";
    }

    // Should not be call in AJAX area
    public String getUrlParameters()
    {
        if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("page"))
        {
            currentPageNumber = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestParameterMap().get("page"));
        }
        if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().containsKey("el"))
        {
            elementsPerPage = Integer.parseInt(FacesContext.getCurrentInstance().getExternalContext()
                    .getRequestParameterMap().get("el"));
        }
        return "";
    }

    /**
     * This method is called by the corresponding BaseListRetrieverRequestBean whenever the list has to be updated. It
     * reads out basic parameters and calls readOutParamters on implementing subclasses. It uses the
     * BaseListRetrieverRequestBean in order to retrieve the new list and finally calls listUpdated on implementing
     * subclasses.
     */
    public void update()
    {
        // ImejiJena.printModel(ImejiJena.imageModel);
        try
        {
            if (elementsPerPage == 0)
            {
                setElementsPerPage(24);
            }
            if (currentPageNumber == 0)
            {
                setCurrentPageNumber(1);
            }
            setGoToPageBottom(Integer.toString(currentPageNumber));
            setGoToPageTop(Integer.toString(currentPageNumber));
            previousPartList = new ArrayList<ListElementType>();
            previousPartList.addAll(currentPartList);
            currentPartList = retrieveList(getOffset(), elementsPerPage);
            totalNumberOfElements = getTotalNumberOfRecords();
            // reset current page and reload list if list is shorter than the given current page number allows
            if (getTotalNumberOfElements() <= getOffset())
            {
                setCurrentPageNumber(((getTotalNumberOfElements() - 1) / getElementsPerPage()) + 1);
                currentPartList = retrieveList(getOffset(), elementsPerPage);
                totalNumberOfElements = getTotalNumberOfRecords();
            }
            paginatorPageList.clear();
            for (int i = 0; i < ((getTotalNumberOfElements() - 1) / elementsPerPage) + 1; i++)
            {
                paginatorPageList.add(new PaginatorPage(i + 1));
            }
        }
        catch (Exception e)
        {
            BeanHelper.error(e.getMessage());
            logger.error("Error paginator list update ", e);
        }
    }

    /**
     * Returns the current list with the specified elements
     * 
     * @return
     */
    public List<ListElementType> getCurrentPartList()
    {
        return currentPartList;
    }

    /**
     * Returns the size of the current list
     * 
     * @return
     */
    public int getPartListSize()
    {
        return getCurrentPartList().size();
    }

    // protected abstract List<ListElementType> getPartList(int offset, int limit);
    /**
     * Returns the total number of elements, without offset and limit filters. Drawn from BaseRetrieverRequestBean
     */
    public int getTotalNumberOfElements()
    {
        return totalNumberOfElements;
    }

    /**
     * Returns the current offset (starting with 0)
     */
    public int getOffset()
    {
        return ((currentPageNumber - 1) * elementsPerPage);
    }

    /*
     * public abstract String getAdditionalParameterUrl();
     */
    /**
     * Sets the current value for 'element per pages'
     */
    public void setElementsPerPage(int elementsPerPage)
    {
        this.elementsPerPage = elementsPerPage;
        this.elementsPerPageTop = elementsPerPage;
        this.elementsPerPageBottom = elementsPerPage;
    }

    /**
     * WARNING: USE THIS METHOD ONLY FROM UPPER PAGINATOR SELECTION MENU IN JSPF. For setting the value manually, use
     * setElementsPerPage().
     * 
     * @param elementsPerPageTop
     */
    @Deprecated
    public void setElementsPerPageTop(int elementsPerPageTop)
    {
        this.elementsPerPageTop = elementsPerPageTop;
    }

    /**
     * WARNING: USE THIS METHOD ONLY FROM LOWER PAGINATOR SELECTION MENU IN JSPF. For setting the value manually, use
     * setElementsPerPage().
     * 
     * @param elementsPerPageTop
     */
    @Deprecated
    public void setElementsPerPageBottom(int elementsPerPageBottom)
    {
        this.elementsPerPageBottom = elementsPerPageBottom;
    }

    /**
     * WARNING: USE THIS METHOD ONLY FROM UPPER PAGINATOR SELECTION MENU IN JSPF. For getting the value manually, use
     * getElementsPerPage().
     * 
     * @param elementsPerPageTop
     */
    @Deprecated
    public int getElementsPerPageTop()
    {
        return elementsPerPageTop;
    }

    /**
     * WARNING: USE THIS METHOD ONLY FROM UPPER PAGINATOR SELECTION MENU IN JSPF. For getting the value manually, use
     * getElementsPerPage().
     * 
     * @param elementsPerPageTop
     */
    @Deprecated
    public int getElementsPerPageBottom()
    {
        return elementsPerPageBottom;
    }

    /**
     * Returns the currently selected number of elements per page
     * 
     * @return
     */
    public int getElementsPerPage()
    {
        return elementsPerPage;
    }

    /**
     * Used as action when the user changes the upper number of elements menu.
     * 
     * @return
     * @throws Exception
     */
    public String changeElementsPerPageTop() throws Exception
    {
        setElementsPerPage(getElementsPerPageTop());
        // set new PageNumber to a number where the first element of the current Page is still displayed
        setCurrentPageNumber(((currentPageNumber - 1 * elementsPerPage + 1) / (elementsPerPage)) + 1);
        return getPrettyNavigation();
    }

    /**
     * Used as action when the user changes the lower number of elements menu.
     * 
     * @return
     * @throws Exception
     */
    public String changeElementsPerPageBottom() throws Exception
    {
        setElementsPerPage(getElementsPerPageBottom());
        // set new PageNumber to a number where the first element of the current Page is still displayed
        setCurrentPageNumber(((currentPageNumber - 1 * elementsPerPage + 1) / (elementsPerPage)) + 1);
        return getPrettyNavigation();
    }

    /**
     * Used as action when the user sends an value from the upper go to input field
     * 
     * @return
     * @throws Exception
     */
    public String goToPageTop()
    {
        try
        {
            int goToPage = Integer.parseInt(getGoToPageTop());
            if (goToPage > 0 && goToPage <= getPaginatorPageSize())
            {
                setCurrentPageNumber(goToPage);
                setGoToPageBottom(String.valueOf(goToPage));
            }
            else
            {
                BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class))
                        .getMessage("error_page_not_exists"));
            }
        }
        catch (Exception e)
        {
            // error(getMessage("paginator_errorGoToPage"));
        }
        // return getPrettyNavigation();
        return getNavigationString();
    }

    /**
     * Used as action when the user sends an value from the lower go to input field
     * 
     * @return
     * @throws Exception
     */
    public String goToPageBottom()
    {
        try
        {
            int goToPage = Integer.parseInt(getGoToPageBottom());
            if (goToPage > 0 && goToPage <= getPaginatorPageSize())
            {
                setCurrentPageNumber(goToPage);
                setGoToPageTop(String.valueOf(goToPage));
            }
            else
            {
                BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class))
                        .getMessage("error_page_not_exists"));
            }
        }
        catch (Exception e)
        {
            // error(getMessage("paginator_errorGoToPage"));
        }
        return getNavigationString();
    }

    /**
     * Returns the current page number of the paginator
     * 
     * @return
     */
    public int getCurrentPageNumber()
    {
        return currentPageNumber;
    }

    /**
     * Returns a list with the paginator pages. Used from jsf to iterate over the numbers
     * 
     * @return
     */
    public List<PaginatorPage> getPaginatorPages()
    {
        return paginatorPageList;
    }

    /**
     * Returns the number of all paginator pages, not only the visible ones
     * 
     * @return
     */
    public int getPaginatorPageSize()
    {
        return getPaginatorPages().size();
    }

    /**
     * Returns the number of the paginator page button that should be displayed as first button of the paginator in
     * order to always display exactly seven paginator page buttons
     * 
     * @return
     */
    public int getFirstPaginatorPageNumber()
    {
        if (getPaginatorPageSize() > 7 && currentPageNumber > getPaginatorPageSize() - 4)
        {
            return getPaginatorPageSize() - 6;
        }
        else if (getPaginatorPageSize() > 7 && currentPageNumber > 4)
        {
            return currentPageNumber - 3;
        }
        else
        {
            return 1;
        }
    }

    /**
     * Sets the menu entries of the elements per page menu
     * 
     * @param elementsPerPageSelectItems
     */
    public void setElementsPerPageSelectItems(List<SelectItem> elementsPerPageSelectItems)
    {
        this.elementsPerPageSelectItems = elementsPerPageSelectItems;
    }

    /**
     * Returns the menu entries of the elements per page menu
     * 
     * @return
     */
    public List<SelectItem> getElementsPerPageSelectItems()
    {
        return elementsPerPageSelectItems;
    }

    /**
     * Inner class pf which an instance represents an paginator button. Used by the iterator in jsf.
     * 
     * @author Markus Haarlaender (initial creation)
     * @author $Author: haarlaender $ (last modification)
     * @version $Revision: 3185 $ $LastChangedDate: 2009-11-20 14:30:15 +0100 (Fri, 20 Nov 2009) $
     */
    public class PaginatorPage
    {
        /**
         * The page number of the paginator button
         */
        private int number;

        public PaginatorPage(int number)
        {
            this.number = number;
        }

        /**
         * Sets the page number of the paginator button
         * 
         * @param number
         */
        public void setNumber(int number)
        {
            this.number = number;
        }

        /**
         * Returns the page number of the paginator button
         * 
         * @return
         */
        public int getNumber()
        {
            return number;
        }

        /**
         * Returns the link that is used as output link of the paginator page button
         * 
         * @return
         */
        public String goToPage()
        {
            setCurrentPageNumber(getNumber());
            return "";
            // return getNavigationString();
        }
    }

    /**
     * Returns the link for the "Next"-Button of the Paginator
     * 
     * @return
     */
    public String goToNextPage()
    {
        currentPageNumber += 1;
        return getPrettyNavigation();
    }

    /**
     * Returns the link for the "Previous"-Button of the Paginator
     * 
     * @return
     */
    public String goToPreviousPage()
    {
        currentPageNumber -= 1;
        return getPrettyNavigation();
    }

    /**
     * Returns the link for the "First Page"-Button of the Paginator
     * 
     * @return
     */
    public String goToFirstPage()
    {
        currentPageNumber = 1;
        return getPrettyNavigation();
    }

    /**
     * Returns the link for the "Last Page"-Button of the Paginator
     * 
     * @return
     */
    public String goToLastPage()
    {
        currentPageNumber = getPaginatorPageSize();
        return getPrettyNavigation();
    }

    /**
     * Sets the current paginator page number
     * 
     * @param currentPageNumber
     */
    public void setCurrentPageNumber(int currentPageNumber)
    {
        this.currentPageNumber = currentPageNumber;
    }

    /**
     * WARNING: USE THIS METHOD ONLY FROM UPPER GO TO INPUT FIELD MENU IN JSPF. For setting the value manually, use
     * setGoToPage().
     * 
     * @param elementsPerPageTop
     */
    @Deprecated
    public void setGoToPageTop(String goToPage)
    {
        this.goToPageTop = goToPage;
    }

    /**
     * WARNING: USE THIS METHOD ONLY FROM UPPER GO TO INPUT FIELD MENU IN JSPF. For getting the value manually, use
     * getGoToPage().
     * 
     * @param elementsPerPageTop
     */
    @Deprecated
    public String getGoToPageTop()
    {
        return this.goToPageTop;
    }

    /**
     * WARNING: USE THIS METHOD ONLY FROM LOWER GO TO INPUT FIELD MENU IN JSPF. For setting the value manually, use
     * setGoToPage().
     * 
     * @param elementsPerPageTop
     */
    @Deprecated
    public void setGoToPageBottom(String goToPage)
    {
        this.goToPageBottom = goToPage;
    }

    /**
     * WARNING: USE THIS METHOD ONLY FROM LOWER GO TO INPUT FIELD MENU IN JSPF. For getting the value manually, use
     * getGoToPage().
     * 
     * @param elementsPerPageTop
     */
    @Deprecated
    public String getGoToPageBottom()
    {
        return this.goToPageBottom;
    }

    /**
     * Sets the value of the go to input fields.
     * 
     * @param goToPage
     */
    public void setGoToPage(String goToPage)
    {
        this.goToPage = goToPage;
        this.goToPageTop = goToPage;
        this.goToPageBottom = goToPage;
    }

    /**
     * Returns the value of the go to input fields.
     * 
     * @return
     */
    public String getGoToPage()
    {
        return goToPage;
    }

    /**
     * Whenever this method is called, an updated list with elements of type ListelementType has to be returned
     * 
     * @param offset An offset from where the list must start (0 means at the beginning of all records)
     * @param limit The length of the list that has to be returned. If the whole list has less records than this
     *            paramter allows, a smaller list can be returned.
     * @param additionalFilters Additional filters that have to be included when retrieving the list.
     * @return
     * @throws Exception
     */
    public abstract List<ListElementType> retrieveList(int offset, int limit) throws Exception;

    /**
     * Must return the total size of the retrieved list without limit and offset parameters. E.g. for a search the whole
     * number of search records
     * 
     * @return The whole number of elements in the list, regardless of limit and offset parameters
     */
    public abstract int getTotalNumberOfRecords();

    public abstract String getNavigationString();

    public String getPrettyNavigation()
    {
        if (ajaxMode)
            return "";
        else
            return getNavigationString();
    }

    public boolean isAjaxMode()
    {
        return ajaxMode;
    }
}

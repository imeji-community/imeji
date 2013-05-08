/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.history;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.presentation.history.Page.ImejiPages;

/**
 * JavaBean for the http session object related to the history
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class HistorySession
{
    /**
     * {@link List} of {@link Page} stored in the history
     */
    private List<Page> pages = new ArrayList<Page>();
    /**
     * The maximum count of {@link Page} stored in the history
     */
    private static int HISTORY_SIZE = 10;

    /**
     * Create new {@link HistorySession}
     */
    public HistorySession()
    {
    }

    /**
     * Add a new {@link Page} to the History
     * 
     * @param filename
     * @param query
     * @param id
     */
    public void add(String filename, String query, String[] id)
    {
        Page newPage = createPage(filename, query, id);
        addPage(newPage);
        removeOldPages();
    }

    /**
     * Add a {@link Page} to the history
     * 
     * @param page
     * @param id - the ids defined in the url
     */
    public void addPage(Page page)
    {
        if (page != null)
        {
            if (!page.isSame(getCurrentPage()))
            {
                pages.add(page);
            }
        }
    }

    /**
     * Create {@link Page} according to the url
     * 
     * @param filename: the name of the xhtml file
     * @param query the current query (defined by parameter q)
     * @param id - the ids found in the url
     * @return
     */
    private Page createPage(String filename, String query, String[] id)
    {
        for (ImejiPages type : ImejiPages.values())
        {
            if (type.getFileName().equals(filename))
            {
                try
                {
                    if (ImejiPages.IMAGES.equals(type) && query != null && !"".equals(query))
                    {
                        // If a browse page has a query, change the type of the page to search result
                        type = ImejiPages.SEARCH_RESULTS_IMAGES;
                    }
                    else if (ImejiPages.SEARCH_RESULTS_IMAGES.equals(type) && ("".equals(query) || query == null))
                    {
                        // If a searchPage doesn't have a query, change the type to Browse page
                        type = ImejiPages.IMAGES;
                    }
                    Page page = new Page(type, PageURIHelper.getPageURI(type, query, id));
                    if (id != null)
                    {
                        if (id.length == 2)
                        {
                            page.setId(id[1]);
                        }
                        if (id.length == 1)
                        {
                            page.setId(id[0]);
                        }
                    }
                    return page;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * Remove {@link Page} of the history, when the size of the history is greater thant the maximum size
     */
    private void removeOldPages()
    {
        while (pages.size() > HISTORY_SIZE)
        {
            pages.remove(0);
        }
    }

    /**
     * Remove a {@link Page} of the history according to its position in the history
     * 
     * @param pos
     */
    public void remove(int pos)
    {
        for (int i = 0; i < pages.size(); i++)
        {
            if (i > pos)
            {
                pages.remove(i);
                i--;
            }
        }
    }

    /**
     * Return the current {@link Page}
     * 
     * @return
     */
    public Page getCurrentPage()
    {
        if (!pages.isEmpty())
        {
            return pages.get(pages.size() - 1);
        }
        return null;
    }

    /**
     * Return the previous {@link Page} in the history
     * 
     * @return
     */
    public Page getPreviousPage()
    {
        if (!pages.isEmpty())
        {
            return pages.get(pages.size() - 2);
        }
        return null;
    }

    /**
     * Return the size of the history
     * 
     * @return
     */
    public int getHistorySize()
    {
        return pages.size();
    }

    /**
     * Getter- Return the {@link List} of {@link Page} of the history
     * 
     * @return
     */
    public List<Page> getPages()
    {
        return pages;
    }

    /**
     * setter
     * 
     * @param pages
     */
    public void setPages(List<Page> pages)
    {
        this.pages = pages;
    }
}

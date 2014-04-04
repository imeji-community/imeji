/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.history;

import java.util.ArrayList;
import java.util.List;

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
            	page.setPos(pages.size());
                pages.add(page);
                removeOldPages();
            }
        }
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

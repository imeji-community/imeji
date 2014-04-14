/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.history;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.filter.Filter;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ObjectLoader;

/**
 * An imeji web page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Page
{
    private String name;
    private List<Filter> filters = new ArrayList<Filter>();
    private String query = "";
    private String id = null;
    private Map<String, String[]> params;
    private int pos = 0;
    private String url;
    private String title;

    /**
     * Constructor with for one {@link URI}
     * 
     * @param uri
     */
    public Page(String url, String label, Map<String, String[]> params)
    {
        this.setUrl(url);
        name = label;
        this.params = params;
        id = PageURIHelper.extractId(getCompleteUrl());
        title = loadTitle(id);
    }

    /**
     * Load the title the object of the current page according to its id
     * 
     * @param id
     * @return
     */
    private String loadTitle(String id)
    {
        String title = id;
        if (id != null)
        {
            if (url.matches(".+/collection.+"))
            {
                title = ObjectLoader
                        .loadCollectionLazy(ObjectHelper.getURI(CollectionImeji.class, id),
                                ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getUser()).getMetadata()
                        .getTitle();
            }
            else if (url.matches(".+/album.+"))
            {
                title = ObjectLoader
                        .loadAlbumLazy(ObjectHelper.getURI(Album.class, id),
                                ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getUser()).getMetadata()
                        .getTitle();
            }
            else if (url.matches(".+/item.+"))
            {
                title = ObjectLoader.loadItem(ObjectHelper.getURI(Item.class, id),
                        ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getUser()).getFilename();
            }
            else if (url.matches(".+/usergroup.*"))
            {
                title = ObjectLoader.loadUserGroupLazy(id,
                        ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getUser()).getName();
            }
            else if (id.matches(".*@.*"))
            {
                title = ObjectLoader
                        .loadUser(id, ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getUser()).getName();
            }
            // Cut the name of the object
            if (title != null && title.length() > 20)
            {
                title = title.substring(0, 20) + "...";
            }
        }
        return title;
    }

    /**
     * Compares 2 {@link Page}
     * 
     * @param page
     * @return
     */
    public boolean isSame(Page page)
    {
        if (isNull() || page == null || page.isNull())
            return false;
        else if (isNull() && page.isNull())
            return true;
        else
            // return (type.equals(page.getType()) && uri.equals(page.getUri()));
            return page.getName().equals(getName());
    }

    public boolean isNull()
    {
        // return (type == null && uri == null);
        return url == null;
    }

    public String getName()
    {
        return name;
    }

    public String getInternationalizedName()
    {
        try
        {
            String inter = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel(name);
            return title != null ? inter + " " + title : inter;
        }
        catch (Exception e)
        {
            return name;
        }
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<Filter> getFilters()
    {
        return filters;
    }

    public void setFilters(List<Filter> filters)
    {
        this.filters = filters;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getPos()
    {
        return pos;
    }

    public void setPos(int pos)
    {
        this.pos = pos;
    }

    public String getCompleteUrlWithHistory()
    {
        String delim = params.isEmpty() ? "?" : "&";
        return getCompleteUrl() + delim + "h=" + pos;
    }

    public String getCompleteUrl()
    {
        return url + getParamsAsString();
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    private String getParamsAsString()
    {
        String s = "";
        for (String key : params.keySet())
        {
            String delim = "".equals(s) ? "?" : "&";
            s += delim + key + "=" + params.get(key)[0];
        }
        return s;
    }
}

/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.history;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.presentation.filter.Filter;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * An imeji web page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class Page
{
    /**
     * Enumeration of all imeji {@link Page}
     * 
     * @author saquet (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    public enum ImejiPages
    {
        IMAGES("Images.xhtml", "history_images"), COLLECTION_IMAGES("CollectionBrowse.xhtml",
                "history_images_collection"), SEARCH("SearchAdvanced.xhtml", "history_advanced_search"), HOME(
                "Welcome.xhtml", "history_home"), IMAGE("Image.xhtml", "history_image"), COLLECTIONS(
                "Collections.xhtml", "history_collections"), ALBUMS("Albums.xhtml", "history_albums"), COLLECTION_HOME(
                "CollectionEntryPage.xhtml", "collection"), SEARCH_RESULTS_IMAGES("Images.xhtml", "Search results"), EDIT(
                "Edit.xhtml", "edit_images"), COLLECTION_IMAGE("CollectionImage.xhtml", "history_image"), ALBUM_IMAGES(
                "AlbumBrowse.xhtml", "history_images_album"), ALBUM_HOME("AlbumEntryPage.xhtml", "history_album"), ALBUM_IMAGE(
                "AlbumImage.xhtml", "history_image"), HELP("Help.xhtml", "help"), COLLECTION_INFO(
                "CollectionView.xhtml", "history_collection_info"), UPLOAD("Upload.xhtml", "history_upload"), USER(
                "User.xhtml", "user");
        private String fileName = "";
        private String label;

        /**
         * Construct an {@link ImejiPages} object
         * 
         * @param fileName
         * @param label
         */
        private ImejiPages(String fileName, String label)
        {
            this.fileName = fileName;
            this.label = label;
        }

        /**
         * The label of the {@link ImejiPages}
         * 
         * @return
         */
        public String getLabel()
        {
            return label;
        }

        /**
         * The filename of the {@link ImejiPages}
         * 
         * @return
         */
        public String getFileName()
        {
            return fileName;
        }
    }

    private ImejiPages type;
    private URI uri;
    private String name;
    private List<Filter> filters = new ArrayList<Filter>();
    private String query = "";
    private String id = null;

    /**
     * Construct a new imeji web page
     * 
     * @param type
     * @param uri
     */
    public Page(ImejiPages type, URI uri)
    {
        this.uri = uri;
        this.type = type;
        this.name = type.getLabel();
    }

    /**
     * Compares 2 {@link Page}
     * 
     * @param page
     * @return
     */
    public boolean isSame(Page page)
    {
        if (isNull() && page.isNull())
            return true;
        else if (isNull() || page == null || page.isNull())
            return false;
        else
            return (type.equals(page.getType()) && uri.equals(page.getUri()));
    }

    public boolean isNull()
    {
        return (type == null && uri == null);
    }

    public URI getUri()
    {
        return uri;
    }

    public void setUri(URI uri)
    {
        this.uri = uri;
    }

    public ImejiPages getType()
    {
        return type;
    }

    public void setType(ImejiPages type)
    {
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public String getInternationalizedName()
    {
        try
        {
            String s = ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel(name);
            if (id != null)
                s += " id " + id;
            return s;
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
}

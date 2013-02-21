/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.vo;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.chrono.AssembledChronology.Fields;

import de.mpg.imeji.logic.search.FulltextIndex;
import de.mpg.j2j.annotations.j2jId;
import de.mpg.j2j.annotations.j2jList;
import de.mpg.j2j.annotations.j2jLiteral;
import de.mpg.j2j.annotations.j2jModel;
import de.mpg.j2j.annotations.j2jResource;

/**
 * imeji item. Can be an image, a video, a sound, etc.
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@j2jResource("http://imeji.org/terms/item")
@j2jModel("item")
@j2jId(getMethod = "getId", setMethod = "setId")
@XmlRootElement(name = "item")
public class Item extends Properties implements FulltextIndex
{
    public enum Visibility
    {
        PUBLIC, PRIVATE;
    }

    private URI id;
    // @j2jResource("http://imeji.org/terms/properties")
    // private Properties properties = new Properties();
    @j2jResource("http://imeji.org/terms/collection")
    private URI collection;
    @j2jList("http://imeji.org/terms/metadataSet")
    private List<MetadataSet> metadataSets = new ArrayList<MetadataSet>();
    @j2jResource("http://imeji.org/terms/webImageUrl")
    private URI webImageUrl;
    @j2jResource("http://imeji.org/terms/thumbnailImageUrl")
    private URI thumbnailImageUrl;
    @j2jResource("http://imeji.org/terms/fullImageUrl")
    private URI fullImageUrl;
    @j2jResource("http://imeji.org/terms/visibility")
    private URI visibility = URI.create("http://imeji.org/terms/visibility#" + Visibility.PRIVATE.name());
    @j2jLiteral("http://imeji.org/terms/filename")
    private String filename;
    @j2jLiteral("http://imeji.org/terms/escidocId")
    private String escidocId;
    @j2jLiteral("http://imeji.org/terms/storageId")
    private String storageId;
    @j2jLiteral("http://imeji.org/terms/fulltext")
    private String fulltext;

    public Item()
    {
    }

    public Item(Item im)
    {
        copyInFields(im);
    }

    public String getEscidocId()
    {
        return escidocId;
    }

    public void setEscidocId(String escidocId)
    {
        this.escidocId = escidocId;
    }

    public URI getWebImageUrl()
    {
        return webImageUrl;
    }

    public void setWebImageUrl(URI webImageUrl)
    {
        this.webImageUrl = webImageUrl;
    }

    public URI getThumbnailImageUrl()
    {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(URI thumbnailImageUrl)
    {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public URI getFullImageUrl()
    {
        return fullImageUrl;
    }

    public void setFullImageUrl(URI fullImageUrl)
    {
        this.fullImageUrl = fullImageUrl;
    }

    public void setVisibility(Visibility visibility)
    {
        this.visibility = URI.create("http://imeji.org/terms/visibility#" + visibility.name());
    }

    public Visibility getVisibility()
    {
        return Visibility.valueOf(visibility.getFragment());
    }

    public MetadataSet getMetadataSet()
    {
        if (metadataSets.size() > 0)
            return metadataSets.get(0);
        return null;
    }

    public void setId(URI id)
    {
        this.id = id;
    }

    public URI getId()
    {
        return id;
    }

    public void setCollection(URI collection)
    {
        this.collection = collection;
    }

    public URI getCollection()
    {
        return collection;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getFilename()
    {
        return filename;
    }

    /**
     * Copy all {@link Fields} of an {@link Item} (including {@link Metadata}) to the current {@link Item}
     * 
     * @param copyFrom
     */
    protected void copyInFields(Item copyFrom)
    {
        Class copyFromClass = copyFrom.getClass();
        Class copyToClass = this.getClass();
        for (Method methodFrom : copyFromClass.getDeclaredMethods())
        {
            String setMethodName = null;
            if (methodFrom.getName().startsWith("get"))
            {
                setMethodName = "set" + methodFrom.getName().substring(3, methodFrom.getName().length());
            }
            else if (methodFrom.getName().startsWith("is"))
            {
                setMethodName = "set" + methodFrom.getName().substring(2, methodFrom.getName().length());
            }
            if (setMethodName != null)
            {
                try
                {
                    Method methodTo = copyToClass.getMethod(setMethodName, methodFrom.getReturnType());
                    try
                    {
                        methodTo.invoke(this, methodFrom.invoke(copyFrom, null));
                    }
                    catch (Exception e)
                    {
                        // logger.error("Could not copy field from method: " + methodFrom.getName(), e);
                    }
                }
                // No setter, do nothing.
                catch (NoSuchMethodException e)
                {
                }
            }
        }
    }

    public void setMetadataSets(List<MetadataSet> metadataSets)
    {
        this.metadataSets = metadataSets;
    }

    public List<MetadataSet> getMetadataSets()
    {
        return metadataSets;
    }

    /**
     * @return the storageId
     */
    public String getStorageId()
    {
        return storageId;
    }

    /**
     * @param storageId the storageId to set
     */
    public void setStorageId(String storageId)
    {
        this.storageId = storageId;
    }

    @Override
    public void setFulltextIndex(String fulltext)
    {
        this.fulltext = fulltext;
    }

    @Override
    public String getFulltextIndex()
    {
        return fulltext;
    }

    /**
     * Set the value for the fulltext search (according to all {@link Metadata} values)
     */
    @Override
    public void indexFulltext()
    {
        fulltext = filename;
        for (Metadata md : getMetadataSet().getMetadata())
        {
            if (!"".equals(md.asFulltext()))
            {
                fulltext += " " + md.asFulltext();
            }
        }
        fulltext = fulltext.trim();
    }

    /**
     * return the {@link URI} of this {@link Item} as a String. Return only the number, not the complete {@link URI}
     * 
     * @return
     */
    public String getIdString()
    {
        if (id != null)
        {
            return id.getPath().substring(id.getPath().lastIndexOf("/"));
        }
        return "";
    }
}

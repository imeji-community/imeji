/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.vo;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URI;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;
import thewebsemantic.RdfType;

@Namespace("http://imeji.mpdl.mpg.de/")
@RdfType("image")
public class Image implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 2244772571432924116L;


	@Namespace("http://imeji.mpdl.mpg.de/image/")
    @RdfType("visibility")
    public enum Visibility
    {
        PUBLIC, PRIVATE;
    }

    private URI id;
    private Properties properties = new Properties();
    private URI webImageUrl;
    private URI thumbnailImageUrl;
    private URI fullImageUrl;
    private Visibility visibility;
    private URI collection;
    private MetadataSet metadataSet = new MetadataSet();
    //private Collection<ImageMetadata> metadata = new LinkedList<ImageMetadata>();
    private String filename;
    private String escidocId;

    
    public Image()
    {
    	
    }
    
    public Image(Image im)
    {
    	copyInFields(im);
    }
    
    public String getEscidocId() {
		return escidocId;
	}

	public void setEscidocId(String escidocId) {
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

    /*
     * public void setProperties(ImejiProperties properties) { this.properties = properties; } public ImejiProperties
     * getProperties() { return properties; }
     */
    public void setVisibility(Visibility visibility)
    {
        this.visibility = visibility;
    }

    public Visibility getVisibility()
    {
        return visibility;
    }

    //@RdfProperty("http://imeji.mpdl.mpg.de/metadataSet")
    public MetadataSet getMetadataSet() 
    {
		return metadataSet;
	}

	public void setMetadataSet(MetadataSet metadataSet) 
	{
		this.metadataSet = metadataSet;
	}
    
    public void setId(URI id)
    {
        this.id = id;
    }

	@Id
    public URI getId()
    {
        return id;
    }

    public void setProperties(Properties properties)
    {
        this.properties = properties;
    }

    @RdfProperty("http://imeji.mpdl.mpg.de/properties")
    public Properties getProperties()
    {
        return properties;
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
    
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected void copyInFields(Image copyFrom)
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
                    	methodTo.invoke(this, methodFrom.invoke(copyFrom, (Object[])null));
                    }
                    catch (Exception e)
                    {
                        //logger.error("Could not copy field from method: " + methodFrom.getName(), e);
                    }
                }
                // No setter, do nothing.
                catch (NoSuchMethodException e)
                {
                    
                }
            }
        }
    }
    
    
}

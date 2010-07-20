package de.mpg.escidoc.faces.item;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.xml.rpc.ServiceException;

import de.escidoc.schemas.components.x09.ComponentDocument.Component;
import de.escidoc.schemas.item.x09.ItemDocument;
import de.escidoc.schemas.item.x09.ItemDocument.Item;
import de.mpg.escidoc.faces.beans.SessionBean;
import de.mpg.escidoc.faces.container.album.AlbumSession;
import de.mpg.escidoc.faces.container.album.AlbumVO;
import de.mpg.escidoc.faces.metadata.MdsItemVO;
import de.mpg.escidoc.faces.util.BeanHelper;
import de.mpg.escidoc.faces.util.UrlHelper;
import de.mpg.escidoc.services.common.xmltransforming.JiBXHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.escidoc.services.framework.ServiceLocator;

/**
 * An item of the Solution.
 * @author saquet
 *
 */
public class ItemVO
{
    /**
     * The itemDocument got directly from the FW.
     */
    protected Item item;
    
    /**
     * The md-records of the item.
     */
    protected MdsItemVO mdRecords = null;
   
    /**
     * The type of the item, for instance a face-item, or a diamonds item.
     * Use for rendering of specific feature;
     */
    private String type = null;
    
    /**
     * Define is the item is in the current Album
     */
    protected boolean inAlbum = false;
    
    /**
     * The style class of the item in the browsing page
     */
    private String itemStyleClass;
    
    /**
     * The Style class of the image of the item.
     */
    private String imageStyleClass;
    
    /**
     * If the item checkbox is selected
     */
    private boolean selected;
    
    /**
     * Constructor for an ItemVO.
     * @param item
     */
    public ItemVO(ItemDocument item)
    {
        this.item = item.getItem();
        mdRecords = new MdsItemVO(item.getItem().getMdRecords().xmlText());
        type = mdRecords.getFirstMdRecord().getDescriptionArray(0).getResourceClass();
        init();
    }
    
    /**
     * Constructor for an ItemVO
     * @param item
     */
    public ItemVO(Item item)
    {
        this.item = item;
        mdRecords = new MdsItemVO(item.getMdRecords().xmlText());
        type = mdRecords.getFirstMdRecord().getDescriptionArray(0).getResourceClass();
        init();
    }
    
    /**
     * Constructor for an itemVO.
     * @param item ItemVO.
     */
    public ItemVO(ItemVO item)
    {
        this.item = item.getItem();
        mdRecords = item.getMdRecords();
        type = item.getType();
        init();
    }
    
    /**
     * Initialize variables of the item
     */
    public void init()
    {
        AlbumSession albumSession = (AlbumSession) BeanHelper.getSessionBean(AlbumSession.class);
        AlbumVO album = albumSession.getActive();
        if (album == null ||
                album.getMembersId().indexOf(item.getObjid()) == -1)
        {
            inAlbum = false;
        }   
        else
        {
            inAlbum = true;
        }
    }
    
    /**
     * Add the item to the current album
     * @return a leer string
     * @throws Exception
     */
    public String addToCurrentAlbum() throws Exception 
    {
        // initializations
        SessionBean sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        AlbumSession albumSession = (AlbumSession) BeanHelper.getSessionBean(AlbumSession.class);
        FacesContext context = FacesContext.getCurrentInstance();
        AlbumVO album = albumSession.getActive(); 
        // add of the item if item not already in the list
        if (album.getMembers().indexOf(item.getProperties().getLatestRelease().getObjid()) == -1)
        {
            // add
            String param ="<param last-modification-date=\"";
            param += JiBXHelper.serializeDate(album.getLatestVersion().getModificationDate());
            param += "\"><id>";
            param += item.getProperties().getLatestRelease().getObjid();
            param += "</id></param>";
            ServiceLocator.getContainerHandler(sessionBean.getUserHandle()).addMembers(album.getLatestVersion().getObjectId(), param);
        }
        UrlHelper urlHelper = (UrlHelper) BeanHelper.getRequestBean(UrlHelper.class);
        // Reload the current Page
        context.getExternalContext().redirect(urlHelper.getCompleteUrl());
        
        return "";
    }
    
    /**
     * Remove this item from the current album
     * @return a leer string 
     * @throws Exception
     */
    public String removeFromCurrentAlbum() throws Exception
    {
        // initializations
        SessionBean sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        AlbumSession albumSession = (AlbumSession) BeanHelper.getSessionBean(AlbumSession.class);
        FacesContext context = FacesContext.getCurrentInstance();
        AlbumVO album = albumSession.getActive();
        // define parameter 
        String param ="<param last-modification-date=\"";
        param += JiBXHelper.serializeDate(album.getLatestVersion().getModificationDate());
        param += "\"><id>";
        param += item.getProperties().getLatestRelease().getObjid();
        param += "</id></param>";
        
        ServiceLocator.getContainerHandler(sessionBean.getUserHandle())
                .removeMembers(album.getLatestVersion().getObjectId(), param);
                
        // Reload the current Page
        UrlHelper urlHelper = (UrlHelper) BeanHelper.getRequestBean(UrlHelper.class);
        // Reload the current Page
        context.getExternalContext().redirect(urlHelper.getCompleteUrl());

        return "";
    }
    
    /**
     * Get the URL of the Thumbnail image.
     * @return
     * @throws Exception
     */
    public String getThumbnailUrl() throws Exception
    {   
        for (Component c : item.getComponents().getComponentArray())
        {
            if (c.getProperties().getContentCategory().equals(PropertyReader.getProperty("xsd.metadata.content-category.thumbnail")) 
                    || c.getProperties().getContentCategory().equals("small_photo") )
            {
                // method with servlet
                //String url = navigation.getApplicationUrl() + "image?href=" + f.getContent();
                
                // Method without servlet
                String url =  ServiceLocator.getFrameworkUrl()   + c.getContent().getHref();
                return url;
            }
        }
        
        return null;
    }
    
    /**
     * Get the URL of the component in original format.
     * @return
     * @throws Exception
     */
    public String getOriginalUrl() throws Exception
    {        
        boolean found = false;
       
        for (Component c : item.getComponents().getComponentArray())
        {
            if (c.getProperties().getContentCategory().equals(PropertyReader.getProperty("xsd.metadata.content-category.original-resolution"))
                    || c.getProperties().getContentCategory().equals("medium_photo")
                    || c.getProperties().getContentCategory().equals("original resolution"))
            {
                found = true;
                // Method without Servlet
                String url =  ServiceLocator.getFrameworkUrl()   + c.getContent().getHref();
                
                return url;
            }
        }
        
        if (!found)
        {
            for (Component c : item.getComponents().getComponentArray())
            {
                if (c.getProperties().getContentCategory().equals(PropertyReader.getProperty("xsd.metadata.content-category.web-resolution")))
                {
                    found = true;
                    // Method without Servlet
                    String url =  ServiceLocator.getFrameworkUrl()   + c.getContent().getHref();
                    
                    return url;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Get the Url of the we Format of the images
     * @return
     * @throws Exception
     */
    public String getWebResolutionUrl() throws Exception
    {
        for (Component c : item.getComponents().getComponentArray())
        {
            if (c.getProperties().getContentCategory().equals(PropertyReader.getProperty("xsd.metadata.content-category.web-resolution"))
                    || c.getProperties().getContentCategory().equals("medium_photo"))
            {
                // Method with Servlet
                //String url = navigation.getApplicationUrl() + "image?href=" + f.getContent();
                
                // Method without Servlet
                String url =  ServiceLocator.getFrameworkUrl()   + c.getContent().getHref();
                
                return url;
            }
        }
        
        return null;
    }

    public Item getItem()
    {
        return item;
    }

    public void setItem(Item item)
    {
        this.item = item;
    }

    public MdsItemVO getMdRecords()
    {
        return mdRecords;
    }

    public void setMdRecords(MdsItemVO mdRecords)
    {
        this.mdRecords = mdRecords;
    }
    
    /**
     * Get the url of the technical metadata.
     * @return
     * @throws NamingException
     * @throws ServiceException
     * @throws URISyntaxException
     */
    public String getTechnicalMetadata() throws NamingException, ServiceException, URISyntaxException
    {
        String id = null;
        
        for (int i = 0; i < item.getComponents().sizeOfComponentArray(); i++)
        {
            if ("high resolution".equals(item.getComponents().getComponentArray(i).getProperties().getDescription()))
            {
                id = item.getComponents().getComponentArray(i).getObjid();
            }
        }
        
        if (id != null)
        {
            String link = ServiceLocator.getFrameworkUrl() + "/ir/item/" + item.getObjid() + "/components/component/" + id + "/md-records/md-record/technical-md";
            return link;
        }
        
        return null;
    }
    
    /**
     * Check if the item is in the current album
     * @return a boolean
     * @throws Exception 
     */
    public boolean getInAlbum() throws Exception
    {        
        init();
        return inAlbum;
    }

    public void setInAlbum(boolean inAlbum)
    {
        this.inAlbum = inAlbum;
    }
    
    /**
     * Get the CSS class of a thumbnails in browse page.
     * @return
     * @throws Exception
     */
    public String getItemStyleClass() throws Exception
    {
        SessionBean sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        init();
       
        if (!inAlbum || sessionBean.getCurrentUrl().equals("viewAlbum"))
        {
            itemStyleClass = "free_area1 matrixItem";
        }
        else
        {
            itemStyleClass = "free_area1 matrixItem inAlbum";
        }

        return itemStyleClass;
    }

    public void setItemStyleClass(String itemStyleClass)
    {
        this.itemStyleClass = itemStyleClass;
    }
    
    /**
     * Get the CSS class of an detail view image.
     * @return
     * @throws Exception
     */
    public String getImageStyleClass() throws Exception
    {
        SessionBean sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
        init();
        if (!inAlbum
                || sessionBean.getCurrentUrl().equals("detailsFromAlbum")
                || sessionBean.getCurrentUrl().equals("comparisonFromAlbum"))
        {
            imageStyleClass = "";
        }
        else
        {
            imageStyleClass = "inAlbum";
        }
        return imageStyleClass;
    }

    public void setImageStyleClass(String imageStyleClass)
    {
        this.imageStyleClass = imageStyleClass;
    }
    
    public String getFileName() throws IOException, URISyntaxException
    {
        for (Component c : item.getComponents().getComponentArray())
        {
            if (c.getProperties().getContentCategory().equals(PropertyReader.getProperty("xsd.metadata.content-category.original-resolution"))
                    || c.getProperties().getContentCategory().equals("medium_photo")
                    || c.getProperties().getContentCategory().equals("original resolution"))
            {
                return c.getProperties().getFileName().replace("_high_resolution.jpg", ".jpg");
            }
        }
        
        return null;
    }

    /**
     * Return type (face-item, diamonds, ...) of the item.
     * @return
     */
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}
}

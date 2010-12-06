package de.mpg.imeji.image;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.event.ValueChangeEvent;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import de.mpg.escidoc.services.framework.PropertyReader;
import de.mpg.imeji.album.AlbumBean;
import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.metadata.EditMetadataBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.LoginHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;

public class ImageBean
{
    public enum TabType
    {
        VIEW, EDIT, TECHMD;
    }

    private String tab = null;
    private SessionBean sessionBean = null;
    private Image image;
    private String id = null;
    private boolean selected;
    private ImageController imageController = null;
    private List<ImageMetadata> imgMetadata;
    private EditMetadataBean editMetadataBean;
    private CollectionImeji collection;
    private CollectionController collectionController;
    private String previous = null;
    private String next = null;
    private List<String> techMd;
    private Navigation navigation;
    protected String prettyLink;

    public ImageBean(Image img)
    {
        this.image = img;
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        imageController = new ImageController(sessionBean.getUser());
        imgMetadata = new ArrayList<ImageMetadata>();
        prettyLink = "pretty:editImage";
        if (sessionBean.getSelected().contains(img.getId()))
        {
            setSelected(true);
        }
    }

    public ImageBean()
    {
        image = new Image();
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        imageController = new ImageController(sessionBean.getUser());
        collectionController = new CollectionController(sessionBean.getUser());
        imgMetadata = new ArrayList<ImageMetadata>();
        prettyLink = "pretty:editImage";
    }

    public void init() throws Exception
    {
        try 
        {
        	image = imageController.retrieve(id);
		} 
        catch (Exception e) 
		{
			BeanHelper.error(id + " not found");
		}
        collection = collectionController.retrieve(this.getImage().getCollection());
        if (sessionBean.getSelected().contains(image.getId()))
        {
            setSelected(true);
        }
        if (UrlHelper.getParameterBoolean("reset"))
        {
            this.initEditMetadataBean();
        }
    }
    
    public void initEditMetadataBean()
    {
    	editMetadataBean = new EditMetadataBean(image, prettyLink);
    }
    
    public String getExpandAll()
    {
    	editMetadataBean.expandAllMetadata();
    	return "pretty:";
    }

    public void initView() throws Exception
    {
        this.init();
        setTab(TabType.VIEW.toString());
    }

    public void initEdit() throws Exception
    {
        this.init();
        setTab(TabType.EDIT.toString());
    }

    public void initTechMd() throws Exception
    {
        this.init();
        setTab(TabType.TECHMD.toString());
    }

    public List<String> getTechMd() throws Exception
    {
        techMd = new ArrayList<String>();
        URI uri = image.getFullImageUrl();
        String imageUrl = uri.toURL().toString();
        GetMethod method = new GetMethod(imageUrl);
        method.setFollowRedirects(false);
        String userHandle = null;
        userHandle = LoginHelper.login(PropertyReader.getProperty("imeji.escidoc.user"), PropertyReader
                .getProperty("imeji.escidoc.password"));
        method.addRequestHeader("Cookie", "escidocCookie=" + userHandle);
        HttpClient client = new HttpClient();
        client.executeMethod(method);
        InputStream input = method.getResponseBodyAsStream();
        ImageInputStream iis = ImageIO.createImageInputStream(input);
        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
        if (readers.hasNext())
        {
            // pick the first available ImageReader
            ImageReader reader = readers.next();
            // attach source to the reader
            reader.setInput(iis, true);
            // read metadata of first image
            IIOMetadata metadata = reader.getImageMetadata(0);
            String[] names = metadata.getMetadataFormatNames();
            int length = names.length;
            for (int i = 0; i < length; i++)
            {
                displayMetadata(this.techMd, metadata.getAsTree(names[i]));
            }
        }
        return techMd;
    }

    static void displayMetadata(List<String> techMd, Node root)
    {
        displayMetadata(techMd, root, 0);
    }

    static void indent(List<String> techMd, StringBuffer sb, int level)
    {
        for (int i = 0; i < level; i++)
        {
            sb.append("    ");
        }
    }

    static void displayMetadata(List<String> techMd, Node node, int level)
    {
        StringBuffer sb = new StringBuffer();
        // print open tag of element
        indent(techMd, sb, level);
        sb.append("<" + node.getNodeName());
        NamedNodeMap map = node.getAttributes();
        if (map != null)
        {
            // print attribute values
            int length = map.getLength();
            for (int i = 0; i < length; i++)
            {
                Node attr = map.item(i);
                sb.append(" " + attr.getNodeName() + "=\"" + attr.getNodeValue() + "\"");
            }
        }
        Node child = node.getFirstChild();
        if (child == null)
        {
            // no children, so close element and return
            sb.append("/>");
            techMd.add(sb.toString());
            sb.delete(0, sb.length());
            return;
        }
        // children, so close current tag
        sb.append(">");
        techMd.add(sb.toString());
        sb.delete(0, sb.length());
        while (child != null)
        {
            // print children recursively
            displayMetadata(techMd, child, level + 1);
            child = child.getNextSibling();
        }
        // print close tag of element
        indent(techMd, sb, level);
        sb.append("</" + node.getNodeName() + ">");
        techMd.add(sb.toString());
        sb.delete(0, sb.length());
    }

    public void setTechMd(List<String> md)
    {
        this.techMd = md;
    }

    public String save()
    {
        try
        {
            imageController.update(image);
            if (!editMetadataBean.edit())
            {
                BeanHelper.error("Error editing images");
            }
            BeanHelper.info("Images edited");
        }
        catch (Exception e)
        {
            e.getMessage();
        }
        return getNavigationString();
    }

    public String getPageUrl()
    {
        return navigation.getApplicationUrl() + "image/" + this.id;
    }

    public String select()
    {
        if (!selected)
        {
            sessionBean.getSelected().remove(image.getId());
        }
        else
        {
            sessionBean.getSelected().add(this.image.getId());
        }
        return "";
    }

    public String clearAll()
    {
        sessionBean.getSelected().clear();
        return "pretty:";
    }

    public CollectionImeji getCollection()
    {
        return collection;
    }

    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

    public List<ImageMetadata> getImgMetadata()
    {
        return new ArrayList<ImageMetadata>(image.getMetadata());
    }

    public void setImgMetadata(List<ImageMetadata> imgMetadata)
    {
        this.imgMetadata = imgMetadata;
    }

    public void setImage(Image image)
    {
        this.image = image;
    }

    public Image getImage()
    {
        return image;
    }

    public EditMetadataBean getEditMetadataBean()
    {
        return editMetadataBean;
    }

    public void setEditMetadataBean(EditMetadataBean editMetadataBean)
    {
        this.editMetadataBean = editMetadataBean;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public boolean getSelected()
    {
        if (sessionBean.getSelected().contains(image.getId()))
            selected = true;
        else
            selected = false;
        return selected;
    }

    public String getThumbnailImageUrlAsString()
    {
        return image.getThumbnailImageUrl().toString();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTab()
    {
        return tab;
    }

    public void setTab(String tab)
    {
        this.tab = tab.toUpperCase();
    }

    protected String getNavigationString()
    {
        return "pretty:viewImage";
    }

    public String getPrevious()
    {
        return previous;
    }

    public void setPrevious(String previous)
    {
        this.previous = previous;
    }

    public String getNext()
    {
        return next;
    }

    public void setNext(String next)
    {
        this.next = next;
    }

    public SessionBean getSessionBean()
    {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean)
    {
        this.sessionBean = sessionBean;
    }

    public String addToActiveAlbum()
    {
        AlbumBean activeAlbum = sessionBean.getActiveAlbum();
        AlbumController ac = new AlbumController(sessionBean.getUser());
        if (activeAlbum.getAlbum().getImages().contains(image.getId()))
        {
            BeanHelper.error("Image " + image.getFilename() + " already in active album!");
        }
        else
        {
            activeAlbum.getAlbum().getImages().add(image.getId());
            ac.update(activeAlbum.getAlbum());
            BeanHelper.info("Image " + image.getFilename() + " added to active album");
        }
        return "pretty:";
    }

    public boolean getIsInActiveAlbum()
    {
        if (sessionBean.getActiveAlbum() != null)
        {
            return sessionBean.getActiveAlbum().getAlbum().getImages().contains(image.getId());
        }
        return false;
    }

    public void selectedChanged(ValueChangeEvent event)
    {
        if (event.getNewValue().toString().equals("true") && !sessionBean.getSelected().contains(image.getId()))
        {
            setSelected(true);
            select();
        }
        else if (event.getNewValue().toString().equals("false") && sessionBean.getSelected().contains(image.getId()))
        {
            setSelected(false);
            select();
        }
    }
}

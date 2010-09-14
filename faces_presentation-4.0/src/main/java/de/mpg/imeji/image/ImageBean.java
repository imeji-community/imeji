package de.mpg.imeji.image;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.ComplexType.ComplexTypes;
import de.mpg.jena.vo.complextypes.ConePerson;

public class ImageBean
{
    private SessionBean sessionBean = null;
    private Image image;
    private String id = null;
    private URI imgUri; 
    private boolean selected;
    private ImageController imageController= null;
    private List<ImageMetadata> imgMetadata = null;
    private CollectionImeji  collection;
    private CollectionController collectionController;

	public ImageBean(Image img)
    {
        this.image = img;
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        imageController = new ImageController(sessionBean.getUser());
   

    }
    
    public ImageBean(){
    	image = new Image();
    	sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    	imageController = new ImageController(sessionBean.getUser());
    	collectionController = new CollectionController(sessionBean.getUser());
    	imgMetadata = new ArrayList<ImageMetadata>();
    }
    
    public void init() throws Exception{ 
    	imgUri = new URI("http://imeji.mpdl.mpg.de/image/" + id);
    	image = imageController.retrieve(imgUri);
    	collection = collectionController.retrieve(this.getImage().getCollection());
   }
    
    public CollectionImeji getCollection() {
		return collection;
	}

	public void setCollection(CollectionImeji collection) {
		this.collection = collection;
	}

	public List<ImageMetadata> getImgMetadata() {
		for(ImageMetadata im: image.getMetadata() ){
			
			
			if(im.getType().getEnumType().equals(ComplexTypes.CONE_AUTHOR)){
				System.err.println(((ConePerson)im.getType()).getPerson().getFamilyName());
				System.err.println(((ConePerson)im.getType()).getPerson().getAlternativeName());
				System.err.println(((ConePerson)im.getType()).getPerson().getGivenName());
				System.err.println(((ConePerson)im.getType()).getPerson().getIdentifier());

				
			}

		}
    	imgMetadata = new ArrayList<ImageMetadata>(image.getMetadata());
		return imgMetadata;
	}

	public void setImgMetadata(List<ImageMetadata> imgMetadata) {
		this.imgMetadata = imgMetadata;
	} 
    
    public URI getImgUri() {
		return imgUri;
	}

	public void setImgUri(URI imgUri) {
		this.imgUri = imgUri;
	}

	public void setImage(Image image)
    {
        this.image = image;
    }

    public Image getImage()
    {
        return image;
    }

    public void selectListener(ValueChangeEvent event)
    {
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {
            selected = Boolean.parseBoolean(event.getNewValue().toString());
        }
        if (!selected)
            sb.getSelected().remove(image.getId());
        else
            sb.getSelected().add(this.image.getId());
    }

    public void select(ActionEvent event)
    {
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (!selected)
            sb.getSelected().remove(image.getId());
        else
            sb.getSelected().add(this.image.getId());
    }

    /**
     * @return the selected
     */
    public boolean isSelected()
    {
        return selected;
    }

    /**
     * @param selected the selected to set
     */
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    public String getThumbnailImageUrlAsString()
    {
        return image.getThumbnailImageUrl().toString();
    }

    public String getId()
    {
        return id;
    }
    
    public void setId(String id){
    	this.id = id;
    }
}

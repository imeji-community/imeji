package de.mpg.imeji.image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.metadata.EditMetadataBean;
import de.mpg.imeji.metadata.MetadataBean;
import de.mpg.imeji.metadata.MetadataBean.MdField;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.util.ComplexTypeHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;

public class ImageBean
{
	public enum TabType{
		view, edit;

	}
	  
	private String tab;
	private SessionBean sessionBean = null;
    private Image image;
    private String id = null;
    private boolean selected;
    private ImageController imageController= null;
    private List<ImageMetadata> imgMetadata;
    private CollectionImeji  collection;
    private CollectionController collectionController;

    
	public ImageBean(Image img){
        this.image = img;
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        imageController = new ImageController(sessionBean.getUser());
        imgMetadata = new ArrayList<ImageMetadata>();
    }
    
    public ImageBean(){
    	image = new Image();
    	sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    	imageController = new ImageController(sessionBean.getUser());
    	collectionController = new CollectionController(sessionBean.getUser());
    	imgMetadata = new ArrayList<ImageMetadata>();
    }
      
    public void init() throws Exception{ 
    	image = imageController.retrieve(id);
    	collection = collectionController.retrieve(this.getImage().getCollection());
    	this.tab = TabType.view.toString();
    }
       
    public String save(){
        try{
            imageController.update(image);
        }
        catch (Exception e){
        	e.getMessage();
        }
        return getNavigationString();
    }
  
    public CollectionImeji getCollection() {
		return collection;
	}

	public void setCollection(CollectionImeji collection) {
		this.collection = collection;
	}
 
	public List<ImageMetadata> getImgMetadata() {
		return  new ArrayList<ImageMetadata>(image.getMetadata());
	}

	public void setImgMetadata(List<ImageMetadata> imgMetadata) {
		this.imgMetadata = imgMetadata;
	} 
       
	public void setImage(Image image){
        this.image = image;
    }
  
    public Image getImage(){
        return image;
    }

    public void selectListener(ValueChangeEvent event){
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

    public void select(ActionEvent event){
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (!selected)
            sb.getSelected().remove(image.getId());
        else
            sb.getSelected().add(this.image.getId());
    }

    /**
     * @return the selected
     */
    public boolean isSelected(){
        return selected;
    }
   
    /**
     * @param selected the selected to set
     */    
    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public String getThumbnailImageUrlAsString(){
        return image.getThumbnailImageUrl().toString();
    }

    public String getId(){
        return id;
    }
    
    public void setId(String id){
    	this.id = id;
    }
    

    public String getTab() {
    	System.err.println(tab);
		return tab;
	}
    
	public void setTab(String tab) {
			this.tab = tab;
	}
	
    protected String getNavigationString(){
    	if(getTab().equalsIgnoreCase("edit"))
    		return "pretty:editImage";
    	else
    		return "pretty:viewImage";
    }
}

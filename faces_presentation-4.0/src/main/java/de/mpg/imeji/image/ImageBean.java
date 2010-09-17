package de.mpg.imeji.image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.xalan.xsltc.compiler.sym;

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
		VIEW, EDIT;

	}
	  
	private String tab = null;
	private SessionBean sessionBean = null;
    private Image image;
    private String id = null;
    private boolean selected;
    private ImageController imageController= null;
    private List<ImageMetadata> imgMetadata;
    private CollectionImeji  collection;
    private CollectionController collectionController;
    private String previous = null;
    private String next = null;
    private boolean deSelected;

	public ImageBean(Image img){
        this.image = img;
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        imageController = new ImageController(sessionBean.getUser());
        imgMetadata = new ArrayList<ImageMetadata>();
        if(sessionBean.getSelected().contains(img.getId())){
    		setSelected(true);
    	}
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
    	setTab(TabType.VIEW.toString());
    	if(sessionBean.getSelected().contains(image.getId())){
    		setSelected(true);
    	}
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
        if (event.getNewValue() != null && event.getNewValue() != event.getOldValue())
        {  
            selected = Boolean.parseBoolean(event.getNewValue().toString());
        }
        if (!selected)
            sessionBean.getSelected().remove(image.getId());
        else
            sessionBean.getSelected().add(this.image.getId());
    }

    public void select(ActionEvent event){
        if (!selected)
        	sessionBean.getSelected().remove(image.getId());
        else
        	sessionBean.getSelected().add(this.image.getId());
    }
    
    public void deSelectedEvent(ValueChangeEvent event){
    	if(event.getNewValue() != null && event.getNewValue() != event.getOldValue() && Boolean.parseBoolean(event.getNewValue().toString())){
        	if(sessionBean.getSelected().contains(image.getId())){
        		sessionBean.getSelected().remove(image.getId());
        	}
    	}
    }
    
	public boolean getDeSelected() {
    	if(!(sessionBean.getSelected().contains(image.getId()))){
    		deSelected= true;
    	}else
    		deSelected= false;
		return deSelected;
	}
  
	public void setDeSelected(boolean deSelected) {
		this.deSelected= deSelected;
	}
   
    /**
     * @param selected the selected to set
     */    
    public void setSelected(boolean selected){
        this.selected = selected;
    }
        
    public boolean getSelected(){
    	return selected; 
    	
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
		return tab;
	}
    
	public void setTab(String tab) {
		this.tab = tab.toUpperCase();
	}
	
    protected String getNavigationString(){
    	if(getTab().equalsIgnoreCase("edit"))
    		return "pretty:editImage";
    	else
    		return "pretty:viewImage";
    }
    
	public String getPrevious() {
		return previous;
	}

	public void setPrevious(String previous) {
		this.previous = previous;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}
}

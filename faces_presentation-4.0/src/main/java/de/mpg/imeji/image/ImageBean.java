package de.mpg.imeji.image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import thewebsemantic.NotFoundException;
import de.mpg.imeji.album.AlbumBean;
import de.mpg.imeji.album.AlbumImagesBean;
import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.lang.MetadataLabels;
import de.mpg.imeji.metadata.SingleEditBean;
import de.mpg.imeji.metadata.extractors.BasicExtractor;
import de.mpg.imeji.metadata.util.MetadataHelper;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.jena.concurrency.locks.Locks;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.security.Security;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

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
    private CollectionImeji collection;
    private CollectionController collectionController;
    private List<String> techMd;
    private Navigation navigation;
    private MetadataProfile profile;
    private SingleEditBean edit;
    protected String prettyLink;
    private MetadataLabels labels;
    private SingleImageBrowse browse = null;

    public ImageBean(Image img) throws Exception
    {
        this.image = img;
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        prettyLink = "pretty:editImage";
        labels = (MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class);
        if (sessionBean.getSelected().contains(img.getId()))
        {
            setSelected(true);
        }
    }

    public ImageBean() throws Exception
    {
        image = new Image();
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        prettyLink = "pretty:editImage";
        labels = (MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class);
    }
    
    public String getInitPopup() throws Exception
    {
    	loadProfile();
    	labels.init(profile);
    	return "";
    } 

    public void init() throws Exception
    {
    	loadImage();
    	loadCollection();
        loadProfile();
        sortMetadataAccordingtoProfile();
        initBrowsing();
        
    	if (sessionBean.getSelected().contains(image.getId()))
        {
            setSelected(true);
        }
        
    	labels.init(profile);
    	
        edit = new SingleEditBean(image, profile, getPageUrl());
      
        cleanImageMetadata();
    }
    
    public void initBrowsing()
    {
    	browse = new SingleImageBrowse((ImagesBean) BeanHelper.getSessionBean(ImagesBean.class), image);
    }
    
    public void sortMetadataAccordingtoProfile()
    {
    	Collection<ImageMetadata> mdSorted = new ArrayList<ImageMetadata>();
    	for (Statement st : profile.getStatements())
    	{
    		for (ImageMetadata md : image.getMetadataSet().getMetadata())
    		{
    			if (st.getName().equals(md.getNamespace()))
    			{
    				mdSorted.add(md);
    			}
    		}
    	}
    	image.getMetadataSet().setMetadata(mdSorted);
    }
    
    public void loadImage()
    {
    	try 
        {
    		imageController = new ImageController(sessionBean.getUser());
         	if (id != null)	image = imageController.retrieve(id);
 		} 
    	catch (NotFoundException e) 
    	{
    		BeanHelper.error(sessionBean.getLabel("image") + " " + id + sessionBean.getLabel("not_found"));
		}
        catch (Exception e) 
 		{
 			BeanHelper.error(sessionBean.getMessage("error_image_load") + ": " + e );
 		}
    }
    
    public void loadCollection()
    {
    	try 
    	{
    		collectionController = new CollectionController(sessionBean.getUser());
    		collection = collectionController.retrieve(this.getImage().getCollection());
		} 
    	catch (Exception e) 
    	{
			BeanHelper.error(e.getMessage());
			collection = null;
		}
    }
    
    public void loadProfile()
    {
    	try 
    	{
    		profile = ProfileHelper.loadProfile(image);
		} 
    	catch (Exception e) 
    	{
			BeanHelper.error(sessionBean.getMessage("error_profile_load") + " " + image.getMetadataSet().getProfile() + "  " + sessionBean.getLabel("of") + " " + image.getId());
			BeanHelper.error(e.getMessage());
			profile = null;
		}
    }
    
    private void cleanImageMetadata()
	{
		for (int i=0; i < image.getMetadataSet().getMetadata().size(); i++)
		{
			if (MetadataHelper.isEmpty(((List<ImageMetadata>)image.getMetadataSet().getMetadata()).get(i)))
			{
				((List<ImageMetadata>)image.getMetadataSet().getMetadata()).remove(i);i--;
			}
		}
	}
    
    public String getInitLabels() throws Exception
    {
    	labels.init(profile);
    	return "";
    }

    public void initView() throws Exception
    {
    	if (image == null || image.getId() == null || !image.getId().toString().equals(ObjectHelper.getURI(Image.class, id).toString())) 
    	{
    		init();
    	}

        setTab(TabType.VIEW.toString());
    }

    public void initTechMd() throws Exception
    {
    	if (image == null || image.getId() == null || !image.getId().toString().equals(ObjectHelper.getURI(Image.class, id).toString())) 
    	{
    		this.init();
    	}
        setTab(TabType.TECHMD.toString());
    }

    public List<String> getTechMd() throws Exception
    {
    	techMd = BasicExtractor.extractTechMd(image);
    	return techMd;
    }

    public void setTechMd(List<String> md)
    {
        this.techMd = md;
    }

    public String getPageUrl()
    {
        return navigation.getApplicationUrl() + "image/" + this.id;
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

    public void setImage(Image image)
    {
        this.image = image;
    }

    public Image getImage()
    {
        return image;
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
    	sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (sessionBean.getSelected().contains(image.getId())) selected = true;
        else selected = false;
        return selected;
    }

    public String getThumbnailImageUrlAsString()
    {
        if (image.getThumbnailImageUrl() == null) return "/no_thumb";
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

    public String getNavigationString()
    {
        return "pretty:viewImage";
    }

    public SessionBean getSessionBean()
    {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean)
    {
        this.sessionBean = sessionBean;
    }

    public String addToActiveAlbum() throws Exception
    {
        AlbumBean activeAlbum = sessionBean.getActiveAlbum();
        AlbumController ac = new AlbumController(sessionBean.getUser());
        if (activeAlbum.getAlbum().getImages().contains(image.getId()))
        {
            BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("image") + " " + image.getFilename() + " " + ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getMessage("already_in_active_album"));       
        }
        else
        {
            activeAlbum.getAlbum().getImages().add(image.getId());
            ac.update(activeAlbum.getAlbum());
            BeanHelper.info(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("image") + " " + image.getFilename() + " " + ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getMessage("added_to_active_album"));       
        }
        return "";
    }
    
    public String removeFromAlbum() throws Exception
    {
    	AlbumImagesBean aib = (AlbumImagesBean) BeanHelper.getSessionBean(AlbumImagesBean.class);    	
        AlbumController ac = new AlbumController(sessionBean.getUser());
        aib.getAlbum().getAlbum().getImages().remove(image.getId());
        ac.update(aib.getAlbum().getAlbum());
        if(getIsInActiveAlbum()) sessionBean.getActiveAlbum().getAlbum().getImages().remove(image.getId());
        BeanHelper.info(sessionBean.getLabel("image") + " " + image.getFilename() + " " + sessionBean.getMessage("success_album_remove_from"));
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
    	sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);

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
    
    public String select()
    {
        if (!selected)
        {
        	((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getSelected().remove(image.getId());
        }
        else
        {
            ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getSelected().add(image.getId());
        }
        return "";
    }
    
    public MetadataProfile getProfile() {
		return profile;
	}

	public void setProfile(MetadataProfile profile) {
		this.profile = profile;
	}

	public List<SelectItem> getStatementMenu()
    {
    	List<SelectItem> statementMenu = new ArrayList<SelectItem>();
    	if (profile == null)
    	{
    		loadProfile();
    	}
    	for (Statement s : profile.getStatements())
	    {
    		statementMenu.add(new SelectItem(s.getName(), s.getLabels().iterator().next().toString()));
	    }
    	return statementMenu;
    }
	
	public SingleEditBean getEdit() 
	{
		return edit;
	}

	public void setEdit(SingleEditBean edit) 
	{
		this.edit = edit;
	}
	
	public boolean isLocked()
	{
		return Locks.isLocked(this.image.getId().toString(), sessionBean.getUser().getEmail());
	}

	public boolean isEditable() 
	{
		Security security = new Security();
		return security.check(OperationsType.UPDATE, sessionBean.getUser(), image);
	}
	
	public boolean isVisible() 
	{
		Security security = new Security();
		return security.check(OperationsType.READ, sessionBean.getUser(), image);
	}
	
	public boolean isDeletable() 
	{
		Security security = new Security();
		return security.check(OperationsType.DELETE, sessionBean.getUser(), image);
	}

	public SingleImageBrowse getBrowse() {
		return browse;
	}

	public void setBrowse(SingleImageBrowse browse) {
		this.browse = browse;
	}
}

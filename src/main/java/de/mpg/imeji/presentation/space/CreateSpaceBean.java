package de.mpg.imeji.presentation.space;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.SpaceController;
import de.mpg.imeji.logic.controller.exceptions.TypeNotAllowedException;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Space;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.upload.IngestImage;
import de.mpg.imeji.presentation.util.BeanHelper;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ManagedBean(name ="CreateSpaceBean")
@ViewScoped
public class CreateSpaceBean implements Serializable{
	private static final long serialVersionUID = -5469506610392004531L;
	private Space space;
	//private String slug;
	//private Part logoFile;
	private SessionBean sessionBean;
	private Navigation navigation;
    private List<CollectionImeji> collections;

    private List<String> selectedCollections = new ArrayList<String>();
    
    private IngestImage ingestImage;
    
    public CreateSpaceBean() {
    	space = new Space();
    	collections = new ArrayList<CollectionImeji>();
    	sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
    	navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
    	init();
    }
    
    public void init() {
    	CollectionController cc = new CollectionController();
    	String q = "";  
    	User user = sessionBean.getUser();
    	try {
    		if (!StringHelper.isNullOrEmptyTrim(space.getIdString())) {
    			collections = cc.retrieveCollections(user, q, space.getIdString());
    		}
    		else
    		{
    			collections.addAll(cc.retrieveCollectionsNotInSpace(user));
    		}
    		Collections.sort(collections, new Comparator<CollectionImeji>(){
				@Override
				public int compare(CollectionImeji coll1, CollectionImeji coll2) {
					return coll1.getMetadata().getTitle().compareToIgnoreCase(coll2.getMetadata().getTitle());
				}    			
    		});
		} catch (ImejiException e) {
			BeanHelper.info(sessionBean.getMessage("could_not_load_collections_for_space"));
		}
    	if(UrlHelper.getParameterBoolean("start")){
    		upload();			
    	}
    	
    }
      
	public List<SelectItem> getCollectionItems() {
		List<SelectItem> itemList = new ArrayList<SelectItem>();
		for(CollectionImeji ci : collections)
			itemList.add(new SelectItem(ci.getIdString(), ci.getMetadata().getTitle()));
		return itemList;
	}
    
    public String save() throws Exception {
    	if(createdSpace()) {
	    		sessionBean.setSpaceId(space.getSlug());
	    		//Go to the home URL of the Space
	    		FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getHomeUrl());
	    	}
		   	
   		return "";
    }
    
    public boolean createdSpace() throws ImejiException, IOException 
    {
    	if (valid()) {
    	SpaceController spaceController = new SpaceController();
   	   	File spaceLogoFile = (sessionBean.getSpaceLogoIngestImage() != null) ? sessionBean.getSpaceLogoIngestImage().getFile():null;
    	URI uri = spaceController.create(space, getSelectedCollections(), spaceLogoFile, sessionBean.getUser());
    	setSpace(spaceController.retrieve(uri, sessionBean.getUser()));
	    	//reset the Session bean and this local, as anyway it will navigate back to the home page
	    	//Note: check how it will work with eDit! Edit bean should be implemented
    	setIngestImage(null);
        BeanHelper.info(sessionBean.getMessage("success_space_create"));
    	return true;
    	}
    	return false;
    }
	
	public Space getSpace() {
		return space;
	}
	public void setSpace(Space space) {
		this.space = space;
	}
	
	public List<CollectionImeji> getCollections() {
		return collections;
	}
	public void setCollections(List<CollectionImeji> collections) {
		this.collections = collections;
	}

//	public Part getLogoFile() {
//		return logoFile;
//	}
//
//	public void setLogoFile(Part logoFile) {
//		this.logoFile = logoFile;
//	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

//	public String getSlug() {
//		return slug;
//	}
//
//	public void setSlug(String slug) {
//		this.slug = slug;
//	}

	public List<String> getSelectedCollections() {
		return selectedCollections;
	}

	public void setSelectedCollections(List<String> selectedCollections) {
		this.selectedCollections = selectedCollections;
	}
	
	
	
	public void upload() {  
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		try {
			setIngestImage(getUploadedIngestFile(request));
		} catch (FileUploadException|TypeNotAllowedException e) {
			BeanHelper.error("Could not upload the image " + e.getMessage());
		}

    }
	
	
	private IngestImage getUploadedIngestFile(HttpServletRequest request) throws FileUploadException, TypeNotAllowedException{
		File tmp = null;
		boolean isMultipart=ServletFileUpload.isMultipartContent(request);
		IngestImage ii = new IngestImage();
		if (isMultipart) {
			ServletFileUpload upload=new ServletFileUpload();
			try {    
				FileItemIterator iter = upload.getItemIterator(request);

				while (iter.hasNext()) {  
					FileItemStream fis = iter.next();
					if(fis.getName() != null && FilenameUtils.getExtension(fis.getName()).matches("ini|exe|sh|bin"))
					{
	                	throw new TypeNotAllowedException(sessionBean.getMessage("Logo_single_upload_invalid_content_format"));
					}

					InputStream in = fis.openStream();
					tmp = File.createTempFile("spacelogo", "." + FilenameUtils.getExtension(fis.getName()));
					FileOutputStream fos = new FileOutputStream(tmp);
					if(fis.getName() != null)
						ii.setName(fis.getName());
					if(!fis.isFormField())
					{
						try {
							IOUtils.copy(in, fos);
						}
						catch (Exception e) {
							BeanHelper.error("Could not process uploaded Logo file streams");
						}
						
					}
					in.close();
					fos.close();
				}
				ii.setFile(tmp);
				
			} catch (IOException | FileUploadException e) {
				ii.setFile(null);
				BeanHelper.error("Could not process uploaded Logo file");
			}
		}
		return ii;
	}   

	
	public void setIngestImage (IngestImage im)
	{
		this.ingestImage = im;
		sessionBean.setSpaceLogoIngestImage(im);
	}
	
	
	public IngestImage getIngestImage ()
	{
		return this.ingestImage;
	}
	
	
	public boolean valid(){
			SpaceController cc = new SpaceController();
			try {
				cc.validate(space, sessionBean.getUser());
				return true;
			} catch (Exception e) 
			{
				BeanHelper.error(sessionBean.getMessage(e.getMessage()));
				return false;
			}

		}

}

package de.mpg.imeji.presentation.space;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.SpaceController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Space;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.rest.process.CommonUtils;

@ManagedBean(name ="CreateSpaceBean")
@ViewScoped
public class CreateSpaceBean implements Serializable{


	private static final long serialVersionUID = -5469506610392004531L;
	private Space space;
	private String slug;
	private Part logoFile;
	private SessionBean sessionBean;
	private Navigation navigation;
    private List<CollectionImeji> collections;

    private List<String> selectedCollections = new ArrayList<String>();
    
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
    		if(space.getIdString() != null)
    			collections = cc.retrieveCollections(user, q, space.getIdString());
   			collections.addAll(cc.retrieveCollectionsNotInSpace(user));
		} catch (ImejiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
      
	public List<SelectItem> getCollectionItems() {
		List<SelectItem> itemList = new ArrayList<SelectItem>();
		for(CollectionImeji ci : collections)
			itemList.add(new SelectItem(ci.getIdString(), ci.getMetadata().getTitle()));
		return itemList;
	}
    
    public String save() throws Exception {
    	if(createdSpace())
    		FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getSpacePath()+ getSpace().getSlug());
    	return "";
    }
    
    public boolean createdSpace(){
    	try {
    		URI slugTest = new URI(slug);
			space.setSlug(slug);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    	if(logoFile != null)
    	{
    		try {
				InputStream inputStream = logoFile.getInputStream();
	    		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	    		File tmpPath = (File)servletContext.getAttribute(CommonUtils.JAVAX_SERVLET_CONTEXT_TEMPDIR);
	    		File tmpFile = File.createTempFile("spaceLogo", "." + Files.getFileExtension(logoFile.getName()), tmpPath);
	    		ByteStreams.copy(inputStream, new FileOutputStream(tmpFile));
	    		//...Upload To Server
	    		
	    		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	}
    	
    	SpaceController spaceController = new SpaceController();
    	try {
			spaceController.validate(space, sessionBean.getUser());
	    	URI uri = spaceController.create(space, sessionBean.getUser());        
	    	setSpace(spaceController.retrieve(uri, sessionBean.getUser()));
		} catch (ImejiException e) {
			BeanHelper.error(sessionBean.getMessage(e.getMessage()));
			return false;
		}
        
        /*
         * Update User Grant
        UserController uc = new UserController(user);
        uc.update(sessionBean.getUser(), user);
        */
        
        BeanHelper.info(sessionBean.getMessage("success_space_create"));
        return true;
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

	public Part getLogoFile() {
		return logoFile;
	}

	public void setLogoFile(Part logoFile) {
		this.logoFile = logoFile;
	}

	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public List<String> getSelectedCollections() {
		return selectedCollections;
	}

	public void setSelectedCollections(List<String> selectedCollections) {
		this.selectedCollections = selectedCollections;
	}
	
	
	
	
	
	
	
	
	
	

}

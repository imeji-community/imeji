package de.mpg.imeji.presentation.space;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.SpaceController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Space;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

@ManagedBean(name ="CreateSpaceBean")
@ViewScoped
public class CreateSpaceBean implements Serializable{


	private static final long serialVersionUID = -5469506610392004531L;
	private Space space;
	private String slug;
	private Part logoFile;
	private SessionBean sessionBean;
	private Navigation navigation;
    private List<CollectionImeji> collections = new ArrayList<CollectionImeji>();
    private List<String> selectedCollections = new ArrayList<String>();
    
    public CreateSpaceBean() {
    	space = new Space();
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
    
    public String save() throws Exception {
    	if(createdSpace())
    		FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getSpacePath()+ getSpace().getSlug());
    	return "";
    }
    
    public boolean createdSpace(){
    	try {
			space.setSlug(new URI(slug));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    	if(logoFile != null)
    	{
    		InputStream inputStream = logoFile.getInputStream();
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
	
	
	
	
	
	
	
	
	
	

}

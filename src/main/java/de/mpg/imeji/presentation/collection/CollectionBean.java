/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.CollectionController.MetadataProfileCreationMethod;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.search.SPARQLSearch;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.presentation.beans.ContainerBean;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.mdProfile.wrapper.StatementWrapper;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import org.apache.log4j.Logger;

import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.logic.notification.CommonMessages.getSuccessCollectionDeleteMessage;

/**
 * Abstract bean for all collection beans
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class CollectionBean extends ContainerBean {
	private static final long serialVersionUID = -3071769388574710503L;

	public enum TabType {
		COLLECTION, PROFILE, HOME, UTIL;
	}

	private static Logger logger = Logger.getLogger(CollectionBean.class);
	private TabType tab = TabType.HOME;
	
	protected SessionBean sessionBean;

	protected Navigation navigation;
	private CollectionImeji collection;
	private MetadataProfile profile = null;
	private MetadataProfile profileTemplate;
	
	private String id;
	private String profileId;
	private boolean selected;
	
	private List<SelectItem> profileItems = new ArrayList<SelectItem>();
	private String selectedProfileItem;
  
    private boolean useMDProfileTemplate = true;
	private SelectItem[] profileCreationMethod = {new SelectItem("Reference", "Reference"), new SelectItem("Copy", "Copy")};
	private String selectedCreationMethod = profileCreationMethod[0].getValue().toString();

	private List<StatementWrapper> statementWrappers = new ArrayList<StatementWrapper>();
    private Map<URI, Integer> levels;


    private boolean sendEmailNotification = false;
    
    private boolean collectionCreateMode = true;
    
    private boolean profileSelectMode = false;

	private static final int MARGIN_PIXELS_FOR_STATEMENT_CHILD = 5;
    	
	/**
	 * New default {@link CollectionBean}
	 */
	public CollectionBean() {
		collection = new CollectionImeji();
		sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
	}

	/**
	 * Validate whether the {@link CollectionImeji} values are correct
	 * 
	 * @return
	 */
	public boolean valid() {
		CollectionController cc = new CollectionController();
		ProfileController pc = new ProfileController();
		try {
			cc.validateCollection(collection, sessionBean.getUser());
			if(useMDProfileTemplate && "".equals(profile.getTitle())){
				pc.validateProfile(profile, sessionBean.getUser());
				return false;
			}
			return true;
		} catch (ImejiException e) 
		{
			BeanHelper.error(sessionBean.getMessage(e.getMessage()));
			return false;
		}

	}
	
    /**
     * Load the templates (i.e. the {@link MetadataProfile} that can be used by the {@link User}), and add it the the
     * menu (sorted by name)
     */
    public void loadProfiles()
    {    
    	profileItems.clear();
        try
        {    
            ProfileController pc = new ProfileController();
            List<MetadataProfile> profiles = pc.search(sessionBean.getUser(), sessionBean.getSpaceId());
            String profileTitle="";
        	useMDProfileTemplate = false;
            MetadataProfile defaultMetadataProfile = pc.retrieveDefaultProfile();
            //Add default Metadata Profile as first one
            if (defaultMetadataProfile != null ) {
            	useMDProfileTemplate = true;
            	profileItems.add(new SelectItem(defaultMetadataProfile.getIdString(), defaultMetadataProfile.getTitle()));
            }

            for (MetadataProfile mdp : profiles)
            {
            	if ( mdp.getStatements().size() > 0) {
            		profileTitle=isNullOrEmpty(mdp.getTitle())?
            				(mdp.getIdString()+" - "+ "No Title provided") :
            				mdp.getTitle();
            		if (defaultMetadataProfile == null || !mdp.getIdString().equals(defaultMetadataProfile.getIdString())) {
	            		profileItems.add(new SelectItem(mdp.getIdString(), profileTitle));
	            		if (!useMDProfileTemplate ) 
	            				useMDProfileTemplate = true;
            		}
            	}  
            }            
            selectedProfileItem = (String) profileItems.get(0).getValue();
            // do this only in creation mode, if it is editing collection mode, keep the profile of the old collection
            // the update will be done when the collection is saved again
            if (isCollectionCreateMode()) {
	            this.profile = new MetadataProfile();
	            profile.setTitle(profiles.get(0).getTitle());
            }
	        this.profileTemplate = pc.retrieve(selectedProfileItem, sessionBean.getUser());
			initStatementWrappers(this.profileTemplate);

        }
        catch (Exception e)
        {
            BeanHelper.info(sessionBean.getMessage("error_profile_template_load"));
        }
    }
    
    
    public void profileChangeListener(AjaxBehaviorEvent event) throws Exception
    {
		if(!"".equals(selectedProfileItem))
		{  
			//change Listener should not automatically change the old profile of the collection in Edit Mode, as there is already a profile 
			//automatically created in any case by the collection controller during the collection creation.
			//this is to be done by the Edit collection bean and the save existing collection
			ProfileController pc = new ProfileController();
			if (isCollectionCreateMode()) {
		        MetadataProfile mProfile = pc.retrieve(selectedProfileItem, sessionBean.getUser());
		        setProfile(mProfile);
				this.profile.setTitle(profileTemplate.getTitle());
			}
		
			this.profileTemplate = pc.retrieve(selectedProfileItem, sessionBean.getUser());
			initStatementWrappers(this.profileTemplate);

		}
    }
    
    protected void initStatementWrappers(MetadataProfile mdp)
    {  
    	statementWrappers.clear();
        levels = new HashMap<URI, Integer>();
        for (Statement st : mdp.getStatements())
        {
        	statementWrappers.add(new StatementWrapper(st, mdp.getId(), getLevel(st)));
        }
    }
    
	
    protected int getLevel(Statement st)
    {
        if (!levels.containsKey(st.getId()))
        {
            if (st.getParent() != null && levels.get(st.getParent()) != null)
            {
                levels.put(st.getId(), (levels.get(st.getParent()) + MARGIN_PIXELS_FOR_STATEMENT_CHILD));
            }
            else
            {
                levels.put(st.getId(), 0);
            }
        }
        return levels.get(st.getId());
    }

    @Override
	protected String getErrorMessageNoAuthor() {
		return "error_collection_need_one_author";
	}

	/**
	 * Listener for the discard comment
	 * 
	 * @param event
	 */
	public void discardCommentListener(ValueChangeEvent event) {
		if (event.getNewValue() != null
				&& event.getNewValue().toString().trim().length() > 0) {
			getContainer().setDiscardComment(
					event.getNewValue().toString().trim());
		}
	}

	/**
	 * getter
	 * 
	 * @return the tab
	 */
	public TabType getTab() {
		if (UrlHelper.getParameterValue("tab") != null) {
			tab = TabType.valueOf(UrlHelper.getParameterValue("tab")
					.toUpperCase());
		}
		return tab;
	}

	/**
	 * setter
	 * 
	 * @param the
	 *            tab to set
	 */
	public void setTab(TabType tab) {
		this.tab = tab;
	}

	/**
	 * @return the collection
	 */
	public CollectionImeji getCollection() {
		return collection;
	}

	/**
	 * @param collection
	 *            the collection to set
	 */
	public void setCollection(CollectionImeji collection) {
		this.collection = collection;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the selected
	 */
	public boolean getSelected() {
		if (sessionBean.getSelectedCollections().contains(collection.getId()))
			selected = true;
		else
			selected = false;
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		if (selected) {
			if (!(sessionBean.getSelectedCollections().contains(collection.getId())))
				sessionBean.getSelectedCollections().add(collection.getId());
		} else
			sessionBean.getSelectedCollections().remove(collection.getId());
		this.selected = selected;
	}

	/**
	 * release the {@link CollectionImeji}
	 * 
	 * @return
	 */
	public String release() {
		CollectionController cc = new CollectionController();
		try {
			cc.release(collection, sessionBean.getUser());
			BeanHelper.info(sessionBean.getMessage("success_collection_release"));
		} catch (Exception e) {
			BeanHelper
					.error(sessionBean.getMessage("error_collection_release"));
			BeanHelper.error(e.getMessage());
			logger.error("Error during collection release", e);
		}
		return "pretty:";
	}

	/**
	 * Delete the {@link CollectionImeji}
	 * 
	 * @return
	 */
	public String delete() {
		CollectionController cc = new CollectionController();
		try {
			cc.delete(collection, sessionBean.getUser());
			BeanHelper.info(getSuccessCollectionDeleteMessage(this.collection.getMetadata().getTitle(), sessionBean));
		} catch (Exception e) {
			BeanHelper.error(sessionBean.getMessage("error_collection_delete"));
			logger.error("Error delete collection", e);
		}
		return sessionBean.getPrettySpacePage("pretty:collections");
	}

	/**
	 * Discard the {@link CollectionImeji} of this {@link CollectionBean}
	 * 
	 * @return
	 * @throws Exception
	 */
	public String withdraw() throws Exception {
		CollectionController cc = new CollectionController();
		try {
			cc.withdraw(collection, sessionBean.getUser());
			BeanHelper.info(sessionBean.getMessage("success_collection_withdraw"));
		} catch (Exception e) {
			BeanHelper.error(sessionBean.getMessage("error_collection_withdraw"));
			BeanHelper.error(e.getMessage());
			logger.error("Error discarding collection:", e);
		}
		return "pretty:";
	}

	/**
	 * getter
	 * 
	 * @return
	 */
	public MetadataProfile getProfile() {
		return profile;
	}

	/**
	 * setter
	 * 
	 * @param profile
	 */
	public void setProfile(MetadataProfile profile) {
		this.profile = profile;
	}

	

	public MetadataProfile getProfileTemplate() {
		return profileTemplate;
	}

	public void setProfileTemplate(MetadataProfile profileTemplate) {
		this.profileTemplate = profileTemplate;
	}

	/**
	 * getter
	 * 
	 * @return
	 */
	public String getProfileId() {
		return profileId;
	}

	/**
	 * setter
	 * 
	 * @param profileId
	 */
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getPageUrl() {
		return navigation.getCollectionUrl() + id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.imeji.presentation.beans.ContainerBean#getType()
	 */
	@Override
	public String getType() {
		return CONTAINER_TYPE.COLLECTION.name();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.imeji.presentation.beans.ContainerBean#getContainer()
	 */
	@Override
	public Container getContainer() {
		return collection;
	}

	public String getDiscardComment() {
		return this.getContainer().getDiscardComment();
	}

	public void setDiscardComment(String comment) {
		this.getContainer().setDiscardComment(comment);
	}
	

    public List<SelectItem> getProfileItems() {
		return profileItems;
	}

	public void setProfileItems(List<SelectItem> profileItems) {
		this.profileItems = profileItems;
	}

	
	public String getSelectedProfileItem() {
		return selectedProfileItem;
	}

	public void setSelectedProfileItem(String selectedProfileItem) {
		this.selectedProfileItem = selectedProfileItem;
	}

	public boolean isUseMDProfileTemplate() {
		return useMDProfileTemplate;
	}

	public void setUseMDProfileTemplate(boolean useMDProfileTemplate) {
		this.useMDProfileTemplate = useMDProfileTemplate;
	}

	public List<StatementWrapper> getStatementWrappers() {
		return statementWrappers;
	}

	public void setStatementWrappers(List<StatementWrapper> statementWrappers) {
		this.statementWrappers = statementWrappers;
	}

    public boolean isSendEmailNotification() {

        return sendEmailNotification;

    }

    public void setSendEmailNotification(boolean sendEmailNotification) {
        this.sendEmailNotification = sendEmailNotification;
        //check if id already set
        if (!isNullOrEmpty(id)) {
            User user = sessionBean.getUser();
            if (sendEmailNotification) {
                user.addObservedCollection(id);
            } else {
                user.removeObservedCollection(id);
            }
        }

    }
    
	public SelectItem[] getProfileCreationMethod() {
		return profileCreationMethod;
	}

	public String getSelectedCreationMethod() {
		return selectedCreationMethod;
	}

	public void setSelectedCreationMethod(String selectedCreationMethod) {
		this.selectedCreationMethod = selectedCreationMethod;
	}
	
	public boolean isCopyProfileMethod() {
		CollectionController cc= new CollectionController();
		if (MetadataProfileCreationMethod.COPY.equals( cc.getProfileCreationMethod(getSelectedCreationMethod()))) {
			return true;
		}
		return false;
	}
	
	public boolean isCollectionCreateMode() {
			return collectionCreateMode;
	}

	public void setCollectionCreateMode(boolean collectionCreateMode) {
			this.collectionCreateMode = collectionCreateMode;
	}
	
    public boolean isProfileSelectMode() {
		return profileSelectMode;
	}

	public void setProfileSelectMode(boolean profileSelectMode) {
		this.profileSelectMode = profileSelectMode;
	}
	
    /**
     * Load the templates (i.e. the {@link MetadataProfile} that can be used by the {@link User}), and add it the the
     * menu (sorted by name)
     */
    public boolean isHasProfiles()
    {    
          ProfileController pc = new ProfileController();
          try {
              return (pc.search(sessionBean.getUser(), sessionBean.getSpaceId()).size()>0);        	  
          }
          catch (ImejiException e) {
        	  return false;
          }

    }
    
}

/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ManagedProperty;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.ContainerBean;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ObjectCachedLoader;
import de.mpg.imeji.presentation.util.UrlHelper;

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
	private MetadataProfile profile;
	private String id;
	private String profileId;
	private boolean selected;
	
	private String template;
    private List<SelectItem> profilesMenu = new ArrayList<SelectItem>();
	
    /**
     * Listener for the template value
     * 
     * @param event
     * @throws Exception
     */
    public void templateListener(ValueChangeEvent event) throws Exception
    {
        if (event != null && event.getNewValue() != event.getOldValue())
        {
            this.template = event.getNewValue().toString();
            MetadataProfile tp = ObjectCachedLoader.loadProfile(URI.create(this.template));
            if (tp.getStatements().isEmpty())
                profile.getStatements().add(ImejiFactory.newStatement());
            else
                profile.setStatements(tp.clone().getStatements());
//            collectionSession.setProfile(profile);
//            initStatementWrappers(profile);
        }
    }
    
    /**
     * Load the templates (i.e. the {@link MetadataProfile} that can be used by the {@link User}), and add it the the
     * menu (sorted by name)
     */
    public void loadtemplates()
    {
        profilesMenu = new ArrayList<SelectItem>();
        try
        {
            ProfileController pc = new ProfileController();
            for (MetadataProfile mdp : pc.search(sessionBean.getUser()))
            {
                if (!mdp.getId().toString().equals(profile.getId().toString()) && !mdp.getStatements().isEmpty())
                {
                    profilesMenu.add(new SelectItem(mdp.getId().toString(), mdp.getTitle()));
                }
            }
            // sort profilesMenu
            Collections.sort(profilesMenu, new profilesLabelComparator());
            // add title to first position
            profilesMenu.add(0, new SelectItem(null, sessionBean.getLabel("profile_select_template")));
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_profile_template_load"));
        }
    }
    
    /**
     * Comparator of {@link MetadataProfile} names, to sort a {@link List} of {@link MetadataProfile} according to their
     * name
     * 
     * @author saquet (initial creation)
     * @author $Author$ (last modification)
     * @version $Revision$ $LastChangedDate$
     */
    static class profilesLabelComparator implements Comparator<Object>
    {
        @Override
        public int compare(Object o1, Object o2)
        {
            SelectItem profile1 = (SelectItem)o1;
            SelectItem profile2 = (SelectItem)o2;
            String profile1Label = profile1.getLabel();
            String profile1Labe2 = profile2.getLabel();
            return profile1Label.compareTo(profile1Labe2);
        }
    }
    
    public List<SelectItem> getProfilesMenu()
    {
        return profilesMenu;
    }

    public void setProfilesMenu(List<SelectItem> profilesMenu)
    {
        this.profilesMenu = profilesMenu;
    }
    
    public String getTemplate()
    {
        return template;
    }

    public void setTemplate(String template)
    {
        this.template = template;
    }
	

	/**
	 * New default {@link CollectionBean}
	 */
	public CollectionBean() {
		collection = new CollectionImeji();
		sessionBean = (SessionBean) BeanHelper
				.getSessionBean(SessionBean.class);
		navigation = (Navigation) BeanHelper
				.getApplicationBean(Navigation.class);
	}

	/**
	 * Validate whether the {@link CollectionImeji} values are correct
	 * 
	 * @return
	 */
	public boolean valid() {
		if (collection.getMetadata().getTitle() == null
				|| "".equals(collection.getMetadata().getTitle())) {
			BeanHelper.error(sessionBean
					.getMessage("error_collection_need_title"));
			return false;
		}
		List<Person> pers = new ArrayList<Person>();
		for (Person c : collection.getMetadata().getPersons()) {
			List<Organization> orgs = new ArrayList<Organization>();
			for (Organization o : c.getOrganizations()) {
				if (!"".equals(o.getName())) {
					orgs.add(o);
				}
			}
			if (!"".equals(c.getFamilyName())) {
				if (orgs.size() > 0) {
					c.setOrganizations(orgs);
					pers.add(c);
				} else {
					BeanHelper.error(sessionBean
							.getMessage("error_author_need_one_organization"));
					return false;
				}
			} else {
				BeanHelper.error(sessionBean
						.getMessage("error_author_need_one_family_name"));
				return false;
			}
		}
		if (pers.size() == 0) {
			BeanHelper.error(sessionBean
					.getMessage("error_collection_need_one_author"));
			return false;
		}
		collection.getMetadata().setPersons(pers);
		return true;
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
			if (!(sessionBean.getSelectedCollections().contains(collection
					.getId())))
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
			BeanHelper.info(sessionBean
					.getMessage("success_collection_release"));
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
			// BeanHelper.info(sessionBean.getMessage("success_collection_delete"));
			BeanHelper.info(sessionBean.getMessage("success_collection_delete")
					.replace("XXX_collectionName_XXX",
							this.collection.getMetadata().getTitle()));
		} catch (Exception e) {
			BeanHelper.error(sessionBean.getMessage("error_collection_delete"));
			logger.error("Error delete collection", e);
		}
		return "pretty:collections";
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
			BeanHelper.info(sessionBean
					.getMessage("success_collection_withdraw"));
		} catch (Exception e) {
			BeanHelper.error(sessionBean
					.getMessage("error_collection_withdraw"));
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
}

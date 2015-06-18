/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.VocabularyHelper;

/**
 * Java Bean for the create Collection Page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "CreateCollectionBean")
@ViewScoped
public class CreateCollectionBean extends CollectionBean {
	private static final long serialVersionUID = 1257698224590957642L;
	private VocabularyHelper vocabularyHelper;

	/**
	 * Bean Constructor
	 */
	public CreateCollectionBean() {
		initialize();
	}

	/**
	 * Method called when paged is loaded (defined in pretty-config.xml)
	 */
	public void initialize() {
		setCollectionCreateMode(true);
		setCollection(ImejiFactory.newCollection());
		((List<Person>) getCollection().getMetadata().getPersons()).set(0,
				sessionBean.getUser().getPerson().clone());
		vocabularyHelper = new VocabularyHelper();
		loadProfiles();
		if (getProfileItems().size() == 0) {
			setUseMDProfileTemplate(false);
		}

	}

	/**
	 * Method for save button. Create the {@link CollectionImeji} according to
	 * the form
	 * 
	 * @return
	 * @throws Exception
	 */
	public String save() throws Exception {
		if (createdCollection())
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							navigation.getCollectionUrl()
									+ getCollection().getIdString());
		return "";
	}

	/**
	 * Method for save&editProfile button. Create the {@link CollectionImeji}
	 * according to the form
	 * 
	 * @return
	 * @throws Exception
	 */
	public String saveAndEditProfile() throws Exception {
		if (createdCollection())
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							navigation.getProfileUrl()
									+ extractIDFromURI(getCollection()
											.getProfile())
									+ "/edit?init=1&col="
									+ getCollection().getIdString());
		return "";
	}

	public boolean createdCollection() throws ImejiException,
			URISyntaxException {
		try {
			CollectionController collectionController = new CollectionController();
			int pos = 0;
			// Set the position of the persons and organizations (used for the sorting later)
			for (Person p : getCollection().getMetadata().getPersons()) {
				p.setPos(pos);
				pos++;
				int pos2 = 0;
				for (Organization o : p.getOrganizations()) {
					o.setPos(pos2);
					pos2++;
				}
			}
			User user = sessionBean.getUser();

			MetadataProfile whichProfile = isUseMDProfileTemplate() ? getProfileTemplate()
					: null;
			// feature below will always create a collection with a new metadata
			// profile copied (cloned)
			// if there is no metadata profile template selected, then it will
			// create a new metadata profile
			URI id = collectionController
					.create(getCollection(),
							whichProfile,
							user,
							collectionController
									.getProfileCreationMethod(getSelectedCreationMethod()),
							sessionBean.getSelectedSpaceString());
			setCollection(collectionController.retrieve(id, user));
			setId(ObjectHelper.getId(id));

			// Setting user email notification for the collection downloads
			setSendEmailNotification(isSendEmailNotification());
			UserController uc = new UserController(user);
			uc.update(user, user);

			BeanHelper
					.info(sessionBean.getMessage("success_collection_create"));

			return true;
		} catch (UnprocessableError e) {
			BeanHelper.error(sessionBean.getMessage(e.getMessage()));
			getCollection().setId(null);
			return false;
		}
	}

	public static String extractIDFromURI(URI uri) {
		return uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
	}

	/**
	 * Return the link for the Cancel button
	 * 
	 * @return
	 */
	public String getCancel() {
		return navigation.getCollectionsUrl() + "?q=";
	}

	@Override
	protected String getNavigationString() {
		return sessionBean.getPrettySpacePage("pretty:createCollection");
	}

	public String getVocabularyLabel(URI id) {
		for (SelectItem item : vocabularyHelper.getVocabularies()) {

			if (id.toString().equals(item.getValue().toString())) {
				return item.getLabel();
			}
		}
		return "";
	}

}

/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;

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
		setCollection(ImejiFactory.newCollection());
		((List<Person>) getCollection().getMetadata().getPersons()).set(0,
				sessionBean.getUser().getPerson().clone());
		// if (UrlHelper.getParameterBoolean("reset")) {
		// setCollection(ImejiFactory.newCollection());
		// ((List<Person>) getCollection().getMetadata().getPersons()).set(0,
		// sessionBean.getUser().getPerson().clone());
		// }
	}

	/**
	 * Method for save button. Create the {@link CollectionImeji} according to
	 * the form
	 * 
	 * @return
	 * @throws Exception
	 */
	public String save() throws Exception {
		if (valid()) {
			// Create collection
			CollectionController collectionController = new CollectionController();
	        int pos = 0;
	        // Set the position of the statement (used for the sorting later)
	        for (Person p : getCollection().getMetadata().getPersons())
	        {
	            p.setPos(pos);
	            pos++;
	            int pos2 = 0;
	            for(Organization o : p.getOrganizations()){
	            	o.setPos(pos2);
	            	pos2++;	            	
	            }
	        }
			collectionController.create(getCollection(), null,
					sessionBean.getUser());
			BeanHelper
					.info(sessionBean.getMessage("success_collection_create"));
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.redirect(
							navigation.getCollectionUrl()
									+ getCollection().getIdString());
			return "";
		} else
			return "";
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
		return "pretty:createCollection";
	}
}

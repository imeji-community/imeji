package de.mpg.imeji.presentation.user;

import java.util.List;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.presentation.beans.ContainerBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;

/**
 * The JSF Composite for a {@link Person}
 * 
 * @author saquet
 * 
 */
@ManagedBean(name = "PersonBean")
@ViewScoped
public class PersonBean {
	private SessionBean sb;

	private String personURI;
	private String orgaURI;

	public PersonBean() {
		sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	}

	/**
	 * Change the person
	 * 
	 * @return
	 */
	public String changePerson(Object bean, int position) {
		Person person = loadPerson(personURI);
		if (bean instanceof UserCreationBean) {
			((UserCreationBean) bean).getUser().setPerson(person.clone());
		} else if (bean instanceof ContainerBean) {
			List<Person> l = (List<Person>) ((ContainerBean) bean)
					.getContainer().getMetadata().getPersons();
			l.set(position, person.clone());
		} else if (bean instanceof UserBean) {
			((UserBean) bean).getUser().setPerson(person.clone());
		}
		System.out.println(bean);
		return ":";
	}

	/**
	 * Change the {@link Organization}
	 * 
	 * @param bean
	 * @param positionUser
	 * @param positionOrga
	 * @return
	 */
	public String changeOrga(Object bean, int positionUser, int positionOrga) {
		Organization orga = loadOrga(orgaURI);
		if (bean instanceof UserCreationBean) {
			List<Organization> l = (List<Organization>) ((UserCreationBean) bean)
					.getUser().getPerson().getOrganizations();
			l.set(positionOrga, orga);
		} else if (bean instanceof ContainerBean) {

			List<Person> pl = (List<Person>) ((ContainerBean) bean)
					.getContainer().getMetadata().getPersons();
			List<Organization> l = (List<Organization>) pl.get(positionUser)
					.getOrganizations();
			l.set(positionOrga, orga.clone());
		} else if (bean instanceof UserBean) {
			List<Organization> l = (List<Organization>) ((UserBean) bean)
					.getUser().getPerson().getOrganizations();
			l.set(positionOrga, orga);
		}
		return ":";
	}

	/**
	 * Load the {@link Person} with the passed uri
	 * 
	 * @param uri
	 * @return
	 */
	private Person loadPerson(String uri) {
		if (uri != null) {
			try {
				UserController uc = new UserController(sb.getUser());
				return uc.retrievePersonById(personURI);
			} catch (Exception e) {
				BeanHelper.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

	private Organization loadOrga(String uri) {
		if (uri != null) {
			try {
				UserController uc = new UserController(sb.getUser());
				return uc.retrieveOrganizationById(uri);
			} catch (Exception e) {
				BeanHelper.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Get the {@link Person} which comes from the parent bean
	 */
	private Person getPersonFromParentBean() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExpression = facesContext
				.getApplication()
				.getExpressionFactory()
				.createValueExpression(elContext, "#{cc.attrs.person}",
						Person.class);

		return (Person) valueExpression.getValue(elContext);
	}

	/**
	 * Add an organization to an author of the {@link CollectionImeji}
	 * 
	 * @param authorPosition
	 * @param organizationPosition
	 * @return
	 */
	public String addOrganization(int organizationPosition) {
		List<Organization> orgs = (List<Organization>) getPersonFromParentBean()
				.getOrganizations();
		Organization o = ImejiFactory.newOrganization();
		o.setPos(organizationPosition);
		orgs.add(organizationPosition, o);
		return "";
	}

	/**
	 * Remove an organization to an author of the {@link CollectionImeji}
	 * 
	 * @return
	 */
	public String removeOrganization(int organizationPosition) {

		List<Organization> orgs = (List<Organization>) getPersonFromParentBean()
				.getOrganizations();
		if (orgs.size() > 1)
			orgs.remove(organizationPosition);
		else
			BeanHelper.error(((SessionBean) BeanHelper
					.getSessionBean(SessionBean.class))
					.getMessage("error_author_need_one_organization"));
		return "";
	}

	/**
	 * Listener
	 * 
	 * @param event
	 */
	public void orgaListener(ValueChangeEvent event) {
		this.orgaURI = event.getNewValue().toString();
	}

	/**
	 * Listener
	 * 
	 * @param event
	 */
	public void personListener(ValueChangeEvent event) {
		this.personURI = event.getNewValue().toString();
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public String getPersonURI() {
		return personURI;
	}

	/**
	 * setter
	 * 
	 * @param personURI
	 */
	public void setPersonURI(String personURI) {
		this.personURI = personURI;
	}

	public String getOrgaURI() {
		return orgaURI;
	}

	public void setOrgaURI(String orgaURI) {
		this.orgaURI = orgaURI;
	}

}

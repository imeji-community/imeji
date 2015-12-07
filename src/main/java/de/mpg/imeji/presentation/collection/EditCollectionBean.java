/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.history.HistorySession;
import de.mpg.imeji.presentation.util.BeanHelper;

@ManagedBean(name = "EditCollectionBean")
@SessionScoped
public class EditCollectionBean extends CollectionBean {
  private static final long serialVersionUID = 568267990816647451L;

  public EditCollectionBean() {
    super();
  }

  public void init() throws Exception {
    super.setTab(TabType.COLLECTION);
    // sets explicitely Edit mode for the collection Bean, to avoid
    // classname comparisons
    setCollectionCreateMode(false);
    //
    // //retrieves the parameter with which edit collection Bean is set-up
    getProfileSelect();
    String id = super.getId();
    if (id != null) {
      ((ViewCollectionBean) BeanHelper.getSessionBean(ViewCollectionBean.class)).setId(id);
      ((ViewCollectionBean) BeanHelper.getSessionBean(ViewCollectionBean.class)).init();
      setProfile(
          ((ViewCollectionBean) BeanHelper.getSessionBean(ViewCollectionBean.class)).getProfile());
      setCollection(((ViewCollectionBean) BeanHelper.getSessionBean(ViewCollectionBean.class))
          .getCollection());
      setSendEmailNotification(sessionBean.getUser().getObservedCollections().contains(id));
      LinkedList<Person> persons = new LinkedList<Person>();
      if (getCollection().getMetadata().getPersons().size() == 0) {
        getCollection().getMetadata().getPersons().add(new Person());
      }
      for (Person p : getCollection().getMetadata().getPersons()) {
        LinkedList<Organization> orgs = new LinkedList<Organization>();
        for (Organization o : p.getOrganizations()) {
          orgs.add(o);
        }
        p.setOrganizations(orgs);
        persons.add(p);
      }
      getCollection().getMetadata().setPersons(persons);
      // set the loaded collection in the session
      ((CollectionSessionBean) BeanHelper.getSessionBean(CollectionSessionBean.class))
          .setActive(getCollection());
    } else {
      BeanHelper.error(sessionBean.getLabel("error") + " : no ID in URL");
    }

    if (UrlHelper.getParameterBoolean("start")) {
      try {
        upload();
      } catch (Exception e) {
        BeanHelper.error(sessionBean.getMessage("error_collection_logo_uri_save"));
      }

    }

  }

  public String save() throws Exception {
    if (saveEditedCollection()) {
      FacesContext.getCurrentInstance().getExternalContext()
          .redirect(((HistorySession) BeanHelper.getSessionBean(HistorySession.class))
              .getPreviousPage().getCompleteUrl());
    }

    return "";
  }

  public boolean saveEditedCollection() {
    try {
      CollectionController collectionController = new CollectionController();
      User user = sessionBean.getUser();
      CollectionImeji icPre = collectionController.retrieve(getCollection().getId(), user);
      CollectionImeji ic = collectionController.update(getCollection(), user);
      if (icPre.getLogoUrl() != null && getCollection().getLogoUrl() == null) {
        collectionController.updateLogo(icPre, null, user);
      }
      UserController uc = new UserController(user);
      uc.update(user, user);

      // here separate update for the Logo only, as it will only be
      // allowed by edited collection through the web application
      // not yet for REST
      // getIngestImage is inherited from Container!

      if (getIngestImage() != null) {
        collectionController.updateLogo(ic, getIngestImage().getFile(), user);
        setIngestImage(null);
      }
      BeanHelper.info(sessionBean.getMessage("success_collection_save"));
      return true;
    } catch (ImejiException e) {
      BeanHelper.cleanMessages();
      BeanHelper.error(sessionBean.getMessage("error_collection_save"));
      List<String> listOfErrors = Arrays.asList(e.getMessage().split(";"));
      for (String errorM : listOfErrors) {
        BeanHelper.error(sessionBean.getMessage(errorM));
      }
      return false;
    } catch (IOException e) {
      BeanHelper.error(sessionBean.getMessage("error_collection_logo_save"));
      return false;
    } catch (URISyntaxException e) {
      BeanHelper.error(sessionBean.getMessage("error_collection_logo_uri_save"));
      return false;
    }

  }

  /**
   * Return the link for the Cancel button
   * 
   * @return
   */
  public String getCancel() {
    Navigation nav = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
    return nav.getCollectionUrl() + ObjectHelper.getId(getCollection().getId()) + "/"
        + nav.getInfosPath() + "?init=1";
  }

  @Override
  protected String getNavigationString() {
    return sessionBean.getPrettySpacePage("pretty:editCollection");
  }

  /**
   * Method called on the html page to trigger the initialization of the bean
   * 
   * @return
   */
  public String getProfileSelect() {
    if (UrlHelper.getParameterBoolean("profileSelect")) {
      setProfileSelectMode(true);
    } else {
      setProfileSelectMode(false);
    }
    return "";
  }

  /**
   * Method for save&editProfile button. Create the {@link MetadataProfile} according to the form
   * 
   * @return
   * @throws Exception
   */
  public String saveAndEditProfile() throws Exception {
    if (saveEditedCollection()) {
      FacesContext.getCurrentInstance().getExternalContext().redirect(navigation.getProfileUrl()
          + getProfileId() + "/edit?init=1&col=" + getCollection().getIdString());
    }
    return "";
  }

}

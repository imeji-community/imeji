/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ObjectLoader;

/**
 * Bean for the pages "CollectionEntryPage" and "ViewCollection"
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "ViewCollectionBean")
@RequestScoped
public class ViewCollectionBean extends CollectionBean {
  private static final long serialVersionUID = 6473181109648137472L;
  private List<Person> persons = null;
  private static Logger logger = Logger.getLogger(ViewCollectionBean.class);
  private String email;
  private String encryptedPassword;
  /**
   * Maximum number of items displayed on collection start page
   */
  private static final int MAX_ITEM_NUM_VIEW = 13;

  /**
   * Construct a default {@link ViewCollectionBean}
   */
  public ViewCollectionBean() {
    super();
  }

  /**
   * Initialize all elements of the page.
   * 
   * @throws Exception
   */
  public void init() throws Exception {
    try {
      User user = super.sessionBean.getUser();
      String id = getId();


      CollectionImeji requestedCollection = null;
      URI uRIID = ObjectHelper.getURI(CollectionImeji.class, id);

      requestedCollection = ObjectLoader.loadCollectionLazy(uRIID, user);
      if (user != null) {
        for (Grant g : user.getGrants()) {
          System.err.println(requestedCollection.getId());
          System.out.println(g.getGrantFor());
          System.out.println(g.getGrantType());
          if (g.getGrantFor().equals(requestedCollection.getId())
              && (g.asGrantType() == GrantType.ADMIN || g.asGrantType() == GrantType.CREATE)) {
            this.email = user.getEmail();
            this.encryptedPassword = user.getEncryptedPassword();
            break;
          }
        }

      }

      setCollection(requestedCollection);

      if (getCollection() != null && getCollection().getId() != null) {
        findItems(user, MAX_ITEM_NUM_VIEW);
        loadItems(user);
        countItems();
      }

      if (sessionBean.getUser() != null) {
        setSendEmailNotification(sessionBean.getUser().getObservedCollections().contains(id));
      }
      if (getCollection() != null) {
        setProfile(ObjectLoader.loadProfile(getCollection().getProfile(), user));
        setProfileId(ObjectHelper.getId(getProfile().getId()));
        // super.setTab(TabType.COLLECTION);
        persons = new ArrayList<Person>();
        for (Person p : super.getCollection().getMetadata().getPersons()) {
          List<Organization> orgs = new ArrayList<Organization>();
          for (Organization o : p.getOrganizations()) {
            orgs.add(o);
          }
          p.setOrganizations(orgs);
          persons.add(p);
        }
        getCollection().getMetadata().setPersons(persons);
      }
    } catch (Exception e) {
      logger.error("", e);
      // Has to be in try/catch block, otherwise redirct from
      // HistoryFilter will not work.
      // Here simply do nothing
    }

  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEncryptedPassword() {
    return encryptedPassword;
  }

  public void setEncryptedPassword(String encryptedPassword) {
    this.encryptedPassword = encryptedPassword;
  }

  public List<Person> getPersons() {
    return persons;
  }

  public void setPersons(List<Person> persons) {
    this.persons = persons;
  }

  @Override
  protected String getNavigationString() {
    return sessionBean.getPrettySpacePage("pretty:collectionInfos");
  }

  public String getSmallDescription() {
    if (this.getCollection() == null || this.getCollection().getMetadata().getDescription() == null)
      return "No Description";
    if (this.getCollection().getMetadata().getDescription().length() > 100) {
      return this.getCollection().getMetadata().getDescription().substring(0, 100) + "...";
    } else {
      return this.getCollection().getMetadata().getDescription();
    }
  }

  /**
   * @return
   */
  public String getFormattedDescription() {
    if (this.getCollection() == null || this.getCollection().getMetadata().getDescription() == null)
      return "";
    return this.getCollection().getMetadata().getDescription().replaceAll("\n", "<br/>");
  }

  /**
   * @return
   */
  public String getCitation() {
    String title = super.getCollection().getMetadata().getTitle();
    String author = this.getPersonString();
    String url = super.getPageUrl();
    String citation =
        title + " " + sessionBean.getLabel("from") + " <i>" + author + "</i></br>" + url;
    return citation;
  }
}

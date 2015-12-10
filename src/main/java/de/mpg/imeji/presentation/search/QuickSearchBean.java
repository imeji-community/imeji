/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Java bean for the simple search
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class QuickSearchBean implements Serializable {
  private static final long serialVersionUID = 1599497861175666068L;
  private String searchString = "";
  private String selectedSearchType = "images";
  private static Logger logger = Logger.getLogger(QuickSearchBean.class);

  /**
   * Method when search is submitted
   * 
   * @return
   * @throws IOException
   */
  public String search() throws IOException {
    return search(null);
  }

  public String search(String type) throws UnsupportedEncodingException, IOException {
    Navigation navigation = (Navigation) BeanHelper.getApplicationBean(Navigation.class);
    if ("".equals(searchString)) {
      BeanHelper.error(((SessionBean) BeanHelper.getSessionBean(SessionBean.class))
          .getMessage("error_search_query_emtpy"));
      return "pretty:";
    }
    if ("collections".equals(type)) {
      FacesContext.getCurrentInstance().getExternalContext().redirect(
          navigation.getCollectionsUrl() + "?q=" + URLEncoder.encode(searchString, "UTF-8"));
    } else if ("albums".equals(type)) {
      FacesContext.getCurrentInstance().getExternalContext()
          .redirect(navigation.getAlbumsUrl() + "?q=" + URLEncoder.encode(searchString, "UTF-8"));
    } else {
      FacesContext.getCurrentInstance().getExternalContext()
          .redirect(navigation.getBrowseUrl() + "?q=" + URLEncoder.encode(searchString, "UTF-8"));
    }
    return "";
  }

  /**
   * setter
   * 
   * @param searchString
   */
  public void setSearchString(String searchString) {
    this.searchString = searchString;
  }

  /**
   * getter
   * 
   * @return
   */
  public String getSearchString() {
    return searchString;
  }

  /**
   * setter
   * 
   * @param selectedSearchType
   */
  public void setSelectedSearchType(String selectedSearchType) {
    this.selectedSearchType = selectedSearchType;
  }

  /**
   * getter
   * 
   * @return
   */
  public String getSelectedSearchType() {
    if (selectedSearchType == null)
      selectedSearchType = "images";
    return selectedSearchType;
  }

  /**
   * Listener for the search type
   * 
   * @param event
   */
  public void selectedSearchTypeListener(ValueChangeEvent event) {
    if (event.getNewValue() != null && !event.getNewValue().equals(event.getOldValue())) {
      selectedSearchType = (String) event.getNewValue();
    }
  }
}

/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.concurrency.locks.Lock;
import de.mpg.imeji.logic.concurrency.locks.Locks;
import de.mpg.imeji.logic.controller.resource.ItemController;
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.metadata.editors.SingleEditor;
import de.mpg.imeji.presentation.metadata.util.SuggestBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Metadata Editor for the detail item page
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SingleEditorWrapper {
  private Item item = null;
  private MetadataProfile profile = null;
  private SingleEditor editor = null;
  private String toggleState = "displayMd";
  private int mdPosition = 0;
  private List<MetadataWrapper> metadataList;
  private User sessionUser;
  private Locale locale;

  /**
   * Constructor
   * 
   * @param im
   * @param profile
   * @param pageUrl
   */
  public SingleEditorWrapper(Item im, MetadataProfile profile, User sessionUser, Locale locale) {
    this.item = im;
    this.profile = profile;
    this.sessionUser = sessionUser;
    this.locale = locale;
    init();
  }

  /**
   * Check in the url if the editor should be automatically shown
   * 
   * @return
   */
  public String getCheckToggleState() {
    toggleState = "displayMd";
    if (UrlHelper.getParameterBoolean("edit")) {
      showEditor();
    }
    return "";
  }

  /**
   * Initialize the page
   */
  private void init() {
    editor = new SingleEditor(item, profile, null, sessionUser, locale);
    ((SuggestBean) BeanHelper.getSessionBean(SuggestBean.class)).init(profile);
    metadataList = new ArrayList<MetadataWrapper>();
    metadataList.addAll(editor.getItems().get(0).getMds().getTree().getList());
  }


  /**
   * Cancel the editing, and reset original values
   * 
   * @return
   * @throws Exception
   */
  public String cancel() throws Exception {
    this.toggleState = "displayMd";
    SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
    Locks.unLock(new Lock(item.getId().toString(), sb.getUser().getEmail()));
    reloadImage();
    editor = new SingleEditor(item, profile, null, sessionUser, locale);
    return "";
  }

  /**
   * Reload the current image
   */
  private void reloadImage() {
    SessionBean sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
    ItemController itemController = new ItemController();
    try {
      item = itemController.retrieve(item.getId(), sessionBean.getUser());
    } catch (Exception e) {
      BeanHelper.error("Error reload item" + e.getMessage());
    }
  }

  /**
   * Show the metadata editor
   * 
   * @return
   */
  public String showEditor() {
    SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
    if (AuthUtil.staticAuth().updateContent(sb.getUser(), item)) {
      this.toggleState = "editMd";
      try {
        Locks.lock(new Lock(item.getId().toString(), sb.getUser().getEmail()));
      } catch (Exception e) {
        BeanHelper
            .error(Imeji.RESOURCE_BUNDLE.getMessage("error_editor_image_locked", sb.getLocale()));
      }
    } else {
      BeanHelper
          .error(Imeji.RESOURCE_BUNDLE.getMessage("error_editor_not_allowed", sb.getLocale()));
    }
    return "";
  }

  public SingleEditor getEditor() {
    return editor;
  }

  public void setEditor(SingleEditor editor) {
    this.editor = editor;
  }

  public Item getImage() {
    return item;
  }

  public void setImage(Item item) {
    this.item = item;
  }

  public MetadataProfile getProfile() {
    return profile;
  }

  public void setProfile(MetadataProfile profile) {
    this.profile = profile;
  }

  public int getMdPosition() {
    return mdPosition;
  }

  public void setMdPosition(int mdPosition) {
    this.mdPosition = mdPosition;
  }

  public String getToggleState() {
    return toggleState;
  }

  public void setToggleState(String toggleState) {
    this.toggleState = toggleState;
  }

  public List<MetadataWrapper> getMetadataList() {
    return metadataList;
  }

  public void setMetadataList(List<MetadataWrapper> metadataList) {
    this.metadataList = metadataList;
  }

}

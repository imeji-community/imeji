/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.metadata.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.resource.ItemController;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.util.MetadataFactory;
import de.mpg.imeji.presentation.metadata.ItemWrapper;
import de.mpg.imeji.presentation.metadata.MetadataWrapper;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Abstract call for the {@link Metadata} editors
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public abstract class AbstractMetadataEditor {
  private static final Logger LOGGER = Logger.getLogger(AbstractMetadataEditor.class);
  protected List<ItemWrapper> items;
  protected Statement statement;
  protected MetadataProfile profile;
  protected User sessionUser;
  protected Locale locale;

  // protected Validator validator;
  /**
   * Editor: Edit a list of images for one statement.
   * 
   * @param items
   * @param statement
   */
  public AbstractMetadataEditor(List<Item> itemList, MetadataProfile profile, Statement statement,
      boolean addEmtpyValue, User sessionUser, Locale locale) {
    this.statement = statement;
    this.profile = profile;
    this.locale = locale;
    this.sessionUser = sessionUser;
    items = new ArrayList<ItemWrapper>();
    for (Item item : itemList) {
      items.add(new ItemWrapper(item, profile, addEmtpyValue));
    }
    initialize();
  }

  /**
   * Default editor
   */
  public AbstractMetadataEditor() {
    // cosntructor...
  }

  /**
   * Reset all value to empty state
   */
  public void reset() {
    items = new ArrayList<ItemWrapper>();
    statement = null;
    profile = null;
  }

  /**
   * Clone as a {@link MultipleEditor}
   */
  @Override
  public AbstractMetadataEditor clone() {
    AbstractMetadataEditor editor = new MultipleEditor();
    editor.setItems(items);
    editor.setProfile(profile);
    editor.setStatement(statement);
    editor.setSessionUser(sessionUser);
    editor.setLocale(locale);
    return editor;
  }

  /**
   * Save the {@link Item} and {@link Metadata} defined in the editor
   * 
   * @throws ImejiException
   */
  public void save() throws ImejiException {
    ItemController ic = new ItemController();
    List<Item> itemList = validateAndFormatItemsForSaving();
    ic.updateBatch(itemList, sessionUser);
    // String str =
    // items.size() + " " + Imeji.RESOURCE_BUNDLE.getMessage("success_editor_images", locale);
    // if (items.size() == 1) {
    // str = Imeji.RESOURCE_BUNDLE.getMessage("success_editor_image", locale);
    // }
    // BeanHelper.info(str);
    // try {
    // List<Item> itemList = validateAndFormatItemsForSaving();
    // ic.updateBatch(itemList, sessionUser);
    // String str =
    // items.size() + " " + Imeji.RESOURCE_BUNDLE.getMessage("success_editor_images", locale);
    // if (items.size() == 1) {
    // str = Imeji.RESOURCE_BUNDLE.getMessage("success_editor_image", locale);
    // }
    // BeanHelper.info(str);
    // return true;
    //
    // } catch (UnprocessableError e) {
    // for (ItemWrapper eib : this.items) {
    // eib.getMds().setTree(eib.getMds().getUncutTree());
    // }
    // BeanHelper.cleanMessages();
    // BeanHelper.error(Imeji.RESOURCE_BUNDLE.getMessage("error_metadata_edit", locale));
    // List<String> listOfErrors = Arrays.asList(e.getMessage().split(";"));
    // for (String errorM : listOfErrors) {
    // BeanHelper.error(errorM);
    // }
    // return false;
    // } catch (ImejiException e) {
    // BeanHelper.error(Imeji.RESOURCE_BUNDLE.getMessage("error_metadata_edit", locale));
    // LOGGER.error("Error saving editor", e);
    // return false;
    // }
  }

  /**
   * Validate and prepare the Items of the editor, so they can be saved
   * 
   * @return
   */
  public List<Item> validateAndFormatItemsForSaving() {
    List<Item> itemList = new ArrayList<Item>();
    SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);

    if (prepareUpdate()) {
      try {
        addPositionToMetadata();
        for (ItemWrapper eib : items) {
          itemList.add(eib.asItem());
        }
      } catch (Exception e) {
        BeanHelper.error(Imeji.RESOURCE_BUNDLE.getMessage("error_metadata_edit", sb.getLocale())
            + ": " + e.getLocalizedMessage());
      }
    } else {
      BeanHelper
          .error(Imeji.RESOURCE_BUNDLE.getMessage("error_metadata_edit_no_images", sb.getLocale()));
    }
    return itemList;

  }

  /**
   * enable ordering for metadata values
   */
  protected void addPositionToMetadata() {
    for (ItemWrapper eib : items) {
      int pos = 0;
      for (MetadataWrapper smb : eib.getMds().getTree().getList()) {
        smb.setPos(pos);
        pos++;
      }
    }
  }

  public abstract void initialize();

  public abstract boolean prepareUpdate();

  /**
   * Create a new Metadata according to current Editor configuration.
   * 
   * @return
   */
  protected Metadata newMetadata() {
    if (statement != null) {
      return MetadataFactory.createMetadata(statement);
    }
    return null;
  }

  public List<ItemWrapper> getItems() {
    return items;
  }

  public int getItemsSize() {
    return items.size();
  }

  public void setItems(List<ItemWrapper> items) {
    this.items = items;
  }

  public Statement getStatement() {
    return statement;
  }

  public void setStatement(Statement statement) {
    this.statement = statement;
  }

  public MetadataProfile getProfile() {
    return profile;
  }

  public void setProfile(MetadataProfile profile) {
    this.profile = profile;
  }

  /**
   * @return the sessionUser
   */
  public User getSessionUser() {
    return sessionUser;
  }

  /**
   * @param sessionUser the sessionUser to set
   */
  public void setSessionUser(User sessionUser) {
    this.sessionUser = sessionUser;
  }

  /**
   * @return the locale
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * @param locale the locale to set
   */
  public void setLocale(Locale locale) {
    this.locale = locale;
  }


}

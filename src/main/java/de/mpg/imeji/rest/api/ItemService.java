package de.mpg.imeji.rest.api;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE.CREATE;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE.UPDATE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.SearchQueryParser;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.defaultTO.DefaultItemTO;
import de.mpg.imeji.rest.helper.ProfileCache;
import de.mpg.imeji.rest.process.ReverseTransferObjectFactory;
import de.mpg.imeji.rest.process.TransferObjectFactory;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;

public class ItemService implements API<ItemTO> {

  private static ItemController controller = new ItemController();

  @Override
  public ItemTO create(ItemTO to, User u) throws ImejiException {
    if (to instanceof ItemWithFileTO) {
      // get newFilename
      String filename = getFilename((ItemWithFileTO) to);

      // transfer TO into item
      Item item = new Item();

      ReverseTransferObjectFactory.transferItem(to, item, u, CREATE);

      item = controller.create(item, ((ItemWithFileTO) to).getFile(), filename, u,
          ((ItemWithFileTO) to).getFetchUrl(), ((ItemWithFileTO) to).getReferenceUrl());
      // transfer item into ItemTO
      ItemTO itemTO = new ItemTO();
      ProfileCache profileCache = new ProfileCache();
      TransferObjectFactory.transferItem(item, itemTO,
          profileCache.read(item.getMetadataSet().getProfile()));
      return itemTO;
    } else {
      throw new BadRequestException(
          "A file must be uploaded, referenced or fetched from external location.");
    }
  }

  public DefaultItemTO readDefault(String id, User u) throws ImejiException {
    DefaultItemTO defaultTO = new DefaultItemTO();
    Item item = controller.retrieve(ObjectHelper.getURI(Item.class, id), u);
    ProfileCache profileCache = new ProfileCache();
    TransferObjectFactory.transferDefaultItem(item, defaultTO,
        profileCache.read(item.getMetadataSet().getProfile()));
    return defaultTO;
  }

  @Override
  public ItemTO read(String id, User u) throws ImejiException {
    Item item = controller.retrieve(ObjectHelper.getURI(Item.class, id), u);
    ItemTO to = new ItemTO();
    ProfileCache profileCache = new ProfileCache();
    TransferObjectFactory.transferItem(item, to,
        profileCache.read(item.getMetadataSet().getProfile()));
    return to;

  }

  public List<ItemTO> readItems(User u, String q, int offset, int size)
      throws ImejiException, IOException {
    ProfileCache profileCache = new ProfileCache();
    List<ItemTO> tos = new ArrayList<>();
    for (Item vo : new ItemController().searchAndRetrieve(null,
        SearchQueryParser.parseStringQuery(q), null, u, null, offset, size)) {
      ItemTO to = new ItemTO();
      TransferObjectFactory.transferItem(vo, to,
          profileCache.read(vo.getMetadataSet().getProfile()));
      tos.add(to);
    }
    return tos;
  }

  public List<DefaultItemTO> readDefaultItems(User u, String q, int offset, int size)
      throws ImejiException, IOException {
    ProfileCache profileCache = new ProfileCache();
    List<DefaultItemTO> tos = new ArrayList<>();
    for (Item vo : new ItemController().searchAndRetrieve(null,
        SearchQueryParser.parseStringQuery(q), null, u, null, offset, size)) {
      DefaultItemTO to = new DefaultItemTO();
      TransferObjectFactory.transferDefaultItem(vo, to,
          profileCache.read(vo.getMetadataSet().getProfile()));
      tos.add(to);
    }
    return tos;
  }


  public ItemTO update(ItemTO to, User u) throws ImejiException {
    Item item = controller.retrieve(ObjectHelper.getURI(Item.class, to.getId()), u);
    ReverseTransferObjectFactory.transferItem(to, item, u, UPDATE);
    if (to instanceof ItemWithFileTO) {
      ItemWithFileTO tof = (ItemWithFileTO) to;
      String url = getExternalFileUrl(tof);
      if (tof.getFile() != null) {
        item = controller.updateFile(item, tof.getFile(), to.getFilename(), u);
      } else if (!isNullOrEmpty(url)) {
        item = controller.updateWithExternalFile(item, getExternalFileUrl(tof), to.getFilename(),
            downloadFile(tof), u);
      }
    } else {
      item = controller.update(item, u);
    }
    to = new ItemTO();
    ProfileCache profileCache = new ProfileCache();
    TransferObjectFactory.transferItem(item, to,
        profileCache.read(item.getMetadataSet().getProfile()));
    return to;
  }

  @Override
  public boolean delete(String id, User u) throws ImejiException {
    controller.delete(id, u);
    return true;
  }

  @Override
  public ItemTO release(String id, User u) throws ImejiException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public ItemTO withdraw(String id, User u, String discardComment) throws ImejiException {
    // TODO Auto-generated method stub
    return null;

  }

  @Override
  public void share(String id, String userId, List<String> roles, User u) throws ImejiException {
    // TODO Auto-generated method stub

  }

  @Override
  public void unshare(String id, String userId, List<String> roles, User u) throws ImejiException {
    // TODO Auto-generated method stub

  }

  @Override
  public List<String> search(String q, User u) throws ImejiException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Find the correct filename if there
   * 
   * @param to
   * @return
   **/
  private String getFilename(ItemWithFileTO to) {
    return ObjectUtils.firstNonNull(to.getFilename(),
        (to.getFile() != null) ? FilenameUtils.getName(to.getFile().getName()) : "",
        FilenameUtils.getName(to.getFetchUrl()), FilenameUtils.getName(to.getReferenceUrl()));
  }

  /**
   * Return the external Url
   * 
   * @param to
   * @return
   */
  private String getExternalFileUrl(ItemWithFileTO to) {
    return firstNonNullOrEmtpy(to.getFetchUrl(), to.getReferenceUrl());
  }

  private String firstNonNullOrEmtpy(String... strs) {
    if (strs == null)
      return null;
    for (String str : strs)
      if (str != null && !"".equals(str.trim()))
        return str;
    return null;
  }

  /**
   * True if the file must be download in imeji (i.e fetchurl is defined)
   * 
   * @param to
   * @return
   */
  private boolean downloadFile(ItemWithFileTO to) {
    return !isNullOrEmpty(to.getFetchUrl());
  }



}

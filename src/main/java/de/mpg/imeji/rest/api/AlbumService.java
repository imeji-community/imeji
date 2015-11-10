package de.mpg.imeji.rest.api;

import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.transferAlbum;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE.CREATE;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE.UPDATE;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.helper.ProfileCache;
import de.mpg.imeji.rest.process.CommonUtils;
import de.mpg.imeji.rest.process.TransferObjectFactory;
import de.mpg.imeji.rest.to.AlbumTO;
import de.mpg.imeji.rest.to.ItemTO;

public class AlbumService implements API<AlbumTO> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlbumService.class);

  private AlbumTO getAlbumTO(AlbumController controller, String id, User u) throws ImejiException {
    AlbumTO to = new AlbumTO();
    Album vo = controller.retrieve(ObjectHelper.getURI(Album.class, id), u);
    TransferObjectFactory.transferAlbum(vo, to);
    return to;
  }

  @Override
  public AlbumTO read(String id, User u) throws ImejiException {
    AlbumController controller = new AlbumController();
    return getAlbumTO(controller, id, u);
  }

  public List<AlbumTO> readAll(User u, String q) throws ImejiException {
    return Lists.transform(new AlbumController().retrieve(u, q, null),
        new Function<Album, AlbumTO>() {
          @Override
          public AlbumTO apply(Album vo) {
            AlbumTO to = new AlbumTO();
            TransferObjectFactory.transferAlbum(vo, to);
            return to;
          }
        });
  }

  public List<ItemTO> readItems(String id, User u, String q) throws ImejiException {
    AlbumController cc = new AlbumController();
    ProfileCache profileCache = new ProfileCache();
    List<ItemTO> tos = new ArrayList<>();
    for (Item vo : cc.retrieveItems(id, u, q)) {
      ItemTO to = new ItemTO();
      TransferObjectFactory.transferItem(vo, to,
          profileCache.read(vo.getMetadataSet().getProfile()));
      tos.add(to);
    }
    return tos;
  }

  @Override
  public AlbumTO create(AlbumTO to, User u) throws ImejiException {
    AlbumController ac = new AlbumController();
    Album vo = new Album();
    transferAlbum(to, vo, CREATE, u);
    URI albumURI;
    albumURI = ac.create(vo, u);
    return read(CommonUtils.extractIDFromURI(albumURI), u);
  }

  @Override
  public AlbumTO update(AlbumTO to, User u) throws ImejiException {
    AlbumController ac = new AlbumController();

    Album vo = ac.retrieve(ObjectHelper.getURI(Album.class, to.getId()), u);
    if (vo == null)
      throw new UnprocessableError("Album not found");

    transferAlbum(to, vo, UPDATE, u);
    AlbumTO newTO = new AlbumTO();
    TransferObjectFactory.transferAlbum(ac.update(vo, u), newTO);
    return newTO;
  }

  @Override
  public boolean delete(String id, User u) throws ImejiException {
    AlbumController controller = new AlbumController();
    Album vo = controller.retrieve(ObjectHelper.getURI(Album.class, id), u);
    controller.delete(vo, u);
    return true;
  }

  @Override
  public AlbumTO release(String id, User u) throws ImejiException {
    AlbumController controller = new AlbumController();
    Album vo = controller.retrieve(ObjectHelper.getURI(Album.class, id), u);
    controller.release(vo, u);

    // Now Read the album and return it back
    return getAlbumTO(controller, id, u);
  }

  @Override
  public AlbumTO withdraw(String id, User u, String discardComment) throws ImejiException {
    AlbumController controller = new AlbumController();
    Album vo = controller.retrieve(ObjectHelper.getURI(Album.class, id), u);
    vo.setDiscardComment(discardComment);
    controller.withdraw(vo, u);

    // Now Read the withdrawn album and return it back
    return getAlbumTO(controller, id, u);
  }

  @Override
  public void share(String id, String userId, List<String> roles, User u) throws ImejiException {
    // TODO Auto-generated method stub

  }

  @Override
  public List<String> search(String q, User u) throws ImejiException {
    // TODO Auto-generated method stub
    return null;
  }

  public List<String> addItems(String id, User u, List<String> itemIds) throws ImejiException {
    AlbumController controller = new AlbumController();
    Album vo = controller.retrieve(ObjectHelper.getURI(Album.class, id), u);
    List<String> itemUris = new ArrayList<>();

    // Convert Ids to Uris
    for (String itemId : itemIds) {
      itemUris.add(ObjectHelper.getURI(Item.class, itemId).toASCIIString());
    }
    List<String> ids = new ArrayList<String>();
    for (URI itemURI : controller.addToAlbum(vo, itemUris, u))
      ids.add(CommonUtils.extractIDFromURI(itemURI));
    return ids;
  }

  public boolean removeItems(String id, User u, List<String> itemIds, boolean removeAll)
      throws ImejiException {
    AlbumController controller = new AlbumController();
    Album vo = controller.retrieve(ObjectHelper.getURI(Album.class, id), u);
    List<String> itemUris = new ArrayList<>();
    if (!removeAll) {
      // Convert Ids to Uris
      for (String itemId : itemIds) {
        itemUris.add(ObjectHelper.getURI(Item.class, itemId).toASCIIString());
      }

      controller.removeFromAlbum(vo, itemUris, u);
    } else {
      controller.clearAlbumItems(vo, u);
    }
    return true;
  }

  @Override
  public void unshare(String id, String userId, List<String> roles, User u) throws ImejiException {
    // TODO Auto-generated method stub
  }

}

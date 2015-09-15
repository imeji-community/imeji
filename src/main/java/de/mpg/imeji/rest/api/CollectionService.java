package de.mpg.imeji.rest.api;

import static com.google.common.base.Strings.isNullOrEmpty;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.transferCollection;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE.CREATE;
import static de.mpg.imeji.rest.process.ReverseTransferObjectFactory.TRANSFER_MODE.UPDATE;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.search.SearchQueryParser;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.rest.defaultTO.DefaultItemTO;
import de.mpg.imeji.rest.process.CommonUtils;
import de.mpg.imeji.rest.process.TransferObjectFactory;
import de.mpg.imeji.rest.to.CollectionProfileTO;
import de.mpg.imeji.rest.to.CollectionProfileTO.METHOD;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.ItemTO;

public class CollectionService implements API<CollectionTO> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CollectionService.class);

  private CollectionTO getCollectionTO(CollectionController cc, String id, User u)
      throws ImejiException {
    CollectionTO to = new CollectionTO();
    TransferObjectFactory.transferCollection(getCollectionVO(cc, id, u), to);
    return to;
  }

  private CollectionImeji getCollectionVO(CollectionController cc, String id, User u)
      throws ImejiException {
    return cc.retrieve(ObjectHelper.getURI(CollectionImeji.class, id), u);
  }

  @Override
  public CollectionTO read(String id, User u) throws ImejiException {

    CollectionController controller = new CollectionController();
    return getCollectionTO(controller, id, u);
  }

  /**
   * Read all the items of a collection according to search query. Response is done with the raw
   * format
   * 
   * @param id
   * @param u
   * @param q
   * @return
   * @throws ImejiException
   * @throws IOException
   */
  public List<ItemTO> readItems(String id, User u, String q) throws ImejiException, IOException {
    ItemController itemController = new ItemController();
    return Lists.transform(itemController.searchAndRetrieve(
        ObjectHelper.getURI(CollectionImeji.class, id), SearchQueryParser.parseStringQuery(q),
        null, u, null), new Function<Item, ItemTO>() {
      @Override
      public ItemTO apply(Item vo) {
        ItemTO to = new ItemTO();
        TransferObjectFactory.transferItem(vo, to);
        return to;
      }
    });
  }

  /**
   * Read all the items of a collection according to search query. Response is done with the default
   * format
   * 
   * @param id
   * @param u
   * @param q
   * @return
   * @throws ImejiException
   * @throws IOException
   */
  public Object readDefaultItems(String id, User u, String q) throws ImejiException, IOException {
    ItemController itemController = new ItemController();
    return Lists.transform(itemController.searchAndRetrieve(
        ObjectHelper.getURI(CollectionImeji.class, id), SearchQueryParser.parseStringQuery(q),
        null, u, null), new Function<Item, DefaultItemTO>() {
      @Override
      public DefaultItemTO apply(Item vo) {
        DefaultItemTO to = new DefaultItemTO();
        TransferObjectFactory.transferDefaultItem(vo, to);
        return to;
      }
    });
  }

  public List<CollectionTO> readAll(User u, String q) throws ImejiException, IOException {
    CollectionController cc = new CollectionController();
    return Lists.transform(
        cc.searchAndRetrieve(SearchQueryParser.parseStringQuery(q), null, 0, -1, u, null),
        new Function<CollectionImeji, CollectionTO>() {
          @Override
          public CollectionTO apply(CollectionImeji vo) {
            CollectionTO to = new CollectionTO();
            TransferObjectFactory.transferCollection(vo, to);
            return to;
          }
        });
  }

  @Override
  public CollectionTO create(CollectionTO to, User u) throws ImejiException {
    // toDo: Move to Controller
    CollectionController cc = new CollectionController();
    ProfileController pc = new ProfileController();

    MetadataProfile mp = null;
    String profileId = to.getProfile().getId();
    String method = to.getProfile().getMethod();
    String newId = null;
    // create new profile (take default)
    // if (isNullOrEmpty(profileId))
    // mp = pc.create(ImejiFactory.newProfile(), u);
    // set reference to existed profile
    if (!isNullOrEmpty(profileId)) {
      try {
        mp = pc.retrieve(ObjectHelper.getURI(MetadataProfile.class, profileId), u);
      } catch (ImejiException e) {
        throw new UnprocessableError(
            "Can not find the metadata profile you have referenced in the JSON body");

      }
    }

    CollectionImeji vo = new CollectionImeji();
    transferCollection(to, vo, CREATE, u);

    URI collectionURI = null;
    collectionURI = cc.create(vo, mp, u, cc.getProfileCreationMethod(method), null);
    return read(CommonUtils.extractIDFromURI(collectionURI), u);
  }

  @Override
  public CollectionTO update(CollectionTO to, User u) throws ImejiException {

    ProfileController pc = new ProfileController();
    CollectionController cc = new CollectionController();

    CollectionImeji vo = getCollectionVO(cc, to.getId(), u);
    MetadataProfile originalMp = pc.retrieve(vo.getProfile(), u);
    String hasStatements =
        originalMp.getStatements().size() > 0 ? " Existing metadata profile has already defined metadata elements. It is not allowed to update it: remove the profileId from your input."
            : "";

    // profile is defined
    CollectionProfileTO profTO = to.getProfile();
    String profileId = (profTO != null) ? profTO.getId() : "";
    String method = (profTO != null) ? profTO.getMethod() : "";

    MetadataProfile mp = null;

    transferCollection(to, vo, UPDATE, u);
    // profileId is filled
    if (!isNullOrEmpty(profileId)) {
      try {
        mp = pc.retrieve(profileId, u);
      } catch (ImejiException e) {
        throw new UnprocessableError(
            "Can not retrieve the metadata profile provided in the JSON body with id: " + profileId
                + hasStatements);
      }

      if (!profileId.equals(originalMp.getIdString())) {

        if (!METHOD.COPY.toString().equals(method) && !METHOD.REFERENCE.toString().equals(method)) {
          throw new BadRequestException("Wrong metadata profile update method: " + method
              + " ! Allowed values are {copy, reference}. ");
        }

        // if the original profile already has statements, no profile
        // update is allowed
        if (originalMp.getStatements().size() > 0) {
          throw new UnprocessableError(
              "It is not allowed to update related metadata profile which has already defined metadata elements.");
        }
      }

    }

    CollectionImeji updatedCollection =
        cc.updateWithProfile(vo, mp, u, cc.getProfileCreationMethod(method));
    CollectionTO newTO = new CollectionTO();
    TransferObjectFactory.transferCollection(updatedCollection, newTO);
    return newTO;
  }

  @Override
  public CollectionTO release(String id, User u) throws ImejiException {

    CollectionController controller = new CollectionController();
    CollectionImeji vo = controller.retrieve(ObjectHelper.getURI(CollectionImeji.class, id), u);
    controller.release(vo, u);

    // Now Read the collection and return it back
    return getCollectionTO(controller, id, u);

  }

  @Override
  public boolean delete(String id, User u) throws ImejiException {
    CollectionController controller = new CollectionController();
    CollectionImeji vo = controller.retrieve(ObjectHelper.getURI(CollectionImeji.class, id), u);
    controller.delete(vo, u);
    return true;

  }

  @Override
  public CollectionTO withdraw(String id, User u, String discardComment) throws ImejiException {

    CollectionController controller = new CollectionController();
    CollectionImeji vo = controller.retrieve(ObjectHelper.getURI(CollectionImeji.class, id), u);
    vo.setDiscardComment(discardComment);
    controller.withdraw(vo, u);

    // Now Read the withdrawn collection and return it back
    return getCollectionTO(controller, id, u);
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



}

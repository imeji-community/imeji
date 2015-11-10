package de.mpg.imeji.logic.ingest.controller;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.ingest.mapper.ItemMapperTask;
import de.mpg.imeji.logic.ingest.parser.ItemParser;
import de.mpg.imeji.logic.ingest.validator.ItemContentValidator;
import de.mpg.imeji.logic.util.IdentifierUtil;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.metadata.util.MetadataHelper;

/**
 * Controller to ingest {@link Item}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class IngestItemController {
  private User user;
  private MetadataProfile profile;

  /**
   * Constructor
   * 
   * @param user
   * @param profile
   */
  public IngestItemController(User user, MetadataProfile profile) {
    this.user = user;
    this.setProfile(profile);
  }

  /**
   * Ingest a {@link Item} from its xml {@link File} representation
   * 
   * @param itemListXmlFile
   * @throws SAXException
   * @throws JAXBException
   * @throws NotFoundException
   * @throws Exception
   */
  public void ingest(File itemListXmlFile) throws JAXBException, SAXException, ClassCastException,
      NotFoundException {
    ItemParser ip = new ItemParser();
    List<Item> itemList = ip.parseItemList(itemListXmlFile);
    itemList = copyIngestedMetadataToCurrentItem(itemList);
    try {
      ItemContentValidator.validate(itemList);
      ItemMapperTask im = new ItemMapperTask(itemList);
      im.execute();
      ItemController ic = new ItemController();
      ic.update(im.get(), user);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Copy the {@link MetadataSet} of the ingested {@link Item} into the already existing
   * {@link Item}. This way, we avoid to ingest (i.e overwrite) technical data like creator,
   * checksum, etc.
   * 
   * @param originalList
   * @param ingestedList
   * @return
   * @throws NotFoundException
   * @throws Exception
   */
  private List<Item> copyIngestedMetadataToCurrentItem(List<Item> ingestedList)
      throws NotFoundException {
    List<Item> originalList = new ArrayList<Item>();
    for (Item ingested : ingestedList) {
      Item original;
      try {
        original = retrieveItem(ingested);
        List<MetadataSet> mdsList = new ArrayList<MetadataSet>();
        mdsList.add(copyMetadataIfValid(ingested.getMetadataSet(), original.getMetadataSet()));
        original.setMetadataSets(mdsList);
        originalList.add(original);
      } catch (Exception e) {
        throw new NotFoundException(
            "Item with identifier: "
                + ingested.getId()
                + " could not be found in the system. Please make sure you have the right identifiers and try again.");
      }
    }
    return originalList;
  }

  /**
   * Copy the {@link Metadata} of mds1 into mds2, if: <br/>
   * - the {@link Metadata} is not emtpy <br/>
   * - the {@link Metadata} doens't exist in mds2 <br/>
   * - the {@link Metadata} exists and has the same {@link Statement} in mds2
   * 
   * @param mds1
   * @param mds2
   * @return
   */
  private MetadataSet copyMetadataIfValid(MetadataSet mds1, MetadataSet mds2) {
    List<Metadata> l = new ArrayList<Metadata>();
    for (Metadata md : mds1.getMetadata()) {
      if (!MetadataHelper.isEmpty(md)) {
        Metadata copyTo = findMetadata(md.getId(), mds2);
        if (copyTo == null) {
          // If the metadata doesn't exist, give it a new id
          md.setId(IdentifierUtil.newURI(Metadata.class));
          l.add(md);
        } else if (copyTo != null && copyTo.getStatement().compareTo(md.getStatement()) == 0) {
          l.add(md);
        }
      }
    }
    mds2.setMetadata(l);
    return mds2;
  }

  /**
   * Find the {@link Metadata} wit the given {@link URI} into the given {@link MetadataSet}
   * 
   * @param uri
   * @param mds
   * @return
   */
  private Metadata findMetadata(URI uri, MetadataSet mds) {
    for (Metadata md : mds.getMetadata()) {
      if (md.getId().compareTo(uri) == 0)
        return md;
    }
    return null;
  }

  /**
   * Retrieve the {@link Item} from the database
   * 
   * @param item
   * @return
   * @throws Exception
   */
  private Item retrieveItem(Item item) throws Exception {
    ItemController ic = new ItemController();
    return ic.retrieve(item.getId(), user);
  }

  /**
   * getter
   * 
   * @return
   */
  public MetadataProfile getProfile() {
    return profile;
  }

  /**
   * setter
   * 
   * @param profile
   */
  public void setProfile(MetadataProfile profile) {
    this.profile = profile;
  }
}

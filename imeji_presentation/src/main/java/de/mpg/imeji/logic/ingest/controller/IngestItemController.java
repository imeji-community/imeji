package de.mpg.imeji.logic.ingest.controller;

import java.io.File;
import java.util.List;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.ingest.mapper.ItemMapper;
import de.mpg.imeji.logic.ingest.parser.ItemParser;
import de.mpg.imeji.logic.ingest.validator.ItemContentValidator;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;

/**
 * Controller to ingest {@link Item}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class IngestItemController
{
    private User user;
    private MetadataProfile profile;

    /**
     * Constructor
     * 
     * @param user
     * @param profile
     */
    public IngestItemController(User user, MetadataProfile profile)
    {
        this.user = user;
        this.profile = profile;
    }

    /**
     * Ingest a {@link Item} from its xml {@link File} representation
     * 
     * @param itemListXmlFile
     * @throws Exception
     */
    public void ingest(File itemListXmlFile) throws Exception
    {
        ItemParser ip = new ItemParser();
        List<Item> itemList = ip.parseItemList(itemListXmlFile);
        ItemContentValidator.validate(itemList);
        ItemMapper im = new ItemMapper(itemList);
        ItemController ic = new ItemController(user);
        ic.update(im.getMappedItemObjects());
    }
}

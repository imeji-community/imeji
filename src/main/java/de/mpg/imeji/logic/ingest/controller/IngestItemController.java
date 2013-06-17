package de.mpg.imeji.logic.ingest.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.ingest.mapper.ItemMapperTask;
import de.mpg.imeji.logic.ingest.parser.ItemParser;
import de.mpg.imeji.logic.ingest.validator.ItemContentValidator;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.MetadataSet;
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
        this.setProfile(profile);
    }

    /**
     * Ingest a {@link Item} from its xml {@link File} representation
     * 
     * @param itemListXmlFile
     * @throws SAXException 
     * @throws JAXBException 
     * @throws Exception
     */
    public void ingest(File itemListXmlFile) throws JAXBException, SAXException
    {
        try
        {
        	ItemParser ip = new ItemParser();
            List<Item> itemList = ip.parseItemList(itemListXmlFile);
        	itemList = copyIngestedMetadataToCurrentItem(itemList);
        	/* TODO: This part pertains to the content validator, not finished yet
         	 * ItemContentValidator.validate(itemList);
         	 */
        	ItemMapperTask im = new ItemMapperTask(itemList);   	
        	im.execute();
        	ItemController ic = new ItemController(user);
        	ic.update(im.get(), user);
        }
        catch(Exception e)
        {
        	throw new RuntimeException();
        }
    }

    /**
     * Copy the {@link MetadataSet} of the ingested {@link Item} into the already existing {@link Item}. This way, we
     * avoid to ingest (i.e overwrite) technical data like creator, checksum, etc.
     * 
     * @param originalList
     * @param ingestedList
     * @return
     * @throws Exception
     */
    private List<Item> copyIngestedMetadataToCurrentItem(List<Item> ingestedList) throws Exception
    {
        List<Item> originalList = new ArrayList<Item>();
        for (Item ingested : ingestedList)
        {
            Item original = retrieveItem(ingested);
            original.setMetadataSets(ingested.getMetadataSets());
            originalList.add(original);
        }
        return originalList;
    }

    /**
     * Retrieve the {@link Item} from the database
     * 
     * @param item
     * @return
     * @throws Exception
     */
    private Item retrieveItem(Item item) throws Exception
    {
        ItemController ic = new ItemController(user);
        return ic.retrieve(item.getId());
    }

    /**
     * getter
     * 
     * @return
     */
    public MetadataProfile getProfile()
    {
        return profile;
    }

    /**
     * setter
     * 
     * @param profile
     */
    public void setProfile(MetadataProfile profile)
    {
        this.profile = profile;
    }
}

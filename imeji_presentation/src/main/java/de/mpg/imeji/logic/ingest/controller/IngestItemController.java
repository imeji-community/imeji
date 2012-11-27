package de.mpg.imeji.logic.ingest.controller;

import java.io.File;
import java.util.List;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.ingest.parser.ItemParser;
import de.mpg.imeji.logic.ingest.validator.ItemValidator;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;

public class IngestItemController
{
    private User user;
    private MetadataProfile profile;

    public IngestItemController(User user, MetadataProfile profile)
    {
        this.user = user;
        this.profile = profile;
    }

    public void ingest(File itemListXmlFile) throws Exception
    {
       
        ItemParser ip = new ItemParser();
        List<Item> itemList = ip.parseItemList(itemListXmlFile);
        ItemValidator iv = new ItemValidator();
        iv.valid(itemListXmlFile, profile);
        ItemController ic = new ItemController(user);
        ic.update(itemList);
    }
}

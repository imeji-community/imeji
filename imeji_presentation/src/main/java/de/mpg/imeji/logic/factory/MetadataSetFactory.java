package de.mpg.imeji.logic.factory;

import java.net.URI;
import java.util.UUID;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.presentation.util.PropertyReader;

public class MetadataSetFactory extends ImejiFactory
{
    public static MetadataSet create(Item item, URI profile)
    {
        MetadataSet metadataSet = new MetadataSet();
        try
        {
            //metadataSet.setId(URI.create(PropertyReader.getProperty("escidoc.imeji.instance.url") + "metadataSet/" + UUID.randomUUID()));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        metadataSet.setProfile(profile);
        return metadataSet;
    }
    
    public static MetadataSet create(URI profile)
    {
        MetadataSet metadataSet = new MetadataSet();
        try
        {
            //metadataSet.setId(URI.create(PropertyReader.getProperty("escidoc.imeji.instance.url") + "metadataSet/" + UUID.randomUUID()));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        metadataSet.setProfile(profile);
        return metadataSet;
    }
}

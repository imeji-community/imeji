package de.mpg.imeji.logic.ingest.validator;

import de.mpg.imeji.logic.ingest.factory.ItemSchemaFactory;
import de.mpg.imeji.logic.vo.MetadataProfile;

public class ItemValidator
{
    /**
     * Valid the xml against the profile
     * @param itemListXml
     * @param mdp
     */
    public void valid(String itemListXml, MetadataProfile mdp)
    {
        // This must be changed with the real schema object, according to the chosen parser
        ItemSchemaFactory isf = new ItemSchemaFactory();
        Object itemSchema = isf.create(mdp);
        // If not valid through an exception
    }
}

package de.mpg.imeji.logic.ingest.validator;

import java.io.File;
import de.mpg.imeji.logic.vo.MetadataProfile;

public class ItemValidator
{
    /**
     * Valid the xml against the profile
     * 
     * @param itemListXml
     * @param mdp
     */
    public void valid(File itemListXmlFile, MetadataProfile mdp)
    {
        // This must be changed with the real schema object, according to the chosen parser
        // ItemSchemaFactory isf = new ItemSchemaFactory();
        // Object itemSchema = isf.create(mdp);
        // TODO
        // If not valid through an exception
    }
}

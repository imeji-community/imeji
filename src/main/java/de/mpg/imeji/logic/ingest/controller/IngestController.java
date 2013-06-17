package de.mpg.imeji.logic.ingest.controller;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;

/**
 * Controller for ingest
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class IngestController
{
    private static Logger logger = Logger.getLogger(IngestController.class);
    /**
     * The collection in with the ingest will be done.
     */
    private CollectionImeji collection;
    /**
     * The user doing the ingest
     */
    private User user;

    public IngestController(User user, CollectionImeji collection)
    {
        this.collection = collection;
        this.user = user;
    }

    /**
     * Ingest items and profile. Items schema is validated against the profile
     * 
     * @param itemListXml
     * @param profileXml
     * @throws SAXException 
     * @throws JAXBException 
     * @throws Exception
     */
    public void ingest(File itemListXmlFile, File profileXmlFile) throws JAXBException, SAXException 
    {
        if (profileXmlFile != null)
        {
            IngestProfileController ipc = new IngestProfileController(user);
            ipc.ingest(profileXmlFile, collection.getProfile());
        }
        if (itemListXmlFile != null)
        {
            ProfileController pc = new ProfileController();
            try {
	            MetadataProfile mdp = pc.retrieve(collection.getProfile(), user);
	            IngestItemController iic = new IngestItemController(user, mdp);
	            iic.ingest(itemListXmlFile);
            }
            catch (Exception e)
            {
            	throw new RuntimeException();
            }
        }
    }
}
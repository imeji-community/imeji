package de.mpg.imeji.logic.ingest.controller;

import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;

public class IngestController
{
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
     * @throws Exception
     */
    public void ingest(String itemListXml, String profileXml) throws Exception
    {
        if (profileXml != null)
        {
            IngestProfileController ipc = new IngestProfileController(user);
            ipc.ingest(profileXml);
        }
        ProfileController pc = new ProfileController(user);
        MetadataProfile mdp = pc.retrieve(collection.getProfile());
        IngestItemController iic = new IngestItemController(user, mdp);
        iic.ingest(itemListXml);
    }
}

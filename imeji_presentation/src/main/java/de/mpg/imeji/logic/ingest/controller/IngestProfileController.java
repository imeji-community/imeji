package de.mpg.imeji.logic.ingest.controller;

import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.ingest.parser.ProfileParser;
import de.mpg.imeji.logic.ingest.validator.ProfileValidator;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;

public class IngestProfileController
{
    private User user;

    public IngestProfileController(User user)
    {
        this.user = user;
    }

    public void ingest(String profileXml) throws Exception
    {
        ProfileValidator pv = new ProfileValidator();
        pv.valid(profileXml);
        ProfileParser pp = new ProfileParser();
        MetadataProfile mdp = pp.parse(profileXml);
        ProfileController pc = new ProfileController(user);
        pc.update(mdp);
    }
}

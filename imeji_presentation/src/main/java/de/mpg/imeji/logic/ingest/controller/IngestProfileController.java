package de.mpg.imeji.logic.ingest.controller;

import java.io.File;

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

    public void ingest(File profileXmlFile) throws Exception
    {
        ProfileValidator pv = new ProfileValidator();
        pv.valid(profileXmlFile);
        ProfileParser pp = new ProfileParser();
        MetadataProfile mdp = pp.parse(profileXmlFile);
        ProfileController pc = new ProfileController(user);
        pc.update(mdp);
    }
}

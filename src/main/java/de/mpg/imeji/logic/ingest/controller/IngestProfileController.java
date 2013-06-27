package de.mpg.imeji.logic.ingest.controller;

import java.io.File;
import java.net.URI;

import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.ingest.parser.ProfileParser;
import de.mpg.imeji.logic.util.IdentifierUtil;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;

/**
 * Controller to ingest a {@link MetadataProfile}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class IngestProfileController
{
    private User user;

    /**
     * Constructor
     * 
     * @param user
     */
    public IngestProfileController(User user)
    {
        this.user = user;
    }

    /**
     * Ingest a {@link MetadataProfile} as defined in an xml {@link File}
     * 
     * @param profileXmlFile
     * @throws Exception
     */
    public void ingest(File profileXmlFile, URI profile) throws Exception
    {
        ProfileParser pp = new ProfileParser();
        MetadataProfile mdp = pp.parse(profileXmlFile);
        if (isCopyOfOther(mdp, profile))
        {
            changeStatementURI(mdp);
        }
        ProfileController pc = new ProfileController();
        MetadataProfile original = pc.retrieve(profile, user);
        original.setStatements(mdp.getStatements());
        try
        {
            pc.update(mdp, user);
        }
        catch (Exception e)
        {
            throw new RuntimeException();
        }
        pc.removeMetadataWithoutStatement();
    }

    /**
     * Change the {@link URI} of all {@link Statement}, to avoid to overwrite the orginal {@link Statement}
     * 
     * @param mdp
     * @return
     */
    private MetadataProfile changeStatementURI(MetadataProfile mdp)
    {
        for (Statement st : mdp.getStatements())
        {
            st.setId(IdentifierUtil.newURI(Statement.class));
        }
        return mdp;
    }

    /**
     * True if the {@link URI} is different to the {@link URI} of the {@link MetadataProfile}. In that case, the
     * ingested file is a copy of anther existing profile
     * 
     * @param mdp
     * @param uri
     * @return
     */
    private boolean isCopyOfOther(MetadataProfile mdp, URI uri)
    {
        return uri.compareTo(mdp.getId()) != 0;
    }
}
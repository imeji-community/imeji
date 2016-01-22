package de.mpg.imeji.logic.ingest.controller;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.ingest.parser.ProfileParser;
import de.mpg.imeji.logic.util.IdentifierUtil;
import de.mpg.imeji.logic.vo.CollectionImeji;
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
public class IngestProfileController {
  private User user;

  /**
   * Constructor
   * 
   * @param user
   */
  public IngestProfileController(User user) {
    this.user = user;
  }

  /**
   * Ingest a {@link MetadataProfile} as defined in an xml {@link File}
   * 
   * @param profileXmlFile
   * @throws Exception
   */
  public void ingest(File profileXmlFile, CollectionImeji collection) throws Exception {
    URI profile = collection.getProfile();
    ProfileParser pp = new ProfileParser();
    MetadataProfile mdp = pp.parse(profileXmlFile);
    MetadataProfile original;
    ProfileController pc = new ProfileController();
    if (profile != null ){
        if (isCopyOfOther(mdp, profile)) {
          changeStatementURI(mdp);
        }
        original = pc.retrieve(profile, user);
    }
    else 
    {
      original = new MetadataProfile();
    }
    
    original.setStatements(mdp.getStatements());
    // ingested profile can never be default profile
    original.setDefault(false);
    try {
      if (profile != null ) {
          pc.update(original, user);
          pc.removeMetadataWithoutStatement(original);
      }
      else
      {
        if (mdp.getTitle()!= null && mdp.getTitle() != ""  ) {
          original.setTitle(mdp.getTitle());
        }
        else
        {
          original.setTitle("Metadata profile for "+collection.getMetadata().getTitle()+" (ingested at:  "+new Date(System.currentTimeMillis())+" )");
        }
        original.setDescription(mdp.getDescription());
        mdp = pc.create(original, user);
        CollectionController cc = new CollectionController();
        collection.setProfile(mdp.getId());
        cc.update(collection, user);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    
  }

  /**
   * Change the {@link URI} of all {@link Statement}, to avoid to overwrite the orginal
   * {@link Statement}
   * 
   * @param mdp
   * @return
   */
  private MetadataProfile changeStatementURI(MetadataProfile mdp) {
    HashMap<URI, URI> idMap = new HashMap<URI, URI>();
    for (Statement st : mdp.getStatements()) {
      URI oldURI = st.getId();
      st.setId(IdentifierUtil.newURI(Statement.class));
      idMap.put(oldURI, st.getId());
    }
    return changeParentId(mdp, idMap);
  }

  /**
   * Change the {@link URI} of the parent {@link Statement} with the newly created ids
   * 
   * @param mdp
   * @param idMap
   * @return
   */
  private MetadataProfile changeParentId(MetadataProfile mdp, HashMap<URI, URI> idMap) {
    for (Statement st : mdp.getStatements()) {
      if (st.getParent() != null) {
        if (idMap.get(st.getParent()) == null)
          throw new RuntimeException("Unknown parent " + st.getParent() + " in current profile");
        st.setParent(idMap.get(st.getParent()));
      }
    }
    return mdp;
  }

  /**
   * True if the {@link URI} is different to the {@link URI} of the {@link MetadataProfile}. In that
   * case, the ingested file is a copy of anther existing profile
   * 
   * @param mdp
   * @param uri
   * @return
   */
  private boolean isCopyOfOther(MetadataProfile mdp, URI uri) {
    if ( mdp == null || uri ==null) {
      return false;
    }
    else
    {
      return uri.compareTo(mdp.getId()) != 0;
    }
    
  }
}

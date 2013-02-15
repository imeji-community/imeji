/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.util;

import java.net.URI;

import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.session.SessionBean;

/**
 * If an object is already in a session, return it. <br/>
 * Increase performance compared to {@link ObjectLoader}
 * 
 * @author saquet
 */
public class ObjectCachedLoader
{
    /**
     * Load a {@link MetadataProfile} from the session if possible, otherwise from jena
     * 
     * @param uri
     * @return
     */
    public static MetadataProfile loadProfile(URI uri)
    {
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        MetadataProfile profile = sessionBean.getProfileCached().get(uri);
        if (profile == null)
        {
            profile = ObjectLoader.loadProfile(uri, sessionBean.getUser());
            sessionBean.getProfileCached().put(profile.getId(), profile);
        }
        return profile;
    }

    /**
     * Load a {@link CollectionImeji} from the session if possible, otherwise from jena
     * 
     * @param uri
     * @param user
     * @return
     */
    public static CollectionImeji loadCollection(URI uri)
    {
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        CollectionImeji collection = sessionBean.getCollectionCached().get(uri);
        if (collection == null)
        {
            collection = ObjectLoader.loadCollection(uri, sessionBean.getUser());
            sessionBean.getCollectionCached().put(collection.getId(), collection);
        }
        return collection;
    }
}

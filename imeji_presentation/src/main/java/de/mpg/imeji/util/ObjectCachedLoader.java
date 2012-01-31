/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.util;

import java.net.URI;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.jena.vo.MetadataProfile;

/**
 * If an object is already in a session, return it.
 * <br/> Increase performance compared to {@link ObjectLoader}
 * @author saquet
 *
 */
public class ObjectCachedLoader
{
	public static MetadataProfile loadProfile(URI uri)
	{
		SessionBean sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		MetadataProfile profile = sessionBean.getProfileCached().get(uri);
		if (profile == null)
		{
			profile = ObjectLoader.loadProfile(uri, sessionBean.getUser());
		}
		return profile;
	}
	
}

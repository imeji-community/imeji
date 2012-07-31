/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.util;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata.Types;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.util.MetadataTypesHelper;
import de.mpg.imeji.presentation.beans.SessionBean;

public class ProfileHelper
{
    public static String getDefaultVocabulary(URI uri)
    {
        if (Types.CONE_PERSON.equals(MetadataTypesHelper.getTypesForNamespace(uri.toString())))
        {
            return "http://dev-pubman.mpdl.mpg.de/cone/persons/query";
        }
        else if (Types.LICENSE.equals(MetadataTypesHelper.getTypesForNamespace(uri.toString())))
        {
            return "http://dev-pubman.mpdl.mpg.de/cone/cclicenses/query";
        }
        return "http://example.com";
    }

    public static Map<URI, MetadataProfile> loadProfiles(List<Item> imgs) throws Exception
    {
        Map<URI, MetadataProfile> pMap = new HashMap<URI, MetadataProfile>();
        for (Item im : imgs)
        {
            if (pMap.get(im.getMetadataSet().getProfile()) == null)
            {
            	pMap.put(im.getMetadataSet().getProfile(), ObjectCachedLoader.loadProfile(im.getMetadataSet().getProfile()));
            }
        }
        return pMap;
    }

    public static Map<URI, CollectionImeji> loadCollections(List<Item> imgs) throws Exception
    {
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        Map<URI, CollectionImeji> pMap = new HashMap<URI, CollectionImeji>();
        for (Item im : imgs)
        {
            if (!pMap.containsKey(im.getCollection()))
            {
            	CollectionImeji coll = ObjectLoader.loadCollectionLazy(im.getCollection(), sb.getUser());
                pMap.put(coll.getId(), coll);
            }
        }
        return pMap;
    }

    public static Statement loadStatement(Item item, String statementName)
    {
    	SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    	MetadataProfile profile = ObjectLoader.loadProfile(item.getMetadataSet().getProfile(), sb.getUser());
    	for (Statement st : profile.getStatements()) 
    	{
    		if (statementName.equals(st.getId())) 
    		{
    			return st;
			}
		}
    	return null;
    }
}

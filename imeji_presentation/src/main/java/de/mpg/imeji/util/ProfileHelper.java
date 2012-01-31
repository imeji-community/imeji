/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.util;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.ComplexType.ComplexTypes;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.complextypes.util.ComplexTypeHelper;

public class ProfileHelper
{
    public static String getDefaultVocabulary(URI uri)
    {
        if (ComplexTypes.PERSON.equals(ComplexTypeHelper.getComplexType(uri)))
        {
            return "http://dev-pubman.mpdl.mpg.de/cone/persons/query";
        }
        else if (ComplexTypes.LICENSE.equals(ComplexTypeHelper.getComplexType(uri)))
        {
            return "http://dev-pubman.mpdl.mpg.de/cone/cclicenses/query";
        }
        return "http://example.com";
    }

    public static Map<URI, MetadataProfile> loadProfiles(List<Image> imgs) throws Exception
    {
        Map<URI, MetadataProfile> pMap = new HashMap<URI, MetadataProfile>();
        for (Image im : imgs)
        {
            if (pMap.get(im.getMetadataSet().getProfile()) == null)
            {
            	pMap.put(im.getMetadataSet().getProfile(), ObjectCachedLoader.loadProfile(im.getMetadataSet().getProfile()));
            }
        }
        return pMap;
    }

    public static Map<URI, CollectionImeji> loadCollections(List<Image> imgs) throws Exception
    {
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        Map<URI, CollectionImeji> pMap = new HashMap<URI, CollectionImeji>();
        for (Image im : imgs)
        {
            if (!pMap.containsKey(im.getCollection()))
            {
            	CollectionImeji coll = ObjectLoader.loadCollection(im.getCollection(), sb.getUser());
                pMap.put(coll.getId(), coll);
            }
        }
        return pMap;
    }

    public static Statement loadStatement(Image image, String statementName)
    {
    	SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
    	MetadataProfile profile = ObjectLoader.loadProfile(image.getMetadataSet().getProfile(), sb.getUser());
    	for (Statement st : profile.getStatements()) 
    	{
    		if (statementName.equals(st.getName())) 
    		{
    			return st;
			}
		}
    	return null;
    }
}

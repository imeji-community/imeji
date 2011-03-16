package de.mpg.imeji.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thewebsemantic.LocalizedString;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.metadata.MetadataBean;
import de.mpg.imeji.metadata.MetadataBean.MdField;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.ComplexType.ComplexTypes;
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

    public static Map<URI, MetadataProfile> loadProfiles(List<Image> imgs)
    {
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        ProfileController pc = new ProfileController(sb.getUser());
        Map<URI, MetadataProfile> pMap = new HashMap<URI, MetadataProfile>();
        for (Image im : imgs)
        {
            if (pMap.get(im.getMetadataSet().getProfile()) == null)
				try 
            	{
					pMap.put(im.getMetadataSet().getProfile(), pc.retrieve(im.getMetadataSet().getProfile()));
				} 
            	catch (Exception e) 
            	{
					e.printStackTrace();
				}
        }
        return pMap;
    }

    public static Map<URI, CollectionImeji> loadCollections(List<Image> imgs)
    {
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        CollectionController c = new CollectionController(sb.getUser());
        Map<URI, CollectionImeji> pMap = new HashMap<URI, CollectionImeji>();
        for (Image im : imgs)
        {
            if (!pMap.containsKey(im.getCollection()))
            {
                CollectionImeji coll = c.retrieve(im.getCollection());
                pMap.put(coll.getId(), coll);
            }
        }
        return pMap;
    }

    public static MetadataProfile loadProfile(Image image)
    {
        MetadataProfile profile = new MetadataProfile();
        SessionBean sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        ProfileController pc = new ProfileController(sb.getUser());
        try {
			profile = pc.retrieve(image.getMetadataSet().getProfile());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collections.sort((List<Statement>) profile.getStatements());
        return profile;
    }
    
    public static Statement loadStatement(Image image, String statementName)
    {
    	MetadataProfile profile = loadProfile(image);
    	for (Statement st : profile.getStatements()) 
    	{
    		if (statementName.equals(st.getName())) 
    		{
    			return st;
			}
		}
    	return null;
    }

//    public static List<ComplexType> getComplextTypes(Map<URI, MetadataProfile> pMap)
//    {
//        List<ComplexType> cts = new ArrayList<ComplexType>();
//        for (MetadataProfile mdp : pMap.values())
//        {
//            for (Statement s : mdp.getStatements())
//            {
//                cts.add(ComplexTypeHelper.newComplexType(s.getType()));
//            }
//        }
//        return cts;
//    }

}

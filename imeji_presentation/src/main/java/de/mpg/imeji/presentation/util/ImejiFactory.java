/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.ContainerMetadata;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.Properties;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.album.AlbumBean;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.collection.CollectionListItem;
import de.mpg.imeji.presentation.collection.ViewCollectionBean;
import de.mpg.imeji.presentation.image.ImageBean;
import de.mpg.imeji.presentation.image.ThumbnailBean;
import de.mpg.j2j.misc.LocalizedString;

public class ImejiFactory
{
	private static Logger logger = Logger.getLogger(ImejiFactory.class);
	
    public static CollectionImeji newCollection()
    {
        CollectionImeji coll = new CollectionImeji();
        coll.setMetadata(newContainerMetadata());
        return coll;
    }

    public static ContainerMetadata newContainerMetadata()
    {
        ContainerMetadata cm = new ContainerMetadata();
        return cm;
    }

//    public static Properties newProperties()
//    {
//        Properties props = new Properties();
//        return props;
//    }

    public static Statement newStatement()
    {
        Statement s = new Statement();
        s.getLabels().add(new LocalizedString("", null));
        return s;
    }

    public static Person newPerson()
    {
        Person pers = new Person();
        pers.setAlternativeName("");
        pers.setFamilyName("");
        pers.setGivenName("");
        pers.setIdentifier("");
        pers.getOrganizations().add(newOrganization());
        return pers;
    }

    public static Organization newOrganization()
    {
        Organization org = new Organization();
        org.setName("");
        return org;
    }
    
    public static List<CollectionListItem> collectionListToListItem(Collection<CollectionImeji> collList, User user)
    {
    	List<CollectionListItem> l = new ArrayList<CollectionListItem>();
    	for(CollectionImeji c : collList)
    	{
    		l.add(new CollectionListItem(c, user));
    	}
    	return l;
    }

    public static List<ViewCollectionBean> collectionListToBeanList(Collection<CollectionImeji> collList)
    {
        List<ViewCollectionBean> beanList = new ArrayList<ViewCollectionBean>();
        for (CollectionImeji coll : collList)
        {
            beanList.add(new ViewCollectionBean(coll));
        }
        return beanList;
    }

    public static List<AlbumBean> albumListToBeanList(Collection<Album> albumList)
    {
        List<AlbumBean> beanList = new ArrayList<AlbumBean>();
        for (Album album : albumList)
        {
            beanList.add(new AlbumBean(album));
        }
        return beanList;
    }
    
    public static List<ThumbnailBean> imageListToThumbList(Collection<Item> imgList)
    {
        List<ThumbnailBean> beanList = new ArrayList<ThumbnailBean>();
        try 
        {
			((SessionBean)BeanHelper.getSessionBean(SessionBean.class))
				.setProfileCached(ProfileHelper.loadProfiles((List<Item>) imgList));
		} 
        catch (Exception e) 
        {
        	logger.error("Error loading profiles", e);
		}
        for (Item img : imgList)
        {
        	try 
        	{
        		beanList.add(new ThumbnailBean(img));
			} 
        	catch (Exception e) 
			{
        		logger.error("Error creating ThumbnailBean list", e);
			} 
        }
        return beanList;
    }

    public static List<ImageBean> imageListToBeanList(Collection<Item> imgList)
    {
        List<ImageBean> beanList = new ArrayList<ImageBean>();
        try 
        {
			((SessionBean)BeanHelper.getSessionBean(SessionBean.class))
				.setProfileCached(ProfileHelper.loadProfiles((List<Item>) imgList));
		} 
        catch (Exception e1) 
        {
			e1.printStackTrace();
		}
        for (Item img : imgList)
        {
        	try 
        	{
        		beanList.add(new ImageBean(img));
			} 
        	catch (Exception e) 
			{
				e.printStackTrace();
			} 
        }
        return beanList;
    }
}

package de.mpg.imeji.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import thewebsemantic.LocalizedString;
import de.mpg.imeji.album.AlbumBean;
import de.mpg.imeji.collection.ViewCollectionBean;
import de.mpg.imeji.image.ImageBean;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.ContainerMetadata;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Organization;
import de.mpg.jena.vo.Person;
import de.mpg.jena.vo.Properties;
import de.mpg.jena.vo.Statement;

public class ImejiFactory
{
    public static CollectionImeji newCollection()
    {
        CollectionImeji coll = new CollectionImeji();
        coll.setMetadata(newContainerMetadata());
        coll.setProperties(newProperties());
        coll.setProfile(new MetadataProfile());
        return coll;
    }

    public static ContainerMetadata newContainerMetadata()
    {
        ContainerMetadata cm = new ContainerMetadata();
        return cm;
    }

    public static Properties newProperties()
    {
        Properties props = new Properties();
        return props;
    }

    public static Statement newStatement()
    {
        Statement s = new Statement();
        s.getLabels().add(new LocalizedString("", ""));
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

    public static List<ImageBean> imageListToBeanList(Collection<Image> imgList)
    {
        List<ImageBean> beanList = new ArrayList<ImageBean>();
        for (Image img : imgList)
        {
        	beanList.add(new ImageBean(img));
        }
        return beanList;
    }
}

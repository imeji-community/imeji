/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.ContainerMetadata;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Item.Visibility;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.album.AlbumBean;
import de.mpg.imeji.presentation.collection.CollectionListItem;
import de.mpg.imeji.presentation.collection.ViewCollectionBean;
import de.mpg.imeji.presentation.image.ThumbnailBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.j2j.misc.LocalizedString;

/**
 * Create objects ready to be displayed in JSF
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class ImejiFactory
{
    private static Logger logger = Logger.getLogger(ImejiFactory.class);

    public static CollectionImeji newCollection()
    {
        CollectionImeji coll = new CollectionImeji();
        coll.setMetadata(newContainerMetadata());
        return coll;
    }

    public static MetadataProfile newProfile()
    {
        MetadataProfile p = new MetadataProfile();
        return p;
    }

    public static ContainerMetadata newContainerMetadata()
    {
        ContainerMetadata cm = new ContainerMetadata();
        cm.getPersons().add(newPerson());
        return cm;
    }

    /**
     * Crate a new emtpy {@link Statement}
     * 
     * @return
     */
    public static Statement newStatement()
    {
        Statement s = new Statement();
        s.getLabels().add(new LocalizedString("", null));
        return s;
    }

    /**
     * Create an emtpy {@link Statement} as a child of another {@link Statement}
     * 
     * @param parent
     * @param isFirstChild
     * @return
     */
    public static Statement newStatement(URI parent)
    {
        Statement s = newStatement();
        s.setParent(parent);
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

    public static MetadataSet newMetadataSet(URI profile)
    {
        MetadataSet mds = new MetadataSet();
        mds.setProfile(profile);
        return mds;
    }

    /**
     * Create a new emtpy {@link Item}
     * 
     * @param collection
     * @return
     */
    public static Item newItem(CollectionImeji collection)
    {
        Item item = new Item();
        if (collection == null || collection.getId() == null)
        {
            throw new RuntimeException("Can not create item with a collection null");
        }
        item.setCollection(collection.getId());
        item.getMetadataSets().add(newMetadataSet(collection.getProfile()));
        return item;
    }

    /**
     * Factory Method used during the upload
     * 
     * @param collection
     * @param user
     * @param storageId
     * @param title
     * @param fullImageURI
     * @param thumbnailURI
     * @param webURI
     * @return
     */
    public static Item newItem(CollectionImeji collection, User user, String storageId, String title, URI fullImageURI,
            URI thumbnailURI, URI webURI)
    {
        Item item = ImejiFactory.newItem(collection);
        item.setFullImageUrl(fullImageURI);
        item.setThumbnailImageUrl(thumbnailURI);
        item.setWebImageUrl(webURI);
        item.setVisibility(Visibility.PUBLIC);
        item.setFilename(title);
        if (storageId != null)
        {
            item.setStorageId(storageId);
        }
        if (collection.getStatus() == Status.RELEASED)
        {
            item.setStatus(Status.RELEASED);
        }
        return item;
    }

    public static List<CollectionListItem> collectionListToListItem(Collection<CollectionImeji> collList, User user)
    {
        List<CollectionListItem> l = new ArrayList<CollectionListItem>();
        for (CollectionImeji c : collList)
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

    /**
     * Transform a {@link List} of {@link Item} to a {@link List} of {@link ThumbnailBean}
     * 
     * @param itemList
     * @return
     */
    public static List<ThumbnailBean> imageListToThumbList(Collection<Item> itemList)
    {
        List<ThumbnailBean> beanList = new ArrayList<ThumbnailBean>();
        try
        {
            ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).setProfileCached(ProfileHelper
                    .loadProfiles(new ArrayList<Item>(itemList)));
        }
        catch (Exception e)
        {
            logger.error("Error loading profiles", e);
        }
        for (Item img : itemList)
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
}

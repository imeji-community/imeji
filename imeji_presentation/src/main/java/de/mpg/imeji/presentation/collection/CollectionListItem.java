/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import java.net.URI;

import org.apache.log4j.Logger;

import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.security.Security;
import de.mpg.imeji.logic.security.Operations.OperationsType;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Item of the collections page.
 * 
 * @author saquet
 */
public class CollectionListItem
{
    private String title = "";
    private String description = "";
    private String authors = "";
    private int size = 0;
    private String status = Status.PENDING.toString();
    private String id = null;
    private URI uri = null;
    private String discardComment = "";
    private boolean selected = false;
    // dates
    private String creationDate = null;
    private String lastModificationDate = null;
    private String versionDate = null;
    // security
    private boolean visible = false;
    private boolean deletable = false;
    private boolean editable = false;
    private static Logger logger = Logger.getLogger(CollectionListItem.class);

    public CollectionListItem(CollectionImeji collection, User user)
    {
        try
        {
            title = collection.getMetadata().getTitle();
            description = collection.getMetadata().getDescription();
            if (description != null && description.length() > 100)
            {
                description = description.substring(0, 100) + "...";
            }
            for (Person p : collection.getMetadata().getPersons())
            {
                if (!"".equals(authors))
                    authors += ", ";
                authors += p.getFamilyName() + " " + p.getGivenName();
            }
            uri = collection.getId();
            setId(ObjectHelper.getId(uri));
            status = collection.getProperties().getStatus().toString();
            discardComment = collection.getProperties().getDiscardComment();
            creationDate = collection.getProperties().getCreated().toString();
            lastModificationDate = collection.getProperties().getModified().toString();
            if (collection.getProperties().getVersionDate() != null)
            {
                versionDate = collection.getProperties().getVersionDate().toString();
            }
            // initializations
            initSize(user);
            initSelected();
            initSecurity(collection, user);
        }
        catch (Exception e)
        {
            logger.error("Error creating collectionListItem", e);
        }
    }

    private void initSecurity(CollectionImeji collection, User user)
    {
        Security security = new Security();
        visible = security.check(OperationsType.READ, user, collection);
        deletable = security.check(OperationsType.DELETE, user, collection);
        editable = security.check(OperationsType.UPDATE, user, collection);
    }

    private void initSize(User user)
    {
        ItemController ic = new ItemController(user);
        size = ic.countImagesInContainer(uri, new SearchQuery());
    }

    private void initSelected()
    {
        if (((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getSelectedCollections().contains(uri))
            selected = true;
        else
            selected = false;
    }

    public String release()
    {
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        CollectionController cc = new CollectionController(sessionBean.getUser());
        try
        {
            cc.release(cc.retrieve(uri));
            BeanHelper.info(sessionBean.getMessage("success_collection_release"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_collection_release"));
            logger.error(sessionBean.getMessage("error_collection_release"), e);
        }
        return "pretty:";
    }

    public String delete()
    {
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        CollectionController cc = new CollectionController(sessionBean.getUser());
        try
        {
            cc.delete(cc.retrieve(uri), sessionBean.getUser());
            BeanHelper.info(sessionBean.getMessage("success_collection_delete"));
        }
        catch (Exception e)
        {
            BeanHelper.error(sessionBean.getMessage("error_collection_delete"));
            logger.error(sessionBean.getMessage("error_collection_delete"), e);
        }
        return "pretty:collections";
    }

    public String withdraw()
    {
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        CollectionController cc = new CollectionController(sessionBean.getUser());
        if ("".equals(discardComment.trim()))
        {
            BeanHelper.error(sessionBean.getMessage("error_collection_withdraw"));
            BeanHelper.error(sessionBean.getMessage("error_collection_withdraw_discardcomment"));
        }
        else
        {
            try
            {
                CollectionImeji c = cc.retrieve(uri);
                c.getProperties().setDiscardComment(discardComment);
                cc.withdraw(c);
                BeanHelper.info(sessionBean.getMessage("success_collection_withdraw"));
            }
            catch (Exception e)
            {
                BeanHelper.error(sessionBean.getMessage("error_collection_withdraw"));
                logger.error(sessionBean.getMessage("error_collection_withdraw"), e);
            }
        }
        return "pretty:";
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAuthors()
    {
        return authors;
    }

    public void setAuthors(String authors)
    {
        this.authors = authors;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDiscardComment()
    {
        return discardComment;
    }

    public void setDiscardComment(String discardComment)
    {
        this.discardComment = discardComment;
    }

    public String getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(String creationDate)
    {
        this.creationDate = creationDate;
    }

    public String getLastModificationDate()
    {
        return lastModificationDate;
    }

    public void setLastModificationDate(String lastModificationDate)
    {
        this.lastModificationDate = lastModificationDate;
    }

    public String getVersionDate()
    {
        return versionDate;
    }

    public void setVersionDate(String versionDate)
    {
        this.versionDate = versionDate;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        SessionBean sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        if (selected)
        {
            if (!(sessionBean.getSelectedCollections().contains(uri)))
            {
                sessionBean.getSelectedCollections().add(uri);
            }
        }
        else
            sessionBean.getSelectedCollections().remove(uri);
        this.selected = selected;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    public boolean isDeletable()
    {
        return deletable;
    }

    public void setDeletable(boolean deletable)
    {
        this.deletable = deletable;
    }

    public boolean isEditable()
    {
        return editable;
    }

    public void setEditable(boolean editable)
    {
        this.editable = editable;
    }

    public URI getUri()
    {
        return uri;
    }

    public void setUri(URI uri)
    {
        this.uri = uri;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
}

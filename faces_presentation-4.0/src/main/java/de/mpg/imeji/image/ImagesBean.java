package de.mpg.imeji.image;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.mpg.imeji.beans.BasePaginatorListSessionBean;
import de.mpg.imeji.vo.ImageVO;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;

public class ImagesBean extends BasePaginatorListSessionBean<Image>
{
    private ImageController controller;
    private int totalNumberOfRecords;
    private String id = null;
    private String objectClass;
    private URI uri;

    public ImagesBean()
    {
        super();
        controller = new ImageController(null);
    }

    @Override
    public String getNavigationString()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return totalNumberOfRecords;
    }

    @Override
    public List<Image> retrieveList(int offset, int limit)
    {
        if ("collection".equals(objectClass))
        {
            uri = ObjectHelper.getURI(CollectionImeji.class, id);
        }
        if ("album".equals(objectClass))
        {
            uri = ObjectHelper.getURI(Album.class, id);
        }
        // totalNumberOfRecords = controller.search(null, null, null, 0, 0).size();
        Collection<Image> images = controller.search(null, null, null, limit, offset);
        return (LinkedList<Image>)images;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getObjectClass()
    {
        return objectClass;
    }

    public void setObjectClass(String objectClass)
    {
        this.objectClass = objectClass;
    }

    
}

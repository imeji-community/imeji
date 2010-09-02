package de.mpg.imeji.image;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.beans.BasePaginatorListSessionBean;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;

public class ImagesBean extends BasePaginatorListSessionBean<ImageBean>
{
 
    private int totalNumberOfRecords;
    private String id = null;
    private String objectClass;
    private URI uri;
    private SessionBean sb;
    

    public ImagesBean()
    {
        super();
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);

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
    public List<ImageBean> retrieveList(int offset, int limit)
    {
        ImageController controller = new ImageController(sb.getUser());
        
        if ("collection".equals(objectClass))
        {
            uri = ObjectHelper.getURI(CollectionImeji.class, id);
        }
        if ("album".equals(objectClass))
        {
            uri = ObjectHelper.getURI(Album.class, id);
        }
        Collection<Image> images = new ArrayList<Image>();
        
        try
        {
        
            totalNumberOfRecords = controller.search(null, null, -1, offset).size();
            images = controller.search(null, null, limit, offset);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ImejiFactory.imageListToBeanList(images);
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

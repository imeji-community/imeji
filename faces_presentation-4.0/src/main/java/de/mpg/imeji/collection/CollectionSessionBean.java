package de.mpg.imeji.collection;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.vo.list.CollectionListVO;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.ComplexType;
import de.mpg.jena.vo.ComplexType.AllowedTypes;

public class CollectionSessionBean
{
    // Collection active (image browsed are in that collection)
    private CollectionImeji active = null;
    private CollectionListVO collectionList = null;
    private SessionBean sessionBean = null;
    private String selectedMenu = "SORTING";
    private String filter = "all";
    private CollectionListVO collections = null;
    private List<ComplexType> metadataTypes = null;

    public CollectionSessionBean()
    {
        sessionBean = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        try
        {
            init();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error initializing collection session:", e);
        }
    }

    public void init() throws Exception
    {
        active = new CollectionImeji();
        metadataTypes = new ArrayList<ComplexType>();
        for (AllowedTypes t : AllowedTypes.values())
        {
            metadataTypes.add((ComplexType)t.getType().newInstance());
        }
    }
    
    public CollectionListVO getCollectionList()
    {
        return collectionList;
    }

    public void setCollectionList(CollectionListVO collectionList)
    {
        this.collectionList = collectionList;
    }

    /**
     * @return the active
     */
    public CollectionImeji getActive()
    {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(CollectionImeji active)
    {
        this.active = active;
    }

    /**
     * @return the selectedMenu
     */
    public String getSelectedMenu()
    {
        return selectedMenu;
    }

    /**
     * @param selectedMenu the selectedMenu to set
     */
    public void setSelectedMenu(String selectedMenu)
    {
        this.selectedMenu = selectedMenu;
    }

    public String getFilter()
    {
        return filter;
    }

    public void setFilter(String filter)
    {
        this.filter = filter;
    }

    /**
     * @return the collections
     */
    public CollectionListVO getCollections()
    {
        return collections;
    }

    /**
     * @param collections the collections to set
     */
    public void setCollections(CollectionListVO collections)
    {
        this.collections = collections;
    }

    public void setMetadataTypes(List<ComplexType> metadataTypes)
    {
        this.metadataTypes = metadataTypes;
    }

    public List<ComplexType> getMetadataTypes()
    {
        return metadataTypes;
    }
}

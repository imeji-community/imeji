package de.mpg.imeji.collection;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.VocabularyHelper;
import de.mpg.imeji.vo.CollectionVO;
import de.mpg.imeji.vo.MetadataVO;
import de.mpg.imeji.vo.StatementVO;
import de.mpg.imeji.vo.list.CollectionListVO;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.vo.CollectionImeji;

public class CollectionSessionBean
{
    // Collection active (image browsed are in that collection)
    private CollectionVO active = null;
    private CollectionListVO collectionList = null;
    private SessionBean sessionBean = null;
    private String selectedMenu = "SORTING";
    private String filter = "all";
    private CollectionListVO collections = null;
    private List<StatementVO> mdVocabulary = null;

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
        List<StatementVO> mList = VocabularyHelper.getEtermsVocabulary();
        mList.addAll(VocabularyHelper.getDcTermsVocabulary());
        for (StatementVO st : mList)
        {
            mdVocabulary.add(st);
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
    public CollectionVO getActive()
    {
        return active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(CollectionVO active)
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

    /**
     * @return the mdVocabulary
     */
    public List<StatementVO> getMdVocabulary()
    {
        return mdVocabulary;
    }

    /**
     * @param mdVocabulary the mdVocabulary to set
     */
    public void setMdVocabulary(List<StatementVO> mdVocabulary)
    {
        this.mdVocabulary = mdVocabulary;
    }

    
}

package de.mpg.imeji.image;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.BasePaginatorListSessionBean;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.facet.FacetsBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.vo.util.ImejiFactory;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Album;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;

public class ImagesBean extends BasePaginatorListSessionBean<ImageBean>
{
    private String objectClass;
    private int totalNumberOfRecords;
    private SessionBean sb;
    private List<SelectItem> sortMenu;
    private String selectedSortCriterion;
    private String selectedSortOrder;
    private FacetsBean facets;
    
    private String query;

    public ImagesBean()
    {
        super();
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        initMenus();
    }

    private void initMenus()
    {
        sortMenu = new ArrayList<SelectItem>();
        sortMenu.add(new SelectItem(ImejiNamespaces.PROPERTIES_CREATION_DATE, ImejiNamespaces.PROPERTIES_CREATION_DATE
                .name()));
        sortMenu.add(new SelectItem(ImejiNamespaces.IMAGE_COLLECTION, ImejiNamespaces.IMAGE_COLLECTION.name()));
        //
        sortMenu.add(new SelectItem(ImejiNamespaces.PROPERTIES_LAST_MODIFICATION_DATE,
                ImejiNamespaces.PROPERTIES_LAST_MODIFICATION_DATE.name()));
        selectedSortCriterion = ImejiNamespaces.PROPERTIES_CREATION_DATE.name();
        selectedSortOrder = SortOrder.DESCENDING.name();
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
        Collection<Image> images = new ArrayList<Image>();
        try
        {
            SortCriterion sortCriterion = new SortCriterion();
            sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
            sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
            
            
            List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
            if(query!=null && !query.equals(""))
            {
                scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_TEXT, getQuery(), Filtertype.REGEX));
                scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_NUMBER, getQuery(), Filtertype.REGEX));
                scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_FAMILY_NAME, getQuery(), Filtertype.REGEX));
                scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_GIVEN_NAME, getQuery(), Filtertype.REGEX));
                scList.add(new SearchCriterion(Operator.OR, ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_PERSON_ORGANIZATION_NAME, getQuery(), Filtertype.REGEX));
            }
            
            
            totalNumberOfRecords = controller.search(scList, null, -1, offset).size();
            images = controller.search(scList, sortCriterion, limit, offset);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        this.setFacets(new FacetsBean((List<Image>)images));
        return ImejiFactory.imageListToBeanList(images);
    }

    public List<SelectItem> getSortMenu()
    {
        return sortMenu;
    }

    public void setSortMenu(List<SelectItem> sortMenu)
    {
        this.sortMenu = sortMenu;
    }

    public String getSelectedSortCriterion()
    {
        return selectedSortCriterion;
    }

    public void setSelectedSortCriterion(String selectedSortCriterion)
    {
        this.selectedSortCriterion = selectedSortCriterion;
    }

    public String getSelectedSortOrder()
    {
        return selectedSortOrder;
    }

    public void setSelectedSortOrder(String selectedSortOrder)
    {
        this.selectedSortOrder = selectedSortOrder;
    }

    public String toggleSortOrder()
    {
        if (selectedSortOrder.equals("DESCENDING"))
        {
            selectedSortOrder = "ASCENDING";
        }
        else
        {
            selectedSortOrder = "DESCENDING";
        }
        return getNavigationString();
    }

    public String getObjectClass()
    {
        return objectClass;
    }

    public void setObjectClass(String objectClass)
    {
        this.objectClass = objectClass;
    }

    public FacetsBean getFacets()
    {
        return facets;
    }

    public void setFacets(FacetsBean facets)
    {
        this.facets = facets;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getQuery()
    {
        return query;
    }
}

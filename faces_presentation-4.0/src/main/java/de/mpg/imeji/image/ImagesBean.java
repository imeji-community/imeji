package de.mpg.imeji.image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.BasePaginatorListSessionBean;
import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.facet.FacetsBean;
import de.mpg.imeji.filter.FiltersBean;
import de.mpg.imeji.lang.MetadataLabels;
import de.mpg.imeji.search.URLQueryTransformer;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.jena.ImejiJena;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.vo.Image;

public class ImagesBean extends BasePaginatorListSessionBean<ImageBean>
{
    private int totalNumberOfRecords;
    private SessionBean sb;
    private List<SelectItem> sortMenu;
    private String selectedSortCriterion;
    private String selectedSortOrder;
    private FacetsBean facets;
    protected FiltersBean filters;
    private String query;
    private Navigation navigation;
    protected MetadataLabels labels;
    private List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
    
    public ImagesBean()
    {
        super();
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        labels = (MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class);
        initMenus();
    }

    private void initMenus()
    {
        sortMenu = new ArrayList<SelectItem>();
        sortMenu.add(new SelectItem(ImejiNamespaces.PROPERTIES_CREATION_DATE, sb
                .getLabel(ImejiNamespaces.PROPERTIES_CREATION_DATE.name())));
        sortMenu.add(new SelectItem(ImejiNamespaces.IMAGE_COLLECTION, sb.getLabel(ImejiNamespaces.IMAGE_COLLECTION
                .name())));
        sortMenu.add(new SelectItem(ImejiNamespaces.PROPERTIES_LAST_MODIFICATION_DATE, sb
                .getLabel(ImejiNamespaces.PROPERTIES_LAST_MODIFICATION_DATE.name())));
        selectedSortCriterion = ImejiNamespaces.PROPERTIES_CREATION_DATE.name();
        selectedSortOrder = SortOrder.DESCENDING.name();
    }

    @Override
    public String getNavigationString()
    {
        return "pretty:images";
    }

    @Override
    public int getTotalNumberOfRecords()
    {
        return totalNumberOfRecords;
    }

    @Override
    public List<ImageBean> retrieveList(int offset, int limit) throws Exception
    {
        ImageController controller = new ImageController(sb.getUser());
        Collection<Image> images = new ArrayList<Image>();
        SortCriterion sortCriterion = new SortCriterion();
        sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
        sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
       
        try
        {
            scList = URLQueryTransformer.transform2SCList(query);
//            System.out.println("url:" + query);
//            System.out.println("parsed:" + URLQueryTransformer.transform2URL(scList));
        }
        catch (Exception e)
        {
            BeanHelper.error("Invalid search query!");
        }
        totalNumberOfRecords = controller.getNumberOfResults(scList);
        images = controller.search(scList, sortCriterion, limit, offset);
        
        filters = new FiltersBean(query, totalNumberOfRecords);
        
        labels.init((List<Image>) images);
        
        return ImejiFactory.imageListToBeanList(images);
    }

    public String getImageBaseUrl()
    {
        return navigation.getApplicationUri();
    }
    
    public String getBackUrl() 
    {
		return navigation.getImagesUrl();
	}

    public String initFacets()
    {
    	this.setFacets(new FacetsBean(scList));
    	return "pretty";
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

	public FiltersBean getFilters() {
		return filters;
	}

	public void setFilters(FiltersBean filters) {
		this.filters = filters;
	}
    
    
}

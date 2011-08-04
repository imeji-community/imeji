package de.mpg.imeji.image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import de.mpg.imeji.album.AlbumBean;
import de.mpg.imeji.beans.BasePaginatorListSessionBean;
import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.facet.FacetsBean;
import de.mpg.imeji.filter.FiltersBean;
import de.mpg.imeji.filter.FiltersSession;
import de.mpg.imeji.history.HistorySession;
import de.mpg.imeji.lang.MetadataLabels;
import de.mpg.imeji.search.URLQueryTransformer;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ImejiFactory;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.controller.AlbumController;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.controller.SortCriterion.SortOrder;
import de.mpg.jena.search.SearchResult;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.security.Security;
import de.mpg.jena.vo.CollectionImeji;
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
  
    private List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
    private Collection<Image> images = new ArrayList<Image>();
    private SearchResult searchResult = null;
    
    public ImagesBean()
    {
        super();
        sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        initMenus();
    }

    private void initMenus()
    {
        sortMenu = new ArrayList<SelectItem>();
        sortMenu.add(new SelectItem(ImejiNamespaces.PROPERTIES_CREATION_DATE, sb.getLabel(ImejiNamespaces.PROPERTIES_CREATION_DATE.name())));
        sortMenu.add(new SelectItem(ImejiNamespaces.IMAGE_COLLECTION, sb.getLabel(ImejiNamespaces.IMAGE_COLLECTION.name())));
        sortMenu.add(new SelectItem(ImejiNamespaces.PROPERTIES_LAST_MODIFICATION_DATE, sb.getLabel(ImejiNamespaces.PROPERTIES_LAST_MODIFICATION_DATE.name())));
        selectedSortCriterion = ImejiNamespaces.PROPERTIES_CREATION_DATE.name();
        selectedSortOrder = SortOrder.DESCENDING.name();
    }

    @Override
    public String getNavigationString()
    {
		if(sb.getSelectedImagesContext()!=null && !(sb.getSelectedImagesContext().equals("pretty:images")))
		{
			sb.getSelected().clear();
		}
		sb.setSelectedImagesContext("pretty:images");
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
        if (true || reloadPage())
        {
	        images = new ArrayList<Image>();
	        if (facets != null)
	        {
	        	facets.getFacets().clear(); 
	        }
	        SortCriterion sortCriterion = new SortCriterion();
	        sortCriterion.setSortingCriterion(ImejiNamespaces.valueOf(getSelectedSortCriterion()));
	        sortCriterion.setSortOrder(SortOrder.valueOf(getSelectedSortOrder()));
	       
	        initBackPage();
	        try
	        {
	            scList = URLQueryTransformer.transform2SCList(query);
	        }
	        catch (Exception e)
	        {
	            BeanHelper.error("Invalid search query!");
	        }
			searchResult = controller.searchImages(scList, sortCriterion, limit, offset);
			totalNumberOfRecords = searchResult.getNumberOfRecords();
			searchResult.setQuery(query);
			searchResult.setSort(sortCriterion);
    	}
        images = controller.loadImages(searchResult.getResults(), limit, offset);
        return ImejiFactory.imageListToBeanList(images);
    }
    
    public boolean reloadPage()
    {
    	return searchResult == null 
    	|| UrlHelper.getParameterBoolean("load")
    	|| (query != null && !query.equals(searchResult.getQuery())) 
    	|| searchResult.getSort() == null
    	|| !(ImejiNamespaces.valueOf(getSelectedSortCriterion()).getNs().equals(searchResult.getSort().getSortingCriterion().getNs())
    			&& SortOrder.valueOf(getSelectedSortOrder()).equals(searchResult.getSort().getSortOrder()));
    }
    
    public void initBackPage()
    {
    	HistorySession hs = (HistorySession) BeanHelper.getSessionBean(HistorySession.class);
        FiltersSession fs = (FiltersSession) BeanHelper.getSessionBean(FiltersSession.class);
                
        if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("h") != null)
        {
        	fs.setFilters(hs.getCurrentPage().getFilters());
        	query = hs.getCurrentPage().getQuery();
        }
        else
        {
        	filters = new FiltersBean(query, totalNumberOfRecords);
        	hs.getCurrentPage().setFilters(fs.getFilters());
        	hs.getCurrentPage().setQuery(fs.getWholeQuery());
        }
    }
    
    public String addToActiveAlbum() throws Exception
    {
    	AlbumBean activeAlbum = sb.getActiveAlbum();
        AlbumController ac = new AlbumController(sb.getUser());
        
        int els = getElementsPerPage();
        int page = getCurrentPageNumber();
        
        setElementsPerPage(totalNumberOfRecords);
        setCurrentPageNumber(1);
        
        update();
        
        setElementsPerPage(els);
        setCurrentPageNumber(page);
        
        int count = 0;
        
        for (Image im : getImages())
        {
        	 if (activeAlbum.getAlbum().getImages().contains(im.getId()))
             {
                 BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("image") + " " + im.getFilename() + " " + ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getMessage("already_in_active_album"));       
             }
             else
             {
                 activeAlbum.getAlbum().getImages().add(im.getId());
                 count++;
             }
        }
        try 
        {
        	  ac.update(activeAlbum.getAlbum());
              BeanHelper.info(count + " images added to active album");
              BeanHelper.info(count + " " + ((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getMessage("images_added_to_active_album"));       

		} 
        catch (Exception e) 
        {
			BeanHelper.error(e.getMessage());
			activeAlbum.setAlbum(ac.retrieve(activeAlbum.getAlbum().getId()));
		}

        return "pretty:";
    }
    
    public String deleteAll() throws Exception 
    {
    	ImageController ic = new ImageController(sb.getUser());
    	CollectionController cc = new CollectionController(sb.getUser());
    	CollectionImeji coll = null;
    	
    	if (!images.isEmpty()) coll = cc.retrieve(images.iterator().next().getCollection());
    	int count = 0;
    	for(Image im : images)
    	{
    		try 
    		{
				ic.delete(im, sb.getUser());
				if (coll.getImages().contains(im.getId())) coll.getImages().remove(im.getId());
				count++;
			} 
    		catch (Exception e) 
			{
				BeanHelper.error("Error deleting " + im.getFilename());
				e.printStackTrace();
			}
    	}
    	
    	BeanHelper.info(count + " images deleted.");
    	cc.update(coll);
    	sb.getSelected().clear();
    	
    	return "pretty:";
    }
    
    public String withdrawAll() throws Exception 
    {
    	ImageController ic = new ImageController(sb.getUser());
    	update();   	
    	
    	int count = 0;
    	for(Image im : images)
    	{
    		try 
    		{
				ic.withdraw(im);
				count++;
			} 
    		catch (Exception e) 
			{
				BeanHelper.error("Error withdrawing " + im.getFilename());
				e.printStackTrace();
			}
    	}
    	BeanHelper.info(count + " images deleted.");
    	return "pretty:";
    }

    public String getImageBaseUrl()
    {
        return navigation.getApplicationUri();
    }
    
    public String getBackUrl() 
    {
		return navigation.getImagesUrl();
	}

    public String initFacets() throws Exception
    {
    	scList =  URLQueryTransformer.transform2SCList(query);;
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
	
	public String selectAll() {
		for(ImageBean bean: getCurrentPartList())
		{
			bean.setSelected(true);
			if(!(sb.getSelected().contains(bean.getImage().getId())))
			{
				sb.getSelected().add(bean.getImage().getId());
			}
		}
		return getNavigationString();
	}
	
	public String selectNone()
	{
		sb.getSelected().clear();
		return getNavigationString();
	}
    
	public Collection<Image> getImages() {
		return images;
	}

	public void setImages(Collection<Image> images) {
		this.images = images;
	}
	
	/**
	 * Check that at leat one image is editable
	 */
	public boolean isImageEditable()
	{
		Security security = new Security();
    	for (Image im : getImages())
    	{
    		if (security.check(OperationsType.UPDATE, sb.getUser(), im))
    		{
    			return true;
    		}
    	}
    	return false;
	}
	
	public boolean isImageDeletable()
	{
		Security security = new Security();
    	for (Image im : getImages())
    	{
    		if (security.check(OperationsType.DELETE, sb.getUser(), im))
    		{
    			return true;
    		}
    	}
    	return false;
	}
	
	public boolean isEditable()
    {
    	return false;
    }

	public boolean isVisible() 
	{
		return false;
	}
	
	public boolean isDeletable() 
	{
		return false;
	}

	public SearchResult getSearchResult() {
		return searchResult;
	}

	public void setSearchResult(SearchResult searchResult) {
		this.searchResult = searchResult;
	}
    
}

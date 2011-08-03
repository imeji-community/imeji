package de.mpg.imeji.facet;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.facet.Facet.FacetType;
import de.mpg.imeji.filter.Filter;
import de.mpg.imeji.filter.FiltersSession;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.vo.ComplexType.ComplexTypes;

public class TechnicalFacets 
{
	private SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	private FiltersSession fs = (FiltersSession) BeanHelper.getSessionBean(FiltersSession.class);
	
	private List<Facet> facets = new ArrayList<Facet>();
	
	public TechnicalFacets(List<SearchCriterion> scList) 
	{
		FacetURIFactory uriFactory = new FacetURIFactory(scList);
		Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
		
		String baseURI = nav.getImagesUrl() + "?q=";
		
		try 
		{
			int count = 0;
			if (sb.getUser() != null)
			{	
				if (!fs.isFilter("my_images") && !fs.isNoResultFilter("my_images"))
				{
					SearchCriterion myImagesSC = new SearchCriterion(Operator.AND, ImejiNamespaces.MY_IMAGES , "my", Filtertype.EQUALS);
					count = getCount(new ArrayList<SearchCriterion>(scList), myImagesSC);
					if (count > 0 ) facets.add(new Facet(uriFactory.createFacetURI(baseURI, myImagesSC, "my_images", FacetType.TECHNICAL), "my_images", count, FacetType.TECHNICAL, null));
					else fs.getNoResultsFilters().add(new Filter("My images", "", 0, FacetType.TECHNICAL, null));
				}
				
				if (!fs.isFilter("pending_images") && !fs.isNoResultFilter("pending_images"))
				{
					SearchCriterion privateImagesSC = new SearchCriterion(Operator.AND, ImejiNamespaces.PROPERTIES_STATUS, "http://imeji.mpdl.mpg.de/status/PENDING", Filtertype.URI);
					count = getCount(new ArrayList<SearchCriterion>(scList), privateImagesSC);
					if (count > 0 )facets.add(new Facet(uriFactory.createFacetURI(baseURI, privateImagesSC, "pending_images", FacetType.TECHNICAL), "pending_images",count, FacetType.TECHNICAL, null));
				}
				if (!fs.isFilter("released_images") && !fs.isNoResultFilter("released_images"))
				{
					SearchCriterion publicImagesSC = new SearchCriterion(Operator.AND, ImejiNamespaces.PROPERTIES_STATUS,"http://imeji.mpdl.mpg.de/status/RELEASED", Filtertype.URI);
					count = getCount(new ArrayList<SearchCriterion>(scList), publicImagesSC);
					if (count > 0)	facets.add(new Facet(uriFactory.createFacetURI(baseURI, publicImagesSC, "released_images", FacetType.TECHNICAL), "released_images", count, FacetType.TECHNICAL, null));
				}
			}
				
			for (ComplexTypes ct : ComplexTypes.values())
			{
				if (!fs.isFilter(ct.name()) && !fs.isNoResultFilter(ct.name()))
				{
					SearchCriterion sc = new  SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_TYPE, ct.getURI().toString(), Filtertype.URI);
					count = getCount(new ArrayList<SearchCriterion>(scList), sc);
					if (count > 0)
					{
						facets.add(new Facet(uriFactory.createFacetURI(baseURI, sc, ct.name(), FacetType.TECHNICAL), ct.name().toLowerCase(), count, FacetType.TECHNICAL, null));
					}
					else 
					{
						fs.getNoResultsFilters().add(new Filter(ct.name(), "", 0, FacetType.TECHNICAL, null));
					}
					count = 0;	
				}
			}
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	}
	
	public int getCount(List<SearchCriterion> scList, SearchCriterion sc)
	{
		ImageController ic = new ImageController(sb.getUser());
		scList.add(sc);
		return ic.countImages(scList);
	}

	public List<Facet> getFacets() {
		return facets;
	}

	public void setFacets(List<Facet> facets) {
		this.facets = facets;
	}
	
	
}

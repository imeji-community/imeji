/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.facet;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.vo.SearchIndexes;
import de.mpg.imeji.logic.search.vo.SearchCriterion;
import de.mpg.imeji.logic.search.vo.SearchCriterion.Filtertype;
import de.mpg.imeji.logic.search.vo.SearchCriterion.Operator;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.beans.SessionBean;
import de.mpg.imeji.presentation.facet.Facet.FacetType;
import de.mpg.imeji.presentation.filter.Filter;
import de.mpg.imeji.presentation.filter.FiltersSession;
import de.mpg.imeji.presentation.util.BeanHelper;

public class TechnicalFacets 
{
	private SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	private FiltersSession fs = (FiltersSession) BeanHelper.getSessionBean(FiltersSession.class);
	
	private List<List<Facet>> facets = new ArrayList<List<Facet>>();
	
	public TechnicalFacets(List<SearchCriterion> scList) 
	{
		FacetURIFactory uriFactory = new FacetURIFactory(scList);
		Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
		
		String baseURI = nav.getImagesUrl() + "?q=";
		
		List<Facet> techFacets = new ArrayList<Facet>();
		
		try 
		{
			int count = 0;
			if (sb.getUser() != null)
			{	
				if (!fs.isFilter("my_images") && !fs.isNoResultFilter("my_images"))
				{
					SearchCriterion myImagesSC = new SearchCriterion(Operator.AND, SearchIndexes.MY_IMAGES , "my", Filtertype.EQUALS);
					count = getCount(new ArrayList<SearchCriterion>(scList), myImagesSC);
					if (count > 0 ) techFacets.add(new Facet(uriFactory.createFacetURI(baseURI, myImagesSC, "my_images", FacetType.TECHNICAL), "my_images", count, FacetType.TECHNICAL, null));
					else fs.getNoResultsFilters().add(new Filter("My images", "", 0, FacetType.TECHNICAL, null));
				}
				
				if (!fs.isFilter("pending_images") && !fs.isNoResultFilter("pending_images"))
				{
					SearchCriterion privateImagesSC = new SearchCriterion(Operator.AND, SearchIndexes.PROPERTIES_STATUS, "http://imeji.org/terms/status/PENDING", Filtertype.URI);
					count = getCount(new ArrayList<SearchCriterion>(scList), privateImagesSC);
					if (count > 0 )techFacets.add(new Facet(uriFactory.createFacetURI(baseURI, privateImagesSC, "pending_images", FacetType.TECHNICAL), "pending_images",count, FacetType.TECHNICAL, null));
				}
				if (!fs.isFilter("released_images") && !fs.isNoResultFilter("released_images"))
				{
					SearchCriterion publicImagesSC = new SearchCriterion(Operator.AND, SearchIndexes.PROPERTIES_STATUS,"http://imeji.org/terms/status/RELEASED", Filtertype.URI);
					count = getCount(new ArrayList<SearchCriterion>(scList), publicImagesSC);
					if (count > 0)	techFacets.add(new Facet(uriFactory.createFacetURI(baseURI, publicImagesSC, "released_images", FacetType.TECHNICAL), "released_images", count, FacetType.TECHNICAL, null));
				}
			}

			for (Metadata.Types t : Metadata.Types.values())
			{
				if (!fs.isFilter(t.name()) && !fs.isNoResultFilter(t.name()))
				{
					SearchCriterion sc = new  SearchCriterion(Operator.AND, SearchIndexes.IMAGE_METADATA_TYPE_RDF, t.name(), Filtertype.URI);
					count = getCount(new ArrayList<SearchCriterion>(scList), sc);
					if (count > 0)
					{
						techFacets.add(new Facet(uriFactory.createFacetURI(baseURI, sc, t.name(), FacetType.TECHNICAL), t.name().toLowerCase(), count, FacetType.TECHNICAL, null));
					}
					else 
					{
						fs.getNoResultsFilters().add(new Filter(t.name(), "", 0, FacetType.TECHNICAL, null));
					}
					count = 0;	
				}
			}
			facets.add(techFacets);
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
	}
	
	public int getCount(List<SearchCriterion> scList, SearchCriterion sc)
	{
		ItemController ic = new ItemController(sb.getUser());
		scList.add(sc);
		return ic.countImages(scList);
	}

	public List<List<Facet>> getFacets() 
	{
		return facets;
	}

	public void setFacets(List<List<Facet>> facets) 
	{
		this.facets = facets;
	}
	
	
}

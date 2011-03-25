package de.mpg.imeji.facet;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
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
				if (!fs.isFilter("My images"))
				{
					SearchCriterion myImagesSC = new SearchCriterion(Operator.AND, ImejiNamespaces.MY_IMAGES , "", Filtertype.EQUALS);
					if (count > 0 ) facets.add(new Facet(uriFactory.createFacetURI(baseURI, myImagesSC, "My images"), "My images", getCount(new ArrayList<SearchCriterion>(scList), myImagesSC)));	
				}
				
				if (!fs.isFilter("Pending images"))
				{
					SearchCriterion privateImagesSC = new SearchCriterion(Operator.AND, ImejiNamespaces.PROPERTIES_STATUS, "http://imeji.mpdl.mpg.de/status/PENDING", Filtertype.URI);
					count = getCount(new ArrayList<SearchCriterion>(scList), privateImagesSC);
					if (count > 0 )facets.add(new Facet(uriFactory.createFacetURI(baseURI, privateImagesSC, "Pending images"), "Pending images",count));
				}
				if (!fs.isFilter("Released images"))
				{
					SearchCriterion publicImagesSC = new SearchCriterion(Operator.AND, ImejiNamespaces.PROPERTIES_STATUS,"http://imeji.mpdl.mpg.de/status/RELEASED", Filtertype.URI);
					count = getCount(new ArrayList<SearchCriterion>(scList), publicImagesSC);
					if (count > 0)	facets.add(new Facet(uriFactory.createFacetURI(baseURI, publicImagesSC, "Released images"), "Released images", count));
				}
			}
				
			for (ComplexTypes ct : ComplexTypes.values())
			{
				if (!fs.isFilter(ct.name()))
				{
					SearchCriterion sc = new  SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_TYPE, ct.getURI().toString(), Filtertype.URI);
					count = getCount(new ArrayList<SearchCriterion>(scList), sc);
					if (count > 0) facets.add(new Facet(uriFactory.createFacetURI(baseURI, sc, ct.name()), ct.name().toLowerCase(), count));
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
		try 
		{
			return ic.getNumberOfResults(scList);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return 0;
	}

	public List<Facet> getFacets() {
		return facets;
	}

	public void setFacets(List<Facet> facets) {
		this.facets = facets;
	}
	
	
}

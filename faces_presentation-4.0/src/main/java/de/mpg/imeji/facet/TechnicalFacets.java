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
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.ComplexType.ComplexTypes;
import de.mpg.jena.vo.Image.Visibility;
import de.mpg.jena.vo.User;

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
			if (sb.getUser() != null)
			{	
			
				SearchCriterion myImagesSC = new SearchCriterion(Operator.AND, ImejiNamespaces.PROPERTIES_CREATED_BY , ObjectHelper.getURI(User.class, sb.getUser().getEmail()).toString(), Filtertype.URI);
				facets.add(new Facet(uriFactory.createFacetURI(baseURI, myImagesSC, "My images"), "My images", getCount(new ArrayList<SearchCriterion>(scList), myImagesSC)));
				
				SearchCriterion privateImagesSC = new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_VISIBILITY, "http://imeji.mpdl.mpg.de/image/visibility/PRIVATE", Filtertype.URI);
				facets.add(new Facet(uriFactory.createFacetURI(baseURI, privateImagesSC, "Private images"), "Private images", getCount(new ArrayList<SearchCriterion>(scList), privateImagesSC)));
				
				SearchCriterion publicImagesSC = new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_VISIBILITY,"http://imeji.mpdl.mpg.de/image/visibility/PUBLIC", Filtertype.URI);
				facets.add(new Facet(uriFactory.createFacetURI(baseURI, publicImagesSC, "Public images"), "Public images", getCount(new ArrayList<SearchCriterion>(scList), publicImagesSC)));
			}
				
			for (ComplexTypes ct : ComplexTypes.values())
			{
				if (!fs.isFilter(ct.name()))
				{
					SearchCriterion sc = new  SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_TYPE, ct.getURI().toString(), Filtertype.URI);
					facets.add(new Facet(uriFactory.createFacetURI(baseURI, sc, ct.name()), ct.name().toLowerCase(), getCount(new ArrayList<SearchCriterion>(scList), sc)));
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

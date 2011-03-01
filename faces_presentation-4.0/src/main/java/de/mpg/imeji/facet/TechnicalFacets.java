package de.mpg.imeji.facet;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.vo.ComplexType.ComplexTypes;

public class TechnicalFacets 
{
	private List<Facet> facets = new ArrayList<Facet>();
	private SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	
	public TechnicalFacets(List<SearchCriterion> scList) 
	{
		FacetURIFactory uriFactory = new FacetURIFactory(scList);
		Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
		
		String baseURI = nav.getImagesUrl() + "?q=";
		
		SearchCriterion scText = new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_TYPE, "http://imeji.mpdl.mpg.de/complexTypes/TEXT", Filtertype.URI);
		SearchCriterion scNumber = new SearchCriterion(Operator.AND,ImejiNamespaces.IMAGE_METADATA_TYPE, "http://imeji.mpdl.mpg.de/complexTypes/NUMBER", Filtertype.URI);
		SearchCriterion scURI = new SearchCriterion(Operator.AND,ImejiNamespaces.IMAGE_METADATA_TYPE, "http://imeji.mpdl.mpg.de/complexTypes/URI", Filtertype.URI);
		
		
		
		
		try 
		{
			for (ComplexTypes ct : ComplexTypes.values())
			{
				SearchCriterion sc = new  SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_TYPE, ct.getURI().toString(), Filtertype.URI);
				facets.add(new Facet(uriFactory.createFacetURI(baseURI, sc), ct.name().toLowerCase(), getCount(new ArrayList<SearchCriterion>(scList), sc)));
			}
			
			
//			facets.add(new Facet(uriFactory.createFacetURI(baseURI, scText), "Text", getCount(new ArrayList<SearchCriterion>(scList), scText)));
//			facets.add(new Facet(uriFactory.createFacetURI(baseURI, scNumber), "Number", getCount(new ArrayList<SearchCriterion>(scList), scNumber)));
//			facets.add(new Facet(uriFactory.createFacetURI(baseURI, scURI), "URI", getCount(new ArrayList<SearchCriterion>(scList), scURI)));
			
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

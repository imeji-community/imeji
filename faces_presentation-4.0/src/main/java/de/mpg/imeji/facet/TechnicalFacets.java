package de.mpg.imeji.facet;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;

public class TechnicalFacets 
{
	private List<Facet> facets = new ArrayList<Facet>();
	private SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	
	public TechnicalFacets(List<SearchCriterion> scList) 
	{
		FacetURIFactory uriFactory = new FacetURIFactory(scList);
		Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
		String baseURI = nav.getImagesUrl() + "?q=";
		SearchCriterion scText = new SearchCriterion(ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_TEXT, "[^a-z0-9]+");
		SearchCriterion scNumber = new SearchCriterion(ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE, "NUMBER");
		
		try {
			facets.add(new Facet(uriFactory.createFacetURI(baseURI, scText), "Text", getCount(scList)));
			facets.add(new Facet(uriFactory.createFacetURI(baseURI, scNumber), "Number", 0));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getCount(List<SearchCriterion> scList)
	{
		ImejiNamespaces mdType = ImejiNamespaces.IMAGE_METADATA_COMPLEXTYPE_ENUMTYPE;
		
		ImageController ic = new ImageController(sb.getUser());
		scList.add(new SearchCriterion(mdType, "http://imeji.mpdl.mpg.de/complexTypes/TEXT"));
		
		try {
			return ic.getNumberOfResults(scList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

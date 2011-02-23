package de.mpg.imeji.facet;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.Image;

public class CollectionFacets
{
	private SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	private List<Facet> facets = new ArrayList<Facet>();
	private Map<String, URI> cols = new HashMap<String, URI>();
	
	public CollectionFacets(List<Image> images, List<SearchCriterion> scList) throws Exception 
	{
		for(Image im : images)
		{
			cols.put(im.getCollection().toString(), im.getCollection());
		}
		
		FacetURIFactory fact = new FacetURIFactory(scList);
		Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
		
		for(URI col : cols.values())
		{
            String baseURI = nav.getImagesUrl()  + "/collection" + ObjectHelper.getId(col);
			facets.add(new Facet(fact.createFacetURI(baseURI, null), getCollectionName(col), 0));
		}
	}
	
	
	public String getCollectionName(URI uri)
	{
		return "no name";
	}
	
	public List<Facet> getFacets() {
		return facets;
	}

	public void setFacets(List<Facet> facets) {
		this.facets = facets;
	}
	
	
}

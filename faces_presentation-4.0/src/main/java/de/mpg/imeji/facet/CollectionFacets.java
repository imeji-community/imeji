package de.mpg.imeji.facet;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.lang.MetadataLabels;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class CollectionFacets
{
	private SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	private List<Facet> facets = new ArrayList<Facet>();
	
	
	public CollectionFacets(CollectionImeji col, List<SearchCriterion> scList) throws Exception 
	{
		ProfileController pc = new ProfileController(sb.getUser());
		MetadataProfile profile = pc.retrieve(col.getProfile());
		
		Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
		String baseURI = nav.getImagesUrl() + col.getId().getPath() + "?q=";
		
		FacetURIFactory uriFactory = new FacetURIFactory(scList);
		
		for (Statement st : profile.getStatements()) 
		{
			SearchCriterion sc = new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_NAMESPACE, st.getName().toString(), Filtertype.URI);
			facets.add(new Facet(uriFactory.createFacetURI(baseURI, sc, getName(st.getName())), getName(st.getName()),  getCount(new ArrayList<SearchCriterion>(scList), sc)));
			sc =  new SearchCriterion(Operator.ANDNOT, ImejiNamespaces.IMAGE_METADATA_NAMESPACE, st.getName().toString(), Filtertype.URI);
			facets.add(new Facet(uriFactory.createFacetURI(baseURI, sc, "No+" + getName(st.getName())), "No " + getName(st.getName()),  getCount(new ArrayList<SearchCriterion>(scList), sc)));
		}
	}

	
	public String getName(URI uri)
	{
		MetadataLabels metadataLabels = (MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class);
		String name = metadataLabels.getLabels().get(uri);
		return name;
	}
	public int getCount(List<SearchCriterion> scList, SearchCriterion sc)
	{
		ImageController ic = new ImageController(sb.getUser());
		if (sc != null) scList.add(sc);
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

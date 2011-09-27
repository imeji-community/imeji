package de.mpg.imeji.facet;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.facet.Facet.FacetType;
import de.mpg.imeji.filter.FiltersSession;
import de.mpg.imeji.lang.MetadataLabels;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class CollectionFacets
{
	private SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	private FiltersSession fs = (FiltersSession) BeanHelper.getSessionBean(FiltersSession.class);
	
	 private List<List<Facet>> facets = new ArrayList<List<Facet>>();
	
	private URI colURI = null;
	
	public CollectionFacets(CollectionImeji col, List<SearchCriterion> scList) throws Exception 
	{
		this.colURI = col.getId();
		
		ProfileController pc = new ProfileController(sb.getUser());
		MetadataProfile profile = pc.retrieve(col.getProfile());
		
		Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
		String baseURI = nav.getImagesUrl() + col.getId().getPath() + "?q=";
		
		((MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class)).init(profile);
		
		FacetURIFactory uriFactory = new FacetURIFactory(scList);
		int count = 0;
		
		int sizeAllImages = getCount(scList, null);
			
		for (Statement st : profile.getStatements()) 
		{
			List<Facet> group = new ArrayList<Facet>();
			
			if (!fs.isFilter(getName(st.getName())) || !fs.isFilter("No " + getName(st.getName())))
			{
				SearchCriterion sc = new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_NAMESPACE, st.getName().toString(), Filtertype.URI);
				count =  getCount(new ArrayList<SearchCriterion>(scList), sc);
				System.out.println("saasas");
				if (count > 0 || true) 
				{
					group.add(new Facet(uriFactory.createFacetURI(baseURI, sc, getName(st.getName()), FacetType.COLLECTION), getName(st.getName()), count, FacetType.COLLECTION, st.getName() ));
				}
				if (count < sizeAllImages || true)
				{
					sc =  new SearchCriterion(Operator.NOTAND, ImejiNamespaces.IMAGE_METADATA_NAMESPACE, st.getName().toString(), Filtertype.URI);
					group.add(new Facet(uriFactory.createFacetURI(baseURI, sc, "No " + getName(st.getName()), FacetType.COLLECTION), "No " + getName(st.getName()), sizeAllImages - count, FacetType.COLLECTION, st.getName()));
				}
			}
			
			facets.add(group);
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
		return ic.countImagesInContainer(colURI, scList);
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

package de.mpg.imeji.facet;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.ListUtils;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.collection.CollectionImagesBean;
import de.mpg.imeji.filter.Filter;
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
	
	private List<Facet> facets = new ArrayList<Facet>();
	
	private URI colURI = null;
	
	private int maxRecord = 0;
	
	public CollectionFacets(CollectionImeji col, List<SearchCriterion> scList) throws Exception 
	{
		this.colURI = col.getId();
		
		ProfileController pc = new ProfileController(sb.getUser());
		MetadataProfile profile = pc.retrieve(col.getProfile());
		CollectionImagesBean cib = (CollectionImagesBean) BeanHelper.getSessionBean(CollectionImagesBean.class);
		//maxRecord = cib.getTotalNumberOfRecords();
		
		Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
		String baseURI = nav.getImagesUrl() + col.getId().getPath() + "?q=";
		
		//SearchCriterion scColl = new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_COLLECTION, col.getId().toString(), Filtertype.URI);
		//scList.add(scColl);

		FacetURIFactory uriFactory = new FacetURIFactory(scList);
		int count = 0;
		for (Statement st : profile.getStatements()) 
		{
			if (!fs.isFilter(getName(st.getName())) && !fs.isNoResultFilter(getName(st.getName())))
			{
				SearchCriterion sc = new SearchCriterion(Operator.AND, ImejiNamespaces.IMAGE_METADATA_NAMESPACE, st.getName().toString(), Filtertype.URI);
				count =  getCount(new ArrayList<SearchCriterion>(scList), sc);
				if (count > 0) facets.add(new Facet(uriFactory.createFacetURI(baseURI, sc, getName(st.getName())), getName(st.getName()), count ));
				else fs.getNoResultsFilters().add(new Filter(getName(st.getName()), "", 0));
			}
			if (!fs.isFilter("No " + getName(st.getName())) && !fs.isNoResultFilter("No " + getName(st.getName())))
			{
				SearchCriterion sc =  new SearchCriterion(Operator.NOTAND, ImejiNamespaces.IMAGE_METADATA_NAMESPACE, st.getName().toString(), Filtertype.URI);
				count =  getCount(new ArrayList<SearchCriterion>(scList), sc);
				if (count > 0) facets.add(new Facet(uriFactory.createFacetURI(baseURI, sc, "No " + getName(st.getName())), "No " + getName(st.getName()), count));
				else fs.getNoResultsFilters().add(new Filter("No " + getName(st.getName()), "", 0));
			}
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
		
//		try 
//		{
//			LinkedList<String> all = ic.searchURI(new ArrayList<SearchCriterion>(), null, -1, 0);
//			for (SearchCriterion c : scList) 
//			{
//				List<SearchCriterion> l = new ArrayList<SearchCriterion>();
//				l.add(c);
//				LinkedList<String> col = ic.searchURI(l, null, -1, 0);
//				List<String> inter = ListUtils.intersection(all, col);
//				all = new LinkedList<String>(inter);
//			}
//			return all.size();
//			//return ic.getNumberOfResults(scList, maxRecord);
//		} 
//		catch (Exception e) 
//		{
//			e.printStackTrace();
//		}
//		
//		return 0;
	}
	
	public List<Facet> getFacets() {
		return facets;
	}

	public void setFacets(List<Facet> facets) {
		this.facets = facets;
	}
	
	
}

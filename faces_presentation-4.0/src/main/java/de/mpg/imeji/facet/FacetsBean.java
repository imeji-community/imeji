package de.mpg.imeji.facet;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thewebsemantic.LocalizedString;
import thewebsemantic.NotBoundException;
import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.filter.Filter;
import de.mpg.imeji.lang.labelHelper;
import de.mpg.imeji.search.URLQueryTransformer;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.util.ObjectHelper;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.ComplexType.ComplexTypes;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.complextypes.util.ComplexTypeHelper;

public class FacetsBean
{
    private List<FacetGroupBean> groups = new ArrayList<FacetGroupBean>();
    private Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
    private SessionBean sb;
    private List<SearchCriterion> filters = new ArrayList<SearchCriterion>();
    
    
    private List<Facet> collectionFacets = new ArrayList<Facet>();
    private List<Facet> technicalFacets = new ArrayList<Facet>();

    public FacetsBean() {
		// TODO Auto-generated constructor stub
	}
    
    public FacetsBean(List<Image> images)
    {
        this.sb = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);
        Map<URI, MetadataProfile> profiles = ProfileHelper.loadProfiles(images);
        CollectionController cc = new CollectionController(sb.getUser());
        try
        {
            for (MetadataProfile mdp : profiles.values())
            {
                SearchCriterion sc = new SearchCriterion(Operator.AND, ImejiNamespaces.COLLECTION_PROFILE, mdp.getId().toString(), Filtertype.URI);
                List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
                scList.add(sc);
                Collection<CollectionImeji> coll = cc.search(scList, null, 1, 0);
                if (coll.iterator().hasNext())
                {
                	groups.add(new FacetGroupBean(generateFacets(mdp, coll.iterator().next()), mdp.getTitle()));
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        
        // NEW
        
        try 
        {
			collectionFacets = new CollectionFacets(images, new ArrayList<SearchCriterion>()).getFacets();
			technicalFacets = new TechnicalFacets(new ArrayList<SearchCriterion>()).getFacets();
		} 
        catch (Exception e) 
        {
			e.printStackTrace();
		}
        
    }
   

	public List<Facet> generateFacets(MetadataProfile profile, CollectionImeji coll) throws Exception
    {
        List<Facet> facetbeans = new ArrayList<Facet>();
        if (coll == null) return facetbeans;
        facetbeans.add(
        		new Facet(URI.create(nav.getImagesUrl() + "/collection" + ObjectHelper.getId(coll.getId())
                + "?q="), coll.getMetadata().getTitle(), coll.getImages().size()));
        for (Statement st : profile.getStatements())
        {
            if (st.getLiteralConstraints().size() > 0)
            {
                for (LocalizedString ls : st.getLiteralConstraints())
                    facetbeans.add(generateFacet(profile.getId(), st, true, ls.toString(), coll));
            }
            else
            {
                facetbeans.add(generateFacet(profile.getId(), st, true, null, coll));
                facetbeans.add(generateFacet(profile.getId(), st, false, null, coll));
            }
        }
        return facetbeans;
    }
    
  

    public Facet generateFacet(URI id, Statement st, boolean hasValue, String value, CollectionImeji coll)
            throws Exception
    {
        URI uri = generateUri(id, st, hasValue, value, coll);
        String label = labelHelper.getDefaultLabel(st.getLabels().iterator());
        if (st.getLabels().size() > 0)
            label = st.getLabels().toArray()[0].toString();
        if (value != null)
            label = value;
        if (!hasValue)
            label = "No " + label;
        return new Facet(uri, label, getCount(id, st, hasValue, value, coll));
    }

    public List<Facet> clearDuplicate(List<Facet> list)
    {
        Map<String, Facet> fMap = new HashMap<String, Facet>();
        for (Facet f : list)
            fMap.put(f.getLabel(), f);
        return new ArrayList<Facet>(fMap.values());
    }

    public URI generateUri(URI id, Statement st, boolean hasValue, String value, CollectionImeji coll)
            throws UnsupportedEncodingException
    {
        List<Filter> filters = sb.getFilters();
        String filtersURI ="";
        int i=0;
        for (Filter f : filters)
        {
        	if (!"".equals(f.getQuery()))
        	{
        		filtersURI += "&f" + i + "=" + f.getQuery();
        		i++;
        	}
        
        }
    	filtersURI = URLEncoder.encode(filtersURI, "UTF-8");
    	filtersURI = "?q=" + URLEncoder.encode(generateQuery(id, st, hasValue, value, coll), "UTF-8") + filtersURI;
    	if (coll != null && coll.getId() != null)
        {
            return URI.create(nav.getImagesUrl() + "/collection" + ObjectHelper.getId(coll.getId()) + filtersURI);
        }
        return URI.create(nav.getImagesUrl() +  filtersURI);
    }

    public String generateQuery(URI id, Statement st, boolean hasValue, String value, CollectionImeji coll)
            throws UnsupportedEncodingException
    {
        return URLQueryTransformer.transform2URL(getFacetSCList(id, st, hasValue, value, coll));
    }
    
    public List<SearchCriterion> getFacetSCList(URI id, Statement st, boolean hasValue, String value, CollectionImeji coll)
    {
    	 if (value == null) value = "";
         ComplexTypes ct = ComplexTypeHelper.getComplexType(st.getType());
         List<SearchCriterion> scList = new ArrayList<SearchCriterion>(filters);
         
         SearchCriterion facetMetadataSC = new SearchCriterion(ImejiNamespaces.IMAGE_METADATA_NAME, st.getName().toString());
         facetMetadataSC.setFilterType(Filtertype.EQUALS);
         facetMetadataSC.setInverse(!hasValue);
         scList.add(facetMetadataSC);
         
         if (!"".equalsIgnoreCase(value))
         {
         	 SearchCriterion facetValueSC = new  SearchCriterion();
              facetValueSC.setValue(value);
              facetValueSC.setFilterType(Filtertype.EQUALS);
              for (ImejiNamespaces ims :ImejiNamespaces.values())
              {
              	if (ims.getNs().equals(ct.getURI().toString())) facetValueSC.setNamespace(ims);
              }
              scList.add(facetValueSC);
         }
         return scList;
    }

    public int getCount(URI id, Statement st, boolean hasValue, String value, CollectionImeji coll) throws Exception
    {
        List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
        scList = getFacetSCList(coll.getId(), st, hasValue, value, coll);
        ImageController ic = new ImageController(sb.getUser());
        try
        {
        	return ic.getNumberOfResultsInContainer(coll.getId(), scList);
        }
        catch (NotBoundException e)
        {
            return 0;
        }
    }
    
	public List<FacetGroupBean> getGroups()
    {
        return groups;
    }

    public void setGroups(List<FacetGroupBean> groups)
    {
        this.groups = groups;
    }
    
    // NEW

    public List<Facet> getCollectionFacets()
    {
    	return collectionFacets;
    }
    

    public void setCollectionFacets(List<Facet> collectionFacets) {
		this.collectionFacets = collectionFacets;
	}
    
    

	public List<Facet> getTechnicalFacets() {
		return technicalFacets;
	}

	public void setTechnicalFacets(List<Facet> technicalFacets) {
		this.technicalFacets = technicalFacets;
	}


}

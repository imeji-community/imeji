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
    
    private List<Facet> facets = new ArrayList<Facet>();
    
    private List<Facet> collectionFacets = new ArrayList<Facet>();
    private List<Facet> technicalFacets = new ArrayList<Facet>();
    
    public FacetsBean(List<SearchCriterion> scList) 
    {
    	try 
        {
    		facets = new TechnicalFacets(scList).getFacets();
 		} 
        catch (Exception e) 
        {
        	BeanHelper.error("Error initializing facets! " + e.getMessage());
 		}
	}
    
    public FacetsBean(CollectionImeji col, List<SearchCriterion> scList)
    {
    	try 
    	{
    		facets = new CollectionFacets(col, scList).getFacets();
		} 
    	catch (Exception e) 
    	{
			BeanHelper.error("Error initializing facets! " + e.getMessage());
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

	public List<Facet> getFacets() {
		return facets;
	}

	public void setFacets(List<Facet> facets) {
		this.facets = facets;
	}


}

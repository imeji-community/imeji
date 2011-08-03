package de.mpg.imeji.facet;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.vo.CollectionImeji;

public class FacetsBean
{    
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
        	e.printStackTrace();
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
			e.printStackTrace();
		}
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

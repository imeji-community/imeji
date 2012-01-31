/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.facet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.vo.CollectionImeji;

public class FacetsBean implements Serializable
{    
    private List<List<Facet>> facets = new ArrayList<List<Facet>>();
    
    public FacetsBean(List<SearchCriterion> scList) 
    {
    	try 
        {
    		facets = new TechnicalFacets(scList).getFacets();
 		} 
        catch (Exception e) 
        {
        	BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("error") + ", Facets intialization: " + e.getMessage());
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
    		BeanHelper.error(((SessionBean)BeanHelper.getSessionBean(SessionBean.class)).getLabel("error") + ", Facets intialization: " + e.getMessage());
			e.printStackTrace();
		}
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

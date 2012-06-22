/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.presentation.facet;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.search.vo.SearchCriterion;
import de.mpg.imeji.presentation.facet.Facet.FacetType;
import de.mpg.imeji.presentation.search.URLQueryTransformer;

public class FacetURIFactory
{
	private List<SearchCriterion> scList = new ArrayList<SearchCriterion>();
	
	public FacetURIFactory(List<SearchCriterion> scList) 
	{
		this.scList = scList;
	}
	
	public URI createFacetURI(String baseURI, SearchCriterion sc, String facetName, FacetType type) throws UnsupportedEncodingException
	{
		List<SearchCriterion> scl = new ArrayList<SearchCriterion>(scList);
		if(sc != null) scl.add(sc);
		String uri = baseURI + getCommonURI(scl, facetName, type);
		return URI.create(uri);
	}
	
	private String getCommonURI(List<SearchCriterion> scl, String facetName, FacetType type) throws UnsupportedEncodingException
	{
        String commonURI ="";
        commonURI +=  URLEncoder.encode(URLQueryTransformer.transform2URL(scl), "UTF-8") + "&f=" +  URLEncoder.encode(facetName,  "UTF-8") + "&t=" +URLEncoder.encode(type.name().toLowerCase(),  "UTF-8") + "&page=1";
        return commonURI;
	}
	
}

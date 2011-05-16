package de.mpg.jena.sparql.query;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.vo.User;

public class SimpleQueryFactory 
{
	private static String PATTERN_SELECT = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s a <SEARCH_TYPE_ELEMENT> . ?s <http://imeji.mpdl.mpg.de/properties> ?props . ?props <http://imeji.mpdl.mpg.de/status> ?status . ?s <http://imeji.mpdl.mpg.de/collection> ?coll SECURITY_FILTER  SEARCH_ELEMENT SORT_ELEMENT} SORT_QUERY";
	private static String PATTERN_COUNT =  "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s a <SEARCH_TYPE_ELEMENT> . ?s <http://imeji.mpdl.mpg.de/properties> ?props . ?props <http://imeji.mpdl.mpg.de/status> ?status . ?s <http://imeji.mpdl.mpg.de/collection> ?coll SECURITY_FILTER . SEARCH_ELEMENT}";
	
	public static String search(String type, SearchCriterion sc, SortCriterion sortCriterion,  User user, boolean isCollection)
	{
		return 	PATTERN_SELECT.replaceAll("SECURITY_FILTER",  SimpleSecurityQuery.getQuery(user, sc))
							  .replaceAll("SORT_QUERY",  SortQueryFactory.create(sortCriterion))
							  .replace("SEARCH_ELEMENT", getSearchElement(sc))
							  .replaceAll("SEARCH_TYPE_ELEMENT", type)
							  .replace("SORT_ELEMENT", getSortElement(sortCriterion));
	}
	
	public String count(SearchCriterion sc)
	{
		return PATTERN_COUNT;
	}
	
	public static String getSearchElement(SearchCriterion sc)
	{
		String searchQuery = "";
		
		if(ImejiNamespaces.IMAGE_FILENAME.equals(sc.getNamespace()))
		{
			searchQuery = ". ?s <" + sc.getNamespace().getNs() + "> ?el";
		}
		else if (ImejiNamespaces.IMAGE_METADATA_TYPE_URI.equals(sc.getNamespace()))
		{
			searchQuery = ". OPTIONAL {?s <http://imeji.mpdl.mpg.de/metadataSet> ?mds . OPTIONAL {?mds <http://imeji.mpdl.mpg.de/metadata> ?md .OPTIONAL{ ?md <http://imeji.mpdl.mpg.de/complexTypes> ?type . OPTIONAL {?md <" + sc.getNamespace().getNs() + "> ?el }}}}";
		}
		else if (ImejiNamespaces.PROPERTIES_STATUS.equals(sc.getNamespace()))
		{
			return "";
		}
		else if (ImejiNamespaces.IMAGE_COLLECTION.equals(sc.getNamespace()))
		{
			return "";
		}
		else if (ImejiNamespaces.MY_IMAGES.equals(sc.getNamespace()))
		{
			return "";
		}
		else
		{
			searchQuery = ". OPTIONAL {?s <http://imeji.mpdl.mpg.de/metadataSet> ?mds . OPTIONAL {?mds <http://imeji.mpdl.mpg.de/metadata> ?md  . OPTIONAL {?md <" + sc.getNamespace().getNs() + "> ?el }}}";
		}
		
		if (Operator.NOTAND.equals(sc.getOperator()) || Operator.NOTOR.equals(sc.getOperator()))
		{
			return ".MINUS{ " + searchQuery.substring(1) + " .FILTER(" + FilterFactory.getSimpleFilter(sc,"el") + ")}";
		}
		
		return searchQuery + " .FILTER(" + FilterFactory.getSimpleFilter(sc,"el") + ")";
	}
	
	public static String getSortElement(SortCriterion sc)
	{
		if (sc != null)
		{
			if (ImejiNamespaces.PROPERTIES_CREATION_DATE.equals(sc.getSortingCriterion()))
			{
				return ". ?props <" + sc.getSortingCriterion().getNs() + "> ?sort0";
			}
			else if (ImejiNamespaces.PROPERTIES_LAST_MODIFICATION_DATE.equals(sc.getSortingCriterion()))
			{
				return ". ?props <" + sc.getSortingCriterion().getNs() + "> ?sort0";
			}
			else if (ImejiNamespaces.IMAGE_COLLECTION.equals(sc.getSortingCriterion()))
			{
				return " .?coll <http://imeji.mpdl.mpg.de/container/metadata> ?collmd . ?collmd <http://purl.org/dc/elements/1.1/title> ?sort0";
			}
		}
		return "";
	}

}

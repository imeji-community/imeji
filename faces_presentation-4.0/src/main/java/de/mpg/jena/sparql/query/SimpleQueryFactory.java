package de.mpg.jena.sparql.query;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.vo.User;

public class SimpleQueryFactory 
{
	private static String PATTERN_SELECT = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s a <XXX_SEARCH_TYPE_ELEMENT_XXX> . " +
			"?s <http://imeji.mpdl.mpg.de/properties> ?props . ?props <http://imeji.mpdl.mpg.de/status> ?status XXX_SPECIFIC_QUERY_XXX XXX_SECURITY_FILTER_XXX XXX_SEARCH_ELEMENT_XXX XXX_SORT_ELEMENT_XXX} " +
			"XXX_SORT_QUERY_XXX";
	
	public static String search(String type, SearchCriterion sc, SortCriterion sortCriterion, User user, boolean isCollection, String specificQuery)
	{
		return 	PATTERN_SELECT.replaceAll("XXX_SECURITY_FILTER_XXX",  SimpleSecurityQuery.getQuery(user, sc, type, isCollection))
							  .replaceAll("XXX_SORT_QUERY_XXX",  SortQueryFactory.create(sortCriterion))
							  .replaceAll("XXX_SEARCH_ELEMENT_XXX", getSearchElement(sc))
							  .replaceAll("XXX_SEARCH_TYPE_ELEMENT_XXX", type)
							  .replaceAll("XXX_SORT_ELEMENT_XXX", getSortElement(sortCriterion))
							  .replaceAll("XXX_SPECIFIC_QUERY_XXX", specificQuery);
	}
	
	public static String getSearchElement(SearchCriterion sc)
	{
		String searchQuery = "";
		 
		if(sc == null)
		{
			return "";
		}
		else if(ImejiNamespaces.IMAGE_FILENAME.equals(sc.getNamespace()))
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
		else if (ImejiNamespaces.CONTAINER_METADATA_PERSON_ORGANIZATION_NAME.equals(sc.getNamespace()))
		{
			searchQuery = " .OPTIONAL {?s <http://imeji.mpdl.mpg.de/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/escidoc/metadata/terms/0.1/creator> ?p . OPTIONAL{ ?p <http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit> ?org .OPTIONAL{?org <http://purl.org/dc/elements/1.1/title> ?el}}}}";
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
			else if (ImejiNamespaces.PROPERTIES_STATUS.equals(sc.getSortingCriterion()))
			{
				return ". ?props <" + sc.getSortingCriterion().getNs() + "> ?sort0";
			}
			else if (ImejiNamespaces.IMAGE_COLLECTION.equals(sc.getSortingCriterion()))
			{
				return ". ?c <http://imeji.mpdl.mpg.de/container/metadata> ?cmd . ?cmd <http://purl.org/dc/elements/1.1/title> ?sort0";
			}
			else if (ImejiNamespaces.CONTAINER_METADATA_TITLE.equals(sc.getSortingCriterion()))
			{
				return ". ?s <http://imeji.mpdl.mpg.de/container/metadata> ?cmd . ?cmd <http://purl.org/dc/elements/1.1/title> ?sort0" ;
			}
		}
		return "";
	}

}

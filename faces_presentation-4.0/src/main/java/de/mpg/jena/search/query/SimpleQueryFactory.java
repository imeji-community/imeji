package de.mpg.jena.search.query;

import de.mpg.jena.controller.SearchCriterion;
import de.mpg.jena.controller.SearchCriterion.Filtertype;
import de.mpg.jena.controller.SearchCriterion.ImejiNamespaces;
import de.mpg.jena.controller.SearchCriterion.Operator;
import de.mpg.jena.controller.SortCriterion;
import de.mpg.jena.util.DateFormatter;
import de.mpg.jena.vo.User;

public class SimpleQueryFactory 
{
	private static String PATTERN_SELECT = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s a <XXX_SEARCH_TYPE_ELEMENT_XXX> . " +
	"?s <http://imeji.mpdl.mpg.de/properties> ?props . ?props <http://imeji.mpdl.mpg.de/status> ?status XXX_SPECIFIC_QUERY_XXX XXX_SECURITY_FILTER_XXX XXX_SEARCH_ELEMENT_XXX XXX_SORT_ELEMENT_XXX} " +
	"XXX_SORT_QUERY_XXX";

	public static String getQuery(String type, SearchCriterion sc, SortCriterion sortCriterion, User user, boolean isCollection, String specificQuery)
	{
		return 	PATTERN_SELECT.replaceAll("XXX_SECURITY_FILTER_XXX",  SimpleSecurityQuery.getQuery(user, sc, type, false))
		.replaceAll("XXX_SORT_QUERY_XXX",  SortQueryFactory.create(sortCriterion))
		.replaceAll("XXX_SEARCH_ELEMENT_XXX", getSearchElement(sc))
		.replaceAll("XXX_SEARCH_TYPE_ELEMENT_XXX", type)
		.replaceAll("XXX_SORT_ELEMENT_XXX", getSortElement(sortCriterion))
		.replaceAll("XXX_SPECIFIC_QUERY_XXX", specificQuery);
	}

	public static String getSearchElement(SearchCriterion sc)
	{
		String searchQuery = "";
		String variable="el";

		if(sc == null)
		{
			return "";
		}
		else if(ImejiNamespaces.IMAGE_FILENAME.equals(sc.getNamespace()))
		{
			searchQuery = ". ?s <" + sc.getNamespace().getNs() + "> ?el";
		}
		else if (ImejiNamespaces.ID_URI.equals(sc.getNamespace()))
		{
			searchQuery = "";
			variable ="s";
		}
		else if (ImejiNamespaces.IMAGE_METADATA_TYPE_URI.equals(sc.getNamespace()))
		{
			//slow
			searchQuery = ". OPTIONAL {?s <http://imeji.mpdl.mpg.de/metadataSet> ?mds . OPTIONAL {?mds <http://imeji.mpdl.mpg.de/metadata> ?md .OPTIONAL{ ?md <http://imeji.mpdl.mpg.de/complexTypes> ?type . OPTIONAL {?md <" + sc.getNamespace().getNs() + "> ?el }}}}";
			//fast (not tested)
			//searchQuery = ". ?s <http://imeji.mpdl.mpg.de/metadataSet> ?mds . ?mds <http://imeji.mpdl.mpg.de/metadata> ?md . ?md <http://imeji.mpdl.mpg.de/complexTypes> ?type .  ?md <" + sc.getNamespace().getNs() + "> ?el ";
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
		else if (ImejiNamespaces.CONTAINER_METADATA_TITLE.equals(sc.getNamespace()))
		{
			searchQuery = " .OPTIONAL {?s <http://imeji.mpdl.mpg.de/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/dc/elements/1.1/title> ?el}}";
		}
		else if (ImejiNamespaces.CONTAINER_METADATA_DESCRIPTION.equals(sc.getNamespace()))
		{
			searchQuery = " .OPTIONAL {?s <http://imeji.mpdl.mpg.de/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/dc/elements/1.1/description> ?el}}";
		}
		else if (ImejiNamespaces.CONTAINER_METADATA_PERSON_FAMILY_NAME.equals(sc.getNamespace()))
		{
			searchQuery = " .OPTIONAL {?s <http://imeji.mpdl.mpg.de/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/escidoc/metadata/terms/0.1/creator> ?p . OPTIONAL{ ?p <http://purl.org/escidoc/metadata/terms/0.1/family-name> ?el}}}";
		}
		else if (ImejiNamespaces.CONTAINER_METADATA_PERSON_COMPLETE_NAME.equals(sc.getNamespace()))
		{
			searchQuery = " .OPTIONAL {?s <http://imeji.mpdl.mpg.de/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/escidoc/metadata/terms/0.1/creator> ?p . OPTIONAL{ ?p <http://purl.org/escidoc/metadata/terms/0.1/complete-name> ?el}}}";
		}
		else if (ImejiNamespaces.CONTAINER_METADATA_PERSON_GIVEN_NAME.equals(sc.getNamespace()))
		{
			searchQuery = " .OPTIONAL {?s <http://imeji.mpdl.mpg.de/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/escidoc/metadata/terms/0.1/creator> ?p . OPTIONAL{ ?p <http://purl.org/escidoc/metadata/terms/0.1/given-name> ?el}}}";
		}
		else if (ImejiNamespaces.CONTAINER_METADATA_PERSON_ORGANIZATION_NAME.equals(sc.getNamespace()))
		{
			searchQuery = " .OPTIONAL {?s <http://imeji.mpdl.mpg.de/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/escidoc/metadata/terms/0.1/creator> ?p . OPTIONAL{ ?p <http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit> ?org .OPTIONAL{?org <http://purl.org/dc/elements/1.1/title> ?el}}}}";
		}
		else if (ImejiNamespaces.COLLECTION_PROFILE.equals(sc.getNamespace()))
		{
			searchQuery = " .?s <http://imeji.mpdl.mpg.de/mdprofile> ?el";
		}
		else if(ImejiNamespaces.IMAGE_METADATA_PERSON_FAMILY_NAME.equals(sc.getNamespace()) 
				|| ImejiNamespaces.IMAGE_METADATA_PERSON_GIVEN_NAME.equals(sc.getNamespace())
				|| ImejiNamespaces.IMAGE_METADATA_PERSON_ORGANIZATION_NAME.equals(sc.getNamespace()))
		{
			searchQuery += ". ?s <http://imeji.mpdl.mpg.de/metadataSet> ?mds . ?mds <http://imeji.mpdl.mpg.de/metadata> ?md  . ?md <http://imeji.mpdl.mpg.de/metadata/person> ?p . ?p  <" +  sc.getNamespace().getNs() + "> ?el ";
		}
		else
		{
			//slow
			//searchQuery = ". OPTIONAL {?s <http://imeji.mpdl.mpg.de/metadataSet> ?mds . OPTIONAL {?mds <http://imeji.mpdl.mpg.de/metadata> ?md  . OPTIONAL {?md <" + sc.getNamespace().getNs() + "> ?el }}}";
			//fast for simple metadata search
			searchQuery = ". ?s <http://imeji.mpdl.mpg.de/metadataSet> ?mds . ?mds <http://imeji.mpdl.mpg.de/metadata> ?md  . ?md <" + sc.getNamespace().getNs() + "> ?el ";
		}

		
		if (Operator.NOTAND.equals(sc.getOperator()) || Operator.NOTOR.equals(sc.getOperator()))
		{
			return ".MINUS{ " + searchQuery.substring(1) + " .FILTER(" + getSimpleFilter(sc,variable) + ")}";
		}

		return searchQuery + " .FILTER(" + getSimpleFilter(sc,variable) + ")";
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

	public static String getSimpleFilter(SearchCriterion sc, String variable)
	{
		String filter ="";
		variable = "?" + variable;

		if (sc.getValue() != null)
		{
			// Lok for use cases where this kind of string search for uris make sense.
			// If no cases, then remove it, since it's more performant to search for uri directly like 3. else if
			if (sc.getFilterType().equals(Filtertype.URI) 
					/*&& !sc.getNamespace().equals(ImejiNamespaces.ID_URI) 
					&& !sc.getNamespace().equals(ImejiNamespaces.IMAGE_METADATA_TYPE)
					&& !sc.getNamespace().equals(ImejiNamespaces.IMAGE_METADATA_NAMESPACE)*/)
			{
				// Slow (tested)
				//filter += "str(" + variable + ")='" + sc.getValue() + "'";
				// faster (not tested)
				filter +=  variable + "=<" + sc.getValue() + ">";
			}
			else if (sc.getValue().startsWith("\"") && sc.getValue().endsWith("\""))
			{
				filter +=  variable + "='" + sc.getValue().replaceAll("\"", "")+ "'";
			}
			else if (sc.getFilterType().equals(Filtertype.URI) && 
					(sc.getNamespace().equals(ImejiNamespaces.ID_URI) 
							|| sc.getNamespace().equals(ImejiNamespaces.IMAGE_METADATA_TYPE)
							|| sc.getNamespace().equals(ImejiNamespaces.IMAGE_METADATA_NAMESPACE)))
			{
				filter +=  variable + "=<" + sc.getValue() + ">";
			}
			else if (sc.getFilterType().equals(Filtertype.REGEX))
			{
				filter += "regex(" + variable + ", '"  + sc.getValue() + "', 'i')";
			}
			else if (sc.getFilterType().equals(Filtertype.EQUALS))
			{
				filter += variable + "='" + sc.getValue()+ "'";
			}
			else if (sc.getFilterType().equals(Filtertype.NOT))
			{
				filter +=  variable + "!='" + sc.getValue() + "'";
			}
			else if (sc.getFilterType().equals(Filtertype.BOUND))
			{
				filter += "bound(" + variable + ")=" + sc.getValue() + "";
			}
			else if (sc.getFilterType().equals(Filtertype.EQUALS_NUMBER))
			{	
				try{ Double d = Double.valueOf(sc.getValue());
				filter += variable + "='" + d + "'^^<http://www.w3.org/2001/XMLSchema#double>";}
				catch (Exception e) {/* Not a double*/}
			}
			else if (sc.getFilterType().equals(Filtertype.GREATER_NUMBER))
			{
				try{ Double d = Double.valueOf(sc.getValue());
				filter += variable + ">='" + d + "'^^<http://www.w3.org/2001/XMLSchema#double>";}
				catch (Exception e) {/* Not a double*/}
			}
			else if (sc.getFilterType().equals(Filtertype.LESSER_NUMBER))
			{
				try{ Double d = Double.valueOf(sc.getValue());
				filter += variable + "<='" + d + "'^^<http://www.w3.org/2001/XMLSchema#double>";}
				catch (Exception e) {/* Not a double*/}
			}
			else if (sc.getFilterType().equals(Filtertype.EQUALS_DATE))
			{
				filter += variable + "='" + DateFormatter.getTime(sc.getValue()) + "'^^<http://www.w3.org/2001/XMLSchema#double>";
			}
			else if (sc.getFilterType().equals(Filtertype.GREATER_DATE))
			{
				filter += variable + ">='" + DateFormatter.getTime(sc.getValue()) + "'^^<http://www.w3.org/2001/XMLSchema#double>";
			}
			else if (sc.getFilterType().equals(Filtertype.LESSER_DATE))
			{
				filter += variable + "<='" + DateFormatter.getTime(sc.getValue()) + "'^^<http://www.w3.org/2001/XMLSchema#double>";
			}
		}
		return filter;
	}

}

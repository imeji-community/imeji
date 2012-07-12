/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search.query;

import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.util.DateFormatter;
import de.mpg.imeji.logic.vo.User;

public class SimpleQueryFactory
{
    private static String PATTERN_SELECT = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s WHERE {?s a <XXX_SEARCH_TYPE_ELEMENT_XXX> . "
            + "?s <http://imeji.org/terms/properties> ?props . ?props <http://imeji.org/terms/status> ?status XXX_SPECIFIC_QUERY_XXX XXX_SECURITY_FILTER_XXX XXX_SEARCH_ELEMENT_XXX XXX_SORT_ELEMENT_XXX} "
            + "XXX_SORT_QUERY_XXX";

    // public static String getQuery(String type, SearchCriterion sc, SortCriterion sortCriterion, User user,
    // boolean isCollection, String specificQuery)
    // {
    // return PATTERN_SELECT
    // .replaceAll("XXX_SECURITY_FILTER_XXX", SimpleSecurityQuery.getQuery(user, sc, type, false))
    // .replaceAll("XXX_SORT_QUERY_XXX", SortQueryFactory.create(sortCriterion))
    // .replaceAll("XXX_SEARCH_ELEMENT_XXX", getSearchElement(sc))
    // .replaceAll("XXX_SEARCH_TYPE_ELEMENT_XXX", type)
    // .replaceAll("XXX_SORT_ELEMENT_XXX", getSortElement(sortCriterion))
    // .replaceAll("XXX_SPECIFIC_QUERY_XXX", specificQuery);
    // }
    public static String getQuery(String type, SearchPair pair, SortCriterion sortCriterion, User user,
            boolean isCollection, String specificQuery)
    {
        return PATTERN_SELECT
                .replaceAll("XXX_SECURITY_FILTER_XXX", SimpleSecurityQuery.getQuery(user, pair, type, false))
                .replaceAll("XXX_SORT_QUERY_XXX", SortQueryFactory.create(sortCriterion))
                .replaceAll("XXX_SEARCH_ELEMENT_XXX", getSearchElement(pair))
                .replaceAll("XXX_SEARCH_TYPE_ELEMENT_XXX", type)
                .replaceAll("XXX_SORT_ELEMENT_XXX", getSortElement(sortCriterion))
                .replaceAll("XXX_SPECIFIC_QUERY_XXX", specificQuery);
    }

    public static String getSearchElement(SearchPair pair)
    {
        String searchQuery = "";
        String variable = "el";
        if (pair == null)
        {
            return "";
        }
        else if (SearchIndex.names.IMAGE_FILENAME.name().equals(pair.getIndex().getName()))
        {
            searchQuery = ". ?s <" + pair.getIndex().getNamespace() + "> ?el";
        }
        else if (SearchIndex.names.ID_URI.name().equals(pair.getIndex().getName()))
        {
            searchQuery = "";
            variable = "s";
        }
        else if (SearchIndex.names.PROPERTIES_STATUS.name().equals(pair.getIndex().getName()))
        {
            return "";
        }
        else if (SearchIndex.names.IMAGE_COLLECTION.name().equals(pair.getIndex().getName()))
        {
            return "";
        }
        else if (SearchIndex.names.MY_IMAGES.name().equals(pair.getIndex().getName()))
        {
            return "";
        }
        else if (SearchIndex.names.CONTAINER_METADATA_TITLE.name().equals(pair.getIndex().getName()))
        {
            searchQuery = " .OPTIONAL {?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/dc/elements/1.1/title> ?el}}";
        }
        else if (SearchIndex.names.CONTAINER_METADATA_DESCRIPTION.name().equals(pair.getIndex().getName()))
        {
            searchQuery = " .OPTIONAL {?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/dc/elements/1.1/description> ?el}}";
        }
        else if (SearchIndex.names.CONTAINER_METADATA_PERSON_FAMILY_NAME.name().equals(pair.getIndex().getName()))
        {
            searchQuery = " .OPTIONAL {?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/escidoc/metadata/terms/0.1/creator> ?p . OPTIONAL{ ?p <http://purl.org/escidoc/metadata/terms/0.1/family-name> ?el}}}";
        }
        else if (SearchIndex.names.CONTAINER_METADATA_PERSON_COMPLETE_NAME.name().equals(pair.getIndex().getName()))
        {
            searchQuery = " .OPTIONAL {?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/escidoc/metadata/terms/0.1/creator> ?p . OPTIONAL{ ?p <http://purl.org/escidoc/metadata/terms/0.1/complete-name> ?el}}}";
        }
        else if (SearchIndex.names.CONTAINER_METADATA_PERSON_GIVEN_NAME.name().equals(pair.getIndex().getName()))
        {
            searchQuery = " .OPTIONAL {?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/escidoc/metadata/terms/0.1/creator> ?p . OPTIONAL{ ?p <http://purl.org/escidoc/metadata/terms/0.1/given-name> ?el}}}";
        }
        else if (SearchIndex.names.CONTAINER_METADATA_PERSON_ORGANIZATION_NAME.name().equals(pair.getIndex().getName()))
        {
            searchQuery = " .OPTIONAL {?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/escidoc/metadata/terms/0.1/creator> ?p . OPTIONAL{ ?p <http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit> ?org .OPTIONAL{?org <http://purl.org/dc/elements/1.1/title> ?el}}}}";
        }
        else if (SearchIndex.names.COLLECTION_PROFILE.name().equals(pair.getIndex().getName()))
        {
            searchQuery = " .?s <http://imeji.org/terms/mdprofile> ?el";
        }
        else if (SearchIndex.names.IMAGE_METADATA_TYPE_RDF.name().equals(pair.getIndex().getName()))
        {
            return ". ?s <http://imeji.org/terms/metadataSet> ?mds . ?mds <http://imeji.org/terms/metadata> ?md  . ?md a <"
                    + pair.getValue() + ">";
        }
        else
        {
            searchQuery = ". ?s <http://imeji.org/terms/metadataSet> ?mds . ?mds <http://imeji.org/terms/metadata> ?md  . ?md "
                    + getSearchElementsParent(pair.getIndex(), 0) + " <" + pair.getIndex().getNamespace() + "> ?el ";
        }
        if (pair.isNot())
        {
            return ".MINUS{ " + searchQuery.substring(1) + " .FILTER(" + getSimpleFilter(pair, variable) + ")}";
        }
        return searchQuery + " .FILTER(" + getSimpleFilter(pair, variable) + ")";
    }

    private static String getSearchElementsParent(SearchIndex index, int parentNumber)
    {
        String q = "";
        if (index.getParent() != null)
        {
            q += getSearchElementsParent(index.getParent(), parentNumber + 1) + " <" + index.getParent().getNamespace()
                    + "> ?p" + parentNumber + " . ?p" + parentNumber;
        }
        return q;
    }

    // public static String getSearchElement(SearchCriterion sc)
    // {
    // String searchQuery = "";
    // String variable = "el";
    // if (sc == null)
    // {
    // return "";
    // }
    // else if (SearchIndexes.IMAGE_FILENAME.equals(sc.getNamespace()))
    // {
    // searchQuery = ". ?s <" + sc.getNamespace().getNs() + "> ?el";
    // }
    // else if (SearchIndexes.ID_URI.equals(sc.getNamespace()))
    // {
    // searchQuery = "";
    // variable = "s";
    // }
    // else if (SearchIndexes.IMAGE_METADATA_TYPE_URI.equals(sc.getNamespace()))
    // {
    // // slow
    // searchQuery =
    // ". OPTIONAL {?s <http://imeji.org/terms/metadataSet> ?mds . OPTIONAL {?mds <http://imeji.org/terms/metadata> ?md .OPTIONAL{ ?md <http://imeji.org/terms/complexTypes> ?type . OPTIONAL {?md <"
    // + sc.getNamespace().getNs() + "> ?el }}}}";
    // // fast (not tested)
    // // searchQuery =
    // //
    // ". ?s <http://imeji.org/terms/metadataSet> ?mds . ?mds <http://imeji.org/terms/metadata> ?md . ?md <http://imeji.org/terms/complexTypes> ?type .  ?md <"
    // // + sc.getNamespace().getNs() + "> ?el ";
    // }
    // else if (SearchIndexes.PROPERTIES_STATUS.equals(sc.getNamespace()))
    // {
    // return "";
    // }
    // else if (SearchIndexes.IMAGE_COLLECTION.equals(sc.getNamespace()))
    // {
    // return "";
    // }
    // else if (SearchIndexes.MY_IMAGES.equals(sc.getNamespace()))
    // {
    // return "";
    // }
    // else if (SearchIndexes.CONTAINER_METADATA_TITLE.equals(sc.getNamespace()))
    // {
    // searchQuery =
    // " .OPTIONAL {?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/dc/elements/1.1/title> ?el}}";
    // }
    // else if (SearchIndexes.CONTAINER_METADATA_DESCRIPTION.equals(sc.getNamespace()))
    // {
    // searchQuery =
    // " .OPTIONAL {?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/dc/elements/1.1/description> ?el}}";
    // }
    // else if (SearchIndexes.CONTAINER_METADATA_PERSON_FAMILY_NAME.equals(sc.getNamespace()))
    // {
    // searchQuery =
    // " .OPTIONAL {?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/escidoc/metadata/terms/0.1/creator> ?p . OPTIONAL{ ?p <http://purl.org/escidoc/metadata/terms/0.1/family-name> ?el}}}";
    // }
    // else if (SearchIndexes.CONTAINER_METADATA_PERSON_COMPLETE_NAME.equals(sc.getNamespace()))
    // {
    // searchQuery =
    // " .OPTIONAL {?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/escidoc/metadata/terms/0.1/creator> ?p . OPTIONAL{ ?p <http://purl.org/escidoc/metadata/terms/0.1/complete-name> ?el}}}";
    // }
    // else if (SearchIndexes.CONTAINER_METADATA_PERSON_GIVEN_NAME.equals(sc.getNamespace()))
    // {
    // searchQuery =
    // " .OPTIONAL {?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/escidoc/metadata/terms/0.1/creator> ?p . OPTIONAL{ ?p <http://purl.org/escidoc/metadata/terms/0.1/given-name> ?el}}}";
    // }
    // else if (SearchIndexes.CONTAINER_METADATA_PERSON_ORGANIZATION_NAME.equals(sc.getNamespace()))
    // {
    // searchQuery =
    // " .OPTIONAL {?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL{?cmd <http://purl.org/escidoc/metadata/terms/0.1/creator> ?p . OPTIONAL{ ?p <http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit> ?org .OPTIONAL{?org <http://purl.org/dc/elements/1.1/title> ?el}}}}";
    // }
    // else if (SearchIndexes.COLLECTION_PROFILE.equals(sc.getNamespace()))
    // {
    // searchQuery = " .?s <http://imeji.org/terms/mdprofile> ?el";
    // }
    // else if (SearchIndexes.IMAGE_METADATA_PERSON_FAMILY_NAME.equals(sc.getNamespace())
    // || SearchIndexes.IMAGE_METADATA_PERSON_GIVEN_NAME.equals(sc.getNamespace())
    // || SearchIndexes.IMAGE_METADATA_PERSON_ORGANIZATION_NAME.equals(sc.getNamespace()))
    // {
    // searchQuery +=
    // ". ?s <http://imeji.org/terms/metadataSet> ?mds . ?mds <http://imeji.org/terms/metadata> ?md  . ?md <http://imeji.org/terms/metadata/person> ?p . ?p  <"
    // + sc.getNamespace().getNs() + "> ?el ";
    // }
    // else
    // {
    // // slow
    // // searchQuery =
    // //
    // ". OPTIONAL {?s <http://imeji.org/terms/metadataSet> ?mds . OPTIONAL {?mds <http://imeji.org/terms/metadata> ?md  . OPTIONAL {?md <"
    // // + sc.getNamespace().getNs() + "> ?el }}}";
    // // fast for simple metadata search
    // searchQuery =
    // ". ?s <http://imeji.org/terms/metadataSet> ?mds . ?mds <http://imeji.org/terms/metadata> ?md  . ?md <"
    // + sc.getNamespace().getNs() + "> ?el ";
    // }
    // if (Operator.NOTAND.equals(sc.getOperator()) || Operator.NOTOR.equals(sc.getOperator()))
    // {
    // return ".MINUS{ " + searchQuery.substring(1) + " .FILTER(" + getSimpleFilter(sc, variable) + ")}";
    // }
    // return searchQuery + " .FILTER(" + getSimpleFilter(sc, variable) + ")";
    // }
    //
    public static String getSortElement(SortCriterion sortCriterion)
    {
        if (sortCriterion != null && sortCriterion.getIndex() != null)
        {
            if (SearchIndex.names.PROPERTIES_CREATION_DATE.name().equals(sortCriterion.getIndex().getName()))
            {
                return ". ?props <" + sortCriterion.getIndex().getNamespace() + "> ?sort0";
            }
            else if (SearchIndex.names.PROPERTIES_LAST_MODIFICATION_DATE.name().equals(
                    sortCriterion.getIndex().getName()))
            {
                return ". ?props <" + sortCriterion.getIndex().getNamespace() + "> ?sort0";
            }
            else if (SearchIndex.names.PROPERTIES_STATUS.name().equals(sortCriterion.getIndex().getName()))
            {
                return ". ?props <" + sortCriterion.getIndex().getNamespace() + "> ?sort0";
            }
            else if (SearchIndex.names.IMAGE_COLLECTION.name().equals(sortCriterion.getIndex().getName()))
            {
                return ". ?c <http://imeji.org/terms/container/metadata> ?cmd . ?cmd <http://purl.org/dc/elements/1.1/title> ?sort0";
            }
            else if (SearchIndex.names.CONTAINER_METADATA_TITLE.name().equals(sortCriterion.getIndex().getName()))
            {
                return ". ?s <http://imeji.org/terms/container/metadata> ?cmd . ?cmd <http://purl.org/dc/elements/1.1/title> ?sort0";
            }
        }
        return "";
    }

    // public static String getSortElement(SortCriterion sc)
    // {
    // if (sc != null)
    // {
    // if (SearchIndexes.PROPERTIES_CREATION_DATE.equals(sc.getSortingCriterion()))
    // {
    // return ". ?props <" + sc.getSortingCriterion().getNs() + "> ?sort0";
    // }
    // else if (SearchIndexes.PROPERTIES_LAST_MODIFICATION_DATE.equals(sc.getSortingCriterion()))
    // {
    // return ". ?props <" + sc.getSortingCriterion().getNs() + "> ?sort0";
    // }
    // else if (SearchIndexes.PROPERTIES_STATUS.equals(sc.getSortingCriterion()))
    // {
    // return ". ?props <" + sc.getSortingCriterion().getNs() + "> ?sort0";
    // }
    // else if (SearchIndexes.IMAGE_COLLECTION.equals(sc.getSortingCriterion()))
    // {
    // return
    // ". ?c <http://imeji.org/terms/container/metadata> ?cmd . ?cmd <http://purl.org/dc/elements/1.1/title> ?sort0";
    // }
    // else if (SearchIndexes.CONTAINER_METADATA_TITLE.equals(sc.getSortingCriterion()))
    // {
    // return
    // ". ?s <http://imeji.org/terms/container/metadata> ?cmd . ?cmd <http://purl.org/dc/elements/1.1/title> ?sort0";
    // }
    // }
    // return "";
    // }
    public static String getSimpleFilter(SearchPair pair, String variable)
    {
        String filter = "";
        variable = "?" + variable;
        if (pair.getValue() != null)
        {
            switch (pair.getOperator())
            {
                case URI:
                    filter += variable + "=<" + pair.getValue() + ">";
                    break;
                case REGEX:
                    filter += "regex(" + variable + ", '" + pair.getValue() + "', 'i')";
                    break;
                case EQUALS:
                    filter += variable + "='" + pair.getValue() + "'";
                    break;
                case NOT:
                    filter += variable + "!='" + pair.getValue() + "'";
                    break;
                case BOUND:
                    filter += "bound(" + variable + ")=" + pair.getValue() + "";
                    break;
                case EQUALS_NUMBER:
                    try
                    {
                        Double d = Double.valueOf(pair.getValue());
                        filter += variable + "='" + d + "'^^<http://www.w3.org/2001/XMLSchema#double>";
                    }
                    catch (Exception e)
                    {/* Not a double */
                    }
                    break;
                case GREATER_NUMBER:
                    try
                    {
                        Double d = Double.valueOf(pair.getValue());
                        filter += variable + ">='" + d + "'^^<http://www.w3.org/2001/XMLSchema#double>";
                    }
                    catch (Exception e)
                    {/* Not a double */
                    }
                    break;
                case LESSER_NUMBER:
                    try
                    {
                        Double d = Double.valueOf(pair.getValue());
                        filter += variable + "<='" + d + "'^^<http://www.w3.org/2001/XMLSchema#double>";
                    }
                    catch (Exception e)
                    {/* Not a double */
                    }
                    break;
                case EQUALS_DATE:
                    filter += variable + "='" + DateFormatter.getTime(pair.getValue())
                            + "'^^<http://www.w3.org/2001/XMLSchema#double>";
                    break;
                case GREATER_DATE:
                    filter += variable + ">='" + DateFormatter.getTime(pair.getValue())
                            + "'^^<http://www.w3.org/2001/XMLSchema#double>";
                    break;
                case LESSER_DATE:
                    filter += variable + "<='" + DateFormatter.getTime(pair.getValue())
                            + "'^^<http://www.w3.org/2001/XMLSchema#double>";
                    break;
                default:
                    if (pair.getValue().startsWith("\"") && pair.getValue().endsWith("\""))
                    {
                        filter += variable + "='" + pair.getValue().replaceAll("\"", "") + "'";
                    }
                    break;
            }
        }
        if ("".equals(filter.trim()))
        {
            return "true";
        }
        return filter;
    }
    // public static String getSimpleFilter(SearchCriterion sc, String variable)
    // {
    // String filter = "";
    // variable = "?" + variable;
    // if (sc.getValue() != null)
    // {
    // // Look for use cases where this kind of string search for uris make sense.
    // // If no cases, then remove it, since it's more performant to search for uri directly like 3. else if
    // if (sc.getFilterType().equals(Filtertype.URI)
    // /*
    // * && !sc.getNamespace().equals(ImejiNamespaces.ID_URI) &&
    // * !sc.getNamespace().equals(ImejiNamespaces.IMAGE_METADATA_TYPE) &&
    // * !sc.getNamespace().equals(ImejiNamespaces.IMAGE_METADATA_NAMESPACE)
    // */)
    // {
    // // Slow (tested)
    // // filter += "str(" + variable + ")='" + sc.getValue() + "'";
    // // faster (not tested)
    // filter += variable + "=<" + sc.getValue() + ">";
    // }
    // else if (sc.getValue().startsWith("\"") && sc.getValue().endsWith("\""))
    // {
    // filter += variable + "='" + sc.getValue().replaceAll("\"", "") + "'";
    // }
    // else if (sc.getFilterType().equals(Filtertype.URI)
    // && (sc.getNamespace().equals(SearchIndexes.ID_URI)
    // || sc.getNamespace().equals(SearchIndexes.IMAGE_METADATA_TYPE) || sc.getNamespace().equals(
    // SearchIndexes.IMAGE_METADATA_STATEMENT)))
    // {
    // filter += variable + "=<" + sc.getValue() + ">";
    // }
    // else if (sc.getFilterType().equals(Filtertype.REGEX))
    // {
    // filter += "regex(" + variable + ", '" + sc.getValue() + "', 'i')";
    // }
    // else if (sc.getFilterType().equals(Filtertype.EQUALS))
    // {
    // filter += variable + "='" + sc.getValue() + "'";
    // }
    // else if (sc.getFilterType().equals(Filtertype.NOT))
    // {
    // filter += variable + "!='" + sc.getValue() + "'";
    // }
    // else if (sc.getFilterType().equals(Filtertype.BOUND))
    // {
    // filter += "bound(" + variable + ")=" + sc.getValue() + "";
    // }
    // else if (sc.getFilterType().equals(Filtertype.EQUALS_NUMBER))
    // {
    // try
    // {
    // Double d = Double.valueOf(sc.getValue());
    // filter += variable + "='" + d + "'^^<http://www.w3.org/2001/XMLSchema#double>";
    // }
    // catch (Exception e)
    // {/* Not a double */
    // }
    // }
    // else if (sc.getFilterType().equals(Filtertype.GREATER_NUMBER))
    // {
    // try
    // {
    // Double d = Double.valueOf(sc.getValue());
    // filter += variable + ">='" + d + "'^^<http://www.w3.org/2001/XMLSchema#double>";
    // }
    // catch (Exception e)
    // {/* Not a double */
    // }
    // }
    // else if (sc.getFilterType().equals(Filtertype.LESSER_NUMBER))
    // {
    // try
    // {
    // Double d = Double.valueOf(sc.getValue());
    // filter += variable + "<='" + d + "'^^<http://www.w3.org/2001/XMLSchema#double>";
    // }
    // catch (Exception e)
    // {/* Not a double */
    // }
    // }
    // else if (sc.getFilterType().equals(Filtertype.EQUALS_DATE))
    // {
    // filter += variable + "='" + DateFormatter.getTime(sc.getValue())
    // + "'^^<http://www.w3.org/2001/XMLSchema#double>";
    // }
    // else if (sc.getFilterType().equals(Filtertype.GREATER_DATE))
    // {
    // filter += variable + ">='" + DateFormatter.getTime(sc.getValue())
    // + "'^^<http://www.w3.org/2001/XMLSchema#double>";
    // }
    // else if (sc.getFilterType().equals(Filtertype.LESSER_DATE))
    // {
    // filter += variable + "<='" + DateFormatter.getTime(sc.getValue())
    // + "'^^<http://www.w3.org/2001/XMLSchema#double>";
    // }
    // }
    // if ("".equals(filter.trim()))
    // {
    // return "true";
    // }
    // return filter;
    // }
}

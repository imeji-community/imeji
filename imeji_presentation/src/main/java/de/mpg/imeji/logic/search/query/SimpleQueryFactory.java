/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search.query;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.util.DateFormatter;
import de.mpg.imeji.logic.vo.User;

public class SimpleQueryFactory
{
    private static String PATTERN_SELECT = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s ?sort0 WHERE {?s a <XXX_SEARCH_TYPE_ELEMENT_XXX> . "
            + "?s <http://imeji.org/terms/properties> ?props . ?props <http://imeji.org/terms/status> ?status XXX_SPECIFIC_QUERY_XXX XXX_SECURITY_FILTER_XXX XXX_SEARCH_ELEMENT_XXX XXX_SORT_ELEMENT_XXX} "
            + "XXX_SORT_QUERY_XXX ";

    public static String getQuery(String rdfType, SearchPair pair, SortCriterion sortCriterion, User user,
            boolean isCollection, String specificQuery)
    {
        PATTERN_SELECT = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT ?s ?sort0 WHERE {XXX_SEARCH_ELEMENT_XXX XXX_SPECIFIC_QUERY_XXX "
                + " ?s <http://imeji.org/terms/status> ?status  XXX_SECURITY_FILTER_XXX XXX_SORT_ELEMENT_XXX}";
        return PATTERN_SELECT
                .replaceAll("XXX_SECURITY_FILTER_XXX", SimpleSecurityQuery.getQuery(user, pair, rdfType, false))
                .replaceAll("XXX_SORT_QUERY_XXX", SortQueryFactory.create(sortCriterion))
                .replaceAll("XXX_SEARCH_ELEMENT_XXX", getSearchElement(pair))
                .replaceAll("XXX_SEARCH_TYPE_ELEMENT_XXX", rdfType)
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
        else if (SearchIndex.names.FULLTEXT.name().equals(pair.getIndex().getName()))
        {
            searchQuery = " ?s <" + pair.getIndex().getNamespace() + "> ?el";
        }
        else if (SearchIndex.names.IMAGE_FILENAME.name().equals(pair.getIndex().getName()))
        {
            searchQuery = " ?s <" + pair.getIndex().getNamespace() + "> ?el";
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
            return "?s <http://imeji.org/terms/metadataSet> ?mds . ?mds <http://imeji.org/terms/metadata> ?md  . ?md a <"
                    + pair.getValue() + "> .";
        }
        else
        {
            searchQuery = " ?s <http://imeji.org/terms/metadataSet> ?mds . ?mds <http://imeji.org/terms/metadata> ?md  . ?md "
                    + getSearchElementsParent(pair.getIndex(), 0) + " <" + pair.getIndex().getNamespace() + "> ?el ";
        }
        if (pair.isNot())
        {
            return " MINUS{ " + searchQuery.substring(1) + " .FILTER(" + getSimpleFilter(pair, variable) + ")} .";
        }
        return searchQuery + " .FILTER(" + getSimpleFilter(pair, variable) + ") .";
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

    public static String getSortElement(SortCriterion sortCriterion)
    {
        if (sortCriterion != null && sortCriterion.getIndex() != null)
        {
            if (SearchIndex.names.PROPERTIES_CREATION_DATE.name().equals(sortCriterion.getIndex().getName()))
            {
                return ". ?s <" + sortCriterion.getIndex().getNamespace() + "> ?sort0";
            }
            else if (SearchIndex.names.PROPERTIES_LAST_MODIFICATION_DATE.name().equals(
                    sortCriterion.getIndex().getName()))
            {
                return ". ?s <" + sortCriterion.getIndex().getNamespace() + "> ?sort0";
            }
            else if (SearchIndex.names.PROPERTIES_STATUS.name().equals(sortCriterion.getIndex().getName()))
            {
                return ". ?s <" + sortCriterion.getIndex().getNamespace() + "> ?sort0";
            }
        }
        return "";
    }

    public static String getSimpleFilter(SearchPair pair, String variable)
    {
        if (pair.getIndex().equals(Search.getIndex(SearchIndex.names.FULLTEXT)))
        {
            return getTextSearchFilter(pair, variable);
        }
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

    public static String getTextSearchFilter(SearchPair pair, String variable)
    {
        String filter = "";
        String text = pair.getValue();
        StringReader simpleReader = new StringReader(text);
        int i = 0;
        boolean isExactQuery = false;
        List<String> words = new ArrayList<String>();
        String word = "";
        try
        {
            while ((i = simpleReader.read()) != -1)
            {
                if (i == '"')
                {
                    if (isExactQuery)
                    {
                        isExactQuery = false;
                        words.add(word);
                        word = "";
                    }
                    else
                        isExactQuery = true;
                }
                else if (i == ' ' && !isExactQuery)
                {
                    words.add(word);
                    word = "";
                }
                else
                {
                    word += (char)i;
                }
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        if (!"".equals(word.trim()))
        {
            words.add(word);
        }
        for (String str : words)
        {
            if (!"".equals(filter))
            {
                filter += " || ";
            }
            filter += "regex(?" + variable + ", '" + str + "', 'i')";
        }
        return filter.trim();
    }
}

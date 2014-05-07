/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search.query;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchMetadata;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.util.DateFormatter;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Properties.Status;
import de.mpg.imeji.logic.vo.User;
import de.mpg.j2j.helper.J2JHelper;

/**
 * Factory to created Sparql query from a {@link SearchPair}
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SimpleQueryFactory
{
    private static String PATTERN_SELECT = "";

    /**
     * Create a SPARQL query
     * 
     * @param rdfType
     * @param pair
     * @param sortCriterion
     * @param user
     * @param isCollection
     * @param specificQuery
     * @return
     */
    public static String getQuery(String modelName, String rdfType, SearchPair pair, SortCriterion sortCriterion,
            User user, boolean isCollection, String specificQuery)
    {
        PATTERN_SELECT = "PREFIX fn: <http://www.w3.org/2005/xpath-functions#> SELECT DISTINCT ?s ?sort0 XXX_MODEL_NAMES_XXX WHERE {XXX_SECURITY_FILTER_XXX XXX_SEARCH_ELEMENT_XXX XXX_SPECIFIC_QUERY_XXX XXX_SEARCH_TYPE_ELEMENT_XXX  ?s <http://imeji.org/terms/status> ?status XXX_SORT_ELEMENT_XXX}";
        return PATTERN_SELECT
                .replace("XXX_MODEL_NAMES_XXX", getModelNames(modelName, pair))
                .replace("XXX_SECURITY_FILTER_XXX",
                        SimpleSecurityQuery.queryFactory(user, rdfType, getFilterStatus(pair), isUserSearchPair(pair)))
                .replace("XXX_SEARCH_ELEMENT_XXX", getSearchElement(pair, rdfType, user))
                .replace("XXX_SEARCH_TYPE_ELEMENT_XXX", getRdfType(rdfType))
                .replace("XXX_SORT_ELEMENT_XXX",
                        getSortElement(sortCriterion, "http://imeji.org/terms/item".equals(rdfType)))
                .replace("XXX_SPECIFIC_QUERY_XXX", specificQuery);
    }

    /**
     * Return the RDF Type of the search objects
     * 
     * @param rdfType
     * @return
     */
    private static String getRdfType(String rdfType)
    {
        if (rdfType == null || rdfType.equals(""))
            return "";
        return "?s a <" + rdfType + "> .";
    }

    /**
     * Return the names of the dataset (model) of the query
     * 
     * @param modelName
     * @return
     */
    private static String getModelNames(String modelName, SearchPair pair)
    {
        String names = "";
        if (modelName != null && !modelName.equals(""))
        {
            names = "FROM <" + modelName + "> FROM <" + Imeji.userModel + ">";
            if (Imeji.profileModel.equals(modelName))
            {
                names += " FROM <" + Imeji.collectionModel + ">";
            }
            if (pair != null && SearchIndex.names.item.name().equals(pair.getIndex().getName())
                    && !Imeji.imageModel.equals(modelName))
            {
                names += " FROM <" + Imeji.imageModel + ">";
            }
        }
        return names;
    }

    /**
     * Return all sparql elements needed for the query
     * 
     * @param pair
     * @return
     */
    private static String getSearchElement(SearchPair pair, String rdfType, User user)
    {
        String searchQuery = "";
        String variable = "el";
        if (pair == null)
        {
            return "";
        }
        else if (SearchIndex.names.all.name().equals(pair.getIndex().getName()))
        {
            // Search for ??
            searchQuery = "?s <" + pair.getIndex().getNamespace() + "> ?el";
        }
        else if (SearchIndex.names.filename.name().equals(pair.getIndex().getName()))
        {
            // Search for filename
            searchQuery = "?s <" + pair.getIndex().getNamespace() + "> ?el";
        }
        else if (SearchIndex.names.created.name().equals(pair.getIndex().getName())
                || SearchIndex.names.modified.name().equals(pair.getIndex().getName()))
        {
            // Search for filename
            searchQuery = "?s <" + pair.getIndex().getNamespace() + "> ?el";
        }
        else if (SearchIndex.names.checksum.name().equals(pair.getIndex().getName()))
        {
            // / Search for checksum
            searchQuery = "?s <" + pair.getIndex().getNamespace() + "> ?el";
        }
        else if (SearchIndex.names.filetype.name().equals(pair.getIndex().getName()))
        {
            searchQuery = " ?s <" + pair.getIndex().getNamespace() + "> ?el";
        }
        else if (SearchIndex.names.item.name().equals(pair.getIndex().getName()))
        {
            if (J2JHelper.getResourceNamespace(new CollectionImeji()).equals(rdfType)
                    || J2JHelper.getResourceNamespace(new Album()).equals(rdfType))
            {
                searchQuery = "?s <" + pair.getIndex().getNamespace() + "> ?el";
            }
            else
            {
                // Search for and item by id (uri)
                pair.setValue(normalizeURI(Item.class, pair.getValue()));
                return "FILTER(" + getSimpleFilter(pair, "s") + ") .";
            }
        }
        else if (SearchIndex.names.status.name().equals(pair.getIndex().getName()))
        {
            // this is filtered in the security query
            return "";
        }
        else if (SearchIndex.names.col.name().equals(pair.getIndex().getName())
                || SearchIndex.names.alb.name().equals(pair.getIndex().getName()))
        {
            // Search for one collection or one album by id (uri)
            return "FILTER(" + getSimpleFilter(pair, SimpleSecurityQuery.getVariableName(rdfType)) + ") .";
        }
        else if (SearchIndex.names.user.name().equals(pair.getIndex().getName()))
        {
            // Search for all objects of rdfType for which the user (pair.getValue) has a Grant
            // If user is same as searchpair value, then the security query will be enough else Search for all objects
            // of rdfType for which the user (pair.getValue) has a Grant
            if (user != null && pair.getValue().equals(user.getId().toString()))
                return "";
            else
                return "<" + pair.getValue()
                        + "> <http://imeji.org/terms/grant> ?g . ?g <http://imeji.org/terms/grantFor> ?"
                        + SimpleSecurityQuery.getVariableName(rdfType) + " .";
        }
        else if (SearchIndex.names.prof.name().equals(pair.getIndex().getName()))
        {
            // Search for a profile
            if (J2JHelper.getResourceNamespace(new MetadataProfile()).equals(rdfType))
            {
                pair.setValue(normalizeURI(MetadataProfile.class, pair.getValue()));
                return "FILTER(" + getSimpleFilter(pair, "s") + ") . ?c <"
                        + Search.getIndex(SearchIndex.names.prof).getNamespace() + "> ?s .";
            }
            else if (J2JHelper.getResourceNamespace(new CollectionImeji()).equals(rdfType))
            {
                searchQuery = "?s <http://imeji.org/terms/mdprofile> ?el";
            }
            else if (J2JHelper.getResourceNamespace(new Item()).equals(rdfType))
            {
                searchQuery = "?c <http://imeji.org/terms/mdprofile> ?el";
            }
        }
        else if (SearchIndex.names.cont_title.name().equals(pair.getIndex().getName()))
        {
            // Search for container title
            searchQuery = "?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL_FOR_NOT {?cmd <http://purl.org/dc/elements/1.1/title> ?el";
        }
        else if (SearchIndex.names.cont_description.name().equals(pair.getIndex().getName()))
        {
            // Search for container description
            searchQuery = "?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL_FOR_NOT {?cmd <http://purl.org/dc/elements/1.1/description> ?el";
        }
        else if (SearchIndex.names.cont_person_family.name().equals(pair.getIndex().getName()))
        {
            // Search for container creator family name
            searchQuery = "?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL_FOR_NOT { ?cmd <http://xmlns.com/foaf/0.1/person> ?p . ?p <http://purl.org/escidoc/metadata/terms/0.1/family-name> ?el";
        }
        else if (SearchIndex.names.cont_person_name.name().equals(pair.getIndex().getName()))
        {
            // Search for container creator complete name
            searchQuery = "?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL_FOR_NOT {?cmd <http://xmlns.com/foaf/0.1/person> ?p . ?p <http://purl.org/escidoc/metadata/terms/0.1/complete-name> ?el";
        }
        else if (SearchIndex.names.cont_person_given.name().equals(pair.getIndex().getName()))
        {
            // Search for container creator given name
            searchQuery = "?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL_FOR_NOT {?cmd <http://xmlns.com/foaf/0.1/person> ?p . ?p <http://purl.org/escidoc/metadata/terms/0.1/given-name> ?el";
        }
        else if (SearchIndex.names.cont_person_org_name.name().equals(pair.getIndex().getName()))
        {
            // Search for container creator organization name
            searchQuery = "?s <http://imeji.org/terms/container/metadata> ?cmd . OPTIONAL_FOR_NOT { ?cmd <http://xmlns.com/foaf/0.1/person> ?p . ?p <http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit> ?org . ?org <http://purl.org/dc/terms/title> ?el";
        }
        else if (SearchIndex.names.type.name().equals(pair.getIndex().getName()))
        {
            // Search for metadata type (i.e. the type the statement with wicht the metadata is defined)
            return "?s <http://imeji.org/terms/metadataSet> ?mds . ?mds <http://imeji.org/terms/metadata> ?md  . ?md a <"
                    + pair.getValue() + "> .";
        }
        else
        {
            // Search for a metadata value
            searchQuery = "?s <http://imeji.org/terms/metadataSet> ?mds . OPTIONAL_FOR_NOT { ?mds <http://imeji.org/terms/metadata> ?md  . ?md "
                    + getSearchElementsParent(pair.getIndex(), 0) + " <" + pair.getIndex().getNamespace() + "> ?el ";
        }
        if (pair instanceof SearchMetadata)
        {
            searchQuery = searchQuery + " . ?md <http://imeji.org/terms/statement> ?el1 ";
        }
        if (pair.isNot())
        {
            // If the pair is a negation
            return searchQuery.replace("OPTIONAL_FOR_NOT {", "OPTIONAL {") + " . FILTER("
                    + getSimpleFilter(pair, variable) + ")} . FILTER (!bound(?el) ) .";
        }
        else
        {
            // If the pair is not a negation
            return searchQuery.replace("OPTIONAL_FOR_NOT {", "") + " . FILTER(" + getSimpleFilter(pair, variable)
                    + ") .";
        }
    }
    
    
    /**
     * Return all parent search element (according to {@link SearchIndex}) of a search element, as a sparql query
     * 
     * @param index
     * @param parentNumber
     * @return
     */
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

    /**
     * If the uri has been corrupted (for instance /profile/ instead /metadataProfile/), return the correct uri
     * 
     * @param c
     * @param uri
     * @return
     */
    private static String normalizeURI(Class<?> c, String uri)
    {
        if (isURL(uri))
        {
            return ObjectHelper.getURI(c, ObjectHelper.getId(URI.create(uri))).toString();
        }
        return ObjectHelper.getURI(c, uri).toString();
    }

    /**
     * If the curretn {@link SearchPair} search for a {@link Status}, then return the search value
     * 
     * @param pair
     * @return
     */
    private static Status getFilterStatus(SearchPair pair)
    {
        if (pair != null && SearchIndex.names.status.name().equals(pair.getIndex().getName()))
        {
            if ("http://imeji.org/terms/status#PENDING".equals(pair.getValue()))
            {
                return Status.PENDING;
            }
            else if ("http://imeji.org/terms/status#RELEASED".equals(pair.getValue()))
            {
                return Status.RELEASED;
            }
            else if ("http://imeji.org/terms/status#WITHDRAWN".equals(pair.getValue()))
            {
                return Status.WITHDRAWN;
            }
        }
        return null;
    }

    /**
     * True if th {@link SearchPair} is searching for a {@link User}
     * 
     * @param pair
     * @return
     */
    private static boolean isUserSearchPair(SearchPair pair)
    {
        return pair != null && SearchIndex.names.user.name().equals(pair.getIndex().getName());
    }

    /**
     * Return the sparql elements needed for the search
     * 
     * @param sortCriterion
     * @return
     */
    private static String getSortElement(SortCriterion sortCriterion, boolean item)
    {
        if (sortCriterion != null && sortCriterion.getIndex() != null)
        {
            if (SearchIndex.names.created.name().equals(sortCriterion.getIndex().getName()))
            {
                return ". ?s <" + sortCriterion.getIndex().getNamespace() + "> ?sort0";
            }
            else if (SearchIndex.names.modified.name().equals(sortCriterion.getIndex().getName()))
            {
                return ". ?s <" + sortCriterion.getIndex().getNamespace() + "> ?sort0";
            }
            else if (SearchIndex.names.status.name().equals(sortCriterion.getIndex().getName()))
            {
                return ". ?s <" + sortCriterion.getIndex().getNamespace() + "> ?sort0";
            }
            else if (SearchIndex.names.cont_title.name().equals(sortCriterion.getIndex().getName()))
            {
                return (item ? " . ?c" : " . ?s") + " <http://imeji.org/terms/container/metadata> ?title . ?title <"
                        + sortCriterion.getIndex().getNamespace() + "> ?sort0";
            }
            else if (SearchIndex.names.filename.name().equals(sortCriterion.getIndex().getName()))
            {
                return ". ?s <" + sortCriterion.getIndex().getNamespace() + "> ?sort0";
            }
        }
        return "";
    }

    /**
     * Return a sparql filter for a {@link SearchPair}
     * 
     * @param pair
     * @param variable
     * @return
     */
    private static String getSimpleFilter(SearchPair pair, String variable)
    {
        if (pair.getIndex().equals(Search.getIndex(SearchIndex.names.all)))
        {
            return getTextSearchFilter(pair, variable);
        }
        String filter = "";
        variable = "?" + variable;
        if (pair.getValue() != null)
        {
            switch (pair.getOperator())
            {
                case REGEX:
                    filter += "regex(" + variable + ", '" + escapeApostroph(pair.getValue()) + "', 'i')";
                    break;
                case EQUALS:
                    filter += variable + "=" + getSearchValueInSPARQL(pair.getValue(), isRDFDate(pair));
                    break;
                case GREATER:
                    filter += variable + ">=" + getSearchValueInSPARQL(pair.getValue(), isRDFDate(pair));
                    break;
                case LESSER:
                    filter += variable + "<=" + getSearchValueInSPARQL(pair.getValue(), isRDFDate(pair));
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
        if (pair instanceof SearchMetadata)
        {
            filter += " && ?el1=<" + ((SearchMetadata)pair).getStatement().toString() + ">";
        }
        return filter;
    }

    /**
     * Return the search value in SPARQL
     * 
     * @param str
     * @return
     */
    private static String getSearchValueInSPARQL(String str, boolean dateAsTime)
    {
        if (isURL(str))
        {
            return "<" + URI.create(str) + ">";
        }
        else if (isDate(str))
        {
            if (dateAsTime)
                return "'" + DateFormatter.getTime(str) + "'^^<http://www.w3.org/2001/XMLSchema#double>";
            else
                return "'" + DateFormatter.formatToSparqlDateTime(str)
                        + "'^^<http://www.w3.org/2001/XMLSchema#dateTime>";
        }
        else if (isNumber(str))
        {
            return "'" + Double.valueOf(str) + "'^^<http://www.w3.org/2001/XMLSchema#double>";
        }
        return "'" + escapeApostroph(str) + "'";
    }

    /**
     * True if the {@link String} is an URL
     * 
     * @param str
     * @return
     */
    private static boolean isURL(String str)
    {
        return str.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    }

    /**
     * True if the String is a {@link Date}
     * 
     * @param str
     * @return
     */
    private static boolean isDate(String str)
    {
        return DateFormatter.parseDate(str, "yyyy-MM-dd") != null;
    }

    /**
     * True if it is a Number
     * 
     * @param str
     * @return
     */
    private static boolean isNumber(String str)
    {
        return str.matches("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
    }

    /**
     * Return the {@link String} search value of the filter
     * 
     * @param pair
     * @param variable
     * @return
     */
    private static String getTextSearchFilter(SearchPair pair, String variable)
    {
        String filter = "";
        String text = pair.getValue();
        StringReader simpleReader = new StringReader(text);
        int i = 0;
        boolean isPhraseQuery = false;
        List<String> words = new ArrayList<String>();
        String word = "";
        try
        {
            while ((i = simpleReader.read()) != -1)
            {
                if (i == '"')
                {
                    if (isPhraseQuery)
                    {
                        isPhraseQuery = false;
                        words.add(word);
                        word = "";
                    }
                    else
                        isPhraseQuery = true;
                }
                else if (i == ' ' && !isPhraseQuery)
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
            filter += "regex(?" + variable + ", '" + escapeApostroph(str) + "', 'i')";
        }
        return filter.trim();
    }

    /**
     * Escape the Apostrophe in String
     * 
     * @param s
     * @return
     */
    private static String escapeApostroph(String s)
    {
        return s.replace("'", "\\'");
    }

    /**
     * True if the pair used a rdf date format
     * 
     * @param pair
     * @return
     */
    private static boolean isRDFDate(SearchPair pair)
    {
        return !(SearchIndex.names.created.name().equals(pair.getIndex().getName()) || SearchIndex.names.modified
                .name().equals(pair.getIndex().getName()));
    }
}

/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.vo.SearchElement;
import de.mpg.imeji.logic.search.vo.SearchElement.SEARCH_ELEMENTS;
import de.mpg.imeji.logic.search.vo.SearchGroup;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchMetadata;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Static methods to manipulate imeji url search queries
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class URLQueryTransformer
{
    /**
     * Parse a url search query into a {@link SearchQuery}. Decode the query with UTF-8
     * 
     * @param query
     * @return
     * @throws IOException
     */
    public static SearchQuery parseStringQuery(String query) throws IOException
    {
        if (query == null)
            query = "";
        return parseStringQueryDecoded(URLDecoder.decode(query, "UTF-8"));
    }

    /**
     * Parse a url search query into a {@link SearchQuery}. The query should be already decoded
     * 
     * @param query
     * @return
     * @throws IOException
     */
    public static SearchQuery parseStringQueryDecoded(String query) throws IOException
    {
        SearchQuery searchQuery = new SearchQuery();
        String subQuery = "";
        String scString = "";
        boolean not = false;
        boolean hasBracket = false; // don't try to look for group if there isn't any bracket
        int bracketsOpened = 0;
        int bracketsClosed = 0;
        if (query == null)
            query = "";
        StringReader reader = new StringReader(query);
        int c = 0;
        while ((c = reader.read()) != -1)
        {
            if (bracketsOpened - bracketsClosed != 0)
                subQuery += (char)c;
            else
                scString += (char)c;
            if (c == '(')
            {
                hasBracket = true;
                bracketsOpened++;
            }
            if (c == ')')
            {
                bracketsClosed++;
                scString = "";
            }
            if (scString.trim().equals("AND") || scString.trim().equals("OR"))
            {
                searchQuery.getElements().add(new SearchLogicalRelation(LOGICAL_RELATIONS.valueOf(scString.trim())));
                scString = "";
            }
            if (scString.trim().equals("NOT") || scString.trim().equals("NOT"))
            {
                not = true;
                scString = "";
            }
            if (hasBracket && (bracketsOpened - bracketsClosed == 0))
            {
                SearchQuery subSearchQuery = parseStringQueryDecoded(subQuery);
                if (!subSearchQuery.isEmpty())
                {
                    SearchGroup searchGroup = new SearchGroup();
                    searchGroup.getGroup().addAll(parseStringQueryDecoded(subQuery).getElements());
                    searchQuery.getElements().add(searchGroup);
                    subQuery = "";
                }
            }
            if (matchSearchMetadataPattern(scString))
            {
                int indexOp = scString.indexOf("=");
                int indexValue = scString.indexOf("\"");
                int indexIndex = scString.indexOf(":");
                String value = scString.substring(indexValue + 1, scString.length() - 1).trim();
                if (value.startsWith("\""))
                {
                    value += "\"";
                }
                SearchIndex index = Search.getIndex(scString.substring(indexIndex + 1, indexOp).trim());
                SearchOperators operator = stringOperator2SearchOperator(scString.substring(indexOp, indexValue).trim());
                searchQuery.addPair(new SearchMetadata(index, operator, value, URI.create("http://imeji.org/statement/"
                        + scString.substring(0, indexIndex).trim()), not));
                not = false;
                scString = "";
            }
            else if (matchSearchPairPattern(scString))
            {
                int indexOp = scString.indexOf("=");
                int indexValue = scString.indexOf("\"");
                String value = scString.substring(indexValue + 1, scString.length() - 1).trim();
                if (value.startsWith("\""))
                {
                    value += "\"";
                }
                SearchIndex index = Search.getIndex(scString.substring(0, indexOp).trim());
                SearchOperators operator = stringOperator2SearchOperator(scString.substring(indexOp, indexValue).trim());
                searchQuery.addPair(new SearchPair(index, operator, value, not));
                scString = "";
                not = false;
            }
        }
        if (!"".equals(query) && searchQuery.isEmpty())
        {
            searchQuery.addPair(new SearchPair(Search.getIndex(SearchIndex.names.all), SearchOperators.REGEX, query
                    .trim()));
        }
        return searchQuery;
    }

    /**
     * Pattern to parse a {@link SearchPair} from an url query
     * 
     * @param str
     * @return
     */
    private static boolean matchSearchPairPattern(String str)
    {
        return str.trim().matches("\\s*[^\\s]+=.*\".+\"\\s*");
    }

    /**
     * Pattern to parse a {@link SearchMetadata} from a url query
     * 
     * @param str
     * @return
     */
    private static boolean matchSearchMetadataPattern(String str)
    {
        return str.trim().matches("[a-z0-9-]+:[a-z_]+=.*\".+\"\\s*");
    }

    /**
     * Transform a {@link String} to a {@link SearchOperators}
     * 
     * @param str
     * @return
     */
    private static SearchOperators stringOperator2SearchOperator(String str)
    {
        if ("=".equals(str))
        {
            return SearchOperators.REGEX;
        }
        else if ("==".equals(str))
        {
            return SearchOperators.URI;
        }
        return null;
    }

    /**
     * True is a {@link SearchQuery} is a simple search (i.e. triggered from the simple search form)
     * 
     * @param searchQuery
     * @return
     */
    public static boolean isSimpleSearch(SearchQuery searchQuery)
    {
        for (SearchElement element : searchQuery.getElements())
        {
            if (SEARCH_ELEMENTS.PAIR.equals(element.getType())
                    && ((SearchPair)element).getIndex().getName().equals(SearchIndex.names.all.name()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Transform a {@link SearchQuery} into a url search query encorded in UTF-8
     * 
     * @param searchQuery
     * @return
     */
    public static String transform2UTF8URL(SearchQuery searchQuery)
    {
        try
        {
            return URLEncoder.encode(transform2URL(searchQuery), "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Error encoding search query: " + searchQuery, e);
        }
    }

    /**
     * Transform a {@link SearchQuery} into a url search query
     * 
     * @param searchQuery
     * @return
     */
    public static String transform2URL(SearchQuery searchQuery)
    {
        String query = "";
        for (SearchElement se : searchQuery.getElements())
        {
            switch (se.getType())
            {
                case GROUP:
                    if (((SearchGroup)se).isNot())
                    {
                        query += " NOT";
                    }
                    query += "(" + transform2URL(new SearchQuery(((SearchGroup)se).getGroup())) + ")";
                    break;
                case LOGICAL_RELATIONS:
                    query += " " + ((SearchLogicalRelation)se).getLogicalRelation().name() + " ";
                    break;
                case PAIR:
                    if (((SearchPair)se).isNot())
                    {
                        query += " NOT";
                    }
                    query += ((SearchPair)se).getIndex().getName() + operator2URL(((SearchPair)se).getOperator())
                            + searchValue2URL(((SearchPair)se));
                    break;
                case METADATA:
                    if (((SearchMetadata)se).isNot())
                    {
                        query += " NOT";
                    }
                    query += transformStatementToIndex(((SearchMetadata)se).getStatement(), ((SearchPair)se).getIndex())
                            + operator2URL(((SearchMetadata)se).getOperator()) + searchValue2URL(((SearchMetadata)se));
                    break;
                default:
                    break;
            }
        }
        return query.trim();
    }

    /**
     * Transform a {@link Statement} to an index
     * 
     * @param statement
     * @param index
     * @return
     */
    public static String transformStatementToIndex(URI statement, SearchIndex index)
    {
        return ObjectHelper.getId(statement) + ":" + index.getName();
    }

    /**
     * REturn the search value of the {@link SearchMetadata} as string for an url
     * 
     * @param md
     * @return
     */
    private static String searchValue2URL(SearchPair pair)
    {
        return "\"" + pair.getValue() + "\"";
    }

    /**
     * Transform a {@link SearchOperators} to a {@link String} value used in url query
     * 
     * @param op
     * @return
     */
    private static String operator2URL(SearchOperators op)
    {
        switch (op)
        {
            case GREATER_DATE:
                return ">=";
            case GREATER_NUMBER:
                return ">=";
            case LESSER_DATE:
                return "<=";
            case LESSER_NUMBER:
                return "<=";
            case REGEX:
                return "=";
            case NOT:
                return "";// to be removed
            default:
                return "==";
        }
    }

    /**
     * Transform a {@link SearchQuery} into a user friendly query
     * 
     * @param sq
     * @return
     */
    public static String searchQuery2PrettyQuery(SearchQuery sq)
    {
        return searchElements2PrettyQuery(sq.getElements());
    }

    /**
     * Transform a {@link SearchPair} into a user friendly query
     * 
     * @param pair
     * @return
     */
    private static String searchPair2PrettyQuery(SearchPair pair)
    {
        if (pair.getValue() == null || pair.getValue() == "")
            return "";
        if (pair.getIndex().getName().equals(SearchIndex.names.all.name()))
        {
            return pair.getValue();
        }
        else
        {
            return indexNamespace2PrettyQuery(pair.getIndex().getNamespace()) + " "
                    + negation2PrettyQuery(pair.isNot()) + searchOperator2PrettyQuery(pair.getOperator()) + " "
                    + pair.getValue();
        }
    }

    /**
     * Transform a {@link SearchGroup} into a user friendly query
     * 
     * @param group
     * @return
     */
    private static String searchGroup2PrettyQuery(SearchGroup group)
    {
        String str = "";
        int groupSize = group.getElements().size();
        if (isSearchGroupForComplexMetadata(group))
        {
            str = searchMetadata2PrettyQuery((SearchMetadata)group.getElements().get(0));
            groupSize = 1;
        }
        else
        {
            str = searchElements2PrettyQuery(group.getElements());
        }
        if ("".equals(str))
            return "";
        if (groupSize > 1)
            return " (" + removeUseLessLogicalOperation(str) + ") ";
        else
            return removeUseLessLogicalOperation(str);
    }

    /**
     * Check if the search group is an group with pair about the same metadata. For instance, when searching for person,
     * the search group will be conposed of many pairs (family-name, givennane, etc) which sould be displayed as a
     * pretty query of only one metadata (person = value)
     * 
     * @param group
     * @return
     */
    private static boolean isSearchGroupForComplexMetadata(SearchGroup group)
    {
        List<String> statementUris = new ArrayList<String>();
        for (SearchElement el : group.getElements())
        {
            if (el.getType().equals(SEARCH_ELEMENTS.METADATA))
            {
                SearchMetadata md = (SearchMetadata)el;
                if (statementUris.contains(md.getStatement().toString()))
                {
                    return true;
                }
                statementUris.add(md.getStatement().toString());
            }
        }
        return false;
    }

    /**
     * transform a {@link SearchLogicalRelation} into a user friendly query
     * 
     * @param rel
     * @return
     */
    private static String searchLogicalRelation2PrettyQuery(SearchLogicalRelation rel)
    {
        return " " + rel.getLogicalRelation().name() + " ";
    }

    /**
     * Transform a {@link SearchElement} into a user friendly query
     * 
     * @param els
     * @return
     */
    private static String searchElements2PrettyQuery(List<SearchElement> els)
    {
        String q = "";
        for (SearchElement el : els)
        {
            switch (el.getType())
            {
                case PAIR:
                    q += searchPair2PrettyQuery((SearchPair)el);
                    break;
                case GROUP:
                    q += searchGroup2PrettyQuery((SearchGroup)el);
                    break;
                case LOGICAL_RELATIONS:
                    q += searchLogicalRelation2PrettyQuery((SearchLogicalRelation)el);
                    break;
                case METADATA:
                    q += searchMetadata2PrettyQuery((SearchMetadata)el);
                default:
                    break;
            }
        }
        return removeUseLessLogicalOperation(q).trim();
    }

    /**
     * Remove a logical operation if is not followed by a non empty search element
     * 
     * @param q
     * @return
     */
    private static String removeUseLessLogicalOperation(String q)
    {
        if (q.endsWith(" "))
            q = q.substring(0, q.length() - 1);
        if (q.endsWith(" AND"))
            q = q.substring(0, q.length() - 4);
        if (q.endsWith(" OR"))
            q = q.substring(0, q.length() - 3);
        return q;
    }

    /**
     * transform a namespace of a {@link SearchIndex} into a user friendly value
     * 
     * @param namespace
     * @return
     */
    public static String indexNamespace2PrettyQuery(String namespace)
    {
        String s[] = namespace.split("/");
        if (s.length > 0)
        {
            return namespace.split("/")[s.length - 1];
        }
        return namespace;
    }

    /**
     * Transform a {@link SearchOperators} into a user friendly label
     * 
     * @param op
     * @return
     */
    private static String searchOperator2PrettyQuery(SearchOperators op)
    {
        switch (op)
        {
            case GREATER_DATE:
                return ">=";
            case GREATER_NUMBER:
                return ">=";
            case LESSER_DATE:
                return "<=";
            case LESSER_NUMBER:
                return "<=";
            default:
                return "=";
        }
    }

    /**
     * Display a negation in a user friendly way
     * 
     * @param isNot
     * @return
     */
    private static String negation2PrettyQuery(boolean isNot)
    {
        if (isNot)
            return "!";
        return "";
    }

    /**
     * Special case to display a search for a metadata in a
     * 
     * @param group
     * @return
     */
    private static String searchMetadata2PrettyQuery(SearchMetadata md)
    {
        String label = ((MetadataLabels)BeanHelper.getSessionBean(MetadataLabels.class)).getInternationalizedLabels()
                .get(md.getStatement());
        if (label == null)
        {
            label = "Metadata-" + indexNamespace2PrettyQuery(md.getStatement().toString());
        }
        return label + " " + negation2PrettyQuery(md.isNot()) + searchOperator2PrettyQuery(md.getOperator()) + " "
                + md.getValue();
    }
}

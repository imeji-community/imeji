/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
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
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.util.BeanHelper;

public class URLQueryTransformer
{
    public static SearchQuery parseStringQuery(String query) throws IOException
    {
        SearchQuery searchQuery = new SearchQuery();
        String subQuery = "";
        String scString = "";
        boolean not = false;
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
            if (bracketsOpened - bracketsClosed == 0)
            {
                SearchQuery subSearchQuery = parseStringQuery(subQuery);
                if (!subSearchQuery.isEmpty())
                {
                    SearchGroup searchGroup = new SearchGroup();
                    searchGroup.getGroup().addAll(parseStringQuery(subQuery).getElements());
                    searchQuery.getElements().add(searchGroup);
                    subQuery = "";
                }
            }
            if (matchSearchMetadataPattern(scString))
            {
                String[] pairString = scString.split("=");
                String value = pairString[2].trim();
                String ns = pairString[0].split(",")[0].replace("IMAGE_METADATA[", "");
                value = value.substring(1, value.length() - 1);
                if (value.startsWith("\""))
                {
                    value += "\"";
                }
                SearchIndex index = Search.getIndex(pairString[0].split(",")[1].replace("]", "").trim());
                SearchOperators operator = SearchOperators.valueOf(pairString[1].trim());
                searchQuery.addPair(new SearchMetadata(index, operator, value, URI.create(ns), not));
                scString = "";
                not = false;
            }
            if (matchSearchPairPattern(scString) && !matchSearchMetadataPattern(scString))
            {
                String[] pairString = scString.split("=");
                String value = pairString[2].trim();
                value = value.substring(1, value.length() - 1);
                if (value.startsWith("\""))
                {
                    value += "\"";
                }
                SearchIndex index = Search.getIndex(pairString[0].trim());
                SearchOperators operator = SearchOperators.valueOf(pairString[1].trim());
                searchQuery.addPair(new SearchPair(index, operator, value, not));
                scString = "";
                not = false;
            }
        }
        if (!"".equals(query) && searchQuery.isEmpty())
        {
            searchQuery.addPair(new SearchPair(Search.getIndex(SearchIndex.names.FULLTEXT), SearchOperators.REGEX,
                    query.trim()));
        }
        return searchQuery;
    }

    private static boolean matchSearchPairPattern(String str)
    {
        return str.matches("\\s*[^\\s]+=.*=\".+\"\\s*");
    }

    private static boolean matchSearchMetadataPattern(String str)
    {
        return str.matches("\\s*" + SearchIndex.names.IMAGE_METADATA.name() + "\\[[^\\s]+\\]=.*=\".+\"\\s*");
    }

    public static boolean isSimpleSearch(SearchQuery searchQuery)
    {
        for (SearchElement element : searchQuery.getElements())
        {
            if (SEARCH_ELEMENTS.PAIR.equals(element.getType())
                    && ((SearchPair)element).getIndex().getName().equals(SearchIndex.names.FULLTEXT.name()))
            {
                return true;
            }
        }
        return false;
    }

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
                    query += " (" + transform2URL(new SearchQuery(((SearchGroup)se).getGroup())) + ") ";
                    break;
                case LOGICAL_RELATIONS:
                    query += ((SearchLogicalRelation)se).getLogicalRelation().name();
                    break;
                case PAIR:
                    if (((SearchPair)se).isNot())
                    {
                        query += " NOT";
                    }
                    query += " " + ((SearchPair)se).getIndex().getName() + "=" + ((SearchPair)se).getOperator().name()
                            + "=\"" + ((SearchPair)se).getValue() + "\" ";
                    break;
                case METADATA:
                    if (((SearchMetadata)se).isNot())
                    {
                        query += " NOT";
                    }
                    query += SearchIndex.names.IMAGE_METADATA.name() + "[" + ((SearchMetadata)se).getStatement() + ","
                            + ((SearchPair)se).getIndex().getName() + "]" + "="
                            + ((SearchMetadata)se).getOperator().name() + "=\"" + ((SearchMetadata)se).getValue()
                            + "\" ";
                    break;
            }
        }
        return query.trim();
    }

    public static String searchQuery2PrettyQuery(SearchQuery sq)
    {
        return searchElements2PrettyQuery(sq.getElements());
    }

    private static String searchPair2PrettyQuery(SearchPair pair)
    {
        if (pair.getValue() == null || pair.getValue() == "")
            return "";
        if (pair.getIndex().getName().equals(SearchIndex.names.FULLTEXT.name()))
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

    private static String searchGroup2PrettyQuery(SearchGroup group)
    {
        String str = searchElements2PrettyQuery(group.getElements());
        if ("".equals(str))
            return "";
        if (group.getElements().size() > 1)
            return " (" + removeUseLessLogicalOperation(str) + ") ";
        else
            return removeUseLessLogicalOperation(str);
    }

    private static String searchLogicalRelation2PrettyQuery(SearchLogicalRelation rel)
    {
        return " " + rel.getLogicalRelation().name() + " ";
    }

    private static String searchElements2PrettyQuery(List<SearchElement> els)
    {
        String q = "";
        int position = 0;
        for (SearchElement el : els)
        {
            position++;
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

    public static String indexNamespace2PrettyQuery(String namespace)
    {
        String s[] = namespace.split("/");
        if (s.length > 0)
        {
            return namespace.split("/")[s.length - 1];
        }
        return namespace;
    }

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

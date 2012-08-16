/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.search;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;

import de.mpg.imeji.logic.search.Search;
import de.mpg.imeji.logic.search.vo.SearchElement;
import de.mpg.imeji.logic.search.vo.SearchElement.SEARCH_ELEMENTS;
import de.mpg.imeji.logic.search.vo.SearchGroup;
import de.mpg.imeji.logic.search.vo.SearchIndex;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation;
import de.mpg.imeji.logic.search.vo.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.vo.SearchOperators;
import de.mpg.imeji.logic.search.vo.SearchPair;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.util.ObjectHelper;

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
            if (scString.matches("\\s*[^\\s]+=\".*\"\\s+"))
            {
                String[] pairString = scString.split("=");
                String value = pairString[2].trim();
                value = value.substring(1, value.length() - 1);
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
                    query));
        }
        return searchQuery;
    }

    public static boolean isSimpleSearch(SearchQuery searchQuery)
    {
        for (SearchElement element : searchQuery.getElements())
        {
            if (SEARCH_ELEMENTS.PAIR.equals(element.getType())
                    && ((SearchPair)element).getIndex().getName().equals(SearchIndex.names.FULLTEXT))
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
            }
        }
        return query;
    }

    // public static String transform2SimpleQuery(List<SearchCriterion> scList)
    // {
    // String query = "";
    // String metadataNamespace = null;
    // String metadataValue = null;
    // String filename = null;
    // Filtertype filter = Filtertype.EQUALS;
    // if (scList == null)
    // return "";
    // for (SearchCriterion sc : scList)
    // {
    // if (!"".equals(query))
    // {
    // query += " " + sc.getOperator().name() + " ";
    // }
    // if (isPersonCriterion(sc) != null)
    // {
    // query += isPersonCriterion(sc);
    // }
    // else if (sc.getChildren().size() > 0)
    // {
    // String subquery = transform2SimpleQuery(sc.getChildren());
    // if (subquery.contains("OR") || subquery.contains("AND"))
    // {
    // query += " ( ";
    // }
    // query += subquery;
    // if (subquery.contains("OR") || subquery.contains("AND"))
    // {
    // query += " ) ";
    // }
    // }
    // else
    // {
    // String value = "";
    // if (sc.getValue() != null)
    // value = sc.getValue();
    // if (sc.getNamespace() != null)
    // {
    // if (sc.getNamespace().name().contains(SearchIndexes.IMAGE_METADATA.name()))
    // {
    // if (sc.getNamespace().equals(SearchIndexes.IMAGE_METADATA_STATEMENT))
    // {
    // metadataNamespace = value;
    // }
    // else
    // {
    // metadataValue = value;
    // filter = sc.getFilterType();
    // }
    // }
    // else if (sc.getNamespace().equals(SearchIndexes.IMAGE_FILENAME))
    // {
    // filename = value;
    // }
    // else
    // {
    // query += " " + getNamespaceAsLabel(sc.getNamespace().getNs())
    // + getFilterAsLabel(sc.getFilterType()) + getIdAsLabel(value) + "  ";
    // }
    // if (metadataNamespace != null && metadataValue != null)
    // {
    // query += " " + getNamespaceAsLabel(metadataNamespace) + getFilterAsLabel(filter)
    // + metadataValue;
    // }
    // if (filename != null && metadataValue != null)
    // {
    // query += value;
    // }
    // }
    // }
    // }
    // return query;
    // }
    public static String getNamespaceAsLabel(String namespace)
    {
        String s[] = namespace.split("/");
        if (s.length > 0)
        {
            return namespace.split("/")[s.length - 1];
        }
        return namespace;
    }

    public static String getIdAsLabel(String uri)
    {
        if (URI.create(uri).isAbsolute())
        {
            String id = ObjectHelper.getId(URI.create(uri));
            if (id != null)
            {
                return "id " + id;
            }
        }
        return uri;
    }
    // public static String getFilterAsLabel(Filtertype filter)
    // {
    // switch (filter)
    // {
    // case GREATER_DATE:
    // return " >= ";
    // case GREATER_NUMBER:
    // return " >= ";
    // case LESSER_DATE:
    // return " <= ";
    // case LESSER_NUMBER:
    // return " <= ";
    // default:
    // return " = ";
    // }
    // }
}

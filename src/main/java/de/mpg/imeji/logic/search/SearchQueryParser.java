/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.search;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.search.model.SearchElement;
import de.mpg.imeji.logic.search.model.SearchElement.SEARCH_ELEMENTS;
import de.mpg.imeji.logic.search.model.SearchGroup;
import de.mpg.imeji.logic.search.model.SearchIndex;
import de.mpg.imeji.logic.search.model.SearchIndex.SearchFields;
import de.mpg.imeji.logic.search.model.SearchLogicalRelation;
import de.mpg.imeji.logic.search.model.SearchLogicalRelation.LOGICAL_RELATIONS;
import de.mpg.imeji.logic.search.model.SearchMetadata;
import de.mpg.imeji.logic.search.model.SearchOperators;
import de.mpg.imeji.logic.search.model.SearchPair;
import de.mpg.imeji.logic.search.model.SearchQuery;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Static methods to manipulate imeji url search queries
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
public class SearchQueryParser {
  /**
   * The Pattern to match a metadata search
   */
  private static final String SEARCH_METADATA_PATTERN = "[a-zA-Z0-9-_]+:[a-z_]+[=<>]{1,2}\".+\"";
  /**
   * The Pattern to match a search pair
   */
  private static final String SEARCH_PAIR_PATTERN = "[a-zA-Z_]+[=<>]{1,2}\".+\"";

  private static final String SEARCH_METADATA_SIMPLE_PATTERN = "md:[a-zA-Z0-9-_]+:[=<>]{1,2}\".+\"";

  /**
   * Parse a url search query into a {@link SearchQuery}. Decode the query with UTF-8
   * 
   * @param query
   * @return
   * @throws IOException
   */
  public static SearchQuery parseStringQuery(String query) throws IOException {
    if (query == null) {
      query = "";
    }
    String decodedQuery = URLDecoder.decode(query, "UTF-8");
    return parseStringQueryDecoded(decodedQuery);
  }

  /**
   * Parse a url search query into a {@link SearchQuery}. The query should be already decoded
   * 
   * @param query
   * @return
   * @throws IOException
   */
  public static SearchQuery parseStringQueryDecoded(String query) throws IOException {
    SearchQuery searchQuery = new SearchQuery();
    String subQuery = "";
    String scString = "";
    boolean not = false;
    boolean hasBracket = false; // don't try to look for group if there
    // isn't any bracket
    int bracketsOpened = 0;
    int bracketsClosed = 0;
    if (query == null) {
      query = "";
    }
    StringReader reader = new StringReader(query);
    int c = 0;
    while ((c = reader.read()) != -1) {
      if (bracketsOpened - bracketsClosed != 0) {
        subQuery += (char) c;
      } else {
        scString += (char) c;
      }
      if (c == '(') {
        hasBracket = true;
        bracketsOpened++;
      }
      if (c == ')') {
        bracketsClosed++;
        scString = "";
      }
      if (scString.trim().equals("AND") || scString.trim().equals("OR")) {
        searchQuery.getElements()
            .add(new SearchLogicalRelation(LOGICAL_RELATIONS.valueOf(scString.trim())));
        scString = "";
      }
      if (scString.trim().equals("NOT")) {
        not = true;
        scString = "";
      }
      if (hasBracket && (bracketsOpened - bracketsClosed == 0)) {
        SearchQuery subSearchQuery = parseStringQueryDecoded(subQuery);
        if (!subSearchQuery.isEmpty()) {
          SearchGroup searchGroup = new SearchGroup();
          searchGroup.getGroup().addAll(parseStringQueryDecoded(subQuery).getElements());
          searchQuery.getElements().add(searchGroup);
          subQuery = "";
        }
      }
      if (matchSearchMetadataPattern(scString)) {
        String op = parseOperatorInSearchPattern(scString, SEARCH_METADATA_PATTERN);
        int indexOp = scString.indexOf(op);
        int indexValue = scString.indexOf("\"");
        int indexIndex = scString.indexOf(":");
        String value = scString.substring(indexValue + 1, scString.length() - 1).trim();
        if (value.startsWith("\"")) {
          value += "\"";
        }
        SearchFields field =
            SearchFields.valueOf(scString.substring(indexIndex + 1, indexOp).trim());
        SearchOperators operator = stringOperator2SearchOperator(op);
        searchQuery.addPair(new SearchMetadata(field, operator, value,
            ObjectHelper.getURI(Statement.class, scString.substring(0, indexIndex).trim()), not));
        not = false;
        scString = "";
      } else if (matchSearchPairPattern(scString)) {
        String op = parseOperatorInSearchPattern(scString, SEARCH_PAIR_PATTERN);
        int indexOp = scString.indexOf(op);
        int indexValue = scString.indexOf("\"");
        String value = scString.substring(indexValue + 1, scString.length() - 1).trim();
        if (value.startsWith("\"")) {
          value += "\"";
        }
        SearchFields field = SearchFields.valueOf(scString.substring(0, indexOp).trim());
        SearchOperators operator = stringOperator2SearchOperator(op);
        searchQuery.addPair(new SearchPair(field, operator, value, not));
        scString = "";
        not = false;
      }
    }
    if (!"".equals(query) && searchQuery.isEmpty()) {
      searchQuery
          .addPair(new SearchPair(SearchFields.all, SearchOperators.REGEX, query.trim(), false));
    }
    return searchQuery;
  }

  /**
   * Parse the operator (=, ==, =<, >=) in the search pattern
   * 
   * @param pattern
   * @return
   */
  private static String parseOperatorInSearchPattern(String str, String pattern) {
    for (SearchOperators op : SearchOperators.values()) {
      String opString = operator2URL(op);
      String opPattern = pattern.replace("[=<>]{1,2}", opString);
      if (str.trim().matches(opPattern))
        return opString;
    }
    throw new RuntimeException("Operator not found in " + pattern);
  }

  /**
   * Pattern to parse a {@link SearchPair} from an url query
   * 
   * @param str
   * @return
   */
  private static boolean matchSearchPairPattern(String str) {
    return str.trim().matches(SEARCH_PAIR_PATTERN);
  }

  /**
   * Pattern to parse a {@link SearchMetadata} from a url query
   * 
   * @param str
   * @return
   */
  private static boolean matchSearchMetadataPattern(String str) {
    return str.trim().matches(SEARCH_METADATA_PATTERN);
  }

  /**
   * Transform a {@link String} to a {@link SearchOperators}
   * 
   * @param str
   * @return
   */
  private static SearchOperators stringOperator2SearchOperator(String str) {
    if ("=".equals(str)) {
      return SearchOperators.REGEX;
    } else if ("==".equals(str)) {
      return SearchOperators.EQUALS;
    } else if (">=".equals(str)) {
      return SearchOperators.GREATER;
    } else if ("<=".equals(str)) {
      return SearchOperators.LESSER;
    }
    return null;
  }

  /**
   * True is a {@link SearchQuery} is a simple search (i.e. triggered from the simple search form)
   * 
   * @param searchQuery
   * @return
   */
  public static boolean isSimpleSearch(SearchQuery searchQuery) {
    for (SearchElement element : searchQuery.getElements()) {
      if (SEARCH_ELEMENTS.PAIR.equals(element.getType())
          && ((SearchPair) element).getField() == SearchFields.all) {
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
  public static String transform2UTF8URL(SearchQuery searchQuery) {
    try {
      return URLEncoder.encode(transform2URL(searchQuery), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Error encoding search query: " + searchQuery, e);
    }
  }

  /**
   * Transform a {@link SearchQuery} into a url search query
   * 
   * @param searchQuery
   * @return
   */
  public static String transform2URL(SearchQuery searchQuery) {
    String query = "";
    String logical = "";
    for (SearchElement se : searchQuery.getElements()) {
      switch (se.getType()) {
        case GROUP:
          if (((SearchGroup) se).isNot()) {
            query += " NOT";
          }
          String g = transform2URL(new SearchQuery(((SearchGroup) se).getGroup()));
          if (!"".equals(g)) {
            query += logical + "(" + g + ")";
          }
          break;
        case LOGICAL_RELATIONS:
          logical = " " + ((SearchLogicalRelation) se).getLogicalRelation().name() + " ";
          break;
        case PAIR:
          if ("".equals(((SearchPair) se).getValue()))
            break;
          if (((SearchPair) se).isNot()) {
            query += " NOT";
          }
          query += logical + ((SearchPair) se).getField()
              + operator2URL(((SearchPair) se).getOperator()) + searchValue2URL(((SearchPair) se));
          break;
        case METADATA:
          if (((SearchMetadata) se).isNot()) {
            query += " NOT";
          }
          query += logical
              + transformStatementToIndex(((SearchMetadata) se).getStatement(),
                  ((SearchPair) se).getField())
              + operator2URL(((SearchMetadata) se).getOperator())
              + searchValue2URL(((SearchMetadata) se));
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
  public static String transformStatementToIndex(URI statement, SearchFields field) {
    return ObjectHelper.getId(statement) + ":" + field;
  }

  /**
   * REturn the search value of the {@link SearchMetadata} as string for an url
   * 
   * @param md
   * @return
   */
  private static String searchValue2URL(SearchPair pair) {
    return "\"" + pair.getValue() + "\"";
  }

  /**
   * Transform a {@link SearchOperators} to a {@link String} value used in url query
   * 
   * @param op
   * @return
   */
  private static String operator2URL(SearchOperators op) {
    switch (op) {
      case GREATER:
        return ">=";
      case LESSER:
        return "<=";
      case REGEX:
        return "=";
      case GEO:
        return "@";
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
  public static String searchQuery2PrettyQuery(SearchQuery sq) {
    if (sq == null) {
      return "";
    }
    return searchElements2PrettyQuery(sq.getElements());
  }

  /**
   * Transform a {@link SearchPair} into a user friendly query
   * 
   * @param pair
   * @return
   */
  private static String searchPair2PrettyQuery(SearchPair pair) {
    if (pair == null || pair.getField() == null || pair.getValue() == null
        || pair.getValue().equals("")) {
      return "";
    }
    if (pair.getField() == SearchFields.all) {
      return pair.getValue();
    } else {
      return indexNamespace2PrettyQuery(pair.getField().name()) + " "
          + negation2PrettyQuery(pair.isNot()) + searchOperator2PrettyQuery(pair.getOperator())
          + " " + pair.getValue();
    }
  }

  /**
   * Transform a {@link SearchGroup} into a user friendly query
   * 
   * @param group
   * @return
   */
  private static String searchGroup2PrettyQuery(SearchGroup group) {
    String str = "";
    int groupSize = group.getElements().size();
    if (isSearchGroupForComplexMetadata(group)) {
      for (SearchElement md : group.getElements()) {
        if (md instanceof SearchMetadata) {
          str += searchMetadata2PrettyQuery((SearchMetadata) md);
        } else if (md instanceof SearchLogicalRelation) {
          str += searchLogicalRelation2PrettyQuery((SearchLogicalRelation) md);
        }
      }
      // str =
      // searchMetadata2PrettyQuery((SearchMetadata)group.getElements().get(0));
      // groupSize = 1;
    } else {
      str = searchElements2PrettyQuery(group.getElements());
    }
    if ("".equals(str)) {
      return "";
    }
    if (groupSize > 1) {
      return " (" + removeUseLessLogicalOperation(str) + ") ";
    } else {
      return removeUseLessLogicalOperation(str);
    }
  }

  /**
   * Check if the search group is an group with pair about the same metadata. For instance, when
   * searching for person, the search group will be conposed of many pairs (family-name, givennane,
   * etc) which sould be displayed as a pretty query of only one metadata (person = value)
   * 
   * @param group
   * @return
   */
  private static boolean isSearchGroupForComplexMetadata(SearchGroup group) {
    List<String> statementUris = new ArrayList<String>();
    for (SearchElement el : group.getElements()) {
      if (el.getType().equals(SEARCH_ELEMENTS.METADATA)) {
        SearchMetadata md = (SearchMetadata) el;
        if (statementUris.contains(md.getStatement().toString())) {
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
  private static String searchLogicalRelation2PrettyQuery(SearchLogicalRelation rel) {
    SessionBean session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
    switch (rel.getLogicalRelation()) {
      case AND:
        return " " + session.getLabel("and_big") + " ";
      default:
        return " " + session.getLabel("or_big") + " ";
    }
  }

  /**
   * Transform a {@link SearchElement} into a user friendly query
   * 
   * @param els
   * @return
   */
  private static String searchElements2PrettyQuery(List<SearchElement> els) {
    String q = "";
    for (SearchElement el : els) {
      switch (el.getType()) {
        case PAIR:
          q += searchPair2PrettyQuery((SearchPair) el);
          break;
        case GROUP:
          q += searchGroup2PrettyQuery((SearchGroup) el);
          break;
        case LOGICAL_RELATIONS:
          q += searchLogicalRelation2PrettyQuery((SearchLogicalRelation) el);
          break;
        case METADATA:
          q += searchMetadata2PrettyQuery((SearchMetadata) el);
          break;
        default:
          break;
      }
    }
    return removeUseLessLogicalOperation(q.trim());
  }

  /**
   * Remove a logical operation if is not followed by a non empty search element
   * 
   * @param q
   * @return
   */
  private static String removeUseLessLogicalOperation(String q) {
    SessionBean session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
    String orString = session.getLabel("or_big");
    String andString = session.getLabel("and_big");
    if (q.endsWith(" ")) {
      q = q.substring(0, q.length() - 1);
    }
    if (q.endsWith(" " + andString)) {
      q = q.substring(0, q.length() - andString.length());
    }
    if (q.endsWith(" " + orString)) {
      q = q.substring(0, q.length() - orString.length());
    }
    if (q.startsWith(orString)) {
      q = q.substring(orString.length(), q.length());
    }
    if (q.startsWith(andString)) {
      q = q.substring(andString.length(), q.length());
    }
    if (q.endsWith(" ") || q.endsWith(" " + session.getLabel("and_big"))
        || q.endsWith(" " + session.getLabel("or_big"))) {
      q = removeUseLessLogicalOperation(q);
    }
    return q.trim();
  }

  /**
   * transform a namespace of a {@link SearchIndex} into a user friendly value
   * 
   * @param namespace
   * @return
   */
  public static String indexNamespace2PrettyQuery(String namespace) {
    String s[] = namespace.split("/");
    if (s.length > 0) {
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
  private static String searchOperator2PrettyQuery(SearchOperators op) {
    switch (op) {
      case GREATER:
        return ">=";
      case LESSER:
        return "<=";
      case EQUALS:
        return "==";
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
  private static String negation2PrettyQuery(boolean isNot) {
    if (isNot) {
      return "!";
    }
    return "";
  }

  /**
   * Special case to display a search for a metadata in a
   * 
   * @param group
   * @return
   */
  private static String searchMetadata2PrettyQuery(SearchMetadata md) {
    String label = ((MetadataLabels) BeanHelper.getSessionBean(MetadataLabels.class))
        .getInternationalizedLabels().get(md.getStatement());
    if (label == null) {
      label = "Metadata-" + indexNamespace2PrettyQuery(md.getStatement().toString());
    }
    switch (md.getField()) {
      case coordinates:
        label += "(" + ((SessionBean) BeanHelper.getSessionBean(SessionBean.class))
            .getLabel("geolocation_location") + ")";
        break;
      case person_family:
        label += "("
            + ((SessionBean) BeanHelper.getSessionBean(SessionBean.class)).getLabel("family_name")
            + ")";
        break;
      case person_given:
        label += "("
            + ((SessionBean) BeanHelper.getSessionBean(SessionBean.class)).getLabel("first_name")
            + ")";
        break;
      case person_id:
        label += "( "
            + ((SessionBean) BeanHelper.getSessionBean(SessionBean.class)).getLabel("identifier")
            + ")";
        break;
      case person_org_name:
        label += "("
            + ((SessionBean) BeanHelper.getSessionBean(SessionBean.class)).getLabel("organization")
            + ")";
        break;
      case url:
        label += "(" + ((SessionBean) BeanHelper.getSessionBean(SessionBean.class)).getLabel("url")
            + ")";
        break;
      default:
        break;
    }
    return label + " " + negation2PrettyQuery(md.isNot())
        + searchOperator2PrettyQuery(md.getOperator()) + " " + md.getValue();
  }
}

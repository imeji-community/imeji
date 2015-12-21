package de.mpg.imeji.logic.search.elasticsearch.util;

import java.util.Map;

import org.apache.lucene.queryparser.classic.QueryParser;

import de.mpg.imeji.logic.search.elasticsearch.ElasticService;
import de.mpg.imeji.logic.search.elasticsearch.model.ElasticFields;

/**
 * Utility Class for ElasticSearch
 * 
 * @author bastiens
 *
 */
public class ElasticSearchUtil {

  /**
   * Read the field of an object in Elasticsearch. The value is returned as String
   * 
   * @param id
   * @param field
   * @param dataType
   * @param index
   * @return
   */
  public static String readFieldAsString(String id, ElasticFields field, String dataType,
      String index) {
    Map<String, Object> sourceMap = ElasticService.client.prepareGet(index, dataType, id)
        .setFetchSource(true).execute().actionGet().getSource();
    if (sourceMap != null) {
      Object obj = sourceMap.get(field.field());
      return obj != null ? obj.toString() : "";
    }
    return "";
  }

  /**
   * Escape input to avoid error in Elasticsearch. * and ? are unescaped, to allow wildcard search
   * 
   * @param s
   * @return
   */
  public static String escape(String s) {
    return QueryParser.escape(s).replace("\\*", "*").replace("\\?", "?").replace("\\\"", "\"");
  }
}

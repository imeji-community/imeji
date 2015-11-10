package de.mpg.imeji.logic.search.elasticsearch.util;

import java.util.Map;

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

}

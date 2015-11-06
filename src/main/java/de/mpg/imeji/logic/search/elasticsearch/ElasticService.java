package de.mpg.imeji.logic.search.elasticsearch;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

import de.mpg.imeji.presentation.util.PropertyReader;

/**
 * elasticsearch service for spot
 * 
 * @author bastiens
 * 
 */
public class ElasticService {
  private static Node node;
  public static Client client;
  private static String CLUSTER_NAME = "name of my cluster";
  private static final Logger logger = Logger.getLogger(ElasticService.class);

  /**
   * The Index in Elasticsearch
   * 
   * @author bastiens
   * 
   */
  public enum ElasticIndex {
    data;
  }
  /**
   * The Types in Elasticsearch
   * 
   * @author bastiens
   * 
   */
  public enum ElasticTypes {
    items, folders, albums, spaces;
  }

  public static void start() throws IOException, URISyntaxException {
    CLUSTER_NAME = PropertyReader.getProperty("elastic.cluster.name");
    node = NodeBuilder.nodeBuilder().clusterName(CLUSTER_NAME).node();
    client = node.client();
    new ElasticIndexer(ElasticIndex.data, ElasticTypes.items).addMapping();
    new ElasticIndexer(ElasticIndex.data, ElasticTypes.folders).addMapping();
    new ElasticIndexer(ElasticIndex.data, ElasticTypes.albums).addMapping();
    new ElasticIndexer(ElasticIndex.data, ElasticTypes.spaces).addMapping();
  }

  /**
   * delete all data from elasticsearch
   */
  public static void deleteAll() {
    DeleteIndexResponse delete =
        ElasticService.client.admin().indices().delete(new DeleteIndexRequest("_all")).actionGet();
    if (!delete.isAcknowledged()) {
      // Error
    }
  }

  public static void shutdown() {
    node.close();
  }
}

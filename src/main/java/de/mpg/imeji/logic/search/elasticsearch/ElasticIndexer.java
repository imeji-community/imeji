package de.mpg.imeji.logic.search.elasticsearch;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.search.SearchIndexer;
import de.mpg.imeji.logic.search.elasticsearch.ElasticService.ElasticIndex;
import de.mpg.imeji.logic.search.elasticsearch.ElasticService.ElasticTypes;
import de.mpg.imeji.logic.search.elasticsearch.model.ElasticAlbum;
import de.mpg.imeji.logic.search.elasticsearch.model.ElasticFolder;
import de.mpg.imeji.logic.search.elasticsearch.model.ElasticItem;
import de.mpg.imeji.logic.search.model.SearchIndex.SearchFields;
import de.mpg.imeji.logic.search.model.SearchOperators;
import de.mpg.imeji.logic.search.model.SearchPair;
import de.mpg.imeji.logic.search.model.SearchQuery;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Properties;

/**
 * Indexer for ElasticSearch
 * 
 * @author bastiens
 * 
 */
public class ElasticIndexer implements SearchIndexer {
  private static Logger logger = Logger.getLogger(ElasticIndexer.class);
  private static final ObjectMapper mapper = new ObjectMapper();
  private String index = "data";
  private String dataType;
  private String mappingFile = "elasticsearch/Elastic_TYPE_Mapping.json";


  public ElasticIndexer(ElasticIndex index, ElasticTypes dataType) {
    this.index = index.name();
    this.dataType = dataType.name();
    this.mappingFile = mappingFile.replace("_TYPE_", StringUtils.capitalize(this.dataType));
  }


  @Override
  public void index(Object obj) {
    try {
      indexJSON(getId(obj), toJson(obj));
      commit();
    } catch (UnprocessableError e) {
      logger.error("Error indexing object ", e);
    }
  }

  @Override
  public void indexBatch(List<?> l) {
    try {
      for (Object obj : l) {
        indexJSON(getId(obj), toJson(obj));
      }
      commit();
    } catch (Exception e) {
      logger.error("error indexing object ", e);
      e.printStackTrace();
    }
  }


  @Override
  public void delete(Object obj) {
    ElasticService.client.prepareDelete(index, dataType, getId(obj)).execute();
  }

  @Override
  public void deleteBatch(List<?> l) {
    for (Object obj : l) {
      ElasticService.client.prepareDelete(index, dataType, getId(obj)).execute();
    }
  }



  /**
   * Transform an object to a json
   * 
   * @param obj
   * @return
   * @throws UnprocessableError
   */
  public static String toJson(Object obj) throws UnprocessableError {
    try {
      return mapper.writeValueAsString(toESEntity(obj));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      throw new UnprocessableError("Error serializing object to json", e);
    }
  }

  /**
   * Index in Elasticsearch the passed json with the given id
   * 
   * @param id
   * @param json
   */
  public void indexJSON(String id, String json) {
    ElasticService.client.prepareIndex(index, dataType).setId(id).setSource(json).execute()
        .actionGet();
  }

  /**
   * Make all changes done searchable. Kind of a commit. Might be important if data needs to be
   * immediately available for other tasks
   */
  public void commit() {
    ElasticService.client.admin().indices().prepareRefresh(index).execute().actionGet();
  }

  /**
   * Remove all indexed data
   */
  public static void clear(ElasticIndex index) {
    DeleteIndexResponse delete =
        ElasticService.client.admin().indices().delete(new DeleteIndexRequest(index.name()))
            .actionGet();
    if (!delete.isAcknowledged()) {
      // Error
    }
  }

  /**
   * Transform a model Entity into an Elasticsearch Entity
   * 
   * @param obj
   * @return
   */
  private static Object toESEntity(Object obj) {
    if (obj instanceof Item) {
      obj = setAlbums((Item) obj);
      return new ElasticItem((Item) obj);
    } else if (obj instanceof CollectionImeji) {
      return new ElasticFolder((CollectionImeji) obj);
    } else if (obj instanceof Album) {
      return new ElasticAlbum((Album) obj);
    }
    return obj;
  }

  /**
   * Set the albums of an item
   * 
   * @param item
   * @return
   */
  private static Item setAlbums(Item item) {
    AlbumController c = new AlbumController();
    SearchQuery q = new SearchQuery();
    q.addPair(new SearchPair(SearchFields.member, SearchOperators.EQUALS, item.getId().toString(),
        false));
    item.setAlbums(c.search(q, Imeji.adminUser, null, -1, 0, null).getResults());
    return item;
  }

  /**
   * Get the Id of an Object
   * 
   * @param obj
   * @return
   */
  private String getId(Object obj) {
    if (obj instanceof Properties) {
      return ((Properties) obj).getId().toString();
    }
    return null;
  }

  /**
   * Add a mapping to the fields (important to have a better search)
   */
  public void addMapping() {
    try {
      String jsonMapping =
          new String(Files.readAllBytes(Paths.get(ElasticIndexer.class.getClassLoader()
              .getResource(mappingFile).toURI())), "UTF-8");
      try {
        ElasticService.client.admin().indices().create(new CreateIndexRequest(this.index))
            .actionGet();
      } catch (Exception e) {
        logger.info("Index already existing");
      }
      ElasticService.client.admin().indices().preparePutMapping(this.index).setType(dataType)
          .setSource(jsonMapping).execute().actionGet();
    } catch (Exception e) {
      logger.error("Error initializing the Elastic Search Mapping", e);
      e.printStackTrace();
    }
  }

}

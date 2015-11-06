package de.mpg.imeji.logic.search.elasticsearch;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.get.GetResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.search.SearchIndexer;
import de.mpg.imeji.logic.search.elasticsearch.ElasticService.ElasticIndex;
import de.mpg.imeji.logic.search.elasticsearch.ElasticService.ElasticTypes;
import de.mpg.imeji.logic.search.elasticsearch.model.ElasticAlbum;
import de.mpg.imeji.logic.search.elasticsearch.model.ElasticFields;
import de.mpg.imeji.logic.search.elasticsearch.model.ElasticFolder;
import de.mpg.imeji.logic.search.elasticsearch.model.ElasticItem;
import de.mpg.imeji.logic.search.elasticsearch.model.ElasticSpace;
import de.mpg.imeji.logic.search.model.SearchIndex.SearchFields;
import de.mpg.imeji.logic.search.model.SearchOperators;
import de.mpg.imeji.logic.search.model.SearchPair;
import de.mpg.imeji.logic.search.model.SearchQuery;
import de.mpg.imeji.logic.vo.Album;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Properties;
import de.mpg.imeji.logic.vo.Space;

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
    List<String> collectionsToReindex = new ArrayList<String>();
    try {
      
       if (dataType.equals(ElasticTypes.folders.name())) {
          //reindex items and collections in Space (check first if this has been changed)
         if ( isSpaceCollectionChanged((CollectionImeji)obj, dataType, index)){
           collectionsToReindex.add(getId(obj));
         }
       
       }

       indexJSON(getId(obj), toJson(obj, dataType, index));
      
       commit();
       
       //Note: there is no need to explicitly index collections, as these actually are updated whenever they are assigned to a space, 
       // thus the indexing is automatically triggered.
       if (collectionsToReindex.size() >0 ) {
           logger.info("There has been a collections space change, items in "+collectionsToReindex.size()+" collections will be reindexed! ");
           for (String collectionR:collectionsToReindex) {
               reindexItemsInContainer(collectionR);
           }
       }
       
    } catch (Exception e) {
      logger.error("Error indexing object ", e);
    }
  }

  @Override
  public void indexBatch(List<?> l) {
    List<String> collectionsToReindex = new ArrayList<String>();
    try {
      for (Object obj : l) {
        
        if (dataType.equals(ElasticTypes.folders.name())) {
            //reindex items and collections in Space (check first if this has been changed)
            if ( isSpaceCollectionChanged((CollectionImeji)obj, dataType, index)){
                  collectionsToReindex.add(getId(obj));
            }
        }
        
        indexJSON(getId(obj), toJson(obj, dataType, index));

      }
      commit();
      
      //Note: there is no need to explicitly index collections, as these actually are updated whenever they are assigned to a space, 
      // thus the indexing is automatically triggered with collection update.
      //here we get list of all collections with changed space to reindex in items
      if (collectionsToReindex.size() >0 ) {
          logger.info("There has been a collections space change, items in "+collectionsToReindex.size()+" collections will be reindexed! ");
          for (String collectionR:collectionsToReindex) {
              reindexItemsInContainer(collectionR);
          }
      }
     
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
  public static String toJson(Object obj, String dataType, String index) throws UnprocessableError {
    try {
      return mapper.writeValueAsString(toESEntity(obj, dataType, index));
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
  private static Object toESEntity(Object obj, String dataType, String index) {
    if (obj instanceof Item) {
      obj = setAlbums((Item) obj);
      ElasticItem es = new ElasticItem((Item) obj);
      es.setSpace(getSpace((Item)obj));
      return es;
    } else if (obj instanceof CollectionImeji) {
      ElasticFolder ef = new ElasticFolder((CollectionImeji) obj);
      return ef;
    } else if (obj instanceof Album) {
      return new ElasticAlbum((Album) obj);
    } else if (obj instanceof Space) {
      return new ElasticSpace((Space) obj);
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
  
  
  /**
   * Retrieve the space of the Item
   * 
   * @param item
   * @return
   */
  //TODO: this method at the moment goes to the controller and there it fetches the space from Jena. This 
  //check should accordingly be replaced with a cached Map object, containing URIs of all existing collections and spaces - or other
  //type of optimization
  private static String getSpace(Item item) {
    CollectionController c = new CollectionController();
      return c.retrieveSpaceOfCollection(item.getCollection());
  }
  
  private  static boolean isSpaceCollectionChanged (CollectionImeji ef, String dataType, String index)
  {
    
      GetResponse gr = ElasticService.client.prepareGet(index, dataType, ef.getId().toString()).setFetchSource(true).execute().actionGet();
      Map<String, Object> fieldMap = gr.getSourceAsMap();

      String oldSpaceInCollection =  fieldMap != null ? 
            (String)fieldMap.get(ElasticFields.SPACE.name().toLowerCase()): "";
        
      String newSpaceInCollection = ef.getSpace() != null? ef.getSpace().toString()   :"";
      
      if (!oldSpaceInCollection.equals(newSpaceInCollection)) {
                logger.info("Collection space for "+ef.getId().toString()+" has been changed! Items will be reindexed! ");
                return true;
        }

      return false;
  }
  
  
  /**
   * Reindex all {@link Item} stored in the database
   * 
   * @throws ImejiException
   * @throws URISyntaxException 
   * @throws IOException 
   * 
   */
  //TODO - this probably should be done solely via Elastic Index, without any need to retrieve Items again  
  
  private void reindexItemsInContainer(String containerUri) throws ImejiException, IOException, URISyntaxException {
    logger.info("Indexing Items in container  ..."+containerUri);
    
    ElasticIndexer indexer = new ElasticIndexer(ElasticIndex.data, ElasticTypes.items);
    indexer.addMapping();
    
    ItemController controller = new ItemController();
    List<Item> items = controller.searchAndRetrieve(new URI(containerUri), (SearchQuery)null, null, Imeji.adminUser, null, -1, -1);
    logger.info("+++ " + items.size() + " items in Collection to reindex +++");
    indexer.indexBatch(items);
    indexer.commit();

    logger.info("Items in container "+containerUri+" are reindexed!");
  }
  
}

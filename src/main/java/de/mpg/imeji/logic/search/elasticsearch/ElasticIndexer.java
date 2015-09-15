package de.mpg.imeji.logic.search.elasticsearch;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.AlbumController;
import de.mpg.imeji.logic.controller.ItemController;
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
      specialIndex(obj);
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
        specialIndex(obj);
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
   * Special Index operations:<br/>
   * * By indexing the member of {@link Album}
   * 
   * @param obj
   */
  private void specialIndex(Object obj) {
    if (obj instanceof Album) {
      addRelationFromItemsToAlbums((Album) obj);
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
      return new ElasticItem((Item) obj);
    } else if (obj instanceof CollectionImeji) {
      return new ElasticFolder((CollectionImeji) obj);
    } else if (obj instanceof Album) {
      return new ElasticAlbum((Album) obj);
    }
    return obj;
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
   * Add the album id to the item which are member of this album. Important to query items within an
   * album
   * 
   * @param album
   */
  private void addRelationFromItemsToAlbums(Album album) {
    try {
      cleanRelatedItems(album);
      addRelatedItems(album);
    } catch (ImejiException e) {
      e.printStackTrace();
      logger.error(e);
    }
  }

  /**
   * Check that all meber of an album are related to him. If not, add the relation
   * 
   * @param album
   * @throws ImejiException
   */
  private void addRelatedItems(Album album) throws ImejiException {
    ItemController itemController = new ItemController();
    ElasticIndexer itemIndexer = new ElasticIndexer(ElasticIndex.data, ElasticTypes.items);
    // Get all items related to the album
    List<String> relatedItems =
        itemController.search(album.getId(), null, null, null, Imeji.adminUser, null).getResults();
    // Find Items where the relation is missing, i.e member of the album but not related
    List<Item> itemsToUpdate = new ArrayList<>();
    for (URI uri : album.getImages()) {
      if (!isInList(relatedItems, uri.toString())) {
        Item item = retrieveItemWithIndexedAlbums(uri.toString());
        itemsToUpdate.add(item);
      }
    }
    itemIndexer.indexBatch(itemsToUpdate);
  }

  /**
   * Check all Items related to this album. If the relation is wrong (i.e the item is not an actual
   * member of the album), remove the relation<br/>
   * An Item ia related to an album, if the album exists in the field item.album
   * 
   * @param album
   * @throws ImejiException
   */
  private void cleanRelatedItems(Album album) throws ImejiException {
    ItemController itemController = new ItemController();
    ElasticIndexer itemIndexer = new ElasticIndexer(ElasticIndex.data, ElasticTypes.items);
    List<String> albumMembers = new ArrayList<>();
    for (URI uri : album.getImages()) {
      albumMembers.add(uri.toString());
    }
    // Get all items related to the album
    List<String> indexedItems =
        itemController.search(album.getId(), null, null, null, Imeji.adminUser, null).getResults();
    // Find Items where the relation is wrong, i.e. not member of the item
    List<Item> itemsToUpdate = new ArrayList<>();
    for (String uri : indexedItems) {
      if (!isInList(albumMembers, uri)) {
        Item item = retrieveItemWithIndexedAlbums(uri);
        item.getAlbums().remove(album.getId().toString()); // remove the relation to the album
        itemsToUpdate.add(item);
      }
    }
    itemIndexer.indexBatch(itemsToUpdate);
  }

  /**
   * Retrieve an Item with all its album where it is into
   * 
   * @param uri
   * @return
   * @throws ImejiException
   */
  private Item retrieveItemWithIndexedAlbums(String uri) throws ImejiException {
    ItemController itemController = new ItemController();
    Item item = itemController.retrieve(URI.create(uri), Imeji.adminUser);// retrieve the item
    item.setAlbums(searchAlbums(item)); // Set the albums the items belongs to
    return item;
  }

  /**
   * Search all albums where the Item is into
   * 
   * @param item
   * @return
   */
  private List<String> searchAlbums(Item item) {
    AlbumController albumController = new AlbumController();
    SearchQuery q = new SearchQuery();
    q.addPair(new SearchPair(SearchFields.member, SearchOperators.EQUALS, item.getId().toString(),
        false));
    return albumController.search(q, Imeji.adminUser, null, -1, 0, null).getResults();
  }

  /**
   * Return true if the Item
   * 
   * @param items
   * @param member
   * @return
   */
  private boolean isInList(Collection<String> uris, String id) {
    for (String uri : uris) {
      if (uri.equals(id)) {
        return true;
      }
    }
    return false;
  }


}

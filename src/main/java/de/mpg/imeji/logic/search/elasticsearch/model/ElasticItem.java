package de.mpg.imeji.logic.search.elasticsearch.model;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;

/**
 * The object which is indexed in Elastic search <br/>
 * !!! IMPORTANT !!!<br/>
 * This File must be synchronized with resources/elasticsearch/ElasticItemsMapping.json
 * 
 * @author bastiens
 * 
 */
public class ElasticItem extends ElasticProperties {

  private String folder;
  private String filename;
  private String filetype;
  private String space;
  private String checksum;
  private long width;
  private long height;
  private List<ElasticMetadata> metadata = new ArrayList<>();
  private List<String> album;


  /**
   * Constructor with an {@link Item}
   * 
   * @param item
   */
  public ElasticItem(Item item) {
    super(item);
    this.checksum = item.getChecksum();
    this.folder = item.getCollection().toString();
    this.filename = item.getFilename();
    this.filetype = item.getFiletype();
    this.height = item.getHeight();
    this.width = item.getWidth();
    this.album = item.getAlbums();
    //Space is set-up after ElasticItem call is initialized
    this.space = "";
    for (Metadata md : item.getMetadataSet().getMetadata()) {
      metadata.add(new ElasticMetadata(md));
    }
  }


  /**
   * @return the filename
   */
  public String getFilename() {
    return filename;
  }


  /**
   * @param filename the filename to set
   */
  public void setFilename(String filename) {
    this.filename = filename;
  }


  /**
   * @return the filetype
   */
  public String getFiletype() {
    return filetype;
  }


  /**
   * @param filetype the filetype to set
   */
  public void setFiletype(String filetype) {
    this.filetype = filetype;
  }


  /**
   * @return the checksum
   */
  public String getChecksum() {
    return checksum;
  }


  /**
   * @param checksum the checksum to set
   */
  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }


  /**
   * @return the width
   */
  public long getWidth() {
    return width;
  }


  /**
   * @param width the width to set
   */
  public void setWidth(long width) {
    this.width = width;
  }


  /**
   * @return the height
   */
  public long getHeight() {
    return height;
  }


  /**
   * @param height the height to set
   */
  public void setHeight(long height) {
    this.height = height;
  }


  /**
   * @return the metadata
   */
  public List<ElasticMetadata> getMetadata() {
    return metadata;
  }


  /**
   * @param metadata the metadata to set
   */
  public void setMetadata(List<ElasticMetadata> metadata) {
    this.metadata = metadata;
  }


  /**
   * @return the folder
   */
  public String getFolder() {
    return folder;
  }



  /**
   * @param folder the folder to set
   */
  public void setFolder(String folder) {
    this.folder = folder;
  }


  /**
   * @return the albums
   */
  public List<String> getAlbum() {
    return album;
  }


  /**
   * @param albums the albums to set
   */
  public void setAlbum(List<String> album) {
    this.album = album;
  }


  /**
   * @return the space
   */
  public String getSpace() {
    return space;
  }


  /**
   * @param space the space to set
   */
  public void setSpace(String space) {
    this.space = space;
  }

}

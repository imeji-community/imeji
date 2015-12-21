package de.mpg.imeji.logic.search.elasticsearch.model;

import java.net.URI;
import java.util.List;

import de.mpg.imeji.logic.vo.Album;

/**
 * The Elastic version of an {@link Album}
 * 
 * @author bastiens
 * 
 */
public class ElasticAlbum extends ElasticContainerProperties {

  private List<URI> member;

  public ElasticAlbum(Album a) {
    super(a);
    this.member = (List<URI>) a.getImages();

  }

  /**
   * @return the items
   */
  public List<URI> getMember() {
    return member;
  }

  /**
   * @param items the items to set
   */
  public void setMember(List<URI> items) {
    this.member = items;
  }



}

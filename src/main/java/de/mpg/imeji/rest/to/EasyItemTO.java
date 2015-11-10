package de.mpg.imeji.rest.to;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;

@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EasyItemTO implements Serializable {

  private static final long serialVersionUID = -1870847854605861134L;

  private String collectionId;

  private String fetchUrl;

  // private List<EasyTO> ez_metadata = new ArrayList<EasyTO>();

  private Map<String, JsonNode> ez_metadata = new HashMap<String, JsonNode>();

  public String getCollectionId() {
    return collectionId;
  }

  public void setCollectionId(String collectionId) {
    this.collectionId = collectionId;
  }

  public String getFetchUrl() {
    return fetchUrl;
  }

  public void setFetchUrl(String fetchUrl) {
    this.fetchUrl = fetchUrl;
  }

  // public List<EasyTO> getEz_metadata() {
  // return ez_metadata;
  // }
  //
  // public void setEz_metadata(List<EasyTO> ez_metadata) {
  // this.ez_metadata = ez_metadata;
  // }



  public Map<String, JsonNode> getEz_metadata() {
    return ez_metadata;
  }

  public void setEz_metadata(Map<String, JsonNode> ez_metadata) {
    this.ez_metadata = ez_metadata;
  }



}

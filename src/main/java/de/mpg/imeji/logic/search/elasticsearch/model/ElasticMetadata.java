package de.mpg.imeji.logic.search.elasticsearch.model;

import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson;
import de.mpg.imeji.logic.vo.predefinedMetadata.Date;
import de.mpg.imeji.logic.vo.predefinedMetadata.Geolocation;
import de.mpg.imeji.logic.vo.predefinedMetadata.License;
import de.mpg.imeji.logic.vo.predefinedMetadata.Link;
import de.mpg.imeji.logic.vo.predefinedMetadata.Publication;
import de.mpg.imeji.logic.vo.predefinedMetadata.Text;

/**
 * The indexed {@link Metadata}<br/>
 * !!! IMPORTANT !!!<br/>
 * This File must be synchronized with resources/elasticsearch/ElasticItemsMapping.json
 * 
 * @author bastiens
 * 
 */
public class ElasticMetadata extends ElasticPerson {
  private String statement;
  private String text;
  private double number;
  private String uri;
  private String type;
  private String location;


  /**
   * Constructor with a {@link Metadata}
   * 
   * @param md
   */
  public ElasticMetadata(Metadata md) {
    super();
    this.statement = ObjectHelper.getId(md.getStatement());
    this.type = md.getTypeNamespace();
    if (md instanceof Text) {
      this.text = ((Text) md).getText();
    } else if (md instanceof de.mpg.imeji.logic.vo.predefinedMetadata.Number) {
      this.number = ((de.mpg.imeji.logic.vo.predefinedMetadata.Number) md).getNumber();
    } else if (md instanceof Date) {
      this.text = ((Date) md).getDate();
      this.number = ((Date) md).getTime();
    } else if (md instanceof Link) {
      this.text = ((Link) md).getLabel();
      this.uri = ((Link) md).getUri().toString();
    } else if (md instanceof ConePerson) {
      setPerson(((ConePerson) md).getPerson());
    } else if (md instanceof Geolocation) {
      this.text = ((Geolocation) md).getName();
      this.location = ((Geolocation) md).getLatitude() + "," + ((Geolocation) md).getLongitude();
    } else if (md instanceof License) {
      this.text = ((License) md).getLicense();
      this.uri = ((License) md).getExternalUri() == null ? null
          : ((License) md).getExternalUri().toString();
    } else if (md instanceof Publication) {
      this.text = ((Publication) md).getCitation();
      this.uri =
          ((Publication) md).getUri() == null ? null : ((Publication) md).getUri().toString();
    }
  }

  /**
   * @return the statement
   */
  public String getStatement() {
    return statement;
  }


  /**
   * @param statement the statement to set
   */
  public void setStatement(String statement) {
    this.statement = statement;
  }


  /**
   * @return the text
   */
  public String getText() {
    return text;
  }


  /**
   * @param text the text to set
   */
  public void setText(String text) {
    this.text = text;
  }


  /**
   * @return the number
   */
  public double getNumber() {
    return number;
  }


  /**
   * @param number the number to set
   */
  public void setNumber(double number) {
    this.number = number;
  }


  /**
   * @return the uri
   */
  public String getUri() {
    return uri;
  }


  /**
   * @param uri the uri to set
   */
  public void setUri(String uri) {
    this.uri = uri;
  }


  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * @param location the location to set
   */
  public void setLocation(String location) {
    this.location = location;
  }

}

package de.mpg.imeji.rest.to;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@XmlRootElement
@XmlType(propOrder = {"position", "Labels", "value", "statementUri", "typeUri",})
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(using = MetadataSetTODeserializer.class)
public class MetadataSetTO implements Serializable {

  private static final long serialVersionUID = 5826924314949469841L;

  @JsonIgnore
  private int position;

  private List<LabelTO> Labels = new ArrayList<LabelTO>();

  private MetadataTO value;

  private URI statementUri;

  private URI typeUri;

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public List<LabelTO> getLabels() {
    return Labels;
  }

  public void setLabels(List<LabelTO> labels) {
    Labels = labels;
  }

  public MetadataTO getValue() {
    return value;
  }

  public void setValue(MetadataTO value) {
    this.value = value;

  }

  public URI getStatementUri() {
    return statementUri;
  }

  public void setStatementUri(URI statementUri) {
    this.statementUri = statementUri;
  }

  public URI getTypeUri() {
    return typeUri;
  }

  public void setTypeUri(URI typeUri) {
    this.typeUri = typeUri;
  }


}

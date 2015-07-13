package de.mpg.imeji.rest.to;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Created by vlad on 11.12.14.
 */
public class MetadataSetTODeserializer extends JsonDeserializer<MetadataSetTO> {


  @Override
  public MetadataSetTO deserialize(JsonParser jsonParser,
      DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

    ObjectCodec codec = jsonParser.getCodec();
    JsonNode rootNode = codec.readTree(jsonParser);

    MetadataSetTO mdSet = new MetadataSetTO();

    /*
     * JsonNode pos = rootNode.get("position"); if (pos != null) { mdSet.setPosition(pos.asInt()); }
     */

    JsonNode statementUri = rootNode.get("statementUri");
    if (statementUri != null) {
      mdSet.setStatementUri(URI.create(statementUri.asText()));
    }
    ArrayNode labels = (ArrayNode) rootNode.get("labels");
    if (labels != null) {
      List<LabelTO> ltoList = new ArrayList<LabelTO>();
      for (Iterator<JsonNode> iterator = labels.iterator(); iterator.hasNext();) {
        JsonNode labelJSON = iterator.next();
        ltoList
            .add(new LabelTO(labelJSON.get("language").asText(), labelJSON.get("value").asText()));
      }
      mdSet.setLabels(ltoList);
    }

    JsonNode typeUri = rootNode.get("typeUri");
    if (typeUri != null) {
      URI type = URI.create(typeUri.asText());
      mdSet.setTypeUri(type);
      try {
        // MetadataTO
        Class<MetadataTO> mdTOClass = MetadataTO.Types.getClassOfType(type);
        JsonNode valueNode = rootNode.get("value");
        MetadataTO mdTO = codec.treeToValue(valueNode, mdTOClass);
        mdSet.setValue(mdTO);

      } catch (IllegalAccessException | InstantiationException e) {
        Logger.getLogger(MetadataSetTODeserializer.class).info(
            "Exception, will return new Metadata Set TO", e);
        return new MetadataSetTO();
      }

    }

    return mdSet;
  }


}

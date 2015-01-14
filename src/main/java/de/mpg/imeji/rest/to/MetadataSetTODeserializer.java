package de.mpg.imeji.rest.to;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vlad on 11.12.14.
 */
public class MetadataSetTODeserializer extends JsonDeserializer<MetadataSetTO> {


    @Override
    public MetadataSetTO deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        ObjectCodec codec = jsonParser.getCodec();
        JsonNode rootNode = codec.readTree(jsonParser);

        MetadataSetTO mdSet = new MetadataSetTO();

       /* JsonNode pos = rootNode.get("position");
        if (pos != null) {
            mdSet.setPosition(pos.asInt());
        }*/

        JsonNode statementUri = rootNode.get("statementUri");
        if (statementUri != null) {
            mdSet.setStatementUri(URI.create(statementUri.asText()));
        }

        JsonNode labels = rootNode.get("labels");
        if (labels != null) {
            List<LabelTO> ltoList = new ArrayList<LabelTO>();
            ltoList = codec.treeToValue(labels, ltoList.getClass());
            mdSet.setLabels(ltoList);
        }

        JsonNode typeUri = rootNode.get("typeUri");
        if (typeUri != null) {
            URI type = URI.create(typeUri.asText());
            mdSet.setTypeUri(type);
            try {
                //MetadataTO
                Class<MetadataTO> mdTOClass = MetadataTO.Types.getClassOfType(type);
                JsonNode valueNode = rootNode.get("value");
                MetadataTO mdTO = codec.treeToValue(valueNode, mdTOClass);
                mdSet.setValue(mdTO);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return new MetadataSetTO();
            } catch (InstantiationException e) {
                e.printStackTrace();
                return new MetadataSetTO();
            }

        }

        return mdSet;
    }


}

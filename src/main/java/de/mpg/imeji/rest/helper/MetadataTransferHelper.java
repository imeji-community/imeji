package de.mpg.imeji.rest.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultConePersonTO;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultGeolocationTO;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultLicenseTO;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultLinkTO;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultPublicationTO;
import de.mpg.imeji.rest.to.MetadataSetTO;
import de.mpg.imeji.rest.to.MetadataTO;
import de.mpg.imeji.rest.to.PersonTO;
import de.mpg.imeji.rest.to.StatementTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.ConePersonTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.DateTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.GeolocationTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.LicenseTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.LinkTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.NumberTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.PublicationTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.TextTO;

/**
 * Utility class for transfer of {@link MetadataTO} to or from {@link Metadata}
 * 
 * @author bastiens
 * 
 */
public class MetadataTransferHelper {

  private static JsonFactory factory = new JsonFactory();
  private static ObjectMapper mapper = new ObjectMapper(factory);

  /**
   * Parse the {@link JsonNode} into a List of {@link MetadataSetTO}.
   * 
   * @param json
   * @param statement
   * @return
   * @throws BadRequestException
   */
  public static List<MetadataSetTO> parseMetadata(JsonNode json, StatementTO statement)
      throws BadRequestException {
    List<MetadataSetTO> l = new ArrayList<MetadataSetTO>();
    String label = statement.getLabels().get(0).getValue();
    if (json.getNodeType() == JsonNodeType.ARRAY) {
      if (!statement.getMaxOccurs().equals("unbounded")) {
        throw new BadRequestException("Metadata \"" + label + "\" can only have one value");
      }
      for (Iterator<JsonNode> iterator = json.elements(); iterator.hasNext();) {
        JsonNode node = (JsonNode) iterator.next();
        l.add(parseMetadataTO(node, statement));
      }
    } else {
      l.add(parseMetadataTO(json, statement));
    }
    return l;
  }

  /**
   * Parse the {@link JsonNode} into a {@link MetadataSetTO}. Node cannot be an array!!!
   * 
   * @param json
   * @param statement
   * @return
   * @throws BadRequestException
   */
  private static MetadataSetTO parseMetadataTO(JsonNode json, StatementTO statement)
      throws BadRequestException {
    String type = statement.getType().toString();
    String label = statement.getLabels().get(0).getValue();
    MetadataSetTO metadata = new MetadataSetTO(statement);
    switch (Metadata.Types.valueOfUri(type)) {
      case TEXT:
        String textValue = json.textValue();
        if (textValue == null) {
          throw new BadRequestException("Wrong value <" + json.toString()
              + "> in metadata of type " + type + " with label " + label);
        } else {
          TextTO newT = new TextTO();
          newT.setText(textValue);
          metadata.setValue(newT);
        }
        break;
      case NUMBER:
        NumberTO newNT = new NumberTO();
        newNT.setNumber(json.asDouble());
        metadata.setValue(newNT);
        break;
      case CONE_PERSON:
        DefaultConePersonTO easyCPTO = null;
        try {
          easyCPTO = mapper.readValue(json.toString(), new TypeReference<DefaultConePersonTO>() {});
        } catch (Exception e) {
          throw new BadRequestException(label + e.getMessage());
        }
        ConePersonTO newCone =
            metadata.getValue() != null ? (ConePersonTO) metadata.getValue() : new ConePersonTO();
        PersonTO newP = metadata.getValue() != null ? newCone.getPerson() : new PersonTO();
        newP.setFamilyName(easyCPTO.getFamilyName());
        newP.setGivenName(easyCPTO.getGivenName());
        newCone.setPerson(newP);
        metadata.setValue(newCone);
        break;
      case DATE:
        DateTO newDT = new DateTO();
        newDT.setDate(json.textValue());
        metadata.setValue(newDT);
        break;
      case GEOLOCATION:
        DefaultGeolocationTO easyGeoTO = null;
        try {
          easyGeoTO =
              mapper.readValue(json.toString(), new TypeReference<DefaultGeolocationTO>() {});
        } catch (Exception e) {
          throw new BadRequestException(label + e.getMessage());
        }
        GeolocationTO newGT = new GeolocationTO();
        newGT.setName(easyGeoTO.getName());
        newGT.setLatitude(easyGeoTO.getLatitude());
        newGT.setLongitude(easyGeoTO.getLongitude());
        metadata.setValue(newGT);
        break;
      case LICENSE:
        DefaultLicenseTO easyLTO = null;
        try {
          easyLTO = mapper.readValue(json.toString(), new TypeReference<DefaultLicenseTO>() {});
        } catch (Exception e) {
          throw new BadRequestException(label + e.getMessage());
        }
        LicenseTO newLicense = new LicenseTO();
        newLicense.setLicense(easyLTO.getLicense());
        newLicense.setUrl(easyLTO.getUrl());
        metadata.setValue(newLicense);
        break;
      case LINK:
        DefaultLinkTO easyLinkTO = null;
        try {
          easyLinkTO = mapper.readValue(json.toString(), new TypeReference<DefaultLinkTO>() {});
        } catch (Exception e) {
          throw new BadRequestException(label + e.getMessage());
        }
        LinkTO newLink = new LinkTO();
        newLink.setLink(easyLinkTO.getLink());
        newLink.setUrl(easyLinkTO.getUrl());
        metadata.setValue(newLink);
        break;
      case PUBLICATION:
        DefaultPublicationTO easyPTO = null;
        try {
          easyPTO = mapper.readValue(json.toString(), new TypeReference<DefaultPublicationTO>() {});
        } catch (Exception e) {
          throw new BadRequestException(label + e.getMessage());
        }
        PublicationTO newPub = new PublicationTO();
        newPub.setCitation(easyPTO.getCitation());
        newPub.setFormat(easyPTO.getFormat());
        newPub.setPublication(easyPTO.getPublication());
        metadata.setValue(newPub);
        break;
    }
    return metadata;
  }

}

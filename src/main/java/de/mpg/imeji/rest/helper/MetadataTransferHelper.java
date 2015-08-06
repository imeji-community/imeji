package de.mpg.imeji.rest.helper;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson;
import de.mpg.imeji.logic.vo.predefinedMetadata.Geolocation;
import de.mpg.imeji.logic.vo.predefinedMetadata.License;
import de.mpg.imeji.logic.vo.predefinedMetadata.Link;
import de.mpg.imeji.logic.vo.predefinedMetadata.Publication;
import de.mpg.imeji.logic.vo.predefinedMetadata.Text;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultConePersonTO;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultGeolocationTO;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultLicenseTO;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultLinkTO;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultPublicationTO;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.process.TransferObjectFactory;
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
   * Serialize a list of {@link Metadata} to one {@link JsonNode}
   * 
   * @param metadataSet
   * @param profile
   * @return
   */
  public static Map<String, JsonNode> serializeMetadataSet(Collection<Metadata> metadataSet,
      MetadataProfile profile) {
    Map<String, JsonNode> json = new HashMap<String, JsonNode>();
    for (Statement statement : profile.getStatements()) {
      List<Metadata> l = filterMetadataByStatement(metadataSet, statement);
      if (l.size() == 1) {
        json.put(statement.getLabel(), serializeMetadata(l.get(0), statement));
      } else if (l.size() > 1) {
        List<JsonNode> nodes = new ArrayList<JsonNode>();
        for (Metadata md : l) {
          nodes.add(serializeMetadata(md, statement));
        }
        json.put(statement.getLabel(), RestProcessUtils.buildJsonNode(nodes));
      }
    }
    return json;
  }

  /**
   * Return only the metadata defined by the statement
   * 
   * @param metadataSet
   * @param statement
   * @return
   */
  private static List<Metadata> filterMetadataByStatement(Collection<Metadata> metadataSet,
      Statement statement) {
    List<Metadata> l = new ArrayList<Metadata>();
    for (Metadata md : metadataSet) {
      if (md.getStatement().equals(statement.getId())) {
        l.add(md);
      }
    }
    return l;
  }

  /**
   * Serialize a {@link Metadata} to one {@link JsonNode}
   * 
   * @param metadata
   * @param statement
   * @return
   */
  private static JsonNode serializeMetadata(Metadata metadata, Statement statement) {
    switch (Metadata.Types.valueOfUri(statement.getType().toString())) {
      case TEXT:
        return RestProcessUtils.buildJsonNode(((Text) metadata).getText());
      case NUMBER:
        return RestProcessUtils
            .buildJsonNode(((de.mpg.imeji.logic.vo.predefinedMetadata.Number) metadata).getNumber());
      case CONE_PERSON:
        DefaultConePersonTO pTO = new DefaultConePersonTO();
        TransferObjectFactory.transferDefaultPerson(((ConePerson) metadata).getPerson(), pTO);
        return RestProcessUtils.buildJsonNode(pTO);
      case DATE:
        return RestProcessUtils
            .buildJsonNode(((de.mpg.imeji.logic.vo.predefinedMetadata.Date) metadata).getDate());
      case GEOLOCATION:
        Geolocation mdGeo = (Geolocation) metadata;
        DefaultGeolocationTO dgto = new DefaultGeolocationTO();
        dgto.setName(mdGeo.getName());
        dgto.setLongitude(mdGeo.getLongitude());
        dgto.setLatitude(mdGeo.getLatitude());
        return RestProcessUtils.buildJsonNode(dgto);
      case LICENSE:
        License mdLicense = (License) metadata;
        DefaultLicenseTO dlto = new DefaultLicenseTO();
        dlto.setLicense(mdLicense.getLicense());
        URI externalUri = mdLicense.getExternalUri();
        dlto.setUrl(externalUri != null ? externalUri.toString() : "");
        return RestProcessUtils.buildJsonNode(dlto);
      case LINK:
        Link mdLink = (Link) metadata;
        DefaultLinkTO dllto = new DefaultLinkTO();
        dllto.setLink(mdLink.getLabel());
        dllto.setUrl(mdLink.getUri().toString());
        return RestProcessUtils.buildJsonNode(dllto);
      case PUBLICATION:
        Publication mdP = (Publication) metadata;
        DefaultPublicationTO dpto = new DefaultPublicationTO();
        dpto.setPublication(mdP.getUri().toString());
        dpto.setFormat(mdP.getExportFormat());
        dpto.setCitation(mdP.getCitation());
        return RestProcessUtils.buildJsonNode(dpto);
    }
    return null;
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

package de.mpg.imeji.testimpl.rest.resources.item;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.test.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_REST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.java.dev.webdav.jaxrs.ResponseStatus;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.JenaUtil;
import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.process.ItemProcess;
import de.mpg.imeji.rest.to.IdentifierTO;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.rest.to.LabelTO;
import de.mpg.imeji.rest.to.MetadataSetTO;
import de.mpg.imeji.rest.to.MetadataTO;
import de.mpg.imeji.rest.to.OrganizationTO;
import de.mpg.imeji.rest.to.PersonTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.ConePersonTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.DateTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.GeolocationTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.LicenseTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.LinkTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.NumberTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.PublicationTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.TextTO;
import de.mpg.imeji.test.rest.resources.test.integration.ItemTestBase;

/**
 * Created by vlad on 09.12.14.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemUpdateMetadata extends ItemTestBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(ItemUpdateMetadata.class);

  protected static String updateJSON;
  private static final String PATH_PREFIX = "/rest/items";



  @BeforeClass
  public static void specificSetup() throws Exception {
    updateJSON = getStringFromPath(STATIC_CONTEXT_REST + "/easyUpdateItemBasic.json");
    initCollectionWithProfile(getBasicStatements());
    initItemWithFullMetadata();
  }

  @Test
  public void test_1_UpdateItem_1_Change_Metadata_Statements_Allowed_Common() throws IOException,
      UnprocessableError, ImejiException {

    final String CHANGED = "allowed_change";
    double NUM = 90;
    final String REP_CHANGED = "$1\"" + CHANGED + "\"";
    final String NUM_CHANGED = "$1" + NUM;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date();
    final String DATE_CHANGED = "$1\"" + dateFormat.format(date) + "\"";

    FormDataMultiPart multiPart = new FormDataMultiPart();
    
    multiPart.field(
        "json",
        updateJSON
            .replace("___FILE_NAME___", CHANGED)
             .replace("___COLLECTION_ID___", collectionId)
             .replace("___ITEM_ID___", itemId)
            // text
            .replaceAll("(\"text\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            // person
            .replaceAll("(\"familyName\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            .replaceAll("(\"givenName\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            .replaceAll("(\"completeName\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            .replaceAll("(\"alternativeName\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            .replaceAll("(\"role\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            // organization
            .replaceAll("(\"name\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            .replaceAll("(\"description\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            .replaceAll("(\"country\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            .replaceAll("(\"city\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            // number
            .replaceAll("(\"number\"\\s*:\\s*).+", NUM_CHANGED+", ")
            // geo
            .replaceAll("(\"longitude\"\\s*:\\s*).+", NUM_CHANGED + ",")
            .replaceAll("(\"latitude\"\\s*:\\s*).+", NUM_CHANGED)
            // date
            .replaceAll("(\"date\"\\s*:\\s*)\"(.+)\"", DATE_CHANGED)
            // license
            .replaceAll("(\"license\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            .replaceAll("(\"url\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            // link
            .replaceAll("(\"link\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            // publication
            .replaceAll("(\"format\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            .replaceAll("(\"publication\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            .replaceAll("(\"citation\"\\s*:\\s*)\"(.+)\"", REP_CHANGED));

    //LOGGER.info(multiPart.getField("json").getValue());

    Response response =
        target(PATH_PREFIX).path("/" + itemId)
            .register(authAsUser)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( OK.getStatusCode(), response.getStatus());
    
    //DefaultItemTO updatedDItem = (DefaultItemTO) response.readEntity(DefaultItemTO.class);
    ItemTO updatedItem = new ItemTO();

    updatedItem = ItemProcess.prepareDefaultItemTOAsItemTO(true, response.readEntity(String.class), updatedItem, itemId, JenaUtil.testUser);
    itemTO = updatedItem;

    //LOGGER.info(buildJSONFromObject(updatedItem));

    assertThat(updatedItem.getFilename(), equalTo(CHANGED));

    List<MetadataSetTO> mds = updatedItem.getMetadata();

    // text
    assertThat(((TextTO) mds.get(0).getValue()).getText(), equalTo(CHANGED));

    // geolocation
    final GeolocationTO geolocationTO = (GeolocationTO) mds.get(3).getValue();
    assertThat(geolocationTO.getName(), equalTo(CHANGED));
    assertThat(geolocationTO.getLatitude(), equalTo(NUM));
    assertThat(geolocationTO.getLongitude(), equalTo(NUM));

    // number
    assertThat(((NumberTO) mds.get(1).getValue()).getNumber(), equalTo(NUM));

    // person
    PersonTO person = ((ConePersonTO) mds.get(2).getValue()).getPerson();
    assertThat(person.getFamilyName(), equalTo(CHANGED));
    assertThat(person.getGivenName(), equalTo(CHANGED));
    final OrganizationTO organizationTO = person.getOrganizations().get(0);
    assertThat(organizationTO.getName(), equalTo(CHANGED));
    assertThat(organizationTO.getCountry(), equalTo(CHANGED));
    assertThat(organizationTO.getCity(), equalTo(CHANGED));
    assertThat(organizationTO.getDescription(), equalTo(CHANGED));

    // date
    assertThat(((DateTO) mds.get(4).getValue()).getDate(), equalTo(dateFormat.format(date)));

    // license
    final LicenseTO licenseTO = (LicenseTO) mds.get(5).getValue();
    assertThat(licenseTO.getLicense(), equalTo(CHANGED));
    assertThat(licenseTO.getUrl(), equalTo(CHANGED));

    // link
    assertThat(((LinkTO) mds.get(6).getValue()).getLink(), equalTo(CHANGED));
    assertThat(((LinkTO) mds.get(6).getValue()).getUrl(), equalTo(CHANGED));

    // publication
    assertThat(((PublicationTO) mds.get(7).getValue()).getFormat(), equalTo(CHANGED));
    assertThat(((PublicationTO) mds.get(7).getValue()).getPublication(), equalTo(CHANGED));
  }


  
@Test
  public void test_2_UpdateItem_2_Change_Metadata_Statements_Not_Allowed() throws IOException,
      UnprocessableError, ImejiException {

    final String CHANGED = "not_allowed_change";
    final String REP_CHANGED = "$1\"" + CHANGED + "\"";

    FormDataMultiPart multiPart = new FormDataMultiPart();
    
    // identifiers
    MetadataSetTO md =
        itemTO.filterMetadataByTypeURI(MetadataTO.getTypeURI(ConePersonTO.class)).get(0);
    PersonTO p = ((ConePersonTO) md.getValue()).getPerson();

    for (IdentifierTO ident : p.getIdentifiers()) {
      ident.setType(CHANGED);
      ident.setValue(CHANGED);
    }
    for (OrganizationTO org : p.getOrganizations()) {
      for (IdentifierTO ident : org.getIdentifiers()) {
        ident.setType(CHANGED);
        ident.setValue(CHANGED);
      }
    }

    int i = 0;
    for (IdentifierTO identifier:p.getIdentifiers()) {
        if (i == 0) {
          identifier.setValue(CHANGED);
          identifier.setType(CHANGED);
          identifier = p.getOrganizations().get(i).getIdentifiers().get(i);
          identifier.setValue(CHANGED);
          identifier.setType(CHANGED);
        }
   }

    // labels
    for (MetadataSetTO mds : itemTO.getMetadata()) {
      for (LabelTO lb : mds.getLabels()) {
        lb.setValue(CHANGED);
        lb.setLanguage(CHANGED);
      }
    }

    multiPart.field(
        "json",
        updateJSON
            .replace("___COLLECTION_ID___", collectionId)
            .replace("___ITEM_ID___", itemId)
            // item properties
            .replaceAll("(\"fullname\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
            .replaceAll("(\"userId\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
            .replaceAll("(\"createdDate\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
            .replaceAll("(\"modifiedDate\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
            .replaceAll("(\"versionDate\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
            .replaceAll("(\"status\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
            .replaceAll("(\"version\"\\s*:\\s*)(.*)", "$1-1,")
            .replaceAll("(\"discardComment\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
            .replaceAll("(\"visibility\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
            .replaceAll("(\"mimetype\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
            .replaceAll("(\"checksumMd5\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
            .replaceAll("(\"webResolutionUrlUrl\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
            .replaceAll("(\"thumbnailUrl\"\\s*:\\s*)\"(.*)\"", REP_CHANGED)
            .replaceAll("(\"fileUrl\"\\s*:\\s*)\"(.*)\"", REP_CHANGED));

    //LOGGER.info(multiPart.getField("json").getValue());

    Response response =
        target(PATH_PREFIX).path("/" + itemId)
            .register(authAsUser)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals(OK.getStatusCode(), response.getStatus() );
    
      ItemTO updItem = new ItemTO();
      updItem.setCollectionId(collectionId);
      updItem = ItemProcess.prepareDefaultItemTOAsItemTO(false, response.readEntity(String.class), updItem, itemId, JenaUtil.testUser);
      itemTO = updItem;

    // LOGGER.info(buildJSONFromObject(updItem));

    // assertThat(updItem.getCreatedBy().getFullname(), not(equalTo(CHANGED)));
    assertThat(updItem.getCreatedBy().getUserId(), not(equalTo(CHANGED)));
    assertThat(updItem.getModifiedBy().getFullname(), not(equalTo(CHANGED)));
    assertThat(updItem.getModifiedBy().getUserId(), not(equalTo(CHANGED)));

    assertThat(updItem.getCreatedDate(), not(equalTo(CHANGED)));
    assertThat(updItem.getModifiedDate(), not(equalTo(CHANGED)));
    assertThat(updItem.getVersionDate(), not(equalTo(CHANGED)));

    assertThat(updItem.getStatus(), not(equalTo(CHANGED)));

    assertThat(updItem.getVersion(), not(equalTo(-1)));

    assertThat(updItem.getDiscardComment(), not(equalTo(CHANGED)));
    assertThat(updItem.getVisibility(), not(equalTo(CHANGED)));
    assertThat(updItem.getCollectionId(), not(equalTo(CHANGED)));
    assertThat(updItem.getMimetype(), not(equalTo(CHANGED)));
    assertThat(updItem.getChecksumMd5(), not(equalTo(CHANGED)));
    assertThat(updItem.getWebResolutionUrlUrl().toString(), not(equalTo(CHANGED)));
    assertThat(updItem.getThumbnailUrl().toString(), not(equalTo(CHANGED)));
    assertThat(updItem.getFileUrl().toString(), not(equalTo(CHANGED)));


    List<MetadataSetTO> mdsList = updItem.getMetadata();

    for (MetadataSetTO mds : mdsList) {
      // statements
      assertThat(mds.getStatementUri().toString(), not(equalTo(CHANGED)));
      assertThat(mds.getTypeUri().toString(), not(equalTo(CHANGED)));
      // labels
      for (LabelTO l : mds.getLabels()) {
        assertThat(l.getValue(), not(equalTo(CHANGED)));
        assertThat(l.getLanguage(), not(equalTo(CHANGED)));
      }
    }

    // person
    p = ((ConePersonTO) mdsList.get(2).getValue()).getPerson();
    assertThat(p.getId(), not(equalTo(CHANGED)));
    // person identifiers
    for (IdentifierTO ident : p.getIdentifiers()) {
      assertThat(ident.getType(), not(equalTo(CHANGED)));
      assertThat(ident.getValue(), not(equalTo(CHANGED)));
    }
    for (OrganizationTO org : p.getOrganizations()) {
      assertThat(org.getId(), not(equalTo(CHANGED)));
      for (IdentifierTO ident : org.getIdentifiers()) {
        assertThat(ident.getType(), not(equalTo(CHANGED)));
        assertThat(ident.getValue(), not(equalTo(CHANGED)));
      }
    }
  }


  
 
  
@Test
  public void test_3_UpdateItem_1_Change_Metadata_Statements_EmptyValues() throws IOException,
      BadRequestException {

    final String REP_CHANGED = "$1\"\"";
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.field(
        "json",
        updateJSON
            .replace("___COLLECTION_ID___", collectionId)
            .replace("___ITEM_ID___", itemId)
            .replaceAll("(\"text\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            .replaceAll("(\"date\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            .replaceAll("(\"license\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            .replaceAll("(\"link\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            .replaceAll("(\"url\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
            // /only publication should be filled, not citation or format
            .replaceAll("(\"publication\"\\s*:\\s*)\"(.+)\"", REP_CHANGED));

    //LOGGER.info(multiPart.getField("json").getValue());

    Response response =
        target(PATH_PREFIX).path("/" + itemId)
            .register(authAsUser)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals(response.getStatus(), OK.getStatusCode());

    final String json = response.readEntity(String.class);
    //LOGGER.info(json);

    assertThat(json, not(containsString("\"text\"")));
    assertThat(json, not(containsString("\"date\"")));
    assertThat(json, allOf(not(containsString("\"license\"")), not(containsString("\"url\""))));
    assertThat(json, allOf(not(containsString("\"link\"")), not(containsString("\"url\""))));
    assertThat(
        json,
        allOf(not(containsString("\"format\"")), not(containsString("\"publication\"")),
            not(containsString("\"citation\""))));
  }

 
@Test
  public void test_3_UpdateItem_2_Change_Metadata_Statements_EmptyStatements_SomeSections()
      throws IOException, UnprocessableError, ImejiException {
    
    FormDataMultiPart multiPart = new FormDataMultiPart();
    
    multiPart.field(
        "json",
        updateJSON
        .replace("___COLLECTION_ID___", collectionId)
        .replace("___ITEM_ID___", itemId));
    
    Response responseUpd =
        target(PATH_PREFIX).path("/" + itemId)
            .register(authAsUser)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(OK.getStatusCode(), responseUpd.getStatus() );
    String originalJ = responseUpd.readEntity(String.class);
    //LOGGER.info("ORIGINAL= "+originalJ);
    
    ItemTO updItem = new ItemTO();
    updItem = ItemProcess.prepareDefaultItemTOAsItemTO(false, originalJ, updItem, itemId, JenaUtil.testUser);

   multiPart = new FormDataMultiPart();

    multiPart.field(
        "json",
        originalJ
            .replaceAll("\"text.*\",", "")
            .replaceAll("\"date.*\",", ""));
    
    //LOGGER.info("UPDATE"+multiPart.getField("json").getValue());

    Response responseUpd2 =
        target(PATH_PREFIX).path("/" + itemId)
            .register(authAsUser)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(OK.getStatusCode(), responseUpd2.getStatus());
    
    ItemTO newUpdItem = new ItemTO();
    newUpdItem = ItemProcess.prepareDefaultItemTOAsItemTO(false, responseUpd2.readEntity(String.class), newUpdItem, itemId, JenaUtil.testUser);
    assertThat(newUpdItem.getMetadata().size(), equalTo(updItem.getMetadata().size()-2));

  }

  
 @Test
  public void test_3_UpdateItem_3_Change_Metadata_Statements_EmptyStatements_CompleteSection()
      throws IOException, UnprocessableError, ImejiException {

    FormDataMultiPart multiPart = new FormDataMultiPart();

    multiPart.field("json", 
       updateJSON
       .replace("___COLLECTION_ID___", collectionId)
       .replace("___ITEM_ID___", itemId)
       .replaceAll("\"metadata\".*", "\"metadata\" : {} }")
     );

    Response response =
        target(PATH_PREFIX).path("/" + itemId)
            .register(authAsUser)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals( OK.getStatusCode(), response.getStatus());
    String newJson = response.readEntity(String.class);
    
    ItemTO newUpdItem = new ItemTO();
    newUpdItem = ItemProcess.prepareDefaultItemTOAsItemTO(false, newJson, newUpdItem, itemId, JenaUtil.testUser);
    assertThat(newUpdItem.getMetadata().size(), equalTo(0));

  }

 
  @Test
  public void test_4_UpdateItem_3_Change_Metadata_Statements_Add_MultipleStatement()
      throws Exception {

    initCollectionWithProfile(getMultipleStatements());
    initItemWithMultipleStatements();
    
    String ADDED_TITLE = "addedMultipleText";
    FormDataMultiPart multiPart = new FormDataMultiPart();
   
    String jsonAddNewTitle = 
        getStringFromPath(STATIC_CONTEXT_REST + "/easyUpdateItemBasicMultipleStatements.json")
        .replace("___COLLECTION_ID___", collectionId)
        .replace("___ITEM_ID___", itemId)
        .replaceAll("\"text\".*],", "\"text\": [\"value1\", \"value2\",\""+ADDED_TITLE+"\"],");
    
    multiPart.field("json", jsonAddNewTitle );
    //LOGGER.info("MODIEIF "+jsonAddNewTitle );

    Response response =
        target(PATH_PREFIX).path("/" + itemId)
            .register(authAsUser)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(OK.getStatusCode(), response.getStatus());
    
    ItemTO updItem = new ItemTO();
    updItem = ItemProcess.prepareDefaultItemTOAsItemTO(false, response.readEntity(String.class), updItem, itemId, JenaUtil.testUser);

    assertThat(updItem.getMetadata(), hasSize(itemTO.getMetadata().size()+1));
    int i = 0;
    String updatedTextString = "";
    for (MetadataSetTO mdsTo:updItem.getMetadata()){
        if (mdsTo.getLabels().get(0).getValue().equals("text")) {
            i++;
            updatedTextString+= ((TextTO)mdsTo.getValue()).getText();
        }
    }
    
    assertThat(i, equalTo(3));
    assertThat(updatedTextString, containsString(ADDED_TITLE));
    

  }


  @Test
  public void test_4_UpdateItem_3_Change_Metadata_Statements_Add_NonMultipleStatement()
      throws Exception {

    initCollectionWithProfile(getMultipleStatements());
    initItemWithMultipleStatements();

    FormDataMultiPart multiPart = new FormDataMultiPart();
    String jsonAddNewTitle = 
        getStringFromPath(STATIC_CONTEXT_REST + "/easyUpdateItemBasicMultipleStatements.json")
        .replace("___COLLECTION_ID___", collectionId)
        .replace("___ITEM_ID___", itemId)
        .replaceAll("\"date\".*\",", "\"date\": [\"2015-12-01\", \"2015-12-02\"],");
    
    multiPart.field("json", jsonAddNewTitle );

    Response response =
        target(PATH_PREFIX).path("/" + itemId)
            .register(authAsUser)
            .register(MultiPartFeature.class).register(JacksonFeature.class)
            .request(MediaType.APPLICATION_JSON_TYPE)
            .put(Entity.entity(multiPart, multiPart.getMediaType()));

    assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

  }



  protected static void initItemWithFullMetadata() throws Exception {
    
    
    ItemService s = new ItemService();
    itemTO = new ItemTO();
    itemTO.setCollectionId(collectionId);
    itemTO = ItemProcess.prepareDefaultItemTOAsItemTO(true, 
           updateJSON
             .replace("___COLLECTION_ID___", collectionId),
             itemTO, itemTO.getId(), JenaUtil.testUser);
    ((ItemWithFileTO) itemTO).setFile(new File("src/test/resources/storage/test.png"));
    
    itemTO = s.create(itemTO, JenaUtil.testUser);
    itemId = itemTO.getId();
  }

  private static void initItemWithMultipleStatements() throws Exception {
    ItemService s = new ItemService();

    itemTO = new ItemTO();
    itemTO.setCollectionId(collectionId);
    itemTO = ItemProcess.prepareDefaultItemTOAsItemTO(true, 
              getStringFromPath(STATIC_CONTEXT_REST + "/easyUpdateItemBasicMultipleStatements.json")
             .replace("___COLLECTION_ID___", collectionId),
             itemTO, itemTO.getId(), JenaUtil.testUser);
    ((ItemWithFileTO) itemTO).setFile(new File("src/test/resources/storage/test.png"));
    
    itemTO = s.create(itemTO, JenaUtil.testUser);
    itemId = itemTO.getId();

  }


}

package de.mpg.imeji.test.rest.resources.test.integration;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.test.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_STORAGE;
import static net.java.dev.webdav.jaxrs.ResponseStatus.UNPROCESSABLE_ENTITY;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
//import org.apache.tika.metadata.Metadata;


import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.process.ReverseTransferObjectFactory;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.j2j.misc.LocalizedString;

/**
 * Created by vlad on 10.06.15.
 */
public class ItemTestBase extends ImejiTestBase {

  private static Logger logger = Logger.getLogger(ItemTestBase.class);

  public static MetadataProfile profile;
  public static Item item;
  private static final String TARGET_PATH_PREFIX = "/rest/items";
  
  protected static ProfileController pc = new ProfileController();

  protected static void initCollectionWithProfile(Collection<Statement> statements)
      throws Exception {

    MetadataProfile p = ImejiFactory.newProfile();
    p.setTitle("test");
    p.setStatements(statements);

    MetadataProfile mp = pc.create(p, JenaUtil.testUser);
    pc.release(mp, JenaUtil.testUser);

    profileId = ObjectHelper.getId(mp.getId());

    try {

      collectionTO = (CollectionTO) RestProcessUtils.buildTOFromJSON(
          getStringFromPath("src/test/resources/rest/createCollection.json"), CollectionTO.class);

      CollectionController cc = new CollectionController();
      CollectionImeji ci = new CollectionImeji();
      ci.setProfile(mp.getId());
      ReverseTransferObjectFactory.transferCollection(collectionTO, ci,
          ReverseTransferObjectFactory.TRANSFER_MODE.CREATE, JenaUtil.testUser);

      collectionId = ObjectHelper.getId(cc.create(ci, mp, JenaUtil.testUser,
          CollectionController.MetadataProfileCreationMethod.REFERENCE, null));

      System.out.println("PROFILEW= "+profileId+" collection = "+collectionId);

    } catch (Exception e) {
      logger.error("Cannot init Collection", e);
    }

  }

  protected static Collection<Statement> getDefaultBasicStatements() {
    Collection<Statement> statements = new ArrayList<Statement>();
    Statement st;
    for (String type : new String[] {"text", "number", "conePerson", "geolocation", "date", "license",
        "link", "publication"}) {
      st = new Statement();
      st.setType(URI.create("http://imeji.org/terms/metadata#" + type));
      st.getLabels().add(new LocalizedString(type, "en"));
      statements.add(st);
    }
    return statements;
  }
  
 
  protected static Collection<Statement> getDefaultHierarchicalStatements() {
    Collection<Statement> statements = new ArrayList<Statement>();
    Statement st;

    for (String type : new String[] {"text", "number", "conePerson", "geolocation", "date", "license",
        "link", "publication"}) {
      st = new Statement();
      st.setType(URI.create("http://imeji.org/terms/metadata#" + type));
      st.getLabels().add(new LocalizedString(type, "en"));
      if (type.equals("text") || type.equals("number")){
        st.setMaxOccurs("unbounded");
      }
      
      statements.add(st);
      
      if (type.equals("text") || type.equals("number")) {
        //text and number are multiple, text is parent of date and License, number is parent of conePerson and geo and text
        for (String typeChild : new String[] { "date", "license", "conePerson", "text", "geolocation"}) {
          Statement stChild = new Statement();
          stChild.setType(URI.create("http://imeji.org/terms/metadata#" + typeChild));
          stChild.getLabels().add(new LocalizedString(typeChild+"Child", "en"));
          if(  ( type.equals("text") && (typeChild.equals("date") || typeChild.equals("license"))) ||
               ( type.equals("number") && (typeChild.equals("text") || typeChild.equals("conePerson") || typeChild.equals("geolocation"))) )
          {
            stChild.setParent(st.getId());
            statements.add(stChild);
          }
        }
      }
      
    }
    
    for (Statement mySt:statements) {
      System.out.println("Statement = "+mySt.getLabel()+", ID= "+mySt.getId()+", PARENT= "+mySt.getParent());
    }
    return statements;
  }


  protected static Collection<Statement> getBasicStatements() {
    Collection<Statement> statements = new ArrayList<Statement>();
    Statement st;
    for (String type : new String[] {"text", "number", "conePerson", "geolocation", "date",
        "license", "link", "publication"}) {
      st = new Statement();
      st.setType(URI.create("http://imeji.org/terms/metadata#" + type));
      st.getLabels().add(new LocalizedString(type, "en"));
      statements.add(st);
    }
    return statements;
  }

  protected static Collection<Statement> getMultipleStatements() {
    Collection<Statement> statements = new ArrayList<Statement>();
    Statement st;
    for (String type : new String[] {"text", "number", "conePerson", "geolocation", "date",
        "license", "link", "publication"}) {
      st = new Statement();
      st.setType(URI.create("http://imeji.org/terms/metadata#" + type));
      st.getLabels().add(new LocalizedString(type, "en"));
      if (type.equals("text") || type.equals("number")){
        st.setMaxOccurs("unbounded");
      }
      statements.add(st);
    }
    return statements;
  }
  
  protected static void createItem() throws Exception {
    CollectionController cc = new CollectionController();
    ItemController ic = new ItemController();
    CollectionImeji coll =
        cc.retrieve(ObjectHelper.getURI(CollectionImeji.class, collectionId), JenaUtil.testUser);
    item = ImejiFactory.newItem(coll);
    item = ic.create(item, coll.getId(), JenaUtil.testUser);
    itemId = item.getIdString();
    // TransferObjectFactory.transferItem(item, itemTO);
  }
  
  
  private String replaceWithStringValueNotLastField(String jSon, String fieldName) {
    return jSon.replace("___COLLECTION_ID___", collectionId).replace("___FILENAME___", "test.jpg")
        .replaceAll("\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",", "")
        .replaceAll("\"referenceUrl\"\\s*:\\s*\"___REFERENCE_URL___\",", "")
        .replaceAll("\""+fieldName+"\"\\s*:\\s*.*,", "\""+fieldName+"\": \"sometext\",");
  }
  
  private String replaceWithStringValueLastField(String jSon, String fieldName) {
    return jSon.replace("___COLLECTION_ID___", collectionId).replace("___FILENAME___", "test.jpg")
        .replaceAll("\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",", "")
        .replaceAll("\"referenceUrl\"\\s*:\\s*\"___REFERENCE_URL___\",", "")
        .replaceAll("\""+fieldName+"\"\\s*:\\s*.*", "\""+fieldName+"\": \"sometext\"");
    
  }


  private String replaceWithNumberValueNotLastField(String jSon, String fieldName) {
    return  jSon.replace("___COLLECTION_ID___", collectionId).replace("___FILENAME___", "test.jpg")
        .replaceAll("\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",", "")
        .replaceAll("\"referenceUrl\"\\s*:\\s*\"___REFERENCE_URL___\",", "")
        .replaceAll("\""+fieldName+"\"\\s*:\\s*.*,", "\""+fieldName+"\": 123456,");
  }

  private String replaceWithNumberValueLastField(String jSon, String fieldName) {
    return  jSon.replace("___COLLECTION_ID___", collectionId).replace("___FILENAME___", "test.jpg")
        .replaceAll("\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",", "")
        .replaceAll("\"referenceUrl\"\\s*:\\s*\"___REFERENCE_URL___\",", "")
        .replaceAll("\""+fieldName+"\"\\s*:\\s*.*", "\""+fieldName+"\": 123456");
  }
  
  private String replaceFieldName(String jSon, String oldFieldName){
    String newFieldName = oldFieldName+"-changed";
    return jSon.replace("___COLLECTION_ID___", collectionId).replace("___FILENAME___", "test.jpg")
        .replaceAll("\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",", "")
        .replaceAll("\"referenceUrl\"\\s*:\\s*\"___REFERENCE_URL___\",", "")
        .replaceAll("\""+oldFieldName+"\"\\s*:", "\""+newFieldName+"\":");
  }

  

  public void test_5_defaultSyntax_badTypedValues(String itemId, String jSon) throws IOException {

    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart
        .bodyPart(new FileDataBodyPart("file", new File(STATIC_CONTEXT_STORAGE + "/test.jpg")));

    logger.info("Checking textual values ... ");
    //Put Number Value to a String metadata
    multiPart.field("json", replaceWithNumberValueNotLastField(jSon, "text"));
    Response response = itemId.equals("") ?
                          getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
                          getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
    
    logger.info("Checking number values ... ");
    //Put String to Number Value metadata
    multiPart.getField("json").setValue(replaceWithStringValueNotLastField(jSon, "number"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking date values ... ");
    //Put "sometext" String to Date Value metadata
    multiPart.getField("json").setValue(replaceWithStringValueNotLastField(jSon, "date"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking longitude values with text ... ");
    //Put "sometext" String to Longitude Value metadata
    multiPart.getField("json").setValue(replaceWithStringValueNotLastField(jSon, "longitude"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking latitude values with text... ");
    //Put "sometext" String to Latitude (last) Value metadata
    multiPart.getField("json").setValue(replaceWithStringValueLastField(jSon, "latitude"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking longitude values with wrong value... ");
    //Put bad Value to Longitude Value metadata
    multiPart.getField("json").setValue(replaceWithNumberValueNotLastField(jSon, "longitude"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking latitude values with wrong value... ");
    //Put bad Value to Longitude Value metadata
    multiPart.getField("json").setValue(replaceWithNumberValueLastField(jSon, "latitude"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
  }
  
  

  public void test_6_ExistingDefaultFields(String itemId, String jSon) throws IOException {
    //validates the name of each predefined metadata from a metadata record 
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart
        .bodyPart(new FileDataBodyPart("file", new File(STATIC_CONTEXT_STORAGE + "/test.jpg")));

    logger.info("Checking text field label  ");
    multiPart.field("json", replaceFieldName(jSon, "text"));
    Response  response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking geolocation field label  ");
    //Put Number Value to a String metadata
    multiPart.field("json", replaceFieldName(jSon, "geolocation"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking name field label  ");
    multiPart.field("json", replaceFieldName(jSon, "name"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking latitude field label  ");
    multiPart.field("json", replaceFieldName(jSon, "latitude"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());


    logger.info("Checking longitude field label  ");
    multiPart.field("json", replaceFieldName(jSon, "longitude"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking number field label  ");
    multiPart.field("json", replaceFieldName(jSon, "number"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
    

    logger.info("Checking conePerson field label  ");
    multiPart.field("json", replaceFieldName(jSon, "conePerson"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking familyName field label  ");
    multiPart.field("json", replaceFieldName(jSon, "familyName"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking completeName field label  ");
    multiPart.field("json", replaceFieldName(jSon, "completeName"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking alternativeName field label  ");
    multiPart.field("json", replaceFieldName(jSon, "alternativeName"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
    
    logger.info("Checking role field label  ");
    multiPart.field("json", replaceFieldName(jSon, "role"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking organizations field label  ");
    multiPart.field("json", replaceFieldName(jSon, "organizations"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking description field label  ");
    multiPart.field("json", replaceFieldName(jSon , "description"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking city field label  ");
    multiPart.field("json", replaceFieldName(jSon, "city"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking country field label  ");
    multiPart.field("json", replaceFieldName(jSon, "country"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking date field label  ");
    multiPart.field("json", replaceFieldName(jSon, "date"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking license field label  ");
    multiPart.field("json", replaceFieldName(jSon, "license"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking url field label  ");
    multiPart.field("json", replaceFieldName(jSon, "url"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking link field label  ");
    multiPart.field("json", replaceFieldName(jSon, "link"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking publication field label  ");
    multiPart.field("json", replaceFieldName(jSon, "publication"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    logger.info("Checking citation field label  ");
    multiPart.field("json", replaceFieldName(jSon, "citation"));
    response = itemId.equals("") ?
        getCreateTargetAuth().post(Entity.entity(multiPart, multiPart.getMediaType())) :
        getUpdateTargetAuth(itemId).put(Entity.entity(multiPart, multiPart.getMediaType()));
    assertEquals( UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
  }

  
  private Invocation.Builder getUpdateTargetAuth(String itemId) {
    return target(TARGET_PATH_PREFIX).path("/"+itemId).register(authAsUser).register(MultiPartFeature.class)
        .register(JacksonFeature.class).request(MediaType.APPLICATION_JSON_TYPE);
  }
  
  private Invocation.Builder getCreateTargetAuth() {
    return target(TARGET_PATH_PREFIX).register(authAsUser).register(MultiPartFeature.class)
        .register(JacksonFeature.class).request(MediaType.APPLICATION_JSON_TYPE);
  }

}

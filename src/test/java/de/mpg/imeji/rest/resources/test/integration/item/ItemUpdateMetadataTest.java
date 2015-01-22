package de.mpg.imeji.rest.resources.test.integration.item;

import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.resources.test.integration.ImejiTestBase;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.rest.to.MetadataSetTO;
import de.mpg.imeji.rest.to.PersonTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.ConePersonTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.DateTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.GeolocationTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.LicenseTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.LinkTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.NumberTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.PublicationTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.TextTO;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.mpg.imeji.rest.resources.test.TestUtils.getStringFromPath;
import static de.mpg.imeji.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_REST;
import static de.mpg.imeji.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_STORAGE;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

/**
 * Created by vlad on 09.12.14.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ItemUpdateMetadataTest extends ImejiTestBase {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ItemUpdateMetadataTest.class);

    private static String updateJSON;
    private static final String PATH_PREFIX = "/rest/items";
    private static final String UPDATED_FILE_NAME = "updated_filename.png";
    private static final File ATTACHED_FILE = new File(
            STATIC_CONTEXT_STORAGE + "/test2.jpg");
    private static String storedFileURL;
    private final String UPDATE_ITEM_FILE_JSON = STATIC_CONTEXT_REST + "/updateItemFile.json";

    @BeforeClass
    public static void specificSetup() throws Exception {
        initCollection();
        initItem();
        updateJSON = getStringFromPath(STATIC_CONTEXT_REST + "/updateItemBasic.json");
    }


    @Test
    public void test_2_UpdateItem_1_Change_Metadta_Statements_Allowed() throws IOException {

        final String CHANGED = "changed";
        double NUM = 12345;
        final String REP_CHANGED = "$1\"" + CHANGED + "\"";
        final String NUM_CHANGED = "$1"+ NUM;
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");
        Date date = new Date();
        final String DATE_CHANGED = "$1\"" + dateFormat.format(date) + "\"";
        

        FormDataMultiPart multiPart = new FormDataMultiPart();
        
        
        multiPart.field("json", updateJSON
                        .replace("___FILE_NAME___", CHANGED)
                        .replaceAll("(\"text\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"familyName\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"name\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"number\"\\s*:\\s*).+", NUM_CHANGED)
                        .replaceAll("(\"longitude\"\\s*:\\s*).+", NUM_CHANGED+",")
                        .replaceAll("(\"latitude\"\\s*:\\s*).+", NUM_CHANGED)
                        .replaceAll("(\"givenName\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"alternativeName\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"role\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"city\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"country\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"description\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"date\"\\s*:\\s*)\"(.+)\"", DATE_CHANGED)
                        .replaceAll("(\"license\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"link\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"url\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"publication\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"format\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"citation\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
        );
        
        LOGGER.info(multiPart.getField("json").getValue());

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        assertEquals(response.getStatus(), OK.getStatusCode());
        ItemTO updatedItem = (ItemTO) response.readEntity(ItemWithFileTO.class);
        
        
        LOGGER.info(RestProcessUtils.buildJSONFromObject(updatedItem));

        assertThat(updatedItem.getFilename(), equalTo(CHANGED));
       
        List<MetadataSetTO> mds = updatedItem.getMetadata();
        assertThat( ((TextTO) mds.get(0).getValue()).getText(), equalTo(CHANGED));
        
        assertThat( ((GeolocationTO) mds.get(1).getValue()).getName(), equalTo(CHANGED));
        assertThat( ((GeolocationTO) mds.get(1).getValue()).getLatitude(), equalTo(NUM));
        assertThat( ((GeolocationTO) mds.get(1).getValue()).getLongitude(), equalTo(NUM));
        
        assertThat( ((NumberTO) mds.get(2).getValue()).getNumber(), equalTo(NUM));
        
        PersonTO person = ((ConePersonTO) mds.get(3).getValue()).getPerson();
        assertThat(person.getFamilyName(), equalTo(CHANGED));
        assertThat(person.getGivenName(), equalTo(CHANGED));
//        assertThat(person.getCompleteName(), equalTo(CHANGED+" , "+CHANGED));
//        TODO if complete name changed, should family name and given name changed??
//        assertThat(person.getRole(), equalTo(CHANGED));
        assertThat(person.getOrganizations().get(0).getCountry(), equalTo(CHANGED));
        assertThat(person.getOrganizations().get(0).getCity(), equalTo(CHANGED));
        assertThat(person.getOrganizations().get(0).getCountry(), equalTo(CHANGED));
        
        assertThat( ((DateTO) mds.get(4).getValue()).getDate(), equalTo(dateFormat.format(date)));
        
        assertThat( ((LicenseTO) mds.get(5).getValue()).getLicense(), equalTo(CHANGED));
        assertThat( ((LicenseTO) mds.get(5).getValue()).getUrl(), equalTo(CHANGED));
        
        assertThat( ((LinkTO) mds.get(6).getValue()).getLink(), equalTo(CHANGED));
        assertThat( ((LinkTO) mds.get(6).getValue()).getUrl(), equalTo(CHANGED));
        
        assertThat( ((PublicationTO) mds.get(7).getValue()).getFormat(), equalTo(CHANGED));
//       assertThat( ((PublicationTO) mds.get(7).getValue()).getCitation(), equalTo(CHANGED));
        assertThat( ((PublicationTO) mds.get(7).getValue()).getPublication(), equalTo(CHANGED));
    }
    @Test
    public void test_2_UpdateItem_2_Change_Metadta_Statements_Not_Allowed() throws IOException {

        final String CHANGED = "_changed";
        final String REP_CHANGED = "$1\"" + CHANGED + "\"";

        FormDataMultiPart multiPart = new FormDataMultiPart();
        
        
        multiPart.field("json", updateJSON
                        .replaceAll("(\"createdDate\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"collectionId\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"checksumMd5\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"webResolutionUrlUrl\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
//                        .replaceAll("(\"language\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
//                        .replaceAll("(\"value\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"id\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
//                        .replaceAll("(\"statementUri\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
//                        .replaceAll("(\"typeUri\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                       
        );
        
       
       LOGGER.info(multiPart.getField("json").getValue());

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        assertEquals(response.getStatus(), OK.getStatusCode());
        ItemTO updatedItem = (ItemTO) response.readEntity(ItemWithFileTO.class);

        LOGGER.info(RestProcessUtils.buildJSONFromObject(updatedItem));

        assertThat(updatedItem.getCreatedDate(), not(equalTo(CHANGED)));
        assertThat(updatedItem.getCollectionId(), not(equalTo(CHANGED)));
        assertThat(updatedItem.getChecksumMd5(), not(equalTo(CHANGED)));
        assertThat(updatedItem.getWebResolutionUrlUrl().toString(), not(equalTo(CHANGED)));

        List<MetadataSetTO> mds = updatedItem.getMetadata();
        
       
        
        assertThat(  mds.get(0).getStatementUri().toString(), not(equalTo(CHANGED)));
        assertThat(  mds.get(1).getStatementUri().toString(), not(equalTo(CHANGED)));
        assertThat(  mds.get(2).getStatementUri().toString(), not(equalTo(CHANGED)));
        assertThat(  mds.get(3).getStatementUri().toString(), not(equalTo(CHANGED)));
        assertThat(  mds.get(4).getStatementUri().toString(), not(equalTo(CHANGED)));
        assertThat(  mds.get(5).getStatementUri().toString(), not(equalTo(CHANGED)));
        assertThat(  mds.get(6).getStatementUri().toString(), not(equalTo(CHANGED)));
        assertThat(  mds.get(7).getStatementUri().toString(), not(equalTo(CHANGED)));
        
        assertThat(  mds.get(0).getTypeUri().toString(), not(equalTo(CHANGED)));
        assertThat(  mds.get(1).getTypeUri().toString(), not(equalTo(CHANGED)));
        assertThat(  mds.get(2).getTypeUri().toString(), not(equalTo(CHANGED)));
        assertThat(  mds.get(3).getTypeUri().toString(), not(equalTo(CHANGED)));
        assertThat(  mds.get(4).getTypeUri().toString(), not(equalTo(CHANGED)));
        assertThat(  mds.get(5).getTypeUri().toString(), not(equalTo(CHANGED)));
        assertThat(  mds.get(6).getTypeUri().toString(), not(equalTo(CHANGED)));
        assertThat(  mds.get(7).getTypeUri().toString(), not(equalTo(CHANGED)));
        
//        assertThat(  mds.get(0).getLabels().get(0).getLanguage(), not(equalTo(CHANGED)));
//        assertThat(  mds.get(0).getLabels().get(0).getValue(), not(equalTo(CHANGED)));
        

        PersonTO person = ((ConePersonTO) mds.get(3).getValue()).getPerson();
        assertThat(person.getId(), not(equalTo(CHANGED)));
        assertThat(person.getIdentifiers().get(0).getType(), not(equalTo(CHANGED)));
        assertThat(person.getIdentifiers().get(0).getValue(), not(equalTo(CHANGED)));
        assertThat(person.getOrganizations().get(0).getId(), not(equalTo(CHANGED)));
        assertThat(person.getOrganizations().get(0).getIdentifiers().get(0).getType(), not(equalTo(CHANGED)));
        assertThat(person.getOrganizations().get(0).getIdentifiers().get(0).getValue(), not(equalTo(CHANGED)));
       
    }

    @Test
    public void test_2_UpdateItem_3_Change_Metadta_Statements_Empty() throws IOException {

        final String REP_CHANGED = "$1\"\"";
     
         

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json", updateJSON
                        .replaceAll("(\"text\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
//                        .replaceAll("(\"number\"\\s*:\\s*).+", "$1")
                        .replaceAll("(\"familyName\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"date\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"url\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"license\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"link\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"publication\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
        );
        
//        LOGGER.info(multiPart.getField("json").getValue());

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        assertEquals(response.getStatus(), OK.getStatusCode());
        
//        ItemTO updatedItem = (ItemTO) response.readEntity(ItemWithFileTO.class);
//        LOGGER.info(RestProcessUtils.buildJSONFromObject(updatedItem));
        
        final String json = response.readEntity(String.class);
        
        assertThat(json, not(containsString("\"text\"")));
//        assertThat(json, not(containsString("\"number\"")));
//        assertThat(json, not(containsString("\"person\"")));
        assertThat(json, not(containsString("\"date\"")));
        assertThat(json, not(containsString("\"publication\"")));
        assertThat(json, not(containsString("\"license\"")));
        
 
    }
    
    @Test
    public void test_2_UpdateItem_3_Change_Metadta_Statements_EmptyAll() throws IOException {
    	
 
        final String CHANGED = "$1";
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json", updateJSON
                .replaceAll("(\"metadata\"\\s*:\\s*)(.*)", CHANGED +"[]}")
 
        
        );
        
        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        
        assertEquals(response.getStatus(), OK.getStatusCode());
        
        
        final String json = response.readEntity(String.class);
        LOGGER.info(json);
        
        assertThat(json, not(containsString("\"text\"")));
		assertThat(json, not(containsString("\"number\"")));
		assertThat(json, not(containsString("\"person\"")));
		assertThat(json, not(containsString("\"date\"")));
		assertThat(json, not(containsString("\"publication\"")));
		assertThat(json, not(containsString("\"license\"")));
    }

}
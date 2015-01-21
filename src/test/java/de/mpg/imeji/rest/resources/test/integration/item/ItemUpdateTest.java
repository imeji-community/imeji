package de.mpg.imeji.rest.resources.test.integration.item;

import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.resources.test.integration.ImejiTestBase;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.imeji.rest.to.MetadataSetTO;
import de.mpg.imeji.rest.to.PersonTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.ConePersonTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.TextTO;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
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
import java.io.IOException;
import java.util.List;

import static de.mpg.imeji.logic.storage.util.StorageUtils.calculateChecksum;
import static de.mpg.imeji.rest.resources.test.TestUtils.getStringFromPath;
import static de.mpg.imeji.rest.resources.test.integration.MyTestContainerFactory.*;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
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
public class ItemUpdateTest extends ImejiTestBase {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ItemUpdateTest.class);

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
    public void test_1_UpdateItem_1_Basic() throws IOException {
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json",
                updateJSON.replace("___FILE_NAME___", UPDATED_FILE_NAME));

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        assertEquals(response.getStatus(), OK.getStatusCode());
        ItemTO updatedItem = (ItemTO) response.readEntity(ItemWithFileTO.class);
        assertThat("Filename has not been updated", updatedItem.getFilename(),
                equalTo(UPDATED_FILE_NAME));

    }


    @Test
    public void test_1_UpdateItem_2_NotAllowedUser() throws IOException {
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json", updateJSON);
        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser2)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        assertEquals(FORBIDDEN.getStatusCode(), response.getStatus());
    }


    @Test
    public void test_1_UpdateItem_3_WithFile_Attached() throws IOException {

        FileDataBodyPart filePart = new FileDataBodyPart("file", ATTACHED_FILE);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json",
                getStringFromPath(UPDATE_ITEM_FILE_JSON)
                        .replace("___FILE_NAME___", ATTACHED_FILE.getName())
                        .replace("___FETCH_URL___", "")
                        .replace("___REFERENCE_URL___", "")
        );

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser).register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(OK.getStatusCode(), response.getStatus());
        ItemWithFileTO itemWithFileTO = response.readEntity(ItemWithFileTO.class);
        assertThat("Wrong file name", itemWithFileTO.getFilename(), equalTo(ATTACHED_FILE.getName()));

        storedFileURL = target().getUri() + itemWithFileTO.getFileUrl().getPath().substring(1);

        LOGGER.info(RestProcessUtils.buildJSONFromObject(itemWithFileTO));


    }

    @Test
    public void test_1_UpdateItem_4_WithFile_Fetched() throws IOException {

        final String fileURL = target().getUri() +
                STATIC_CONTEXT_PATH.substring(1) + "/test2.jpg";

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json",
                getStringFromPath(UPDATE_ITEM_FILE_JSON)
                        .replace("___FILE_NAME___", UPDATED_FILE_NAME)
                        .replace("___FETCH_URL___", fileURL)
                        .replace("___REFERENCE_URL___", "")
        );

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser).register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(OK.getStatusCode(), response.getStatus());
        ItemWithFileTO itemWithFileTO = response.readEntity(ItemWithFileTO.class);
        assertThat("Checksum of stored file does not match the source file",
                itemWithFileTO.getChecksumMd5(), equalTo(calculateChecksum(ATTACHED_FILE)));
        LOGGER.info(RestProcessUtils.buildJSONFromObject(itemWithFileTO));
    }

    //@Ignore
    //TODO after execution this test case, the following test cases can not be executed successfully, a service exception reported.
    //TODO if change the order of this test case as the last one, all test cases can be executed successfully
    @Test
    public void test_1_UpdateItem_5_WithFile_Referenced() throws IOException {

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json",
                getStringFromPath(UPDATE_ITEM_FILE_JSON)
                        .replace("___FILE_NAME___", UPDATED_FILE_NAME)
                        .replace("___FETCH_URL___", "")
                        .replace("___REFERENCE_URL___", storedFileURL)
        );

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser).register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(OK.getStatusCode(), response.getStatus());
        ItemWithFileTO itemWithFileTO = response.readEntity(ItemWithFileTO.class);
        assertThat("Reference URL does not match",
                storedFileURL, equalTo(itemWithFileTO.getFileUrl().toString()));
    }


    @Test
    public void test_1_UpdateItem_6_WithFile_Attached_Fetched() throws IOException {

        File newFile = new File(
                STATIC_CONTEXT_STORAGE + "/test.png");

        final String fileURL = target().getUri() +
                STATIC_CONTEXT_PATH.substring(1) + "/test.jpg";

        FileDataBodyPart filePart = new FileDataBodyPart("file", newFile);

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json",
                getStringFromPath(UPDATE_ITEM_FILE_JSON)
                        .replace("___FILE_NAME___", newFile.getName())
                        .replace("___FETCH_URL___", fileURL)
                        .replace("___REFERENCE_URL___", "")
        );

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser).register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(OK.getStatusCode(), response.getStatus());
        ItemWithFileTO itemWithFileTO = response.readEntity(ItemWithFileTO.class);
        assertThat("Checksum of stored file does not match the source file",
                itemWithFileTO.getChecksumMd5(), equalTo(calculateChecksum(newFile)));

    }

    @Test
    public void test_1_UpdateItem_7_WithFile_Attached_Referenced() throws IOException {

        File newFile = new File(
                STATIC_CONTEXT_STORAGE + "/test.png");

        FileDataBodyPart filePart = new FileDataBodyPart("file", newFile);

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json",
                getStringFromPath(UPDATE_ITEM_FILE_JSON)
                        .replace("___FILE_NAME___", newFile.getName())
                        .replace("___FETCH_URL___", "")
                        .replace("___REFERENCE_URL___", storedFileURL)
        );

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser).register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(OK.getStatusCode(), response.getStatus());
        ItemWithFileTO itemWithFileTO = response.readEntity(ItemWithFileTO.class);
        assertThat("Checksum of stored file does not match the source file",
                itemWithFileTO.getChecksumMd5(), equalTo(calculateChecksum(newFile)));
    }

    @Test
    public void test_1_UpdateItem_8_WithFile_Fetched_Referenced() throws IOException {

        final String fileURL = target().getUri() +
                STATIC_CONTEXT_PATH.substring(1) + "/test.jpg";

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json",
                getStringFromPath(UPDATE_ITEM_FILE_JSON)
                        .replace("___FILE_NAME___", UPDATED_FILE_NAME)
                        .replace("___FETCH_URL___", fileURL)
                        .replace("___REFERENCE_URL___", storedFileURL)
        );

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser).register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(OK.getStatusCode(), response.getStatus());
        ItemWithFileTO itemWithFileTO = response.readEntity(ItemWithFileTO.class);
        LOGGER.info(RestProcessUtils.buildJSONFromObject(itemWithFileTO));
        assertThat("Checksum of stored file does not match the source file",
                itemWithFileTO.getChecksumMd5(), equalTo(calculateChecksum(new File(STATIC_CONTEXT_STORAGE + "/test.jpg"))));
    }

    @Test
    public void test_1_UpdateItem_9_WithFile_Attached_Fetched_Referenced() throws IOException {

        File newFile = new File(
                STATIC_CONTEXT_STORAGE + "/test.png");
        FileDataBodyPart filePart = new FileDataBodyPart("file", newFile);

        final String fileURL = target().getUri() +
                STATIC_CONTEXT_PATH.substring(1) + "/test.jpg";

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json",
                getStringFromPath(UPDATE_ITEM_FILE_JSON)
                        .replace("___FILE_NAME___", newFile.getName())
                        .replace("___FETCH_URL___", fileURL)
                        .replace("___REFERENCE_URL___", storedFileURL)
        );

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser).register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(OK.getStatusCode(), response.getStatus());
        ItemWithFileTO itemWithFileTO = response.readEntity(ItemWithFileTO.class);
        assertThat("Checksum of stored file does not match the source file",
                itemWithFileTO.getChecksumMd5(), equalTo(calculateChecksum(newFile)));
    }

    @Test
    public void test_2_UpdateItem_1_Change_Metadta_Statements_Allowed() throws IOException {

        final String CHANGED = "_changed";
        final String REP_CHANGED = "$1\"" + CHANGED + "\"";

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json", updateJSON
                        .replace("___FILE_NAME___", CHANGED)
                        .replaceAll("(\"text\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"familyName\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"city\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
        );

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        assertEquals(response.getStatus(), OK.getStatusCode());
        ItemTO updatedItem = (ItemTO) response.readEntity(ItemWithFileTO.class);


        assertThat(updatedItem.getFilename(), equalTo(CHANGED));
        List<MetadataSetTO> mds = updatedItem.getMetadata();
        assertThat( ((TextTO) mds.get(0).getValue()).getText(), equalTo(CHANGED));
        PersonTO person = ((ConePersonTO) mds.get(3).getValue()).getPerson();
        assertThat(person.getFamilyName(), equalTo(CHANGED));
        assertThat(person.getOrganizations().get(0).getCity(), equalTo(CHANGED));

    }
    @Test
    public void test_2_UpdateItem_2_Change_Metadta_Statements_Not_Allowed() throws IOException {

        final String CHANGED = "_changed";
        final String REP_CHANGED = "$1\"" + CHANGED + "\"";

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json", updateJSON
                        .replaceAll("(\"createdDate\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"checksumMd5\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"webResolutionUrlUrl\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
                        .replaceAll("(\"type\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
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
        String identifierType = ((ConePersonTO) mds.get(3).getValue()).getPerson().getIdentifiers().get(0).getType();
        assertThat(identifierType, not(equalTo(CHANGED)));
    }

    @Test
    public void test_2_UpdateItem_3_Change_Metadta_Statements_Empty() throws IOException {

        final String REP_CHANGED = "$1\"\"";

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json", updateJSON
                        .replaceAll("(\"text\"\\s*:\\s*)\"(.+)\"", REP_CHANGED)
        );

        Response response = target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(multiPart, multiPart.getMediaType()));
        assertEquals(response.getStatus(), OK.getStatusCode());

        final String json = response.readEntity(String.class);

        assertThat(json, not(containsString("\"text\"")));

    }



}
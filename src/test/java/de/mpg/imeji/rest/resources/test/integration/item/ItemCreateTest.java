package de.mpg.imeji.rest.resources.test.integration.item;

import static de.mpg.imeji.rest.resources.test.TestUtils.getStringFromPath;
import static de.mpg.imeji.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_PATH;
import static de.mpg.imeji.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_STORAGE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.java.dev.webdav.jaxrs.ResponseStatus;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.JenaUtil;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.resources.test.integration.ImejiTestBase;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;

/**
 * Created by vlad on 09.12.14.
 */

public class ItemCreateTest extends ImejiTestBase {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ItemCreateTest.class);
    public static final File TEST_PNG_FILE = new File("src/test/resources/storage/test.png");

    private static String itemJSON;
    private static final String pathPrefix = "/rest/items";
    private static final File ATTACHED_FILE = new File(
            STATIC_CONTEXT_STORAGE + "/test2.jpg");

    @BeforeClass
    public static void specificSetup() throws Exception {
        initCollection();
        initItem();
        itemJSON = getStringFromPath("src/test/resources/rest/createItemBasic.json");
    }

    @Test
    public void createItemWithEmtpyFilename() throws IOException {

        FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON
                        .replace("___COLLECTION_ID___", collectionId)
                        .replace("___FILENAME___", "")
        );
        
        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }


    //see bug https://github.com/imeji-community/imeji/issues/1023
    @Test
    public void createItemWithFile_NullAsExtensionInFileUrl_Bug1023() throws IOException {

        FileDataBodyPart filePart = new FileDataBodyPart("file", new File("src/test/resources/storage/test"));
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON
                        .replace("___COLLECTION_ID___", collectionId)
                        .replaceAll("\\s*\"filename\":\\s*\"___FILENAME___\"\\s*,", "")
        );

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(CREATED.getStatusCode(), response.getStatus());
        ItemTO createdItem = (ItemTO) response.readEntity(ItemWithFileTO.class);

        assertThat(createdItem.getFileUrl().toString(), allOf(not(endsWith(".null")), endsWith(".png")));

    }

    @Test
    public void createItemWithoutFilename() throws IOException {

        FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON
                .replace("___COLLECTION_ID___", collectionId)
                .replaceAll("\\s*\"filename\":\\s*\"___FILENAME___\"\\s*,", "")
        );

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(CREATED.getStatusCode(), response.getStatus());
        ItemTO item = response.readEntity(ItemTO.class);
        assertThat("File name is wrong", item.getFilename(), equalTo(filePart.getFileEntity().getName()));

    }

    @Test
    public void createItemWithFilename() throws IOException {

        FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON
                .replace("___COLLECTION_ID___", collectionId)
                .replace("___FILENAME___", "test.png"));

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(CREATED.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void createItemWithOutCollection() throws IOException {

        FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON);

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void createItemWithoutFile() throws IOException {

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json", itemJSON
                .replace("___COLLECTION_ID___", collectionId)
                .replace("___FILENAME___", "test.png")
                .replaceAll("\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",", "")
                .replaceAll("\"referenceUrl\"\\s*:\\s*\"___REFERENCE_URL___\",", ""));
    

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void createItemInNotExistingCollection() throws IOException {

        FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON
                .replace("___COLLECTION_ID___", collectionId+"i_do_not_exist")
                .replace("___FILENAME___", "test.png"));

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void createItem_NotLoggedIn() throws IOException {

        FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON
                .replace("___COLLECTION_ID___", collectionId)
                .replace("___FILENAME___", "test.png"));

        Response response = target(pathPrefix)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void createItem_InReleasedCollection() throws Exception {
    	initCollection();
    	initItem();
    	CollectionService sc = new CollectionService();
    	sc.release(collectionId, JenaUtil.testUser);
        assertEquals("RELEASED", sc.read(collectionId, JenaUtil.testUser).getStatus());
    	
        FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON
                .replace("___COLLECTION_ID___", collectionId)
                .replace("___FILENAME___", "test.png"));

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
        
        ItemService is = new ItemService();
        assertEquals("RELEASED", is.read(itemId, JenaUtil.testUser).getStatus());
        
    }
    
    @Test
    public void createItem_InWithdrawnCollection() throws Exception {
    	initCollection();
    	initItem();
    	CollectionService sc = new CollectionService();
    	sc.release(collectionId, JenaUtil.testUser);
        assertEquals("RELEASED", sc.read(collectionId, JenaUtil.testUser).getStatus());
        sc.withdraw(collectionId,JenaUtil.testUser, "ItemCreateTest_createItem_InWithdrawnCollection");
    	
        FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON
                .replace("___COLLECTION_ID___", collectionId)
                .replace("___FILENAME___", "test.png"));

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
        
    }
    
    @Test
    public void createItem_WithNotAllowedUser() throws Exception {
    	
        FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        multiPart.field("json", itemJSON
                .replace("___COLLECTION_ID___", collectionId)
                .replace("___FILENAME___", "test.png"));

        Response response = target(pathPrefix).register(authAsUser2)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
        
    }
       
    @Test
    public void createItem_SyntaxInvalidJSONFile() throws Exception {
    	
        FileDataBodyPart filePart = new FileDataBodyPart("file", TEST_PNG_FILE);
        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(filePart);
        String wrongJSON = getStringFromPath("src/test/resources/rest/wrongSyntax.json");
        
        multiPart.field("json", wrongJSON
                .replace("___COLLECTION_ID___", collectionId)
                .replace("___FILENAME___", "test.png"));

        Response response = target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        
    }
    
    @Test
    public void createItem_WithFile_Fetched() throws IOException {

        final String fileURL = target().getUri() +
                STATIC_CONTEXT_PATH.substring(1) + "/test2.jpg";

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json",itemJSON
                        .replace("___COLLECTION_ID___", collectionId)
                        .replace("___FETCH_URL___", fileURL)
                       
        );

        Response response = target(pathPrefix)
                .register(authAsUser).register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());
 
    }

    @Test
    public void createItem_WithFile_Fetched_WithEmptyFileName() throws IOException {

        final String fileURL = target().getUri() +
                STATIC_CONTEXT_PATH.substring(1) + "/test2.jpg";

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json",itemJSON
                        .replace("___COLLECTION_ID___", collectionId)
                        .replace("___FETCH_URL___", fileURL)
                        .replace("___FILENAME___", "")
        );

        Response response = target(pathPrefix)
                .register(authAsUser).register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus());
       
       
    }

    @Test
    public void createItem_WithFile_Referenced() throws IOException {
    	

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.field("json",itemJSON
                .replace("___COLLECTION_ID___", collectionId)
                .replace("___FILENAME___", "test.png")
                .replace("___REFERENCE_URL___", "http://th03.deviantart.net/fs71/PRE/i/2012/242/1/f/png_moon_by_paradise234-d5czhdo.png")
                .replaceAll("\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",", "")
         
        		);

        Response response = target(pathPrefix)
                .register(authAsUser).register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(Status.CREATED.getStatusCode(), response.getStatus());

    }


    @Test
    public void createItem_InvalidFetchURL() throws IOException {


        FormDataMultiPart multiPart = new FormDataMultiPart();

        multiPart.field("json",itemJSON
        				.replace("___COLLECTION_ID___", collectionId)
                        .replace("___FETCH_URL___", "invalid url")

        );

        Response response = target(pathPrefix)
                .register(authAsUser).register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    }
    
    @Test
    public void createItem_FetchURL_NoFile() throws IOException {


        FormDataMultiPart multiPart = new FormDataMultiPart();

        multiPart.field("json",
        		itemJSON
        		.replace("___COLLECTION_ID___", collectionId)
                        .replace("___FETCH_URL___", "www.google.de")
                       
        );

        Response response = target(pathPrefix)
                .register(authAsUser).register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());

    }


    
    
}
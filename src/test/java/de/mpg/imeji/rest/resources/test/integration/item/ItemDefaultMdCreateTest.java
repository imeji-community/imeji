package de.mpg.imeji.rest.resources.test.integration.item;

import de.mpg.imeji.rest.defaultTO.DefaultItemTO;
import de.mpg.imeji.rest.to.ItemTO;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_STORAGE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;

/**
 * Created by vlad on 09.12.14.
 */

public class ItemDefaultMdCreateTest extends ItemTestBase {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ItemDefaultMdCreateTest.class);

	private static final String referenceUrl = "http://th03.deviantart.net/fs71/PRE/i/2012/242/1/f/png_moon_by_paradise234-d5czhdo.png";

    private static String itemJSON;
    private static final String pathPrefix = "/rest/items";

    DefaultItemTO defaultItemTO;

    @BeforeClass
	public static void specificSetup() throws Exception {
		initCollectionWithProfile(getDefaultBasicStatements());
		itemJSON = getStringFromPath("src/test/resources/rest/defaultCreateItem.json");
	}

	@Test
	public void test_1_createItem_emptySyntax() throws IOException {

		FormDataMultiPart multiPart = new FormDataMultiPart();
		multiPart.field("json",
						itemJSON.replace("___COLLECTION_ID___", collectionId)
								.replace("___FILENAME___", "test.png")
								.replaceAll(
										"\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",",
										"")
								.replaceAll("___REFERENCE_URL___",referenceUrl));

		Response response = getTargetAuth()
				.post(Entity.entity(multiPart, multiPart.getMediaType()));

		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        defaultItemTO = response.readEntity(DefaultItemTO.class);
        //assertThat(defaultItemTO.getMetadata(), hasSize(7)); //check defaultCreateItem.json
		assertThat(defaultItemTO.getCollectionId(), equalTo(collectionId));


	}

	@Test
	public void test_2_createItem_defaultSyntax() throws IOException {

		FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FileDataBodyPart("file", new File(STATIC_CONTEXT_STORAGE + "/test.jpg")));
        multiPart.field("json",
						itemJSON.replace("___COLLECTION_ID___", collectionId)
								.replace("___FILENAME___", "test.jpg")
								.replaceAll("\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",", "")
                                .replaceAll("\"referenceUrl\"\\s*:\\s*\"___REFERENCE_URL___\",", "")
        );
		Response response = target(pathPrefix).register(authAsUser)
                .queryParam("syntax", ItemTO.SYNTAX.DEFAULT.toString().toLowerCase())
				.register(MultiPartFeature.class)
				.register(JacksonFeature.class)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(multiPart, multiPart.getMediaType()));

		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        defaultItemTO = response.readEntity(DefaultItemTO.class);
        assertThat(defaultItemTO.getMetadata().keySet(), hasSize(7)); //check defaultCreateItem.json
		assertThat(defaultItemTO.getCollectionId(), equalTo(collectionId));

	}

    @Test
	public void test_3_createItem_empty_defaultSyntax () throws IOException {

		FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FileDataBodyPart("file", new File(STATIC_CONTEXT_STORAGE + "/test.png")));
        multiPart.field("json",
						itemJSON.replace("___COLLECTION_ID___", collectionId)
								.replace("___FILENAME___", "test.png")
								.replaceAll("\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",", "")
                                .replaceAll("\"referenceUrl\"\\s*:\\s*\"___REFERENCE_URL___\",", "")
                                .replaceAll("\"metadata\"\\s*:\\s*\\{[\\d\\D]*\\}", "\"metadata\":{}}")
        );
		Response response = target(pathPrefix).register(authAsUser)
                .queryParam("syntax", ItemTO.SYNTAX.DEFAULT.toString().toLowerCase())
				.register(MultiPartFeature.class)
				.register(JacksonFeature.class)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(multiPart, multiPart.getMediaType()));

		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        defaultItemTO = response.readEntity(DefaultItemTO.class);
        assertThat(defaultItemTO.getMetadata().keySet(), empty()); //check defaultCreateItem.json
		assertThat(defaultItemTO.getCollectionId(), equalTo(collectionId));

	}

	@Test
	public void test_4_createItem_imejiSyntax() throws IOException {
		FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FileDataBodyPart("file", new File(STATIC_CONTEXT_STORAGE + "/test.png")));
        multiPart.field("json",
						itemJSON.replace("___COLLECTION_ID___", collectionId)
								.replace("___FILENAME___", "test.png")
								.replaceAll("\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",", "")
                                .replaceAll("\"referenceUrl\"\\s*:\\s*\"___REFERENCE_URL___\",", "")
        );
		Response response = target(pathPrefix).register(authAsUser)
                .queryParam("syntax", ItemTO.SYNTAX.RAW.toString().toLowerCase())
				.register(MultiPartFeature.class)
				.register(JacksonFeature.class)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(multiPart, multiPart.getMediaType()));

		assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

	}

    @Test
    public void test_5_createItem_defaultSyntax_badTextValue() throws IOException {

        FormDataMultiPart multiPart = new FormDataMultiPart();
        multiPart.bodyPart(new FileDataBodyPart("file", new File(STATIC_CONTEXT_STORAGE + "/test.jpg")));
        multiPart.field("json",
                itemJSON.replace("___COLLECTION_ID___", collectionId)
                        .replace("___FILENAME___", "test.jpg")
                        .replaceAll("\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",", "")
                        .replaceAll("\"referenceUrl\"\\s*:\\s*\"___REFERENCE_URL___\",", "")
                        //bad value: int instead of text in quotes
                        .replaceAll("(\"text\"\\s*:\\s*)\".*\",", "$112345,")
        );
        Response response = getTargetAuth()
                .post(Entity.entity(multiPart, multiPart.getMediaType()));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    }

    private Invocation.Builder getTargetAuth() {
        return target(pathPrefix).register(authAsUser)
                .register(MultiPartFeature.class)
                .register(JacksonFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE);
    }


}
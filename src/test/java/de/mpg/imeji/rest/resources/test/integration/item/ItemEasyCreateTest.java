package de.mpg.imeji.rest.resources.test.integration.item;

import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;

/**
 * Created by vlad on 09.12.14.
 */

public class ItemEasyCreateTest extends ItemTestBase {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ItemEasyCreateTest.class);

	private static String itemJSON;
	private static final String pathPrefix = "/rest/items";
	private static final String referenceUrl = "http://th03.deviantart.net/fs71/PRE/i/2012/242/1/f/png_moon_by_paradise234-d5czhdo.png";

	@BeforeClass
	public static void specificSetup() throws Exception {
		initCollectionWithProfile(getEasyBasicStatements());
		itemJSON = getStringFromPath("src/test/resources/rest/easyCreateItem.json");
	}

	@Test
	public void test_1_createItem_with_easyMetadata() throws IOException {

		FormDataMultiPart multiPart = new FormDataMultiPart();
		multiPart.field("json",
						itemJSON.replace("___COLLECTION_ID___", collectionId)
								.replace("___FILENAME___", "test.png")
								.replaceAll(
										"\"fetchUrl\"\\s*:\\s*\"___FETCH_URL___\",",
										"")
								.replaceAll("___REFERENCE_URL___",referenceUrl));

		Response response = target(pathPrefix).register(authAsUser)
				.register(MultiPartFeature.class)
				.register(JacksonFeature.class)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(multiPart, multiPart.getMediaType()));

		assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

		ItemTO itemTO = response.readEntity(ItemWithFileTO.class);
		assertThat(itemTO.getMetadata(), hasSize(7)); //check easyCreateItem.json
		assertThat(itemTO.getCollectionId(), equalTo(collectionId));



	}



}
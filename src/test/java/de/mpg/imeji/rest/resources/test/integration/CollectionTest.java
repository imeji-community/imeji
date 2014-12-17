package de.mpg.imeji.rest.resources.test.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.JenaUtil;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.resources.test.TestUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CollectionTest extends ImejiRestTest {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CollectionTest.class);

	private static String pathPrefix = "/collections";

	@Before
	public void specificSetup() {
		initCollection();
		initItem();
	}

	@Test
	public void test_1_CreateCollection_1_DefaultProfile() throws IOException {
		Path jsonPath = Paths
				.get("src/test/resources/rest/createCollection.json");
		String jsonString = new String(Files.readAllBytes(jsonPath), "UTF-8");

		Response response = target(pathPrefix)
				.register(authAsUser)
				.register(MultiPartFeature.class)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity
						.entity(jsonString, MediaType.APPLICATION_JSON_TYPE));
		assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
		Map<String, Object> collData = TestUtils.jsonToPOJO(response);
		assertNotNull("Created collection is null", collData);
		collectionId = (String) collData.get("id");
		assertThat("Empty collection id", collectionId,
				not(isEmptyOrNullString()));
		System.out.println(collectionId);
	}

	@Ignore
	@Test
	public void test_1_CreateCollection_2_CopyProfile() throws IOException {
		Path jsonPath = Paths
				.get("src/test/resources/rest/createCollectionWithProfile.json");
		String jsonString = new String(Files.readAllBytes(jsonPath), "UTF-8");
		jsonString = jsonString.replace("___PROFILE_ID___",
				collectionTO.getProfile().getProfileId()).replace(
				"___METHOD___", "copy");

		Response response = target(pathPrefix)
				.register(authAsUser)
				.register(MultiPartFeature.class)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity
						.entity(jsonString, MediaType.APPLICATION_JSON_TYPE));

		assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
		Map<String, Object> collData = TestUtils.jsonToPOJO(response);
		assertNotNull("Created collection is null", collData);
		collectionId = (String) collData.get("id");
		assertThat("Empty collection id", collectionId,
				not(isEmptyOrNullString()));
	}

	@Ignore
	@Test
	public void test_1_CreateCollection_3_ReferenceProfile() throws IOException {
		Path jsonPath = Paths
				.get("src/test/resources/rest/createCollectionWithProfile.json");
		String jsonString = new String(Files.readAllBytes(jsonPath), "UTF-8");
		jsonString = jsonString.replace("___PROFILE_ID___",
				collectionTO.getProfile().getProfileId()).replace(
				"___METHOD___", "reference");

		Response response = target(pathPrefix)
				.register(authAsUser)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity
						.entity(jsonString, MediaType.APPLICATION_JSON_TYPE));

		assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
		Map<String, Object> collData = TestUtils.jsonToPOJO(response);
		assertNotNull("Created collection is null", collData);
		collectionId = (String) collData.get("id");
		assertThat("Empty collection id", collectionId,
				not(isEmptyOrNullString()));
	}

	@Test
	public void test_1_CreateCollection_4_NotExistedReferenceProfile()
			throws IOException {
		Path jsonPath = Paths
				.get("src/test/resources/rest/createCollectionWithProfile.json");
		String jsonString = new String(Files.readAllBytes(jsonPath), "UTF-8");
		jsonString = jsonString.replace("___PROFILE_ID___",
				collectionTO.getProfile().getProfileId() + "shmarrn").replace(
				"___METHOD___", "reference");
		Response response = target(pathPrefix)
				.register(authAsUser)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity
						.entity(jsonString, MediaType.APPLICATION_JSON_TYPE));

		assertEquals(response.getStatus(), Status.BAD_REQUEST.getStatusCode());

	}

	@Test
	public void test_2_ReadCollection_1() throws IOException {
		Response response = target(pathPrefix).path(collectionId)
				.register(authAsUser).request(MediaType.APPLICATION_JSON).get();

		String jsonString = response.readEntity(String.class);
		assertThat("Empty collection", jsonString, not(isEmptyOrNullString()));
	}

	@Test
	public void test_2_ReadCollection_2_BadRequest() throws IOException {
		Response response = target(pathPrefix).path(collectionId + "schmarrn")
				.register(authAsUser).request(MediaType.APPLICATION_JSON).get();

		assertThat(response.getStatus(),
				equalTo(Status.BAD_REQUEST.getStatusCode()));
	}

	@Test
	public void test_2_ReadCollection_3_Unauthorized() throws IOException {
		Response response = target(pathPrefix).path(collectionId)
				.request(MediaType.APPLICATION_JSON).get();
		// String jsonString = response.readEntity(String.class);
		// assertThat("Authentication should fail!", jsonString,
		// containsString("<div class=\"header\">Unauthorized</div>"));
		assertThat(response.getStatus(),
				equalTo(Status.UNAUTHORIZED.getStatusCode()));

	}

	@Test
	public void test_2_ReadCollection_4_Forbidden() throws IOException {
		Response response = target(pathPrefix).path(collectionId)
				.register(authAsUser2).request(MediaType.APPLICATION_JSON)
				.get();
		assertThat(response.getStatus(),
				equalTo(Status.FORBIDDEN.getStatusCode()));
	}

	@Test
	public void test_3_ReleaseCollection_1_WithAdmin() throws Exception {
		Response response = target(pathPrefix)
				.path("/" + collectionId + "/release").register(authAsUser)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.put(Entity.json("{}"));
		assertEquals(response.getStatus(), Status.OK.getStatusCode());
		CollectionService s = new CollectionService();
		assertEquals("RELEASED", s.read(collectionId, JenaUtil.testUser)
				.getStatus());

	}
}

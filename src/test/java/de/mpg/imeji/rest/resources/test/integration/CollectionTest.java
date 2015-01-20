package de.mpg.imeji.rest.resources.test.integration;


import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
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
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.java.dev.webdav.jaxrs.ResponseStatus;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.JenaUtil;
import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.resources.test.TestUtils;
import de.mpg.j2j.exceptions.NotFoundException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CollectionTest extends ImejiTestBase {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CollectionTest.class);

	private static String pathPrefix = "/rest/collections";

	@Before
	public void specificSetup() {
		initCollection();
		
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
		assertEquals(response.getStatus(), CREATED.getStatusCode());
		Map<String, Object> collData = TestUtils.jsonToPOJO(response);
		assertNotNull("Created collection is null", collData);
		collectionId = (String) collData.get("id");
		assertThat("Empty collection id", collectionId,
				not(isEmptyOrNullString()));
	}

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

		assertEquals(response.getStatus(), CREATED.getStatusCode());
		Map<String, Object> collData = TestUtils.jsonToPOJO(response);
		assertNotNull("Created collection is null", collData);
		collectionId = (String) collData.get("id");
		assertThat("Empty collection id", collectionId,
				not(isEmptyOrNullString()));
	}

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

		assertEquals(response.getStatus(), CREATED.getStatusCode());
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

		assertEquals(response.getStatus(), ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode());

	}

	//TODO: TEST for user who does not have right to create collection
	
	@Test
	public void test_2_ReadCollection_1() throws IOException {
		Response response = target(pathPrefix).path(collectionId)
				.register(authAsUser).request(MediaType.APPLICATION_JSON).get();

		String jsonString = response.readEntity(String.class);
		assertThat("Empty collection", jsonString, not(isEmptyOrNullString()));
	}

	@Test
	public void test_2_ReadCollection_3_Unauthorized() throws IOException {
		Response response = target(pathPrefix).path(collectionId)
				.request(MediaType.APPLICATION_JSON).get();
		// String jsonString = response.readEntity(String.class);
		// assertThat("Authentication should fail!", jsonString,
		// containsString("<div class=\"header\">Unauthorized</div>"));
		assertThat(response.getStatus(),
				equalTo(UNAUTHORIZED.getStatusCode()));

	}

	@Test
	public void test_2_ReadCollection_4_Forbidden() throws IOException {
		Response response = target(pathPrefix).path(collectionId)
				.register(authAsUser2).request(MediaType.APPLICATION_JSON)
				.get();
		assertThat(response.getStatus(),
				equalTo(FORBIDDEN.getStatusCode()));
	}
	
	@Test
	public void test_2_ReadCollection_4_DoesNotExist() throws IOException {
		Response response = target(pathPrefix).path(collectionId+"i_do_not_exist")
				.register(authAsUser).request(MediaType.APPLICATION_JSON)
				.get();
		assertThat(response.getStatus(),
				equalTo(Status.NOT_FOUND.getStatusCode()));
	}

	@Test
	public void test_3_ReleaseCollection_1_WithAuth() throws Exception {
		ItemService itemStatus = new ItemService();
		initItem();
		assertEquals("PENDING",itemStatus.read(itemId, JenaUtil.testUser).getStatus());
		
		Response response = target(pathPrefix)
				.path("/" + collectionId + "/release").register(authAsUser)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.put(Entity.json("{}"));

		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		CollectionService s = new CollectionService();
		assertEquals("RELEASED", s.read(collectionId, JenaUtil.testUser)
				.getStatus());
		
		assertEquals("RELEASED",itemStatus.read(itemId, JenaUtil.testUser).getStatus());
		
	}
	
	@Test
	public void test_3_ReleaseCollection_2_WithUnauth() throws NotAllowedError, NotFoundException, Exception{
		ItemService itemStatus = new ItemService();
		initItem();
		assertEquals("PENDING",itemStatus.read(itemId, JenaUtil.testUser).getStatus());
		Response response = target(pathPrefix)
				.path("/" + collectionId + "/release").register(authAsUser2)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.put(Entity.json("{}"));
		assertEquals(FORBIDDEN.getStatusCode(), response.getStatus());
		
	
		assertEquals("PENDING",itemStatus.read(itemId, JenaUtil.testUser).getStatus());
	}
	@Test
	public void test_3_ReleaseCollection_3_EmptyCollection(){
		Response response = target(pathPrefix)
				.path("/" + collectionId + "/release").register(authAsUser)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.put(Entity.json("{}"));	
		assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
	}
	@Test
	public void test_3_ReleaseCollection_4_WithOutUser(){
		
		Response response = target(pathPrefix)
				.path("/" + collectionId + "/release")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.put(Entity.json("{}"));
		assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void test_3_ReleaseCollection_5_ReleaseCollectionTwice() throws NotAllowedError, NotFoundException, Exception{
		initItem();
		CollectionService s = new CollectionService();
		s.release(collectionId, JenaUtil.testUser);
		assertEquals("RELEASED", s.read(collectionId, JenaUtil.testUser)
				.getStatus());
		Response response = target(pathPrefix)
				.path("/" + collectionId + "/release").register(authAsUser)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.put(Entity.json("{}"));
		assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void test_3_ReleaseCollection_6_nonExistingCollection(){
		Response response = target(pathPrefix)
				.path("/" + collectionId + "i_do_not_exist/release").register(authAsUser)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.put(Entity.json("{}"));	
		assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}

	@Test
	public void test_4_WithdrawCollection_1_WithAuth() throws Exception {
		
		ItemService itemStatus = new ItemService();
		initItem();
		CollectionService s = new CollectionService();
		s.release(collectionId, JenaUtil.testUser);
		
		assertEquals("RELEASED", s.read(collectionId, JenaUtil.testUser).getStatus());
		assertEquals("RELEASED",itemStatus.read(itemId, JenaUtil.testUser).getStatus());
		
		Form form= new Form();
		form.param("id", collectionId);
		form.param("discardComment", "test_4_WithdrawCollection_1_WithAuth_"+System.currentTimeMillis());
		Response response = target(pathPrefix)
				.path("/" + collectionId + "/withdraw").register(authAsUser)
				.request((MediaType.APPLICATION_JSON_TYPE))
				.put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		
		assertEquals("WITHDRAWN", s.read(collectionId, JenaUtil.testUser)
				.getStatus());
		
		assertEquals("WITHDRAWN",itemStatus.read(itemId, JenaUtil.testUser).getStatus());
		
	}
	
	
	@Test
	public void test_4_WithdrawCollection_2_WithUnauth() throws Exception{
		
		initItem();
		CollectionService s = new CollectionService();
		s.release(collectionId, JenaUtil.testUser);
		
		assertEquals("RELEASED", s.read(collectionId, JenaUtil.testUser).getStatus());
		
		Form form= new Form();
		form.param("id", collectionId);
		form.param("discardComment", "test_4_WithdrawCollection_2_WithUnAuth_"+System.currentTimeMillis());
		Response response = target(pathPrefix)
				.path("/" + collectionId + "/withdraw").register(authAsUser2)
				.request((MediaType.APPLICATION_JSON_TYPE))
				.put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
	 }

	@Test
	public void test_4_WithdrawCollection_3_WithNonAuth() throws Exception {

		initItem();
		CollectionService s = new CollectionService();
		s.release(collectionId, JenaUtil.testUser);
		assertEquals("RELEASED", s.read(collectionId, JenaUtil.testUser).getStatus());
		
		Form form= new Form();
		form.param("id", collectionId);
		form.param("discardComment", "test_4_WithdrawCollection_3_WithNonAuth_"+System.currentTimeMillis());
		Response response = target(pathPrefix)
				.path("/" + collectionId + "/withdraw")
				.request((MediaType.APPLICATION_JSON_TYPE))
				.put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
	 }


	@Test

	public void test_4_WithdrawCollection_4_NotReleasedCollection() throws Exception {

		initItem();
		CollectionService s = new CollectionService();
		assertEquals("PENDING", s.read(collectionId, JenaUtil.testUser).getStatus());
		
		Form form= new Form();
		form.param("id", collectionId);
		form.param("discardComment", "test_4_WithdrawCollection_4_NotReleasedCollection_"+System.currentTimeMillis());
		Response response = target(pathPrefix)
				.path("/" + collectionId + "/withdraw").register(authAsUser)
				.request((MediaType.APPLICATION_JSON_TYPE))
				.put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void test_4_WithdrawCollection_5_WithdrawCollectionTwice() throws Exception{

		initItem();
		CollectionService s = new CollectionService();
		s.release(collectionId, JenaUtil.testUser);
		s.withdraw (collectionId, JenaUtil.testUser,"test_4_WithdrawCollection_5_WithdrawCollectionTwice_"+System.currentTimeMillis());

		assertEquals("WITHDRAWN", s.read(collectionId, JenaUtil.testUser).getStatus());
		
		Form form= new Form();
		form.param("id", collectionId);
		form.param("discardComment", "test_4_WithdrawCollection_5_WithdrawCollectionTwice_SecondTime_"+System.currentTimeMillis());
		Response response = target(pathPrefix)
				.path("/" + collectionId + "/withdraw").register(authAsUser)
				.request((MediaType.APPLICATION_JSON_TYPE))
				.put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void test_4_WithdrawCollection_5_NotExistingCollection() throws Exception{
		
		Form form= new Form();
		form.param("id", collectionId+"i_do_not_exist");
		form.param("discardComment", "test_4_WithdrawCollection_5_WithdrawCollectionTwice_SecondTime_"+System.currentTimeMillis());
		Response response = target(pathPrefix)
				.path("/" + collectionId + "i_do_not_exist/withdraw").register(authAsUser)
				.request((MediaType.APPLICATION_JSON_TYPE))
				.put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

		assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}
}

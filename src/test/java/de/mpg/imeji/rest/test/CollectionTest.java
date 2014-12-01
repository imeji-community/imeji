package de.mpg.imeji.rest.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserController.USER_TYPE;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.MyApplication;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.test.DeploymentContext;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.ServletDeploymentContext;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.JenaUtil;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static javax.ws.rs.core.Response.*;


import static org.hamcrest.MatcherAssert.assertThat;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CollectionTest extends JerseyTest {


	private static final Logger LOGGER = LoggerFactory.getLogger(CollectionTest.class);


	private static String adminEmail = "admin@imeji.org";
	private static String adminName = "imeji admin";
	private static String adminPwd = "admin";

	private static String userEmail = "user@imeji.org";
	private static String userName = "imeji user";
	private static String userPwd = "user";


	private static String pathPrefix = "/collections";

	private static String collId = "";

	private static HttpAuthenticationFeature authAsAdmin = HttpAuthenticationFeature.basic(adminEmail, adminPwd);
	private static HttpAuthenticationFeature authAsUser = HttpAuthenticationFeature.basic(userEmail, userPwd);
	private static String profileId = null;

	@Override
	protected Application configure() {
		return new MyApplication();
	}




	@Override
	protected TestContainerFactory getTestContainerFactory() throws TestContainerException {
		return new MyTestContainerFactory();
//		return new GrizzlyWebTestContainerFactory();
	}

	@BeforeClass
	public static void initilizeResources() throws IOException, URISyntaxException {
		JenaUtil.initJena();
		User admin = createImejiUser(adminEmail, adminName, adminPwd, USER_TYPE.ADMIN);
		createImejiUser(userEmail, userName, userPwd, USER_TYPE.DEFAULT);
		profileId = createProfile(admin);
	}

	@AfterClass
	public static void finishResources() throws IOException, URISyntaxException {
		JenaUtil.closeJena();

	}

	@Test
	public void test_1_CreateCollection_1_DefaultProfile() throws IOException {
		Path jsonPath = Paths.get("src/test/resources/rest/createCollection.json");
		String jsonString = new String(Files.readAllBytes(jsonPath), "UTF-8");

		Response response = target(pathPrefix)
				.register(authAsAdmin)
				.register(MultiPartFeature.class)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(jsonString, MediaType.APPLICATION_JSON_TYPE));

		assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
		Map<String, Object> collData = jsonToPOJO(response);
		assertNotNull("Created collection is null", collData);
		collId = (String)collData.get("id");
		assertThat("Empty collection id", collId, not(isEmptyOrNullString()));
	}

	@Test
	public void test_1_CreateCollection_2_CopyProfile() throws IOException {
		Path jsonPath = Paths.get("src/test/resources/rest/createCollectionWithProfile.json");
		String jsonString = new String(Files.readAllBytes(jsonPath), "UTF-8");
		jsonString = jsonString
				.replace("___PROFILE_ID___", profileId)
				.replace("___METHOD___", "copy");

		Response response = target(pathPrefix)
				.register(authAsAdmin)
				.register(MultiPartFeature.class)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(jsonString, MediaType.APPLICATION_JSON_TYPE));

		assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
		Map<String, Object> collData = jsonToPOJO(response);
		assertNotNull("Created collection is null", collData);
		collId = (String)collData.get("id");
		assertThat("Empty collection id", collId, not(isEmptyOrNullString()));
	}

	@Test
	public void test_1_CreateCollection_3_ReferenceProfile() throws IOException {
		Path jsonPath = Paths.get("src/test/resources/rest/createCollectionWithProfile.json");
		String jsonString = new String(Files.readAllBytes(jsonPath), "UTF-8");
		jsonString = jsonString
				.replace("___PROFILE_ID___", profileId)
				.replace("___METHOD___", "reference");

		Response response = target(pathPrefix)
				.register(authAsAdmin)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(jsonString, MediaType.APPLICATION_JSON_TYPE));

		assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
		Map<String, Object> collData = jsonToPOJO(response);
		assertNotNull("Created collection is null", collData);
		collId = (String)collData.get("id");
		assertThat("Empty collection id", collId, not(isEmptyOrNullString()));
	}


	@Test
	public void test_2_ReadCollection_1() throws IOException {
		Response response = target(pathPrefix)
				.path(collId)
				.register(authAsAdmin)
				.request(MediaType.APPLICATION_JSON)
				.get();
		String jsonString = response.readEntity(String.class);
		assertThat("Empty collection", jsonString, not(isEmptyOrNullString()));
	}

	@Test
	public void test_2_ReadCollection_2_BadRequest() throws IOException {
		Response response = target(pathPrefix)
				.path(collId + "schmarrn")
				.register(authAsAdmin)
				.request(MediaType.APPLICATION_JSON)
				.get();
		assertThat(response.getStatus(), equalTo(Status.BAD_REQUEST.getStatusCode()));
	}

	@Test
	public void test_2_ReadCollection_3_Unathorized() throws IOException {
		Response response = target(pathPrefix)
				.path(collId)
				.request(MediaType.APPLICATION_JSON)
				.get();
		//String jsonString = response.readEntity(String.class);
//		assertThat("Authentication should fail!", jsonString, containsString("<div class=\"header\">Unauthorized</div>"));
		assertThat(response.getStatus(), equalTo(Status.UNAUTHORIZED.getStatusCode()));

	}

	@Test
	public void test_2_ReadCollection_4_Forbidden() throws IOException {
		Response response = target(pathPrefix)
				.path(collId)
				.register(authAsUser)
				.request(MediaType.APPLICATION_JSON)
				.get();
		assertThat(response.getStatus(), equalTo(Status.FORBIDDEN.getStatusCode()));
	}



	public static User createImejiUser(String email, String name, String pwd, USER_TYPE type) {
		User user = null;
		try {
			UserController c = new UserController(Imeji.adminUser);
			user = new User();
			user.setEmail(email);
			user.setName(name);
			user.setEncryptedPassword(StringHelper.convertToMD5(pwd));
			user = c.create(user, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public static String createProfile(User u) {
		try {
			ProfileController c = new ProfileController();
			MetadataProfile p = c.create(ImejiFactory.newProfile(), u);
			return p.getId().toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	private Map<String, Object> jsonToPOJO(Response response) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(ByteStreams.toByteArray(response.readEntity(InputStream.class)), Map.class);
	}



}

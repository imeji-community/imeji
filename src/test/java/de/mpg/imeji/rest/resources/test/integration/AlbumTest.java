package de.mpg.imeji.rest.resources.test.integration;

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
import java.io.UnsupportedEncodingException;
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

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.JenaUtil;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.exceptions.NotAllowedError;
import de.mpg.imeji.exceptions.NotFoundException;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.resources.test.TestUtils;




@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AlbumTest extends ImejiTestBase{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AlbumTest.class);
	
	private static String pathPrefix = "/rest/albums";

	
	@Before
	public void specificSetup() {
		initAlbum();
		
	}

	@Test
	public void test_1_CreateAlbum_1() throws IOException {
		Path jsonPath = Paths
				.get("src/test/resources/rest/createAlbum.json");
		String jsonString = new String(Files.readAllBytes(jsonPath), "UTF-8");

		Response response = target(pathPrefix)
				.register(authAsUser)
				.register(MultiPartFeature.class)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity
						.entity(jsonString, MediaType.APPLICATION_JSON_TYPE));
		assertEquals(response.getStatus(), CREATED.getStatusCode());
		Map<String, Object> albData = TestUtils.jsonToPOJO(response);
		assertNotNull("Created album is null", albData);
		albumId = (String) albData.get("id");
		assertThat("Empty album id", albumId,
				not(isEmptyOrNullString()));
	}


	@Test
	public void test_2_ReadAlbum_1() throws ImejiException {
		Response response = target(pathPrefix).path(albumId)
				.register(authAsUser).request(MediaType.APPLICATION_JSON).get();

		String jsonString = response.readEntity(String.class);
		assertThat("Empty album", jsonString, not(isEmptyOrNullString()));
	}

	@Test
	public void test_2_ReadAlbum_2_Unauthorized() throws ImejiException {
		Response response = target(pathPrefix).path(albumId)
				.request(MediaType.APPLICATION_JSON).get();
		assertThat(response.getStatus(),
				equalTo(UNAUTHORIZED.getStatusCode()));

	}

	@Test
	public void test_2_ReadAlbum_3_Forbidden() throws ImejiException {
		Response response = target(pathPrefix).path(albumId)
				.register(authAsUser2).request(MediaType.APPLICATION_JSON)
				.get();
		assertThat(response.getStatus(),
				equalTo(FORBIDDEN.getStatusCode()));
	}

	@Test
	public void test_2_ReadAlbum_4_DoesNotExist() throws IOException {
		Response response = target(pathPrefix).path(albumId+"i_do_not_exist")
				.register(authAsUser).request(MediaType.APPLICATION_JSON)
				.get();
		assertThat(response.getStatus(),
				equalTo(Status.NOT_FOUND.getStatusCode()));
	}
	
	@Test
	public void test_3_DeleteAlbum_1_WithAuth() throws ImejiException {

		initAlbum();

		Response response = target(pathPrefix)
				.path("/" + albumId).register(authAsUser)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.delete();

		assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());
		
		response = target(pathPrefix).
				path(albumId).register(authAsUser).
				request(MediaType.APPLICATION_JSON)
				.get();
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
		
	}
	
	@Test
	public void test_3_DeleteAlbum_2_WithUnauth() throws ImejiException{
		initAlbum();

		Response response = target(pathPrefix)
				.path("/" + albumId).register(authAsUser2)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.delete();

		assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
	}

	@Test
	public void test_3_DeleteAlbum_4_WithOutUser(){
		
		initAlbum();
		
		Response response = target(pathPrefix)
				.path("/" + albumId)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.delete();
		assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
	}
	
		
	@Test
	public void test_3_DeleteAlbum_6_nonExistingAlbum(){
		Response response = target(pathPrefix)
				.path("/" + albumId+"i_do_not_exist").register(authAsUser)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.delete();

		assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}




}

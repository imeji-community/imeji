package de.mpg.imeji.rest.resources.test.integration;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;






import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.rest.api.CollectionService;

public class ProfileTest extends ImejiTestBase{

	private static String pathPrefix = "/rest/profiles";

	@Before
	public void specificSetup() {
		initCollection();
		initItem();
	}
	
	@Test
	public void test_1_ReadProfiles(){
		String profileId = collectionTO.getProfile().getProfileId();
		Response response = target(pathPrefix).path(profileId)
				.register(authAsUser)
				.request(MediaType.APPLICATION_JSON).get();
		assertEquals(Status.OK.getStatusCode(),response.getStatus());
	}
	
	@Test
	public void test_1_ReadProfiles_ReleaseCollection() throws Exception{
		CollectionService cs = new CollectionService();
		cs.release(collectionId, JenaUtil.testUser);
		String profileId = collectionTO.getProfile().getProfileId();
		Response response = target(pathPrefix).path(profileId)
				.request(MediaType.APPLICATION_JSON).get();
		assertEquals(Status.OK.getStatusCode(),response.getStatus());
	}
	
	@Ignore
	@Test
	public void test_1_ReadProfiles_Unauthorized(){
		String profileId = collectionTO.getProfile().getProfileId();
		Response response = target(pathPrefix).path(profileId)
				.request(MediaType.APPLICATION_JSON).get();
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),response.getStatus());
	}
	
	@Test
	public void test_1_ReadProfiles_InvalidProfileId(){
		String profileId = collectionTO.getProfile().getProfileId();
		Response response = target(pathPrefix).path(profileId+"invalidID")
				.register(authAsUser)
				.request(MediaType.APPLICATION_JSON).get();
		assertEquals(Status.NOT_FOUND.getStatusCode(),response.getStatus());
	}
	@Ignore
	@Test
	public void test_1_ReadProfiles_NotAllowedUser(){
		String profileId = collectionTO.getProfile().getProfileId();
		Response response = target(pathPrefix).path(profileId)
				.register(authAsUser2)
				.request(MediaType.APPLICATION_JSON).get();
		assertEquals(Status.FORBIDDEN.getStatusCode(),response.getStatus());
	}
}

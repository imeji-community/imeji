package de.mpg.imeji.rest.resources.test.integration;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.to.MetadataProfileTO;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.JenaUtil;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ProfileTest extends ImejiTestBase{

	private static String pathPrefix = "/rest/profiles";
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ProfileTest.class);

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

	@Test
	public void test_1_ReadProfiles_NotAllowedUser(){
		String profileId = collectionTO.getProfile().getProfileId();
		Response response = target(pathPrefix).path(profileId)
				.register(authAsUser2)
				.request(MediaType.APPLICATION_JSON).get();
		assertEquals(Status.FORBIDDEN.getStatusCode(),response.getStatus());
	}

	@Test
	public void test_2_ReadProfiles_Default(){
		String profileId = ProfileService.DEFAULT_METADATA_PROFILE_ID;
		Response response = target(pathPrefix).path(profileId)
				.register(authAsUser2)
				.request(MediaType.APPLICATION_JSON).get();
		assertEquals(Status.OK.getStatusCode(),response.getStatus());

        MetadataProfileTO profile = response.readEntity(MetadataProfileTO.class);
        assertThat(profile.getId(), equalTo(ObjectHelper.getId(Imeji.defaultMetadataProfile.getId())));


    }
}

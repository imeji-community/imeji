package de.mpg.imeji.rest.resources.test.integration;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.MetadataProfileTO;
import net.java.dev.webdav.jaxrs.ResponseStatus;

import org.junit.Before;
import org.junit.Ignore;
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
		initProfile();
	}
	
	@Test
	public void test_1_ReadProfiles(){
		String profileId = collectionTO.getProfile().getId();
		Response response = target(pathPrefix).path(profileId)
				.register(authAsUser)
				.request(MediaType.APPLICATION_JSON).get();
		assertEquals(Status.OK.getStatusCode(),response.getStatus());
	}
	
	@Test
	public void test_1_ReadProfiles_ReleaseCollection() throws Exception{
		CollectionService cs = new CollectionService();
		cs.release(collectionId, JenaUtil.testUser);
		String profileId = collectionTO.getProfile().getId();
		Response response = target(pathPrefix).path(profileId).register(authAsUser2)
				.request(MediaType.APPLICATION_JSON).get();
		assertEquals(Status.OK.getStatusCode(),response.getStatus());
	}
	
	@Ignore // Everybody ca read any profiles
	@Test
	public void test_1_ReadProfiles_Unauthorized(){
		String profileId = collectionTO.getProfile().getId();
		Response response = target(pathPrefix).path(profileId)
				.request(MediaType.APPLICATION_JSON).get();
		assertEquals(Status.UNAUTHORIZED.getStatusCode(),response.getStatus());
	}
	
	@Test
	public void test_1_ReadProfiles_InvalidProfileId(){
		String profileId = collectionTO.getProfile().getId();
		Response response = target(pathPrefix).path(profileId+"invalidID")
				.register(authAsUser)
				.request(MediaType.APPLICATION_JSON).get();
		assertEquals(Status.NOT_FOUND.getStatusCode(),response.getStatus());
	}
	
	@Test
	public void test_1_ReadProfiles_RegularProfileId(){
		String profileId = collectionTO.getProfile().getId();
		Response response = target(pathPrefix).path(profileId)
				.register(authAsUser)
				.request(MediaType.APPLICATION_JSON).get();
		assertEquals(Status.OK.getStatusCode(),response.getStatus());
	}

	@Ignore // Everybody ca read any profiles
	@Test
	public void test_1_ReadProfiles_NotAllowedUser(){
		String profileId = collectionTO.getProfile().getId();
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
	
	@Test
	public void test_3_DeleteProfile_NotAuthorized(){
		Response response = target(pathPrefix).path(profileId)
				.register(authAsUser2)
				.request(MediaType.APPLICATION_JSON).delete();
		assertEquals(Status.FORBIDDEN.getStatusCode(),response.getStatus());
    }

	@Test
	public void test_3_DeleteProfile_Referenced(){
		String profileId = collectionTO.getProfile().getId();
		Response response = target(pathPrefix).path(profileId)
				.register(authAsUser)
				.request(MediaType.APPLICATION_JSON).delete();
		assertEquals(ResponseStatus.UNPROCESSABLE_ENTITY.getStatusCode(),response.getStatus());
    }
	
	@Test
	public void test_3_DeleteProfile_notExists(){
		String profileId = collectionTO.getProfile().getId()+"_doesNotExist";
		Response response = target(pathPrefix).path(profileId)
				.register(authAsUser)
				.request(MediaType.APPLICATION_JSON).delete();
		assertEquals(Status.NOT_FOUND.getStatusCode(),response.getStatus());
    }
	
	
	/*
	 * At the moment no standalone test for delete profile can be created. 
	 * Imeji silently deletes it if its not related with anything else through the update of the collection
	 * 
	 
	 @Test
	public void test_3_DeleteProfile() throws ImejiException{
		initCollection();
		//keep data from the old collection
		String myOldProfileId = collectionTO.getProfile().getId();
		String myOldCollection = collectionTO.getId();
		CollectionTO oldCollectionTO = collectionTO; 
		//create new collection and new profile
		initCollection();
		//set the old collection profile to the newly created collection profile from the new collection
		oldCollectionTO.setProfile(collectionTO.getProfile());
		CollectionService cs = new CollectionService();
		oldCollectionTO.getProfile().setMethod("reference");
		cs.update(oldCollectionTO, JenaUtil.testUser);

		Response response = target(pathPrefix).path(myOldProfileId)
				.register(authAsUser)
				.request(MediaType.APPLICATION_JSON).delete();
		assertEquals(Status.NO_CONTENT.getStatusCode(),response.getStatus());
		
		response = target(pathPrefix).path(myOldProfileId).register(authAsUser)
				.request(MediaType.APPLICATION_JSON).get();
		assertEquals(Status.NOT_FOUND.getStatusCode(),response.getStatus());
    }
	* */
}

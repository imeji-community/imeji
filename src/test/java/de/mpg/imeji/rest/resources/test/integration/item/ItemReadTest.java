package de.mpg.imeji.rest.resources.test.integration.item;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.Before;
import org.junit.Test;

import util.JenaUtil;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.resources.test.TestUtils;
import de.mpg.imeji.rest.resources.test.integration.ImejiTestBase;

public class ItemReadTest extends ImejiTestBase{
	private static final String PATH_PREFIX = "/rest/items";
	
	@Before
	public void specificSetup() {
		initCollection();
		initItem();
		
	}
	
	@Test
	public void test_1_ReadItem_Default() throws Exception {
		
		 Response response = ( target(PATH_PREFIX).path("/" + itemId)
	                .register(authAsUser)
	                .register(MultiPartFeature.class)
	                .request(MediaType.APPLICATION_JSON_TYPE)).get();
	                
		 assertEquals(Status.OK.getStatusCode(), response.getStatus());
		 Map<String,Object> itemData = TestUtils.jsonToPOJO(response);
		 assertEquals(itemId, (String)itemData.get("id"));
	}
	
	@Test
	public void test_2_ReadItem_Unauthorized() throws IOException{
		 Response response = ( target(PATH_PREFIX).path("/" + itemId)
	                .register(MultiPartFeature.class)
	                .request(MediaType.APPLICATION_JSON_TYPE)).get();
		assertEquals(Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
//		String jsonString = response.readEntity(String.class);
		
		Response response2 = ( target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser2)
                .register(MultiPartFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)).get();
		assertEquals(Status.FORBIDDEN.getStatusCode(), response2.getStatus());

	}
	
	@Test
	public void test_3_ReadItem_Forbidden() throws IOException{
		
		Response response2 = ( target(PATH_PREFIX).path("/" + itemId)
                .register(authAsUser2)
                .register(MultiPartFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)).get();
		assertEquals(Status.FORBIDDEN.getStatusCode(), response2.getStatus());

	}
	
	@Test
	public void test_4_ReadItem_InReleaseCollection() throws Exception{
		CollectionService s = new CollectionService();
		s.release(collectionId, JenaUtil.testUser);
		assertEquals("RELEASED", s.read(collectionId, JenaUtil.testUser)
				.getStatus());
		
		Response response = ( target(PATH_PREFIX).path("/" + itemId)
                .register(MultiPartFeature.class)
                .request(MediaType.APPLICATION_JSON_TYPE)).get();
		 assertEquals(Status.OK.getStatusCode(), response.getStatus());
		 
		 Response response2 = ( target(PATH_PREFIX).path("/" + itemId)
	                .register(authAsUser2)
	                .register(MultiPartFeature.class)
	                .request(MediaType.APPLICATION_JSON_TYPE)).get();
		 assertEquals(Status.OK.getStatusCode(), response2.getStatus());
	}
	
	@Test
	public void test_5_ReadItem_InWithDrawnCollection() throws Exception{
		CollectionService s = new CollectionService();
		s.release(collectionId, JenaUtil.testUser);
		assertEquals("RELEASED", s.read(collectionId, JenaUtil.testUser)
				.getStatus());
		s.withdraw (collectionId, JenaUtil.testUser,"test_5_ReadItem_InWithDrawnCollection_"+System.currentTimeMillis());

		assertEquals("WITHDRAWN", s.read(collectionId, JenaUtil.testUser).getStatus());
	 
		 Response response = ( target(PATH_PREFIX).path("/" + itemId)
	                .register(authAsUser)
	                .register(MultiPartFeature.class)
	                .request(MediaType.APPLICATION_JSON_TYPE)).get();
		 
		 assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
	
	@Test
	public void test_6_ReadItem_NotFound() throws Exception{
		
		 Response response = ( target(PATH_PREFIX).path("/" + itemId+"_not_exist_item")
	                .register(authAsUser)
	                .register(MultiPartFeature.class)
	                .request(MediaType.APPLICATION_JSON_TYPE)).get();
		 
		 assertEquals(Status.NOT_FOUND.getStatusCode(), response.getStatus());
	}
	
	

}

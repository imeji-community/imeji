package de.mpg.imeji.rest.resources.test.integration.item;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildJSONFromObject;
import static de.mpg.imeji.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_REST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.JenaUtil;
import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.ItemService;
import de.mpg.imeji.rest.resources.test.integration.ImejiTestBase;
import de.mpg.imeji.rest.to.EasyItemTO;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.ItemWithFileTO;
import de.mpg.j2j.misc.LocalizedString;

/**
 * Test for easy Update
 * 
 * @author saquet
 *
 */
public class ItemEasyUpdateMetadataTest extends ImejiTestBase {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ItemEasyUpdateMetadataTest.class);
	private static EasyItemTO itemTO;
	private static String collectionId;
	private static String profileId;
	private static String easyItemJSON;
	private static final String PATH_PREFIX = "/rest/items/";
	private static final String SERVICE_ADDRESS = "http://localhost:9998";

	@BeforeClass
	public static void specificSetup() throws Exception {
		easyItemJSON = getStringFromPath(STATIC_CONTEXT_REST
				+ "/easyUpdateItemBasic.json");
		// initCollectionWithProfile(getBasicStatements());
		// createItem();
	}

	@Test
	public void simnpleEdit() throws BadRequestException {
		// TODO PATCH Method not supported by Jersey Client -> find a
		// workaround

		// FormDataMultiPart multiPart = new FormDataMultiPart();
		// multiPart.field("json", easyItemJSON);
		// LOGGER.info(multiPart.getField("json").getValue());
		// Response response = target(PATH_PREFIX + "?_HttpMethod=PATCH")
		// .path("/" + itemId).register(authAsUser)
		// .register(MultiPartFeature.class)
		// .register(JacksonFeature.class)
		// .request(MediaType.APPLICATION_JSON_TYPE)
		// .post(Entity.entity(multiPart, multiPart.getMediaType()));
		//
		// assertEquals(response.getStatus(), OK.getStatusCode());
		// EasyItemTO updatedItem = (EasyItemTO) response
		// .readEntity(EasyItemTO.class);

	}

	protected static void createItem() throws Exception {
		CollectionController cc = new CollectionController();
		ItemController ic = new ItemController();
		CollectionImeji coll = cc.retrieve(
				ObjectHelper.getURI(CollectionImeji.class, collectionId),
				JenaUtil.testUser);
		Item item = ImejiFactory.newItem(coll);
		item = ic.create(item, coll.getId(), JenaUtil.testUser);
		itemId = item.getIdString();

	}

	protected static void initCollectionWithProfile(
			Collection<Statement> statements) throws Exception {

		MetadataProfile p = ImejiFactory.newProfile();
		p.setTitle("test");
		p.setStatements(statements);
		ProfileController pc = new ProfileController();
		MetadataProfile mp = pc.create(p, JenaUtil.testUser);
		profileId = ObjectHelper.getId(mp.getId());

		try {
			CollectionController cc = new CollectionController();
			CollectionImeji col = ImejiFactory.newCollection();
			col.getMetadata().setTitle("test");
			Person per = ImejiFactory.newPerson();
			per.setFamilyName("test");
			Organization org = ImejiFactory.newOrganization();
			org.setName("test");
			List<Organization> orgs = new ArrayList<Organization>();
			orgs.add(org);
			per.setOrganizations(orgs);
			List<Person> pers = new ArrayList<Person>();
			pers.add(per);
			col.getMetadata().setPersons(pers);
			col.setProfile(mp.getId());
			collectionId = ObjectHelper.getId(cc.create(col, mp,
					JenaUtil.testUser, null));
		} catch (Exception e) {
			LOGGER.error("Cannot init Collection", e);
		}

	}

	protected static Collection<Statement> getBasicStatements() {
		Collection<Statement> statements = new ArrayList<Statement>();
		Statement st;
		for (String type : new String[] { "text", "number", "conePerson",
				"geolocation", "date", "license", "link", "publication" }) {
			st = new Statement();
			st.setType(URI.create("http://imeji.org/terms/metadata#" + type));
			st.getLabels().add(new LocalizedString(type, "en"));
			statements.add(st);
		}
		return statements;
	}
}

package de.mpg.imeji.rest.resources.test.integration.item;

import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildResponse;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildTOFromJSON;
import static de.mpg.imeji.rest.resources.test.integration.MyTestContainerFactory.STATIC_CONTEXT_REST;
import static javax.ws.rs.core.Response.Status.OK;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.JenaUtil;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.Patch;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.defaultTO.DefaultItemTO;
import de.mpg.imeji.rest.process.RestProcessUtils;
import de.mpg.imeji.rest.process.ReverseTransferObjectFactory;
import de.mpg.imeji.rest.process.TransferObjectFactory;
import de.mpg.imeji.rest.resources.test.integration.ImejiTestBase;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.MetadataProfileTO;
import de.mpg.imeji.rest.to.MetadataSetTO;
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
	private static Item item;
	private static String collectionId;
	private static String profileId;
	private static MetadataProfile profile;
	private static String easyItemJSON;
	private static final String ITEM_SERVICE_ADDRESS = "http://localhost:9998/rest/items/";

	@BeforeClass
	public static void specificSetup() throws Exception {
		easyItemJSON = getStringFromPath(STATIC_CONTEXT_REST
				+ "/easyUpdateItemBasic.json");
		initCollectionWithProfile(getBasicStatements());
		createItem();
	}

	@Test
	public void test_1_simpleEdit() throws Exception {
		Patch patch = new Patch(URI.create(ITEM_SERVICE_ADDRESS + itemId),
				JenaUtil.TEST_USER_EMAIL, JenaUtil.TEST_USER_PWD);
		try {
			patch.executeJSON(easyItemJSON);
			CloseableHttpResponse response = patch.getResponse();
			Assert.assertEquals(response.getStatusLine().getStatusCode(),
					HttpServletResponse.SC_OK);
			String responseJSON = EntityUtils.toString(response.getEntity());
//			System.out.println(responseJSON);
//			DefaultItemTO easyTO = (DefaultItemTO) buildTOFromJSON(
//					responseJSON, DefaultItemTO.class);
//			MetadataProfileTO profileTO = new MetadataProfileTO();
//			TransferObjectFactory.transferMetadataProfile(profile, profileTO);
//			ItemTO to = new ItemTO();
//			TransferObjectFactory.transferEasyItemTOItem(profileTO, easyTO,
//					itemTO);
//			String requestJSON = RestProcessUtils.buildJSONFromObject(itemTO);
//			System.out.println(requestJSON);
//			System.out.println(responseJSON);
			
		} finally {
			patch.close();
		}
	}

	protected static void createItem() throws Exception {
		CollectionController cc = new CollectionController();
		ItemController ic = new ItemController();
		CollectionImeji coll = cc.retrieve(
				ObjectHelper.getURI(CollectionImeji.class, collectionId),
				JenaUtil.testUser);
		item = ImejiFactory.newItem(coll);
		item = ic.create(item, coll.getId(), JenaUtil.testUser);
		itemId = item.getIdString();
		//TransferObjectFactory.transferItem(item, itemTO);
	}

	protected static void initCollectionWithProfile(
			Collection<Statement> statements) throws Exception {

		MetadataProfile p = ImejiFactory.newProfile();
		p.setTitle("test");
		p.setStatements(statements);
		ProfileController pc = new ProfileController();
		profile = pc.create(p, JenaUtil.testUser);
		profileId = ObjectHelper.getId(profile.getId());

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
			col.setProfile(profile.getId());
			collectionId = ObjectHelper.getId(cc.create(col, profile,
					JenaUtil.testUser, null));
		} catch (Exception e) {
			LOGGER.error("Cannot init Collection", e);
		}

	}

	protected static Collection<Statement> getBasicStatements() {
		Collection<Statement> statements = new ArrayList<Statement>();
		Statement st;
		for (String type : new String[] { "text", "number", "person",
				"geolocation", "date", "license", "link", "publication" }) {
			st = new Statement();
			st.setType(URI.create("http://imeji.org/terms/metadata#" + type));
			st.getLabels().add(new LocalizedString(type, "en"));
			statements.add(st);
		}
		return statements;
	}
}

package de.mpg.imeji.service.test;

import de.mpg.imeji.logic.auth.exception.NotAllowedError;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.to.MetadataProfileTO;
import de.mpg.imeji.rest.to.StatementTO;
import de.mpg.j2j.exceptions.NotFoundException;
import de.mpg.j2j.misc.LocalizedString;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.JenaUtil;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.*;

public class ProfileServiceTest {

	private CollectionImeji c;
	private MetadataProfile p;

	private String typeText = "http://imeji.org/terms/metadata#text";
	private String typeNumber = "http://imeji.org/terms/metadata#number";
	private String textLabel = "profile text";
	private String numberLabel = "profile number";
	private String textLanguage = "en";
	private String numberLanguage = "de";

	@Before
	public void setup() throws Exception {
		JenaUtil.initJena();
		initProfile();
	}

	@After
	public void tearDown() throws Exception {

		JenaUtil.closeJena();
	}

	public void initProfile() throws Exception {
		CollectionController cController = new CollectionController();
		ProfileController pController = new ProfileController();
		Collection<Statement> statements = new ArrayList<Statement>();
		Collection<LocalizedString> labels1 = new ArrayList<LocalizedString>();
		Collection<LocalizedString> labels2 = new ArrayList<LocalizedString>();

		Statement stText = new Statement();
		Statement stNumber = new Statement();
		LocalizedString lsText = new LocalizedString();
		LocalizedString lsNumber = new LocalizedString();

		stText.setType(URI.create(typeText));
		lsText.setValue(textLabel);
		lsText.setLang(textLanguage);

		stNumber.setType(URI.create(typeNumber));
		stNumber.setParent(stText.getId());
		lsNumber.setValue(numberLabel);
		lsNumber.setLang(numberLanguage);

		labels1.add(lsText);
		stText.setLabels(labels1);

		labels2.add(lsNumber);
		stNumber.setLabels(labels2);

		statements.add(stText);
		statements.add(stNumber);

		c = ImejiFactory.newCollection();
		p = ImejiFactory.newProfile();

		c.getMetadata().setTitle("test collection");
		p.setStatements(statements);

		c.setProfile(p.getId());

		pController.create(p, JenaUtil.testUser);
		cController.create(c, p, JenaUtil.testUser);
	}

	@Test
	public void testProfileCRUD() throws NotFoundException, NotAllowedError,
			Exception {
		CollectionService collcrud = new CollectionService();
		ProfileService pCrud = new ProfileService();
		MetadataProfileTO profile = new MetadataProfileTO();

		// read profile without login
		try {
			pCrud.read(p.getIdString(), null);
			fail("Not logged in should not allowed to read profile");
		} catch (Exception e) {

		}

		// read profile with login
		try {
			profile = pCrud.read(p.getIdString(), JenaUtil.testUser);
		} catch (Exception e) {
			fail("could not read Profile");
		}

		// check profile id exists
		assertNotNull(profile.getId());

		// check profile id
		assertEquals(c.getProfile().toString().split("/")[5], profile.getId());

		StatementTO proTO1 = profile.getStatements().get(0);
		StatementTO proTO2 = profile.getStatements().get(1);

		Statement pro1 = ((ArrayList<Statement>) p.getStatements()).get(0);
		Statement pro2 = ((ArrayList<Statement>) p.getStatements()).get(1);

		// check statement id
		assertEquals(pro1.getId().toString().split("/")[5], proTO1.getId());
		assertEquals(pro2.getId().toString().split("/")[5], proTO2.getId());

		// check statement 2 has a parent
		assertNotNull(proTO2.getParentStatementId());

		// check statement 1 do not have a parent
		assertNull(proTO1.getParentStatementId());

		// check statememt 2 is child of statement 1
		assertEquals(proTO1.getId(), proTO2.getParentStatementId());
		assertNotEquals(proTO2.getId(), proTO1.getParentStatementId());
		assertEquals(pro1.getId().toString().split("/")[5],
				proTO2.getParentStatementId());

		// check statement type
		assertEquals(pro1.getType(), proTO1.getType());
		assertEquals(pro2.getType(), proTO2.getType());

		// check statement label value
		assertEquals(((ArrayList<LocalizedString>) pro1.getLabels()).get(0)
				.getValue(), proTO1.getLabels().get(0).getValue());
		assertEquals(((ArrayList<LocalizedString>) pro2.getLabels()).get(0)
				.getValue(), proTO2.getLabels().get(0).getValue());

	}

}

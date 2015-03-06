package de.mpg.imeji.service.test;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.to.MetadataProfileTO;
import de.mpg.imeji.rest.to.StatementTO;
import de.mpg.j2j.misc.LocalizedString;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.JenaUtil;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import static de.mpg.imeji.logic.Imeji.adminUser;
import static de.mpg.imeji.logic.util.ResourceHelper.getStringFromPath;
import static de.mpg.imeji.rest.process.RestProcessUtils.buildTOFromJSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static util.JenaUtil.testUser;

public class ProfileServiceTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ProfileServiceTest.class);

    public static final String PROFILE_DESCRIPTION = "Ordinary profile";
    private static CollectionImeji c;
	private static MetadataProfile p;

    private static final String DEFAULT_PROFILE_JSON_PATH = "src/main/webapp/WEB-INF/default-metadata-profile.json";
	private static final String typeText = "http://imeji.org/terms/metadata#text";
	private static final String typeNumber = "http://imeji.org/terms/metadata#number";
	private static final String textLabel = "profile text";
	private static final String numberLabel = "profile number";
	private static final String textLanguage = "en";
	private static final String numberLanguage = "de";

    @BeforeClass
	public static void setup() throws Exception {
		JenaUtil.initJena();
		initProfile();
	}

	@AfterClass
	public static void tearDown() throws Exception {

		JenaUtil.closeJena();
	}

	public static void initProfile() throws Exception {
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
        p.setDefault(false);
        p.setDescription(PROFILE_DESCRIPTION);

		c.setProfile(p.getId());

		pController.create(p, testUser);
		cController.createNoValidate(c, p, testUser);
	}

	@Test
	public void testProfileCRUD_TO()  {
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
			profile = pCrud.read(p.getIdString(), testUser);
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

    @Test
	public void testProfile_VO() throws ImejiException {

        ProfileController pc = new ProfileController();

        final MetadataProfile profile = pc.retrieve(p.getId(), testUser);

        assertThat(profile.getDefault(), equalTo(false));
        assertThat(profile.getDescription(), equalTo(PROFILE_DESCRIPTION));

	}

    @Test
	public void testDefaultProfile() throws ImejiException, IOException {

        ProfileController pc = new ProfileController();

        MetadataProfileTO pFromJSON = (MetadataProfileTO) buildTOFromJSON(getStringFromPath(DEFAULT_PROFILE_JSON_PATH), MetadataProfileTO.class);

        MetadataProfile pFromController = pc.retrieveDefaultProfile();

        assertThat(pFromController.getDefault(), equalTo(true));
        assertThat(pFromController.getId(), equalTo(Imeji.defaultMetadataProfile.getId()));
        assertThat(pFromController.getDescription(), equalTo(pFromJSON.getDescription()));
        assertThat(pFromController.getStatements(), hasSize(pFromJSON.getStatements().size()));

        ProfileService ps = new ProfileService();
        MetadataProfileTO pFromService = ps.read(ObjectHelper.getId(Imeji.defaultMetadataProfile.getId()), adminUser);

        assertThat(pFromService.getDefault(), equalTo(true));
        assertThat(pFromService.getId(), equalTo(ObjectHelper.getId(Imeji.defaultMetadataProfile.getId())));
        assertThat(pFromService.getDescription(), equalTo(pFromJSON.getDescription()));
        assertThat(pFromService.getStatements(), hasSize(pFromJSON.getStatements().size()));

    }

    @Test
	public void testProfile_TO() throws ImejiException {


        ProfileService pCrud = new ProfileService();
        MetadataProfileTO pTO = new MetadataProfileTO();

        // read profile with login
        try {
            pTO = pCrud.read(p.getIdString(), testUser);
        } catch (Exception e) {
            fail("could not read Profile");
        }

        assertThat(pTO.getDefault(), equalTo(false));
        assertThat(pTO.getDescription(), equalTo(PROFILE_DESCRIPTION));

	}

}

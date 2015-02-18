package de.mpg.imeji.rest.process;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.logic.vo.predefinedMetadata.*;
import de.mpg.imeji.logic.vo.predefinedMetadata.Number;
import de.mpg.imeji.rest.to.*;
import de.mpg.imeji.rest.to.predefinedMetadataTO.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ReverseTransferObjectFactory {


	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReverseTransferObjectFactory.class);



    public enum TRANSFER_MODE {CREATE, UPDATE};

	public static void transferCollection(CollectionTO to, CollectionImeji vo, TRANSFER_MODE mode) {
		ContainerMetadata metadata = new ContainerMetadata();
		metadata.setTitle(to.getTitle());
		metadata.setDescription(to.getDescription());

		// set contributors
		transferCollectionContributors(to.getContributors(), metadata, mode);
		vo.setMetadata(metadata);

		// set Metadata
		CollectionProfileTO profileTO = to.getProfile();

		// TODO: change the code after extension of CollectionImeji mdprofile
		// container
		if (null == profileTO || profileTO.getProfileId() == null) {
			// profile = ImejiFactory.newProfile();
			vo.setProfile(URI.create("default___copy"));
			// reference profile to existed one
		} else {
			vo.setProfile(URI.create(profileTO.getProfileId() + "___"
					+ profileTO.getMethod()));
		}

	}

	public static void transferItem(ItemTO to, Item vo, User u, TRANSFER_MODE mode) throws ImejiException  {

		// only fields which can be transferred for TO to VO!!!
		if (mode == TRANSFER_MODE.CREATE) {
			if (!isNullOrEmpty(to.getId()))
                vo.setId(ObjectHelper.getURI(Item.class, to.getId()));

			if (!isNullOrEmpty(to.getCollectionId()))
				     vo.setCollection(ObjectHelper.getURI(CollectionImeji.class,
		                        to.getCollectionId()));
		}

		if (!isNullOrEmpty(to.getFilename()))
			vo.setFilename(to.getFilename());

		transferItemMetadata(to, vo, u, mode);
	}

	public static void transferItemMetadata(ItemTO to, Item vo, User u, TRANSFER_MODE mode) throws ImejiException  {


		Collection<Metadata> voMDs = vo.getMetadataSet().getMetadata();
		//Collection<Metadata> copyOfvoMDs =  ImmutableList.copyOf(voMDs);
		voMDs.clear();

		MetadataProfile mp = getMetadataProfile(vo.getCollection(), u);

        validateMetadata(to, mp);

        for (Statement st : mp.getStatements()) {
			final URI stURI = st.getId();

			MetadataSetTO md = lookUpMetadata(to, st.getType(), stURI);
			//Metadata mdVOPrev = lookUpMetadata(copyOfvoMDs, stURI, st.getType());
			if (md != null) {
				switch (st.getType().toString()) {
					case "http://imeji.org/terms/metadata#text":
						TextTO textTO = (TextTO) md.getValue();
						if (!isNullOrEmpty(textTO.getText())) {
							Text mdVO = new Text();
							mdVO.setStatement(stURI);
							mdVO.setText(textTO.getText());
							voMDs.add(mdVO);
						}
						break;
					case "http://imeji.org/terms/metadata#geolocation":
						GeolocationTO geoTO = (GeolocationTO) md.getValue();
						if (geoTO != null) {
							Geolocation mdVO = new Geolocation();
							mdVO.setStatement(stURI);
							mdVO.setName(geoTO.getName());
							mdVO.setLatitude(geoTO.getLatitude());
							mdVO.setLongitude(geoTO.getLongitude());
							voMDs.add(mdVO);
						}
						break;
					case "http://imeji.org/terms/metadata#number":
						NumberTO numberTO = (NumberTO) md.getValue();
						if (numberTO != null) {
							Number mdVO = new Number();
							mdVO.setStatement(stURI);
							mdVO.setNumber(numberTO.getNumber());
							voMDs.add(mdVO);
						}
						break;
					case "http://imeji.org/terms/metadata#conePerson":
						ConePersonTO personTO = (ConePersonTO) md.getValue();
						if (personTO != null) {
							ConePerson mdVO = new ConePerson();
							mdVO.setStatement(stURI);
							mdVO.setPerson(new Person());
							transferPerson(personTO.getPerson(), mdVO.getPerson(), mode);
							voMDs.add(mdVO);
						}
						break;
					case "http://imeji.org/terms/metadata#date":
						DateTO dateTO = (DateTO) md.getValue();
						if (!isNullOrEmpty(dateTO.getDate())) {
							de.mpg.imeji.logic.vo.predefinedMetadata.Date mdVO = new de.mpg.imeji.logic.vo.predefinedMetadata.Date();
							mdVO.setStatement(stURI);
							mdVO.setDate(dateTO.getDate());
							voMDs.add(mdVO);
						}
						break;
					case "http://imeji.org/terms/metadata#license":
						LicenseTO licenseTO = (LicenseTO) md.getValue();
						final String lic = licenseTO.getLicense();
						final String url = licenseTO.getUrl();
						if (!isNullOrEmpty(lic) || !isNullOrEmpty(url)) {
							License mdVO = new License();
							mdVO.setStatement(stURI);
							//set license to uri if empty
							mdVO.setLicense(isNullOrEmpty(lic) ? url : lic);
							if (!isNullOrEmpty(url))
								mdVO.setExternalUri(URI.create(url));
							voMDs.add(mdVO);
						}
						break;
					case "http://imeji.org/terms/metadata#publication":
						PublicationTO pubTO = (PublicationTO) md.getValue();
						if (!isNullOrEmpty(pubTO.getPublication())) {
							Publication mdVO = new Publication();
							mdVO.setStatement(stURI);
							mdVO.setUri(URI.create(pubTO.getPublication()));
							mdVO.setExportFormat(pubTO.getFormat());
							mdVO.setCitation(pubTO.getCitation());
							voMDs.add(mdVO);
						}
						break;
					case "http://imeji.org/terms/metadata#link":
						LinkTO linkTO = (LinkTO) md.getValue();
						if (!isNullOrEmpty(linkTO.getUrl())) {
							Link mdVO = new Link();
							mdVO.setStatement(stURI);
							mdVO.setLabel(linkTO.getLink());
							mdVO.setUri(URI.create(linkTO.getUrl()));
							voMDs.add(mdVO);
						}
							break;
				}
			} else {
				final String message = "Statement { type: \"" + st.getType() +
						"\", id: \"" + stURI +
						"\" } has not been found for item id: \"" + vo.getId() + "\"";
				//throw new RuntimeException(message);
                LOGGER.debug(message);
			}
		}

	}

    public static void transferMetadataProfile(MetadataProfileTO to, MetadataProfile vo, TRANSFER_MODE mode) {
        if (mode == TRANSFER_MODE.CREATE) {
            vo.setTitle(to.getTitle());
            vo.setDescription(to.getDescription());
            vo.setDefault(to.getDefault());
            for (StatementTO stTO: to.getStatements()) {
                Statement stVO = new Statement();
                stVO.setType(stTO.getType());
                stVO.setLabels(stTO.getLabels());
                stVO.setVocabulary(stTO.getVocabulary());
                for (LiteralConstraintTO lc: stTO.getLiteralConstraints()) {
                    stVO.getLiteralConstraints().add(lc.getValue());
                }
                stVO.setMinOccurs(stTO.getMinOccurs());
                stVO.setMaxOccurs(stTO.getMaxOccurs());
                //TODO: check namespace
                //stVO.setNamespace(???);
                if (!isNullOrEmpty(stTO.getParentStatementId()))
                    stVO.setParent(URI.create(stTO.getParentStatementId()));
                vo.getStatements().add(stVO);
            }
        }

    }


    /**
     * Check all item metadata statement/types:
     * they should be presented in the MetadataProfile statements
     *
     * @param to
     * @param mp
     * @throws de.mpg.imeji.exceptions.BadRequestException
     */
    private static void validateMetadata(ItemTO to, MetadataProfile mp) throws BadRequestException {
        for (MetadataSetTO md: to.getMetadata()) {
            if (lookUpStatement(mp.getStatements(), md.getTypeUri(), md.getStatementUri()) == null)
                throw new BadRequestException("Cannot find { typeUri: " + md.getTypeUri()
                        + " , statementUri: " + md.getStatementUri() +
                        "} in profile: " + mp.getId());

        }
    }

    private static MetadataProfile getMetadataProfile(URI collectionURI, User u) throws ImejiException  {
		ProfileController pc = new ProfileController();
		return pc.retrieveByCollectionId(collectionURI, u);
	}


	private static Statement lookUpStatement(Collection<Statement> statements, URI type, URI statementUri) {
		for (Statement st: statements)
			if (st.getType().equals(type) && st.getId().equals(statementUri))
				return st;
		return null;
	}

	private static MetadataSetTO lookUpMetadata(ItemTO to, URI type, URI statement) {
		for (MetadataSetTO md: to.getMetadata())
			if (md.getTypeUri().equals(type) && md.getStatementUri().equals(statement))
				return md;
		return null;
	}


	public static void transferPerson(PersonTO pto, Person p, TRANSFER_MODE mode) {

		if (mode == TRANSFER_MODE.CREATE) {
			//p.setPos(pto.getPosition());
			IdentifierTO ito = new IdentifierTO();
			ito.setValue(pto.getIdentifiers().get(0).getValue());
			p.setIdentifier(ito.getValue());
		}
		p.setRole(URI.create(pto.getRole()));
		p.setFamilyName(pto.getFamilyName());
		p.setGivenName(pto.getGivenName());
		p.setCompleteName(pto.getCompleteName());
		p.setAlternativeName(pto.getAlternativeName());

		// set oganizations
		transferContributorOrganizations(pto.getOrganizations(), p, mode);

	}

	public static void transferCollectionContributors(List<PersonTO> persons,
		ContainerMetadata metadata, TRANSFER_MODE mode) {
		for (PersonTO pTO : persons) {
			Person person = new Person();
			person.setFamilyName(pTO.getFamilyName());
			person.setGivenName(pTO.getGivenName());
			person.setCompleteName(pTO.getCompleteName());
			person.setAlternativeName(pTO.getAlternativeName());
			// person.setRole(pto.getRole());
			//person.setPos(pTO.getPosition());

			if (pTO.getIdentifiers().size() == 1 ) {
			// set the identifier of current person
				IdentifierTO ito = new IdentifierTO();
				ito.setValue(pTO.getIdentifiers().get(0).getValue());
				person.setIdentifier(ito.getValue());
			}
			else if (pTO.getIdentifiers().size() > 1) {
				System.out.println("I have more identifiers than needed for Person");
			}

			// set organizations
			transferContributorOrganizations(pTO.getOrganizations(), person, mode);
			metadata.getPersons().add(person);
		}

	}

	public static void transferContributorOrganizations(
			List<OrganizationTO> orgs, Person person, TRANSFER_MODE mode) {
		for (OrganizationTO orgTO : orgs) {
			Organization org = new Organization();

			if (mode == TRANSFER_MODE.CREATE && orgTO.getIdentifiers().size() == 1) {
				//org.setPos(orgTO.getPosition());
				IdentifierTO ito = new IdentifierTO();
				ito.setValue(orgTO.getIdentifiers().get(0).getValue());
				org.setIdentifier(ito.getValue());
			}
			else if (orgTO.getIdentifiers().size() > 1) {
				System.out.println("Have more organization identifiers than needed");
			}

			org.setName(orgTO.getName());
			org.setDescription(orgTO.getDescription());
			org.setCity(orgTO.getCity());
			org.setCountry(orgTO.getCountry());

			// set the identifier of current organization

			person.getOrganizations().add(org);
		}

	}

}

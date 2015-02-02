package de.mpg.imeji.rest.process;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.ContainerMetadata;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson;
import de.mpg.imeji.logic.vo.predefinedMetadata.Geolocation;
import de.mpg.imeji.logic.vo.predefinedMetadata.License;
import de.mpg.imeji.logic.vo.predefinedMetadata.Link;
import de.mpg.imeji.logic.vo.predefinedMetadata.Number;
import de.mpg.imeji.logic.vo.predefinedMetadata.Publication;
import de.mpg.imeji.logic.vo.predefinedMetadata.Text;
import de.mpg.imeji.rest.to.CollectionProfileTO;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.IdentifierTO;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.MetadataSetTO;
import de.mpg.imeji.rest.to.OrganizationTO;
import de.mpg.imeji.rest.to.PersonTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.ConePersonTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.DateTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.GeolocationTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.LicenseTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.LinkTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.NumberTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.PublicationTO;
import de.mpg.imeji.rest.to.predefinedMetadataTO.TextTO;

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

		transferItemMetaData(to, vo, u, mode);
	}

	public static void transferItemMetaData(ItemTO to, Item vo, User u, TRANSFER_MODE mode) throws ImejiException  {


		Collection<Metadata> voMDs = vo.getMetadataSet().getMetadata();
		//Collection<Metadata> copyOfvoMDs =  ImmutableList.copyOf(voMDs);
		voMDs.clear();

		MetadataProfile mp = getMetadataProfile(vo.getCollection(), u);
		for (Statement st : mp.getStatements()) {
			final URI stURI = st.getId();
			MetadataSetTO md = findMetadata(to, stURI, st.getType());
			//Metadata mdVOPrev = findMetadata(copyOfvoMDs, stURI, st.getType());
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
						if (!isNullOrEmpty(pubTO.getPublication()) || !isNullOrEmpty(pubTO.getCitation())) {
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
				//TODO: Correct exception handling
				//
				final String message = "Statement { type: \"" + st.getType() +
						"\", id: \"" + stURI +
						"\" } has not been found for item id: \"" + vo.getId() + "\"";
				//throw new RuntimeException(message);
				LOGGER.info(message);
			}
		}

	}

	private static MetadataProfile getMetadataProfile(URI collectionURI, User u) throws ImejiException  {
		ProfileController pc = new ProfileController();
		return pc.retrieveByCollectionId(collectionURI, u);
	}


	private static Metadata findMetadata(Collection<Metadata> mdColl, URI statement, URI type) {
		for (Metadata md: mdColl)
			if (md.getTypeNamespace().equals(type) && md.getStatement().equals(statement))
				return md;
		return null;
	}

	private static MetadataSetTO findMetadata(ItemTO to, URI statement, URI type) {
		for (MetadataSetTO md: to.getMetadata())
			if (md.getTypeUri().equals(type) && md.getStatementUri().equals(statement))
				return md;
		return null;
	}

	private static Metadata lookUpMetadata(MetadataSetTO mdTO, ImmutableList<Metadata> mdList, ItemTO to, User u) {
		URI typeUri =  mdTO.getTypeUri();
		URI statementUri =  mdTO.getStatementUri();
		//check VO firstly
		for (Metadata md: mdList) {
			if (md.getStatement().equals(statementUri) && md.getId().equals(typeUri))
				return md;
		}
		//if not found in VO, lookup in profile
		CollectionController cc = new CollectionController();
		ProfileController pc = new ProfileController();
		CollectionImeji c = null;
		MetadataProfile p = null;
		try {
			c = cc.retrieve(URI.create(to.getCollectionId()), u);
			p = pc.retrieve(c.getProfile(), u);
		} catch (Exception e) {
			//TODO: Correct exception handling
			throw new RuntimeException("Cannot retrieve metadata profile" + e.getLocalizedMessage());
		}

		for (Statement st: p.getStatements()) {
			if (st.getId().equals(statementUri) && st.getType().equals(typeUri)) {
				Metadata md = null;
				try {
					md = Metadata.createNewInstance(typeUri);
					md.setId(typeUri);
					md.setStatement(statementUri);
					return md;
				} catch (Exception e) {
					//TODO: Correct exception handling
					throw new RuntimeException("Cannot instantiate metadata class: " + e.getLocalizedMessage());
				}
			}

		}

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

			// set the identifier of current person
			IdentifierTO ito = new IdentifierTO();
			ito.setValue(pTO.getIdentifiers().get(0).getValue());
			person.setIdentifier(ito.getValue());

			// set organizations
			transferContributorOrganizations(pTO.getOrganizations(), person, mode);
			metadata.getPersons().add(person);
		}

	}

	public static void transferContributorOrganizations(
			List<OrganizationTO> orgs, Person person, TRANSFER_MODE mode) {
		for (OrganizationTO orgTO : orgs) {
			Organization org = new Organization();

			if (mode == TRANSFER_MODE.CREATE) {
				//org.setPos(orgTO.getPosition());
				IdentifierTO ito = new IdentifierTO();
				ito.setValue(orgTO.getIdentifiers().get(0).getValue());
				org.setIdentifier(ito.getValue());
			}

			org.setName(orgTO.getName());
			org.setDescription(orgTO.getDescription());
			org.setCity(orgTO.getCity());
			org.setCountry(orgTO.getCountry());

			// set the identifier of current organization

			person.getOrganizations().add(org);
		}

	}

	public static String formatDate(Date d) {
		String output = "";
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		output = f.format(d);
		f = new SimpleDateFormat("HH:mm:SS Z");
		output += "T" + f.format(d);
		return output;
	}

}

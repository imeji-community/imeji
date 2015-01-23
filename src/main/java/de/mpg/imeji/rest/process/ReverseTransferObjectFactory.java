package de.mpg.imeji.rest.process;

import de.mpg.imeji.logic.auth.exception.UnprocessableError;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.logic.vo.predefinedMetadata.*;
import de.mpg.imeji.logic.vo.predefinedMetadata.Number;
import de.mpg.imeji.rest.to.*;
import de.mpg.imeji.rest.to.predefinedMetadataTO.*;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ReverseTransferObjectFactory {

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

	public static void transferItem(ItemTO to, Item vo, TRANSFER_MODE mode) {

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

		transferItemMetaData(to.getMetadata(), vo, mode);
	}

	public static void transferItemMetaData(List<MetadataSetTO> toMds, Item vo, TRANSFER_MODE mode) {
		vo.getMetadataSet().getMetadata().clear();
		for (MetadataSetTO mdTO : toMds) {
			Metadata md = null;
			String typeUri = mdTO.getTypeUri().toString();
			switch (typeUri) {
			case "http://imeji.org/terms/metadata#text":
				TextTO text = (TextTO) mdTO.getValue();
				if (!isNullOrEmpty(text.getText())) {
					Text mdText = new Text();
					mdText.setText(text.getText());
					md = mdText;
				}
				break;
			case "http://imeji.org/terms/metadata#geolocation":
				GeolocationTO geo = (GeolocationTO) mdTO.getValue();
				if (geo != null) {
					Geolocation mdGeo = new Geolocation();
					mdGeo.setName(geo.getName());
					mdGeo.setLatitude(geo.getLatitude());
					mdGeo.setLongitude(geo.getLongitude());
					md = mdGeo;
				}
				break;
			case "http://imeji.org/terms/metadata#number":
				NumberTO num = (NumberTO) mdTO.getValue();
				if (num != null) {
					Number 	mdNum = new Number();
					mdNum.setNumber(num.getNumber());
					md = mdNum;
				}
				break;
			case "http://imeji.org/terms/metadata#conePerson":
				ConePersonTO p = (ConePersonTO) mdTO.getValue();
				if (p != null) {
					ConePerson mdP = new ConePerson();
					Person person = new Person();
					mdP.setPerson(person);
					transferPerson(p.getPerson(), mdP.getPerson(), mode);
					md = mdP;
				}
				break;
			case "http://imeji.org/terms/metadata#date":
				DateTO date = (DateTO) mdTO.getValue();
				if (!isNullOrEmpty(date.getDate())) {
					de.mpg.imeji.logic.vo.predefinedMetadata.Date mdDate = new de.mpg.imeji.logic.vo.predefinedMetadata.Date();
					mdDate.setDate(date.getDate());
					md = mdDate;
				}
				break;
			case "http://imeji.org/terms/metadata#license":
				LicenseTO license = (LicenseTO) mdTO.getValue();
				if (!isNullOrEmpty(license.getLicense())) {
					License mdLic = new License();
					mdLic.setLicense(license.getLicense());
					mdLic.setExternalUri(URI.create(license.getUrl()));
					md = mdLic;
				}
				break;
			case "http://imeji.org/terms/metadata#publication":
				PublicationTO pub = (PublicationTO) mdTO.getValue();
				if (!isNullOrEmpty(pub.getPublication()) || !isNullOrEmpty(pub.getCitation()) ) {
					Publication mdPub = new Publication();
					mdPub.setUri(URI.create(pub.getPublication()));
					mdPub.setExportFormat(pub.getFormat());
					mdPub.setCitation(pub.getCitation());
					md = mdPub;
				}
				break;
			case "http://imeji.org/terms/metadata#link":
				LinkTO link = (LinkTO) mdTO.getValue();
				if (!isNullOrEmpty(link.getUrl()) ) {
					Link mdLink = new Link();
					mdLink.setLabel(link.getLink());
					mdLink.setUri(URI.create(link.getUrl()));
					md = mdLink;
				}
				break;
			}
			if (md != null) {
				md.setStatement(mdTO.getStatementUri());
				vo.getMetadataSet().getMetadata().add(md);
			}
		}

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

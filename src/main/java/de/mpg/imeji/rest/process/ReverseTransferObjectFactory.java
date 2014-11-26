package de.mpg.imeji.rest.process;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.ContainerMetadata;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataSet;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson;
import de.mpg.imeji.logic.vo.predefinedMetadata.Geolocation;
import de.mpg.imeji.logic.vo.predefinedMetadata.License;
import de.mpg.imeji.logic.vo.predefinedMetadata.Link;
import de.mpg.imeji.logic.vo.predefinedMetadata.Number;
import de.mpg.imeji.logic.vo.predefinedMetadata.Publication;
import de.mpg.imeji.logic.vo.predefinedMetadata.Text;
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
	



	public static void transferCollection(CollectionTO to, CollectionImeji vo) {
		ContainerMetadata metadata = new ContainerMetadata();
		metadata.setTitle(to.getTitle());
		metadata.setDescription(to.getDescription());
		
		//set contributors
		transferCollectionContributors(to.getContributors(), metadata);
		vo.setMetadata(metadata);
	}
	
	public static void transferItem(ItemTO to, Item vo){
		vo.setCollection(ObjectHelper.getURI(CollectionImeji.class, to.getCollectionId()));
		vo.setFilename(to.getFilename());
		
		transferItemMetaData(to.getMetadata(), vo);
		
	}
	
	public static void transferItemMetaData(List<MetadataSetTO> toMds, Item vo){
		MetadataSet voMds = new MetadataSet();
	
		for(MetadataSetTO mdTO : toMds){
			Metadata md = null;
			md.setPos(mdTO.getPosition());
			md.setStatement(mdTO.getStatementUri());
			String typeUri = mdTO.getTypeUri().toString();
			switch(typeUri){
			case "http://imeji.org/terms/metadata#text":
				Text mdText = new Text();
				TextTO text = (TextTO) mdTO.getValue();
				mdText.setText(text.getText());
				md = mdText;
				break;
			case "http://imeji.org/terms/metadata#geolocation":
				Geolocation mdGeo = new Geolocation();
				GeolocationTO geo = (GeolocationTO) mdTO.getValue();
				mdGeo.setName(geo.getName());
				mdGeo.setLatitude(geo.getLatitude());
				mdGeo.setLongitude(geo.getLongitude());
				md=mdGeo;
				break;
			case "http://imeji.org/terms/metadata#number":
				Number mdNum = new Number();
				NumberTO num = (NumberTO) mdTO.getValue();
				mdNum.setNumber(num.getNumber());
				md=mdNum;
				break;
			case "http://imeji.org/terms/metadata#conePerson":
				ConePerson mdP = new ConePerson();
				ConePersonTO p = (ConePersonTO) mdTO.getValue();
				Person person = new Person();
				mdP.setPerson(person);
				transferPerson(p.getPerson(), mdP.getPerson());
				md=mdP;
				break;
			case "http://imeji.org/terms/metadata#date":
				de.mpg.imeji.logic.vo.predefinedMetadata.Date mdDate = new de.mpg.imeji.logic.vo.predefinedMetadata.Date();
				DateTO date = (DateTO) mdTO.getValue();
				mdDate.setDate(date.getDate());
				md = mdDate;
				
				break;
			case "http://imeji.org/terms/metadata#license":
				License mdLic = new License();
				LicenseTO license = (LicenseTO) mdTO.getValue();
				mdLic.setLicense(license.getLicense());
				mdLic.setExternalUri(URI.create(license.getUrl()));
				md=mdLic;
				break;
			case "http://imeji.org/terms/metadata#publication":
				Publication mdPub = new Publication();
				PublicationTO pub = (PublicationTO) mdTO.getValue();
				mdPub.setUri(URI.create(pub.getPublication()));
				mdPub.setExportFormat(pub.getFormat());
				mdPub.setCitation(pub.getCitation());
				md= mdPub;
				break;
			case "http://imeji.org/terms/metadata#link":
				Link mdLink = new Link();
				LinkTO link = (LinkTO) mdTO.getValue();
				mdLink.setLabel(link.getLink());
				mdLink.setUri(URI.create(link.getUrl()));
				md = mdLink;
				break;
			}
			voMds.getMetadata().add(md);
			vo.getMetadataSets().add(voMds);
		}
			
	}
	
	public static void transferPerson(PersonTO pto, Person p){  

		p.setPos(pto.getPosition());
		p.setFamilyName(pto.getFamilyName());
		p.setGivenName(pto.getGivenName());
		p.setCompleteName(pto.getCompleteName());
		p.setAlternativeName(pto.getAlternativeName());
		
		IdentifierTO ito = new IdentifierTO();
		ito.setValue(pto.getIdentifiers().get(0).getValue());
		p.setIdentifier(ito.getValue());
		
		//set oganizations
		transferContributorOrganizations(pto.getOrganizations(), p);			
	
		}
	

	
	public static void transferCollectionContributors(List<PersonTO> persons, ContainerMetadata metadata){
		for(PersonTO pTO : persons){
			Person person = new Person();
			person.setFamilyName(pTO.getFamilyName());
			person.setGivenName(pTO.getGivenName());
			person.setCompleteName(pTO.getCompleteName());
			person.setAlternativeName(pTO.getAlternativeName());
			//person.setRole(pto.getRole());
			person.setPos(pTO.getPosition());
			
			//set the identifier of current person 
			IdentifierTO ito = new IdentifierTO();
			ito.setValue(pTO.getIdentifiers().get(0).getValue());
			person.setIdentifier(ito.getValue());
			
			//set organizations
			transferContributorOrganizations(pTO.getOrganizations(), person);			
			metadata.getPersons().add(person);
		}
		
	}

	public static void transferContributorOrganizations(List<OrganizationTO> orgs, Person person){
		for(OrganizationTO orgTO : orgs){
			Organization org = new Organization();
			org.setPos(orgTO.getPosition());
			org.setName(orgTO.getName());
			org.setDescription(orgTO.getDescription());
			
			//set the identifier of current organization 
			IdentifierTO ito = new IdentifierTO();
			ito.setValue(orgTO.getIdentifiers().get(0).getValue());
			org.setIdentifier(ito.getValue());
			
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

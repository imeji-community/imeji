package de.mpg.imeji.rest.process;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.Properties;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.logic.vo.predefinedMetadata.Geolocation;
import de.mpg.imeji.logic.vo.predefinedMetadata.Text;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.api.UserService;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.GeolocationTO;
import de.mpg.imeji.rest.to.IdentifierTO;
import de.mpg.imeji.rest.to.ItemTO;
import de.mpg.imeji.rest.to.LabelTO;
import de.mpg.imeji.rest.to.MetadataSetTO;
import de.mpg.imeji.rest.to.NumberTO;
import de.mpg.imeji.rest.to.OrganizationTO;
import de.mpg.imeji.rest.to.PersonTO;
import de.mpg.imeji.rest.to.PersonTOBasic;
import de.mpg.imeji.rest.to.PropertiesTO;
import de.mpg.imeji.rest.to.TextTO;
import de.mpg.j2j.misc.LocalizedString;

public class TransferObjectFactory {
	
	
	public static void transferCollection(CollectionImeji vo, CollectionTO to) {
		transferProperties(vo, to);
		//set visibility
//		to.setVisibility(vo.getVisibility().toString());

		to.setProfileId(extractIDFromURI(vo.getProfile()));
		to.setTitle(vo.getMetadata().getTitle());
		to.setDescription(vo.getMetadata().getDescription());
		
		transferCollectionContributors(vo.getMetadata().getPersons(), to);


	}
	 
	public static void transferCollectionContributors(Collection<Person> persons, CollectionTO to ){
		for(Person p : persons){
			PersonTO pto = new PersonTO();
			pto.setPosition(p.getPos());
			pto.setId(extractIDFromURI(p.getId()));
			pto.setFamilyName(p.getFamilyName());
			pto.setGivenName(p.getGivenName());
			pto.setCompleteName(p.getCompleteName());
			pto.setAlternativeName(p.getAlternativeName());

			IdentifierTO ito = new IdentifierTO();
			ito.setValue(p.getIdentifier());
			pto.getIdentifiers().add(ito);
			
			//set oganizations
			transferContributorOrganizations(p.getOrganizations(), pto);			
			
			to.getContributors().add(pto);
		}
		
	}
	
	public static void transferContributorOrganizations(Collection<Organization> orgas, PersonTO pto){
		for(Organization orga : orgas){
			OrganizationTO oto = new OrganizationTO();
			oto.setPosition(orga.getPos());
			oto.setId(extractIDFromURI(orga.getId()));
			oto.setName(orga.getName());
			oto.setDescription(orga.getDescription());
			IdentifierTO ito = new IdentifierTO();
			ito.setValue(orga.getIdentifier());
			oto.getIdentifiers().add(ito);			
			pto.getOriganizations().add(oto);			
		}
		
	}
	
	public static void transferProperties(Properties vo, PropertiesTO to){
		//set ID
		to.setId(vo.getIdString());
		//set createdBy
		UserService ucrud = new UserService();
		User u = new User();
		try {
			u = ucrud.read(vo.getCreatedBy());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		to.setCreatedBy(new PersonTOBasic(u.getPerson().getFamilyName(), extractIDFromURI(u.getPerson().getId())));
		//set modifiedBy
		try {
			u = ucrud.read(vo.getModifiedBy());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		to.setModifiedBy(new PersonTOBasic(u.getPerson().getFamilyName(), extractIDFromURI(u.getPerson().getId())));
		//set createdDate, modifiedDate, versionDate
		to.setCreatedDate(formatDate(vo.getCreated().getTime()));
		to.setModifiedDate(formatDate(vo.getModified().getTime()));
		to.setVersionDate(formatDate(vo.getVersionDate().getTime()));
		//set status
		to.setStatus(vo.getStatus().toString());
		//set version
		to.setVersion(vo.getVersion());
		//set discardComment
		to.setDiscardComment(vo.getDiscardComment());
		
		
	}
	

	public static void transferItem(Item vo, ItemTO to) {
		transferProperties(vo, to);
		//set visibility
		to.setVisibility(vo.getVisibility().toString());
		//set collectionID
		to.setCollectionId(extractIDFromURI(vo.getCollection()));
		to.setFilename(vo.getFilename());
		to.setMimetype(vo.getFiletype());
		to.setChecksumMd5(vo.getChecksum());
		to.setWebResolutionUrlUrl(vo.getWebImageUrl());
		to.setThumbnailUrl(vo.getThumbnailImageUrl());
		to.setFileUrl(vo.getFullImageUrl());

		//set Metadata
		ProfileService pcrud = new ProfileService();
		MetadataProfile profile = new MetadataProfile();
		try {
			profile = pcrud.read(vo.getMetadataSet().getProfile());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tranferItemMetadata(profile, vo.getMetadataSet().getMetadata(), to);

	}

	public static void tranferItemMetadata(MetadataProfile profile, Collection<Metadata> voMds, ItemTO to) {

		for (Metadata md : voMds) {
			md.getId();
			MetadataSetTO mdTO = new MetadataSetTO();
			mdTO.setPosition(md.getPos());
			mdTO.setStatementUri(md.getStatement());
			mdTO.setTypeUri(URI.create(md.getTypeNamespace()));

			List<LabelTO> ltos = new ArrayList<LabelTO>();
			for (Statement s : profile.getStatements()) {
				if (s.getId().toString().equals(md.getStatement().toString())) {
					for (LocalizedString ls : s.getLabels()) {
						LabelTO lto = new LabelTO(ls.getLang(), ls.getValue());
						ltos.add(lto);
					}

				}
			}
			mdTO.setLabels(ltos);

			// if(md.getClass().isInstance(Text.class))
			// {
			// Text mdText = (Text) md;
			//
			//
			// }
			switch (md.getClass().getName()) {
			case "de.mpg.imeji.logic.vo.predefinedMetadata.Text":
				Text mdText = (Text) md;
				TextTO tt = new TextTO();
				tt.setName(mdText.getText());
				mdTO.setValue(tt);
				break;
			case "de.mpg.imeji.logic.vo.predefinedMetadata.Number":
				de.mpg.imeji.logic.vo.predefinedMetadata.Number mdNumber = (de.mpg.imeji.logic.vo.predefinedMetadata.Number) md;
				NumberTO nt = new NumberTO();
				nt.setNumber(mdNumber.getNumber());
				mdTO.setValue(nt);
				break;
			case "de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson":
				break;
			case "de.mpg.imeji.logic.vo.predefinedMetadata.Date":
				break;
			case "de.mpg.imeji.logic.vo.predefinedMetadata.Geolocation":
				Geolocation mdGeo = (Geolocation) md;
				GeolocationTO gto = new GeolocationTO();
				gto.setName(mdGeo.getName());
				gto.setLongitude(mdGeo.getLongitude());
				gto.setLatitude(mdGeo.getLatitude());
				mdTO.setValue(gto);
				break;
			case "de.mpg.imeji.logic.vo.predefinedMetadata.License":
				break;
			case "de.mpg.imeji.logic.vo.predefinedMetadata.Link":
				break;
			case "de.mpg.imeji.logic.vo.predefinedMetadata.Publication":
				break;
			}

			to.getMetadata().add(mdTO);
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

	public static String extractIDFromURI(URI uri) {
		return uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
	}



}

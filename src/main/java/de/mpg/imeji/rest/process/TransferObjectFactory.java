package de.mpg.imeji.rest.process;

import com.fasterxml.jackson.databind.JsonNode;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.logic.vo.Properties;
import de.mpg.imeji.logic.vo.predefinedMetadata.*;
import de.mpg.imeji.logic.vo.predefinedMetadata.Number;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.api.UserService;
import de.mpg.imeji.rest.defaultTO.DefaultItemTO;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.*;
import de.mpg.imeji.rest.to.*;
import de.mpg.imeji.rest.to.predefinedMetadataTO.*;
import de.mpg.j2j.misc.LocalizedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;

public class TransferObjectFactory {


	private static final Logger LOGGER = LoggerFactory.getLogger(TransferObjectFactory.class);

	/**
	 * Transfer a {@link DefaultItemTO} into an {@link ItemTO} according to
	 * {@link MetadataProfileTO}
	 * 
	 * @param profileTO
	 * @param easyTO
	 * @param itemTO
	 * @throws BadRequestException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 */

	/**
	 * Transfer a {@link MetadataProfile} into a {@link MetadataProfileTO}
	 * 
	 * @param vo
	 * @param to
	 */
	public static void transferMetadataProfile(MetadataProfile vo, MetadataProfileTO to) {
		transferProperties(vo, to);
		to.setTitle(vo.getTitle());
		to.setDefault(vo.getDefault());
		to.setDescription(vo.getDescription());
		transferStatements(vo.getStatements(), to);
	}

	public static void transferStatements(Collection<Statement> stats,
			MetadataProfileTO to) {
		for (Statement t : stats) {
			StatementTO sto = new StatementTO();
			sto.setId(CommonUtils.extractIDFromURI(t.getId()));
			sto.setPos(t.getPos());
			sto.setType(t.getType());
			sto.setLabels(new ArrayList<LocalizedString>(t.getLabels()));
			sto.setVocabulary(t.getVocabulary());
			for (String s : t.getLiteralConstraints()) {
				LiteralConstraintTO lcto = new LiteralConstraintTO();
				lcto.setValue(s);
				sto.getLiteralConstraints().add(lcto);
			}
			sto.setMinOccurs(t.getMinOccurs());
			sto.setMaxOccurs(t.getMaxOccurs());
			if (t.getParent() != null)
				sto.setParentStatementId(CommonUtils.extractIDFromURI(t
						.getParent()));
			sto.setUseInPreview(t.isPreview());
			to.getStatements().add(sto);
		}

	}

	public static void transferCollection(CollectionImeji vo, CollectionTO to) {
		transferProperties(vo, to);

		// TODO: Container
		to.setTitle(vo.getMetadata().getTitle());
		to.setDescription(vo.getMetadata().getDescription());

		// TODO: versionOf

		// in output jsen reference to mdprofile
		to.getProfile().setId(CommonUtils.extractIDFromURI(vo.getProfile()));
		to.getProfile().setMethod("");

		for (Person p : vo.getMetadata().getPersons()) {
			PersonTO pto = new PersonTO();
			transferPerson(p, pto);
			to.getContributors().add(pto);
		}
	}

	public static void transferAlbum(Album vo, AlbumTO to) {
		transferProperties(vo, to);

		// TODO: Container
		to.setTitle(vo.getMetadata().getTitle());
		to.setDescription(vo.getMetadata().getDescription());

		for (Person p : vo.getMetadata().getPersons()) {
			PersonTO pto = new PersonTO();
			transferPerson(p, pto);
			to.getContributors().add(pto);
		}

	}

	public static void transferPerson(Person p, PersonTO pto) {

		// pto.setPosition(p.getPos());
		pto.setId(CommonUtils.extractIDFromURI(p.getId()));
		pto.setFamilyName(p.getFamilyName());
		pto.setGivenName(p.getGivenName());
		pto.setCompleteName(p.getCompleteName());
		pto.setAlternativeName(p.getAlternativeName());
		pto.setRole(p.getRole() == null ? "" : p.getRole().toString());
		IdentifierTO ito = new IdentifierTO();
		ito.setValue(p.getIdentifier());
		pto.getIdentifiers().add(ito);
		// set oganizations
		transferContributorOrganizations(p.getOrganizations(), pto);

	}

	public static void transferContributorOrganizations(
			Collection<Organization> orgas, PersonTO pto) {
		for (Organization orga : orgas) {
			OrganizationTO oto = new OrganizationTO();
			oto.setId(CommonUtils.extractIDFromURI(orga.getId()));
			oto.setName(orga.getName());
			oto.setDescription(orga.getDescription());
			IdentifierTO ito = new IdentifierTO();
			ito.setValue(orga.getIdentifier());
			oto.getIdentifiers().add(ito);
			oto.setCity(orga.getCity());
			oto.setCountry(orga.getCountry());
			pto.getOrganizations().add(oto);
		}

	}

	public static void transferProperties(Properties vo, PropertiesTO to) {
		// set ID
		to.setId(vo.getIdString());
		// set createdBy
		UserService ucrud = new UserService();
		String completeName = null;
		URI userId = vo.getCreatedBy();
		try {
			completeName = ucrud.getCompleteName(vo.getCreatedBy());
		} catch (Exception e) {
			LOGGER.info("Cannot read createdBy user: " + userId, e);
		}
		// set createdBy
		to.setCreatedBy(new PersonTOBasic(completeName, ObjectHelper
				.getId(userId)));
		if (!vo.getModifiedBy().equals(vo.getCreatedBy())) {
			userId = vo.getModifiedBy();
			try {
				completeName = ucrud.getCompleteName(vo.getModifiedBy());
			} catch (Exception e) {
				LOGGER.info("Cannot read modifiedBy user: " + userId, e);
			}
		}
		// set modifiedBy
		to.setModifiedBy(new PersonTOBasic(completeName, ObjectHelper
				.getId(userId)));
		// set createdDate, modifiedDate, versionDate
		to.setCreatedDate(CommonUtils.formatDate(vo.getCreated().getTime()));
		to.setModifiedDate(CommonUtils.formatDate(vo.getModified().getTime()));
		to.setVersionDate((vo.getVersionDate() != null) ? CommonUtils
				.formatDate(vo.getVersionDate().getTime()) : "");
		// set status
		to.setStatus(vo.getStatus().toString());
		// set version
		to.setVersion(vo.getVersion());
		// set discardComment
		to.setDiscardComment(vo.getDiscardComment());
	}

	/**
	 * Transfer an {@link Item} into a {@link ItemTO}
	 * 
	 * @param vo
	 * @param to
	 */
	public static void transferItem(Item vo, ItemTO to) {
		transferProperties(vo, to);
		// set visibility
		to.setVisibility(vo.getVisibility().toString());
		// set collectionID
		to.setCollectionId(CommonUtils.extractIDFromURI(vo.getCollection()));
		to.setFilename(vo.getFilename());
		to.setFileSize(vo.getFileSize());
		to.setMimetype(vo.getFiletype());
		to.setChecksumMd5(vo.getChecksum());
		to.setWebResolutionUrlUrl(vo.getWebImageUrl());
		to.setThumbnailUrl(vo.getThumbnailImageUrl());
		to.setFileUrl(vo.getFullImageUrl());

		// set Metadata
		ProfileService pcrud = new ProfileService();
		MetadataProfile profile = new MetadataProfile();
		try {
			profile = pcrud.read(vo.getMetadataSet().getProfile());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.info("Something nasty happend after reading the profile", e);
		}
		transferItemMetadata(profile, vo.getMetadataSet().getMetadata(), to);
	}
	
	
	public static void transferDefaultItem(Item vo, DefaultItemTO to) {
		transferProperties(vo, to);
		//set visibility
		to.setVisibility(vo.getVisibility().toString());
		//set collectionID
		to.setCollectionId(CommonUtils.extractIDFromURI(vo.getCollection()));
		to.setFilename(vo.getFilename());
		to.setFileSize(vo.getFileSize());
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
			LOGGER.info("Something nasty happend after reading the profile", e);
		}
		transferItemMetadataDefault(profile, vo.getMetadataSet().getMetadata(), to);
	}
	
	public static void transferItemMetadataDefault(MetadataProfile profile, Collection<Metadata> voMds, DefaultItemTO to) {
		if(voMds.size() == 0)
			return; 
		Map<String, JsonNode> metadata = new HashMap<String, JsonNode>();
		for(Metadata md : voMds){
			for(Statement s : profile.getStatements())
			{
				if(s.getId().equals(md.getStatement())){
					for (LocalizedString ls : s.getLabels()) {
						String key = ls.getValue();
						switch(md.getTypeNamespace())
						{
						case "http://imeji.org/terms/metadata#text": 
							metadata.put(key, RestProcessUtils.buildJsonNode(((Text) md).getText()));		
							break;
						case "http://imeji.org/terms/metadata#number":
							metadata.put(key, RestProcessUtils.buildJsonNode(((Number) md).getNumber()));
							break;
						case "http://imeji.org/terms/metadata#conePerson":							
							ConePerson mdCP = (ConePerson) md;
							DefaultConePersonTO dcpto = new DefaultConePersonTO();
							dcpto.setFamilyName(mdCP.getPerson().getFamilyName());
							dcpto.setGivenName(mdCP.getPerson().getGivenName());
							metadata.put(key, RestProcessUtils.buildJsonNode(dcpto));
							break;
						case "http://imeji.org/terms/metadata#date":
							metadata.put(key, RestProcessUtils.buildJsonNode(((de.mpg.imeji.logic.vo.predefinedMetadata.Date) md).getDate()));
							break;
						case "http://imeji.org/terms/metadata#geolocation":						
							Geolocation mdGeo = (Geolocation) md;
							DefaultGeolocationTO dgto = new DefaultGeolocationTO();
							dgto.setName(mdGeo.getName());
							dgto.setLongitude(mdGeo.getLongitude());
							dgto.setLatitude(mdGeo.getLatitude());
							metadata.put(key, RestProcessUtils.buildJsonNode(dgto));						
							break;
						case "http://imeji.org/terms/metadata#license":					
							License mdLicense = (License) md;
							DefaultLicenseTO dlto = new DefaultLicenseTO();
							dlto.setLicense(mdLicense.getLicense());
		                    final URI externalUri = mdLicense.getExternalUri();
		                    dlto.setUrl(externalUri != null ? externalUri.toString() : "");
		                    metadata.put(key, RestProcessUtils.buildJsonNode(dlto));
							break;
						case "http://imeji.org/terms/metadata#link":						
							Link mdLink = (Link)md;
							DefaultLinkTO dllto = new DefaultLinkTO();
							dllto.setLink(mdLink.getLabel());
							dllto.setUrl(mdLink.getUri().toString());
							metadata.put(key, RestProcessUtils.buildJsonNode(dllto));
							break;
						case "http://imeji.org/terms/metadata#publication":						
							Publication mdP = (Publication) md;
							DefaultPublicationTO dpto = new DefaultPublicationTO();
							dpto.setPublication(mdP.getUri().toString());
							dpto.setFormat(mdP.getExportFormat());
							dpto.setCitation(mdP.getCitation());
							metadata.put(key, RestProcessUtils.buildJsonNode(dpto));
							break;
						}
                    }
				}
			}
		}
		to.setMetadata(metadata);
	}
	
	

	public static void transferItemMetadata(MetadataProfile profile,
											Collection<Metadata> voMds, ItemTO to) {

		if (voMds.size() == 0) {
			// to.setMetadata(null);
			return;
		}

		for (Metadata md : voMds) {
			md.getId();
			MetadataSetTO mdTO = new MetadataSetTO();
			// mdTO.setPosition(md.getPos());
			mdTO.setStatementUri(md.getStatement());
			mdTO.setTypeUri(URI.create(md.getTypeNamespace()));

			if (profile.getStatements().size() > 0) {
				List<LabelTO> ltos = new ArrayList<LabelTO>();
				for (Statement s : profile.getStatements()) {
					if (s.getId().toString()
							.equals(md.getStatement().toString())) {
						for (LocalizedString ls : s.getLabels()) {
							LabelTO lto = new LabelTO(ls.getLang(),
									ls.getValue());
							ltos.add(lto);
						}
					}
				}
				mdTO.setLabels(ltos);
			}

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
				tt.setText(mdText.getText());
				mdTO.setValue(tt);
				break;
			case "de.mpg.imeji.logic.vo.predefinedMetadata.Number":
				Number mdNumber = (Number) md;
				NumberTO nt = new NumberTO();
				nt.setNumber(mdNumber.getNumber());
				mdTO.setValue(nt);
				break;
			case "de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson":
				ConePerson mdCP = (ConePerson) md;
				ConePersonTO cpto = new ConePersonTO();
				PersonTO personTo = new PersonTO();
				cpto.setPerson(personTo);
				transferPerson(mdCP.getPerson(), cpto.getPerson());
				mdTO.setValue(cpto);
				break;
			case "de.mpg.imeji.logic.vo.predefinedMetadata.Date":
				de.mpg.imeji.logic.vo.predefinedMetadata.Date mdDate = (de.mpg.imeji.logic.vo.predefinedMetadata.Date) md;
				DateTO dt = new DateTO();
				dt.setDate(mdDate.getDate());
				mdTO.setValue(dt);
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
				License mdLicense = (License) md;
				LicenseTO lto = new LicenseTO();
				lto.setLicense(mdLicense.getLicense());
				final URI externalUri = mdLicense.getExternalUri();
				lto.setUrl(externalUri != null ? externalUri.toString() : "");
				mdTO.setValue(lto);
				break;
			case "de.mpg.imeji.logic.vo.predefinedMetadata.Link":
				Link mdLink = (Link) md;
				LinkTO llto = new LinkTO();
				llto.setLink(mdLink.getLabel());
				llto.setUrl(mdLink.getUri().toString());
				mdTO.setValue(llto);
				break;
			case "de.mpg.imeji.logic.vo.predefinedMetadata.Publication":
				Publication mdP = (Publication) md;
				PublicationTO pto = new PublicationTO();
				pto.setPublication(mdP.getUri().toString());
				pto.setFormat(mdP.getExportFormat());
				pto.setCitation(mdP.getCitation());
				mdTO.setValue(pto);
				break;
			}

			to.getMetadata().add(mdTO);
		}
	}

}

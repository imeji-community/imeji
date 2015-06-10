package de.mpg.imeji.rest.process;

import com.fasterxml.jackson.core.JsonFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.imeji.exceptions.BadRequestException;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.controller.ProfileController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.logic.vo.predefinedMetadata.*;
import de.mpg.imeji.logic.vo.predefinedMetadata.Number;
import de.mpg.imeji.rest.defaultTO.DefaultItemTO;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultConePersonTO;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultGeolocationTO;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultLicenseTO;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultLinkTO;
import de.mpg.imeji.rest.defaultTO.predefinedEasyMetadataTO.DefaultPublicationTO;
import de.mpg.imeji.rest.to.*;
import de.mpg.imeji.rest.to.predefinedMetadataTO.*;
import de.mpg.j2j.misc.LocalizedString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.google.common.base.Strings.isNullOrEmpty;

public class ReverseTransferObjectFactory {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ReverseTransferObjectFactory.class);

	public enum TRANSFER_MODE {
		CREATE, UPDATE
	}

	public static void transferCollection(CollectionTO to, CollectionImeji vo,
			TRANSFER_MODE mode, User u) {

		ContainerMetadata metadata = new ContainerMetadata();

		metadata.setTitle(to.getTitle());
		metadata.setDescription(to.getDescription());

		// set contributors
		transferCollectionContributors(to.getContributors(), metadata, u, mode);
		vo.setMetadata(metadata);

	}

	public static void transferAlbum(AlbumTO to, Album vo, TRANSFER_MODE mode,
			User u) {
		ContainerMetadata metadata = new ContainerMetadata();
		metadata.setTitle(to.getTitle());
		metadata.setDescription(to.getDescription());

		// set contributors
		transferCollectionContributors(to.getContributors(), metadata, u, mode);
		vo.setMetadata(metadata);
	}

	/**
	 * Transfert an {@link ItemTO} into an Item
	 * 
	 * @param to
	 * @param vo
	 * @param u
	 * @param mode
	 * @throws ImejiException
	 */
	public static void transferItem(ItemTO to, Item vo, User u,
			TRANSFER_MODE mode) throws ImejiException {

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

	public static void transferItemMetadata(ItemTO to, Item vo, User u,
			TRANSFER_MODE mode) throws ImejiException {

		Collection<Metadata> voMDs = vo.getMetadataSet().getMetadata();
		// Collection<Metadata> copyOfvoMDs = ImmutableList.copyOf(voMDs);
		voMDs.clear();

		MetadataProfile mp = getMetadataProfile(vo.getCollection(), u);

		validateMetadata(to, mp);

		for (Statement st : mp.getStatements()) {
			final URI stURI = st.getId();

			Collection<MetadataSetTO> mdsList = lookUpMetadata(to, st);
			// Metadata mdVOPrev = lookUpMetadata(copyOfvoMDs, stURI,
			// st.getType());
			if (mdsList.isEmpty()) {
				{
					final String message = "Statement { type: \""
							+ st.getType() + "\", id: \"" + stURI
							+ "\" } has not been found for item id: \""
							+ vo.getId() + "\"";
					// throw new RuntimeException(message);
					LOGGER.debug(message);
				}
			} else if (mdsList.size() > 1 && "1".equals(st.getMaxOccurs())) {
				throw new BadRequestException("Statement { type: \""
						+ st.getType() + "\", id: \"" + stURI
						+ "\" } for item id: \"" + vo.getId()
						+ "\" occurs more then once");
			} else
				for (MetadataSetTO md : mdsList)
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
							transferPerson(personTO.getPerson(),
									mdVO.getPerson(), mode);
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
							// set license to uri if empty
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
		}
	}

	/**
	 * Transfer a {@link MetadataProfileTO} into a {@link MetadataProfile}
	 * 
	 * @param to
	 * @param vo
	 * @param mode
	 */
	public static void transferMetadataProfile(MetadataProfileTO to,
			MetadataProfile vo, TRANSFER_MODE mode) {
		if (mode == TRANSFER_MODE.CREATE) {
			vo.setTitle(to.getTitle());
			vo.setDescription(to.getDescription());
			vo.setDefault(to.getDefault());
			for (StatementTO stTO : to.getStatements()) {
				Statement stVO = new Statement();
				stVO.setType(stTO.getType());
				stVO.setLabels(stTO.getLabels());
				stVO.setVocabulary(stTO.getVocabulary());
				for (LiteralConstraintTO lc : stTO.getLiteralConstraints()) {
					stVO.getLiteralConstraints().add(lc.getValue());
				}
				stVO.setMinOccurs(stTO.getMinOccurs());
				stVO.setMaxOccurs(stTO.getMaxOccurs());
				// TODO: check namespace
				// stVO.setNamespace(???);
				if (!isNullOrEmpty(stTO.getParentStatementId()))
					stVO.setParent(URI.create(stTO.getParentStatementId()));
				vo.getStatements().add(stVO);
			}
		}

	}

	/**
	 * Check all item metadata statement/types: they should be presented in the
	 * MetadataProfile statements
	 *
	 * @param to
	 * @param mp
	 * @throws de.mpg.imeji.exceptions.BadRequestException
	 */
	private static void validateMetadata(ItemTO to, MetadataProfile mp)
			throws BadRequestException {
		for (MetadataSetTO md : to.getMetadata()) {
			try {
				lookUpStatement(mp.getStatements(), md.getTypeUri(),
						md.getStatementUri());
			} catch (NoSuchElementException e) {
				throw new BadRequestException("Cannot find { typeUri: "
						+ md.getTypeUri() + " , statementUri: "
						+ md.getStatementUri() + "} in profile: " + mp.getId());
			}
		}
	}

	private static MetadataProfile getMetadataProfile(URI collectionURI, User u)
			throws ImejiException {
		ProfileController pc = new ProfileController();
		return pc.retrieveByCollectionId(collectionURI, u);
	}

	private static Statement lookUpStatement(Collection<Statement> statements,
			final URI type, final URI statementUri) {
		return Iterables.find(statements, new Predicate<Statement>() {
			@Override
			public boolean apply(Statement st) {
				return st.getType().equals(type)
						&& st.getId().equals(statementUri);
			}
		});
	}

	private static Collection<MetadataSetTO> lookUpMetadata(ItemTO to,
			final Statement st) {
		return Collections2.filter(to.getMetadata(),
				new Predicate<MetadataSetTO>() {
					@Override
					public boolean apply(MetadataSetTO md) {
						return md.getTypeUri().equals(st.getType())
								&& md.getStatementUri().equals(st.getId());
					}
				});
	}

	public static void transferPerson(PersonTO pto, Person p, TRANSFER_MODE mode) {

		if (mode == TRANSFER_MODE.CREATE) {
			// p.setPos(pto.getPosition());
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
			ContainerMetadata metadata, User u, TRANSFER_MODE mode) {
		for (PersonTO pTO : persons) {
			Person person = new Person();
			person.setFamilyName(pTO.getFamilyName());
			person.setGivenName(pTO.getGivenName());
			person.setCompleteName(pTO.getCompleteName());
			person.setAlternativeName(pTO.getAlternativeName());
			person.setRole(URI.create(pTO.getRole()));
			// person.setPos(pTO.getPosition());

			if (pTO.getIdentifiers().size() == 1) {
				// set the identifier of current person
				IdentifierTO ito = new IdentifierTO();
				ito.setValue(pTO.getIdentifiers().get(0).getValue());
				person.setIdentifier(ito.getValue());
			} else if (pTO.getIdentifiers().size() > 1) {
				System.out
						.println("I have more identifiers than needed for Person");
			}

			// set organizations
			transferContributorOrganizations(pTO.getOrganizations(), person,
					mode);
			metadata.getPersons().add(person);
		}

		if (metadata.getPersons().size() == 0
				&& TRANSFER_MODE.CREATE.equals(mode)) {
			Person personU = new Person();
			PersonTO pTo = new PersonTO();
			personU.setFamilyName(u.getPerson().getFamilyName());
			personU.setGivenName(u.getPerson().getGivenName());
			personU.setCompleteName(u.getPerson().getCompleteName());
			personU.setAlternativeName(u.getPerson().getAlternativeName());
			if (!isNullOrEmpty(u.getPerson().getIdentifier())) {
				IdentifierTO ito = new IdentifierTO();
				ito.setValue(u.getPerson().getIdentifier());
				personU.setIdentifier(ito.getValue());
			}
			personU.setOrganizations(u.getPerson().getOrganizations());
			personU.setRole(URI.create(pTo.getRole()));
			metadata.getPersons().add(personU);
		}

	}

	public static void transferContributorOrganizations(
			List<OrganizationTO> orgs, Person person, TRANSFER_MODE mode) {
		for (OrganizationTO orgTO : orgs) {
			Organization org = new Organization();

			if (mode == TRANSFER_MODE.CREATE) {
				// TODO: Organization can have only one identifier, why
				// OrganizationTO has many?
				// get only first one!
				if (orgTO.getIdentifiers().size() > 0) {
					IdentifierTO ito = new IdentifierTO();
					ito.setValue(orgTO.getIdentifiers().get(0).getValue());
					org.setIdentifier(ito.getValue());
					if (orgTO.getIdentifiers().size() > 1) {
						LOGGER.info("Have more organization identifiers than needed");
					}
				}
			}

			org.setName(orgTO.getName());
			org.setDescription(orgTO.getDescription());
			org.setCity(orgTO.getCity());
			org.setCountry(orgTO.getCountry());

			// set the identifier of current organization
		}
    }
    
    
	/**
	 * Transfer Default Item Json format to item
	 * 
	 * @return 
	 */
	public static void transferDefaultItemTOtoItemTO(MetadataProfileTO profileTO, DefaultItemTO defaultTO, ItemTO itemTO) throws BadRequestException, JsonParseException, JsonMappingException{
	 	if(defaultTO.getMetadata() == null)
	 	{
	 		itemTO.getMetadata().clear();
	 	}else
	 	{  
			for(Map.Entry<String, JsonNode> entry : defaultTO.getMetadata().entrySet()){  
				boolean update = false;
				String key = "";
				int pos = -1;
				boolean exitMD = false; 
				for(StatementTO sTO : profileTO.getStatements())
				{ 
					for(LocalizedString label : sTO.getLabels())
					{
						if(entry.getKey().equals(label.getValue()))
						{
							key = entry.getKey();
							update = true;
							break;
						}	
						else if(!("1".equals(sTO.getMaxOccurs())))
						{ 
							try{
							key = entry.getKey().substring(0, entry.getKey().lastIndexOf("_"));
							}catch(StringIndexOutOfBoundsException e){
								break;
							}
							if(key.equals(label.getValue())){
								try
								{
									pos = Integer.parseInt(entry.getKey().substring(entry.getKey().lastIndexOf("_")+1));
								}catch(NumberFormatException e){
									break;
								}
								update = true;
								break;
							}
						}
					}
					if(update)
					{
						MetadataSetTO mdTO = new MetadataSetTO();
						int max = 0;
						List<MetadataSetTO> sets = new ArrayList<MetadataSetTO>();
						if( !(pos ==-1 && "unbounded".equals(sTO.getMaxOccurs()))){
							
							for(MetadataSetTO mdTO2 : itemTO.getMetadata())
							{
								for(LabelTO label : mdTO2.getLabels())
								{
									if(key.equals(label.getValue()))
									{
										mdTO = mdTO2;
										exitMD = true;
										sets.add(mdTO2);
										max ++;
										break;
									}
								}												
							} 
						}
						  
						if(!exitMD)
						{
							List<LabelTO> labels = new ArrayList<LabelTO>();
							for(LocalizedString label : sTO.getLabels())
							{
								labels.add(new LabelTO(label.getLang(), label.getValue()));
							}
							mdTO.setLabels(labels);
							mdTO.setStatementUri(ObjectHelper.getURI(Statement.class, sTO.getId()));
							mdTO.setTypeUri(sTO.getType());
							itemTO.getMetadata().add(mdTO);
						}
						else if(pos != -1)
						{
							if(max == pos-1)
							{
								List<LabelTO> labels = new ArrayList<LabelTO>();
								for(LocalizedString label : sTO.getLabels())
								{
									labels.add(new LabelTO(label.getLang(), label.getValue()));
								}
								mdTO.setLabels(labels);;
								mdTO.setStatementUri(ObjectHelper.getURI(Statement.class, sTO.getId()));
								mdTO.setTypeUri(sTO.getType());
								itemTO.getMetadata().add(mdTO);
							}
							else if(max != pos)
							{
								try
								{
									mdTO = sets.get(pos-1);
								}catch(IndexOutOfBoundsException e)
								{
									throw new BadRequestException(key + " has " + max + " value. Input " + key + "_" + String.valueOf(max+1) + " instead of " + key + "_" + pos + " to add the " + String.valueOf(max+1) + ". value.");
								}
							}
						}  
						
						
						JsonNode node = entry.getValue();
						JsonFactory factory = new JsonFactory();
						ObjectMapper mapper = new ObjectMapper(factory);
	
						switch(sTO.getType().toString())
						{
							case "http://imeji.org/terms/metadata#text": 
								if(node == null)
								{
									itemTO.getMetadata().remove(mdTO);
								}
								else
								{
									TextTO newT = new TextTO();
									newT.setText(node.textValue());
									mdTO.setValue(newT);
								}
								break;
							case "http://imeji.org/terms/metadata#number":
								if(node == null)
								{
									itemTO.getMetadata().remove(mdTO);
								}
								else
								{
									NumberTO newNT = new NumberTO();
									newNT.setNumber(node.asDouble());
									mdTO.setValue(newNT);
								}
								break;
							case "http://imeji.org/terms/metadata#conePerson":
								if(node == null)
								{
									itemTO.getMetadata().remove(mdTO);
								}
								else
								{
									DefaultConePersonTO easyCPTO = null;
									try {
										easyCPTO = mapper.readValue(node.toString(), new TypeReference<DefaultConePersonTO>(){});
									} catch (Exception e) {
										throw new BadRequestException( entry + e.getMessage());
									} 
									ConePersonTO newCone = (mdTO.getValue() != null) ? ((ConePersonTO)mdTO.getValue()) : (new ConePersonTO());
									PersonTO newP = (mdTO.getValue() != null) ? (newCone.getPerson()) : (new PersonTO());
									newP.setFamilyName(easyCPTO.getFamilyName());
									newP.setGivenName(easyCPTO.getGivenName());
									newCone.setPerson(newP);
									mdTO.setValue(newCone);
								}
								break;
							case "http://imeji.org/terms/metadata#date":
								if(node == null)
								{
									itemTO.getMetadata().remove(mdTO);
								}
								else
								{
									DateTO newDT = new DateTO();
									newDT.setDate(node.textValue());
									mdTO.setValue(newDT);
								}
								break;
							case "http://imeji.org/terms/metadata#geolocation":  
								if(node == null)
								{
									itemTO.getMetadata().remove(mdTO);
								}
								else
								{
									DefaultGeolocationTO easyGeoTO = null;
									try {
										easyGeoTO = mapper.readValue(node.toString(), new TypeReference<DefaultGeolocationTO>(){});
									} catch (Exception e) {
										throw new BadRequestException( entry + e.getMessage());
									} 
									GeolocationTO newGT = new GeolocationTO();
									newGT.setName(easyGeoTO.getName());
									newGT.setLatitude(easyGeoTO.getLatitude());
									newGT.setLongitude(easyGeoTO.getLongitude());
									mdTO.setValue(newGT);
								}
								break;
							case "http://imeji.org/terms/metadata#license":
								if(node == null)
								{
									itemTO.getMetadata().remove(mdTO);
								}
								else
								{
									DefaultLicenseTO easyLTO = null;
									try {
										easyLTO = mapper.readValue(node.toString(), new TypeReference<DefaultLicenseTO>(){});
									} catch (Exception e) {
										throw new BadRequestException( entry + e.getMessage());
									} 
									LicenseTO newLicense = new LicenseTO();
									newLicense.setLicense(easyLTO.getLicense());
									newLicense.setUrl(easyLTO.getUrl());
									mdTO.setValue(newLicense);
								}
								break;
							case "http://imeji.org/terms/metadata#link":
								if(node == null)
								{
									itemTO.getMetadata().remove(mdTO);
								}
								else
								{
									DefaultLinkTO easyLinkTO = null;
									try {
										easyLinkTO = mapper.readValue(node.toString(), new TypeReference<DefaultLinkTO>(){});
									} catch (Exception e) {
										throw new BadRequestException( entry + e.getMessage());
									} 
									LinkTO newLink = new LinkTO();
									newLink.setLink(easyLinkTO.getLink());
									newLink.setUrl(easyLinkTO.getUrl());
									mdTO.setValue(newLink);
								}
								break;
							case "http://imeji.org/terms/metadata#publication":
								if(node == null)
								{
									itemTO.getMetadata().remove(mdTO);
								}
								else
								{
									DefaultPublicationTO easyPTO = null;
									try {
										easyPTO = mapper.readValue(node.toString(), new TypeReference<DefaultPublicationTO>(){});
									} catch (Exception e) {
										throw new BadRequestException( entry + e.getMessage());
									} 
									PublicationTO newPub = new PublicationTO();
									newPub.setCitation(easyPTO.getCitation());
									newPub.setFormat(easyPTO.getFormat());
									newPub.setPublication(easyPTO.getPublication());
									mdTO.setValue(newPub);
								}
								break;
						}						
						break;
					}
				}
				if(!update){
					throw new BadRequestException(entry+ " does not find in the profile");
				}
			}
	 	}
	}

}

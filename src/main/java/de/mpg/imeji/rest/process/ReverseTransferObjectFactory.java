package de.mpg.imeji.rest.process;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.mpg.imeji.logic.vo.*;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.rest.api.ProfileService;
import de.mpg.imeji.rest.to.*;

import java.util.UUID;

public class ReverseTransferObjectFactory {
	
	
	public static void transferCollection(CollectionTO to, CollectionImeji vo) {
		ContainerMetadata metadata = new ContainerMetadata();
		metadata.setTitle(to.getTitle());
		metadata.setDescription(to.getDescription());
		
		//set contributors
		transferCollectionContributors(to.getContributors(), metadata);
		vo.setMetadata(metadata);

		//set Metadata
		CollectionProfileTO profileTO = to.getProfile();

		//TODO: change the code after CollectionImeji update
		if (null == profileTO || profileTO.getProfileId() == null ) {
			//profile = ImejiFactory.newProfile();
			vo.setProfile(URI.create("default___copy"));
			//reference profile to existed one
		} else {
			vo.setProfile(URI.create(profileTO.getProfileId() + "___" + profileTO.getMethod()));
		}


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

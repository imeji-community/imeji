package de.mpg.imeji.rest.process;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.ContainerMetadata;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.rest.to.CollectionTO;
import de.mpg.imeji.rest.to.IdentifierTO;
import de.mpg.imeji.rest.to.OrganizationTO;
import de.mpg.imeji.rest.to.PersonTO;

public class ReverseTransferObjectFactory {
	
	
	public static void transferCollection(CollectionTO to, CollectionImeji vo) {
		ContainerMetadata metadata = new ContainerMetadata();
		metadata.setTitle(to.getTitle());
		metadata.setDescription(to.getDescription());
		
		//set contributors
		transferCollectionContributors(to.getContributors(), metadata);
		vo.setMetadata(metadata);
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

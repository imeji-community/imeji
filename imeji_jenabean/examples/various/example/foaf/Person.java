package example.foaf;

import java.net.URI;
import java.util.Collection;

import thewebsemantic.Namespace;
import thewebsemantic.Resource;

@Namespace(FoafUri.NS)
public class Person extends Agent {
	String name;
	Document homepage;
	Collection<Person> knows;
	String geekcode;
	String firstName;
	String surname;
	String family_name;
	String plan;
	Resource img;
	String myersBriggs;
	Document workplaceHomepage;
	Document workInfoHomepage;
	Document schoolHomepage;
	Collection<Resource> interest;
	Collection<Resource> topic_interest;
	Collection<Document> publications;
	Collection<Resource> currentProject;
	Collection<Resource> pastProject;

	public Person() {}
	
	public Person(URI uri) {
		super(uri);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Document getHomepage() {
		return homepage;
	}

	public void setHomepage(Document homepage) {
		this.homepage = homepage;
	}

	public Collection<Person> getKnows() {
		return knows;
	}

	public void setKnows(Collection<Person> knows) {
		this.knows = knows;
	}

	public String getGeekcode() {
		return geekcode;
	}

	public void setGeekcode(String geekcode) {
		this.geekcode = geekcode;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getFamily_name() {
		return family_name;
	}

	public void setFamily_name(String family_name) {
		this.family_name = family_name;
	}

	public String getPlan() {
		return plan;
	}

	public void setPlan(String plan) {
		this.plan = plan;
	}

	public Resource getImg() {
		return img;
	}

	public void setImg(Resource img) {
		this.img = img;
	}

	public String getMyersBriggs() {
		return myersBriggs;
	}

	public void setMyersBriggs(String myersBriggs) {
		this.myersBriggs = myersBriggs;
	}

	public Document getWorkplaceHomepage() {
		return workplaceHomepage;
	}

	public void setWorkplaceHomepage(Document workplaceHomepage) {
		this.workplaceHomepage = workplaceHomepage;
	}

	public Document getWorkInfoHomepage() {
		return workInfoHomepage;
	}

	public void setWorkInfoHomepage(Document workInfoHomepage) {
		this.workInfoHomepage = workInfoHomepage;
	}

	public Document getSchoolHomepage() {
		return schoolHomepage;
	}

	public void setSchoolHomepage(Document schoolHomepage) {
		this.schoolHomepage = schoolHomepage;
	}

	public Collection<Resource> getInterest() {
		return interest;
	}

	public void setInterest(Collection<Resource> interest) {
		this.interest = interest;
	}

	public Collection<Resource> getTopic_interest() {
		return topic_interest;
	}

	public void setTopic_interest(Collection<Resource> topic_interest) {
		this.topic_interest = topic_interest;
	}

	public Collection<Document> getPublications() {
		return publications;
	}

	public void setPublications(Collection<Document> publications) {
		this.publications = publications;
	}

	public Collection<Resource> getCurrentProject() {
		return currentProject;
	}

	public void setCurrentProject(Collection<Resource> currentProject) {
		this.currentProject = currentProject;
	}

	public Collection<Resource> getPastProject() {
		return pastProject;
	}

	public void setPastProject(Collection<Resource> pastProject) {
		this.pastProject = pastProject;
	}
}

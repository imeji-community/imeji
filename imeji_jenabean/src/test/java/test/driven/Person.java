package test.driven;

import java.util.Collection;

import thewebsemantic.binding.RdfBeanId;

public class Person extends RdfBeanId<Person> {

	Language lang;
	Collection<Continent> visited;

	public Person() {
		super();
	}

	public Language getLang() {
		return lang;
	}

	public void setLang(Language lang) {
		this.lang = lang;
	}

	public Collection<Continent> getVisited() {
		return visited;
	}

	public void setVisited(Collection<Continent> visited) {
		this.visited = visited;
	}

}
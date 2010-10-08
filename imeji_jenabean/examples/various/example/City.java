package example;

import java.util.Collection;

import thewebsemantic.RdfProperty;
import thewebsemantic.Uri;
import thewebsemantic.binding.RdfBean;

public class City extends RdfBean<City>{

    public static final String NS = "http://www.geonames.org/ontology#";
	private Collection<String> alternateNames;
	private String name;
	private String id;
	private String population;
	
	public City(String uri) {
		id = uri;
	}
	
	@Uri
	public String getUri() {
		return id;
	}
	
	@RdfProperty(NS + "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@RdfProperty(NS + "alternateName")
	public Collection<String> getAlternateNames() {
		return alternateNames;
	}

	public void setAlternateNames(Collection<String> alternateNames) {
		this.alternateNames = alternateNames;
	}

	@RdfProperty(NS + "population")
	public String getPopulation() {
		return population;
	}

	public void setPopulation(String population) {
		this.population = population;
	}
	
}

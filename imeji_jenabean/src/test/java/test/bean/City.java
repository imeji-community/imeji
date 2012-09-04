package test.bean;

import thewebsemantic.Namespace;
import thewebsemantic.Uri;

@Namespace("http://example.org/")
public class City {

	private String name;
	private int population;
	private String uri;
	
	public City(String uri) {
		this.uri = uri;
	}
	
	@Uri
	public String getTheUri() {
		return uri;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getPopulation() {
		return population;
	}
	
	public void setPopulation(int population) {
		this.population = population;
	}
}

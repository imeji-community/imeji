package example.travel;

import java.util.ArrayList;
import java.util.Collection;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace("http://purl.org/travel/")
public class Trip {
	private Collection<Item> items = new ArrayList<Item>();
	private String author;
	private String title;
	private String summary;
	private float lat;
	private float lon;

	public Collection<Item> getItems() {
		return items;
	}
	
	public void setItems(Collection<Item> items) {
		this.items = items;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	@RdfProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat")
	public float getLat() {
		return lat;
	}

	public void setLat(float lat) {
		this.lat = lat;
	}
	
	@RdfProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long")
	public float getLon() {
		return lon;
	}
	
	public void setLon(float lon) {
		this.lon = lon;
	}
	
}

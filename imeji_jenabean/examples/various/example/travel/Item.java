package example.travel;

import java.util.Date;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace("http://purl.org/travel/")
public class Item {
	private String link;
	private String title;
	private String summary;
	private Date from;
	private Date to;
	private float lat;
	private float lon;

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
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

	@RdfProperty("http://www.w3.org/2002/12/cal#dtstart")
	public Date getFrom() {
		return from;
	}

	
	public void setFrom(Date from) {
		this.from = from;
	}

	@RdfProperty("http://www.w3.org/2002/12/cal#dtend")
	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
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

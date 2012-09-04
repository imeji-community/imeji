package test.lang;

import java.net.URI;
import java.util.Collection;

import thewebsemantic.Id;
import thewebsemantic.LocalizedString;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace("http://www.geonames.org/ontology#")
public class Feature {
	
	@Id
	URI uri;
	
	@RdfProperty("http://www.w3.org/2003/01/geo/wgs84_pos#lat")
	public double lat;
	@RdfProperty("http://www.w3.org/2003/01/geo/wgs84_pos#long")
	public double lon;
	
	public String name;
	public Collection<LocalizedString> alternateName;

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

}
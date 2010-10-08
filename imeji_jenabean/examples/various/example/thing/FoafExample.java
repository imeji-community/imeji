package example.thing;

import java.net.URI;

import thewebsemantic.Thing;
import thewebsemantic.vocabulary.Foaf;
import thewebsemantic.vocabulary.Geo;
import thewebsemantic.vocabulary.Rdfs;
import thewebsemantic.vocabulary.Skos;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * To run this you'll need the Jena jars + the jenabean jar. 
 * (http://code.google.com/p/jenabean/)
 *
 */
public class FoafExample {
	
	public static void main(String[] args) {
	Model m = ModelFactory.createDefaultModel();

	m.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
	m.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
	m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
	m.setNsPrefix("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
	
	Thing t = new Thing("http://example.com/1",m);
	t.as(Foaf.class).
	    aimChatID("example").
		birthday("01/01/1999").
		name("Bob Ducharm").
		firstName("Bob").
		family_name("Ducharm").
		homepage(URI.create("http://www.snee.com/")).
		weblog(URI.create("http://www.snee.com/bobdc.blog/")).
		knows(new Thing("http://www.thewebsemantic.com/card.rdf", m)).
	as(Geo.class).
		lat(33.3f).
		long_(120.1f).
	    isa(Skos.Concept.class).
	as(Rdfs.class).
	 	label("an example for Bob");
		m.write(System.out, "N3");
	}
}

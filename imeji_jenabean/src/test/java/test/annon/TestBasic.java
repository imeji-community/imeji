package test.annon;

import java.io.InputStream;

import org.junit.Test;

import thewebsemantic.Bean2RDF;
import thewebsemantic.NotFoundException;
import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestBasic {

	@Test
	public void drive() throws NotFoundException { 
		OntModel m = ModelFactory.createOntologyModel();
		
		// this weirdness is so that the test runs well in eclipse junit and maven
		InputStream in = getClass().getResourceAsStream("/example.n3");
		if (in == null)
			in = getClass().getResourceAsStream("example.n3");
		m.read( in,null, "N3");
		
		// bind Thing.class to a pre-existing RDF type ../thing
		Jenabean j = Jenabean.instance();
		j.bind(m);
		j.bind("http://thewebsemantic.com/thing").to(Thing.class);
		
		// try and find a Thing with specific url
		Thing me = j.include("knows").load(Thing.class, "http://thewebsemantic.com/me");
		
		// the thing's name from RDF
		System.out.println(me.name);
		
		// the annonymous known thing's name too
		System.out.println("me :knows " + me.getKnows().size() + " things");
		for (Thing t : me.getKnows()) {
			System.out.println(t.name);
		}
		Bean2RDF writer = new Bean2RDF(m);
		writer.save(me);
		writer.save(me);
		writer.save(me);
		writer.save(me);
		m.write(System.out, "N3");		

	}
	
	@Test
	public void save() {
		OntModel m = ModelFactory.createOntologyModel();

		Thing t = new Thing(null);
		t.setName("anon thing");
		Bean2RDF writer = new Bean2RDF(m);
		writer.save(t);
		m.write(System.out, "N3");
	}
}

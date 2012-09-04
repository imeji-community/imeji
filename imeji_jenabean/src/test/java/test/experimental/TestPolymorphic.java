package test.experimental;

import java.util.Collection;

import org.junit.Test;

import thewebsemantic.Bean2RDF;
import thewebsemantic.NotFoundException;
import thewebsemantic.RDF2Bean;
import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class TestPolymorphic {
	
	
	/*
	 * see what happens when a resource 
	 * can be loaded as two different classes
	 */
	@Test
	public void basic() throws NotFoundException {
		Model m = ModelFactory.createDefaultModel();
		
		Jenabean.instance().bind(m);
		Bean2RDF w = Jenabean.instance().writer();
		Red red = new Red("http://example.org/red1");
		Blue b = new Blue("http://example.org/blue1");
		b.setLabel("first blue thing");
		red.setLabel("first red item");
		w.save(red);
		w.save(b);
		Resource r = m.getResource(red.uri);
		r.addProperty(RDF.type, m.getResource("http://example.org/Blue"));
		
		RDF2Bean reader = Jenabean.instance().reader();
		red = reader.load(Red.class, "http://example.org/red1");
		
		b = reader.load(Blue.class, "http://example.org/red1");
		System.out.println(b.getLabel());
		System.out.println(b.getClass());		

		Collection<Blue> blueThings = reader.load(Blue.class);
		System.out.println(blueThings.size());		
		b = blueThings.iterator().next();
		System.out.println(b.getLabel());
		System.out.println(b.getClass());		
	}

}

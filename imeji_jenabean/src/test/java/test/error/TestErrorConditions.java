package test.error;

import org.junit.Test;
import static org.junit.Assert.*;

import thewebsemantic.Bean2RDF;
import thewebsemantic.NotFoundException;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestErrorConditions {

	@Test
	public void invalidType() throws NotFoundException {
		Model m = ModelFactory.createDefaultModel();
		
		Bean2RDF writer = new Bean2RDF(m);
		Bad bad = new Bad();
		bad.getThings().add("one");
		bad.getOther().put("foo", "bar");
		writer.save(bad);		
	
		RDF2Bean reader = new RDF2Bean(m);
		bad = reader.load(Bad.class, bad.hashCode());
		assertNotNull(bad);
		assertEquals(1, bad.getThings().size());
	}
	
	@Test
	public void privateMethods() {
		Model m = ModelFactory.createDefaultModel();		
		Bean2RDF writer = new Bean2RDF(m);		
		writer.save(new Private("foo"));
		
	
	}
}

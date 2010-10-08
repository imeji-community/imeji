package test.bean;

import org.junit.Test;
import static org.junit.Assert.*;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestUriAnnotation {
	
	@Test
	public void testBasic()  throws Exception {
		City c = new City("http://example.org/Boston");		
		c.setName("Boston");
		c.setPopulation(12345);
		
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);	
		Bean2RDF writer = new Bean2RDF(m);
		writer.save(c);
	
		RDF2Bean reader = new RDF2Bean(m);
		City boston = reader.load(City.class, "http://example.org/Boston");
		assertEquals(12345, boston.getPopulation());
		assertEquals("Boston", boston.getName());
	}
}

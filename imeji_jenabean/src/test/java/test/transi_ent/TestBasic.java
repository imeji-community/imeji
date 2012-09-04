package test.transi_ent;

import java.net.URI;

import org.junit.Test;
import static org.junit.Assert.*;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestBasic {

	@Test
	public void basic() {
		
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);
		Company c = new Company();
		c.setName("asdf");
		c.setDontsaveme("hi");
		c.setIdentifier(URI.create("http://foo"));
		Bean2RDF writer = new Bean2RDF(m);
		writer.save(c);
		
		RDF2Bean reader = new RDF2Bean(m);
		c = reader.load(Company.class, c.getIdentifier());
		assertNull(c.getDontsaveme());
		
	}
}

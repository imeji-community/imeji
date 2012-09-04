package test.id;

import org.junit.Test;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import static org.junit.Assert.*;

public class TestUriEncoding {
	
	@Test
	public void basic() {
		String id = "please & encode me ?";
		Model m = ModelFactory.createDefaultModel();
		Bean2RDF writer = new Bean2RDF(m);
		Place bean = new Place();
		bean.name = id;
		writer.save(bean);
		m.write(System.out, "N3");
		
		RDF2Bean reader = new RDF2Bean(m);
		Place p = reader.load(Place.class, "please & encode me ?");
		assertNotNull(p);
		assertEquals(id, p.name);
	}

}

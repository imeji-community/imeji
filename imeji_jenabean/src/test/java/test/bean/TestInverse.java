package test.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;

import org.junit.After;
import org.junit.Test;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestInverse {
	
	@After
	public void after() {
		File f = new File("tmp.rdf");
		if (f.exists())
			f.delete();
	}
	@Test
	public void testExtended()  throws Exception {
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);	
		Bean2RDF writer = new Bean2RDF(m);
		
		Tag fun = new Tag("fun");
		writer.save(fun);
		Tag run = new Tag("run");
		Post p1 = new Post();
		p1.setTitle("i like OWL");
		writer.save(p1);
		p1.addTag(fun);
		p1.addTag(run);
		
		writer.save(p1); 
		RDF2Bean reader = new RDF2Bean(m);
		reader.loadDeep(Post.class, p1.hashCode());
		Post test = reader.loadDeep(Post.class, p1.hashCode());
		assertEquals(2, test.getTags().size());
		
		Tag funLoaded = reader.loadDeep(Tag.class, "fun");
		Collection<Taggable> items = funLoaded.getItems();
		assertEquals(1, funLoaded.getItems().size());
		for (Object o : items) {
			assertTrue(o instanceof Post);
			Post p = (Post)o;
			assertEquals("i like OWL", p.getTitle());
			assertEquals(p.getTags().size(), 2);
		}		
	}
	
	@Test
	public void testBasic() throws Exception {
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);	
		Bean2RDF writer = new Bean2RDF(m);

		Apple a = new Apple();
		Apple aprime = new Apple();
		
		a.addOrange(new Orange());
		a.addOrange(new Orange());
		
		Orange o = new Orange();
		int rememberedId = o.hashCode();
		a.addOrange(o);
		aprime.addOrange(o);
		
		writer.save(a);
		writer.save(o);		
		
		//m.writeAll(System.out, "N3", null);
		RDF2Bean reader = new RDF2Bean(m);
		Collection<Orange> oranges = reader.loadDeep(Orange.class);

		for (Orange orange : oranges)
			assertEquals(1, orange.getApples().size());

		writer.save(aprime);
		File f = new File("tmp.rdf");
		if ( f.exists())
			f.delete();
		m.write(new FileWriter("tmp.rdf"));
		m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		reader = new RDF2Bean(m);
		m.read(new FileReader("tmp.rdf"), null);
		o = reader.loadDeep(Orange.class, rememberedId);
		assertEquals(2, o.getApples().size());
		
		//without reasoner, it's back to none
		m = ModelFactory.createOntologyModel();
		reader = new RDF2Bean(m);
		m.read(new FileReader("tmp.rdf"), null);
		o = reader.loadDeep(Orange.class, rememberedId);
		assertEquals(0, o.getApples().size());
		
	}
}

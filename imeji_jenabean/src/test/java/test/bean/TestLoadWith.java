package test.bean;

import static org.junit.Assert.assertEquals;
import static thewebsemantic.binding.Jenabean.load;

import org.junit.BeforeClass;
import org.junit.Test;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;


public class TestLoadWith {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		OntModel m = ModelFactory.createOntologyModel();
		Jenabean.instance().bind(m);
		
	}
	
	@Test
	public void testBasic() throws Exception {
		//System.setProperty("jenabean.fieldaccess", "true");
		OntModel m = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_MEM_MINI_RULE_INF);
		Bean2RDF writer = new Bean2RDF(m);

		Tag fun = new Tag("fun");
		Tag run = new Tag("run");
		Post p1 = new Post();
		p1.setTitle("i like OWL");
		p1.addTag(fun);
		p1.addTag(run);
		writer.save(p1);
		//m.write(System.out, "N3");
		RDF2Bean reader = new RDF2Bean(m);
		Post shallow = reader.load(Post.class, p1.hashCode());
		assertEquals(2, shallow.getTags().size());

		shallow = reader.load(Post.class, p1.hashCode());
		assertEquals(2, shallow.getTags().size());
	}

	@Test
	public void testPeople() throws Exception {
		
		Person p = new Person();
		p.setFirstName("Joe");
		p.setLastName("Joe");
		p.save();
		p.refresh();
		assertEquals(0, p.getFriends().size());

		//Jenabean initiates collections to zero length for you
		assertEquals(0, p.getFriends().size());
		assertEquals(0, p.getAncestors().size());
		assertEquals(0, p.getColleagues().size());
		assertEquals("Joe", p.getFirstName());

		for(int i=0; i<10; i++)
			p.getFriends().add(new Person());

		for(int i=0; i<101; i++)
			p.getColleagues().add(new Person());

		p.save();
		Person p2 = load(Person.class, p.uri()).fill();

		assertEquals(10, p2.getFriends().size());
		assertEquals(0, p2.getAncestors().size());
		assertEquals(101, p2.getColleagues().size());
		
		Resource i = p.asIndividual();
		assertEquals(i.getURI(), p.uri());
		p2.setFirstName("Dan");
		p2.save();

		p.refresh();
		assertEquals("Dan", p.getFirstName());
		p.delete();
		p2.delete();
	
	}
	
	@Test
	public void testOther() throws Exception {
		Person p = new Person();
		p.setFirstName("Guru");
		p.setLastName("Matahandrashingaranthanonan");
		p.save();
		p.refresh();
		System.out.println(p.asIndividual().getURI());
		assertEquals(0, p.getAncestors().size());
	}
}

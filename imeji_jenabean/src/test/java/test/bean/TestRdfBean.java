package test.bean;

import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import thewebsemantic.binding.Jenabean;
import static thewebsemantic.binding.Jenabean.*;
import static org.junit.Assert.*;

public class TestRdfBean {
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		OntModel m = ModelFactory.createOntologyModel();
		Jenabean.instance().bind(m);
	}
	
	@Test
	public void testBasic() throws Exception {
		Article a = new Article("a1");
		a.save();		
		Article b = load(Article.class, "a1");
		assertEquals(0, b.getAuthors().size());
		b.fill("authors");
		assertEquals(0, b.getAuthors().size());
		
		b.getAuthors().add("Vox Day");
		b.getAuthors().add("Dino");
		b.getAuthors().add("Daddy G");
		b.save();
		
		a = load(Article.class, "a1");
		assertEquals(3, a.getAuthors().size());

	}
}

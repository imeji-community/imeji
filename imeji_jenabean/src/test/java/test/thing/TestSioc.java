package test.thing;

import org.junit.Test;

import thewebsemantic.Thing;
import thewebsemantic.vocabulary.Rdfs;
import thewebsemantic.vocabulary.Sioc;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestSioc {

	@Test
	public void basic() {
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		m.setNsPrefix("sioc", "http://rdfs.org/sioc/ns#");
		Thing t = new Thing("http://johnbreslin.com/blog/2006/09/07/creating-connections-between-discussion-clouds-with-sioc/", m);
		Thing creator = new Thing("http://johnbreslin.com/blog/author/cloud/", m);
		Thing topic1 = new Thing("http://johnbreslin.com/blog/category/semantic-web/", m);
		topic1.getOntResource().setLabel("Semantic Web", null);
		Thing topic2 = new Thing("http://johnbreslin.com/blog/category/blogs/", m);
		topic2.getOntResource().setLabel("blog", null);
		
		
		creator.isa(Sioc.User.class).as(Rdfs.class).label("Cloud");
		
		t.isa(Sioc.Post.class).
			has_creator(creator).
			content("SIOC provides a unified vocabulary for content ...").
			topic( topic1 ).
			topic( topic2 );
		m.write(System.out, "RDF/XML-ABBREV");
		
		
		for (Thing topic :  t.as(Sioc.class).topic()) {
			System.out.println("found topic " + topic.getOntResource().getLabel(null));
		}
		
		System.out.println( t.as(Sioc.class).content() );
		
	}
}

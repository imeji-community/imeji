package example;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * Tags describe many taggable things.  Things have many tags.
 * This example shows how OWL inverse properties allow 
 * the inferencer to add new information.  The beans we'll use in
 * this example are clean as far as inheritance, the only requirements
 * are to annotate the beans indicating the id, and which properties
 * have special considerations, such as the inverse property of Taggable.
 * @author SG0897954
 *
 */
public class TagPatternExample {
	public static void main(String[] args) throws Exception {
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MINI_RULE_INF);	
		Bean2RDF writer = new Bean2RDF(m);
		
		// two tags
		Tag jb = new Tag();
		jb.setTerm("jenabean");		
		Tag red = new Tag();
		red.setTerm("red");
		
		Post p = new Post();
		p.setTitle("a new way to bind to rdf");
		
		Image i = new Image();
		i.setName("house.jpg");
		i.addTag(jb);  // just one tag
		
		jb.addElement(p);
		p.addTag(red);
		red.addElement(i);
		

		// jb will persist all objects because...
		// jb has element p, which has tag red, which has element i
		writer.saveDeep(jb);
		
		
		// now image and post both have two tags
		// jb and red both have two elements
		// here we'll see the two elements tagged with red, one via inverse inference
		RDF2Bean reader = new RDF2Bean(m);
		Tag red2 = reader.loadDeep(Tag.class, red.getTerm());

		for (Taggable element : red2.getElements()) {
			System.out.println(element.getTags().size());
			if ( element instanceof Post) {
				p = (Post)element;
				System.out.println("Post '" + p.getTitle() + "' was tagged with " + red2.getTerm());
			} else if ( element instanceof Image) {
				i = (Image)element;
				System.out.println("Image '" + i.getName() + " was tagged with " + red2.getTerm());
			}
		}
		
		// and if you just want to see what we've done in the triple store...
		m.write(System.out, "N3");

	}
}

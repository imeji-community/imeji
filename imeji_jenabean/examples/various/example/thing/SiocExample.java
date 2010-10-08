package example.thing;

import thewebsemantic.Thing;
import thewebsemantic.vocabulary.Sioc;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class SiocExample {

	public static void main(String[] args) {
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		m.setNsPrefix("sioc", "http://rdfs.org/sioc/ns#");
		m.read("http://demo.openlinksw.com/tutorial/sioc.vsp");		
		Sioc space = 
			new Thing("http://demo.openlinksw.com/tutorial/", m).
				as(Sioc.class);
		
		System.out.println("found space " + space.name());		
		System.out.println("which has the following Containers...");
		
		for (Thing thing : space.space_of()) {
			Sioc container = thing.as(Sioc.class);
			System.out.println(container.description());
			System.out.println("\tcontains " + container.container_of().size() + " items.");
			Individual i = (Individual)thing.getResource().as(Individual.class);
			System.out.println("\tAKA..." + i.getURI());
		}
		
		
	}
}

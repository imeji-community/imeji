package example;

import static thewebsemantic.binding.Jenabean.load;

import java.util.Collection;

import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class NTNamesExample2 {

	public static void main(String[] args) {
		OntModel m = ModelFactory.createOntologyModel();
		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		m.read("file:src/example/java/example/NTNames.owl");
		OntDocumentManager.getInstance().addAltEntry(
				"http://www.semanticbible.org/2006/11/NTNames.owl",
				"file:src/example/java/example/NTNames.owl");
		m.read("file:src/example/java/example/NTN-individuals.owl");
		Jenabean J = Jenabean.instance();
		J.bind(m);
		J.bindAll("example");
		Collection<Woman> women = load(Woman.class);
		for (Woman woman : women) {
			System.out.println(woman.uri());
		}

		Collection<Man> men = load(Man.class);
		System.out.println(men.size());
		for (Man man : men) {
			System.out.println(man.uri());
		}
	}
}
package example;

import static java.lang.System.out;
import static thewebsemantic.binding.Jenabean.include;

import java.util.Collection;

import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class GeonamesExample {
	
	/**
	 * load up all the cities from capitals.rdf, based on ontology
	 * defined in geonames.owl.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//prepare the jena model
		Model m = ModelFactory.createDefaultModel();
		m.read("file:src/example/java/example/geonames.owl");
		m.read("file:src/example/java/example/capitals.rdf");
		//setup binding
		Jenabean b = Jenabean.instance();
		b.bind(m);
		b.bind(GeonamesVocabulary.Feature).to(City.class);
		Collection<City> cities =  include("alternateNames").load(City.class);
		out.println("Cities in rdf triple store: " + cities.size());
		for (City city : cities) {
			out.println('\n' + city.getName() + ", pop. " + city.getPopulation());
			out.println("\turi: " + city.getUri());
			out.println("\tnumber of translations: " + 
					city.getAlternateNames().size());
		}
	}
}

package example.people;

import thewebsemantic.Bean2RDF;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Main {
	public static void main(String[] args) {
		Address a = new Address();
		a.setCity("Sydney");
		a.setStreet("Main St.");
		Model m = ModelFactory.createDefaultModel();
		Bean2RDF writer = new Bean2RDF(m);
		Person p = new Person("http://jsmith.myopenid.com");
		p.setName("John Smith");
		p.setAddress(a);
		writer.save(p);
		m.write(System.out, "N3");		
		
	}
}

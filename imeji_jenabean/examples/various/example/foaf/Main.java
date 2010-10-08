package example.foaf;

import java.io.IOException;
import java.util.Collection;

import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Main {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		// this will infer subclasses (ie, if Person then also Agent)
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		
		//first load foaf for class hierarchy
		m.read("http://xmlns.com/foaf/spec/index.rdf");
		
		// now load some foaf off the web
		m.read("http://richard.cyganiak.de/foaf.rdf#cygri");
		m.read("http://www.deri.ie/fileadmin/scripts/foaf.php?id=316");
		
		
		RDF2Bean reader = new RDF2Bean(m);
		//bind all annotated classes in this package
		reader.bindAll("example.foaf");
		Collection<Group> groups = reader.loadDeep(Group.class);
		Collection<Person> people = reader.loadDeep(Person.class);
		Collection<Agent> agents = reader.load(Agent.class);
		
		Collection<Group> many = reader.load( Group.class );
		Group one = reader.load(Group.class, "id");
		
		Object o = many;
		o = one;
		
		//m.writeAll(System.out, "N3", null); System.exit(0);
		System.out.println("There are " + people.size() + " People in this graph.");
		System.out.println("There are " + groups.size() + " Groups in this graph.");
		System.out.println("There are " + agents.size() + " Agents in this graph. (people + groups)");
		
		for (Group g : groups) {
			if ( g.getMember().size() > 0) {
				//note these are not "agents", they are instances of Person
				Agent a = g.getMember().iterator().next();
				System.out.println(a.getClass()); //it's a Person!
				if ( a instanceof Person) {
					Person p = (Person)a;
					System.out.println( g.getUri() + " has member " + p.getName());
				}
				
			}
		}
	}

}

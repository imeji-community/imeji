package example.thing;

import java.net.URI;
import java.net.URISyntaxException;

import thewebsemantic.Thing;
import thewebsemantic.vocabulary.Foaf;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class FoafExample3 {

	/**
	 * @param args
	 * @throws URISyntaxException 
	 */
	public static void main(String[] args) throws URISyntaxException {
		/*	
		 * 	Should create identical RDF as 
		from http://wiki.foaf-project.org/DescribingAPerson

		 	<foaf:Person>
				   <foaf:name>David Banner</foaf:name>
				   <foaf:title>Mr</foaf:title>
				   <foaf:firstName>David</foaf:firstName>
				   <foaf:surname>Banner</foaf:surname>
				   <foaf:nick>hulk</foaf:nick>
				   <foaf:homepage rdf:resource="http://www.davidbanner.com"/>
				   <foaf:weblog rdf:resource="http://www.davidbanner.com/blog"/>
				   <foaf:schoolHomepage rdf:resource="http://www.mit.edu"/>
				   <foaf:workplaceHomepage rdf:resource="http://www.gamma-rays-r-us.com"/>
				   <foaf:workInfoHomepage rdf:resource="http://www.gamma-rays-r-us.com/~banner/crazy-experiments.html"/>
				   <!-- etc -->
				</foaf:Person>
				
			
		 	* Note that Jenabean may improve things a little (surname is typed), 
		 	* like being specific and typing its literals. firstName is untyped to show the 
		 	* technique.
		 	*
		 	*/
				Model m = ModelFactory.createDefaultModel(); 
				m.setNsPrefix("foaf","http://xmlns.com/foaf/0.1/");
				m.setNsPrefix("xsd" , "http://www.w3.org/2001/XMLSchema#");
				new Thing("http://example.org/dbanner",m).
					isa(Foaf.Person.class).
					name_("David Banner").
					title("Mr").
					firstName_("David").
					surname("Banner").
					nick("hulk").
					homepage(new URI("http://www.gamma-rays-r-us.com")).
					weblog(new URI("http://www.davidbanner.com/blog")).
					schoolHomepage( new URI("http://www.mit.edu")).
					workplaceHomepage(new URI("http://www.gamma-rays-r-us.com")).
					workInfoHomepage(new URI("http://www.gamma-rays-r-us.com/~banner/crazy-experiments.html"));
				m.write(System.out, "RDF/XML-ABBREV");
			}

	}



package example.thing;

import thewebsemantic.Thing;
import thewebsemantic.vocabulary.Foaf;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * example foaf taken from 
 * http://wiki.foaf-project.org/UsingFoafImage
 * 
 * This example will create an identical set of triples using Jenabean's
 * "Thing" builder combined with the Foaf vocabulary interface.  Vocabulary interfaces
 * are written by hand and should conform to the authors desires for the property's 
 * range.  Typically RDF in the wild overuses untyped literals.  This example shows
 * how you can assert the same property as either a typed or untyped literal.
 * 
 * @see thewebsemantic.vocabulary.Foaf
 */
public class FoafExample2 {
	public static void main(String[] args) {
		/*
		<foaf:Person>
		  <foaf:name>Dan Brickley</foaf:name>
		  <foaf:depiction>
		    <foaf:Image rdf:about="http://rdfweb.org/people/danbri/mugshot/danbri-small.jpeg">
		      <foaf:sha1>7eff189e90a0b21cddb1b490ee39524f3cd2ca3e</foaf:sha1>
		    </foaf:Image>
		  </foaf:depiction>
		</foaf:Person>
		*/
		
		Model m = ModelFactory.createDefaultModel();
		m.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");

		Thing img = new Thing("http://rdfweb.org/people/danbri/mugshot/danbri-small.jpeg", m);
		img.isa(Foaf.Image.class).sha1("7eff189e90a0b21cddb1b490ee39524f3cd2ca3e");
		new Thing(m). // anonymous
			isa(Foaf.Person.class).
			name_("Dan Brickley"). //this one is an untyped Literal
			depiction(img);
		
		// now let's see it with a typed literal
		new Thing(m). // anonymous
			isa(Foaf.Person.class).
			name("Dan Brickley"). //this one is a typed Literal
			depiction(img);
		m.write(System.out, "RDF/XML-ABBREV");
/*
		<foaf:Person>
		  <foaf:name>Dan Brickley</foaf:name>
		  <foaf:depiction>
		    <foaf:Image/>
		  </foaf:depiction>
		</foaf:Person>
		*/
		m = ModelFactory.createDefaultModel();
		m.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		
		img = new Thing(m);
		img.isa(Foaf.Image.class);
		new Thing(m).
			isa(Foaf.Person.class).
			name_("Dan Brickley"). //this one is an untyped Literal
			depiction(img); //anonymous image
		m.write(System.out, "RDF/XML-ABBREV");

		
	}
}

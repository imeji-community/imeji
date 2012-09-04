package test.thing;


import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import org.junit.Test;

import thewebsemantic.Thing;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class TestBasic {
	@Test
	public void basic() throws URISyntaxException {
		Model m = ModelFactory.createDefaultModel();

		m.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
		m.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		
		Thing t = new Thing("http://example.com/1",m);
		
		DublinCore dcThing = t.as(DublinCore.class);
		Thing me = new Thing("http://tcowan.myopenid.com", m);
		
		t.as(DublinCore.class).
			creator("me").
			subject("binding").
			subject("owl").
			subject(1).
			subject(new Date()).
			subject(me).
			subject(new URI("http://www.google.com")).
			title("The web semantic").
		 as(Foaf.class).
			made(new Thing("http://thewebsemantic.com",m)).
		    made(new Thing("http://tripblox.com,",m)).
		    mbox(new Thing("mailto:gorby.kremvax@example.com",m))
			;
		
		System.out.println(dcThing.subject().size());
		for (Literal subject : dcThing.subject()) {
			System.out.println(":" + subject);
		}
		
		for (Thing subject : dcThing.subject_()) {
			System.out.println("thing: " + subject);
		}
		
		for (Thing thing : t.as(Foaf.class).made())
			System.out.println(thing.getResource()); 
		
		t.as(DublinCore.class).description("this is a description");
		//m.write(System.out, "N3");
		System.out.println(t.as(DublinCore.class).description());
		
	}

	@Test
	public void basic2() {
		Model m = ModelFactory.createDefaultModel();

		m.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
		m.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		m.setNsPrefix("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
		Thing t = new Thing("http://example.com/1",m);
		t.as(Foaf.class).
		    aimChatID("example").
			birthday("01/01/1999").
			weblog(new Thing("http://thewebsemantic.com", m)).
			knows(new Thing("http://bblfish.net/people/henry/card#me", m)).
		as(Geo.class).
			lat(33.3f).
			long_(120.1f).
		    isa(Skos.Concept.class).
		as(RdfsVocab.class).
		 	label("an example of polymorphic Thing with 3 vocabularies");
		//m.write(System.out, "N3");
		
	}
	
	@Test
	public void check() {
		Model m = ModelFactory.createDefaultModel();
		Thing me = new Thing("http://tcowan.myopenid.com", m);
		Date d = new Date();
		me.as(DublinCore.class).date(d);
		Date e = me.as(DublinCore.class).date();
		assertEquals(d,e);
		
		me.as(Various.class).age(40).miles(4000).Float(1.1f).Float(2.2f).Double(1.123d).Char('c');
		//m.write(System.out, "N3");
		
		assertEquals(me.as(Various.class).age(), 40);
		assertEquals(me.as(Various.class).miles(), 4000);
		assertEquals(me.as(Various.class).Float().size(), 2);
		float f = me.as(Various.class).Float().iterator().next();
		assertEquals(f, 2.2, 0.001);
		assertEquals(me.as(Various.class).Double(), 1.123d, 0);
		assertEquals(me.as(Various.class).Char(), 'c');
	}
	
	@Test
	public void skos27() {
		Model m = ModelFactory.createDefaultModel();

		m.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
		m.setNsPrefix("skos", "http://www.w3.org/2008/05/skos#");
		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		m.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		m.setNsPrefix("rdfs","http://www.w3.org/2000/01/rdf-schema#");
		Thing t = new Thing("Protein",m);
		t.as(Skos.class).
			definition("A physical entity consisting of a sequence of amino-acids; a protein monomer; a single polypeptide chain. An example is the EGFR protein.", "en");
		
		
		//28, <A> skos:broader <B> ; skos:related <C> .
		Thing a = new Thing("A", m);
		Thing b = new Thing("B", m);
		Thing c = new Thing("C", m);
		a.as(Skos.class).broader(b).related(c);
		
		Thing rocks = new Thing("rocks", m);
		rocks.isa(Skos.Concept.class).
			prefLabel("rocks", "en").
			altLabel("basalt", "en").
			altLabel("granite", "en").
			altLabel("slate", "en");
		
		//<http://www.w3.org/People/Berners-Lee/card#i> rdf:type foaf:Person;
		//	  foaf:name "Timothy Berners-Lee";
		//	  rdfs:label "Tim Berners-Lee";
		//	  skos:prefLabel "Tim Berners-Lee"@en.

		Thing tbl = new Thing("http://www.w3.org/People/Berners-Lee/card#i", m);
		tbl.as(Foaf.class).isa(Foaf.Person.class).
				name("Timothy Berners-Lee").
			as(Skos.class).
				prefLabel("Tim Berners-Lee", "en").
			as(RdfsVocab.class).
				label("Tim Berners-Lee");
		//m.write(System.out, "N3");
	}
	
	@Test
	public void skosPrimer() {
		/*
ex2:catScheme rdf:type skos:ConceptScheme;
   dc:title "The Complete Cat Thesaurus"@en. 

ex1:cats skos:inScheme ex2:catScheme.

ex2:abyssinian rdf:type skos:Concept;
   skos:prefLabel "Abyssinian Cats"@en;
   skos:broader ex1:cats;
   skos:inScheme ex2:catScheme.

ex2:siamese rdf:type skos:Concept;
   skos:prefLabel "Siamese Cats"@en;
   skos:broader ex1:cats;
   skos:inScheme ex2:catScheme.
		 */
		 
		Model m = ModelFactory.createDefaultModel();

		m.setNsPrefix("dc", "http://purl.org/dc/elements/1.1/");
		m.setNsPrefix("skos", "http://www.w3.org/2008/05/skos#");
		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		m.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		m.setNsPrefix("rdfs","http://www.w3.org/2000/01/rdf-schema#");	
		m.setNsPrefix("ex1","http://ex1#");
		m.setNsPrefix("ex2","http://ex2#");
		Thing ex2 = new Thing("http://ex2#catScheme", m);
		ex2.isa(Skos.ConceptScheme.class).
			as(DublinCore.class).
			title("The Complete Cat Thesaurus","en");
		Thing ex1 = new Thing("http://ex1#cats", m);
		ex1.as(Skos.class).
			inScheme(ex2);
		
		new Thing("http://ex2#abyssinian", m).
			isa(Skos.Concept.class).
			prefLabel("Abyssinian Cats", "en").
			broader(ex1).
			inScheme(ex2);
		
		new Thing("http://ex2#siamese", m).
			isa(Skos.Concept.class).
			prefLabel("Siamese Cats", "en").
			broader(ex1).
			inScheme(ex2);		
		//m.write(System.out, "N3");
	}
	
	@Test
	public void foafExamples() throws URISyntaxException {
		
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
		
	
 	* Note that Jenabean may improve things a little, like being specific and typing its literals.
 	*
 	*
 	*/
		Model m = ModelFactory.createDefaultModel(); 
		m.setNsPrefix("foaf","http://xmlns.com/foaf/0.1/");
		m.setNsPrefix("xsd" , "http://www.w3.org/2001/XMLSchema#");
		new Thing("http://example.org/dbanner",m).
			isa(Foaf.Person.class).
			name("David Banner").
			title("Mr").
			firstName("David").
			surname("Banner").
			nick("hulk").
			homepage(new URI("http://www.gamma-rays-r-us.com")).
			weblog(new URI("http://www.davidbanner.com/blog")).
			schoolHomepage( new URI("http://www.mit.edu")).
			workplaceHomepage(new URI("http://www.gamma-rays-r-us.com")).
			workInfoHomepage(new URI("http://www.gamma-rays-r-us.com/~banner/crazy-experiments.html"));
		//m.write(System.out, "RDF/XML-ABBREV");
	}
	
	
	@Test
	public void foafExamples2() throws URISyntaxException {
		
/*	
 * 	Should create identical RDF as 
from http://wiki.foaf-project.org/UsingFoafKnows

 <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
         xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
         xmlns:foaf="http://xmlns.com/foaf/0.1/">
<foaf:Person>
  <foaf:name>Leigh Dodds</foaf:name>
  <foaf:firstName>Leigh</foaf:firstName>
  <foaf:surname>Dodds</foaf:surname>
  <foaf:mbox_sha1sum>71b88e951cb5f07518d69e5bb49a45100fbc3ca5</foaf:mbox_sha1sum>
  <foaf:knows>
    <foaf:Person>
      <foaf:name>Dan Brickley</foaf:name>
      <foaf:mbox_sha1sum>241021fb0e6289f92815fc210f9e9137262c252e</foaf:mbox_sha1sum>
      <rdfs:seeAlso 
        rdf:resource="http://rdfweb.org/people/danbri/foaf.rdf"/>
    </foaf:Person>
  </foaf:knows>
</foaf:Person>
</rdf:RDF>

		
	
 	* Note that Jenabean may improve things a little, like being specific and typing its literals.
 	*
 	*
 	*/
		Model m = ModelFactory.createDefaultModel(); 
		m.setNsPrefix("foaf","http://xmlns.com/foaf/0.1/");
		m.setNsPrefix("xsd" , "http://www.w3.org/2001/XMLSchema#");
		
		Thing danbri = new Thing(m); //anonymous!
		danbri.isa(Foaf.Person.class).
			name("Dan Brickley").
			mbox_sha1sum("241021fb0e6289f92815fc210f9e9137262c252e").
			as(RdfsVocab.class).
			seeAlso(new URI("http://rdfweb.org/people/danbri/foaf.rdf"));
		new Thing(m). //anonymous!
			isa(Foaf.Person.class).
			name("David Banner").
			firstName("David").
			surname("Banner").
			mbox_sha1sum("71b88e951cb5f07518d69e5bb49a45100fbc3ca5").
			knows(danbri);
		//m.write(System.out, "N3");	
		//System.out.println("\n\n-----------------------------------\n\n");
		//m.write(System.out, "RDF/XML-ABBREV");
	}

	
	@Test
	public void foafExamples3() throws URISyntaxException {
		
/*	
 * 	Should create identical RDF as 
from http://wiki.foaf-project.org/UsingFoafKnows

Work Phone: 682-605-2324 

Organization: None Provided 
 <?xml version="1.0" encoding="UTF-8"?> 
<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
        xmlns:foaf="http://xmlns.com/foaf/0.1/" 
        xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#"
        xmlns:dc="http://purl.org/dc/elements/1.1/"> 
  <foaf:Person rdf:ID="Jason">
    <foaf:name>Jason Cook</foaf:name>
    <foaf:title>Mr</foaf:title>
    <foaf:based_near>
      <geo:Point geo:lat="41.8833" geo:long="12.5"/>
    </foaf:based_near>
  </foaf:Person>
</rdf:RDF>


		
	
 	* Note that Jenabean may improve things a little, like being specific and typing its literals.
 	*
 	*
 	*/
		Model m = ModelFactory.createDefaultModel(); 
		m.setNsPrefix("foaf","http://xmlns.com/foaf/0.1/");
		m.setNsPrefix("xsd" , "http://www.w3.org/2001/XMLSchema#");
		m.setNsPrefix("geo" ,"http://www.w3.org/2003/01/geo/wgs84_pos#");
		Thing loc = new Thing(m); //anonymous!
		loc.isa(Geo.Point.class).
			lat(41.8833f).
			long_(12.5f);
	
		new Thing(m). //anonymous!
			isa(Foaf.Person.class).
			name("Jason Cook").
			title("Mr").
			as(Foaf.class).
			based_near(loc);
		//m.write(System.out, "N3");	
		System.out.println("\n\n-----------------------------------\n\n");
		//m.write(System.out, "RDF/XML-ABBREV");
		
	}
	
	@Test
	public void icalExample1() {
		
		/*
		 * 
		<Vevent>
        	<uid>20020630T230445Z-3895-69-1-7@jammer</uid>
        	<dtstart>2002-07-03</dtstart>
        	<dtend>2002-07-06</date>
        	<summary>Scooby Conference</summary>
        	<location>San Francisco</location>
      	</Vevent>
      */
	
		Model m = ModelFactory.createDefaultModel(); 
		m.setNsPrefix("foaf","http://xmlns.com/foaf/0.1/");
		m.setNsPrefix("xsd" , "http://www.w3.org/2001/XMLSchema#");
		m.setNsPrefix("ical" ,"http://www.w3.org/2002/12/cal#");		
		
		Ical.Vevent t = new Thing(m).isa(Ical.Vevent.class);
		t.uid("20020630T230445Z-3895-69-1-7@jammer").
			dtstart("2002-07-03").
			dtend("2002-07-06").
			summary("Scooby Conference").
			location("San Francisco");
			
		//m.write(System.out, "N3");
		
		Literal start = t.dtstart();
		assertEquals("2002-07-03",start.toString());
		assertEquals("San Francisco", t.location().iterator().next());
	}
	
	@Test
	public void reviewExample1() throws MalformedURLException, URISyntaxException {
		/*
	<mm:Album rdf:about="http://mm.musicbrainz.org/album/37b9a29b-2d39-441b-9ac6-81770916e5b5">
      <dc:title>Aftermath</dc:title>
  
      <review:hasReview>
          <review:Review>
              <review:rating>8</review:rating>
              <review:reviewer rdf:nodeID="A0"/>
              <dc:description>Classic.</dc:description>
          </review:Review>
      </review:hasReview>
 
      <dc:creator>
          <mm:Artist rdf:about="http://mm.musicbrainz.org/artist/b071f9fa-14b0-4217-8e97-eb41da73f598"/>
      </dc:creator>
  </mm:Album>
  
  <mm:Artist rdf:about="http://mm.musicbrainz.org/artist/b071f9fa-14b0-4217-8e97-eb41da73f598">
      <dc:title>The Rolling Stones</dc:title>
  </mm:Artist>
		 */
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		ThingFactory factory = new ThingFactory(m);
		m.setNsPrefix("mm","http://www.purl.org/stuff/rev#");
		new Thing("http://example.org/1",m).isa(ReviewVocab.Review.class).
			rating("8").
			reviewer(new URI("http://example.org/reviewers/sam")).
			as(DublinCore.class).
			description("Classic.");
		//m.write(System.out, "RDF/XML-ABBREV");	
		
		Thing example = factory._("http://example.org/1");
		assertEquals(8, example.as(ReviewVocab.class).rating().getLong());
		assertEquals("Classic.", example.as(DublinCore.class).description());
		assertEquals(1, example.as(ReviewVocab.class).reviewer().size());
		Thing t = example.as(ReviewVocab.class).reviewer().iterator().next();
		assertEquals("http://example.org/reviewers/sam",t.getResource().getURI());
	}
	
}

package example.thing;

import thewebsemantic.Thing;
import thewebsemantic.vocabulary.Ical;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class IcalExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
			
		m.write(System.out, "N3");
	}

}

package example.travel;

import java.util.Date;

import thewebsemantic.Bean2RDF;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Model m = ModelFactory.createDefaultModel();
		Bean2RDF writer = new Bean2RDF(m);
		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		m.setNsPrefix("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
		m.setNsPrefix("travel", "http://purl.org/travel/");
		m.setNsPrefix("vevent", "http://www.w3.org/2002/12/cal#");
		Trip t = new Trip();
		writer.save(t);
		
		Item one = new Item();
		one.setTitle("Zen Beach Resort in Kyushu");
		one.setSummary("summary of item");
		one.setFrom(new Date());
		one.setTo(new Date());
		one.setLat(30.3333f);
		one.setLon(30.3333f);
		one.setLink("http://something/interesting.com");
		
		Item two = new Item();
		two.setTitle("Hang gliding from Koyasan");
		two.setSummary("summary of item");
		two.setLat(30.3333f);
		two.setLon(30.3333f);
		two.setFrom(new Date());
		two.setTo(new Date());
		two.setLink("http://something/interesting.com");
		
		t.setAuthor("http://tcowan.myopenid.com");
		t.setTitle("example trip");
		t.setSummary("summary of trip purpose, intend, or theme");
		t.getItems().add(one);
		t.getItems().add(two);
		t.setLat(30.3333f);
		t.setLon(30.3333f);
		writer.save(t);
		m.write(System.out, "N3");
	}

}

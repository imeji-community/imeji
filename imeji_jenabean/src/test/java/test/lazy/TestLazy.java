package test.lazy;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.lazy.LazyList;
import thewebsemantic.lazy.LazySet;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestLazy {

	@Test
	public void basic() {
		Model m = ModelFactory.createDefaultModel();
		RDF2Bean reader = new RDF2Bean(m);
		Bean2RDF writer = new Bean2RDF(m);
		
		Church c = new Church();
		c.id = 1;
		writer.save(c);
		
		ArrayList<Person> staff = new ArrayList<Person>();
		for(int i=0; i<20; i++) {
			Person p = new Person();
			reader.init(p);
			staff.add(p);
		}
		
		c = reader.load(Church.class, 1);
		assertEquals(0, c.staff.size());
		c.staff.addAll(staff);
		c.staff.add(staff.get(0));
		c.staff.add(staff.get(1));
		
		assertEquals(20, c.staff.size());
		writer.save(c);
		
		c = reader.load(Church.class, 1);
		assertEquals(20, c.staff.size());
		c.staff.addAll(staff);
		c.staff.add(staff.get(0));
		c.staff.add(staff.get(1));
		assertTrue(c.staff instanceof LazySet);
		
		
	}
}

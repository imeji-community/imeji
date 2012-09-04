package test.id;

import java.util.Collection;
import java.util.Date;
import static org.junit.Assert.*;

import org.junit.Test;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestWeirdTypes {
	
	@Test
	public void testDate() {
		Model m = ModelFactory.createDefaultModel();
		Bean2RDF writer = new Bean2RDF(m);
		RDF2Bean reader = new RDF2Bean(m);
		
		Date d = new Date();
		Event event = new Event(d);
		event.setLocation("Dallas");
		event.setName("dinner");
		writer.save(event);
		Event event2 = reader.load(Event.class, d);
		assertNotNull(event2);
		assertEquals(d, event2.id());
	}
	
	@Test
	public void testDouble() {
		Model m = ModelFactory.createDefaultModel();
		Bean2RDF writer = new Bean2RDF(m);
		RDF2Bean reader = new RDF2Bean(m);
		Quantity q = new Quantity();
		q.setAmount(1.0002);
		q.setUnits("km");
		writer.save(q);
		Quantity q2 = reader.load(Quantity.class, 1.0002);
		assertNotNull(q2);
	}
	
	@Test
	public void auto() {
		Model m = ModelFactory.createDefaultModel();
		Bean2RDF writer = new Bean2RDF(m);
		RDF2Bean reader = new RDF2Bean(m);
		
		AutoIdMethod bean2 = new AutoIdMethod();
		reader.init(bean2);
		writer.save(bean2);
		
		bean2 = new AutoIdMethod();
		reader.init(bean2);
		writer.save(bean2);
		for(int i=0; i<20; i++) {
			AutoId bean = new AutoId();
			reader.init(bean);
			assertEquals(i, bean.id);
			writer.save(bean);
		}	
		
		Collection<AutoId> ids = reader.load(AutoId.class);
		assertEquals(20, ids.size());
		
		AutoId bean = new AutoId();
		reader.init(bean);
		assertEquals(20, bean.id);
		
		

		writer.n3();
		
	}

}

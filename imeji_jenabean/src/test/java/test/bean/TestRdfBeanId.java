package test.bean;


import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import thewebsemantic.NotFoundException;
import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import static thewebsemantic.binding.Jenabean.*;
import static org.junit.Assert.*;

public class TestRdfBeanId {

	static String remember=null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		OntModel m = ModelFactory.createOntologyModel();
		Jenabean.instance().bind(m);
		
		Company c = new Company();
		c.setEmployees(1023);
		c.setName("Acme");
		c.setTelephone("444-444-4444");
		c.save();		
		
		Company b = new Company();
		b.setEmployees(1023);
		b.setName("Warbucks");
		b.setTelephone("555-555-5555");
		b.save();	
		remember = b.id();
		
		long t1 = System.currentTimeMillis();
		for(int i=0; i<100; i++) {
			new Company().save();
		}
		long t2 = System.currentTimeMillis();
		System.out.println("100 companies inserted in " + (t2-t1) + " milliseconds");

	}
	
	@Test
	public void testBasic() throws NotFoundException {
		long t1 = System.currentTimeMillis();
		Collection<Company> companies = load(Company.class);
		long t2 = System.currentTimeMillis();
		System.out.println("102 companies read in " + (t2-t1) + " milliseconds");

		assertEquals(102, companies.size());
		
		Company c = load(Company.class, remember);
		System.out.println(c.id());
	}

}

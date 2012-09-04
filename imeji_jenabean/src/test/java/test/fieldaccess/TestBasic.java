package test.fieldaccess;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.binding.Jenabean;
import thewebsemantic.lazy.LazySet;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestBasic {
	
	@Test
	public void companies() {
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF);
		Company ibm = new Company();
		ibm.identifier = URI.create("http://www.ibm.com");
		ibm.name = "IBM";
		ibm.industry = Industry.INFORMATION_TECHNOLOGY;
		ibm.dontsaveme = "hi";
		
		Bean2RDF writer = new Bean2RDF(m);
		writer.save(ibm);
		
		RDF2Bean reader = new RDF2Bean(m);
		Company test = reader.load(Company.class, "http://www.ibm.com");
		assertEquals(URI.create("http://www.ibm.com"), test.identifier);
		assertEquals("IBM", test.name);
		assertEquals(Industry.INFORMATION_TECHNOLOGY, test.industry);
		assertEquals(null, test.dontsaveme);
		
		assertNotNull(test.products);
		Product p = new Product();
		p.id = 0;
		p.name = "AIX";
		test.products.add(p);
		writer.save(test);
		
		test = reader.load(Company.class, "http://www.ibm.com");
		assertEquals(1, test.products.size());
		
		for(int i=0; i<10; i++) {
			p = new Product();
			p.id = i+1;
			test.products.add(p);
		}
		writer.save(test);
		test = reader.load(Company.class, "http://www.ibm.com");
		assertEquals(test.products.size(), 11);
		ArrayList<Product> remove = new ArrayList<Product>();
		for(Product prod : test.products) {
			if (prod.id % 2 == 0)
				remove.add(prod);
		}
		test.products.removeAll(remove);
		writer.save(test);
		test = reader.load(Company.class, "http://www.ibm.com");
		assertEquals(5, test.products.size());
		
		for(Product prod : test.products) {
			assertTrue(prod.id % 2 != 0);
		}	
		
		test = reader.loadDeep(Company.class, "http://www.ibm.com");
		assertTrue(test.products.getClass() != LazySet.class);
	}

	@Test
	public void simple() {
		Model m = ModelFactory.createDefaultModel();
		Bean2RDF writer = new Bean2RDF(m);
		GrandsonOfFatAlbert fa = new GrandsonOfFatAlbert();
		fa.grandsonname("grandson");
		fa.name("fieldaccess"); // this comes from the super super class
		fa.dontsaveme = "I don't get saved"; // this one is transient
		fa.id = 0;
		writer.save(fa);
		//m.write(System.out, "N3");
		RDF2Bean reader = new RDF2Bean(m);
		Collection<GrandsonOfFatAlbert> results = reader
				.load(GrandsonOfFatAlbert.class);
		GrandsonOfFatAlbert bean = results.iterator().next();
		assertEquals("fieldaccess", bean.name());
		assertEquals("son", bean.sonname());
		assertEquals("grandson", bean.grandsonname());
		assertNull(bean.dontsaveme);
	}

	@Test
	public void type() {
		Model m = ModelFactory.createDefaultModel();
		m.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		m.setNsPrefix("", "http://test.fieldaccess/");
		m.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
		m.setNsPrefix("xsd","http://www.w3.org/2001/XMLSchema#");
		Jenabean.instance().bind(m);
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 1967);
		for (int i = 0; i < 100; i++) {
			AllTypes bean = new AllTypes(i + "");
			bean.age = 40;
			bean.ssn = 444444444;
			bean.appointment = c;
			bean.birthday = d;
			bean.debt = 1000000.01;
			bean.salary = 1234.56f;
			bean.name = "Tyler Moon";
			bean.nicknames = new String[] { "bob", "doogy", "fats", "mo" };
			bean.nothing = 57;
			bean.version = 3;
			bean.save();
		}
		//m.write(System.out, "N3");
		RDF2Bean reader = new RDF2Bean(m);
		Collection<AllTypes> beans = reader.load(AllTypes.class);
		assertEquals(100, beans.size());
		AllTypes bean2 = beans.iterator().next();
		assertEquals(40, bean2.age);
		assertEquals(444444444, bean2.ssn);
		assertEquals(1000000.01, bean2.debt, 0);
		assertEquals(d, bean2.birthday);
		assertEquals(3, bean2.version);
		//assertEquals(c, bean2.appointment);
	}
}

package test.bean;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;

import thewebsemantic.Bean2RDF;
import thewebsemantic.NotFoundException;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestArrays {

	@Test public void basic() {
		ExampleArraysBean bean = new ExampleArraysBean();
		bean.setId("34fwe");
		Date d1 = new Date();
		Date d2 = new Date();
		Date d3 = new Date();
		Person p1 = new Person();
		p1.setFirstName("Amer");
		Person p2 = new Person();
		p2.setFirstName("Sam");
		Person p3 = new Person();
		p3.setFirstName("Surekha");
		bean.setAges(new int[] {1,2,3,4,5} );
		bean.setNames(new String[] {"bob", "sarah", "jimmy"});
		bean.setTimes(new Date[] {d1, d2, d3});
		bean.setPeople(new Person[] {p1,p2,p3});
		
		OntModel m = ModelFactory.createOntologyModel();	
		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
	
		Bean2RDF writer = new Bean2RDF(m);
		writer.saveDeep(bean);

		RDF2Bean reader = new RDF2Bean(m);
		Collection<ExampleArraysBean>  beans = reader.load(ExampleArraysBean.class);
		assertEquals(1, beans.size());
		
		ExampleArraysBean loadedBean = beans.iterator().next();
		assertEquals(5, loadedBean.getAges().length);
		
		for (Person p: loadedBean.getPeople()) {
			System.out.println(p.getFirstName());
		}
		for (Date d : loadedBean.getTimes()) {
			System.out.println(d);
		}
		
		bean.getAges()[0] = 100;
		writer.save(bean);
	}
	
	@Test
	public void cycles() throws NotFoundException {
		Model model = ModelFactory.createDefaultModel();	
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");	
		Bean2RDF writer = new Bean2RDF(model);
		RDF2Bean reader = new RDF2Bean(model);
		
		Molecule m = new Molecule();
		ArrayList<Molecule> molecules = new ArrayList<Molecule>();
		for(int i=0; i<10; i++)
			molecules.add(new Molecule());
		m.setNeighbors(molecules.toArray(new Molecule[0]));
		writer.saveDeep(m);
		Molecule actual = reader.load(Molecule.class, m.id(), new String[] {"neighbors"});
		assertEquals(10, actual.neighbors.length);

		molecules.remove(0);
		molecules.remove(0);
		molecules.remove(0);
		molecules.remove(0);
	    m.setNeighbors(molecules.toArray(new Molecule[0]));	 
		writer.saveDeep(m);
		actual = reader.loadDeep(Molecule.class, m.id());
		assertEquals(6, actual.neighbors.length);
		
		for (int i=0;i<20;i++)
			molecules.add(new Molecule());
		molecules.add(null);
	    m.setNeighbors(molecules.toArray(new Molecule[0]));	    
	    writer.saveDeep(m);
		actual = reader.loadDeep(Molecule.class, m.id());
		assertEquals(26, actual.neighbors.length);
		
	}
	
	@Test
	public void testStrings() throws NotFoundException {
		OntModel model = ModelFactory.createOntologyModel();	
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");	
		Bean2RDF writer = new Bean2RDF(model);
		RDF2Bean reader = new RDF2Bean(model);
		Molecule m = new Molecule("zephanol");
		m.setSymbols( new String[] {"H", "2", "O", null, "2", "O", "Na"});
		writer.saveDeep(m);
		reader.loadDeep(Molecule.class);
		m.setSymbols( new String[] {"H", "2", "2", "O", "Na"});
		writer.saveDeep(m);
		m = reader.loadDeep(Molecule.class, m.id());
		assertEquals(5, 5);
		ArrayList<String> stuff = new ArrayList<String>();
		
		for(int i=0; i<10; i++) {
			stuff.add("hello");
			m.setSymbols(stuff.toArray(new String[] {}));
			writer.save(m);
		}
	
		m = reader.load(Molecule.class, "zephanol" );
		assertEquals(10, m.getSymbols().length);
		model.write(System.out, "N3");		
		
	} 
	
	@Test
	public void testTypes()  {
		OntModel model = ModelFactory.createOntologyModel();	
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");	
		Bean2RDF writer = new Bean2RDF(model);
		RDF2Bean reader = new RDF2Bean(model);
		Arrayzing one = new Arrayzing();
		one.id = 0;
		one.strings = new String[] {"1", "2", "3", "4"};
		one.chars = new char[] {'a','b','c','d','e','f','g'};
		one.integers = new int[] {0,1,2,3,4,5,6,2345, -45};
		one.longs = new long[] {1, 2l, 3, 4, 1230000, -234};
		one.shorts = new short[] {0, 1, 2, 3, -4};
		one.doubles = new double[] {1.1, 2.2, 3.333, -4.444};
		one.floats = new float[] {3.444f, 1.23f, -123.123f, 0.6667f};
		writer.save(one);
		
		Arrayzing two = reader.load(Arrayzing.class, 0);
		assertArrayEquals(two.strings, new String[] {"1", "2", "3", "4"});
		assertArrayEquals(two.chars,new char[] {'a','b','c','d','e','f','g'} );
		assertArrayEquals(two.integers, new int[] {0,1,2,3,4,5,6,2345, -45});
		assertArrayEquals(two.longs, new long[] {1, 2l, 3, 4, 1230000, -234});
		assertEquals(one.doubles.length, 4);
		assertEquals(one.floats.length, 4);
	}
	
	@Test
	public void testArrayRemove() {
		OntModel model = ModelFactory.createOntologyModel();	
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");	
		Bean2RDF writer = new Bean2RDF(model);
		RDF2Bean reader = new RDF2Bean(model);
		Arrayzing one = new Arrayzing();
		one.id = 0;
		one.strings = new String[] {"1", "2", "3", "4"};
		one.chars = new char[] {'a','b','c','d','e','f','g'};
		one.integers = new int[] {0,1,2,3,4,5,6,2345, -45};
		one.longs = new long[] {1, 2l, 3, 4, 1230000, -234};
		one.shorts = new short[] {0, 1, 2, 3, -4};
		one.doubles = new double[] {1.1, 2.2, 3.333, -4.444};
		one.floats = new float[] {3.444f, 1.23f, -123.123f, 0.6667f};
		writer.save(one);	
		Arrayzing two = reader.load(Arrayzing.class, 0);
		two.integers = null;
		writer.save(two);
		two = reader.load(Arrayzing.class, 0);
		assertNotNull(two.integers);
		assertArrayEquals(two.integers, new int[] {0,1,2,3,4,5,6,2345, -45});
		
		//remove integers from array
		two.integers = new int[] {};
		writer.save(two);
		
		two = reader.load(Arrayzing.class, 0);
		assertNotNull(two.integers);
		assertEquals(0, two.integers.length);
		model.write(System.out, "N3");
	}

	@Test
	public void testTypes2()  {
		OntModel model = ModelFactory.createOntologyModel();	
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");	
		Bean2RDF writer = new Bean2RDF(model);
		RDF2Bean reader = new RDF2Bean(model);
		Arrayzing2 one = new Arrayzing2();
		one.id = 0;
		one.strings = new String[] {"1", "2", "3", "4"};
		one.chars = new char[] {'a','b','c','d','e','f','g'};
		one.integers = new int[] {0,1,2,3,4,5,6,2345, -45};
		one.longs = new long[] {1, 2l, 3, 4, 1230000, -234};
		one.shorts = new short[] {0, 1, 2, 3, -4};
		one.doubles = new double[] {1.1, 2.2, 3.333, -4.444};
		one.floats = new float[] {3.444f, 1.23f, -123.123f, 0.6667f};
		writer.save(one);
		
		Arrayzing2 two = reader.load(Arrayzing2.class, 0);
		assertArrayEquals(two.strings, new String[] {"1", "2", "3", "4"});
		assertArrayEquals(two.chars,new char[] {'a','b','c','d','e','f','g'} );
		assertArrayEquals(two.integers, new int[] {0,1,2,3,4,5,6,2345, -45});
		assertArrayEquals(two.longs, new long[] {1, 2l, 3, 4, 1230000, -234});
		assertEquals(one.doubles.length, 4);
		assertEquals(one.floats.length, 4);
	}
	
	@Test
	public void testArrayRemove2() {
		OntModel model = ModelFactory.createOntologyModel();	
		model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");	
		Bean2RDF writer = new Bean2RDF(model);
		RDF2Bean reader = new RDF2Bean(model);
		Arrayzing2 one = new Arrayzing2();
		one.id = 0;
		one.strings = new String[] {"1", "2", "3", "4"};
		one.chars = new char[] {'a','b','c','d','e','f','g'};
		one.integers = new int[] {0,1,2,3,4,5,6,2345, -45};
		one.longs = new long[] {1, 2l, 3, 4, 1230000, -234};
		one.shorts = new short[] {0, 1, 2, 3, -4};
		one.doubles = new double[] {1.1, 2.2, 3.333, -4.444};
		one.floats = new float[] {3.444f, 1.23f, -123.123f, 0.6667f};
		writer.save(one);	
		Arrayzing2 two = reader.load(Arrayzing2.class, 0);
		two.integers = null;
		writer.save(two);
		two = reader.load(Arrayzing2.class, 0);
		assertNotNull(two.integers);
		assertArrayEquals(two.integers, new int[] {0,1,2,3,4,5,6,2345, -45});
		
		//remove integers from array
		two.integers = new int[] {};
		writer.save(two);
		
		two = reader.load(Arrayzing2.class, 0);
		assertNotNull(two.integers);
		assertEquals(0, two.integers.length);
		model.write(System.out, "N3");
	}
}

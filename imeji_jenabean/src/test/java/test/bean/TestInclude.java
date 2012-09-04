package test.bean;


import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import thewebsemantic.NotFoundException;
import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import static org.junit.Assert.*;
import static thewebsemantic.binding.Jenabean.*;


public class TestInclude {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		OntModel m = ModelFactory.createOntologyModel();
		Jenabean b = Jenabean.instance();
		b.bind(m);
	}
	
	@Test
	public void testBasic() throws NotFoundException {
		FatBean fat = new FatBean();
		fat.save();
		
		fat = include("hamburgers").load(FatBean.class, fat.id());
		fat.getBeers().add("Carona");
		fat.getFries().add("french");
		fat.getHamburgers().add("double");
		fat.getHamburgers().add("single");
		fat.getHamburgers().add("cheeze");
		fat.getShakes().add("chocolate");
		fat.getSteaks().add("t-bone");
		fat.save();
		
		Collection<FatBean> beans = include("beers").include("shakes").load(FatBean.class);
		fat = beans.iterator().next();
		assertEquals(3, fat.getHamburgers().size());
		assertEquals(1, fat.getShakes().size());
		assertEquals(1, fat.getBeers().size());
		assertEquals(1, fat.getFries().size());
		
		fat.save();
		beans = include("hamburgers").load(FatBean.class);
		fat = beans.iterator().next();
		assertEquals(3, fat.getHamburgers().size());
		
	}
}

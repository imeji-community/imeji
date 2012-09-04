package test.bean;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import thewebsemantic.NotFoundException;
import thewebsemantic.Sparql;
import thewebsemantic.binding.Jenabean;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestSparqlBind {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		OntModel m = ModelFactory.createOntologyModel();
		Jenabean.instance().bind(m);
	}
	
	@Test
	public void test() throws NotFoundException {
		Man man = new Man("http://people/cslewis");
		man.setDescription("C.S. Lewis authored the Lion, Witch, and the Wardrobe");
		man.setName("C.S. Lewis");
		man.save();
		
		FatBean fat = new FatBean();
		fat.save();
		fat = fat.load(fat.id());
		fat.getBeers().add("Coors");
		fat.getBeers().add("Corona");
		fat.getBeers().add("Bud Light");
		fat.save();

		FatBean fat2 = new FatBean();
		fat2.save();
		fat2 = fat2.load(fat2.id());
		fat2.getBeers().add("Coors");
		fat2.getBeers().add("Corona");
		fat2.getBeers().add("Bud Light");
		fat2.save();		

		Jenabean jb = Jenabean.instance();
		Model m = jb.model();
		String queryString = 
			"SELECT ?s WHERE { ?s a <http://test.bean/Man> }";

		Collection<Man> result = Sparql.exec( m, Man.class, queryString);
		assertEquals(1, result.size());
		Man man2 = result.iterator().next();
		assertEquals(man.getName(), man2.getName());
		assertEquals(man.getDescription(), man2.getDescription());

		queryString = 
			"SELECT ?s WHERE { ?s a <http://test.bean/FatBean> }";
		Collection<FatBean> result2 = Sparql.exec(m, FatBean.class, queryString);
		assertEquals(2, result2.size());
		FatBean bean = result2.iterator().next();
		assertEquals(3, bean.getBeers().size());
	}
}

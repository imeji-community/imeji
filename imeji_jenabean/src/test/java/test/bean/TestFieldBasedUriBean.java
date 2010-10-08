package test.bean;

import java.util.Collection;

import org.junit.Test;
import static org.junit.Assert.*;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

public class TestFieldBasedUriBean {
	@Test
	public void basic() {
		FieldBasedUriBean bean1 = new FieldBasedUriBean();
		bean1.setCube(127);
		bean1.setFloor(2);
		
		FieldBasedUriBean bean2 = new FieldBasedUriBean();
		bean2.setCube(127);
		bean2.setFloor(2);
		
		
		OntModel m = ModelFactory.createOntologyModel();	
		Bean2RDF writer = new Bean2RDF(m);
		writer.save(bean1);
		writer.save(bean2);

		
		RDF2Bean reader = new RDF2Bean(m);
		Collection<FieldBasedUriBean> results =  reader.load(FieldBasedUriBean.class);
		assertEquals(1, results.size());
		
	}
}

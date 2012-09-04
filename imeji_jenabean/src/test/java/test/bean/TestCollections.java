package test.bean;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestCollections {
	@Test
	public void testIt() throws Exception {
		OntModel m = ModelFactory.createOntologyModel();	
		final Bean2RDF writer = new Bean2RDF(m);
		SomeBean bean = new SomeBean();
		List<String> stringValues = Arrays.asList(new String[] {"value1", "value2", "value3"});
		bean.setStringList(stringValues);
		bean.setId("1");
		String[] values = { "one", "two", "three" };
		bean.setStringArray(values);
		writer.save(bean);		
		RDF2Bean reader = new RDF2Bean(m);
		SomeBean bean2 = reader.loadDeep(SomeBean.class, "1");
		for (String s :bean2.getStringList()) {
			System.out.println(s);			
		}
		System.out.println(bean2.getStringArray()[0]);
		System.out.println(bean2.getStringArray()[1]);
		System.out.println(bean2.getStringArray()[2]);
	}
}

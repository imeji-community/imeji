package test.bean;

import org.junit.Test;

import thewebsemantic.Bean2RDF;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class TestIndexedProperties {

	@Test
	public void testBasic() {
		IndexedPropertyBean bean = new IndexedPropertyBean();
		String[] tags = {"tag", "bag", "hag"};
		bean.setTags(tags);
		OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RDFS_INF);	
		Bean2RDF writer = new Bean2RDF(m);
		writer.save(bean);
	}
}

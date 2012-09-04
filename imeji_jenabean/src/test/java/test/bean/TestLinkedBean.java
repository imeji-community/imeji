package test.bean;

import org.junit.Test;

import thewebsemantic.Bean2RDF;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;



public class TestLinkedBean {

	@Test
	public void testBasic() {
		Model m = ModelFactory.createDefaultModel();
		Bean2RDF writer = new Bean2RDF(m);
		LinkedBean bean = new LinkedBean();
		bean.setMyFriend(m.createResource("http://foo.bar/wiggle/wong"));
		//writer.save(bean);
		//m.write(System.out, "N3");
	}
}

package test.lang;

import java.io.InputStream;
import java.util.Collection;

import org.junit.Test;

import thewebsemantic.LocalizedString;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class TestLang {

	@Test
	public void basic() {
		
		InputStream in = getClass().getResourceAsStream("/london.rdf");
		if (in == null)
			in = getClass().getResourceAsStream("london.rdf");

		Model m = ModelFactory.createDefaultModel();
		m.read(in, null);

		RDF2Bean reader = new RDF2Bean(m);
		reader.bindAll("test.lang");

		Collection<Feature> locations = reader.load(Feature.class);
		for (Feature feature : locations) {
			for (LocalizedString name : feature.alternateName) {
				System.out.println(name.getLang() + ":" + name);
			}
		}

	}
}

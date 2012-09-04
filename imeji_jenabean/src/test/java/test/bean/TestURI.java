package test.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import thewebsemantic.Bean2RDF;
import thewebsemantic.NotFoundException;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.DC;

public class TestURI {
	@Test
	public void testSimple() throws URISyntaxException, NotFoundException {
		Model m = ModelFactory.createDefaultModel();
		Bean2RDF writer = new Bean2RDF(m);
		URITestBean bean = new URITestBean();
		bean.setUri(new URI("http://foo.com"));
		bean.setSecondaryURI(new URI("http://second.example"));
		bean.setName("test bean");
		bean.setId("1");
		writer.save(bean);
		//m.write(System.out, "N3");

		RDF2Bean reader = new RDF2Bean(m);
		bean = reader.load(URITestBean.class, "1");
		URI foo = new URI("http://foo.com");
		URI second = new URI("http://second.example");
		assertEquals(foo, bean.getUri());
		assertEquals(second, bean.getSecondaryURI());
	}

	@Test
	public void testUriCollectionField() {
		BeanWithCollectionOfUris beanWithCollectionOfUris = new BeanWithCollectionOfUris();
		beanWithCollectionOfUris.setId("1234");
		Collection<URI> uriCollection = new ArrayList<URI>();
		URI personUri = URI.create("http://xmlns.com/foaf/0.1/Person");
		uriCollection.add(personUri);
		URI documentUri = URI.create(DC.NS + "Document");
		uriCollection.add(documentUri);
		beanWithCollectionOfUris.setUriCollection(uriCollection);

		// save the beanWithUri
		Model memoryModel = ModelFactory.createDefaultModel();
		Bean2RDF writer = new Bean2RDF(memoryModel);
		writer.save(beanWithCollectionOfUris);
		//memoryModel.write(System.out, "N3");
		RDF2Bean reader = new RDF2Bean(memoryModel);
		BeanWithCollectionOfUris reconstitutedBean = null;
		try {
			reconstitutedBean = reader
					.load(BeanWithCollectionOfUris.class, "1234", new String[] {"uriCollection"});

		} catch (NotFoundException e) {
			System.out.println("Error reconstituting BeanWithCollectionOfUris");
			e.printStackTrace();
			assertTrue(false);
		}

		Collection<URI> reconstitutedUris = reconstitutedBean
				.getUriCollection();
		System.out
				.println("reconstituted Collection<URI>=" + reconstitutedUris);
		assertTrue(reconstitutedUris.size() == 2);
		assertTrue(reconstitutedUris.contains(personUri));
		assertTrue(reconstitutedUris.contains(documentUri));
	}

}

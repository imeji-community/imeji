package example.guide;

import java.net.Proxy.Type;
import java.util.Collection;

import thewebsemantic.Bean2RDF;
import thewebsemantic.NotFoundException;
import thewebsemantic.RDF2Bean;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Model m = ModelFactory.createDefaultModel();
		m.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		m.setNsPrefix("", "http://example.org/");
		m.setNsPrefix("dc", "http://purl.org/dc/terms/");
		Bean2RDF writer = new Bean2RDF(m);
		Book paradoxOfChoice = new Book();
		paradoxOfChoice.setIsbn("978-0060005689");
		paradoxOfChoice.setTitle("The Paradox of Choice");
		writer.save(paradoxOfChoice);
		writer.save(Type.HTTP);
		m.write(System.out, "N3");
		
		RDF2Bean reader = new RDF2Bean(m);
		try {
			Book book = reader.load(Book.class, "978-0060005689");
			Collection<Book> books = reader.load(Book.class);
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
		
	}
}

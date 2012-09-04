package test.driven;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;

import com.hp.hpl.jena.rdf.model.ModelFactory;

import thewebsemantic.NotFoundException;
import thewebsemantic.binding.Jenabean;

public class Main {

	@Test
	public void main() throws NotFoundException {
		Jenabean.instance().bind(ModelFactory.createDefaultModel());
		Person p = new Person();
		p.setLang(Language.KOREAN);
		ArrayList<Continent> visited = new ArrayList<Continent>();
		visited.add(Continent.ASIA);
		visited.add(Continent.NORTH_AMERICA);
		visited.add(Continent.SOUTH_AMERICA);
		p.setVisited(visited);
		p.save();
		
		Person p1 = p.load(p.id());
		assertEquals(Language.KOREAN, p1.getLang());
		//Jenabean.instance().model().write(System.out, "N3");
		p1.fill("visited");
		assertEquals(3, p1.getVisited().size());
	}
}

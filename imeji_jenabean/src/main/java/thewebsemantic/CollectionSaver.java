package thewebsemantic;

import java.util.ArrayList;
import java.util.Collection;

import thewebsemantic.lazy.Lazy;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class CollectionSaver extends Saver {

	@Override
	public void save(Bean2RDF writer, Resource subject, Property property, Object o) {
		Collection c = (Collection)o;
		if (c == null)
			return;
		
		if (o instanceof Lazy) {
			Lazy lazy = (Lazy)o;
			if (!  (lazy.isConnected() && lazy.modified()))
				return;
		}

		removeAnonymousNodes(subject, property);
		subject.removeAll(property);
		for (Object obj : c)
			subject.addProperty(property, writer.toRDFNode(obj));
	}
	
	
	private void removeAnonymousNodes(Resource subject, Property property) {
		ArrayList<Resource> anonNodes = new ArrayList<Resource>();
		StmtIterator it = subject.listProperties(property);
		while (it.hasNext()) {
			RDFNode n = it.nextStatement().getObject();
			if (n.isAnon())
				anonNodes.add(n.as(Resource.class));
		}
		it.close();
		for (Resource resource : anonNodes)
			resource.removeProperties();
	}

}

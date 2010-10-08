package thewebsemantic;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class ResourceSaver extends Saver {

	@Override
	public void save(Bean2RDF writer, Resource subject, Property property, Object o) {
		if (o==null) {
			subject.removeAll(property);
			return;
		}
		Model m = subject.getModel();
		subject.removeAll(property).addProperty(property,
				m.getResource(o.toString()));
	}

}

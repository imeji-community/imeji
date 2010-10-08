package thewebsemantic;

import java.lang.reflect.Array;
import java.util.List;

import thewebsemantic.lazy.Lazy;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class ListSaver extends Saver {

	@Override
	public void save(Bean2RDF writer, Resource subject, Property property, Object o) {
		// we will not remove children unless we get a 0 length list
		if ( o==null)
			return;
		else if (o instanceof Lazy && ! ((Lazy)o).isConnected()) {
			return;
		} else if (o instanceof Lazy && ! ((Lazy)o).modified()) {
			return;
		}
		Saver.of(Array.class).save(writer, subject, property, ((List)o).toArray());

	}

}

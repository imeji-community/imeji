package thewebsemantic;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public abstract class Saver {

	private static Map<Class<?>, Saver> lookup = new HashMap<Class<?>, Saver>() ;
	
	static {
		lookup.put(thewebsemantic.Resource.class, new ResourceSaver());
		lookup.put(Collection.class, new CollectionSaver());
		lookup.put(Set.class, new CollectionSaver());
		lookup.put(List.class, new ListSaver());
		lookup.put(Array.class, new ArraySaver());
		lookup.put(URI.class, new ResourceSaver());
	}
	
	public static boolean supports(Class<?> type) {
		return (type.isArray()) ? true : lookup.containsKey(type);
	}

	public static Saver of(Class<?> type) {
		return (type.isArray()) ? lookup.get(Array.class) : lookup.get(type);
	}

	public abstract void save(Bean2RDF writer, Resource subject, Property property, Object o);
}

package thewebsemantic.binder;

import com.hp.hpl.jena.ontology.OntClass;

public interface Binder {
	public boolean isBound(Class<?> c);
	public boolean isBound(OntClass c);	
	public String getUri(Class<?> c);
	public String getUri(Object bean);	
	public Class<?> getClass(String uri);
	public void save(Class<?> javaClass, String ontClass);
}

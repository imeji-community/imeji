package thewebsemantic.binder;

import java.util.HashMap;

import com.hp.hpl.jena.ontology.OntClass;


public class BinderImp implements Binder {
	
	private static Binder myself = new BinderImp();
	private HashMap<Class<?>, String> class2url;
	private HashMap<String, Class<?>> url2class;
	
	public BinderImp() {
		class2url = new HashMap<Class<?>, String>();
		url2class = new HashMap<String, Class<?>>();
	}
	
	public boolean isBound(Class<?> c) {
		return class2url.containsKey(c);
	}
	
	public boolean isBound(OntClass c) {
		return url2class.containsKey(c.getURI());
	}
	
	public String getUri(Class<?> c) {
		return class2url.get(c);
	}
	
	public String getUri(Object bean) {
		return getUri(bean.getClass());
	}
	
	public Class<?> getClass(String uri) {
		return url2class.get(uri);		
	}
	
	public void save(Class<?> javaClass, String ontClass) {
		class2url.put(javaClass, ontClass);
		url2class.put(ontClass, javaClass);
	}
	
	public static Binder instance() { return myself;}
}

package thewebsemantic;

import static thewebsemantic.Bean2RDF.logger;
import static thewebsemantic.Util.last;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;

import thewebsemantic.binding.Persistable;


import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Retrieves annotation information as well as other type related operations on
 * Classes. Keeps cached versions to minimize introspection work.
 * 
 */
public abstract class TypeWrapper {
	public static final String JENABEAN_PREFIX = "jenabean.prefix";
	private String NS;
	protected Class<?> c;
	protected BeanInfo info;
	protected Constructor<?> constructor;
	protected PropertyDescriptor[] descriptors;
	private static HashMap<Class<?>, TypeWrapper> cache = new HashMap<Class<?>, TypeWrapper>();
	private String prefix = null;

	protected <T> TypeWrapper(Class<T> c) {
		prefix = System.getProperty(JENABEAN_PREFIX);
		this.c = c;
		info = beanInfo(c);
		Namespace nsa = c.getAnnotation(Namespace.class);
		NS = (nsa != null) ? nsa.value() : getNamespaceFromPackage(c);
		try {
			constructor = c.getConstructor(String.class);
		} catch (Exception e) {}
		cache.put(c, this);
	}

	private String getNamespaceFromPackage(Class<?> c) {
		return (c.getPackage() == null) ? "http://default.package/" : "http://"
				+ c.getPackage().getName() + '/';
	}

	public static synchronized TypeWrapper type(Object o) {
		if (o instanceof Persistable)
			return wrap(o.getClass().getSuperclass());
		else
			return wrap(o.getClass());
	}

	public static String getId(Object o) {
		return type(o).id(o);
	}

	public static ValuesContext[] valueContexts(Object o) {
		return type(o).getValueContexts(o);
	}
	
	public ValuesContext[] getValueContexts(Object o) {
		
		
		ArrayList<ValuesContext> values = new ArrayList<ValuesContext>();		
		for (PropertyDescriptor property : descriptors()) {
			if ( property.getReadMethod().isAnnotationPresent(Transient.class))
				continue;
			values.add( new PropertyContext(o, property) );			
		}
		return values.toArray(new ValuesContext[0]);

	}

	public ValuesContext getProperty(String name) {
		for (PropertyDescriptor p : descriptors()) {
			if (p.getName().equals(name))
				return new NullPropertyContext(this, p);
		}
		return null;
	}

	public static synchronized TypeWrapper wrap(Class<?> c) {
		return (cache.containsKey(c)) ? cache.get(c) : TypeWrapperFactory.newwrapper(c);
	}

	public String typeUri() {
		return NS + Util.getRdfType(c);
	}

	public static String typeUri(Class<?> c) {
		return wrap(c).typeUri();
	}

	protected PropertyDescriptor[] descriptors() {
		if (descriptors == null) {
			Collection<PropertyDescriptor> results = new LinkedList<PropertyDescriptor>();
			for (PropertyDescriptor p : info.getPropertyDescriptors())
				if (p.getWriteMethod() != null && p.getReadMethod() != null)
					results.add(p);
			descriptors = results.toArray(new PropertyDescriptor[0]);
		}
		return descriptors;
	}

	public String[] collections() {
		Collection<String> results = new LinkedList<String>();
		for (PropertyDescriptor p : info.getPropertyDescriptors())
			if (p.getWriteMethod() != null
					&& p.getPropertyType().equals(Collection.class))
				results.add(p.getName());
		return results.toArray(new String[0]);
	}

	public String namespace() {
		return NS;
	}

	public abstract String uri(String id);

	public String uri(Object bean) {
		return uri(id(bean));
	}

	public String uri(AccessibleObject m, String name) {
		RdfProperty rdf = getRDFAnnotation(m);
		return ("".equals(rdf.value())) ? namingPatternUri(name) : rdf.value();
	}

	protected static RdfProperty getRDFAnnotation(AccessibleObject m) {
		return (m.isAnnotationPresent(RdfProperty.class)) ? m
				.getAnnotation(RdfProperty.class) : new NullRdfProperty();
	}

	private String namingPatternUri(String name) {
		return namespace() + prefix(name);
	}

	private String prefix(String p) {
		return (prefix != null) ? prefix + Util.toProperCase(p) : p;
	}

	public static String instanceURI(Object bean) {
		return type(bean).uri(bean);
	}


	/**
	 * Reterns the ID
	 * 
	 * @param bean
	 * @return
	 */
	public abstract String id(Object bean);

	protected static BeanInfo beanInfo(Class<?> c) {
		try {
			return Introspector.getBeanInfo(c);
		} catch (IntrospectionException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	protected String invokeMethod(Object bean, Method me) {
		try {
			Object o = me.invoke(bean);
			return (o==null) ? null: o.toString();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Failed invoking method " + 
					me.getName() + " on class " + bean.getClass() , e);
		}
		return null;
	}



	/**
	 * 
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public final Object toBean(Resource source) {
		return toBean(source.getURI());
	}

	public Object toBean(String uri) {
		try {
			// last gets the id off the end of the URI
			return (constructor != null) ? constructor.newInstance(last(uri))
					: c.newInstance();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception caught while invoking default constructor on " + c, e);
		}
		return null;
	}

	public abstract Object toProxyBean(Resource source, AnnotationHelper jpa);

}

package thewebsemantic;

import static thewebsemantic.Bean2RDF.logger;

import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;

import com.hp.hpl.jena.rdf.model.Resource;

public class IdMethodTypeWrapper extends TypeWrapper {

	private Method idReadMethod;
	private Method idWriteMethod;

	/*
	 * if true this type wrapper services a class with id of type java.lang.URI
	 */
	private boolean uriid = false;
	private Constructor<?> uriConstructor;

	public IdMethodTypeWrapper(Class<?> c, Method m) {
		super(c);
		idReadMethod = m;
		uriid = URI.class.equals(idReadMethod.getReturnType());

		if (uriid) {
			// get the URI constructor if it exists
			try {
				uriConstructor = c.getConstructor(URI.class);
			} catch (NoSuchMethodException e) {}
		}
		
		// now get the id write method property
		PropertyDescriptor[] props = descriptors();
		for (PropertyDescriptor propertyDescriptor : props)
			if (idReadMethod.equals(propertyDescriptor.getReadMethod()))
				idWriteMethod = propertyDescriptor.getWriteMethod();

	}
	
	@Override
	public String uri(String id) {
		if (uriid)
			return id;
		else
			return typeUri() + '/' + urlencode(id);
	}

	private String urlencode(String id) {
		try {
			return URLEncoder.encode(id, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String id(Object bean) {
		return invokeMethod(bean, idReadMethod);
	}

	public ValuesContext[] getValueContexts(Object o) {
		ArrayList<ValuesContext> values = new ArrayList<ValuesContext>();		
		for (PropertyDescriptor property : descriptors()) {
			if (uriid && idReadMethod.equals(property.getReadMethod()))
				continue;
			if ( property.getReadMethod().isAnnotationPresent(Transient.class))
				continue;
			boolean idmethod = idReadMethod.equals(property.getReadMethod());
			values.add( new PropertyContext(o, property, idmethod) );			
		}
		return values.toArray(new ValuesContext[0]);
	}

	public Object toBean(String uri) {
		try {
			if (uriid && uriConstructor != null)
				return uriConstructor.newInstance(URI.create(uri));
			else if (uriid && idWriteMethod != null) {
				Object obj = c.newInstance();
				idWriteMethod.invoke(obj,URI.create(uri));
				return obj;
			} else
				return super.toBean(uri);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error instantiating bean.", e);
		}
		return null;
	}
	
	public Object toProxyBean(Resource source, AnnotationHelper jpa) {
		try {
			Class cls = jpa.getProxy(c);
			Object obj = cls.newInstance();
			if (uriid && idWriteMethod != null)
				idWriteMethod.invoke(obj,URI.create(source.getURI()));
			return obj;
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception caught while invoking default constructor on " + c, e);
		}
		return null;
	}

}

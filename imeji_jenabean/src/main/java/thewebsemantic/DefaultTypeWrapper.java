package thewebsemantic;

import static thewebsemantic.Bean2RDF.logger;

import java.util.logging.Level;

import com.hp.hpl.jena.rdf.model.Resource;

public class DefaultTypeWrapper extends TypeWrapper {

	public DefaultTypeWrapper(Class<?> c) {
		super(c);
	}
	
	@Override
	public String uri(String id) {
		return typeUri() + '/' + id;
	}

	public String id(Object bean) {
		return String.valueOf(bean.hashCode());
	}

	@Override
	public Object toProxyBean(Resource source, AnnotationHelper jpa) {
		try {
			return jpa.getProxy(c).newInstance();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Exception caught while invoking default constructor on " + c, e);
		}
		return null;
	}



}

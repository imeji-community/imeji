package thewebsemantic;

import static thewebsemantic.Bean2RDF.logger;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.logging.Level;


import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;

class FieldContext extends ValuesContext {

	Field field;
	TypeWrapper type;
	boolean idField;

	public FieldContext(Object bean, Field p, boolean id) {
		subject = bean;
		field = p;
		//if (subject==null)
			type =TypeWrapper.wrap(p.getDeclaringClass());
		//else
		//	type = TypeWrapper.type(bean);
		idField = id;
	}
	
	public String uri() {
		return type.uri(field, field.getName());
	}
	
	public boolean isSymmetric() {
		return isSymmetric(field);
	}

	private boolean isSymmetric(Field p) {
		return (field.isAnnotationPresent(Symmetric.class)) ? true
				: TypeWrapper.getRDFAnnotation(field).symmetric();
	}
	
	public boolean isInverse() {
		return ! "".equals(TypeWrapper.getRDFAnnotation(field).inverseOf());
	}
	
	public String inverseOf() {
		return inverseOf(field);
	}

	public boolean existsInModel(Model m) {
		return m.getGraph().contains( Node.createURI( uri() ), Node.ANY, Node.ANY );
	}
	
	
	public Object invokeGetter() {
		Object result=null;
		try {
			if (! field.isAccessible() )
				field.setAccessible(true);
			result = field.get(subject);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error retrieving field value.", e);
		}
		return result;
	}
	
	public void setProperty(Object v) {
	   try {
		 field.setAccessible(true);
         field.set(subject, v);
      } catch (Exception e) {
    	  logger.log(Level.WARNING, "Could not set bean field " + field.getName(), e);
      }
	}
	
	public boolean isDate() {
	   return type().equals(Date.class);
	}
	
	public boolean isPrimitive() {
	   return PrimitiveWrapper.isPrimitive(field.getType());
	}
	
    public Class<?> type() {
    	return field.getType();
    }

	public String getName() {
		return field.getName();
	}

	public Class<?> t() { 
		return getGenericType((ParameterizedType) field.getGenericType());
	}

	@Override
	public boolean isTransitive() {
		return isTransitive(field);
	}

	@Override
	public boolean isId() {
		return idField;
	}

	@Override
	public AccessibleObject getAccessibleObject() {
		return field;
	}
	
}

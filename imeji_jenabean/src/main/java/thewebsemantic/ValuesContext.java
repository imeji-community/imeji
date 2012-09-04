package thewebsemantic;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import thewebsemantic.Base.NullType;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;

public abstract class ValuesContext {

	protected Object subject;

	public abstract AccessibleObject  getAccessibleObject();
	
	public abstract String uri();
	
	public abstract boolean isId();

	public abstract boolean isSymmetric();

	public abstract boolean isTransitive();
	
	public abstract boolean isInverse();
	
	public abstract String inverseOf();

	public abstract boolean existsInModel(Model m);

	public abstract Object invokeGetter();

	public abstract void setProperty(Object v);

	public abstract boolean isPrimitive();

    public boolean isCollection() {
       return type().equals(Collection.class);
    }
    
    public boolean isCollectionOrSet() {
    	return isCollection() || isSet();
    }

    public boolean isSet() {
        return type().equals(Set.class);
    }
    
    public boolean isCollectionType() {
		return Collection.class.isAssignableFrom(type());
	}

	public boolean isURI() {
	   return type().equals(URI.class);
	}
	
	public abstract String getName();

	public abstract Class<?> type();

	public abstract Class<?> t();
	
	public Class<?> getGenericType(ParameterizedType type) {
		return (type == null) ? NullType.class : (Class<?>) type
				.getActualTypeArguments()[0];		
	}

	public boolean isTransitive(AccessibleObject o) {
		return TypeWrapper.getRDFAnnotation(o).transitive();
	}

	public boolean isInverse(AccessibleObject o) {
		String inverseProperty = 
			TypeWrapper.getRDFAnnotation(o).inverseOf();
		return !"".equals(inverseProperty);
	}
	
	public String inverseOf(AccessibleObject o) {
		return TypeWrapper.getRDFAnnotation(o).inverseOf();
	}

	public Property property(Model m) { 
		return m.getProperty(uri());
	}

	public boolean isList() {
		return type().equals(List.class);
	}
	
	public boolean isAggregateType() {
		return isCollectionType() || isArray();
	}

	public boolean isArray() {
		return type().isArray();
	}
	
	public String toString() {return getName();}


}
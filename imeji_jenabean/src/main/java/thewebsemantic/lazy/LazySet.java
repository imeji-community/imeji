package thewebsemantic.lazy;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


import com.hp.hpl.jena.rdf.model.Resource;

@SuppressWarnings("unchecked")
public class LazySet implements Set, Lazy {

	private transient Resource i;
	private transient Provider reader;
	private Set data;
	private String propertyUri;
	private Class type;
	private boolean modified = false;

	public LazySet(Resource i, String propertyUri, Class type, Provider r2b) {
		this.i = i;
		this.propertyUri = propertyUri;
		this.type = type;
		reader = r2b;
	}

	private Set data() {
		if (data == null)
			data = reader.lazySet(i, propertyUri, type); 
		return data; 
	}
	

	public boolean add(Object e) {
		modified = true;
		return data().add(e);
	}

	public boolean addAll(Collection c) {
		modified = true;
		return data().addAll(c);
	}

	public void clear() {
		modified = true;
		data().clear();
	}

	public boolean contains(Object o) {
		return data().contains(o);
	}

	public boolean containsAll(Collection c) {
		return data().containsAll(c);
	}

	public boolean isEmpty() {
		return data().isEmpty();
	}

	public Iterator iterator() {
		return data().iterator();
	}

	public boolean remove(Object o) {
		modified = true;
		return data().remove(o);
	}

	public Object remove(int index) {
		modified = true;
		return data().remove(index);
	}

	public boolean removeAll(Collection c) {
		modified = true;
		return data().removeAll(c);
	}

	public boolean retainAll(Collection c) {
		modified = true;
		return data().retainAll(c);
	}

	public int size() {
		return data().size();
	}

	public Object[] toArray() {
		return data().toArray();
	}

	public Object[] toArray(Object[] a) {
		return data().toArray(a);
	}

	public boolean isConnected() {
		return data != null;
	}

	public boolean modified() {
		return modified;
	}

    public void connect()
    {
       data();
    }

}

package thewebsemantic.lazy;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.hp.hpl.jena.rdf.model.Resource;


@SuppressWarnings("unchecked")
public class LazyList implements List, Lazy {

	private transient Resource subject;
	private transient Provider reader;
	private List data;
	private Class type;
	private String propertyUri;
	private boolean modified = false;
	
	public LazyList(Resource i, String propertyUri, Class type, Provider r2b) {
		this.subject = i;
		this.propertyUri = propertyUri;
		this.type = type;
		reader = r2b;
	}

	private List data() {
		if ( data == null)
			data = reader.lazyList(subject, propertyUri, type);
		return data;
	}

	public void add(int index, Object element) {
		modified = true;
		data().add(index, element);
	}

	public boolean add(Object e) {
		modified = true;
		return data().add(e);
	}

	public boolean addAll(Collection c) {
		modified = true;
		return data().addAll(c);
	}

	public boolean addAll(int index, Collection c) {
		modified = true;
		return data().addAll(index, c);
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

	public boolean equals(Object o) {
		return data().equals(o);
	}

	public Object get(int index) {
		return data().get(index);
	}

	public int hashCode() {
		return data().hashCode();
	}

	public int indexOf(Object o) {
		return data().indexOf(o);
	}

	public boolean isEmpty() {
		return data().isEmpty();
	}

	public Iterator iterator() {
		return data().iterator();
	}

	public int lastIndexOf(Object o) {
		return data().lastIndexOf(o);
	}

	public ListIterator listIterator() {
		return data().listIterator();
	}

	public ListIterator listIterator(int index) {
		return data().listIterator(index);
	}

	public Object remove(int index) {
		modified = true;
		return data().remove(index);
	}

	public boolean remove(Object o) {
		modified = true;
		return data().remove(o);
	}

	public boolean removeAll(Collection c) {
		modified = true;
		return data().removeAll(c);
	}

	public boolean retainAll(Collection c) {
		modified = true;
		return data().retainAll(c);
	}

	public Object set(int index, Object element) {
		modified = true;
		return data().set(index, element);
	}

	public int size() {
		return data().size();
	}

	public List subList(int fromIndex, int toIndex) {
		return data().subList(fromIndex, toIndex);
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

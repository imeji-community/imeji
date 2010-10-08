package thewebsemantic;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Statement;

public class Things {

	Set<Statement> stuff;
	
	public Things(Set<Statement> set) {
		stuff = set;
	}

	public boolean add(Statement e) {
		return stuff.add(e);
	}

	public boolean addAll(Collection<? extends Statement> c) {
		return stuff.addAll(c);
	}

	public void clear() {
		stuff.clear();
	}

	public boolean contains(Object o) {
		return stuff.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return stuff.containsAll(c);
	}

	public boolean equals(Object o) {
		return stuff.equals(o);
	}

	public int hashCode() {
		return stuff.hashCode();
	}

	public boolean isEmpty() {
		return stuff.isEmpty();
	}

	public Iterator<Statement> iterator() {
		return stuff.iterator();
	}

	public boolean remove(Object o) {
		return stuff.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return stuff.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return stuff.retainAll(c);
	}

	public int size() {
		return stuff.size();
	}

	public Object[] toArray() {
		return stuff.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return stuff.toArray(a);
	}


}

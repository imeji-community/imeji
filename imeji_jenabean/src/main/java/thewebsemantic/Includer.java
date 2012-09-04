package thewebsemantic;

import java.util.Collection;
import java.util.LinkedList;


public class Includer {

	private Collection<String> includes = new LinkedList<String>();
	private RDF2Bean reader;
	private static final String[] none = new String[0];
	
	public Includer(String s, RDF2Bean reader) {
		includes.add(s);
		this.reader = reader;
	}
	
	public Includer include(String s) {
		includes.add(s);return this;
	}
	
	public <T> Collection<T> load(Class<T> c) {
		return reader.load(c, true, includes.toArray(none));
	}
	
	public <T> T load(Class<T> c, String id) throws NotFoundException {
		return reader.load(c, id, includes.toArray(none));
	}
}

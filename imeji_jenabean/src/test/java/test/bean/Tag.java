package test.bean;

import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace("http://example.org/")
public class Tag {

	private Collection<Taggable> items = new LinkedList<Taggable>();
	private String name;
	
	public Tag() {}
	
	public Tag(String name) {
		this.name = name;
	}

	@Id
	public String getName() {
		return name;
	}

	public Collection<Taggable> getItems() {
		return items;
	}

	public void setItems(Collection<Taggable> items) {
		this.items = items;
	}	
}

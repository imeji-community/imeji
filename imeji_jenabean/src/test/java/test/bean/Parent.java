package test.bean;

import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Namespace;

@Namespace("http://example.org/")
public class Parent {
	private Collection<Unannotated> children;
	private Collection<String> things;

	public Collection<String> getThings() {
		return things;
	}

	public void setThings(Collection<String> things) {
		this.things = things;
	}

	public Parent() {
		children = new LinkedList<Unannotated>();
		things = new LinkedList<String>();
	}
	
	public Collection<Unannotated> getChildren() {
		return children;
	}

	public void setChildren(Collection<Unannotated> children) {
		this.children = children;
	}
	
	public void addChild(Unannotated child) {
		children.add(child);
	}
	
	public void addThing(String s) {
		things.add(s);
	}
}

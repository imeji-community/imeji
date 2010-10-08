package test.bean;

import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Namespace;

@Namespace("http://example.org/")
public class AutoBoxing {
	private Collection<Integer> foo = new LinkedList<Integer>();
	private Collection<Character> bar = new LinkedList<Character>();

	public Collection<Character> getBar() {
		return bar;
	}

	public void setBar(Collection<Character> bar) {
		this.bar = bar;
	}

	public Collection<Integer> getFoo() {
		return foo;
	}

	public void setFoo(Collection<Integer> foo) {
		this.foo = foo;
	}

	public void addFoo(int i) {
		foo.add(i);
	}
	
	public void addBar(char c) {
		bar.add(c);
	}
}

package test.bean;

import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Namespace;

@Namespace("http://fruity/")
public class Orange {

	private Collection<Apple> apples = new LinkedList<Apple>();

	public Collection<Apple> getApples() {
		return apples;
	}

	public void setApples(Collection<Apple> apples) {
		this.apples = apples;
	} 

	public void addApple(Apple a) {
		apples.add(a);
	}
}

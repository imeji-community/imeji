package test.bean;

import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace("http://fruity/")
public class Apple {
	
	private Collection<Orange> oranges = new LinkedList<Orange>();

	@RdfProperty(inverseOf="apples")
	public Collection<Orange> getOranges() {
		return oranges;
	}

	public void setOranges(Collection<Orange> oranges) {
		this.oranges = oranges;
	}
	
	public void addOrange(Orange o) {
		oranges.add(o);
	}
	
}

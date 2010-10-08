package example;

import java.util.Collection;
import java.util.LinkedHashSet;

public class Tag {

	private Collection<Taggable> elements = new LinkedHashSet<Taggable>();
	private String term;

	public Collection<Taggable> getElements() {
		return elements;
	}

	public void setElements(Collection<Taggable> elements) {
		this.elements = elements;
	}

	public void addElement(Taggable e) {
		elements.add(e);
	}

	@thewebsemantic.Id
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
}
package test.bean;

import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace("http://example.org/")
public class Taggable {
	protected Collection<Tag> tags = new LinkedList<Tag>();


	@RdfProperty(inverseOf="items")	
	public Collection<Tag> getTags() {
		return tags;
	}


	public void setTags(Collection<Tag> tags) {
		this.tags = tags;
	}
	
	public void addTag(Tag t) {
		if (!tags.contains(t))
			tags.add(t);
	}
	

}

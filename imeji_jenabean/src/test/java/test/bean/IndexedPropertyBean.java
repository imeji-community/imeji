package test.bean;

import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace("http://example.org#")
public class IndexedPropertyBean {
	public String[] tags;

	@RdfProperty("http://foo.com/tags")
	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
	}
	
}

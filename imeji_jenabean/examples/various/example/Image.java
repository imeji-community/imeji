package example;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

@Namespace("http://myimages/")
public class Image extends Taggable {

	private String name;

	@Id
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

package example;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

@Namespace("http://myposts/")
public class Post extends Taggable {

	public String title;

	@Id
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}

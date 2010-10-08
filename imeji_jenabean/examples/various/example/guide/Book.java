package example.guide;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfProperty;

@Namespace("http://example.org/")
public class Book {
	String isbn;

	@Id
	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	String title;
	
	@RdfProperty("http://purl.org/dc/terms/title")
	public String getTitle() {return title;}
	
	public void setTitle(String title) {
		this.title = title;
	}
}

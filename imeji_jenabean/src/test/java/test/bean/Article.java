package test.bean;

import java.util.Collection;

import thewebsemantic.Id;
import thewebsemantic.binding.RdfBean;

public class Article extends RdfBean<Article> { 

	private Collection<String> authors;
	private String id;
	
	public Article(String id) {
		this.id = id;
	}
	
	public Collection<String> getAuthors() {
		return authors;
	}

	public void setAuthors(Collection<String> authors) {
		this.authors = authors;
	}
	
	@Id
	public String id() {
		return id;
	}
}

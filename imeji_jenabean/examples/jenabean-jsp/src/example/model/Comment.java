package example.model;

import java.rmi.server.UID;
import java.util.Date;

import thewebsemantic.Namespace;

@Namespace("http://example.org/")
public class Comment {
	private String content;
	private Date createdAt;
	private Post post;
	private String id;

	public Comment() {
		createdAt = new Date();
	}	
	public Comment(Post p) {
		this();
		post = p;
		id = new UID().toString();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Post getPost() {
		return post;
	}
	public void setPost(Post post) {
		this.post = post;
	}
}

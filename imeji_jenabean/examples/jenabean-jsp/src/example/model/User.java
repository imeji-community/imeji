package example.model;

import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Id;
import thewebsemantic.RdfBean;

public class User extends RdfBean<User>{
	private String screenName;
	private String encryptedPassword;
	private String email;
	private Collection<Post> posts = new LinkedList<Post>();
	
	public Collection<Post> getPosts() {
		return posts;
	}
	
	public void setPosts(Collection<Post> posts) {
		this.posts = posts;
	}
	
	public void addPost(Post p) {
		posts.add(p);
	}
	
	@Id
	public String getScreenName() {
		return screenName;
	}
	
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	
	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}

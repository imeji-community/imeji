package action;

import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Includer;
import thewebsemantic.NotFoundException;
import static thewebsemantic.binding.Jenabean.*;

import example.model.Post;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

@UrlBinding("/blog/home")
public class HubAction extends BaseAction {
	
	private Collection<Post> posts;
	private String postid;
	
	@DefaultHandler
	public Resolution show() throws NotFoundException {
		String pathInfo = context.getRequest().getPathInfo();
		if ( pathInfo.length() > 6) {
			postid = pathInfo.substring(6);		
			posts = new LinkedList<Post>();
			posts.add(normal().load(Post.class, postid));
		} else
			posts = normal().load(Post.class);
		return new ForwardResolution("/hub.jsp");
	}
	public Collection<Post> getPosts() {return posts;}
	public String getP() {return postid;}
	private Includer normal() {	return include("tags").include("comments");}
	
}

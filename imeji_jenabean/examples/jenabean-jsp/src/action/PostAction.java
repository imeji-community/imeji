package action;

import java.util.List;

import thewebsemantic.NotFoundException;
import static thewebsemantic.binding.Jenabean.*;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;
import example.model.Post;
import example.model.Tag;

@UrlBinding("/blog/post")
public class PostAction extends BaseAction {

	private Post post;
	private List<Tag> tags;
	private String tag;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	@Before(LifecycleStage.EventHandling)
	public Resolution secure() throws Exception {
		return ( context.getLogin() == null) ?
			new RedirectResolution(LoginAction.class) : null;
	}

	@Before(LifecycleStage.BindingAndValidation)
	public void rehydrate() throws NotFoundException {
		String id = context.getRequest().getParameter("p");
		if (id != null)
			this.post = load(Post.class, id);
	}

	@DefaultHandler
	public Resolution start() {
		return new ForwardResolution("/post.jsp");
	}
	
	@HandlesEvent("addTag")
	public Resolution addTag() {
		return new ForwardResolution("/post.jsp"); 
	}

	@HandlesEvent("post")
	public Resolution post() {
		post.setAuthor(context.getLogin());
		post.setTags(tags);
		post.save();
		return new RedirectResolution(HubAction.class);
	}

	@ValidateNestedProperties({
        @Validate(field="title", required=true, maxlength=75, on = "post"),
        @Validate(field="content", required=true, maxlength=5000, on = "post")
    })
	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}
}

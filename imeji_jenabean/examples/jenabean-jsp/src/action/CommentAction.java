package action;

import java.rmi.server.UID;

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
import example.model.Comment;
import example.model.Post;

@UrlBinding("/blog/comment")
public class CommentAction extends BaseAction {
	private Comment comment;
	private String postid;

	@Before(LifecycleStage.EventHandling)
	public Resolution secure() throws Exception {
		return ( context.getLogin() == null) ?
			new RedirectResolution(LoginAction.class) : null;
	}

	@DefaultHandler
	public Resolution start() {
		return new ForwardResolution("/post.jsp");
	}

	@HandlesEvent("comment")
	public Resolution post() throws NotFoundException {
		comment.setId(new UID().toString());
		Post p = load(Post.class, postid).add(comment).save();
		return new RedirectResolution("/blog/home/" + p.getId());
	}

	@ValidateNestedProperties({
        @Validate(field="title", required=true, maxlength=75, on = "post"),
        @Validate(field="content", required=true, maxlength=5000, on = "post")
    })
	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment post) {
		this.comment = post;
	}

	public void setP(String id) {
		postid = id;
	}

	public void getP() {}
}

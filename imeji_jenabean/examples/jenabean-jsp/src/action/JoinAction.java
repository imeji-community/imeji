package action;

import java.security.NoSuchAlgorithmException;

import static thewebsemantic.binding.Jenabean.*;
import example.model.User;


import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

@UrlBinding("/blog/join")
public class JoinAction extends BaseAction {

	@Validate(required = true, minlength = 1, maxlength = 255, on = "join")
	private String email;

	@Validate(required = true, minlength = 1, maxlength = 255, on = "join")
	private String password;

	@Validate(required = true, minlength = 1, maxlength = 255, on = "join")
	private String screenName;
	
	@Validate(required = true, minlength = 1, maxlength = 255, on = "join")
	private String verify;

	@ValidationMethod(on = "join")
	public void validateRegistration(ValidationErrors errors) {
		if ( !password.equals(verify) )
			errors.add("password", new LocalizableError("verifymatch"));
		if (exists(User.class, screenName))
			errors.addGlobalError(new LocalizableError("userexists"));
	}
	
	@DefaultHandler
	public Resolution start() {
		return new ForwardResolution("/join.jsp");
	}
	
	@HandlesEvent("join")
	public Resolution join() throws NoSuchAlgorithmException {
		User u = new User();
		u.setScreenName(screenName);
		u.setEmail(email);
		u.setEncryptedPassword(hashPassword(password));
		u.save();
		context.setLogin(u);
		return new RedirectResolution(HubAction.class);
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}
}

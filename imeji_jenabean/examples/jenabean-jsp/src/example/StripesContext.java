package example;

import example.model.User;
import net.sourceforge.stripes.action.ActionBeanContext;

public class StripesContext extends ActionBeanContext {
	
	
	public User getLogin() {
		return (User)getRequest().getSession().getAttribute(Constants.LOGIN);
	}

	public void setLogin(User u) {
		getRequest().getSession().setAttribute(Constants.LOGIN, u);
	}

}

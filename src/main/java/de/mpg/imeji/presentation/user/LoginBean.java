/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.user;

import de.mpg.imeji.logic.auth.Authentication;
import de.mpg.imeji.logic.auth.AuthenticationFactory;
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.ConfigurationBean;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.history.HistoryPage;
import de.mpg.imeji.presentation.history.HistorySession;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * Bean for login features
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@ManagedBean(name = "LoginBean")
@ViewScoped
public class LoginBean {
	private String login;
	private String passwd;
	private SessionBean sb;
	private String redirect;

	/**
	 * Constructor
	 */
	public LoginBean() {

	}

	@PostConstruct
	public void init() {
		this.sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		if (!"".equals(sb.getSpaceId())) {
			sb.logoutFromSpot();
			Navigation nav = (Navigation) BeanHelper
					.getApplicationBean(Navigation.class);
			try {
				FacesContext.getCurrentInstance().getExternalContext()
						.redirect(nav.getHomeUrl());
			} catch (IOException e) {
				new RuntimeException(e);
			}
		}
		try {
			if (UrlHelper.getParameterBoolean("logout")) {
				logout();
			}
			if (UrlHelper.getParameterValue("redirect") != null)
				this.redirect = URLDecoder.decode(
						UrlHelper.getParameterValue("redirect"), "UTF-8");
		} catch (Exception e) {
			new RuntimeException(e);
		}
	}

	public void setLogin(String login) {
		this.login = login.trim();
	}

	public String getLogin() {
		return login;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd.trim();
	}

	public String getPasswd() {
		return passwd;
	}

	public void loginClick() {
		sb.setShowLogin(true);
	}

	public void doLogin() throws Exception {
		Authentication auth = AuthenticationFactory.factory(getLogin(),
				getPasswd());
		User user = auth.doLogin();
		if (user != null) {
			sb.setUser(user);
			BeanHelper.info(sb.getMessage("success_log_in"));
		} else {
			String name = ((ConfigurationBean) BeanHelper
					.getApplicationBean(ConfigurationBean.class))
					.getInstanceName();
			BeanHelper.error(sb.getMessage("error_log_in").replace(
					"XXX_INSTANCE_NAME_XXX", name));
			BeanHelper.error(sb.getMessage("error_log_in_description").replace(
					"XXX_INSTANCE_NAME_XXX", name));
		}
		if (redirect == null || "".equals(redirect)) {
			HistoryPage current = ((HistorySession) BeanHelper
					.getSessionBean(HistorySession.class)).getCurrentPage();
			if (current != null) {
				redirect = current.getCompleteUrl();
			}
		}
		// redirect = UrlHelper.addParameter(redirect, "login", "1");
		FacesContext.getCurrentInstance().getExternalContext()
				.redirect(redirect);
	}

	public boolean loginWithEscidocAccount() {
		return false;
	}

	public boolean loginWithImejiAccount() {
		return false;
	}

	/**
	 * Logout and redirect to the home page
	 * 
	 * @throws IOException
	 */
	public void logout() throws IOException {
		FacesContext fc = FacesContext.getCurrentInstance();
		String spaceId = sb.getSpaceId();
		HttpSession session = (HttpSession) fc.getExternalContext().getSession(
				false);
		session.invalidate();
		sb.setUser(null);
		sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		sb.setShowLogin(false);
		sb.setSpaceId(spaceId);

		BeanHelper.info(sb.getMessage("success_log_out"));
		fc = FacesContext.getCurrentInstance();
		Navigation nav = (Navigation) BeanHelper
				.getApplicationBean(Navigation.class);
		FacesContext.getCurrentInstance().getExternalContext()
				.redirect(nav.getHomeUrl());

	}
}

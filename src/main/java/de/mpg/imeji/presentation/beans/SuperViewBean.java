package de.mpg.imeji.presentation.beans;

import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import de.mpg.imeji.logic.vo.User;

/**
 * This bean is a utility Bean. It can be extended to get some basic session information
 * 
 * @author bastiens
 *
 */
@ManagedBean(name = "SuperViewBean")
@ViewScoped
public class SuperViewBean {
  @ManagedProperty(value = "#{SessionBean.user}")
  private User sessionUser;
  @ManagedProperty(value = "#{SessionBean.locale}")
  private Locale locale;

  /**
   * @return the sessionUser
   */
  public User getSessionUser() {
    return sessionUser;
  }

  /**
   * @param sessionUser the sessionUser to set
   */
  public void setSessionUser(User sessionUser) {
    this.sessionUser = sessionUser;
  }

  /**
   * @return the locale
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * @param locale the locale to set
   */
  public void setLocale(Locale locale) {
    this.locale = locale;
  }
}

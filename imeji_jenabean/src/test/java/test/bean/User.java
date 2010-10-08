package test.bean;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

@Namespace("http://test#")
public class User {
	private String encryptedPassword;
	private String email;
	private String screenName;
	private Profile profile;

	public User() {
	}

	public User(String s) {
		screenName = s;
	}

	public String toString() {
		return encryptedPassword + "\n" + email + "\n" + screenName;
	}

	public User(String p, String e, String s) {
		screenName = s;
		encryptedPassword = p;
		email = e;
	}

	@Override
	public int hashCode() {
		return screenName.hashCode();
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if ((o == null) || (o.getClass() != this.getClass()))
			return false;
		return equals((User)o);
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

	@Id
	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screen) {
		this.screenName = screen;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
}
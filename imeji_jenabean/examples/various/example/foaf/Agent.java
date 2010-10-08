package example.foaf;

import java.net.URI;
import java.util.Collection;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.Resource;

@Namespace(FoafUri.NS)
public class Agent {

	
	@Id URI uri;
	Collection<Resource> mbox;	
	String mbox_sha1sum;
	String gender;
	String jabberID;
	String aimChatID; 
	String icqChatID; 
	String yahooChatID; 
	String msnChatID ;
	Document weblog; 
	Resource openid; 
	String tipjar; 
	Collection<Resource> made; 
	Collection<Resource> holdsAccount; 
	String birthday;
	
	public Agent() {}
	
	public Agent(URI uri) {
		this.uri = uri;
	}

	public String getMbox_sha1sum() {
		return mbox_sha1sum;
	}
	public void setMbox_sha1sum(String mbox_sha1sum) {
		this.mbox_sha1sum = mbox_sha1sum;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getJabberID() {
		return jabberID;
	}
	public void setJabberID(String jabberID) {
		this.jabberID = jabberID;
	}
	public String getAimChatID() {
		return aimChatID;
	}
	public void setAimChatID(String aimChatID) {
		this.aimChatID = aimChatID;
	}
	public String getIcqChatID() {
		return icqChatID;
	}
	public void setIcqChatID(String icqChatID) {
		this.icqChatID = icqChatID;
	}
	public String getYahooChatID() {
		return yahooChatID;
	}
	public void setYahooChatID(String yahooChatID) {
		this.yahooChatID = yahooChatID;
	}
	public String getMsnChatID() {
		return msnChatID;
	}
	public void setMsnChatID(String msnChatID) {
		this.msnChatID = msnChatID;
	}
	public Document getWeblog() {
		return weblog;
	}
	public void setWeblog(Document weblog) {
		this.weblog = weblog;
	}
	public Resource getOpenid() {
		return openid;
	}
	public void setOpenid(Resource openid) {
		this.openid = openid;
	}
	public String getTipjar() {
		return tipjar;
	}
	public void setTipjar(String tipjar) {
		this.tipjar = tipjar;
	}
	public Collection<Resource> getMade() {
		return made;
	}
	public void setMade(Collection<Resource> made) {
		this.made = made;
	}
	public Collection<Resource> getHoldsAccount() {
		return holdsAccount;
	}
	public void setHoldsAccount(Collection<Resource> holdsAccount) {
		this.holdsAccount = holdsAccount;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public Collection<Resource> getMbox() {
		return mbox;
	}

	public void setMbox(Collection<Resource> mbox) {
		this.mbox = mbox;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	} 
}

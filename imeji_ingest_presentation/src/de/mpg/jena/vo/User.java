/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.jena.vo;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import thewebsemantic.Id;
import thewebsemantic.Namespace;
import thewebsemantic.RdfType;

@Namespace("http://xmlns.com/foaf/0.1/")
@RdfType("Person")
public class User implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -1638051093184699765L;

	private String name;
	
	private String nick;
	
	private String email;
	
	private String encryptedPassword;
	
	private Collection<Grant> grants = new LinkedList<Grant>();
	
	@Id
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setGrants(Collection<Grant> grants) {
		this.grants = grants;
	}

	public Collection<Grant> getGrants() {
		return grants;
	}
	
}

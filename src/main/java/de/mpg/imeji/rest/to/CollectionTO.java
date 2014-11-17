package de.mpg.imeji.rest.to;

import java.io.Serializable;

public class CollectionTO extends ContainerTO implements Serializable{

	private static final long serialVersionUID = 7039960402363523772L;
	
	private String profileId;

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	
	
	

	

}

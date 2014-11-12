package de.mpg.imeji.rest.to;


public class UserTOBasic {
	private String fullname;
	
	private String id;
	
	public UserTOBasic(String fullname, String id){
		this.fullname = fullname;
		this.id = id;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	
	

}

package de.mpg.imeji.rest.to;


public class PersonTOBasic {
	private String fullname;
	
	private String id;
	
	public PersonTOBasic(String fullname, String id){
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

package test.id;

import thewebsemantic.Generated;
import thewebsemantic.Id;

public class AutoIdMethod {
	
	int id;

	@Id
	@Generated
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

}

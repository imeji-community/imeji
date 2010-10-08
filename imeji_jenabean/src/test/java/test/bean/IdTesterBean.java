package test.bean;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

@Namespace("http://example.org#")
public class IdTesterBean {

	private String id;
	private int value;
	
	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}

/**
 * 
 */
package test.bean;

import thewebsemantic.Id;

public class Flute {
	private String id;

	public int i = 0;

	public Flute(String id) {
		this.id = id;
		i++;
	}

	@Id
	public String getMyId() {
		return id;
	}
}
/**
 * 
 */
package test.bean;

import thewebsemantic.Id;

public class Trumpet {
	private String id;

	public Trumpet() {
	}

	public int hashCode() {
		return id.hashCode();
	}
	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
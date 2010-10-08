package test.bean;

/**
 * compute an id based on values within the bean.
 * This way we are allowing the value to determine
 * if two objects are really the same thing.
 *
 */
public class FieldBasedUriBean {
	
	private int floor;
	private int cube;
	
	@thewebsemantic.Id
	public String computeId() {
		return floor + "_" + cube;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public int getCube() {
		return cube;
	}

	public void setCube(int cube) {
		this.cube = cube;
	}
}

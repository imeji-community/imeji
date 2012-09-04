package test.bean;

import thewebsemantic.Id;

public class Arrayzing {	
	@Id
	int id;
	String[] strings;
	int[] integers;
	long[] longs;
	double[] doubles;
	float[] floats;
	char[] chars;
	short[] shorts;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String[] getStrings() {
		return strings;
	}
	public void setStrings(String[] strings) {
		this.strings = strings;
	}
	public int[] getIntegers() {
		return integers;
	}
	public void setIntegers(int[] integers) {
		this.integers = integers;
	}
	public long[] getLongs() {
		return longs;
	}
	public void setLongs(long[] longs) {
		this.longs = longs;
	}
	public double[] getDoubles() {
		return doubles;
	}
	public void setDoubles(double[] doubles) {
		this.doubles = doubles;
	}
	public float[] getFloats() {
		return floats;
	}
	public void setFloats(float[] floats) {
		this.floats = floats;
	}
	public char[] getChars() {
		return chars;
	}
	public void setChars(char[] chars) {
		this.chars = chars;
	}
	public short[] getShorts() {
		return shorts;
	}
	public void setShorts(short[] shorts) {
		this.shorts = shorts;
	}
	
}

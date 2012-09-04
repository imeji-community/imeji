package test.bean;
import thewebsemantic.Id;

public class KeepItSimple {
	@Id
	private String id;
	private int value;
	public int getValue() {return value;}
	public void setValue(int i) {value = i;}
	public String getId() {return id;}
	public void setId(String id) {this.id = id;}	
}

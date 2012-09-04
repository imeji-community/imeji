package test.fieldaccess;



import thewebsemantic.Id;
import thewebsemantic.RdfProperty;

public class FatAlbert {
	
	@Id
	int id;
	
	@RdfProperty("http://weird/url/goes/here/")
	private String name = "fatalbert";
	
	public transient String dontsaveme = null;

	public String toString() {
		return name;
	}
	
	public String name() {
		return name;
	}
	
	public void name(String s) {
		name=s;
	}
}

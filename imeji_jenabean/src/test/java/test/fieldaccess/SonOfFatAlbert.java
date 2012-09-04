package test.fieldaccess;

public class SonOfFatAlbert extends FatAlbert {


	private String sonname = "son";
	
	public String toString() {
		return super.toString() + "\n" +  sonname;
	}
	
	public String sonname() {
		return sonname;
	}
}

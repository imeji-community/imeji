package test.fieldaccess;

public class GrandsonOfFatAlbert extends SonOfFatAlbert {

	private String grandsonname;
	
	public String toString() {
		return super.toString() + "\n" +grandsonname;
	}
	
	public String grandsonname() {
		return grandsonname;
	}
	
	public void grandsonname(String s) {
		grandsonname = s;
	}
}

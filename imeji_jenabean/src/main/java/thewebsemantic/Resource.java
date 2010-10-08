package thewebsemantic;

public class Resource {
	String uri;

	public Resource(String s) {
		uri = s;
	}
	
	@Uri
	public String getUri() {
		return uri;
	}

	public String toString() {
		return uri;
	}
}

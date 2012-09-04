package test.experimental;

import thewebsemantic.Namespace;
import thewebsemantic.Uri;

@Namespace("http://example.org/")
public class Red {
	String uri;
	String label;
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Red(String uri) {
		this.uri = uri;
	}
	
	@Uri
	public String uri() {return uri;}
	
	
}

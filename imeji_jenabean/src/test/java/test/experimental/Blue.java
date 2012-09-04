package test.experimental;

import thewebsemantic.Namespace;
import thewebsemantic.Uri;

@Namespace("http://example.org/")
public class Blue {
	
	String uri;
	String label;
	
	public Blue(String uri) {
		this.uri = uri;
	}
	
	@Uri
	public String uri() {return uri;}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}

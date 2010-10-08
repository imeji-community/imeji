package example.foaf;

import java.net.URI;

import thewebsemantic.Id;
import thewebsemantic.Namespace;

@Namespace(FoafUri.NS)
public class Document {

	@Id
	URI uri;
	
	public Document() {}
	
	public Document(URI uri) {
		this.uri = uri;
	}
	
	public URI getUri() {
		return uri;
	}

	public String toString() {
		return uri.toString();
	}	
}

package test.error;

import thewebsemantic.Uri;

public class Private {
	private String uri;

	Private(String uri) {
		this.uri = uri;
	}
	
	@Uri
	private String getUri() {
		return uri;
	}

	private void setUri(String uri) {
		this.uri = uri;
	}
	
}

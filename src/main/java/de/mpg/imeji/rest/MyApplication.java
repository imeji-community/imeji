package de.mpg.imeji.rest;

import java.util.HashMap;
import java.util.Map;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class MyApplication extends ResourceConfig {
	final Map<String, String> namespacePrefixMapper = new HashMap<String, String>();

	public MyApplication() {
		packages("de.mpg.imeji.rest.resources");
		// register multipart feature for the application
		register(MultiPartFeature.class);
		// Uncomment loggingFilter registration below to start logging your
		// service requests
		// register(LoggingFilter.class);
		// register(MoxyJsonFeature.class);
		// property(MarshallerProperties.JSON_NAMESPACE_SEPARATOR, ".");
	}
}

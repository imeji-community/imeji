package de.mpg.imeji.rest;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class MyApplication extends ResourceConfig {
	final Map<String, String> namespacePrefixMapper = new HashMap<String, String>();

    public MyApplication() {
        packages("de.mpg.imeji.rest.resources");
        //Uncomment loggingFilter registration below to start logging your service requests
        //register(LoggingFilter.class);
//        register(MoxyJsonFeature.class);
//        property(MarshallerProperties.JSON_NAMESPACE_SEPARATOR, ".");
    }
}

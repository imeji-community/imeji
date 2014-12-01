package de.mpg.imeji.rest;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class MyApplication extends ResourceConfig {

    public MyApplication() {
        packages("de.mpg.imeji.rest.resources");
        //Uncomment loggingFilter registration below to start logging your service requests
        register(LoggingFilter.class);
        register(MultiPartFeature.class);

//        register(MoxyJsonFeature.class);
//        property(MarshallerProperties.JSON_NAMESPACE_SEPARATOR, ".");
  }

}

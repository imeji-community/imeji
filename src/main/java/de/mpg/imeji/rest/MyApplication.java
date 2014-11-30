package de.mpg.imeji.rest;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

public class MyApplication extends ResourceConfig {

    public MyApplication() {
        packages(true, "de.mpg.imeji.rest");
        //Uncomment loggingFilter registration below to start logging your service requests
        register(LoggingFilter.class);
//        register(MoxyJsonFeature.class);
//        property(MarshallerProperties.JSON_NAMESPACE_SEPARATOR, ".");
  }

}

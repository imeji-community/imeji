package de.mpg.imeji.rest;

import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.BasicAuthenticator;
import de.mpg.imeji.rest.api.CollectionService;
import de.mpg.imeji.rest.process.CollectionProcess;
import de.mpg.imeji.rest.resources.CollectionResource;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.enterprise.context.RequestScoped;

public class MyApplication extends ResourceConfig {

    public MyApplication() {
        packages(true, "de.mpg.imeji.rest");
        //Uncomment loggingFilter registration below to start logging your service requests
        register(LoggingFilter.class);
//        register(MoxyJsonFeature.class);
//        property(MarshallerProperties.JSON_NAMESPACE_SEPARATOR, ".");
  }

}

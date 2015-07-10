package de.mpg.imeji.rest;

/**
 * Created by vlad on 10.07.15.
 */

import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.jersey.config.JerseyJaxrsConfig;

import javax.servlet.ServletConfig;

public class SwaggerApiDocsConfig extends JerseyJaxrsConfig {
    @Override
    public void init(ServletConfig servletConfig) {
        super.init(servletConfig);
        ConfigFactory.config().setBasePath(
                servletConfig.getServletContext().getContextPath() + ConfigFactory.config().getBasePath()
        );
    }
}
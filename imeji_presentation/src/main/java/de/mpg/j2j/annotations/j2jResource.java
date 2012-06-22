package de.mpg.j2j.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface j2jResource
{
    /**
     * The namespace of the resource
     * 
     * @return
     */
    public String value();
}

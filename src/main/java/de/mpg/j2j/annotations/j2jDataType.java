package de.mpg.j2j.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.hp.hpl.jena.vocabulary.RDF;

import de.mpg.imeji.logic.vo.Metadata;

/**
 * The {@link RDF}.type of an object. Used to define the {@link Metadata} types
 * 
 * @author saquet (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface j2jDataType {
  public String value();
}

package de.mpg.imeji.logic.validation;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.vo.MetadataProfile;

/**
 * Inteface for validators
 * 
 * @author saquet
 *
 * @param <T>
 */
public interface Validator<T> {

  /**
   * Validate an object according the business rules
   * 
   * @param t
   * @throws UnprocessableError
   */
  public void validate(T t, Method method) throws UnprocessableError;


  /**
   * Validate an object according to its {@link MetadataProfile}
   * 
   * @param t
   * @param p
   * @throws UnprocessableError
   */
  public void validate(T t, MetadataProfile p, Method method) throws UnprocessableError;

  public enum Method {
    CREATE, UPDATE, DELETE, ALL;
  }
}

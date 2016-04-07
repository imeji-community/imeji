package de.mpg.imeji.logic.validation.impl;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.predefinedMetadata.Geolocation;

/**
 * Validator for geolocation
 * 
 * @author bastiens
 *
 */
public class GeolocationValidator extends ContainerValidator implements Validator<Geolocation> {

  @Override
  public void validate(Geolocation geolocation,
      de.mpg.imeji.logic.validation.Validator.Method method) throws UnprocessableError {
    UnprocessableError e = new UnprocessableError();
    String value = geolocation.getName();
    Double latitude = geolocation.getLatitude();
    Double longitude = geolocation.getLongitude();
    if ((!Double.isNaN(latitude) || !Double.isNaN(longitude)) && value != null) {
      if (latitude < -90 || latitude > 90) {
        e = new UnprocessableError("error_latitude_format " + value, e);
      }
      if (longitude < -180 || longitude > 180) {
        e = new UnprocessableError("error_longitude_format " + value, e);
      }
      if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
        e = new UnprocessableError("error_long_latitude_must_be_both_not_null " + value, e);
      }
    }
    if (value == null) {
      e = new UnprocessableError("error_metadata_invalid_value " + value, e);
    }
    if (e.hasMessages()) {
      throw e;
    }
  }

  @Override
  public void validate(Geolocation t, MetadataProfile p,
      de.mpg.imeji.logic.validation.Validator.Method method) throws UnprocessableError {
    validate(t, method);

  }

  @Override
  protected UnprocessableError getException() {
    // TODO Auto-generated method stub
    return null;
  }

}

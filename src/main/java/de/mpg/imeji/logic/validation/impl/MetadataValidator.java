package de.mpg.imeji.logic.validation.impl;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.resource.util.MetadataProfileUtil;
import de.mpg.imeji.logic.resource.vo.Metadata;
import de.mpg.imeji.logic.resource.vo.MetadataProfile;
import de.mpg.imeji.logic.resource.vo.Organization;
import de.mpg.imeji.logic.resource.vo.Statement;
import de.mpg.imeji.logic.resource.vo.metadata.ConePerson;
import de.mpg.imeji.logic.resource.vo.metadata.Date;
import de.mpg.imeji.logic.resource.vo.metadata.Geolocation;
import de.mpg.imeji.logic.resource.vo.metadata.License;
import de.mpg.imeji.logic.resource.vo.metadata.Link;
import de.mpg.imeji.logic.resource.vo.metadata.Number;
import de.mpg.imeji.logic.resource.vo.metadata.Publication;
import de.mpg.imeji.logic.resource.vo.metadata.Text;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.validation.Validator;

/**
 * {@link Validator} for a {@link Metadata}. Only working with profile
 * 
 * @author saquet
 *
 */
public class MetadataValidator extends ObjectValidator implements Validator<Metadata> {

  @Override
  public void validate(Metadata t, Method m) throws UnprocessableError {
    throw new UnprocessableError("Metadata can not be validated without a profile");
  }

  @Override
  public void validate(Metadata md, MetadataProfile p, Method m) throws UnprocessableError {
    setValidateForMethod(m);
    if (isDelete()) {
      return;
    }
    Statement s = MetadataProfileUtil.getStatement(md.getStatement(), p);
    if (!validataMetadata(md, s))
      throw new UnprocessableError("Invalid value provided for metadata of type " + getTypeLabel(md)
          + " (" + md.asFulltext() + "...)");

  }

  /**
   * Validate the {@link Metadata} for the differents types
   * 
   * @param md
   * @param s
   * @return
   */
  private boolean validataMetadata(Metadata md, Statement s) {
    if (md instanceof Text) {
      String value = ((Text) md).getText();
      return value != null && isAllowedValueString(value, s);
    } else if (md instanceof Number) {
      double value = ((Number) md).getNumber();
      return isAllowedValueDouble(value, s);
    } else if (md instanceof Date) {
      // Date validation for format YYYY-MM-DD only, other dates will not be allowed
      String value = ((Date) md).getDate();
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      java.util.Date valueDate = null;
      try {
        valueDate = sdf.parse(value);
        if (!value.equals(sdf.format(valueDate))) {
          return false;
        }
      } catch (ParseException e) {
        return false;
      }
      return ((Date) md).getTime() != Long.MIN_VALUE && isAllowedValueString(value, s);
    } else if (md instanceof Link) {
      URI value = ((Link) md).getUri();
      return value != null && isAllowedValueURI(value, s);
    } else if (md instanceof Geolocation) {
      String value = ((Geolocation) md).getName();
      Double latitude = ((Geolocation) md).getLatitude();
      Double longitude = ((Geolocation) md).getLongitude();
      if (!Double.isNaN(latitude) || !Double.isNaN(longitude)) {
        return value != null && latitude >= -90 && latitude <= 90 && longitude >= -180
            && longitude <= 180;
      }
      return value != null;// No Predefined Value supported
    } else if (md instanceof ConePerson) {
      // no validation here for person only will be invoked, if family name is not
      // provided, presentation is deleting the whole person object!!!
      // should be fixed in the presentation
      String value = ((ConePerson) md).getPerson().getFamilyName();
      boolean valueOrg = true;
      List<Organization> orgs =
          (List<Organization>) ((ConePerson) md).getPerson().getOrganizations();
      for (Organization org : orgs) {
        if (StringHelper.isNullOrEmptyTrim(org.getName())
            && (!StringHelper.isNullOrEmptyTrim(org.getCountry())
                || !StringHelper.isNullOrEmptyTrim(org.getDescription())
                || !StringHelper.isNullOrEmptyTrim(org.getCity())))
          valueOrg = false;
      }
      return !StringHelper.isNullOrEmptyTrim(value) && valueOrg;
    } else if (md instanceof License) {
      String value = ((License) md).getLicense();
      return value != null;// No Predefined Value supported
    } else if (md instanceof Publication) {
      URI value = ((Publication) md).getUri();
      return value != null;// No Predefined Value supported
    }
    return false;
  }

  private String getTypeLabel(Metadata md) {
    if (md instanceof Text) {
      return "Text";
    } else if (md instanceof Number) {
      return "Number";
    } else if (md instanceof Date) {
      return "Date";
    } else if (md instanceof Link) {
      return "Link";
    } else if (md instanceof Geolocation) {
      return "Location";
    } else if (md instanceof ConePerson) {
      return "Person/Organization";
    } else if (md instanceof License) {
      return "License";
    } else if (md instanceof Publication) {
      return "Publication";
    }
    return "";
  }


  /**
   * Check if the value is allowed according the literal constraints
   * 
   * @param value
   * @param s
   * @return
   */
  private boolean isAllowedValueString(String value, Statement s) {
    if (s.getLiteralConstraints() != null && s.getLiteralConstraints().size() > 0) {
      return containsString(s.getLiteralConstraints(), value);
    }
    return true;
  }

  /**
   * Check if the value is allowed according the literal constraints
   * 
   * @param value
   * @param s
   * @return
   */
  private boolean isAllowedValueDouble(double value, Statement s) {
    if (s.getLiteralConstraints() != null && s.getLiteralConstraints().size() > 0) {
      return containsDouble(s.getLiteralConstraints(), value);
    }
    return true;
  }

  /**
   * Check if the value is allowed according the literal constraints
   * 
   * @param value
   * @param s
   * @return
   */
  private boolean isAllowedValueURI(URI value, Statement s) {
    if (s.getLiteralConstraints() != null && s.getLiteralConstraints().size() > 0) {
      return containsURI(s.getLiteralConstraints(), value);
    }
    return true;
  }

  /**
   * Test if the {@link Collection} contains the {@link String}
   * 
   * @param l
   * @param value
   * @return
   */
  private boolean containsString(Collection<String> l, String value) {
    for (String s : l) {
      if (s.equals(value)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Test if the {@link Collection} contains the {@link Double}
   * 
   * @param l
   * @param value
   * @return
   */
  private boolean containsDouble(Collection<String> l, double value) {
    for (String s : l) {
      if (Double.parseDouble(s) == value) {
        return true;
      }
    }
    return false;
  }

  /**
   * Test if the {@link Collection} contains the {@link URI}
   * 
   * @param l
   * @param value
   * @return
   */
  private boolean containsURI(Collection<String> l, URI value) {
    for (String s : l) {
      if (URI.create(s).equals(value)) {
        return true;
      }
    }
    return false;
  }
}

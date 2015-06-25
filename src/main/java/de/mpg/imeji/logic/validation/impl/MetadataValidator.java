package de.mpg.imeji.logic.validation.impl;

import java.net.URI;
import java.util.Collection;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.ConePerson;
import de.mpg.imeji.logic.vo.predefinedMetadata.Date;
import de.mpg.imeji.logic.vo.predefinedMetadata.Geolocation;
import de.mpg.imeji.logic.vo.predefinedMetadata.License;
import de.mpg.imeji.logic.vo.predefinedMetadata.Link;
import de.mpg.imeji.logic.vo.predefinedMetadata.Number;
import de.mpg.imeji.logic.vo.predefinedMetadata.Publication;
import de.mpg.imeji.logic.vo.predefinedMetadata.Text;
import de.mpg.imeji.presentation.util.ProfileHelper;

/**
 * {@link Validator} for a {@link Metadata}. Only working with profile
 * 
 * @author saquet
 *
 */
public class MetadataValidator extends ObjectValidator implements Validator<Metadata> {

	public MetadataValidator(Validator.Method method) {
		super(method);
	}

	@Override
	@Deprecated
	public void validate(Metadata t) throws UnprocessableError {
		throw new UnsupportedOperationException();
	}

	@Override
	public void validate(Metadata md, MetadataProfile p)
			throws UnprocessableError {
		Statement s = ProfileHelper.getStatement(md.getStatement(), p);
		if (!validataMetadata(md, s))
			throw new UnprocessableError("Metadata value not valid: "
					+ md.asFulltext());

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
			String value = ((Date) md).getDate();
			return ((Date) md).getTime() != Long.MIN_VALUE && value != null
					&& isAllowedValueString(value, s);
		} else if (md instanceof Link) {
			URI value = ((Link) md).getUri();
			return value != null && isAllowedValueURI(value, s);
		} else if (md instanceof Geolocation) {
			String value = ((Geolocation) md).getName();
			Double latitude = ((Geolocation) md).getLatitude();
			Double longitude = ((Geolocation) md).getLongitude();
			if (!Double.isNaN(latitude) || !Double.isNaN(longitude))
				return value != null && latitude >= -90 && latitude <= 90
						&& longitude >= -180 && longitude <= 180;
			return value != null;// No Predefined Value supported
		} else if (md instanceof ConePerson) {
			String value = ((ConePerson) md).getPerson().getFamilyName()
					+ ((ConePerson) md).getPerson().getGivenName();
			return value != null; // No Predefined Value supported;
		} else if (md instanceof License) {
			String value = ((License) md).getLicense();
			return value != null;// No Predefined Value supported
		} else if (md instanceof Publication) {
			URI value = ((Publication) md).getUri();
			return value != null;// No Predefined Value supported
		}
		return false;
	}

	/**
	 * Check if the value is allowed according the literal constraints
	 * 
	 * @param value
	 * @param s
	 * @return
	 */
	private boolean isAllowedValueString(String value, Statement s) {
		if (s.getLiteralConstraints() != null
				&& s.getLiteralConstraints().size() > 0) {
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
		if (s.getLiteralConstraints() != null
				&& s.getLiteralConstraints().size() > 0) {
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
		if (s.getLiteralConstraints() != null
				&& s.getLiteralConstraints().size() > 0) {
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
		for (String s : l)
			if (s.equals(value))
				return true;
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
		for (String s : l)
			if (Double.parseDouble(s) == value)
				return true;
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
		for (String s : l)
			if (URI.create(s).equals(value))
				return true;
		return false;
	}

}

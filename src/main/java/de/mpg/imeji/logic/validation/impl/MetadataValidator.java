package de.mpg.imeji.logic.validation.impl;

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
public class MetadataValidator implements Validator<Metadata> {

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
			throw new UnprocessableError("Metadata value not valid");

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
			return value != null && isAllowedValue(value, s);
		} else if (md instanceof Number) {
			String value = Double.toString(((Number) md).getNumber());
			return value != null && isAllowedValue(value, s);
		} else if (md instanceof Date) {
			String value = ((Date) md).getDate();
			return value != null && isAllowedValue(value, s);
		} else if (md instanceof Geolocation) {
			String value = ((Geolocation) md).getName();
			return value != null && isAllowedValue(value, s);
		} else if (md instanceof ConePerson) {
			String value = ((ConePerson) md).getPerson().getCompleteName();
			return value != null && isAllowedValue(value, s);
		} else if (md instanceof Link) {
			String value = ((Link) md).getUri().toString();
			return value != null && isAllowedValue(value, s);
		} else if (md instanceof License) {
			String value = ((License) md).getLicense();
			return value != null && isAllowedValue(value, s);
		} else if (md instanceof Publication) {
			String value = ((Publication) md).getUri().toString();
			return value != null && isAllowedValue(value, s);
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
	private boolean isAllowedValue(String value, Statement s) {
		if (s.getLiteralConstraints() != null
				&& s.getLiteralConstraints().size() > 0)
			return s.getLiteralConstraints().contains(s);
		return true;
	}

}

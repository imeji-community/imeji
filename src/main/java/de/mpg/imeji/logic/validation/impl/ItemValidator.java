package de.mpg.imeji.logic.validation.impl;

import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.presentation.util.ProfileHelper;

/**
 * {@link Validator} for an {@link Item}. Only working when
 * {@link MetadataProfile} is passed
 * 
 * @author saquet
 *
 */
public class ItemValidator extends ObjectValidator implements Validator<Item>  {

	public ItemValidator(Validator.Method method) {
		super(method);
	}

	@Override
	@Deprecated
	public void validate(Item t) throws UnprocessableError {
		throw new UnsupportedOperationException();

	}

	@Override
	public void validate(Item item, MetadataProfile p)
			throws UnprocessableError {
		MetadataValidator mdValidator = new MetadataValidator(getValidateForMethod());
		// List of the statement which are not defined as Multiple
		List<String> nonMultipleStatement = new ArrayList<String>();
		for (Metadata md : item.getMetadataSet().getMetadata()) {
			mdValidator.validate(md, p);
			Statement s = ProfileHelper.getStatement(md.getStatement(), p);
			if (s.getMaxOccurs() == null || s.getMaxOccurs().equals("1")) {
				if (nonMultipleStatement.contains(s.getId().toString()))
					throw new UnprocessableError(
							"Multiple value not allowed for metadata "
									+ s.getLabels().iterator().next()
											.getValue() + "(ID: " + s.getId()
									+ "");
				nonMultipleStatement.add(s.getId().toString());
			}
		}
	}

}

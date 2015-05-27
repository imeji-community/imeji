package de.mpg.imeji.logic.validation.impl;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.MetadataProfile;

/**
 * {@link Validator} for an {@link Item}. Only working when
 * {@link MetadataProfile} is passed
 * 
 * @author saquet
 *
 */
public class ItemValidator implements Validator<Item> {

	@Override
	@Deprecated
	public void validate(Item t) throws UnprocessableError {
		throw new UnsupportedOperationException();

	}

	@Override
	public void validate(Item item, MetadataProfile p)
			throws UnprocessableError {
		MetadataValidator mdValidator = new MetadataValidator();
		for (Metadata md : item.getMetadataSet().getMetadata()) {
			mdValidator.validate(md, p);
		}
	}

}

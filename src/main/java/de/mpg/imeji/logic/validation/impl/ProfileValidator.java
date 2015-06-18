package de.mpg.imeji.logic.validation.impl;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.List;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.j2j.misc.LocalizedString;

/**
 * {@link Validator} for {@link MetadataProfile}
 * 
 * @author saquet
 *
 */
public class ProfileValidator implements Validator<MetadataProfile> {

	@Override
	public void validate(MetadataProfile profile) throws UnprocessableError {
		if (isNullOrEmpty(profile.getTitle())) {
			throw new UnprocessableError("error_profile_need_title");
		}
		if (profile.getStatements() == null) {
			throw new UnprocessableError("error_profile_need_statement");
		}
		int i = 0;
		for (Statement s : profile.getStatements()) {
			for (LocalizedString ls : s.getLabels()) {
				if (ls.getLang() == null || "".equals(ls.getLang())) {
					throw new UnprocessableError("error_profile_label_no_lang");
				}
			}
			if (s.getType() == null) {
				throw new UnprocessableError(
						"error_profile_select_metadata_type");
			} else if (s.getLabels().isEmpty()
					|| "".equals(((List<LocalizedString>) s.getLabels()).get(0)
							.getValue())) {
				throw new UnprocessableError("error_profile_labels_required");
			}
			s.setPos(i);
			i++;
		}
	}

	@Override
	public void validate(MetadataProfile t, MetadataProfile p)
			throws UnprocessableError {
		validate(t);
	}

}

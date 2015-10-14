package de.mpg.imeji.logic.validation.impl;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.j2j.misc.LocalizedString;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * {@link Validator} for {@link MetadataProfile}
 * 
 * @author saquet
 * 
 */
public class ProfileValidator extends ObjectValidator implements Validator<MetadataProfile> {

  public ProfileValidator(Validator.Method method) {
    super(method);
  }

  @Override
  public void validate(MetadataProfile profile) throws UnprocessableError {
    if (isDelete())
      return;

    if (isNullOrEmpty(profile.getTitle())) {
      throw new UnprocessableError("error_profile_need_title");
    }
    if (profile.getStatements() == null) {
      throw new UnprocessableError("error_profile_need_statement");
    }
    int i = 0;

    // helper hashmap to validate uniqueness of metadata labels
    HashMap<String, URI> labels = new HashMap<>();

    for (Statement s : profile.getStatements()) {
      // helper check duplication language input
      List<String> langs = new ArrayList<String>();

      for (LocalizedString ls : s.getLabels()) {
        if (ls.getLang() == null || "".equals(ls.getLang())) {
          throw new UnprocessableError("error_profile_label_no_lang");
        }
        // validate uniqueness of metadata labels        
        if (labels.containsKey(ls.getValue()) && !labels.get(ls.getValue()).equals(s.getId())) {
          throw new UnprocessableError("labels_have_to_be_unique"); 
        }
        if (langs.contains(ls.getLang())) {
          throw new UnprocessableError("labels_duplicate_lang");
        } else {
          langs.add(ls.getLang());
          labels.put(ls.getValue(), s.getId());
        }
      }
      if (s.getType() == null) {
        throw new UnprocessableError("error_profile_select_metadata_type");
      } else if (s.getLabels().isEmpty()
          || "".equals(((List<LocalizedString>) s.getLabels()).get(0).getValue())) {
        throw new UnprocessableError("error_profile_labels_required");
      }
      s.setPos(i);
      i++;
    }
  }

  @Override
  public void validate(MetadataProfile t, MetadataProfile p) throws UnprocessableError {
    validate(t);
  }

}

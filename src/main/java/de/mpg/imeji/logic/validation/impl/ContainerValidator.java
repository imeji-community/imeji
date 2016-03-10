package de.mpg.imeji.logic.validation.impl;

import static com.google.common.base.Strings.isNullOrEmpty;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.ContainerAdditionalInfo;

/**
 * Abstract call for common container methods
 * 
 * @author bastiens
 *
 */
public abstract class ContainerValidator extends ObjectValidator {

  protected abstract UnprocessableError getException();

  protected void validateContainerMetadata(Container c) {
    if (isDelete()) {
      return;
    }
    if (StringHelper.hasInvalidTags(c.getMetadata().getDescription())) {
      getException().getMessages().add("error_bad_format_description");
    }
    if (isNullOrEmpty(c.getMetadata().getTitle().trim())) {
      getException().getMessages().add("error_collection_need_title");
    }
    validateAdditionalInfos(c);
  }

  private void validateAdditionalInfos(Container c) {
    for (ContainerAdditionalInfo info : c.getMetadata().getAdditionalInformations()) {
      if (info.getLabel().isEmpty()) {
        getException().getMessages().add("error_additionalinfo_need_label");
      }
      if (info.getText().isEmpty() && info.getUrl().isEmpty()) {
        getException().getMessages().add("error_additionalinfo_need_value");
      }

    }
  }


}

package de.mpg.imeji.rest.helper;

import de.mpg.imeji.exceptions.BadRequestException;
import de.mpg.imeji.rest.to.MetadataProfileTO;
import de.mpg.imeji.rest.to.StatementTO;
import de.mpg.j2j.misc.LocalizedString;

/**
 * Helper for profile transfer operation
 * 
 * @author bastiens
 * 
 */
public class ProfileTransferHelper {
  /**
   * Find the {@link StatementTO} according to its label. If not found, return null
   * 
   * @param label
   * @param profile
   * @return
   * @throws BadRequestException
   */
  public static StatementTO findStatementByLabel(String label, MetadataProfileTO profile)
      throws BadRequestException {
    for (StatementTO st : profile.getStatements()) {
      for (LocalizedString l : st.getLabels()) {
        if (l.getValue().equals(label))
          return st;
      }
    }
    throw new BadRequestException("Metadata \"" + label + "\" not found!");
  }

}

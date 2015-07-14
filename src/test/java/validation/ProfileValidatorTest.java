package validation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.validation.Validator.Method;
import de.mpg.imeji.logic.validation.impl.ProfileValidator;
import de.mpg.imeji.logic.vo.Metadata.Types;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.j2j.misc.LocalizedString;

public class ProfileValidatorTest {

  private static ProfileValidator validator;
  private static MetadataProfile profile;


  @BeforeClass
  public static void init() {
    validator = new ProfileValidator(Method.ALL);
    profile = new MetadataProfile();
    profile.setTitle("Profile");
  }

  @Test
  public void validateUniquenessOfLabels_NotUnique() {

    Collection<Statement> statements = new ArrayList<>();
    Statement text1 = newStatement(Types.TEXT, "text", null);
    Statement text2 = newStatement(Types.TEXT, "text", null);
    statements.add(text1);
    statements.add(text2);
    profile.setStatements(statements);
    try {
      validator.validate(profile);
      Assert.fail("Validation of uniqueness of metadata labels not working");
    } catch (UnprocessableError e) {
      // good...
    }

  }

  @Test
  public void validateUniquenessOfLabels_SameLabelForOneStatement() {

    Collection<Statement> statements = new ArrayList<>();
    Statement text1 = newStatement(Types.TEXT, "text", "text");
    Statement text2 = newStatement(Types.TEXT, "text", null);
    statements.add(text1);
    statements.add(text2);
    profile.setStatements(statements);
    try {
      validator.validate(profile);
      Assert.fail("Validation of uniqueness...false positive");
    } catch (UnprocessableError e) {

    }
  }


  /**
   * Create a new {@link Statement}
   * 
   * @param type
   * @param label
   * @return
   */
  private static Statement newStatement(Types type, String label, String label2) {
    Statement s = new Statement();
    s.setType(URI.create(type.getClazzNamespace()));
    Collection<LocalizedString> labels = new ArrayList<LocalizedString>();
    labels.add(new LocalizedString(label, "en"));
    labels.add(new LocalizedString(label, "de"));
    s.setLabels(labels);
    return s;
  }

}

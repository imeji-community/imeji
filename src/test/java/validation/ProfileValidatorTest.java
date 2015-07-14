package validation;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.validation.Validator.Method;
import de.mpg.imeji.logic.validation.impl.ProfileValidator;
import de.mpg.imeji.logic.vo.Metadata.Types;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.j2j.misc.LocalizedString;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProfileValidatorTest {

    private static final Logger logger = Logger.getLogger(ProfileValidator.class);

    private static ProfileValidator validator;
    private static MetadataProfile profile;


    @Before
    public void init() {
        validator = new ProfileValidator(Method.ALL);
        profile = new MetadataProfile();
        profile.setTitle("Profile");
    }

    @Test
    public void validateUniqueness_labels_2Statements() {
        Collection<Statement> statements = new ArrayList<>();
        Statement text1 = newStatement(Types.TEXT, "text", null);
        Statement text2 = newStatement(Types.TEXT, "text", null);
        statements.add(text1);
        statements.add(text2);
        profile.setStatements(statements);
        try {
            validator.validate(profile);
            fail("Validation of uniqueness... false positive");
        } catch (UnprocessableError e) {
            assertThat(e.getMessage(), equalTo("labels_have_to_be_unique"));
        }

    }

    @Test
    public void validateUniqueness_labels_1Statement() {
        Collection<Statement> statements = new ArrayList<>();
        Statement text1 = newStatement(Types.TEXT, "text", "text");
        statements.add(text1);
        profile.setStatements(statements);
        try {
            validator.validate(profile);
            fail("Validation of uniqueness... false positive");
        } catch (UnprocessableError e) {
            assertThat(e.getMessage(), equalTo("labels_have_to_be_unique"));
        }
    }

    @Test
    public void validateLabelSyntax_NumberHash() {
        Collection<Statement> statements = new ArrayList<>();
        statements.add(newStatement(Types.TEXT, "12345#text", null));
        profile.setStatements(statements);
        try {
            validator.validate(profile);
            fail("Validation of label syntax... false positive");
        } catch (UnprocessableError e) {
            assertThat(e.getMessage(), equalTo("error_profile_label_not_allowed"));
        }
    }

    @Test
    public void validateUniqueness_languages() {
        Statement s = new Statement();
        s.setType(URI.create(Types.TEXT.getClazzNamespace()));
        Collection<LocalizedString> labels = new ArrayList<>();
        labels.add(new LocalizedString("text1", "en"));
        labels.add(new LocalizedString("text2", "en"));
        s.setLabels(labels);
        profile.setStatements(Arrays.asList(s));
        try {
            validator.validate(profile);
            Assert.fail("Validation of label syntax... false positive");
        } catch (UnprocessableError e) {
            assertThat(e.getMessage(), equalTo("labels_duplicate_lang"));
        }

    }

    @Test
    @Ignore
    public void validateLabelSyntax_NotNumberNumberHash() {
        Collection<Statement> statements = new ArrayList<>();
        statements.add(newStatement(Types.TEXT, "abcd12345#text", null));
        profile.setStatements(statements);
        try {
            validator.validate(profile);
        } catch (UnprocessableError e) {
            fail("Unexpected error:" + e.getLocalizedMessage());
        }

    }

    /**
     * Create a new {@link Statement}
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

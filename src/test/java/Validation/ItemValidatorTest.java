package Validation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.imeji.exceptions.UnprocessableError;
import de.mpg.imeji.logic.util.MetadataFactory;
import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.validation.ValidatorFactory;
import de.mpg.imeji.logic.validation.impl.ItemValidator;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata.Types;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
import de.mpg.imeji.logic.vo.predefinedMetadata.Date;
import de.mpg.imeji.logic.vo.predefinedMetadata.Link;
import de.mpg.imeji.logic.vo.predefinedMetadata.Text;
import de.mpg.j2j.misc.LocalizedString;

/**
 * Test {@link ItemValidator}
 * 
 * @author saquet
 *
 */
public class ItemValidatorTest {

	private static MetadataProfile profile;
	private static Validator<Item> validator;
	private static Statement TEXT_PREDEFINED = newStatement(Types.TEXT, "text",
			Arrays.asList(ValidatorPredefinedValues.TEXT1.value(),
					ValidatorPredefinedValues.TEXT2.value()), false);
	private static Statement TEXT_PREDEFINED_MULTIPLE = newStatement(
			Types.TEXT, "text multiple", Arrays.asList(
					ValidatorPredefinedValues.TEXT1.value(),
					ValidatorPredefinedValues.TEXT2.value()), true);
	private static Statement NUMBER_PREDEFINED = newStatement(Types.NUMBER,
			"number predefined", Arrays.asList(
					ValidatorPredefinedValues.NUMBER1.value(),
					ValidatorPredefinedValues.NUMBER2.value()), false);
	private static Statement DATE_PREDEFINED = newStatement(Types.DATE,
			"date predefined", Arrays.asList(
					ValidatorPredefinedValues.DATE1.value(),
					ValidatorPredefinedValues.DATE2.value()), false);
	private static Statement LINK_PREDEFINED = newStatement(Types.LINK,
			"link predefined", Arrays.asList(
					ValidatorPredefinedValues.LINK1.value(),
					ValidatorPredefinedValues.LINK2.value()), false);

	/**
	 * Predefined values for the Unit Tests
	 * 
	 * @author saquet
	 *
	 */
	private static enum ValidatorPredefinedValues {
		TEXT1("value1"), TEXT2("value2"), NUMBER1("1"), NUMBER2("2"), DATE1(
				"2015-05-24"), DATE2("2015-05-28"), LINK1("http://test.org"), LINK2(
				"https://imeji.org");
		private String value;

		ValidatorPredefinedValues(String value) {
			this.value = value;
		}

		public String value() {
			return value;
		}
	}

	@BeforeClass
	public static void init() {
		initProfile();
		validator = (Validator<Item>) ValidatorFactory.newValidator(getItem());

	}

	@Test
	public void validateEmtpy() {
		try {
			validator.validate(getItem(), profile);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void validateWithCorrectPredefinedValues() {
		Item item = getItem();
		Text md1 = (Text) MetadataFactory.createMetadata(TEXT_PREDEFINED);
		md1.setText(ValidatorPredefinedValues.TEXT1.value());
		de.mpg.imeji.logic.vo.predefinedMetadata.Number md2 = (de.mpg.imeji.logic.vo.predefinedMetadata.Number) MetadataFactory
				.createMetadata(NUMBER_PREDEFINED);
		md2.setNumber(1);
		Date md3 = (Date) MetadataFactory.createMetadata(DATE_PREDEFINED);
		md3.setDate(ValidatorPredefinedValues.DATE1.value());
		Link md4 = (Link) MetadataFactory.createMetadata(LINK_PREDEFINED);
		md4.setUri(URI.create(ValidatorPredefinedValues.LINK1.value()));
		item.getMetadataSet().getMetadata().add(md1);
		item.getMetadataSet().getMetadata().add(md2);
		item.getMetadataSet().getMetadata().add(md3);
		item.getMetadataSet().getMetadata().add(md4);
		try {
			validator.validate(item, profile);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void validateWithWrongPredefinedValues() {
		Item item = getItem();
		// TEXT
		Text md1 = (Text) MetadataFactory.createMetadata(TEXT_PREDEFINED);
		md1.setText(ValidatorPredefinedValues.TEXT1.value() + "QWERTY");
		item.getMetadataSet().getMetadata().add(md1);
		try {
			validator.validate(item, profile);
			Assert.fail("Validation for Text with wrong predefined value not working");
		} catch (UnprocessableError e) {
			// good...
		}
		// NUMBER
		de.mpg.imeji.logic.vo.predefinedMetadata.Number md2 = (de.mpg.imeji.logic.vo.predefinedMetadata.Number) MetadataFactory
				.createMetadata(NUMBER_PREDEFINED);
		md2.setNumber(0);
		item.getMetadataSet().getMetadata().add(md2);
		try {
			validator.validate(item, profile);
			Assert.fail("Validation for Number with wrong predefined value not working");
		} catch (UnprocessableError e) {
			// good
		}
		// Date
		Date md3 = (Date) MetadataFactory.createMetadata(DATE_PREDEFINED);
		md3.setDate("2000");
		item.getMetadataSet().getMetadata().add(md3);
		try {
			validator.validate(item, profile);
			Assert.fail("Validation for Date with wrong predefined value not working");
		} catch (UnprocessableError e) {
			// good
		}
		// Link
		Link md4 = (Link) MetadataFactory.createMetadata(LINK_PREDEFINED);
		md4.setUri(URI.create(ValidatorPredefinedValues.LINK1.value()
				+ "/wrong"));
		item.getMetadataSet().getMetadata().add(md3);
		try {
			validator.validate(item, profile);
			Assert.fail("Validation for Link with wrong predefined value not working");
		} catch (UnprocessableError e) {
			// good
		}

	}

	private static Item getItem() {
		Item item = new Item();
		item.getMetadataSet().setProfile(profile.getId());
		return item;
	}

	/**
	 * Init the profile with {@link Statement}
	 */
	private static void initProfile() {
		profile = new MetadataProfile();
		profile.getStatements().add(TEXT_PREDEFINED);
		profile.getStatements().add(TEXT_PREDEFINED_MULTIPLE);
		profile.getStatements().add(NUMBER_PREDEFINED);
		profile.getStatements().add(DATE_PREDEFINED);
		profile.getStatements().add(LINK_PREDEFINED);
	}

	/**
	 * Create a new {@link Statement}
	 * 
	 * @param type
	 * @param label
	 * @param predefined
	 * @param multiple
	 * @return
	 */
	private static Statement newStatement(Types type, String label,
			List<String> predefined, boolean multiple) {
		Statement s = new Statement();
		s.setType(URI.create(type.getClazzNamespace()));
		Collection<LocalizedString> labels = new ArrayList<LocalizedString>();
		labels.add(new LocalizedString(label, "en"));
		s.setLabels(labels);
		s.setLiteralConstraints(predefined);
		return s;
	}

}

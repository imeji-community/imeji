package Validation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;




import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpg.imeji.logic.validation.Validator;
import de.mpg.imeji.logic.validation.ValidatorFactory;
import de.mpg.imeji.logic.validation.impl.ItemValidator;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.logic.vo.Metadata;
import de.mpg.imeji.logic.vo.Metadata.Types;
import de.mpg.imeji.logic.vo.MetadataProfile;
import de.mpg.imeji.logic.vo.Statement;
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

	@BeforeClass
	public static void init() {
		initProfile();
		validator = (Validator<Item>) ValidatorFactory.newValidator(getItem());

	}

	@Test
	public void validateEmtpyItem() {
		try {
			validator.validate(getItem(), profile);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	private static Item getItem() {
		Item item = new Item();
		item.getMetadataSet().setProfile(profile.getId());
		return item;
	}

	private static void initProfile() {
		profile = new MetadataProfile();
		addStatement(Types.TEXT, "text", Arrays.asList("value1", "value2"),
				false);
		addStatement(Types.TEXT, "text multiple",
				Arrays.asList("value1", "value2"), true);
		addStatement(Types.NUMBER, "Number", Arrays.asList("1", "2"), false);
	}

	private static void addStatement(Types type, String label,
			List<String> predefined, boolean multiple) {
		Statement s = new Statement();
		s.setType(URI.create(type.getClazzNamespace()));
		Collection<LocalizedString> labels = new ArrayList<LocalizedString>();
		labels.add(new LocalizedString(label, "en"));
		s.setLabels(labels);
		s.setLiteralConstraints(predefined);
		profile.getStatements().add(s);
	}

}

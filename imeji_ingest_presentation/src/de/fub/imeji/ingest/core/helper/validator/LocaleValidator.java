/**
 * 
 */
package de.fub.imeji.ingest.core.helper.validator;

import java.util.Locale;
import java.util.MissingResourceException;

/**
 * @author hnguyen
 *
 */
public class LocaleValidator {

	
	public static void setResources(String locale) {
		//validate locale
		Locale lo = parseLocale(locale);
		if (isValid(lo)) {
			System.out.println(lo.getDisplayCountry());
		} else {
			System.out.println("invalid: " + locale);
		}
	}
	
	public static boolean checkValidLanguage(String locale) {
		//validate locale
		Locale lo = parseLocale(locale);
		if (isValid(lo)) {
			return true;
		} else {
			return false;
		}
	}
	
	private static Locale parseLocale(String locale) {
		String[] parts = locale.split("_");
		switch (parts.length) {
			case 3: return new Locale(parts[0], parts[1], parts[2]);
			case 2: return new Locale(parts[0], parts[1]);
			case 1: return new Locale(parts[0]);
			default: throw new IllegalArgumentException("Invalid locale: " + locale);
		}
	}
	
	private static boolean isValid(Locale locale) {
		try {
			return locale.getISO3Language() != null && locale.getISO3Country() != null;
		} catch (MissingResourceException e) {
			return false;
		}
	}
	
}

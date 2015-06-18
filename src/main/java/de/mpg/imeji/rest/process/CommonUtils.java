package de.mpg.imeji.rest.process;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CommonUtils {
	
	public static final String FILENAME_RENAME_EMPTY = "Type a new filename if you want to rename it.";
	public static final String FILENAME_RENAME_INVALID_SUFFIX = "Invalid suffix.";
	public static final String JSON_Invalid ="Malformed json body in the request. Check the JSON body for correctness.";
	public static final String JAVAX_SERVLET_CONTEXT_TEMPDIR = "javax.servlet.context.tempdir";
	public static final String USER_MUST_BE_LOGGED_IN = "Need to be logged-in to proceed with the operation.";


	public static String formatDate(Date d) {
		String output = "";
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		output = f.format(d);
		f = new SimpleDateFormat("HH:mm:ss Z");
		output += "T" + f.format(d);
		return output;

	}

	public static String extractIDFromURI(URI uri) {
		return uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1);
	}

}

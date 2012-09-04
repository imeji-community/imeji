package thewebsemantic;

import java.io.Serializable;

import com.hp.hpl.jena.rdf.model.Literal;

public class LocalizedString implements Serializable {
	private String lang;
	private String value;
	
	public LocalizedString(String value, String lang) {
		this.lang = lang;
		this.value = value;
	}
	
	public LocalizedString(Literal l) {
		this((String)l.getValue(), l.getLanguage());
	}

   public String getLang() {
   	return lang;
   }

	public String toString() {
		return value;
	}
}

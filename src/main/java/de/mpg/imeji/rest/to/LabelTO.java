package de.mpg.imeji.rest.to;

public class LabelTO {
	private String language;
	private String value;
	
	public LabelTO(String lang, String value){
		this.language = lang;
		this.value = value;
	}
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}

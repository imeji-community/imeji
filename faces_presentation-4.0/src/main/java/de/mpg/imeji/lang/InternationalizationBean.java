package de.mpg.imeji.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;

public class InternationalizationBean 
{
	private List<SelectItem> languages = null;
	private String currentLanguage = "en";
	private SessionBean session = null;
	
	public InternationalizationBean() 
	{
		session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		languages = new ArrayList<SelectItem>();
		languages.add(new SelectItem("en", "English"));
		languages.add(new SelectItem("de", "Deutsch"));
	}

	public void setCurrentLanguage(String currentLanguage) 
	{
		this.currentLanguage = currentLanguage;
		session.setLocale(new Locale(currentLanguage));
	}
	
	public String getCurrentLanguage() {
		
		return currentLanguage;
	}
	
	public List<SelectItem> getLanguages() {
		return languages;
	}
	
	public void setLanguages(List<SelectItem> languages) {
		this.languages = languages;
	}
}

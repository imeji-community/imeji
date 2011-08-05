package de.mpg.imeji.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.PropertyReader;

public class InternationalizationBean 
{
	private List<SelectItem> languages = null;
	private String currentLanguage = "en";
	private SessionBean session = null;
	
	private List<SelectItem> internationalizedLanguages;
	
	public InternationalizationBean() 
	{
		session = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
				
		initLanguagesFromProperties();
		internationalizeLanguages();
	}
	
	private void initLanguagesFromProperties()
	{
		try 
		{
			languages = new ArrayList<SelectItem>();
			
			String p = PropertyReader.getProperty("imeji.i18n.languages");
			
			for (int i = 0; i < p.split(",").length; i++) 
			{
				languages.add(new SelectItem(p.split(",")[i].split("-")[0] , p.split(",")[i].split("-")[1] ));
			} 
			
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error reading property imeji.i18n.languages. Check Propety file: " + e);
		}
	}
	
	private void internationalizeLanguages()
	{
		internationalizedLanguages = new ArrayList<SelectItem>();
		for (SelectItem si : languages) 
		{
			internationalizedLanguages.add(new SelectItem(si.getValue(), session.getLabel(si.getLabel())));
		}
	}
	
	public void setCurrentLanguage(String currentLanguage) 
	{
		this.currentLanguage = currentLanguage;
		session.setLocale(new Locale(currentLanguage));
		internationalizeLanguages();
	}
	
	public String getCurrentLanguage() 
	{	
		return currentLanguage;
	}
	
	public List<SelectItem> getLanguages() 
	{
		return languages;
	}
	
	public void setLanguages(List<SelectItem> languages) 
	{
		this.languages = languages;
	}
	
	public List<SelectItem> getInternationalizedLanguages()
	{
		return internationalizedLanguages;
	}
	
	public void setInternationalizedLanguages(List<SelectItem> internationalizedLanguages) 
	{
		this.internationalizedLanguages = internationalizedLanguages;
	}
}

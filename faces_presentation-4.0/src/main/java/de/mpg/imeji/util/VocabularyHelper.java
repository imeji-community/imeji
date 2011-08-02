package de.mpg.imeji.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

public class VocabularyHelper
{
	private List<SelectItem> vocabularies = new ArrayList<SelectItem>();
	
	public VocabularyHelper() 
	{
		vocabularies.add(new SelectItem("", "--"));
		vocabularies.add(new SelectItem("http://pubman.mpdl.mpg.de/cone/persons/query", "Cone authors"));
    	vocabularies.add(new SelectItem("http://pubman.mpdl.mpg.de/cone/cclicences/query", "Cone CreativeCommons licenses"));
    	vocabularies.add(new SelectItem("http://pubman.mpdl.mpg.de/cone/journals/query", "Cone journals"));
    	vocabularies.add(new SelectItem("http://pubman.mpdl.mpg.de/cone/iso639-3/query", "Cone Languages (iso639-3)"));
    	vocabularies.add(new SelectItem("http://pubman.mpdl.mpg.de/cone/mimetypes/query", "Cone IANA Mimetypes"));
	}
	
	public String getVocabularyName(URI uri)
	{
		if (uri == null)
		{
			return null;
		}
		else
		{
			for (SelectItem voc : vocabularies)
			{
				if (voc.getValue().toString().equals(uri.toString()))
				{
					return voc.getLabel();
				}
			}
		}
		return "unknown";
	}
	
    public List<SelectItem> getVocabularies()
    {
    	return vocabularies;
    }
}

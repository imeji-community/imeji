package de.mpg.imeji.metadata;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.metadata.editors.SimpleImageEditor;
import de.mpg.imeji.metadata.util.MetadataHelper;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.concurrency.locks.Lock;
import de.mpg.jena.concurrency.locks.Locks;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class SingleEditBean 
{
	private Image image = null;
	private MetadataProfile profile = null;
	private	SimpleImageEditor editor = null;
	
	private Map<URI, Boolean> valuesMap =  new HashMap<URI, Boolean>();
	private String toggleState = "displayMd";
	
	private int mdPosition = 0;
	
	public SingleEditBean(Image im, MetadataProfile profile) 
	{
		image = im;
		this.profile = profile;
		if (UrlHelper.getParameterBoolean("edit")) this.toggleState = "editMd";
		else toggleState = "displayMd";
		this.init();
	}
	
	public void init()
	{
		List<Image> imAsList = new ArrayList<Image>();
		imAsList.add(image);
		
		for (Statement st : profile.getStatements())
		{
			valuesMap.put(st.getName(), false);
		}
		for (ImageMetadata md : image.getMetadataSet().getMetadata())
		{
			valuesMap.put(md.getNamespace(), true);
		}
		
		editor = new SimpleImageEditor(imAsList, profile, null);
	}
	
	public String save()
	{
		editor.getImages().clear();
		editor.getImages().add(image);
		editor.save();
		this.cancel();
		return "pretty:";
	}
	
	public String cancel()
	{
		this.toggleState = "displayMd";
		if (editor != null && !editor.getImages().isEmpty()) image = editor.getImages().get(0);
		for (int i=0; i < image.getMetadataSet().getMetadata().size(); i++)
		{
			if (MetadataHelper.isEmpty(((List<ImageMetadata>)image.getMetadataSet().getMetadata()).get(i)))
			{
				((List<ImageMetadata>)image.getMetadataSet().getMetadata()).remove(i);i--;
			}
		}
		SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		Locks.unLock(new Lock(this.image.getId().toString(), sb.getUser().getEmail()));
		this.init();
		return "pretty:";
	}
	
	public String showEditor()
	{
		this.toggleState = "editMd";
		SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		Locks.lock(new Lock(this.image.getId().toString(), sb.getUser().getEmail()));
		return "pretty:";
	}
	
	public String addMetadata()
	{
		editor.addMetadata(0, mdPosition);
		this.image = editor.getImages().get(0);
		init();
		return "";
	}
	
	public String removeMetadata()
	{
		editor.removeMetadata(0, mdPosition);
		this.image = editor.getImages().get(0);
		init();
		return "";
	}

	public SimpleImageEditor getEditor() {
		return editor;
	}

	public void setEditor(SimpleImageEditor editor) {
		this.editor = editor;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public MetadataProfile getProfile() {
		return profile;
	}

	public void setProfile(MetadataProfile profile) {
		this.profile = profile;
	}

	public Map<URI, Boolean> getValuesMap() {
		return valuesMap;
	}

	public void setValuesMap(Map<URI, Boolean> valuesMap) {
		this.valuesMap = valuesMap;
	}

	public int getMdPosition() {
		return mdPosition;
	}

	public void setMdPosition(int mdPosition) {
		this.mdPosition = mdPosition;
	}

	public String getToggleState() {
		return toggleState;
	}

	public void setToggleState(String toggleState) {
		this.toggleState = toggleState;
	}


}

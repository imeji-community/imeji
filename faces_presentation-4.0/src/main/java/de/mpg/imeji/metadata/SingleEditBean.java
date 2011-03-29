package de.mpg.imeji.metadata;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.imeji.metadata.editors.SimpleImageEditor;
import de.mpg.imeji.metadata.util.MetadataHelper;
import de.mpg.imeji.util.UrlHelper;
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
	private Map<URI, String> togglePanelState = new HashMap<URI, String>();
	private String toggleState = "displayMd";;
	
	private int mdPosition = 0;
	
	public SingleEditBean(Image im, MetadataProfile profile) 
	{
		image = im;
		this.profile = profile;
		for (Statement st : profile.getStatements())
		{
			togglePanelState.put(st.getName(), "displayMd");
		}
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
		
		if (UrlHelper.getParameterBoolean("edit")) this.toggleState = "editMd";
		else toggleState = "displayMd";
		
		editor = new SimpleImageEditor(imAsList, profile, null);
	}
	
	public String save()
	{
		editor.getImages().clear();
		editor.getImages().add(image);
		editor.save();
		this.toggleState = "displayMd";
		return "pretty:";
	}
	
	public String cancel()
	{
		this.toggleState = "displayMd";
		if (!editor.getImages().isEmpty()) image = editor.getImages().get(0);
		for (int i=0; i < image.getMetadataSet().getMetadata().size(); i++)
		{
			if (MetadataHelper.isEmpty(((List<ImageMetadata>)image.getMetadataSet().getMetadata()).get(i)))
			{
				((List<ImageMetadata>)image.getMetadataSet().getMetadata()).remove(i);i--;
			}
		}
		return "pretty:";
	}
	
	public String showEditor()
	{
		this.toggleState = "editMd";
		return "pretty:";
	}
	
	public String addMetadata()
	{
		editor.addMetadata(0, mdPosition);
		this.image = editor.getImages().get(0);
		togglePanelState.put(editor.getStatement().getName(), "editMd");
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

	public Map<URI, String> getTogglePanelState() {
		return togglePanelState;
	}

	public void setTogglePanelState(Map<URI, String> togglePanelState) {
		this.togglePanelState = togglePanelState;
	}

	public String getToggleState() {
		return toggleState;
	}

	public void setToggleState(String toggleState) {
		this.toggleState = toggleState;
	}


}

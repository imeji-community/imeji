package de.mpg.imeji.metadata;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import de.mpg.imeji.beans.Navigation;
import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.metadata.editors.SimpleImageEditor;
import de.mpg.imeji.metadata.util.MetadataHelper;
import de.mpg.imeji.metadata.util.SuggestBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.UrlHelper;
import de.mpg.jena.concurrency.locks.Lock;
import de.mpg.jena.concurrency.locks.Locks;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.security.Security;
import de.mpg.jena.util.MetadataFactory;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class SingleEditBean implements Serializable
{
	private Image image = null;
	private MetadataProfile profile = null;
	private	SimpleImageEditor editor = null;
	
	private Map<URI, Boolean> valuesMap =  new HashMap<URI, Boolean>();
	private String toggleState = "displayMd";
	
	private int mdPosition = 0;
	private String pageUrl ="";
		
	public SingleEditBean(Image im, MetadataProfile profile, String pageUrl) 
	{
		image = im;
		this.profile = profile;
		this.pageUrl = pageUrl;
		init();
	}
	
	public String getCheckToggleState()
	{
		toggleState = "displayMd";
		if (UrlHelper.getParameterBoolean("edit")) 
		{
			showEditor();
		}
		return "";
	}
	
	public void init()
	{
 		for (Statement st : profile.getStatements())
		{
			valuesMap.put(st.getName(), false);
		}
		for (ImageMetadata md : image.getMetadataSet().getMetadata())
		{
			valuesMap.put(md.getNamespace(), true);
		}
		for (Statement st : profile.getStatements())
		{
			if (!valuesMap.get(st.getName())) image.getMetadataSet().getMetadata().add(MetadataFactory.newMetadata(st));
			valuesMap.put(st.getName(), true);
		}
		List<Image> imAsList = new ArrayList<Image>();
		imAsList.add(image);
		editor = new SimpleImageEditor(imAsList, profile, null);
		((SuggestBean)BeanHelper.getSessionBean(SuggestBean.class)).init(profile);
	}
	
	public String save() throws Exception
	{
		cleanImageMetadata();
		editor.getImages().clear();
		editor.getImages().add(image);
		editor.save();
		reloadPage();
		cancel();
		return "";
	}

	public String cancel() throws Exception
	{
		this.toggleState = "displayMd";
		if (editor != null && !editor.getImages().isEmpty()) image = editor.getImages().get(0);
		{
			cleanImageMetadata();
		}
		SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		Locks.unLock(new Lock(this.image.getId().toString(), sb.getUser().getEmail()));
		reloadImage();
		return "";
	}
	
	private void reloadPage() throws IOException
	{
		FacesContext.getCurrentInstance().getExternalContext().redirect(pageUrl + "/view?init=1");
	}
	
	private void reloadImage()
	{
		SessionBean sessionBean = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		ImageController imageController = new ImageController(sessionBean.getUser());
		try 
		{
			image = imageController.retrieve(image.getId());
		} 
		catch (Exception e) 
		{
			BeanHelper.error(e.getMessage());
		}
	}

	private void cleanImageMetadata()
	{
		for (int i=0; i < image.getMetadataSet().getMetadata().size(); i++)
		{
			if (MetadataHelper.isEmpty(((List<ImageMetadata>)image.getMetadataSet().getMetadata()).get(i)))
			{
				((List<ImageMetadata>)image.getMetadataSet().getMetadata()).remove(i);i--;
			}
		}
	}
	
	public String showEditor()
	{
		SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		Security security = new Security();
		if (security.check(OperationsType.UPDATE, sb.getUser(), image))
		{
			this.toggleState = "editMd";
			Locks.lock(new Lock(image.getId().toString(), sb.getUser().getEmail()));
			init();
		}
		else
		{
			BeanHelper.error(sb.getMessage("error_editor_not_allowed"));
		}
		return "";
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

package de.mpg.imeji.metadata;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.image.SelectedBean;
import de.mpg.imeji.metadata.editors.MetadataBatchEditor;
import de.mpg.imeji.metadata.editors.MetadataEditor;
import de.mpg.imeji.metadata.editors.MetadataMultipleEditor;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.concurrency.locks.Lock;
import de.mpg.jena.concurrency.locks.Locks;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.controller.ImageController;
import de.mpg.jena.controller.ProfileController;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.security.Security;
import de.mpg.jena.vo.CollectionImeji;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.User;

public class EditWorkspaceBean
{
	enum EditorType{
		SINGLE, MULTIPLE, BATCH, SELECTED, ALL;
	}
	
	
	enum CitationStyle{
		APA, AJP, JUS;
	}
	
	private User user = null;
	private MetadataEditor editor = null;
	private List<MetadataProfile> profiles = null;

	private String selectedStatementName = null;
	private String selectedProfileId = null;
	private URI idOfImageToEdit = null;
	private EditorType type = EditorType.BATCH;
	private boolean eraseOldMetadata = false;
	private boolean editAllStatements = false;
	
	private int mdPosition = 0;
	private int imagePosition = 0;
	
	public EditWorkspaceBean() 
	{
		SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		user = sb.getUser();
		reset();
	}
	
	public void reset()
	{
		if (user != null) Locks.unlockAll(user.getEmail());
		profiles = new ArrayList<MetadataProfile>();
		type = EditorType.BATCH;
		eraseOldMetadata = false;
		editor = null;
		selectedProfileId = null;
		selectedStatementName = null;
		editAllStatements = false;
	}
	
	public void init(List<Image> images)
	{
		if (images == null) images = new ArrayList<Image>();
		images = removeNonEditableImages(images);
		lockImages(images);
		profiles = extractProfiles(images);
		MetadataProfile p = getSelectedProfile();
		Statement s = null;
		if (!editAllStatements) s = getSelectedStatement(p);
		editor = initEditor(images, p, s);
	}
	
	public String getInitPage()
	{
		reset();
		String typeString = (String) ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("type");
		
		if ("selected".equals(typeString)) init (retrieveAllSelectedImages());
		else if ("all".equals(typeString)) init(retrieveAllSelectedImages());
		return "";
	}
	
	public String getDefaultWorkspace()
	{
		reset();
		init(retrieveImages());
		return "";
	}
	
	public String save()
	{
		editor.setErase(eraseOldMetadata);
		editor.save();
		reset();
		return "pretty:";
	}
	
	public String cancel()
	{
		reset();
		return "pretty:";
	}
	
	public String requestEditor(ActionEvent event)
	{
		reset();
		try
		{
			Map<String, Object> attrs = event.getComponent().getAttributes();
			selectedStatementName = readAttribute(attrs,"statementName");
			selectedProfileId = readAttribute(attrs,"profileId");
			idOfImageToEdit = URI.create(readAttribute(attrs,"idOfImageToEdit"));
			editAllStatements = Boolean.parseBoolean(readAttribute(attrs,"editAllStatements"));
			type = EditorType.valueOf(readAttribute(attrs,"type"));
		}
		catch (Exception e) 
		{
			BeanHelper.error("Error setting request value of Metadata Editor:" + e.getMessage());
		}
		init(retrieveImages());
		return "";
	}
	
	public MetadataEditor initEditor(List<Image> images, MetadataProfile profile, Statement statement)
	{
		if (profile != null)
		{
			switch (type) 
			{
				case MULTIPLE:
					return new MetadataMultipleEditor(images, profile, statement);
				case SINGLE:
					return new MetadataMultipleEditor(images, profile, statement);
				case BATCH:
					return  new MetadataBatchEditor(images, profile, statement);
				default:
					return null;
			}
		}
		return null;
	}
	
	public Statement getSelectedStatement(MetadataProfile p)
	{
		if (p != null)
		{
			for (Statement s : p.getStatements())
			{
				if (s.getName().toString().equals(selectedStatementName))
				{
					return s;
				}
			}
		}
		return getDefaultStatement();
	}
	
	public Statement getDefaultStatement()
	{
		if (profiles.size() > 0 && profiles.get(0).getStatements().iterator().hasNext())
		{
			return profiles.get(0).getStatements().iterator().next();
		}
		return null;
	}
	
	public MetadataProfile getSelectedProfile()
	{
		for (MetadataProfile p : profiles)
		{
			if (p.getId().toString().equals(selectedProfileId))
			{
				return p;
			}
		}
		return getDefaultProfile();
	}
	
	public MetadataProfile getDefaultProfile()
	{
		if (profiles.size() > 0) return profiles.get(0);
		return null;
	}
	
	public void lockImages(List<Image> images)
	{
		for (Image im : images)
		{
			Locks.lock(new Lock(im.getId().toString(), user.getEmail()));
		}
	}

	public List<Image> retrieveImages()
	{
		List<Image> images = new ArrayList<Image>();
		switch (type) 
		{
			case MULTIPLE: return retrieveAllSelectedImages(); 
			case SINGLE: return retrieveSingleImage(idOfImageToEdit); 
			case BATCH: return retrieveAllSelectedImages();
			default: return images;
		}
	}
	
	public List<Image> removeNonEditableImages(List<Image> l)
	{
		Security security = new Security();
		for (int i=0; i < l.size(); i++)			
		{
			if (!security.check(OperationsType.UPDATE, user, l.get(i)))
			{
				l.remove(i);
				i--;
			}
		}
		return l;
	}
	
	public List<MetadataProfile> extractProfiles(List<Image> l)
	{
		List<MetadataProfile> list = new ArrayList<MetadataProfile>();
		List<String> cl = new ArrayList<String>();
		List<String> pl = new ArrayList<String>();
		try 
		{
			for (Image im :l)
			{
				if (!cl.contains(im.getCollection().toString()))
				{
					cl.add(im.getCollection().toString());
					CollectionController cc = new CollectionController(user);
					CollectionImeji c = cc.retrieve(im.getCollection());
					if (!cl.contains(c.getProfile().toString()))
					{
						pl.add(c.getProfile().toString());
						ProfileController pc = new ProfileController(user);
						list.add(pc.retrieve(c.getProfile()));
					}
				}
			}
		}
		catch (Exception e) 
		{
			BeanHelper.error("Error reading images profile!");
		}
		return list;
	}
	
	/**
	 * Retrieve all the selected images.
	 */
	private List<Image> retrieveAllSelectedImages()
	{
		SelectedBean selectedBean = (SelectedBean) BeanHelper.getSessionBean(SelectedBean.class);
		return (List<Image>) selectedBean.getImages();
	}
	
	/**
	 * Retrieve only the image according to requested id.
	 */
	private List<Image> retrieveSingleImage(URI idOfImageToEdit2)
	{
		ImageController ic = new ImageController(user);
		List<Image> l = new ArrayList<Image>();
		l.add(ic.retrieve(idOfImageToEdit2));
		return l;
	}
	
	public String addMetadata()
    {
		editor.addMetadata(getImagePosition(), getMdPosition());
		return "";
    }
	
	public String removeMetadata()
	{
		editor.removeMetadata(getImagePosition(), getMdPosition());
		return "";
	}
    
    public String readAttribute(Map<String, Object> attrs, String name)
	{
		Object o = attrs.get(name);
		if (o != null) return o.toString();
		return "";
	}

	public int getMdPosition() {
		return mdPosition;
	}

	public void setMdPosition(int mdPosition) {
		this.mdPosition = mdPosition;
	}

	public int getImagePosition() {
		return imagePosition;
	}

	public void setImagePosition(int imagePosition) {
		this.imagePosition = imagePosition;
	}

	public EditorType getType() {
		return type;
	}

	public void setType(EditorType type) {
		this.type = type;
	}

	public MetadataEditor getEditor() {
		return editor;
	}

	public void setEditor(MetadataEditor editor) {
		this.editor = editor;
	}

	public List<SelectItem> getCitationStyles() {
		List<SelectItem> l = new ArrayList<SelectItem>();
		for (CitationStyle str : CitationStyle.values()) 
		{
			l.add(new SelectItem(str.name(), str.name()));
		}
		return l;
	}

	public boolean isEraseOldMetadata() {
		return eraseOldMetadata;
	}

	public void setEraseOldMetadata(boolean eraseOldMetadata) {
		this.eraseOldMetadata = eraseOldMetadata;
	}
	
	public List<MetadataProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<MetadataProfile> profiles) {
		this.profiles = profiles;
	}

	public String getSelectedStatementName() {
		return selectedStatementName;
	}

	public void setSelectedStatementName(String selectedStatementName) {
		this.selectedStatementName = selectedStatementName;
	}

	public String getSelectedProfileId() {
		return selectedProfileId;
	}

	public void setSelectedProfileId(String selectedProfileId) {
		this.selectedProfileId = selectedProfileId;
	}

	public boolean isEditAllStatements() {
		return editAllStatements;
	}

	public void setEditAllStatements(boolean editAllStatements) {
		this.editAllStatements = editAllStatements;
	}
	
}

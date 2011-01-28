package de.mpg.imeji.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.image.SelectedBean;
import de.mpg.imeji.metadata.editors.MetadataBatchEditor;
import de.mpg.imeji.metadata.editors.MetadataEditor;
import de.mpg.imeji.metadata.editors.MetadataMultipleEditor;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.jena.security.Operations.OperationsType;
import de.mpg.jena.security.Security;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class EditWorkspaceBean
{
	enum EditorType{
		SINGLE, MULTIPLE, BATCH;
	}
	
	enum CitationStyle{
		APA, AJP, JUS;
	}
	
	private String statementToEdit = null;
	private String idOfImageToEdit = null;
	private List<Image> images = null;
	private int mdPosition = 0;
	private int imagePosition = 0;
	private EditorType type = EditorType.BATCH;
	private MetadataEditor editor = null;
	private List<SelectItem> citationStyles;
	private boolean eraseOldMetadata = false;
	
	public EditWorkspaceBean() 
	{
		this.init();
	}
	
	public void init()
	{
		images = new ArrayList<Image>();
		idOfImageToEdit = null;
		type = EditorType.BATCH;
		citationStyles = new ArrayList<SelectItem>();
		eraseOldMetadata = false;
		editor= null;
		statementToEdit = null;
		for (CitationStyle str : CitationStyle.values()) 
		{
			citationStyles.add(new SelectItem(str.name(), str.name()));
		}
	}
	
	public String getDefaultInit()
	{
		this.init();
		initialize();
		return "";
	}
	
	public String initTrigger(ActionEvent event)
	{
		this.init();
		if(event.getComponent().getAttributes().get("statementName") != null)
		{
			statementToEdit = event.getComponent().getAttributes().get("statementName").toString();
		}
		else
		{
			statementToEdit = null;
		}
		if (event.getComponent().getAttributes().get("idOfImageToEdit") != null) 
		{
			idOfImageToEdit = event.getComponent().getAttributes().get("idOfImageToEdit").toString();
		}
		if (event.getComponent().getAttributes().get("type")!= null)
		{
			type = EditorType.valueOf(event.getComponent().getAttributes().get("type").toString());
		}
		this.initialize();
		return "pretty:";
	}
	
	public void initialize()
	{	
		initImages();
		Statement st = getStatement();
		if (st != null && isAllowedToEdit())
		{
			initEditor(st);
		}
	}
	
	public void initEditor(Statement statement)
	{
	  	Statement st = ProfileHelper.loadStatement(images.get(0), statementToEdit);
		if (images.size() > 0) 
		switch (type) 
		{
			case MULTIPLE:
				editor = new MetadataMultipleEditor(images, ProfileHelper.loadProfile(images.get(0)), st);
				break;			
			case SINGLE:
				editor = new MetadataMultipleEditor(images, ProfileHelper.loadProfile(images.get(0)), st);
				break;
			case BATCH:
				editor = new MetadataBatchEditor(images, ProfileHelper.loadProfile(images.get(0)), st);
				break;
		}
	}
	
	public Statement getStatement()
	{
		if (statementToEdit == null) 
		{
			statementToEdit = getDefaultStatement();
		}
		return ProfileHelper.loadStatement(images.get(0), statementToEdit);
	}
	
	public void initImages()
	{
		images = new ArrayList<Image>();
		switch (type) 
		{
			case MULTIPLE: retrieveAllSelectedImages(); 
				break;	
			case SINGLE: retrieveSingleImage(); 
				break;
			case BATCH: retrieveAllSelectedImages();
				break;
		}
	}
	
	public boolean isAllowedToEdit()
	{
		Security security = new Security();
		SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		for (Image im :images) 
		{
			if (security.check(OperationsType.UPDATE, sb.getUser(), im)) return true;
		}
		return false;
	}
	
	/**
	 * Find the first statement of the first images
	 * @return
	 */
	private String getDefaultStatement()
	{
		if (images.size() > 0)
		{
			MetadataProfile p = ProfileHelper.loadProfile(images.get(0));
			if (p.getStatements().size() > 0)
			{
				return p.getStatements().get(0).getName();
			}
		}
		return null;
	}
	
	/**
	 * Retrieve all the selected images.
	 */
	private void retrieveAllSelectedImages()
	{
		SelectedBean selectedBean = (SelectedBean) BeanHelper.getSessionBean(SelectedBean.class);
		try 
		{
			for (ImageBean bean : selectedBean.retrieveList(0, 10000))
			{
				images.add(bean.getImage());
			}
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error retrieving selected images", e);
		}
	}
	
	/**
	 * Retrieve only the image according to requested id.
	 */
	private void retrieveSingleImage()
	{
		ImageBean imageBean = (ImageBean) BeanHelper.getSessionBean(ImageBean.class);
		String id = idOfImageToEdit.split("image/")[1];
		imageBean.setId(id);
		try 
		{
			imageBean.init();
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error initializing Edit Workspace for image " + idOfImageToEdit + " :", e);
		}
		images.add(imageBean.getImage());
	}
	
	public String cancel()
	{
		this.statementToEdit = null;
		this.type = EditorType.BATCH;
		this.images.clear();
		editor = null;
		return "pretty:";
	}
	
	public String save()
	{
		editor.setErase(eraseOldMetadata);
		editor.save();
		this.cancel();
		return "pretty:";
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
	
    public List<SelectItem> getStatementMenu()
    {
    	List<SelectItem> statementMenu = new ArrayList<SelectItem>();
    	for (MetadataProfile p : ProfileHelper.loadProfiles(images).values())
    	{
    		 for (Statement s : p.getStatements())
    	     {
    	       	 statementMenu.add(new SelectItem(s.getName(), s.getName()));
    	     }
    	}
    	return statementMenu;
    }

	public String getStatementToEdit() 
	{
		return statementToEdit;
	}

	public void setStatementToEdit(String statementToEdit) 
	{
		this.statementToEdit = statementToEdit;
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
		return citationStyles;
	}

	public void setCitationStyles(List<SelectItem> citationStyles) {
		this.citationStyles = citationStyles;
	}

	public boolean isEraseOldMetadata() {
		return eraseOldMetadata;
	}

	public void setEraseOldMetadata(boolean eraseOldMetadata) {
		this.eraseOldMetadata = eraseOldMetadata;
	}
	
}

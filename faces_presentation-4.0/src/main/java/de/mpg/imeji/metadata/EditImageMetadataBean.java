package de.mpg.imeji.metadata;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.collection.CollectionImagesBean;
import de.mpg.imeji.image.ImagesBean;
import de.mpg.imeji.image.SelectedBean;
import de.mpg.imeji.lang.labelHelper;
import de.mpg.imeji.metadata.editors.MetadataEditor;
import de.mpg.imeji.metadata.editors.MetadataMultipleEditor;
import de.mpg.imeji.metadata.util.MetadataHelper;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.jena.concurrency.locks.Lock;
import de.mpg.jena.concurrency.locks.Locks;
import de.mpg.jena.util.MetadataFactory;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;

public class EditImageMetadataBean 
{
	//objects
	private ImagesBean imagesBean;
	private MetadataEditor editor = null;
	private MetadataProfile profile = null;
	private Statement statement = null;
	private ImageMetadata metadata = null;
	private Collection<MetadataProfile> profiles = new ArrayList<MetadataProfile>();
	
	// menus
	private List<SelectItem> statementMenu =null;
	private List<SelectItem> profileMenu = null;
	private String selectedStatementName = null;
	private String selectedProfileName = null;
	private List<SelectItem> modeRadio = null;
	private String selectedMode = "basic";
	
	
	//other
	private int mdPosition;
	private int imagePosition;
	private String editType = "selected";
	
	public EditImageMetadataBean() 
	{
		statementMenu = new ArrayList<SelectItem>();
		profileMenu = new ArrayList<SelectItem>();
		modeRadio = new ArrayList<SelectItem>();
	}
	
	public String getInit()
	{
		try 
		{
			initImagesBean();
			initMenus();
			profile = getSelectedProfile();
			statement = getSelectedStatement();
		    metadata = MetadataFactory.newMetadata(statement);
			editor = new MetadataMultipleEditor((List<Image>) imagesBean.getImages(), getSelectedProfile(), getSelectedStatement());
		}
		catch (Exception e) 
		{
			BeanHelper.error("Error initializing page");
			e.printStackTrace();
		}
		return "";
	}
	
	public String getAjaxInit()
	{
		lockImages();
		profile = getSelectedProfile();
		statement = getSelectedStatement();
		editor = new MetadataMultipleEditor((List<Image>) imagesBean.getImages(), getSelectedProfile(), getSelectedStatement());
		
		modeRadio = new ArrayList<SelectItem>();
		modeRadio.add(new SelectItem("basic", "Write only when no values"));
		if (this.statement.getMaxOccurs().equals("unbounded"))	modeRadio.add(new SelectItem("append", "Append a new value to all"));
		modeRadio.add(new SelectItem("overwrite", "Overwrite all values"));
		return "";
	}
	
	public void initImagesBean() throws Exception
	{
		editType = (String) ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("type");
		int elementsPerPage = 24;
		if ("selected".equals(editType))
		{
			imagesBean = (SelectedBean) BeanHelper.getSessionBean(SelectedBean.class);
		}
		else if ("all".equals(editType))
		{
			imagesBean = (CollectionImagesBean)BeanHelper.getSessionBean(CollectionImagesBean.class);
			elementsPerPage = imagesBean.getElementsPerPage();
			imagesBean.setElementsPerPage(10000);
		}
		imagesBean.update();
		imagesBean.setElementsPerPage(elementsPerPage);
		profiles =  ProfileHelper.loadProfiles((List<Image>) imagesBean.getImages()).values();
	}
	
	public void initMenus()
	{
		statementMenu = new ArrayList<SelectItem>();
		profileMenu = new ArrayList<SelectItem>();
		
		for(MetadataProfile p : profiles)
		{
			profileMenu.add(new SelectItem(p.getId().toString(), p.getTitle()));
		}
		if (getSelectedProfile() == null || getSelectedProfile().getStatements().isEmpty()) statementMenu.add(new SelectItem("No statements found"));
		for(Statement s: getSelectedProfile().getStatements())
		{
			statementMenu.add(new SelectItem(s.getName().toString(), labelHelper.getDefaultLabel(s.getLabels().iterator())));
		}
		selectedMode = "basic";
		
	}
	
	public String cancel()
	{
		unlockImages();
		return "";
	}
	
	private void lockImages()
	{
		SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		for (Image im : imagesBean.getImages())
		{
			Locks.lock(new Lock(im.getId().toString(), sb.getUser().getEmail()));
		}
	}
	
	private void unlockImages()
	{
		SessionBean sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
		for (Image im : imagesBean.getImages())
		{
			Locks.unLock(new Lock(im.getId().toString(), sb.getUser().getEmail()));
		}
	}
	
	public String addToAll()
	{
		for (Image im : editor.getImages())
		{
			ImageMetadata newMD = MetadataFactory.newMetadata(metadata);
			if ("overwrite".equals(selectedMode)) 
			{
				im = removeAllMetadata(im);
				im.getMetadataSet().getMetadata().add(newMD);
			}
			else if ("append".equals(selectedMode)) im.getMetadataSet().getMetadata().add(newMD);
			else if ("basic".equals(selectedMode))addMetadataIfNotExists(im, newMD);
		}
		metadata =  MetadataFactory.newMetadata(statement);
		return "";
	}
	
	public String addToAllAndSave()
	{
		addToAll();
		editor.save();
		return "";
	}
	
	public String clearAll()
	{
		for (Image im : editor.getImages())
		{
			im = removeAllMetadata(im);
		}
		return "";
	}
	
	private Image addMetadataIfNotExists(Image im, ImageMetadata metadata)
	{	
		boolean hasValue = false;
		int i = 0;
		for (ImageMetadata md : im.getMetadataSet().getMetadata()) 
		{
			if (md.getNamespace().equals(metadata.getNamespace()))
			{
				if (MetadataHelper.isEmpty(md)) ((List<ImageMetadata>) im.getMetadataSet().getMetadata()).set(i, metadata);
				hasValue = true;
			}
			i++;
		}
		if (!hasValue)
		{
			im.getMetadataSet().getMetadata().add(metadata);
		}
		return im;
	}
	
	
	private Image removeAllMetadata(Image im)
	{
		for(int i=0; i<im.getMetadataSet().getMetadata().size(); i++)
		{
			if (((List<ImageMetadata>)im.getMetadataSet().getMetadata()).get(i).getNamespace() == null ||
					((List<ImageMetadata>)im.getMetadataSet().getMetadata()).get(i).getNamespace().equals(metadata.getNamespace()))
			{
				((List<ImageMetadata>)im.getMetadataSet().getMetadata()).remove(i);
				i--;
			}
				
		}
		return im;
	}
	
	public MetadataProfile getSelectedProfile()
	{
		for(MetadataProfile p : profiles)
		{
			if (p.getId().equals(selectedProfileName)) return p;
		}
		return getDefaultProfile();
	}
	
	public MetadataProfile getDefaultProfile()
	{
		if (!profiles.isEmpty())
		{
			return profiles.iterator().next();
		}
		return null;
	}
	
	public Statement getSelectedStatement()
	{
		if (profile != null)
		{
			for (Statement s : profile.getStatements())
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
		if (profile != null &&  profile.getStatements().iterator().hasNext())
		{
			return profile.getStatements().iterator().next();
		}
		return null;
	}
	
	public void profileListener(ValueChangeEvent event)
	{
		if (event != null && event.getNewValue() != event.getOldValue())
		{
			selectedProfileName = (String) event.getNewValue();
			profile = getSelectedProfile();
			editor.setProfile(profile);
			statement = getSelectedStatement();
			editor.setStatement(statement);
		}
	}

	public void statementListener(ValueChangeEvent event)
	{
		if (event != null && event.getNewValue() != event.getOldValue())
		{
			selectedStatementName = (String) event.getNewValue();
			statement = getSelectedStatement();
			metadata = MetadataFactory.newMetadata(statement);
			editor.setStatement(statement);
		}
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

	public MetadataEditor getEditor() {
		return editor;
	}

	public void setEditor(MetadataEditor editor) {
		this.editor = editor;
	}

	public ImagesBean getImagesBean() {
		return imagesBean;
	}

	public void setImagesBean(ImagesBean imagesBean) {
		this.imagesBean = imagesBean;
	}

	public List<SelectItem> getStatementMenu() {
		return statementMenu;
	}

	public void setStatementMenu(List<SelectItem> statementMenu) {
		this.statementMenu = statementMenu;
	}

	public List<SelectItem> getProfileMenu() {
		return profileMenu;
	}

	public void setProfileMenu(List<SelectItem> profileMenu) {
		this.profileMenu = profileMenu;
	}

	public String getSelectedStatementName() {
		return selectedStatementName;
	}

	public void setSelectedStatementName(String selectedStatementName) {
		this.selectedStatementName = selectedStatementName;
	}

	public String getSelectedProfileName() {
		return selectedProfileName;
	}

	public void setSelectedProfileName(String selectedProfileName) {
		this.selectedProfileName = selectedProfileName;
	}

	public ImageMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(ImageMetadata metadata) {
		this.metadata = metadata;
	}

	public MetadataProfile getProfile() {
		return profile;
	}

	public void setProfile(MetadataProfile profile) {
		this.profile = profile;
	}

	public List<SelectItem> getModeRadio() {
		return modeRadio;
	}

	public void setModeRadio(List<SelectItem> modeRadio) {
		this.modeRadio = modeRadio;
	}

	public String getSelectedMode() {
		return selectedMode;
	}

	public void setSelectedMode(String selectedMode) {
		this.selectedMode = selectedMode;
	}

	public String getEditType() {
		return editType;
	}

	public void setEditType(String editType) {
		this.editType = editType;
	}
	
	
}

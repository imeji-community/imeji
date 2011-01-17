package de.mpg.imeji.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.xml.security.utils.HelperNodeList;

import de.mpg.imeji.beans.SessionBean;
import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.image.ImagesBean;
import de.mpg.imeji.image.SelectedBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.imeji.util.ProfileHelper;
import de.mpg.jena.controller.CollectionController;
import de.mpg.jena.vo.Image;
import de.mpg.jena.vo.ImageMetadata;
import de.mpg.jena.vo.MetadataProfile;
import de.mpg.jena.vo.Statement;
import de.mpg.jena.vo.complextypes.util.ComplexTypeHelper;

public class EditWorkspaceBean
{
	enum EditorType{
		SINGLE, MULTIPLE, BATCH, DEFAULT;
	}
	
	private String statementToEdit = null;
	private String idOfImageToEdit = null;
	private List<ImageBean> images = null;
	private int mdPosition = 0;
	private int imagePosition = 0;
	private EditorType type = EditorType.DEFAULT;
	private EditMetadataBean editMetadataBean = null;
	
	public EditWorkspaceBean() 
	{
		this.init();
	}
	
	public void init()
	{
		images = new ArrayList<ImageBean>();
		idOfImageToEdit = null;
		type = EditorType.DEFAULT;
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
		statementToEdit = event.getComponent().getAttributes().get("statementName").toString();
		type = EditorType.valueOf(event.getComponent().getAttributes().get("type").toString());
		if (event.getComponent().getAttributes().get("idOfImageToEdit") != null) 
			idOfImageToEdit = event.getComponent().getAttributes().get("idOfImageToEdit").toString();
		this.initialize();
		return "pretty:";
	}
	
	public void initialize()
	{
		switch (type) 
		{
			case BATCH:
				initWorkspaceForAllSelectedImages();
				prepareBatchEdit();
				break;
			case MULTIPLE:
				initWorkspaceForAllSelectedImages();
				prepareClassicalEdit(false);
				break;				
			case SINGLE:
				initWorkspaceForOneImage();
				prepareClassicalEdit(true);
				break;
			case DEFAULT:
				initWorkspaceForAllSelectedImages();
				if (images.size() == 0) break;
				List<SelectItem> stList = images.get(0).getEditMetadataBean().getStatementMenu();
				if (stList.size() == 0) break;
				statementToEdit = stList.get(0).getLabel();
				prepareBatchEdit();
				break;
		}
	}
	
	/**
	 * Prepare Edit Formular for a Batch edit
	 */
	private void prepareBatchEdit()
	{
		List<Image> imageList = new ArrayList<Image>();
		ImageBean imBean = null;
		for (ImageBean imb: images) 
		{
			imageList.add(imb.getImage());
			if(imBean == null) imBean = imb;
		}
		images.clear();
		if(imBean != null)
		{
			images.add(imBean);
			images.get(0).setEditMetadataBean(new EditMetadataBean(imageList));
			images.get(0).getEditMetadataBean().getMetadata().clear();
			images.get(0).getEditMetadataBean().addMetadata(0, statementToEdit);
		}
		System.out.println("batch");
	}
	
	/**
	 * Prepare Edit formular for a one to one edit.
	 */
	private void prepareClassicalEdit(boolean overwrite)
	{
		for (ImageBean imb: images) 
		{
			List<ImageMetadata> mdToEditList = new ArrayList<ImageMetadata>();
			for (ImageMetadata immd :imb.getImgMetadata()) 
			{
				if (statementToEdit.equals(immd.getName())) 
				{
					mdToEditList.add(immd);
				}
			}
			if (mdToEditList.size() == 0) 
			{
				Statement st = ProfileHelper.loadStatement(imb.getImage(), statementToEdit);
				mdToEditList.add(new ImageMetadata(st.getName(),  ComplexTypeHelper.newComplexType(st.getType())));
			}
			
			imb.initEditMetadataBean(mdToEditList);
			imb.getEditMetadataBean().setPrettyLink("");
			imb.getEditMetadataBean().setOverwrite(overwrite);
		}
	}
	
	private void initWorkspaceForAllSelectedImages()
	{
		SelectedBean selectedBean = (SelectedBean) BeanHelper.getSessionBean(SelectedBean.class);
		try 
		{
			this.images = selectedBean.retrieveList(0, 10000);
		} 
		catch (Exception e) 
		{
			throw new RuntimeException("Error initializing Edit Workspace", e);
		}
	}
	
	private void initWorkspaceForOneImage()
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
		images.add(imageBean);
	}
	
	public String cancel()
	{
		this.statementToEdit = null;
		this.type = EditorType.BATCH;
		this.images.clear();
		return "pretty:";
	}
	
	public String save()
	{
		if (EditorType.BATCH.equals(type))
		{
			images.get(0).getEditMetadataBean().save();
		}
		else
		{
			for(ImageBean imb : images)
			{
				if (hasBeenModified(imb)) 
				{
					imb.getEditMetadataBean().save();
				}
			}
		}
		this.cancel();
		return "pretty:selected";
	}
	
	public boolean hasBeenModified(ImageBean modified)
	{
		// Could be used later on for faster update:
		// Images which haven't been modified don't have to be saved
		return true;
	}

	public String getStatementToEdit() 
	{
		return statementToEdit;
	}

	public void setStatementToEdit(String statementToEdit) 
	{
		this.statementToEdit = statementToEdit;
	}

	public List<ImageBean> getImages() {
		return images;
	}

	public void setImages(List<ImageBean> images) {
		this.images = images;
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
	
	
	
}

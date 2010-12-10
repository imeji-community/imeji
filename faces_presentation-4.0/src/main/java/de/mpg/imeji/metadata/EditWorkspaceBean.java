package de.mpg.imeji.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import de.mpg.imeji.image.ImageBean;
import de.mpg.imeji.image.SelectedBean;
import de.mpg.imeji.util.BeanHelper;
import de.mpg.jena.vo.Image;

public class EditWorkspaceBean
{
	private String statementToEdit = null;
	private String idOfImageToEdit = null;
	private List<ImageBean> images = null;
	private EditMetadataBean editMetadataBean = null;
	
	public EditWorkspaceBean() 
	{
		this.init();
	}
	
	public void init()
	{
		images = new ArrayList<ImageBean>();
		editMetadataBean = new EditMetadataBean();
		idOfImageToEdit = null;
	}
	
	public String init(ActionEvent event)
	{
		this.init();
		statementToEdit = event.getComponent().getAttributes().get("statementName").toString();
		idOfImageToEdit = event.getComponent().getAttributes().get("idOfImageToEdit").toString();
		if ("selected".equals(idOfImageToEdit)) 
		{
			initWorkspaceForAllSelectedImages();
		}
		else if(idOfImageToEdit != null)
		{
			initWorkspaceForOneImage();
		}
		List<Image> imagesToEdit = new ArrayList<Image>();
		for(ImageBean imb : images)
		{
			imb.initEditMetadataBean();
			ImageBean copy = new ImageBean(imb.getImage());
			copy.initEditMetadataBean();
			imb.getEditMetadataBean().getMetadata().clear();
			for(MetadataBean mdb : copy.getEditMetadataBean().getMetadata())
			{
				if(mdb.getSelectedStatementName().equals(statementToEdit))
				{
					imb.getEditMetadataBean().getMetadata().add(mdb);
				}
			}
			imagesToEdit.add(imb.getImage());
		}
		return "";
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
		System.out.println("id = " + id);
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

	public EditMetadataBean getEditMetadataBean() {
		return editMetadataBean;
	}

	public void setEditMetadataBean(EditMetadataBean editMetadataBean) {
		this.editMetadataBean = editMetadataBean;
	}

	
	
}

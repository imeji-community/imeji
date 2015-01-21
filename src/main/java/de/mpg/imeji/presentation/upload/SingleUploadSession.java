package de.mpg.imeji.presentation.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.presentation.lang.MetadataLabels;
import de.mpg.imeji.presentation.mdProfile.SuperStatementBean;


@ManagedBean(name = "SingleUploadSession")
@SessionScoped
public class SingleUploadSession {
	
	private String selectedCollectionItem;
	private CollectionImeji collection;
	private MetadataLabels labels;
	private File file;
	private List<String> techMD = new ArrayList<String>();
	private List<SuperStatementBean> sts;
	private boolean uploadFileToTemp = false;
	private boolean uploadFileToItem = false;
	
	public void reset()
	{
		selectedCollectionItem = "";
		collection = null;
		file = null;
		techMD.clear();;
		sts = null;
		labels = null;
		uploadFileToTemp = false;
		uploadFileToItem = false;		
	}
	
	public void uploadedToTemp()
	{
		uploadFileToTemp = true;
		uploadFileToItem = false;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public List<String> getTechMD() {
		return techMD;
	}

	public void setTechMD(List<String> techMD) {
		this.techMD = techMD;
	}

	public boolean isUploadFileToItem() {
		return uploadFileToItem;
	}

	public void setUploadFileToItem(boolean uploadFileToItem) {
		this.uploadFileToItem = uploadFileToItem;
	}

	public List<SuperStatementBean> getSts() {
		return sts;
	}

	public void setSts(List<SuperStatementBean> sts) {
		this.sts = sts;
	}

	public boolean isUploadFileToTemp() {
		return uploadFileToTemp;
	}

	public void setUploadFileToTemp(boolean uploadFileToTemp) {
		this.uploadFileToTemp = uploadFileToTemp;
	}

	public String getSelectedCollectionItem() {
		return selectedCollectionItem;
	}

	public void setSelectedCollectionItem(String selectedCollectionItem) {
		this.selectedCollectionItem = selectedCollectionItem;
	}

	public CollectionImeji getCollection() {
		return collection;
	}

	public void setCollection(CollectionImeji collection) {
		this.collection = collection;
	}

	public MetadataLabels getLabels() {
		return labels;
	}

	public void setLabels(MetadataLabels labels) {
		this.labels = labels;
	}
	
	
	
	
	
	
}

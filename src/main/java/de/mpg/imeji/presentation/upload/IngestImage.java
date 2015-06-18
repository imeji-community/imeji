package de.mpg.imeji.presentation.upload;

import java.io.File;
import java.io.Serializable;

public class IngestImage implements Serializable{
	private static final long serialVersionUID = 1108032535748498628L;
	private File file;
	private String name;
	
	public IngestImage(){
		
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	

}

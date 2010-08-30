package de.mpg.imeji.search.simulator;

import java.util.List;

public class CollectionSimulator {
	private String title;
	private List<MetaDataSimulator> mdList;
	
	public  CollectionSimulator(String title, List<MetaDataSimulator> mdList){
		this.title = title;
		this.mdList = mdList;
		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<MetaDataSimulator> getMdList() {
		return mdList;
	}

	public void setMdList(List<MetaDataSimulator> mdList) {
		this.mdList = mdList;
	}

}

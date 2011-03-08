package de.mpg.imeji.history;

import java.util.ArrayList;
import java.util.List;

public class HistorySession 
{
	private List<Page> pages = new ArrayList<Page>();
	
	public HistorySession() {
		// TODO Auto-generated constructor stub
	}
	
	public List<Page> getPages() {
		return pages;
	}
	
	public void setPages(List<Page> pages) {
		this.pages = pages;
	}
	
}

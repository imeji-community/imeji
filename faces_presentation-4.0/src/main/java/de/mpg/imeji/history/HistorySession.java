package de.mpg.imeji.history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.history.Page.ImejiPages;

public class HistorySession implements Serializable
{
	private List<Page> pages = new ArrayList<Page>();
	private static int HISTORY_SIZE = 10;
	
	public HistorySession() {
		// TODO Auto-generated constructor stub
	}
	
	public void add(String filename, String query, String[] id)
	{
		Page newPage = null;
		//pages.clear();
		for (ImejiPages type : ImejiPages.values())
		{
			if (type.getFileName().equals(filename))
			{
				System.out.println();
				try
				{
					if (ImejiPages.IMAGES.equals(type) && query != null && !"".equals(query))
					{
						type = ImejiPages.SEARCH_RESULTS_IMAGES;
					}
					else if (ImejiPages.SEARCH_RESULTS_IMAGES.equals(type) && ("".equals(query) || query == null))
					{
						type = ImejiPages.IMAGES;
					}
					newPage = new Page(type, PageURIHelper.getPageURI(type, query, id));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		if (newPage != null)
		{
			if (id!= null)
			{
				if (id.length == 2) newPage.setId(id[1]);// newPage.setName(newPage.getName() + " id " + id[1]);
				if (id.length == 1) newPage.setId(id[0]);// newPage.setName(newPage.getName() + " id " + id[0]);
			}
			
//			if (pages.isEmpty() || !pages.get(pages.size() - 1).getName().equals(newPage.getName()))
//			{					
//				pages.add(newPage);
//			}
			
			if (!newPage.equals(getCurrentPage()))
			{
				pages.add(newPage);
			}
		}

		while (pages.size() > HISTORY_SIZE) pages.remove(0);
	}
	
	public void remove(int pos)
	{
		for (int i = 0; i < pages.size(); i++) 
		{
			if (i > pos)
			{
				pages.remove(i);
				i--;
			}
		}
	}
	
	public Page getCurrentPage()
	{
		if (!pages.isEmpty())
		{
			return pages.get(pages.size() -1);
		}
		return null;
	}
	
	public Page getPreviousPage()
	{
		if (!pages.isEmpty())
		{
			return pages.get(pages.size() - 2);
		}
		return null;
	}
		
	public int getHistorySize()
	{
		return pages.size();
	}

	public List<Page> getPages() {
		return pages;
	}
	
	public void setPages(List<Page> pages) {
		this.pages = pages;
	}
	
}

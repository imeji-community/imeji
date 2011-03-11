package de.mpg.imeji.history;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.mpg.imeji.history.Page.ImejiPages;

public class HistorySession 
{
	private List<Page> pages = new ArrayList<Page>();
	
	public HistorySession() {
		// TODO Auto-generated constructor stub
	}
	
	public void add(String filename, String query, String id)
	{
		Page newPage = null;
		//pages.clear();
		for (ImejiPages type : ImejiPages.values())
		{
			if (type.getFileName().endsWith(filename))
			{
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
			if (pages.isEmpty() || !pages.get(pages.size() - 1).getType().equals(newPage.getType()))
			{
				String uri = newPage.getUri().toString();
				newPage.setUri(URI.create(uri + "&h=" + pages.size()));
				
				if(id != null) newPage.setName(newPage.getName() + " " + id);
				
				pages.add(newPage);
			}
		}
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
	
	public String getCurrentPage()
	{
		if (!pages.isEmpty())
		{
			return pages.get(pages.size() -1).getName();
		}
		return "";
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

package de.mpg.escidoc.faces.upload;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import de.mpg.escidoc.faces.item.ItemVO;

public class Upload 
{
	List<ItemVO> items = null;
	String contentModelId = null;
	Date startDate = null;
	Date endDate = null;
		
	public Upload(String contentModelId) 
	{
		items = new ArrayList<ItemVO>();
		this.contentModelId = contentModelId;
	}

	public List<ItemVO> getItems() 
	{
		return items;
	}

	public void setItems(List<ItemVO> items) 
	{
		this.items = items;
	}

	public Date getStartDate() 
	{
		return startDate;
	}

	public void setStartDate(Date startDate) 
	{
		this.startDate = startDate;
	}

	public Date getEndDate() 
	{
		return endDate;
	}

	public void setEndDate(Date endDate) 
	{
		this.endDate = endDate;
	}

	
}

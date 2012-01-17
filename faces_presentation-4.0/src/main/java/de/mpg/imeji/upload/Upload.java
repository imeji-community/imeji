/**
 * License: src/main/resources/license/escidoc.license
 */

package de.mpg.imeji.upload;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.escidoc.core.resources.om.item.Item;
import de.mpg.jena.vo.CollectionImeji;

/**
 * 
 * @author saquet
 *
 */
public class Upload 
{
	private List<Item> items = null;
	private String contentModelId = null;
	private CollectionImeji collection = null;
	private Date startDate = null;
	private Date endDate = null;
		
	public Upload(String contentModelId) 
	{
		items = new ArrayList<Item>();
		this.contentModelId = contentModelId;
	}

	public List<Item> getItems() 
	{
		return items;
	}

	public void setItems(List<Item> items) 
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

    public String getContentModelId()
    {
        return contentModelId;
    }

    public void setContentModelId(String contentModelId)
    {
        this.contentModelId = contentModelId;
    }

    public CollectionImeji getCollection()
    {
        return collection;
    }

    public void setCollection(CollectionImeji collection)
    {
        this.collection = collection;
    }

	
}

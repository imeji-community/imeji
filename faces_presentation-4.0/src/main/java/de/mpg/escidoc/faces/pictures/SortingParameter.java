package de.mpg.escidoc.faces.pictures;

import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.metadata.ScreenConfiguration;

/**
 * 
 * @author saquet
 *
 */
public class SortingParameter 
{	
	public enum SortParameterType
	{
		SORT1, SORT2, SORT3;
	}
	
	public enum OrderType
	{
		ASC, DSC;
	}
	
	private ScreenConfiguration screen = null;
	private String index = null;
	private String label = null;
	private OrderType order = OrderType.ASC;
	
	public SortingParameter()
	{
		screen = new ScreenConfiguration("sort");
	}
	
	public SortingParameter(String index, OrderType order) 
	{
	    this();
            if (screen.getMdMap().get(index.replace("escidoc.", "")) != null) 
            {
            	this.index = index;
            	this.label = screen.getMdMap().get(index.replace("escidoc.", "")).getLabel();
            	this.order = order;
            }
	}
	
	public SortingParameter(Metadata metadata, OrderType order)
	{
		this(metadata.getIndex(), order);
	}

	/**
	 * Return query for thie parameter
	 * @return
	 */
	public String getCqlQuery()
	{
		index = " sort." + index;
		
		if (OrderType.DSC.equals(order)) 
		{
			return  index + ",,0";
		}
		
		return index + ",,1";
	}
	
	/**
	 * @return the index
	 */
	public String getIndex() 
	{
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(String index) 
	{
		this.index = index;
	}

	/**
	 * @return the label
	 */
	public String getLabel() 
	{
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	
}

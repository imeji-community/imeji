package de.mpg.escidoc.faces.pictures;

public class BrowseParameter 
{
	public enum BrowseParemeterType
	{
		SHOW, PAGE, COLLECTION, ALBUM, SELECTION, QUERY, PERSON;
	}
	
	private BrowseParemeterType type = null;
	private String value = null;
	
	public BrowseParameter(BrowseParemeterType type, String value) 
	{
		this.type = type;
		this.value = value;
	}

	/**
	 * @return the type
	 */
	public BrowseParemeterType getType() 
	{
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(BrowseParemeterType type) 
	{
		this.type = type;
	}

	/**
	 * @return the value
	 */
	public String getValue() 
	{
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) 
	{
		this.value = value;
	}
	
	
}

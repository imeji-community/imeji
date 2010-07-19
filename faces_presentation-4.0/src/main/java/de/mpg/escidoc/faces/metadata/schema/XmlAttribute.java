package de.mpg.escidoc.faces.metadata.schema;

public class XmlAttribute
{
    private String name = null;
    private String value = null;
    private String prefix = null;
	
    public XmlAttribute(String name, String value, String prefix)
    {
	this.name = name;
	this.value = value;
	this.prefix = prefix;
    }

    public String asString()
    {
	String s = "";
	if (prefix != null)
	{
	   s = prefix  + ":";
	}
	return s + name + "=\"" + value + "\"";
    }
    
}

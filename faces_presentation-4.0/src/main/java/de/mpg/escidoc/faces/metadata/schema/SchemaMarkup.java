package de.mpg.escidoc.faces.metadata.schema;

import java.util.ArrayList;
import java.util.List;


public class SchemaMarkup
{
    public enum SchemaMarkupType
    {
	SCHEMA("schema"), ELEMENT("element"), ATTRIBUTE("attribute"), COMPLEXTYPE("complexType"), 
	SIMPLETYPE("simpleType"), SEQUENCE("sequence"), IMPORT("import"), RESTRICTION("restriction"), 
	ENUMERATION("enumeration");
	
	private SchemaMarkupType(String name)
	{
	    this.name = name;
	}
	
	private final String name;
	
	public String toString() 
	{
	    return name;
	}
    }
    
    private SchemaMarkupType type = null;
    private List<XmlAttribute> attributes= new ArrayList<XmlAttribute>();
    
    public static String NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    public static String PREFIX = "xs";
    
    public SchemaMarkup(SchemaMarkupType type, List<XmlAttribute> attributes)
    {
	this.type = type;
	
	if (attributes != null)
	{
	    this.attributes = attributes;
	}
    }
    
    public String asStartTag()
    {
	String s = "<xs:" + type.toString();
	for (XmlAttribute a : attributes)
	{
	    s += " " + a.asString();
	}
	
	return s + ">";
    }
    
    public String asEndTag()
    {
	return "</xs:" + type.toString() + ">";
    }
    
    public String asEmptyTag()
    {
	String s = "<xs:" + type.toString();
	for (XmlAttribute a : attributes)
	{
	    s += " " + a.asString();
	}
	return s + "/>";
    }

}


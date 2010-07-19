package de.mpg.escidoc.faces.metadata.schema;

import java.util.ArrayList;
import java.util.List;


public class SchemaMarkup
{
    public enum SchemaMarkupType
    {
	ELEMENT, ATTRIBUTE, COMPLEXTYPE, SIMPLETYPE, SEQUENCE, IMPORT, RESTRICTION, ENUMERATION;
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
	String s = "<xs:" + type.toString().toLowerCase();
	for (XmlAttribute a : attributes)
	{
	    s += " " + a.asString();
	}
	
	return s + ">";
    }
    
    public String asEndTag()
    {
	return "</xs:" + type.toString().toLowerCase() + ">";
    }
    
    public String asEmptyTag()
    {
	String s = "<xs:" + type.toString().toLowerCase();
	for (XmlAttribute a : attributes)
	{
	    s += " " + a.asString();
	}
	return s + "/>";
    }

}


package de.mpg.escidoc.faces.metadata.schema;

import java.util.ArrayList;
import java.util.List;

import javax.naming.RefAddr;

import de.mpg.escidoc.faces.metadata.Metadata;
import de.mpg.escidoc.faces.metadata.schema.SchemaMarkup.SchemaMarkupType;

public class SchemaElement
{
    public enum SchemaElementType{
	SIMPLE, COMPLEX;
    }
    
    private String name = null;
    private String namespace = null;
    private String prefix = null;
    private SchemaElementType type = SchemaElementType.COMPLEX;
    private List<SchemaElement> childs = null;
    
    private SchemaMarkup element = null;
    private SchemaMarkup reference = null;
    private SchemaMarkup complexType = null;
    private SchemaMarkup simpleType =  new SchemaMarkup(SchemaMarkupType.SIMPLETYPE, null);
    private List<SchemaMarkup> enumerations =  null;
    private SchemaMarkup restriction =  new SchemaMarkup(SchemaMarkupType.RESTRICTION, null);;
    private SchemaMarkup sequence = new SchemaMarkup(SchemaMarkupType.SEQUENCE, null);
    private SchemaMarkup importElement = null;
    
    public SchemaElement(Metadata metadata, String prefix)
    {
	this.name = metadata.getName();
	this.namespace = metadata.getNamespace();
	this.type =  SchemaElementType.COMPLEX;
	this.prefix = prefix;
	this.enumerations =  new ArrayList<SchemaMarkup>();
	
	if (metadata.getConstraint().size() > 0)
	{
	    this.type = SchemaElementType.SIMPLE;
	}
	
	if (prefix != null)
	{
	    prefix = prefix + ":";
	}
	else
	{
	    prefix = "";
	}
	
	childs = new ArrayList<SchemaElement>();
	
	XmlAttribute nameAttr = new XmlAttribute("name", name, null);
	XmlAttribute typeAttr = new XmlAttribute("type", name + "Type", null);
	XmlAttribute refAttr = new XmlAttribute("ref", prefix + name, null);
	XmlAttribute maxOccurrsAttr = new XmlAttribute("maxoccurs", Integer.toString(metadata.getMaxOccurs()) , null);
	XmlAttribute minOccursAttr = new XmlAttribute("minoccurs", Integer.toString(metadata.getMinOccurs()) , null);
	XmlAttribute namespaceAttr = new XmlAttribute("namespace", namespace, null);
	XmlAttribute schemalocAttr = new XmlAttribute("schemaLocation", metadata.getSchemaLocation(), null);
	
	List<XmlAttribute> attributes = new ArrayList<XmlAttribute>();
	attributes.add(nameAttr);
	if (!isSimpleType())
	{
	    attributes.add(typeAttr);
	}
	attributes.add(maxOccurrsAttr);
	attributes.add(minOccursAttr);
	element = new SchemaMarkup(SchemaMarkupType.ELEMENT, attributes);
	
	attributes =  new ArrayList<XmlAttribute>();
	attributes.add(typeAttr);
	attributes.add(maxOccurrsAttr);
	attributes.add(minOccursAttr);
	complexType = new SchemaMarkup(SchemaMarkupType.COMPLEXTYPE, attributes);
	
	attributes = new ArrayList<XmlAttribute>();
	attributes.add(nameAttr);
	attributes.add(refAttr);
	attributes.add(maxOccurrsAttr);
	attributes.add(minOccursAttr);
	reference =  new SchemaMarkup(SchemaMarkupType.COMPLEXTYPE, attributes);
	
	attributes = new ArrayList<XmlAttribute>();
	attributes.add(namespaceAttr);
	attributes.add(schemalocAttr);
	importElement = new SchemaMarkup(SchemaMarkupType.IMPORT, attributes);
	
	attributes = new ArrayList<XmlAttribute>();
	for (String str : metadata.getConstraint())
	{
	    attributes = new ArrayList<XmlAttribute>();
	    attributes.add(new XmlAttribute("value", str, null));
	    enumerations.add(new SchemaMarkup(SchemaMarkupType.ENUMERATION, attributes));
	}
    }
    
    public void setChilds(List<SchemaElement> childs)
    {
	this.childs = childs;
    }
    
    public void addChild(SchemaElement child)
    {
	childs.add(child);
    }
    
    public String getImportElement()
    {
	return importElement.asEmptyTag();
    }
        
    public String getComplexTypeElement()
    {
	String xml = complexType.asStartTag();
	xml += sequence.asStartTag();
	
	for (SchemaElement se : childs)
	{
	    if (se.isSimpleType())
	    {
		xml += se.getElementWithSimpleType();
	    }
	    else
	    {
		 xml += se.getElementWithReference();
	    }
	}
	
	xml += sequence.asEndTag();
	return xml + complexType.asEndTag();
    }
    
    public boolean isSimpleType()
    {
	if (type.equals(SchemaElementType.SIMPLE))
	{
	    return true;
	}
	return false;
    }
    
    public String getElementWithSimpleType()
    {
	String xml = element.asStartTag();
	xml += simpleType.asStartTag();
	xml += restriction.asStartTag();
	for (SchemaMarkup s : enumerations)
	{
	    xml+= s.asEmptyTag();
	}
	xml += restriction.asEndTag();
	xml += simpleType.asEndTag();
	return xml + element.asEndTag();
    }
    
    public String getElementWithComplexType()
    {
	return element.asEmptyTag();
    }
    
    public String getElementWithReference()
    {	
	return reference.asEmptyTag();
    }

    /**
     * @return the namespace
     */
    public String getNamespace()
    {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(String namespace)
    {
        this.namespace = namespace;
    }

    /**
     * @return the prefix
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }
}
